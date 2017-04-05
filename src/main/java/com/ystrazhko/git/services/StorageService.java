package com.ystrazhko.git.services;

import com.ystrazhko.git.entities.Group;

import java.util.Map;

public interface StorageService {

    /**
     * Updates user preference storage
     *
     * @param server   Name of current git-server
     * @param username Name of current user
     * @return Status of updating storage
     */
    boolean updateStorage(String server, String username);

    /**
     * Load cloned user groups from local storage
     *
     * @param server   Name of current git-server
     * @param username Name of current user
     * @return Cloned groups and their directories
     */
    Map<Group, String> loadStorage(String server, String username);
}
