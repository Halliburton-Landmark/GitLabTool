package com.lgc.gitlabtool.git.ui.javafx;

import javafx.scene.paint.Color;

import java.util.Arrays;

public enum GLTTheme {
    DARK_THEME("dark", "darkThemeMenu", "css/modular_dark_style.css", "icons/mainmenu/dark_theme_16x16.png", "Dark theme",
            Color.SILVER, Color.LIGHTGREEN, Color.rgb(201,108,105)),

    LIGHT_THEME("light", "lightThemeMenu", "css/modular_light_style.css", "icons/mainmenu/light_theme_16x16.png", "Light theme",
            Color.rgb(59,59,59), Color.GREEN, Color.RED);

    private String _themeKey;
    private String _themeMenuId;
    private String _cssPath;
    private String _iconPath;
    private String _themeTitle;
    private Color _mainFontColorCss;
    private Color _successFontColorCss;
    private Color _errorFontColorCss;

    GLTTheme(String key, String menuId, String cssPath, String iconPath, String themeTitle, Color mainFontColorCss, Color successFontColorCss, Color errorFontColorCss){
        _themeKey = key;
        _themeMenuId = menuId;
        _cssPath = cssPath;
        _iconPath = iconPath;
        _themeTitle = themeTitle;
        _mainFontColorCss = mainFontColorCss;
        _successFontColorCss = successFontColorCss;
        _errorFontColorCss = errorFontColorCss;
    }

    public String getKey() {
        return _themeKey;
    }

    public String getPath() {
        return _cssPath;
    }

    public static GLTTheme getThemeByKey(String key){
        return Arrays.stream(GLTTheme.values())
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

    public String getIconPath() {
        return _iconPath;
    }

    public String getThemeTitle() {
        return _themeTitle;
    }

    public String getThemeMenuId() {
        return _themeMenuId;
    }
}
