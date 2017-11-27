package com.lgc.gitlabtool.git.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Project status keeps all need info about Git status.
 * For example, we can get:
 *      - a current branch name;
 *      - number ahead and behind commits;
 *      - check if projects has changes or conflicts
 *      - etc
 *
 * @author Lyudmila Lyska
 */
public class ProjectStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean _hasChanges;
    private int _aheadIndex;
    private int _behindIndex;
    private String _currentBranch;
    private Set<String> _conflictedFiles;
    private Set<String> _untrackedFiles;
    private Set<String> _modifiedFiles;
    private Set<String> _changedFiles;
    private Set<String> _removedFiles;
    private Set<String> _missingFiles;

    /**
     * Constructs a ProjectStatus with default parameters.
     */
    public ProjectStatus() {
        this(null);
    }

    /**
     * Constructs a ProjectStatus with a branch name parameter.
     *
     * @param currentBranch the branch name
     */
    public ProjectStatus(String currentBranch) {
        this(false, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with a hasConflicts and a hasChanges parameters.
     *
     * @param hasChanges   <code>true</code> if the project has changes <code>false</code> otherwise.
     */
    public ProjectStatus(boolean hasChanges) {
        this(hasChanges, null);
    }

    /**
     * Constructs a ProjectStatus with a hasConflicts, a hasChanges and a branch name parameters.
     *
     * @param hasChanges    <code>true</code> if the project has changes <code>false</code> otherwise.
     * @param currentBranch the branch name
     */
    public ProjectStatus(boolean hasChanges, String currentBranch) {
        this(hasChanges, 0, 0, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with a behindIndex, a aheadIndex and a branch name parameters.
     *
     * @param aheadIndex    the number of commits ahead index
     * @param behindIndex   the number of commits behind index
     * @param currentBranch the branch name
     */
    public ProjectStatus(int aheadIndex, int behindIndex, String currentBranch) {
        this(false, aheadIndex, behindIndex, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with all parameters.
     *
     * @param hasChanges    <code>true</code> if the project has changes <code>false</code> otherwise.
     * @param aheadIndex    the number of commits ahead index
     * @param behindIndex   the number of commits behind index
     * @param currentBranch the branch name
     */
    public ProjectStatus(boolean hasChanges, int aheadIndex, int behindIndex, String currentBranch) {
        this(hasChanges, aheadIndex, behindIndex, currentBranch, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>(), new HashSet<>(),new HashSet<>());
    }

    /**
     * Constructs a ProjectStatus with all parameters.
     *
     * @param hasChanges     <code>true</code> if the project has changes <code>false</code> otherwise.
     * @param aheadIndex     the number of commits ahead index
     * @param behindIndex    the number of commits behind index
     * @param currentBranch  the branch name
     * @param conflicting    the set of files which has conflicting
     * @param untrackedFiles the set of files which weren't not added to index
     * @param changedFiles   the set of files which were added to index
     * @param removedFiles   the set of removed files which weren't not added to index
     */
    public ProjectStatus(boolean hasChanges, int aheadIndex, int behindIndex, String currentBranch,
                         Set<String> conflicting, Set<String> untrackedFiles, Set<String> changedFiles,
                         Set<String> removedFiles, Set<String> missingFiles, Set<String> modifiedFiles) {
        setHasChanges(hasChanges);
        setAheadIndex(aheadIndex);
        setBehindIndex(behindIndex);
        setCurrentBranch(currentBranch);
        setConflictedFiles(conflicting);
        setUntrackedFiles(untrackedFiles);
        setChangedFiles(changedFiles);
        setRemovedFiles(removedFiles);
        setMissingFiles(missingFiles);
        setModifiedFiles(modifiedFiles);
    }

    /**
     * Gets set of files which have conflicts.
     *
     * @return files set
     */
    public Set<String> getConflictedFiles() {
        return _conflictedFiles;
    }

    /**
     * Gets set of files which don't add to index (new files).
     *
     * @return files set
     */
    public Set<String> getUntrackedFiles() {
        return _untrackedFiles;
    }

    /**
     * Gets hasConflicts parameter.
     *
     * @return <code>true</code> if the project has conflicts <code>false</code> otherwise.
     */
    public boolean hasConflicts() {
        return !_conflictedFiles.isEmpty();
    }

    /**
     * Gets hasChanges parameter.
     *
     * @return <code>true</code> if the project has conflicts <code>false</code> otherwise.
     */
    public boolean hasChanges() {
        return _hasChanges;
    }

    /**
     * Project has new files which weren't added to index.
     *
     * @return <code>true</code> if the project has untracked files <code>false</code> otherwise.
     */
    public boolean hasNewUntrackedFiles() {
        return !_untrackedFiles.isEmpty();
    }

    /**
     * Gets the number of ahead commit.
     *
     * @return number
     */
    public int getAheadIndex() {
        return _aheadIndex;
    }

    /**
     * Gets the number of behind commit.
     *
     * @param number
     */
    public int getBehindIndex() {
        return _behindIndex;
    }

    /**
     * Gets a name of current branch
     *
     * @return a name (StringUtils.EMPTY if current branch isn't set).
     */
    public String getCurrentBranch() {
        return _currentBranch == null ? StringUtils.EMPTY : _currentBranch;
    }

    /**
     * Gets changed files (files which are located in staging)
     *
     * @return a set of files
     */
    public Set<String> getChangedFiles() {
        return _changedFiles;
    }

    /**
     * Gets removed files (deleted files which were added in staging)
     *
     * @return a set of files
     */
    public Set<String> getRemovedFiles() {
        return _removedFiles;
    }

    /**
     * Gets files which were deleted from local disk but haven't added to staging yet
     *
     * @return a set of files
     */
    public Set<String> getMissingFiles() {
        return _missingFiles;
    }

    /**
     * Gets modifies files. It is files which were changed but haven't added to staging yet
     *
     * @return a set of files
     */
    public Set<String> getModifiedFiles() {
        return _modifiedFiles;
    }

    private void setCurrentBranch(String currentBranch) {
        _currentBranch = currentBranch == null ? StringUtils.EMPTY : currentBranch;
    }

    private void setHasChanges(boolean hasChanges) {
        _hasChanges = hasChanges;
    }

    private void setAheadIndex(int aheadIndex) {
        _aheadIndex = aheadIndex < 0 ? 0 : aheadIndex;
    }

    private void setBehindIndex(int behindIndex) {
        _behindIndex = behindIndex < 0 ? 0 : behindIndex;
    }

    private void setUntrackedFiles(Set<String> uncommittedChanges) {
        _untrackedFiles = uncommittedChanges;
    }

    private void setModifiedFiles(Set<String> modifiedFiles) {
        _modifiedFiles = modifiedFiles;
    }

    private void setRemovedFiles(Set<String> removedFiles) {
        _removedFiles = removedFiles;
    }

    private void setMissingFiles(Set<String> missingFiles) {
        _missingFiles = missingFiles;
    }

    private void setChangedFiles(Set<String> changedFiles) {
        _changedFiles = changedFiles;
    }

    private void setConflictedFiles(Set<String> conflictingChanges) {
        _conflictedFiles = conflictingChanges;
    }

}
