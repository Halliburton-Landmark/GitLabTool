package com.ystrazhko.git.services;

/**
 * @author Yevhen Strazhko
 * @version 2017
 */
public interface ReplacementService {

    /**
     * Replaces text in certain files
     *
     * @param groupFolderPath the path to the folder that stores all cloned projects
     * @param fileName the file in that to want making the change
     * @param fromText string for replace
     * @param toText new string
     */
    public void replaceTextInFiles(String groupFolderPath, String fileName, String fromText, String toText);
}
