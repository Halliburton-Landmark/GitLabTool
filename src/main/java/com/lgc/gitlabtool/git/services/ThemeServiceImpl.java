package com.lgc.gitlabtool.git.services;

import javafx.scene.Scene;

import java.util.prefs.Preferences;

public class ThemeServiceImpl implements ThemeService {

    private static String DARK_THEME_PATH = "css/modular_dark_style.css";
    private static String LIGHT_THEME_PATH = "css/modular_light_style.css";

    private String currentStyle;
    private static final String THEME_PREFS_KEY = "gtl_theme";

    private Preferences themePrefs;

    ThemeServiceImpl() {
        themePrefs = Preferences.userRoot().node(THEME_PREFS_KEY);

        String currentTheme = themePrefs.get(THEME_PREFS_KEY, "light");
        if(currentTheme.equals("dark")){
            currentStyle = DARK_THEME_PATH;
        }else if(currentTheme.equals("light")){
            currentStyle = LIGHT_THEME_PATH;
        }
        System.out.println(currentTheme);
    }

    public void styleScene(Scene scene) {
        if (scene == null) {
            return;
        }
        if (scene.getStylesheets() == null){
            return;
        }
        if (!scene.getStylesheets().isEmpty()) {
            scene.getStylesheets().clear();
        }
        scene.getStylesheets().add(getClass().getClassLoader().getResource(currentStyle).toExternalForm());
    }

    public void setTheme(String themeName){
        if (themeName.equals("dark")){
            currentStyle = DARK_THEME_PATH;
            themePrefs.put(THEME_PREFS_KEY, "dark");
        }else if(themeName.equals("light")){
            currentStyle = LIGHT_THEME_PATH;
            themePrefs.put(THEME_PREFS_KEY, "light");
        }
    }
}
