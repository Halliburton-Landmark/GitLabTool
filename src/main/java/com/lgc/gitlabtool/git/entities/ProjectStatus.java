package com.lgc.gitlabtool.git.entities;

/**
 * Current status of the project<br>
 * 
 * This is the simplified version of the {@link org.eclipse.jgit.api.Status}<br>
 * It should be used to prevent {@link org.eclipse.jgit.api.Status} checking each time we need to update item in project list
 * 
 * @author Igor Khlaponin
 */
public enum ProjectStatus {
    /**
     * Shows that project has conflicts<br>
     * We could not do any Git operations before conflicts resolving
     */
    HAS_CONFLICTS,
    
    /**
     * Shows that project has uncommitted changes<br>
     * this is analog of {@link org.eclipse.jgit.api.Status#getUncommittedChanges()}
     */
    HAS_CHANGES,
    
    /**
     * Shows that project has no conflicts or uncommitted changes (index is clear)
     */
    DEFAULT
}
