package com.lgc.gitlabtool.git.jgit.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link Stash} for storing info about a group stash for few projects.
 *
 * It stores list of {@link SingleProjectStash}s which include to this group and its message.
 *
 * @author Lyudmila Lyska
 */
public class GroupStash implements Stash {
    private final String _message;
    private final List<SingleProjectStash> _group;

    private static final String ICON_URL = "icons/stash/stash_group_item_20x20.png";

    /**
     * Constructor for creating object.
     *
     * @param groupMessage the message for current group.
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
    public void addStash(SingleProjectStash stash) {
        _group.add(stash);
    }

    /**
     * Gets all group stashes. Return unmodifiable list
     */
    public List<SingleProjectStash> getGroup() {
        return Collections.unmodifiableList(_group);
    }

}
