package com.lgc.solutiontool.git.ui.javafx;

import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        
        ObservableList<String> options = FXCollections.observableArrayList(
        		"gitlab.com",
        		"gitlab.lgc.com"
        		);
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue(options.get(0));
        grid.add(comboBox, 1, 3, 1, 1);

        getDialogPane().setContent(grid);
        setResultConverter(dialogButton -> {
        	String urlMainPart = "https://" + comboBox.getValue() + "/api/v3";
        	return dialogButton == loginButtonType 
        			? new DialogDTO(userTextField.getText(), pwBox.getText(), urlMainPart)
        			: null;
        });
    }
}