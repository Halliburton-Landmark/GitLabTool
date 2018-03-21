package com.lgc.gitlabtool.git.ui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class for holding an main application icon
 *
 * @author Pavlo Pidhorniy
 */
public class AppIconHolder {
    private static final String APP_ICON_URL = "/icons/gitlab.png";
    private static AppIconHolder instance = null;

    private static Image appIconImage;
    private static ImageView appIconImageView;

    private AppIconHolder() {
        appIconImage = new Image(getClass().
                getResourceAsStream(APP_ICON_URL));
        appIconImageView = new ImageView(appIconImage);
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static AppIconHolder getInstance() {
        if (instance == null) {
            instance = new AppIconHolder();
        }
        return instance;
    }

    /**
     * Gets the icon's url
     *
     * @return icon's url
     */
    public String getAppIcoUrl() {
        return APP_ICON_URL;
    }

    /**
     * Gets the icon's image
     *
     * @return icon's image
     */
    public Image getAppIcoImage() {
        return appIconImage;
    }

    /**
     * Gets the icon's imageView
     *
     * @return icon's imageView
     */
    public ImageView getAppIcoImageView() {
        return appIconImageView;
    }
}
