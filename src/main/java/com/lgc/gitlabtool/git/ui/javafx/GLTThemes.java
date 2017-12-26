package com.lgc.gitlabtool.git.ui.javafx;

import java.util.Arrays;

public enum GLTThemes {
    DARK_THEME("dark", "css/modular_dark_style.css"),
    LIGHT_THEME("light", "css/modular_light_style.css");

    private String _themeKey;
    private String _themePath;

    GLTThemes(String key, String path){
        _themeKey = key;
        _themePath = path;
    }

    public String getKey() {
        return _themeKey;
    }

    public String getPath() {
        return _themePath;
    }

    public static GLTThemes getThemeByKey(String key){
        return Arrays.stream(GLTThemes.values())
                .filter(theme -> theme.getKey().equals(key))
                .findFirst()
                .orElse(LIGHT_THEME);
    }
}
