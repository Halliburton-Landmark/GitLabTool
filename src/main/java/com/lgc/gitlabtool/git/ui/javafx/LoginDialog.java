package com.lgc.gitlabtool.git.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lgc.gitlabtool.git.util.ShutDownUtil;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StorageService;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.ServerInputWindowController;
import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.util.URLManager;
import com.lgc.gitlabtool.git.util.UserGuideUtil;
import com.lgc.gitlabtool.git.xml.Server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

class LoginDialog extends Dialog<DialogDTO> {

    private static final Logger logger = LogManager.getLogger(LoginDialog.class);

    private static final StorageService storageService = (StorageService) ServiceProvider.getInstance()
            .getService(StorageService.class.getName());

    private static final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

    private static final String MESSAGE_WRONG_CREDENTIALS              = "Wrong login or password! Please try again";
    private static final String MESSAGE_WAITING                        = "Login... Please wait";
    private static final String MESSAGE_EMPTY_FIELD                    = "Login or password is empty!";
    private static final String MESSAGE_NETWORK_IS_NO_LONGER_AVAILABLE = "Network is no longer available!";
    private static final String MESSAGE_HTTP_ERROR                     = "Error logging in:";
    private static final String MESSAGE_SPACE                          = " ";
    private static final String MESSAGE_DASH                           = "-";
    private static final String INFO_IMAGE_URL                         = "icons/info_20x20.png";
    private static final String CSS_PATH                               = "css/modular_dark_style.css";

    /** need to store two line message */
    private final double MIN_MESSAGE_HEIGHT = 40;

    private final Label sceneTitle;
    private final Label userName;
    private final TextField userTextField;
    private final Label password;
    private final PasswordField passwordField;
    private final Label repositoryText;
    private final ComboBox<String> comboBox;
    private final Label message;
    private final Button signInButton;
    private final Button infoButton;

    LoginDialog() {
        _themeService.styleScene(getDialogPane().getScene());
        setTitle("GitLab Welcome");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        sceneTitle = new Label("Welcome To GitLab");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        GridPane.setHalignment(sceneTitle, HPos.CENTER);
        grid.add(sceneTitle, 0, 1, 2, 1);

        userName = new Label("User Name:");
        grid.add(userName, 0, 2);

        userTextField = new TextField();
        grid.add(userTextField, 1, 2);

        password = new Label("Password:");
        grid.add(password, 0, 3);

        passwordField = new PasswordField();
        grid.add(passwordField, 1, 3);

        signInButton = new Button("Sign in");
        signInButton.setDefaultButton(true);
        GridPane.setHalignment(signInButton, HPos.RIGHT);
        setOnSignInButtonListener(signInButton);
        grid.add(signInButton, 1, 9, 1, 1);

        infoButton = new Button();
        setUpInfoButton(infoButton);
        grid.add(infoButton, 0, 9);

        message = new Label();
        message.setText("blablabla");
        message.setMinHeight(MIN_MESSAGE_HEIGHT);
        message.setVisible(false);
        grid.add(message, 0, 5, 3, 3);

        repositoryText = new Label("Service: ");
        grid.add(repositoryText, 0, 4);

        ObservableList<String> options = getBoxOptions();
        comboBox = new ComboBox<>(options);
        comboBox.valueProperty().addListener((observableValue, oldValue, currentValue) -> {
            if (currentValue.equals("Other...")) {
                try {
                    openServerInputWindow();
                    Platform.runLater(() -> {
                        modifyComboBoxItems(comboBox, options);
                    });
                } catch (IOException e) {
                    logger.error("", e);
                    return;
                }
            }
            setLastUserName(comboBox, userTextField);
        });
        comboBox.setValue(getDefaultComboBoxOption(options));
        grid.add(comboBox, 1, 4, 1, 1);

        getDialogPane().setContent(grid);
        addUserGuideKeyEvent();
        initializeOnCloseEvent();
    }

    private void setUpInfoButton(Button infoButton) {
        ImageView imageViewInfo = _themeService.getStyledImageView(INFO_IMAGE_URL);
        infoButton.setGraphic(imageViewInfo);

        /* ROUND (30 px is optimal size but can be changed) */
        infoButton.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 30px; " +
                        "-fx-min-height: 30px; " +
                        "-fx-max-width: 30px; " +
                        "-fx-max-height: 30px;"
        );

        /* HOVER ANIMATION */
        infoButton.getStylesheets().add(getClass().getClassLoader().getResource(CSS_PATH).toExternalForm());

