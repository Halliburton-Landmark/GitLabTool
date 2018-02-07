package com.lgc.gitlabtool.git.util;

import java.io.IOException;

public class OpenTerminalUtil {
    /**
     * Template for Windows command prompt
     * Hint: we need to choose appropriate disk first, or else the cd command won't work
     */
    private static final String WINDOWS_TEMPLATE = "cmd /c start cmd.exe /K \"%s: && cd %s\"";
    /**
     * Template only for gnome graphic shell
     * This command won't work if the Linux machine doesn't use default terminal (gnome-terminal)
     */
    private static final String UNIX_GNOME_TEMPLATE = "gnome-terminal --working-directory=%s";

    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Opens a terminal in the specified folder despite of the operation system
     *
     * @param path - path to the folder where the terminal should be started
     */
    public static void openInTerminal(String path) {
        try {
            String command = getCommandForCurrentOS(path);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCommandForCurrentOS(String path) {
        String command;

        if (isWindows()) {
            command = String.format(WINDOWS_TEMPLATE, getWindowsCoreDisk(path), path);
        } else if (isUnix()) {
            command = String.format(UNIX_GNOME_TEMPLATE, path);
        } else {
            throw new IllegalArgumentException("Your OS is not supported!");
        }
        return command;
    }

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    private static String getWindowsCoreDisk(String path) {
        return path.substring(0,1);
    }

}
