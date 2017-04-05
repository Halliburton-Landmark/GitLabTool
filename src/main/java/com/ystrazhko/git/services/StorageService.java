package com.ystrazhko.git.services;

public interface StorageService {

    /**
     * Updates user preference storage
     *
     * @param server   Name of current git-server
     * @param username Name of current user
     * @return Status of updating storage
     */
    boolean updateStorage(String server, String username);

}
