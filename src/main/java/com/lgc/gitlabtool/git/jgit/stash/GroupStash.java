package com.lgc.gitlabtool.git.jgit.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link StashItem} for storing info about group stash for few project.
 *
 * It stores list of {@link Stash}s which include to this group and its message.
 *
 * @author Lyudmila Lyska
 */
public class GroupStash implements StashItem {
    private final String _message;
    private final List<Stash> _group;

    private static final String ICON_URL = "icons/stash/stash_group_item_20x20.png";

    /**
     * Constructor for creating object.
     *
     * @param message the message for current group.
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
