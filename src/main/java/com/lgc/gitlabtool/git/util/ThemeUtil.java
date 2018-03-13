package com.lgc.gitlabtool.git.util;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;

/**
 * This is utility class for light and dark themes
 *
 * Created by Oleksandr Kozlov on 3/13/2018.
 */
public class ThemeUtil {

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
        .getService(ThemeService.class);

    /**
     * This method return light effect for light and dark theme
     *
     * @return light effect
     */
    public static Effect getLightEffect(){
        boolean isDarkTheme = _themeService.getCurrentTheme().equals(GLTTheme.DARK_THEME);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(_themeService.getLightningCoefficient());

        return isDarkTheme ? colorAdjust : null;
    }
}
