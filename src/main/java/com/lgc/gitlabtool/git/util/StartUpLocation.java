/*
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lgc.gitlabtool.git.util;

import java.awt.*;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * X-Y position of a Window on active screen at startup if more than one screen.
 * Note: This works smooth only if the outer most AnchorPane size is fixed at
 * design time. This is because, if the size is not fixed JavaFX calculates
 * Window size after Stage.show() method. If the pref size is fixed, then use
 * this class in WindowEvent.WINDOW_SHOWING event, or if the pref size is set to
 * USE_COMPUTED_SIZE then use it in WindowEvent.WINDOW_SHOWN event (this will
 * give a quick splash Window though). Feel free to improve and share this code.
 * I am new to JavaFX so tired what I know so far. Tested on Windows but need
 * more attention to Linux and Mac
 *
 * @author
 */
public class StartUpLocation {

    private double xPos = 0;
    private double yPos = 0;

    /**
     * Get Top Left X and Y Positions for a Window to centre it on the
     * currently active screen at application startup
     *
     * @param windowWidth  - Window Width
     * @param windowHeight - Window Height
     */
    public StartUpLocation(double windowWidth, double windowHeight) {
        // Get X Y of start-up location on Active Screen
        // simple_JavaFX_App
        try {
            // Get current mouse location, could return null if mouse is moving Super-Man fast
            Point p = MouseInfo.getPointerInfo().getLocation();
            // Get list of available screens
            List<Screen> screens = Screen.getScreens();
            if (p != null && screens != null && screens.size() > 1) {
                // Screen bounds as rectangle
                Rectangle2D screenBounds;
                // Go through each screen to see if the mouse is currently on that screen
                for (Screen screen : screens) {
                    screenBounds = screen.getVisualBounds();
                    // Trying to compute Left Top X-Y position for the Application Window
                    // If the Point p is in the Bounds
                    if (screenBounds.contains(p.x, p.y)) {
                        // Fixed Size Window Width and Height
                        xPos = screenBounds.getMinX() + ((screenBounds.getMaxX() - screenBounds.getMinX() - windowWidth) / 2);
                        yPos = screenBounds.getMinY() + ((screenBounds.getMaxY() - screenBounds.getMinY() - windowHeight) / 2);
                        return;
                    }
                }
            }
        } catch (HeadlessException headlessException) {
            // Catch and report exceptions
            headlessException.printStackTrace();
        }
    }

    /**
     * @return the top left X Position of Window on Active Screen
     */
    public double getXPos() {
        return xPos;
    }

    /**
     * @return the top left Y Position of Window on Active Screen
     */
    public double getYPos() {
        return yPos;
    }
}