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
     * Sets set of files which have conflicts.
     *
     * @param conflictingChanges the files
     */
    public void setConflictedFiles(Set<String> conflictingChanges) {
        _conflictedFiles = conflictingChanges;
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
     * Sets set of files which don't add to index (new files).
     *
     * @param uncommittedChanges the files
     */
    public void setUntrackedFiles(Set<String> uncommittedChanges) {
        _untrackedFiles = uncommittedChanges;
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
     *
     *
     * @return <code>true</code> if the project has untracked files <code>false</code> otherwise.
     */
    public boolean hasNewUntrackedFiles() {
        return !_untrackedFiles.isEmpty();
    }

    /**
     * Sets hasChanges parameter.
     *
     * @return <code>true</code> if the project has conflicts <code>false</code> otherwise.
     */
    public void setHasChanges(boolean hasChanges) {
        _hasChanges = hasChanges;
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
     * Sets the number of ahead commit.
     *
     * @param aheadIndex the number of ahead commit.
     */
    public void setAheadIndex(int aheadIndex) {
        _aheadIndex = aheadIndex < 0 ? 0 : aheadIndex;
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
     * Sets the number of behind commit.
     *
     * @param behindIndex the number of behind commit.
     */
    public void setBehindIndex(int behindIndex) {
        _behindIndex = behindIndex < 0 ? 0 : behindIndex;
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
     * Sets a name of current branch.
     *
     * @param currentBranch the branch name
     *
     * Sets StringUtils.EMPTY if the currentBranch is null.
     */
    public void setCurrentBranch(String currentBranch) {
        _currentBranch = currentBranch == null ? StringUtils.EMPTY : currentBranch;
    }

    public Set<String> getChangedFiles() {
        return _changedFiles;
    }

    public void setChangedFiles(Set<String> changedFiles) {
        _changedFiles = changedFiles;
    }

    public Set<String> getRemovedFiles() {
        return _removedFiles;
    }

    public void setRemovedFiles(Set<String> removedFiles) {
        _removedFiles = removedFiles;
    }

    public Set<String> getMissingFiles() {
        return _missingFiles;
    }

    public void setMissingFiles(Set<String> missingFiles) {
        _missingFiles = missingFiles;
    }

    public Set<String> getModifiedFiles() {
        return _modifiedFiles;
    }

    public void setModifiedFiles(Set<String> modifiedFiles) {
        _modifiedFiles = modifiedFiles;
    }

}
