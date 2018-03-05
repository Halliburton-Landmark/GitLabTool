package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import com.lgc.gitlabtool.git.ui.javafx.listeners.ThemeChangeListener;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ThemeServiceImpl implements ThemeService {

    private static final String THEME_PREFS_KEY = "glt_theme";

    private Preferences themePrefs;
    private GLTTheme currentGLTThemes;
    private static final Double LIGHTING_COEFFICIENT_FOR_DARK_THEMES = +0.65;

    private List<ThemeChangeListener> themeChangeListeners = new ArrayList<ThemeChangeListener>();

    private static final Logger _logger = LogManager.getLogger(ThemeService.class);

    ThemeServiceImpl() {
        themePrefs = Preferences.userRoot().node(THEME_PREFS_KEY);

        String currentThemeKey = themePrefs.get(THEME_PREFS_KEY, GLTTheme.LIGHT_THEME.getKey());
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
        themePrefs.put(THEME_PREFS_KEY, currentGLTThemes.getKey());
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

    @Override
    public void addThemeChangeListener(ThemeChangeListener listener) {
        themeChangeListeners.add(listener);
    }

    @Override
    public void removeThemeChangeListener(ThemeChangeListener listener) {
        themeChangeListeners.remove(listener);
    }

    private void notifyThemeChangeEvent(String themeName) {
        for(ThemeChangeListener listener : themeChangeListeners) {
            listener.onChanged(themeName);
        }
    }
}
