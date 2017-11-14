package com.lgc.gitlabtool.git.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Status;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;

public class GitServiceImpl implements GitService {

    private static final Logger _logger = LogManager.getLogger(GitServiceImpl.class);
    private static final String SWITCH_TO_FINISHED_MESSAGE = "Switch branch operation is finished.";

    private static JGit _git;
    private static StateService _stateService;

    public GitServiceImpl(StateService stateService, JGit jGit) {
        _stateService = stateService;
        _git = jGit;
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
    public Map<Project, JGitStatus> switchTo(List<Project> projects, Branch branch, ProgressListener progress) {
        boolean isRemote = branch.getBranchType().equals(BranchType.REMOTE);
        return switchTo(projects, branch.getBranchName(), isRemote, progress);
    }

    @Override
    public Map<Project, JGitStatus> switchTo(List<Project> projects,
                                             String branchName,
                                             boolean isRemote,
                                             ProgressListener progress) {
        if (progress == null) {
            progress = EmptyProgressListener.get();
        }
        return runSwitchAction(projects, branchName, isRemote, progress);
    }

    private Map<Project, JGitStatus> runSwitchAction(List<Project> projects,
                                                     String branchName,
                                                     boolean isRemote,
                                                     ProgressListener progress) {
        final Map<Project, JGitStatus> switchStatuses = new ConcurrentHashMap<>();
        try {
            _stateService.stateON(ApplicationState.SWITCH_BRANCH);
            final long step = 100 / projects.size();
            final AtomicLong percentages = new AtomicLong(0);
            projects.parallelStream()
                    .forEach(project -> switchTo(switchStatuses, project, branchName, isRemote, progress, percentages, step));
        } finally {
            progress.onFinish(SWITCH_TO_FINISHED_MESSAGE);
            if (_stateService.isActiveState(ApplicationState.SWITCH_BRANCH)) {
                _stateService.stateOFF(ApplicationState.SWITCH_BRANCH);
            }
        }
        return switchStatuses;
    }

    private void switchTo(Map<Project, JGitStatus> switchStatuses,
                          Project project,
                          String branchName,
                          boolean isRemote,
                          ProgressListener progress,
                          AtomicLong percentages,
                          long step) {
        try {
            progress.onStart(project);
            percentages.addAndGet(step);
            JGitStatus status = _git.switchTo(project, branchName, isRemote);
            if (status == JGitStatus.SUCCESSFUL) {
                progress.onSuccess(percentages.get(), project, status);
            } else {
                progress.onError(percentages.get(), project, status);
            }
            switchStatuses.put(project, status);
        } catch (IllegalArgumentException e) {
            progress.onError(percentages.get(), e.getMessage());
        }
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
    public Map<Project, JGitStatus> revertChanges(List<Project> projects) {
        if (projects == null) {
            throw new IllegalArgumentException("Wrong parameters for discarding projects.");
        }
        final Map<Project, JGitStatus> results = new ConcurrentHashMap<>();
        projects.parallelStream()
                .forEach(project -> revertChanges(project, results));
        return results;
    }

    @Override
    public Map<Project, JGitStatus> commitChanges(List<Project> projects, String commitMessage,
            boolean isPushImmediately, ProgressListener progressListener) {
        if (projects == null || projects.isEmpty()) {
            _logger.error("Wrong parameters for committing changes. Projects list is null or empty.");
            return Collections.emptyMap();
        }
        return isPushImmediately ? commitAndPush(projects, commitMessage, progressListener)
                                 : commit(projects, commitMessage, progressListener);
    }

    @Override
    public Map<Project, JGitStatus> createBranch(List<Project> projects, String branchName, String startPoint, boolean force) {
        try {
            _stateService.stateON(ApplicationState.CREATE_BRANCH);
            Map<Project, JGitStatus> statuses = new ConcurrentHashMap<>();
            projects.parallelStream()
                    .filter(prj -> prj.isCloned())
                    .forEach((project) -> statuses.put(project, _git.createBranch(project, branchName, startPoint, force)));
            return statuses;
        } finally {
            if (_stateService.isActiveState(ApplicationState.CREATE_BRANCH)) {
                _stateService.stateOFF(ApplicationState.CREATE_BRANCH);
            }
        }
    }

    private void revertChanges(Project project, Map<Project, JGitStatus> results) {
        if (project == null) {
            results.put(project, JGitStatus.FAILED);
            return;
        }
        results.put(project, _git.revertChanges(project));
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
        if (project == null || branchName == null || branchName.isEmpty() || project.getPath() == null) {
            return new int[] {0, 0};
        }
        return _git.getAheadBehindIndexCounts(project, branchName);
    }

    @Override
    public ProjectStatus getProjectStatus(Project project) {
        if (project == null) {
            return new ProjectStatus();
        }
        Set<String> conflictedFiles = new HashSet<>();
        Set<String> untrackedFiles = new HashSet<>();
        boolean hasChanges = false;

        Optional<Status> optStatus = _git.getStatusProject(project);
        if (optStatus.isPresent()) {
            Status status = optStatus.get();
            conflictedFiles =  status.getConflicting();
            untrackedFiles = status.getUntracked();
            hasChanges = status.hasUncommittedChanges();
        }

        int aheadIndex = 0;
        int behindIndex = 0;
        String nameBranch = getCurrentBranchName(project);
        if (nameBranch != null) {
            int[] indexCount = getAheadBehindIndexCounts(project, nameBranch);
            aheadIndex = indexCount[0];
            behindIndex = indexCount[1];
        }

        return new ProjectStatus(hasChanges, aheadIndex, behindIndex, nameBranch, conflictedFiles, untrackedFiles);
    }

    @Override
    public boolean[] hasConflictsAndChanges(Project project) {
        Optional<Status> status = _git.getStatusProject(project);

        boolean hasConflicts = false;
        boolean hasChanges = false;
        if (status.isPresent()) {
            hasConflicts = status.get().getConflicting().size() > 0;
            hasChanges = status.get().hasUncommittedChanges();
        }
        return new boolean[] {hasConflicts, hasChanges};
    }

    @Override
    public void cancelClone() {
        JGit.getInstance().cancelClone();
    }

    @Override
    public List<ChangedFile> getChangedFiles(Project project) {
        List<ChangedFile> changedFiles = new ArrayList<>();
        if (project == null || !project.isCloned()) {
            return changedFiles;
        }
        ProjectStatus status = project.getProjectStatus();
        status.getUntrackedFiles().forEach(file -> changedFiles.add(new ChangedFile(project, file, false)));
        status.getConflictedFiles().forEach(file -> changedFiles.add(new ChangedFile(project, file, true)));
        return changedFiles;
    }

    @Override
    public List<ChangedFile> addUntrackedFileForCommit(Map<Project, List<ChangedFile>> files) {
        List<ChangedFile> addedFiles = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return addedFiles;
        }
        _stateService.stateON(ApplicationState.ADD_FILES_TO_INDEX);
        try {
            for (Entry<Project, List<ChangedFile>> entry : files.entrySet()) {
                Project project = entry.getKey();
                if (project != null && project.isCloned()) {
                    List<String> fileNames = convertToFileNames(entry.getValue());
                    List<String> result = _git.addUntrackedFileForCommit(fileNames, project);
                    addedFiles.addAll(convertToChangedFile(result, project));
                }
            }
        } finally {
            _stateService.stateOFF(ApplicationState.ADD_FILES_TO_INDEX);
        }
        return addedFiles;
    }

    private List<String> convertToFileNames(List<ChangedFile> changedFiles) {
        return changedFiles.stream()
                           .map(ChangedFile::getFileName)
                           .collect(Collectors.toList());
    }

    private List<ChangedFile> convertToChangedFile(List<String> fileNames, Project project) {
        return fileNames.stream()
                        .map(file -> new ChangedFile(project, file, false))
                        .collect(Collectors.toList());
    }

}
