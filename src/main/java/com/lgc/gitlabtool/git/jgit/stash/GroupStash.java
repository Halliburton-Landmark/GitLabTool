package com.lgc.gitlabtool.git.jgit.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class GroupStash implements StashItem {
    private final String _message;
    private final List<Stash> _group;

    private static final String ICON_URL = "icons/stash/stash_group_item_20x20.png";

    /**
     *
     *
     * @param message
     */
    public GroupStash(String groupMessage) {
        _message = groupMessage;
        _group = new ArrayList<>();
    }

    @Override
    public String getMessage() {
        return _message;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String getIconURL() {
        return ICON_URL;
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
