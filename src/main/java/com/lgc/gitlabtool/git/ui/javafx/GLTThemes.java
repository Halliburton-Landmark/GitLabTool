package com.lgc.gitlabtool.git.ui.javafx;

import javafx.scene.paint.Color;

import java.util.Arrays;

public enum GLTThemes {
    DARK_THEME("dark", "css/modular_dark_style.css", Color.SILVER, Color.LIGHTGREEN, Color.rgb(201,108,105)),
    LIGHT_THEME("light", "css/modular_light_style.css", Color.rgb(59,59,59), Color.GREEN, Color.RED);

    private String _themeKey;
    private String _themePath;
    private Color _mainFontColorCss;
    private Color _successFontColorCss;
    private Color _errorFontColorCss;

    GLTThemes(String key, String path, Color mainFontColorCss, Color successFontColorCss, Color errorFontColorCss){
        _themeKey = key;
        _themePath = path;
        _mainFontColorCss = mainFontColorCss;
        _successFontColorCss = successFontColorCss;
        _errorFontColorCss = errorFontColorCss;
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

    public Color getMainFontColorCss() {
        return _mainFontColorCss;
    }

    public Color getSuccessFontColorCss() {
        return _successFontColorCss;
    }

    public Color getErrorFontColorCss() {
        return _errorFontColorCss;
    }
}
