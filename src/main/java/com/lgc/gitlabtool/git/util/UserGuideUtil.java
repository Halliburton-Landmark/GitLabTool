package com.lgc.gitlabtool.git.util;

import java.io.File;

/**
 * Utility class that works with User guide
 * 
 * @author Igor Khlaponin
 *
 */
public class UserGuideUtil {

    /**
     * User guide path.
     * According to realization, this file should be stored in target folder near the GitlabTool-full.jar file
     */
    private static final String USER_GUIDE_URL = "index.html";

    /**
     * Opens the index.htm file from the target folder
     */
    public static void openUserGuide() {
        String location = UserGuideUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        java.io.File file = new java.io.File(location);
        String parent = file.getParentFile().getPath();
        String path = parent + File.separator + USER_GUIDE_URL;
        FileLauncherUtil.open(path);
    }

}
