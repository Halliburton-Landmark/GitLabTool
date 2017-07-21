package com.lgc.gitlabtool.git.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Status;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;

public class GitServiceImpl implements GitService {

    private static final JGit _git = JGit.getInstance();

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
        final Map<Project, JGitStatus> switchStatuses = new ConcurrentHashMap<>();
        projects.parallelStream()
                .forEach((project) -> switchStatuses.put(project, _git.switchTo(project, branchName, isRemote)));
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
        Optional<Status> status = _git.getStatusProject(project);
        return status.isPresent() && status.get().hasUncommittedChanges();
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
        if (projects == null) {
            return Collections.emptyMap();
        }
        if(progressListener == null){
            progressListener = EmptyProgressListener.get();
        }
        return _git.push(projects, progressListener);
    }

    @Override
    public Map<Project, JGitStatus> pull(List<Project> projects) {
        if (projects == null) {
            return Collections.emptyMap();
        }
        Map<Project, JGitStatus> pullStatuses = new ConcurrentHashMap<>();
        projects.parallelStream()
                .filter(project -> project.isCloned())
                .forEach(project -> pullStatuses.put(project, _git.pull(project)));

        return pullStatuses;
    }
}
