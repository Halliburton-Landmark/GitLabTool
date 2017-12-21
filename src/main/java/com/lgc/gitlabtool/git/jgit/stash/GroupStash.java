package com.lgc.gitlabtool.git.jgit.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class GroupStash implements StashItem {
    private final String _message;
    private final List<Stash> _group;

    /**
     *
     *
     * @param message
     */
    public GroupStash() {
        _message = StringUtils.EMPTY;
        _group = new ArrayList<>(); // TODO is need concurrent ?
    }

    @Override
    public String getMessage() {
        return _message;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    /**
     * Adds stash to the current group
     */
    public void addStash(Stash stash) {
        _group.add(stash);
    }

    /**
     * Gets all group stashes. Return unmodifiable list
     */
    public List<Stash> getGroup() {
        return Collections.unmodifiableList(_group);
    }

}