        GridPane.setHalignment(infoButton, HPos.LEFT);
        infoButton.setTooltip(new Tooltip("Get info"));
        infoButton.setOnAction((event) -> UserGuideUtil.openUserGuide()); 
    }

    private ObservableList<String> getBoxOptions() {
        List<String> servers = new ArrayList<>();
        storageService.loadServers().getServers().forEach((e) -> {
            servers.add(e.getName());
        });
        ObservableList<String> options = FXCollections.observableArrayList(servers);
        options.add("Other...");
        return options;
    }

    private void modifyComboBoxItems(ComboBox<String> comboBox, ObservableList<String> options) {
        ObservableList<String> boxOptions = getBoxOptions();
        boxOptions.forEach(e -> {
            if (!options.contains(e)) {
                comboBox.getItems().add(0, e);
            }
        });
        comboBox.setValue(options.get(0));
    }

    private void openServerInputWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getClassLoader().getResource(ViewKey.SERVER_INPUT_WINDOW.getPath()));
        Parent root = fxmlLoader.load();
        ServerInputWindowController controller = fxmlLoader.getController();
        controller.loadServerInputWindow(root);
    }

    private void showMessage(String msg, Color color) {
        message.setText(msg);
        message.setTextFill(Color.web(color.toString()));
        message.setVisible(true);
    }

    private void setOnSignInButtonListener(Button button) {
        button.setOnAction(event -> {
            if (!isEmptyInputFields(userTextField, passwordField)) {
                logger.info(MESSAGE_WAITING);
                showMessage(MESSAGE_WAITING, Color.GREEN);
                disableSignInButton(true);
                String serverURL = URLManager.completeServerURL(comboBox.getValue());
                String shortServerURL = URLManager.shortServerURL(comboBox.getValue());
                DialogDTO dto = new DialogDTO(userTextField.getText(), passwordField.getText(), serverURL, shortServerURL);
                _loginService.login(dto, this::doAfterLogin);
            } else {
                logger.warn(MESSAGE_EMPTY_FIELD);
                showMessage(MESSAGE_EMPTY_FIELD, Color.RED);
            }
        });
    }

    private void doAfterLogin(HttpResponseHolder responseHolder) {
        if (responseHolder.getResponseCode() >= HttpStatus.SC_OK && responseHolder.getResponseCode() <= HttpStatus.SC_MULTI_STATUS) {
            updateLastUserName();
            Platform.runLater(() -> {
                _consoleService.addMessage("Login successfull", MessageType.SUCCESS);
                getStage().close();
            });
        } else if (responseHolder.getResponseCode() == HttpStatus.SC_UNAUTHORIZED) {
            showWarningAndDisableSignInButton(MESSAGE_WRONG_CREDENTIALS);
        } else if (responseHolder.getResponseCode() == 0) {
            showWarningAndDisableSignInButton(MESSAGE_NETWORK_IS_NO_LONGER_AVAILABLE);
        } else {
            StringBuilder errorMessage = new StringBuilder(MESSAGE_HTTP_ERROR);
            errorMessage.append(MESSAGE_SPACE)
                        .append(responseHolder.getResponseCode())
                        .append(MESSAGE_DASH)
                        .append(responseHolder.getResponseMessage());
            showWarningAndDisableSignInButton(errorMessage.toString());
        }
    }

    private void showWarningAndDisableSignInButton(String warningMessage) {
        Platform.runLater(() -> {
            logger.warn(warningMessage);
            showMessage(warningMessage, Color.RED);
            disableSignInButton(false);
        });
    }

    private void disableSignInButton(boolean trigger) {
        signInButton.setDisable(trigger);
    }

    private boolean isEmptyInputFields(TextField userTextField, PasswordField passwordField) {
        return userTextField == null
                || userTextField.getText().isEmpty()
                || passwordField == null
                || passwordField.getText().isEmpty();
    }

    private Stage getStage() {
        return (Stage) signInButton.getScene().getWindow();
    }

    /*
     * It should be used to close Login window via 'X' button without errors in main JavaFX thread
     * Need to find better solution
     */
    private void initializeOnCloseEvent() {
        Window window = this.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> {
            logger.debug("exit without logging in");
            ShutDownUtil.shutdown();
            System.exit(0);
        });
    }

    private void setLastUserName(ComboBox<String> comboBox, TextField userTextField) {
        String lastUserName = storageService.getLastUserName(comboBox.getValue());
        userTextField.setText(lastUserName.isEmpty() ? userTextField.getText() : lastUserName);
    }

    private void updateLastUserName() {
        storageService.updateLastUserName(comboBox.getValue(), userTextField.getText());
    }

    private String getDefaultComboBoxOption(ObservableList<String> options) {
        Server lastUsedServer = storageService.getLastUsedServer();
        return lastUsedServer != null ? lastUsedServer.getName() : options.get(0);
    }

    private void addUserGuideKeyEvent() {
        getDialogPane().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F1) {
                UserGuideUtil.openUserGuide();
            }
        });
    }

}