package com.lgc.gitlabtool.git.entities;

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
public class ProjectStatus {
    private boolean _hasConflicts;
    private boolean _hasChanges;
    private int _aheadIndex;
    private int _behindIndex;
    private String _currentBranch;
    private Set<String> _conflictedFiles;
    private Set<String> _untrackedFiles;

    /**
     * Constructs a ProjectStatus with default parameters.
     */
    public ProjectStatus() {
        this(false, false, 0, 0, null);
    }

    /**
     * Constructs a ProjectStatus with a branch name parameter.
     *
     * @param currentBranch the branch name
     */
    public ProjectStatus(String currentBranch) {
        this(false, false, 0, 0, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with a hasConflicts and a hasChanges parameters.
     *
     * @param hasConflicts <true> if the project has conflicts <false> otherwise.
     * @param hasChanges   <true> if the project has changes <false> otherwise.
     */
    public ProjectStatus(boolean hasConflicts, boolean hasChanges) {
        this(hasConflicts, hasChanges, 0, 0, null);
    }

    /**
     * Constructs a ProjectStatus with a hasConflicts, a hasChanges and a branch name parameters.
     *
     * @param hasConflicts  <true> if the project has conflicts <false> otherwise.
     * @param hasChanges    <true> if the project has changes <false> otherwise.
     * @param currentBranch the branch name
     */
    public ProjectStatus(boolean hasConflicts, boolean hasChanges, String currentBranch) {
        this(hasConflicts, hasChanges, 0, 0, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with a behindIndex, a aheadIndex and a branch name parameters.
     *
     * @param aheadIndex    the number of commits ahead index
     * @param behindIndex   the number of commits behind index
     * @param currentBranch the branch name
     */
    public ProjectStatus(int aheadIndex, int behindIndex, String currentBranch) {
        this(false, false, aheadIndex, behindIndex, currentBranch);
    }

    /**
     * Constructs a ProjectStatus with all parameters.
     *
     * @param hasConflicts  <true> if the project has conflicts <false> otherwise.
     * @param hasChanges    <true> if the project has changes <false> otherwise.
     * @param aheadIndex    the number of commits ahead index
     * @param behindIndex   the number of commits behind index
     * @param currentBranch the branch name
     */
    public ProjectStatus(boolean hasConflicts, boolean hasChanges, int aheadIndex, int behindIndex, String currentBranch) {
        setHasConflicts(hasConflicts);
        setHasChanges(hasChanges);
        setAheadIndex(aheadIndex);
        setBehindIndex(behindIndex);
        setCurrentBranch(currentBranch);
    }

    /**
     * Constructs a ProjectStatus with all parameters.
     *
     * @param hasConflicts   <true> if the project has conflicts <false> otherwise.
     * @param hasChanges     <true> if the project has changes <false> otherwise.
     * @param aheadIndex     the number of commits ahead index
     * @param behindIndex    the number of commits behind index
     * @param currentBranch  the branch name
     * @param conflicting    the set of files with has conflicting
     * @param untrackedFiles the set of files which are not added to index
     */
    public ProjectStatus(boolean hasConflicts, boolean hasChanges, int aheadIndex, int behindIndex,
                         String currentBranch, Set<String> conflicting, Set<String> untrackedFiles) {
        setHasConflicts(hasConflicts);
        setHasChanges(hasChanges);
        setAheadIndex(aheadIndex);
        setBehindIndex(behindIndex);
        setCurrentBranch(currentBranch);
        setConflictedFiles(conflicting);
        setUntrackedFiles(untrackedFiles);
    }

    public Set<String> getConflictingFiles() {
        return _conflictedFiles;
    }

    public void setConflictedFiles(Set<String> conflictingChanges) {
        _conflictedFiles = conflictingChanges;
    }

    public Set<String> getUntrackedFiles() {
        return _untrackedFiles;
    }

    public void setUntrackedFiles(Set<String> uncommittedChanges) {
        _untrackedFiles = uncommittedChanges;
    }

    /**
     * Gets hasConflicts parameter.
     *
     * @return <true> if the project has conflicts <false> otherwise.
     */
    public boolean hasConflicts() {
        return _hasConflicts;
    }

    /**
     * Sets hasConflicts parameter.
     *
     * @return <true> if the project has conflicts <false> otherwise.
     */
    public void setHasConflicts(boolean hasConflicts) {
        _hasConflicts = hasConflicts;
    }

    /**
     * Gets hasChanges parameter.
     *
     * @return <true> if the project has conflicts <false> otherwise.
     */
    public boolean hasChanges() {
        return _hasChanges;
    }

    /**
     * Sets hasChanges parameter.
     *
     * @return <true> if the project has conflicts <false> otherwise.
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

}
