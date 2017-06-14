package com.lgc.gitlabtool.git.ui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class for holding an branch status icon
 *
 * @author Pavlo Pidhorniy
 */
public class LocalRemoteIconHolder {
    private static final String LOCAL_BRANCH_ICON_URL = "icons/branch/local_branch.png";
    private static final String REMOTE_BRANCH_ICON_URL = "icons/branch/remote_branch.png";

    private static LocalRemoteIconHolder instance = null;

    private static Image localBranchIconImage;
    private static ImageView localBranchIconImageView;

    private static Image remoteBranchIconImage;
    private static ImageView remoteBranchIconImageView;

    private LocalRemoteIconHolder() {
        localBranchIconImage = new Image(getClass().getClassLoader().getResource(LOCAL_BRANCH_ICON_URL).toExternalForm());
        localBranchIconImageView = new ImageView(localBranchIconImage);

        remoteBranchIconImage = new Image(getClass().getClassLoader().getResource(REMOTE_BRANCH_ICON_URL).toExternalForm());
        remoteBranchIconImageView = new ImageView(remoteBranchIconImage);
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static LocalRemoteIconHolder getInstance() {
        if (instance == null) {
            instance = new LocalRemoteIconHolder();
        }
        return instance;
    }

    /**
     * Gets the local branch icon's url
     *
     * @return icon's url
     */
    public String getLocalBranchIcoUrl() {
        return LOCAL_BRANCH_ICON_URL;
    }

    /**
     * Gets the remote branch icon's url
     *
     * @return icon's url
     */
    public String getRemoteBranchIcoUrl() {
        return REMOTE_BRANCH_ICON_URL;
    }

    /**
     * Gets the local branch icon's image
     *
     * @return icon's image
     */
    public Image getLocalBranchIcoImage() {
        return localBranchIconImage;
    }

    /**
     * Gets the icon's image
     *
     * @return icon's image
     */
    public Image getRemoteBranchIcoImage() {
        return remoteBranchIconImage;
    }

    /**
     * Gets the local branch icon's imageView
     *
     * @return icon's imageView
     */
    public ImageView getLocalBranchIcoImageView() {
        return localBranchIconImageView;
    }

    /**
     * Gets the remote branch icon's imageView
     *
     * @return icon's imageView
     */
    public ImageView getRemoteBranchIcoImageView() {
        return remoteBranchIconImageView;
    }
}
