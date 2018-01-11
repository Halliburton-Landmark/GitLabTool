package com.lgc.gitlabtool.git.jgit.stash;

/**
 * Interface for getting base info about a stash regardless of its type ({@link Stash} or {@link GroupStash}).
 *
 * @author Lyudmila Lyska
 */
public interface StashItem {

    /**
     * Gets stash message
     *
     * @return message
     */
    String getMessage();

    /**
     * Gets stash type
     *
     * @return <code>true</code> if it is group stash, otherwise <code>false</code>
     */
    boolean isGroup();

    /**
     * Gets URL for a icon by a stash type.
     *
     * @return URL
     */
    String getIconURL();
}
