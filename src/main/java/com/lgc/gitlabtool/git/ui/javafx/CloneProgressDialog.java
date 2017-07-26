package com.lgc.gitlabtool.git.ui.javafx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.util.ScreenUtil;

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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog box for tracking the process of cloning a group.
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressDialog extends Dialog<DialogDTO> {

    private final Stage _stage;
    private final ProgressBar _progressBar = new ProgressBar(0);
    private final ProgressIndicator _progressIndicator = new ProgressIndicator();

    private final Label _currentGroupLabel;
    private final Label _currentProjectLabel;

    private final Button _cancelButton;
    private final ListView<CloningMessage> _messageConcole;
    private Supplier<Object> _startAction;

    private final String DEFAULT_PROJECT_LABEL = "...";

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    public CloneProgressDialog(Stage primaryStage, String groupName, ApplicationState state) {
        setTitle("Cloning groups...");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label groupsLabel = new Label("Group: ");
        _currentGroupLabel = new Label(groupName == null ? "" : groupName);
        grid.add(groupsLabel, 0, 1);
        grid.add(_currentGroupLabel, 1, 1, 1, 1);

        _currentProjectLabel = new Label(DEFAULT_PROJECT_LABEL);
        _currentProjectLabel.setStyle("-fx-text-fill:blue");
        grid.add(_currentProjectLabel, 1, 2);

        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.BOTTOM_RIGHT);
        progressBox.getChildren().add(_progressBar);
        progressBox.getChildren().add(_progressIndicator);
        grid.add(progressBox, 0, 2);

        _messageConcole = new ListView<>();
        _messageConcole.setCellFactory(param -> new ListCell<CloningMessage>() {

            @Override
            protected void updateItem(CloningMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                }

                if (item != null) {
                    if (item.getStatus() != CloningMessageStatus.SIMPLE) {
                        setStyle(item.getCSSForStatus());
                    }
                    setText(item.getMessage());
                }
            }
        });
        _messageConcole.setMouseTransparent(false);
        _messageConcole.setFocusTraversable(false);
        _messageConcole.setMinSize(600, 100);
        addMessageToConcole("The cloning process of the " + groupName + " group is started...", CloningMessageStatus.SIMPLE);
        grid.add(_messageConcole, 0, 3, 4, 3);

        _cancelButton = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(_cancelButton);
        grid.add(hbBtn, 3, 7);

        _cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                _cancelButton.setDisable(true);
                addMessageToConcole("Starting cancel process of cloning...", CloningMessageStatus.SIMPLE);
                JGit.getInstance().cancelClone();
                updateProgressBar(0.0);
            }
        });
        _progressIndicator.setMaxSize(20, 20);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Scene scene = new Scene(grid, 650, 350);

        Stage stage = new Stage(primaryStage.getStyle());
        stage.setMinWidth(650);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Cloning groups...");
        stage.getIcons().add(appIcon);
        stage.setOnCloseRequest(event -> {
            if (_stateService.isActiveState(state)) {
                event.consume();
            }
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        _stage = stage;
        /* Set size and position */
        ScreenUtil.adaptForMultiScreens(stage, 500, 350);
    }

    public void addMessageToConcole(String message, CloningMessageStatus status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _messageConcole.getItems().add(new CloningMessage(currentDateToString() + message, status));
                _messageConcole.refresh();
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

    /**
     * Sets start action for this dialog
     *
     * @param action the action which will be launched before the dialog is displayed
     */
    public void setStartAction(Supplier<Object> action) {
        if (action != null) {
            _startAction = action;
        }
    }

    public void showDialog() {
        if (_startAction != null) {
            _startAction.get();
        }
        _stage.showAndWait();
    }

    public void updateProgressBar(final double counter) {
        _progressBar.setProgress(counter);
    }

    public void resetProgress() {
        updateProgressBar(0.0);
        _progressIndicator.setVisible(false);
        makeCancelButtonAsOk();
        updateProjectLabel(DEFAULT_PROJECT_LABEL);
    }

    private void makeCancelButtonAsOk() {
        _cancelButton.setDisable(false);
        _cancelButton.setText("OK");
        _cancelButton.setOnAction(event -> {
            getStage().close();
        });
    }

    private Stage getStage() {
        return (Stage) _cancelButton.getScene().getWindow();
    }

    private String currentDateToString() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return "[" + dateFormat.format(date) + "] ";
    }

    class CloningMessage {
        private final StringProperty _message;
        private final CloningMessageStatus _status;

        public CloningMessage(String message, CloningMessageStatus status) {
            if (message == null || status == null) {
                throw new IllegalAccessError("Invalid parameters");
            }
            _message = new SimpleStringProperty(message);
            _status = status;
        }

        public String getMessage() {
            return _message.get();
        }

        public CloningMessageStatus getStatus() {
            return _status;
        }

        public String getCSSForStatus() {
            return CloningMessageStatus.getCSSForStatus(_status);
        }
    }

    /**
     * Status of cloning message for the CloneProgressDialog. It class is needed to get CSS for each status.
     *
     * @author Lyudmila Lyska
     */
    public enum CloningMessageStatus {
        ERROR, SUCCESS, SIMPLE;

        /**
         * Gets CSS style for status
         *
         * @param status for getting CSS
         * @return string with code style
         */
        public static String getCSSForStatus(CloningMessageStatus status) {
            if (status == null || status == CloningMessageStatus.SIMPLE) {
                return "-fx-text-fill:black";
            }
            return status == CloningMessageStatus.ERROR ? "-fx-text-fill:red" : "-fx-text-fill:green";
        }
    }
}
