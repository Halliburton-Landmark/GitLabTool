package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ScreenUtil;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;;

public class GLTDialog<T> extends Dialog<T> {

    private static final ThemeService _themeService = ServiceProvider.getInstance()
            .getService(ThemeService.class);

    /**
     * Creates a dialog without a specified owner.
     */
    public GLTDialog(String title) {
        super();
        _themeService.styleScene(getDialogPane().getScene());

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = getStage();
        stage.setResizable(false);
        stage.setTitle(title);
        stage.getIcons().add(appIcon);
        stage.initModality(Modality.APPLICATION_MODAL);

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 150);
    }

    private Stage getStage() {
        return (Stage) getDialogPane().getScene().getWindow();
    }


}
