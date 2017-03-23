package com.ystrazhko.git.ui.javafx;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.*;
import javafx.util.Pair;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.exceptions.HTTPException;
import com.ystrazhko.git.jgit.JGit;
import com.ystrazhko.git.services.GroupsUserService;
import com.ystrazhko.git.services.LoginService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.util.JSONParser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class LoginDialog extends Dialog<Pair<String, String>> {

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

        getDialogPane().setContent(grid);
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(userTextField.getText(), pwBox.getText());
            }
            return null;
        });
    }
 }