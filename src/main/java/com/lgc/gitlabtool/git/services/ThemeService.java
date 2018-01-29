package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public interface ThemeService extends Service {
    void styleScene(Scene scene);

    void setTheme(String themeName);

    ImageView getStyledImageView(String path);

    GLTTheme getCurrentTheme();
}
