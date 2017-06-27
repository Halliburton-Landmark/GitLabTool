package com.lgc.gitlabtool.git.services;

import java.util.Collection;

/**
 * Service for mass changes in cloned groups and projects.
 *
 * @author Lyska Lyudmila
 */
public interface ReplacementService {

    /**
     * Replaces text in all projects of group that have a file with a name equals the fileName parameter.
     *
     * @param groupFolderPath the path to the folder that stores all cloned projects
     * @param fileName the file in that to want making the change
     * @param fromText string for replace
     * @param toText new string
     */
    public void replaceTextInFiles(String groupFolderPath, String fileName, String fromText, String toText);

    /**
     * Replaces text in the collection of projects that have a file with a name equals the fileName parameter.
     *
     * @param pathsProjects the collection of projects are some absolute paths to cloned projects of group.
     * @param fileName the file in that to want making the change
     * @param fromText string for replace
     * @param toText new string
     */
    public void replaceTextInFiles(Collection<String> pathsProjects, String fileName, String fromText, String toText);
}
