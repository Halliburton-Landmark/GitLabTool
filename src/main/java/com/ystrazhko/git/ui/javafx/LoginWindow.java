package com.ystrazhko.git.ui.javafx;

import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.services.GroupsUserService;
import com.ystrazhko.git.services.LoginService;
import com.ystrazhko.git.services.ProjectService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.util.JSONParser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class LoginWindow {

    private LoginService _loginService;

    LoginWindow(Stage primaryStage, LoginService loginService) {
        setLoginService(loginService);
        primaryStage.setTitle("GitLab Welcome");
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

        final Button btn = new Button("Sign in");
        final HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                Gson gson = new Gson();
                String name = "LyskaL"; //userTextField.getText();
                String password = "2569_LyudA1"; //pwBox.getText();

                Object jsonUser = getLoginService().login(name, password);
                actiontarget.setText(String.valueOf(jsonUser != null));
                //debug code
                String jsonGroup = (String) ((GroupsUserService) ServiceProvider.getInstance().getService
                        (GroupsUserService.class.getName())).getGroups(jsonUser.toString());
                // test parser
                User user = JSONParser.parseToObject(jsonUser, User.class);
                Collection<Group> groups = JSONParser.parseToCollectionObjects(jsonGroup, new TypeToken<List<Group>>(){}.getType());
                Group group = (Group) groups.toArray()[0];

                Object jsonProjects = ((ProjectService)ServiceProvider.getInstance().getService(
                        ProjectService.class.getName())).getProjects(String.valueOf(group.getId()));
            }
        });

        final Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private LoginService getLoginService() {
        return _loginService;
    }

    private void setLoginService(LoginService loginService) {
        _loginService = loginService;
    }
}