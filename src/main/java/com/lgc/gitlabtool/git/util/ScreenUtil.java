package com.lgc.gitlabtool.git.util;

import javafx.stage.Stage;

/**
 * Util class for adapting stage to multiple screens
 *
 * @author Pavlo Pidhorniy
 */
public class ScreenUtil {

    /**
     * Adapts stage for multiple screens
     *
     * @param stage  stage of window or dialog
     * @param width  width of window or dialog
     * @param height height of window or dialog
     */
    public static void adaptForMultiScreens(Stage stage, double width, double height) {
        StartUpLocation startUpLoc = new StartUpLocation(width, height);
        double xPos = startUpLoc.getXPos();
        double yPos = startUpLoc.getYPos();

        if (xPos != 0 && yPos != 0) {
            stage.setX(xPos);
            stage.setY(yPos);
        } else {
            stage.centerOnScreen();
        }
    }

}
