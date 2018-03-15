package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.preferences.ApplicationPreferences;
import com.lgc.gitlabtool.git.preferences.PreferencesNodes;
import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import com.lgc.gitlabtool.git.ui.javafx.listeners.ThemeChangeListener;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThemeServiceImpl implements ThemeService {

    /**
     * This is the main node of ApplicationPreferences.
     * You mustn't use it to change prefs. Use {@link #getThemePrefs()} instead
     */
    private ApplicationPreferences themePrefs;
    private GLTTheme currentGLTThemes;
    private static final Double LIGHTING_COEFFICIENT_FOR_DARK_THEMES = +0.65;

    private List<ThemeChangeListener> themeChangeListeners = new ArrayList<ThemeChangeListener>();

    private static final Logger _logger = LogManager.getLogger(ThemeService.class);

    ThemeServiceImpl(ApplicationPreferences preferences) {
        themePrefs = preferences;
        String currentThemeKey = getThemePrefs().get(PreferencesNodes.THEME_PREFS_NODE, GLTTheme.LIGHT_THEME.getKey());
        currentGLTThemes = GLTTheme.getThemeByKey(currentThemeKey);
    }

    public void styleScene(Scene scene) {
        if (scene == null) {
            return;
        }
        if (scene.getStylesheets() == null) {
            return;
        }
        if (!scene.getStylesheets().isEmpty()) {
            scene.getStylesheets().clear();
        }

        URL cssUrl = getClass().getClassLoader().getResource(currentGLTThemes.getPath());
        if (cssUrl == null) {
            _logger.error("Could not load css resource: " + currentGLTThemes.getPath());
            return;
        }

        scene.getStylesheets().add(cssUrl.toExternalForm());
    }

    public void setTheme(String themeName) {
        currentGLTThemes = GLTTheme.getThemeByKey(themeName);
        getThemePrefs().put(PreferencesNodes.THEME_PREFS_NODE, currentGLTThemes.getKey());
        notifyThemeChangeEvent(themeName);
    }

    @Override
    public ImageView getStyledImageView(String path) {
        URL imageUrl = getClass().getClassLoader().getResource(path);
        if (imageUrl == null) {
            _logger.error("Could not load image resource: " + path);
            return new ImageView();
        }
        Image image = new Image(imageUrl.toExternalForm());
        ImageView view = new ImageView(image);

        if (currentGLTThemes.equals(GLTTheme.DARK_THEME)) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(LIGHTING_COEFFICIENT_FOR_DARK_THEMES);

            view.setEffect(colorAdjust);
        }

        return view;
    }

    @Override
    public GLTTheme getCurrentTheme() {
        return currentGLTThemes;
    }

    @Override
    public Double getLightningCoefficient() {
        return LIGHTING_COEFFICIENT_FOR_DARK_THEMES;
    }

    /**
     * Returns the theme preferences node of Application preferences
     *
     * @return theme preferences node
     */
    private ApplicationPreferences getThemePrefs() {
        return themePrefs.node(PreferencesNodes.THEME_PREFS_NODE);
    }

    @Override
    public void addThemeChangeListener(ThemeChangeListener listener) {
        themeChangeListeners.add(listener);
    }

    @Override
    public void removeThemeChangeListener(ThemeChangeListener listener) {
        themeChangeListeners.remove(listener);
    }

    @Override
    public Effect getLightEffect() {
        boolean isDarkTheme = getCurrentTheme().equals(GLTTheme.DARK_THEME);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(getLightningCoefficient());
        return isDarkTheme ? colorAdjust : null;
    }

    private void notifyThemeChangeEvent(String themeName) {
        for(ThemeChangeListener listener : themeChangeListeners) {
            listener.onChanged(themeName);
        }
    }
}
