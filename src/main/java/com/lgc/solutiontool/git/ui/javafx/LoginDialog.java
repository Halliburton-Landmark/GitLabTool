package com.lgc.solutiontool.git.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.ui.ViewKey;
import com.lgc.solutiontool.git.ui.javafx.controllers.ServerInputWindowController;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;
import com.lgc.solutiontool.git.util.URLManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

class LoginDialog extends Dialog<DialogDTO> {
	
	private StorageService storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());
	
	LoginDialog() {
        setTitle("GitLab Welcome");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        final Text scenetitle = new Text("Welcome To GitLab");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        final Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        final Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        ButtonType loginButtonType = new ButtonType("Sign in", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().add(loginButtonType);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        final Text repositoryText = new Text("Service: ");
        grid.add(repositoryText, 0, 3);
        
        ObservableList<String> options = getBoxOptions();	
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.valueProperty().addListener((observableValue, oldValue, currentValue) -> {
            if (currentValue.equals("Other...")) {
                try {
                    openServerInputWindow();
                    Platform.runLater(() -> {
                        modifyComboBoxItems(comboBox, options);
                    });
                } catch (IOException e) {
                    return;
                }
            }
        });
        comboBox.setValue(options.get(0));
        grid.add(comboBox, 1, 3, 1, 1);

        getDialogPane().setContent(grid);
        setResultConverter(dialogButton -> {
            String serverURL = URLManager.completeServerURL(comboBox.getValue());
            return dialogButton == loginButtonType ? new DialogDTO(userTextField.getText(), pwBox.getText(), serverURL)
                    : null;
        });
    }

    private ObservableList<String> getBoxOptions() {
        List<String> servers = new ArrayList<>();
        storageService.loadServers().getServers().forEach((e) -> {
            servers.add(e.getName());
        });
        ObservableList<String> options = FXCollections.observableArrayList(servers);
        return options;
    }

    private void modifyComboBoxItems(ComboBox<String> comboBox, ObservableList<String> options) {
        ObservableList<String> boxOptions = getBoxOptions();
        boxOptions.forEach(e -> {
            if (!options.contains(e)) {
                comboBox.getItems().add(options.size() - 1, e);
            }
        });
        comboBox.setValue(boxOptions.get(boxOptions.size() - 2));
    }

    private void openServerInputWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getClassLoader().getResource(ViewKey.SERVER_INPUT_WINDOW.getPath()));
        Parent root = fxmlLoader.load();
        ServerInputWindowController controller = (ServerInputWindowController) fxmlLoader.getController();
        controller.loadServerInputWindow(root);
    }

}