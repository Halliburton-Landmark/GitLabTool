package com.lgc.solutiontool.git.services;


import java.util.Map;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.xml.Servers;

/**
 * Class for work with program storage.
 *
 * @author Pavlo Pidhornyi
 */
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
    
    /**
     * Updates servers list in the file
     * 
     * @param servers to store in file
     * @return <code>true</code> if file was updated successfully or <code>false</code> if not
     */
    boolean updateServers(Servers servers);
    
    /**
     * Loads <code>Servers</code> instance from file that contains list of servers
     * 
     * @return instance of <code>Servers</code>
     */
    Servers loadServers();
}
