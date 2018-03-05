package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import com.lgc.gitlabtool.git.ui.javafx.listeners.ThemeChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public interface ThemeService extends Service {
    /**
     * Styles selected theme according to current css file
     *
     * @param scene Scene for theming
     */
    void styleScene(Scene scene);

    /**
     * Change current theme in preferences
     *
     * @param themeName name of the theme
     */
    void setTheme(String themeName);

    /**
     * Returns ImageView with lightning effects according to current theme
     *
     * @param path path to the icon
     * @return image view with icon
     */
    ImageView getStyledImageView(String path);

    /**
     * Returns a current theme
     * @return theme
     */
    GLTTheme getCurrentTheme();

    /**
     * Returns a lightning coefficient which used for coloring icons in dark themes
     *
     * @return lightning coefficient
     */
    Double getLightningCoefficient();

    /**
     * This method add {@link ThemeChangeListener}
     *
     * @param listener
     */
    public void addThemeChangeListener(ThemeChangeListener listener);

    /**
     * This method remove {@link ThemeChangeListener}
     *
     * @param listener
     */
    public void removeThemeChangeListener(ThemeChangeListener listener);

}
