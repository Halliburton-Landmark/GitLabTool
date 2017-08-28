package com.lgc.gitlabtool.git.entities;

import org.apache.commons.lang.StringUtils;

public class ProjectStatus {

    private boolean _hasConflicts;
    private boolean _hasChanges;
    private int _aheadIndex;
    private int _behindIndex;
    private String _currentBranch;

    public ProjectStatus() {}

    public ProjectStatus(String currentBranch) {
        this(false, false, 0, 0, currentBranch);
    }

    public ProjectStatus(boolean hasConflicts, boolean hasChanges) {
        this(hasConflicts, hasChanges, 0, 0, null);
    }

    public ProjectStatus(boolean hasConflicts, boolean hasChanges, String currentBranch) {
        this(hasConflicts, hasChanges, 0, 0, currentBranch);
    }

    public ProjectStatus(int aheadIndex, int behindIndex, String currentBranch) {
        this(false, false, aheadIndex, behindIndex, currentBranch);
    }

    public ProjectStatus(boolean hasConflicts, boolean hasChanges, int aheadIndex, int behindIndex, String currentBranch) {
        setHasConflicts(hasConflicts);
        setHasChanges(hasChanges);
        setAheadIndex(aheadIndex);
        setBehindIndex(behindIndex);
        setCurrentBranch(currentBranch);
    }

    public boolean isHasConflicts() {
        return _hasConflicts;
    }

    public void setHasConflicts(boolean hasConflicts) {
        _hasConflicts = hasConflicts;
    }

    public boolean isHasChanges() {
        return _hasChanges;
    }

    public void setHasChanges(boolean hasChanges) {
        _hasChanges = hasChanges;
    }

    public int getAheadIndex() {
        return _aheadIndex;
    }

    public void setAheadIndex(int aheadIndex) {
        _aheadIndex = aheadIndex < 0 ? 0 : aheadIndex;
    }

    public int getBehindIndex() {
        return _behindIndex;
    }

    public void setBehindIndex(int behindIndex) {
        _behindIndex = behindIndex < 0 ? 0 : behindIndex;
    }

    public String getCurrentBranch() {
        return _currentBranch;
    }

    public void setCurrentBranch(String currentBranch) {
        _currentBranch = currentBranch == null ? StringUtils.EMPTY : currentBranch;
    }

}
