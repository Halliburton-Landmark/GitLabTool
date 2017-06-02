package com.lgc.solutiontool.git.ui.javafx;

import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressDialog extends Dialog<DialogDTO> {

    private final ProgressBar _progressBar = new ProgressBar(0);
    private final ProgressIndicator _progressIndicator = new ProgressIndicator(0);

    private final Label _currentGroupLabel;
    private final Label _currentProjectLabel;

    private final TextArea _concole;
    private final Button _cancelButton;

    public CloneProgressDialog(Stage primaryStage, String groupName) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label groupsLabel = new Label("Group: ");
        _currentGroupLabel = new Label(groupName);
        grid.add(groupsLabel, 0, 1);
        grid.add(_currentGroupLabel, 1, 1);

        Label projectLabel = new Label("Project cloning: ");
        _currentProjectLabel = new Label("...");
        grid.add(projectLabel, 0, 2);
        grid.add(_currentProjectLabel, 1, 2);

        grid.add(_progressBar, 0, 3);
        grid.add(_progressIndicator, 1, 3);


        _concole = new TextArea();
        _concole.setDisable(true);
        grid.add(_concole, 4, 0, 2, 4);

        _cancelButton = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(_cancelButton);
        grid.add(hbBtn, 5, 4);

        _cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                JGit.getInstance().cancelClone();
                _cancelButton.setDisable(true);
            }
        });
        setTitle("Cloning groups...");
        Scene scene = new Scene(grid, 600, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cloning groups...");
        primaryStage.show();
    }

    public void addMessageToConcole(String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // TODO
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void updateGroupLabel(String groupLabel) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                StringProperty groupProperty = new SimpleStringProperty("");
                groupProperty.set(groupLabel);
                _currentGroupLabel.textProperty().bind(groupProperty);
            }
        });
    }

    public void updateProjectLabel(String projectLabel) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                StringProperty projectProperty = new SimpleStringProperty("");
                projectProperty.set(projectLabel);
                _currentProjectLabel.textProperty().bind(projectProperty);
            }
        });
    }

    public void updateValue(final double counter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                _progressBar.setProgress(counter);
                _progressIndicator.setProgress(counter);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
