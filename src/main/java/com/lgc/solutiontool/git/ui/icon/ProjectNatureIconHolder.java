package com.lgc.solutiontool.git.ui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class for holding an project type icon
 *
 * @author Pavlo Pidhorniy
 */
public class ProjectNatureIconHolder {
    private static final String DS_PROJECT_ICON_URL = "icons/dsg_project.png";
    private static final String UNKNOWN_PROJECT_ICON_URL = "icons/unknown_project_2.png";

    private static ProjectNatureIconHolder instance = null;

    private static Image dsProjectIconImage;
    private static ImageView dsProjectIconImageView;

    private static Image unknownProjectIconImage;
    private static ImageView unknownProjectIconImageView;

    private ProjectNatureIconHolder() {
        dsProjectIconImage = new Image(getClass().getClassLoader().getResource(DS_PROJECT_ICON_URL).toExternalForm());
        dsProjectIconImageView = new ImageView(dsProjectIconImage);

        unknownProjectIconImage = new Image(getClass().getClassLoader().getResource(UNKNOWN_PROJECT_ICON_URL).toExternalForm());
        unknownProjectIconImageView = new ImageView(unknownProjectIconImage);
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ProjectNatureIconHolder getInstance() {
        if (instance == null) {
            instance = new ProjectNatureIconHolder();
        }
        return instance;
    }

    /**
     * Gets the ds project icon's url
     *
     * @return icon's url
     */
    public String getDsProjectIcoUrl() {
        return DS_PROJECT_ICON_URL;
    }

    /**
     * Gets the unknown project icon's url
     *
     * @return icon's url
     */
    public String getUnknownProjectIcoUrl() {
        return UNKNOWN_PROJECT_ICON_URL;
    }

    /**
     * Gets the ds project icon's image
     *
     * @return icon's image
     */
    public Image getDsProjectIcoImage() {
        return dsProjectIconImage;
    }

    /**
     * Gets the unknown project icon's image
     *
     * @return icon's image
     */
    public Image getUnknownProjectIcoImage() {
        return unknownProjectIconImage;
    }

    /**
     * Gets the ds project icon's imageView
     *
     * @return icon's imageView
     */
    public ImageView getDsProjectIcoImageView() {
        return dsProjectIconImageView;
    }

    /**
     * Gets the unknown project icon's imageView
     *
     * @return icon's imageView
     */
    public ImageView getUnknownProjectIcoImageView() {
        return unknownProjectIconImageView;
    }
}
