package com.lgc.solutiontool.git.ui.javafx.controllers;

import java.io.IOException;
import java.util.List;

import com.lgc.solutiontool.git.services.NetworkService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.ui.icon.AppIconHolder;
import com.lgc.solutiontool.git.util.URLManager;
import com.lgc.solutiontool.git.xml.Server;
import com.lgc.solutiontool.git.xml.Servers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServerInputWindowController {

    private final String WRONG_INPUT_MESSAGE = "Wrong input! Please try again";
    private final String WRONG_SERVER_ADDRESS_MESSAGE = "Please enter an URL of existing \nGitLab server";
    private final String SERVER_ALREADY_EXIST_MESSAGE = "This server already exist!";
    private final String VERIFYING_MESSAGE = "URL is verifying. Please wait...";
    private final int SERVICE_UNAVAILABLE_ERROR_CODE = 503;

    private StorageService storageService = (StorageService) ServiceProvider.getInstance()
            .getService(StorageService.class.getName());
    private NetworkService networkService = (NetworkService) ServiceProvider.getInstance()
            .getService(NetworkService.class.getName());

    @FXML
    private Label server;

    @FXML
    private TextField serverTextField;

    @FXML
    private Button okButton;

    @FXML
    private Label message;

    @FXML
    private ComboBox<String> api;

    public ServerInputWindowController() {
    }

    @FXML
    private void initialize() {
        message.setVisible(false);

        api.getItems().addAll("v3");
        api.setValue(api.getItems().get(0));
    }

    public void loadServerInputWindow(Parent root) throws IOException {
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Server selection");
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        stage.getIcons().add(appIcon);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void onOkButton() throws Exception {
        showMessage(VERIFYING_MESSAGE);

        if (!isInputValid(serverTextField.getText())) {
            showMessage(WRONG_INPUT_MESSAGE);
            return;
        }
        if (isServerAlreadyExists(serverTextField.getText())) {
            showMessage(SERVER_ALREADY_EXIST_MESSAGE);
            return;
        }
        
        networkService.runURLVerification(serverTextField.getText(), (responseCode) -> {
            if (responseCode > 0 && responseCode != SERVICE_UNAVAILABLE_ERROR_CODE) {
                Platform.runLater(() -> {
                    updateServersList();
                    Stage stage = (Stage) okButton.getScene().getWindow();
                    stage.close();
                });
            } else {
                Platform.runLater(() -> {
                    showMessage(WRONG_SERVER_ADDRESS_MESSAGE);
                });
            }
        });
    }

    private void updateServersList() {
        String inputServerName = URLManager.trimServerURL(serverTextField.getText());
        List<Server> servers = storageService.loadServers().getServers();
        int index = servers.size() - 1;
        if (inputServerName != null && !inputServerName.equals("")) {
            servers.add(index, new Server(inputServerName, api.getValue()));
        }
        storageService.updateServers(new Servers(servers));
    }

    private void showMessage(String msg) {
        message.setText(msg);
        message.setVisible(true);
    }

    private boolean isInputValid(String inputURL) {
        return URLManager.isURLValid(inputURL);
    }

    private boolean isServerAlreadyExists(String inputURL) {
        String url = URLManager.trimServerURL(inputURL);
        List<Server> servers = storageService.loadServers().getServers();
        return servers.contains(new Server(url, api.getValue()));
    }

    public void setAPIVersion() {
        // TODO: manage API version from ComboBox
    }
    
}
