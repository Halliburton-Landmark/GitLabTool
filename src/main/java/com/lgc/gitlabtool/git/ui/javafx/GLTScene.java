package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GLTScene extends Scene {

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

    public GLTScene(Parent root) {
        super(root);
        _themeService.styleScene(this);
    }

    public GLTScene(Parent root, double width, double height) {
        super(root, width, height);
        _themeService.styleScene(this);
    }
}
