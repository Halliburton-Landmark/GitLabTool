package com.lgc.gitlabtool.git.ui.javafx;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.util.ShutDownUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.UserInterface;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ProjectPropertiesUtil;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JavaFXUI extends Application implements UserInterface {
    private static final Logger logger = LogManager.getLogger(LoginDialog.class);

    private static final String TITLE_STATE_WARNING = "Error Exiting";
    private static final String HEADER_CONFIRMATION_MESSAGE = "Confirm exit";
    private static final String CONTENT_CONFIRMATION_MESSAGE = "Are you sure you want to exit?";
    private static final String HEADER_STATE_MESSAGE = "The application can't be closed. "
            + "\nWe have not finished some operations. Please wait.";
    private static final String CONTENT_STATE_PREFIX_MESSAGE ="Unfinished operations: ";
    private static final String EXIT_BUTTON_NAME = "Exit";

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

    private Image appIcon;
    private Stage mainStage;

    @Override
    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        mainStage = primaryStage;
        appIcon = AppIconHolder.getInstance().getAppIcoImage();

        URL modularWindow = getClass().getClassLoader().getResource(ViewKey.MODULAR_CONTAINER.getPath());
        if (modularWindow == null) {
            logger.error("Could not load fxml resource");
            return;
        }

        showLoginDialog();
        FXMLLoader fxmlLoader = new FXMLLoader(modularWindow);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle(ProjectPropertiesUtil.getProjectNameWithVersion());
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(appIcon);
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        _themeService.styleScene(primaryStage.getScene());

        /* Set sizing and position */
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        ScreenUtil.adaptForMultiScreens(primaryStage, primaryScreenBounds.getMaxX() / 1.5,
                primaryScreenBounds.getMaxY() / 1.5);

        primaryStage.setMinWidth(primaryScreenBounds.getMaxX() / 3);
        primaryStage.setMinHeight(primaryScreenBounds.getMaxY() / 3);

        primaryStage.show();
    }

    private final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        List<ApplicationState> activeStates = _stateService.getActiveStates();
        if(!activeStates.isEmpty()) {
            event.consume();
            showWarningAlertForActiveStates(activeStates);
            return;
        }
        showAlertConfirmation(event);
    };

    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog();
        Stage stage = (Stage) loginDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 300);

        loginDialog.showAndWait();
    }

    private void showAlertConfirmation(WindowEvent event) {
        Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION, CONTENT_CONFIRMATION_MESSAGE);
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.OK);

        exitButton.setText(EXIT_BUTTON_NAME);
        closeConfirmation.setHeaderText(HEADER_CONFIRMATION_MESSAGE);
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(mainStage);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.orElse(ButtonType.CANCEL))) {
            event.consume();
        } else {
            ShutDownUtil.shutdown();
        }
    }

    /**
     * Shows alert for user to report about operations which didn't finish.
     * If <code>activeStates</code> is equals to null or empty, an alert won't be shown.
     *
     * @param activeStates the active states
     */
    public static void showWarningAlertForActiveStates(List<ApplicationState> activeStates) {
        if (activeStates == null || activeStates.isEmpty()) {
            return;
        }
        GLTAlert statesAlert = new GLTAlert(AlertType.WARNING, TITLE_STATE_WARNING, HEADER_STATE_MESSAGE,
                CONTENT_STATE_PREFIX_MESSAGE + activeStates.toString());
        statesAlert.showAndWait();
    }
}
