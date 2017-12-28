package com.lgc.gitlabtool.git.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import com.lgc.gitlabtool.git.jgit.ChangedFileStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFileType;
import com.lgc.gitlabtool.git.jgit.ChangedFilesUtils;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.jgit.stash.GroupStash;
import com.lgc.gitlabtool.git.jgit.stash.Stash;
import com.lgc.gitlabtool.git.jgit.stash.StashItem;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;

public class GitServiceImpl implements GitService {

    private static final Logger _logger = LogManager.getLogger(GitServiceImpl.class);
    private static final String CHECKOUT_BRANCH_FINISHED_MESSAGE = "Checkout branch operation is finished.";
    private static final String GROUP_STASH_ID = "[GS%s] ";

    private static JGit _git;
    private static StateService _stateService;
    private static ChangedFilesUtils _changedFilesUtils;

    public GitServiceImpl(StateService stateService, JGit jGit, ChangedFilesUtils changedFilesUtils) {
        _git = jGit;
        _stateService = stateService;
        _changedFilesUtils = changedFilesUtils;
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
    public Map<Project, JGitStatus> checkoutBranch(List<Project> projects, Branch branch, ProgressListener progress) {
        boolean isRemote = branch.getBranchType().equals(BranchType.REMOTE);
        return checkoutBranch(projects, branch.getBranchName(), isRemote, progress);
    }

    @Override
    public Map<Project, JGitStatus> checkoutBranch(List<Project> projects, String branchName, boolean isRemote,
                                             ProgressListener progress) {
        if (progress == null) {
            progress = EmptyProgressListener.get();
        }
        return runCheckoutBranchAction(projects, branchName, isRemote, progress);
    }

    private Map<Project, JGitStatus> runCheckoutBranchAction(List<Project> projects,
                                                     String branchName,
                                                     boolean isRemote,
                                                     ProgressListener progress) {
        final Map<Project, JGitStatus> checkoutStatuses = new ConcurrentHashMap<>();
        try {
            _stateService.stateON(ApplicationState.CHECKOUT_BRANCH);
            final long step = 100 / projects.size();
            final AtomicLong percentages = new AtomicLong(0);
            projects.parallelStream()
                    .forEach(project -> checkoutBranch(checkoutStatuses, project, branchName, isRemote, progress, percentages, step));
        } finally {
            progress.onFinish(CHECKOUT_BRANCH_FINISHED_MESSAGE);
            if (_stateService.isActiveState(ApplicationState.CHECKOUT_BRANCH)) {
                _stateService.stateOFF(ApplicationState.CHECKOUT_BRANCH);
            }
        }
        return checkoutStatuses;
    }

    private void checkoutBranch(Map<Project, JGitStatus> checkoutStatuses, Project project,
                                String branchName, boolean isRemote,
                                ProgressListener progress,
                                AtomicLong percentages, long step) {
        try {
            progress.onStart(project);
            percentages.addAndGet(step);
            JGitStatus status = _git.checkoutBranch(project, branchName, isRemote);
            if (status == JGitStatus.SUCCESSFUL) {
                progress.onSuccess(percentages.get(), project, status);
            } else {
                progress.onError(percentages.get(), project, status);
            }
            checkoutStatuses.put(project, status);
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
        Set<String> changedFiles = new HashSet<>();
        Set<String> removedFiles = new HashSet<>();
        Set<String> missingFiles = new HashSet<>();
        Set<String> modifiedFiles = new HashSet<>();
        Set<String> addedFiles = new HashSet<>();
        boolean hasChanges = false;

        Optional<Status> optStatus = _git.getStatusProject(project);
        if (optStatus.isPresent()) {
            Status status = optStatus.get();
            conflictedFiles.addAll(status.getConflicting());
            changedFiles.addAll(status.getChanged());
            addedFiles.addAll(status.getAdded());
            untrackedFiles.addAll(status.getUntracked());
            modifiedFiles.addAll(status.getModified());
            removedFiles.addAll(status.getRemoved());
            missingFiles.addAll(status.getMissing());

            hasChanges = status.hasUncommittedChanges();
        }

        int aheadIndex = 0;
        int behindIndex = 0;
        String trackingBranch = getTrackingBranch(project);
        String nameBranch = getCurrentBranchName(project);
        if (nameBranch != null) {
            int[] indexCount = getAheadBehindIndexCounts(project, nameBranch);
            aheadIndex = indexCount[0];
            behindIndex = indexCount[1];
        }
        return new ProjectStatus(hasChanges, aheadIndex, behindIndex, nameBranch, trackingBranch, conflictedFiles,
                untrackedFiles, changedFiles, addedFiles, removedFiles, missingFiles, modifiedFiles);
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
        _git.cancelClone();
    }

    @Override
    public String getTrackingBranch(Project project) {
        return _git.getTrackingBranch(project);
    }

    @Override
    public List<ChangedFile> getChangedFiles(Project project) {
        List<ChangedFile> files = new ArrayList<>();
        if (project == null || !project.isCloned()) {
            return files;
        }
        ProjectStatus status = project.getProjectStatus();
        files.addAll(getChangedFiles(status.getChangedFiles(), project, ChangedFileType.STAGED, ChangedFileStatus.CHANGED));
        files.addAll(getChangedFiles(status.getRemovedFiles(), project, ChangedFileType.STAGED, ChangedFileStatus.REMOVED));
        files.addAll(getChangedFiles(status.getAddedFiles(), project, ChangedFileType.STAGED, ChangedFileStatus.ADDED));
        files.addAll(getChangedFiles(status.getUntrackedFiles(), project, ChangedFileType.UNSTAGED, ChangedFileStatus.UNTRACKED));
        files.addAll(getChangedFiles(status.getConflictedFiles(), project,  ChangedFileType.UNSTAGED, ChangedFileStatus.CONFLICTING));
        files.addAll(getChangedFiles(status.getMissingFiles(), project, ChangedFileType.UNSTAGED, ChangedFileStatus.MISSING));
        files.addAll(getChangedFiles(status.getModifiedFiles(), project,ChangedFileType.UNSTAGED, ChangedFileStatus.MODIFIED));
        return files;
    }

    private List<ChangedFile> getChangedFiles(Collection<String> fileNames, Project project,
            ChangedFileType typeFile, ChangedFileStatus statusFile) {
        return fileNames.stream()
                        .map(fileName -> new ChangedFile(project, fileName, typeFile, statusFile))
                        .collect(Collectors.toList());
    }

    @Override
    public Set<Branch> getBranches(Collection<Project> projects, BranchType brType, boolean onlyCommon) {
        return _git.getBranches(projects, brType, onlyCommon);
    }

    @Override
    public List<ChangedFile> addUntrackedFilesToIndex(Map<Project, List<ChangedFile>> files) {
        List<ChangedFile> addedFiles = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return addedFiles;
        }
        _stateService.stateON(ApplicationState.ADD_FILES_TO_INDEX);
        try {
            for (Entry<Project, List<ChangedFile>> entry : files.entrySet()) {
                Project project = entry.getKey();
                List<ChangedFile> changedFiles = entry.getValue();
                if (project != null && project.isCloned() && changedFiles != null && !changedFiles.isEmpty()) {
                    addedFiles.addAll(addFilesToIndex(changedFiles, project));
                }
            }
        } finally {
            _stateService.stateOFF(ApplicationState.ADD_FILES_TO_INDEX);
        }
        return addedFiles;
    }

    @Override
    public List<ChangedFile> resetChangedFiles(Map<Project, List<ChangedFile>> files) {
        List<ChangedFile> resetedFiles = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return resetedFiles;
        }
        _stateService.stateON(ApplicationState.RESET);
        try {
            for (Entry<Project, List<ChangedFile>> entry : files.entrySet()) {
                Project project = entry.getKey();
                List<ChangedFile> changedFiles = entry.getValue();
                if (project != null && project.isCloned() && !changedFiles.isEmpty()) {
                    List<String> fileNames = _changedFilesUtils.getFileNames(changedFiles);
                    List<String> result = _git.resetChangedFiles(fileNames, project);
                    resetedFiles.addAll(getNewChangedFiles(result, project, changedFiles));
                }
            }
        } finally {
            _stateService.stateOFF(ApplicationState.RESET);
        }
        return resetedFiles;
    }

