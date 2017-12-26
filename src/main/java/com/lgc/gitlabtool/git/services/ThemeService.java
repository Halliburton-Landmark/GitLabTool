package com.lgc.gitlabtool.git.services;

import javafx.scene.Scene;

public interface ThemeService extends Service {
    void styleScene(Scene scene);

    public void setTheme(String themeName);
}
