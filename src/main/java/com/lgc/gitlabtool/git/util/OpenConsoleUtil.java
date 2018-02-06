package com.lgc.gitlabtool.git.util;

import java.io.IOException;

public class OpenConsoleUtil {
    private static final String WINDOWS_TEMPLATE = "cmd /c start cmd.exe /K \"cd %s\"";
    private static final String UNIX_TEMPLATE = "/bin/bash -c \"cd %s\"";
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {
        System.out.println("Start");
        runConsole("d:\\neon-ws");
        System.out.println("End");
    }

    public static void runConsole(String path) {
        try {
            String template = getCommandTemplateForCurrentOS();
            Runtime.getRuntime().exec(String.format(template, path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCommandTemplateForCurrentOS() {
        if (isWindows()) {
            return WINDOWS_TEMPLATE;
        } else if (isUnix()) {
            return UNIX_TEMPLATE;
        } else {
            throw new IllegalArgumentException("Your OS is not supported!");
        }
    }

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }
}