    @Override
    public void createStash(List<Project> projects, String stashMessage, boolean includeUntracked) {
        if (projects == null || projects.isEmpty()) {
            return;
        } else if (stashMessage == null) {
            return;
        }
        _stateService.stateON(ApplicationState.STASH);
        try {
            if (projects.size() == 1) {
                _git.stashCreate(projects.get(0), stashMessage, includeUntracked);
            } else {
                String indexOperation = String.format(GROUP_STASH_ID, currentDateToString());
                String stashGroupMessage = indexOperation + stashMessage;
                projects.parallelStream()
                        .filter(project -> project != null && project.isCloned())
                        .forEach(project -> _git.stashCreate(project, stashGroupMessage, includeUntracked));
            }
        } finally {
            _stateService.stateOFF(ApplicationState.STASH);
        }
    }

    @Override
    public List<StashItem> getStashList(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        List<StashItem> allStashes = new ArrayList<>();
        projects.forEach(project -> addStashToList(allStashes, _git.getStashes(project)));
        return allStashes;
    }

    private void addStashToList(List<StashItem> allStashes, List<StashItem> currentStashList) {
        if (allStashes.isEmpty()) {
            allStashes.addAll(currentStashList);
            return;
        }
        for (StashItem stash : currentStashList) {
            Optional<StashItem> foundStash = allStashes.stream()
                                                       .filter(item -> hasGroupIdentificator(item.getMessage(), stash.getMessage()))
                                                       .findFirst();
            if (foundStash.isPresent()) {
              addGroupStash(allStashes, stash, foundStash.get());
          } else {
              allStashes.add(stash);
          }
        }
    }

