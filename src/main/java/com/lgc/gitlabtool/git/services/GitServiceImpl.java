package com.lgc.gitlabtool.git.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Status;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;

public class GitServiceImpl implements GitService {

    private static final Logger _logger = LogManager.getLogger(GitServiceImpl.class);

    private static final JGit _git = JGit.getInstance();
    private static StateService _stateService;

    public GitServiceImpl(StateService stateService) {
        _stateService = stateService;
    }

    @Override
    public boolean containsBranches(Project project, List<Branch> branches, boolean isCommon) {
        if (project == null || branches == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branches.");
        }
        List<Branch> projectBranches = _git.getBranches(project, BranchType.ALL);

        return isCommon ? projectBranches.containsAll(branches) : !Collections.disjoint(projectBranches, branches);
    }

    @Override
    public Map<Project, JGitStatus> switchTo(List<Project> projects, Branch branch) {
        String selectedBranchName = branch.getBranchName();
        boolean isRemote = branch.getBranchType().equals(BranchType.REMOTE);

        return switchTo(projects, selectedBranchName, isRemote);
    }

    @Override
    public Map<Project, JGitStatus> switchTo(List<Project> projects, String branchName, boolean isRemote) {
        _stateService.stateON(ApplicationState.SWITCH_BRANCH);
        final Map<Project, JGitStatus> switchStatuses = new ConcurrentHashMap<>();
        projects.parallelStream()
                .forEach((project) -> switchStatuses.put(project, _git.switchTo(project, branchName, isRemote)));
        _stateService.stateOFF(ApplicationState.SWITCH_BRANCH);
        return switchStatuses;
    }

    @Override
    public List<Project> getProjectsWithChanges(List<Project> projects) {
        if (projects == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branches.");
        }

        List<Project> changedProjects = projects.parallelStream()
                .filter(this::projectHasChanges)
                .collect(Collectors.toList());
        return changedProjects;
    }

    private boolean projectHasChanges(Project project) {
        return project.getProjectStatus().hasChanges();
    }

    @Override
    public Map<Project, JGitStatus> discardChanges(List<Project> projects) {
        if (projects == null) {
            throw new IllegalArgumentException("Wrong parameters for discarding projects.");
        }
        final Map<Project, JGitStatus> results = new ConcurrentHashMap<>();
        projects.parallelStream()
                .forEach(project -> discardChanges(project, results));
        return results;
    }

    @Override
    public Map<Project, JGitStatus> commitChanges(List<Project> projects, String commitMessage,
            boolean isPushImmediately, ProgressListener progressListener) {

        return isPushImmediately ? commitAndPush(projects, commitMessage, progressListener)
                                 : commit(projects, commitMessage, progressListener);
    }

    @Override
    public Map<Project, JGitStatus> createBranch(List<Project> projects, String branchName, String startPoint, boolean force) {
        Map<Project, JGitStatus> statuses = new ConcurrentHashMap<>();
        projects.parallelStream()
                .filter(prj -> prj.isCloned())
                .forEach((project) -> statuses.put(project, _git.createBranch(project, branchName, startPoint, force)));
        return statuses;
    }

    private void discardChanges(Project project, Map<Project, JGitStatus> results) {
        if (project == null) {
            results.put(project, JGitStatus.FAILED);
            return;
        }
        results.put(project, _git.discardChanges(project));
    }

    private Map<Project, JGitStatus> commit(List<Project> projects,
                                            String commitMessage,
                                            ProgressListener progressListener) {
        // use null for getting default user-info
        return _git.commit(projects, commitMessage, true, null, null, null, null, progressListener);
    }

    private Map<Project, JGitStatus> commitAndPush(List<Project> projects,
                                                   String commitMessage,
                                                   ProgressListener progressListener) {
        // use null for getting default user-info
        return _git.commitAndPush(projects, commitMessage, true, null, null, null, null, progressListener);
    }

    @Override
    public Set<Branch> getBranches(List<Project> projects, BranchType branchType, boolean isOnlyCommon) {
        if (projects == null || branchType == null) {
            return Collections.emptySet();
        }
        Set<Branch> branches = _git.getBranches(projects, branchType, isOnlyCommon);
        return branches != null ? branches : Collections.emptySet();
    }

    @Override
    public String getCurrentBranchName(Project project) {
        if (project == null) {
            return null;
        }
        Optional<String> currentBranch = _git.getCurrentBranch(project);
        return currentBranch.isPresent() ? currentBranch.get() : null;
    }

    @Override
    public Map<Project, JGitStatus> push(List<Project> projects, ProgressListener progressListener) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyMap();
        }
        if(progressListener == null){
            progressListener = EmptyProgressListener.get();
        }
        return _git.push(projects, progressListener);
    }

    @Override
    public boolean pull(List<Project> projects, OperationProgressListener progressListener) {
        // Switched on PULL application state. Should be switched of in onFinish() method of progressListener
        _stateService.stateON(ApplicationState.PULL);
        if (projects == null || projects.isEmpty()) {
            if (progressListener == null) {
                _stateService.stateOFF(ApplicationState.PULL);
            } else {
                String errorMessage = "Error during pull! Have no selected projects to pull";
                progressListener.onFinish(errorMessage);
            }
            _logger.error("Error during pull! Projects: " + projects + "; progressListener: " + progressListener);
            return false;
        }
        return _git.pull(projects, progressListener);
    }

    @Override
    public boolean hasAtLeastOneReference(Project project) {
        if (project == null || !project.isCloned()) {
            return false;
        }
        return _git.hasAtLeastOneReference(project);
    }

    @Override
    public int[] getAheadBehindIndexCounts(Project project, String branchName) {
        if (project == null || branchName == null || branchName.isEmpty() || !project.isCloned()) {
            return new int[] {0, 0};
        }
        return _git.getAheadBehindIndexCounts(project, branchName);
    }

    @Override
    public void modifyProjectStatusByGit(Project project) {
        Optional<String> optBranch = _git.getCurrentBranch(project);
        String branchName = optBranch.orElse(StringUtils.EMPTY);

        Optional<Status> status = _git.getStatusProject(project);
        boolean hasConflicts = false;
        boolean hasChanges = false;
        if (status.isPresent()) {
            hasConflicts = status.get().getConflicting().size() > 0;
            hasChanges = status.get().hasUncommittedChanges();
        }

        int aheadIndex = 0;
        int behindIndex = 0;
        if (optBranch.isPresent()) {
            int[] indexCount = getAheadBehindIndexCounts(project, branchName);
            aheadIndex = indexCount[0];
            behindIndex = indexCount[1];
        }

        ProjectStatus projectStatus = new ProjectStatus(hasConflicts, hasChanges, aheadIndex, behindIndex, branchName);
        project.setProjectStatus(projectStatus);
    }
}
