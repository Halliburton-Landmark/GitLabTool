package com.lgc.gitlabtool.git.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StorageService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.ServerInputWindowController;
import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.util.URLManager;
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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

class LoginDialog extends Dialog<DialogDTO> {

    private static final Logger logger = LogManager.getLogger(LoginDialog.class);

    private final StorageService storageService = (StorageService) ServiceProvider.getInstance()
            .getService(StorageService.class.getName());
    private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private final String WRONG_CREDENTIALS = "Wrong login or password! Please try again";
    private final String WAITING_MESSAGE = "Login... Please wait";
    private final String EMPTY_FIELD = "Login or password is empty!";

    private final Text sceneTitle;
    private final Label userName;
    private final TextField userTextField;
    private final Label password;
    private final PasswordField passwordField;
    private final Text repositoryText;
    private final ComboBox<String> comboBox;
    private final Label message;
    private final Button signInButton;

    LoginDialog() {
        setTitle("GitLab Welcome");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        sceneTitle = new Text("Welcome To GitLab");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        GridPane.setHalignment(sceneTitle, HPos.CENTER);
        grid.add(sceneTitle, 0, 0, 2, 1);

        userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        password = new Label("Password:");
        grid.add(password, 0, 2);

        passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        signInButton = new Button("Sign in");
        signInButton.setDefaultButton(true);
        GridPane.setHalignment(signInButton, HPos.RIGHT);
        setOnSignInButtonListener(signInButton);
        grid.add(signInButton, 1, 9, 1, 1);

        message = new Label();
        message.setText("blablabla");
        message.setVisible(false);
        grid.add(message, 0, 5, 3, 3);

        repositoryText = new Text("Service: ");
        grid.add(repositoryText, 0, 3);

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
        grid.add(comboBox, 1, 3, 1, 1);



        getDialogPane().setContent(grid);
        initializeOnCloseEvent();
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
        ServerInputWindowController controller = (ServerInputWindowController) fxmlLoader.getController();
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
                logger.info(WAITING_MESSAGE);
                showMessage(WAITING_MESSAGE, Color.GREEN);
                disableSignInButton(true);
                String serverURL = URLManager.completeServerURL(comboBox.getValue());
                DialogDTO dto = new DialogDTO(userTextField.getText(), passwordField.getText(), serverURL);
                _loginService.login(dto, responseCode -> {
                    if (responseCode == HttpStatus.SC_OK) {
                        updateLastUserName();
                        Platform.runLater(() -> {
                            logger.info("Login successfull");
                            getStage().close();
                        });
                    } else if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
                        Platform.runLater(() -> {
                            logger.warn(WRONG_CREDENTIALS);
                            showMessage(WRONG_CREDENTIALS, Color.RED);
                            disableSignInButton(false);
                        });
                    }
                });
            } else {
                logger.warn(EMPTY_FIELD);
                showMessage(EMPTY_FIELD, Color.RED);
            }
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
}