    private boolean hasGroupIdentificator(String currentMessage, String newStashMessage) {
        return currentMessage.equals(newStashMessage) && currentMessage.matches("\\[GS\\d+\\](.+)?");
    }

    private void addGroupStash(List<StashItem> stashList, StashItem addStash,  StashItem foundStash) {
        Stash newStash = (Stash)addStash;
        if (foundStash instanceof GroupStash) {
            ((GroupStash) foundStash).addStash(newStash);
        } else {
            stashList.remove(foundStash);
            GroupStash newGroup = new GroupStash(foundStash.getMessage());
            newGroup.addStash((Stash)foundStash);
            newGroup.addStash(newStash);
            stashList.add(newGroup);
        }
    }

    private String currentDateToString() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private List<ChangedFile> addFilesToIndex(List<ChangedFile> changedFiles,  Project project) {
        List<String> addedFiles = new ArrayList<>();
        for (ChangedFile changedFile : changedFiles) {
            if (changedFile.wasRemoved() && ChangedFileType.STAGED != changedFile.getTypeFile()) {
                String fileName = changedFile.getFileName();
                if (_git.addDeletedFile(fileName, project, true)) {
                    addedFiles.add(fileName);
                }
            } else {
                String fileName = changedFile.getFileName();
                if (_git.addUntrackedFileToIndex(fileName, project)) {
                    addedFiles.add(fileName);
                }
            }
        }
        return getNewChangedFiles(addedFiles, project, changedFiles);
    }

    private List<ChangedFile> getNewChangedFiles(List<String> fileNames, Project project, List<ChangedFile> sourceList) {
        return resetConflicts(_changedFilesUtils.getChangedFiles(fileNames, project, sourceList));
    }

    private List<ChangedFile> resetConflicts(List<ChangedFile> sourceList) {
        // If the file was added to the index, it can no longer have conflicts even if we do a reset.
        sourceList.stream()
                  .filter(changedFile -> changedFile.hasConflicting())
                  .forEach(changedFile -> changedFile.setStatusFile(ChangedFileStatus.MODIFIED));
        return sourceList;
    }
}
