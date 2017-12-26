package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.ui.javafx.GLTThemes;
import javafx.scene.Scene;

import java.util.prefs.Preferences;

public class ThemeServiceImpl implements ThemeService {

    private static final String THEME_PREFS_KEY = "glt_theme";

    private Preferences themePrefs;
    private GLTThemes currentGLTThemes;

    ThemeServiceImpl() {
        themePrefs = Preferences.userRoot().node(THEME_PREFS_KEY);

        String currentThemeKey = themePrefs.get(THEME_PREFS_KEY, GLTThemes.LIGHT_THEME.getKey());
        currentGLTThemes = GLTThemes.getThemeByKey(currentThemeKey);
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
        scene.getStylesheets().add(
                getClass().getClassLoader().getResource(currentGLTThemes.getPath()).toExternalForm());
    }

    public void setTheme(String themeName){
        currentGLTThemes = GLTThemes.getThemeByKey(themeName);
        themePrefs.put(THEME_PREFS_KEY, currentGLTThemes.getKey());
    }
}
