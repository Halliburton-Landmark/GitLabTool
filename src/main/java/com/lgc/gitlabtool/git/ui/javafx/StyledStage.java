package com.lgc.gitlabtool.git.ui.javafx;

import javafx.scene.Parent;
import javafx.scene.Scene;

class CssStyledScene extends Scene {

    public CssStyledScene(Parent root) {
        super(root);
        root.getStylesheets().add(getClass().getClassLoader().getResource("css/modular_dark_style.css").toExternalForm());
    }

    public CssStyledScene(Parent root, double width, double height) {
        super(root, width, height);
        root.getStylesheets().add(getClass().getClassLoader().getResource("css/modular_dark_style.css").toExternalForm());
    }
}
