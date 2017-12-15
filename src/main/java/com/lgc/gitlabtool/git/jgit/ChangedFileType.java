package com.lgc.gitlabtool.git.jgit;

/**
 * Type for {@link ChangedFile}
 *
 * @author Lyudmila Lyska
 */
public enum ChangedFileType {

    /**
     * File was added to staging.
     */
    STAGED,

    /**
     * File wasn't added to staging.
     */
    UNSTAGED
}
