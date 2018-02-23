package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.GLTScene;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 * Displays the progress of some Git operation: pull, push, clone, etc.
 * <p>
 * The instance of {@link OperationProgressListener} should be used to show progress correctly
 *
 * @author Igor Khlaponin
 * @author Lyudmila Lyska
 *
 */
public abstract class ProgressDialog extends Dialog<Void> {

    private final Stage _stage;
    private final ProgressBar _progressBar = new ProgressBar(0);
    private final ProgressIndicator _progressIndicator = new ProgressIndicator();

    private final Label _currentProjectLabel;

    private final Button _cancelButton;
    private final ListView<OperationMessage> _messageConsole;
    private Runnable _startAction;

    private final String DEFAULT_PROJECT_LABEL = "...";

    private final StateService _stateService = ServiceProvider.getInstance().getService(StateService.class);

    private final BackgroundService _backgroundService = (BackgroundService) ServiceProvider.getInstance()
            .getService(BackgroundService.class);

    /**
     * Creates the instance of this class
     *
     * @param title -               title of the dialog
     * @param state -               {@link ApplicationState} - need to show which operation executed
     * @param cancelButtonStatus -  dialog shows disabled Cancel button if {@link CancelButtonStatus#DEACTIVATED}<br>
     *                              and enables it otherwise<br>
     *                              It is needed if we don't have some cancel logic
     */
    public ProgressDialog(String title, ApplicationState state, CancelButtonStatus cancelButtonStatus) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        _currentProjectLabel = new Label(DEFAULT_PROJECT_LABEL);
        _currentProjectLabel.setId("progress_dialog_title");
        grid.add(_currentProjectLabel, 1, 2);

        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.BOTTOM_RIGHT);
        progressBox.getChildren().add(_progressBar);
        progressBox.getChildren().add(_progressIndicator);
        grid.add(progressBox, 0, 2);

        _messageConsole = new ListView<>();
        _messageConsole.setCellFactory(param -> getNewListCell());
        _messageConsole.setMouseTransparent(false);
        _messageConsole.setFocusTraversable(false);
        _messageConsole.setMinSize(600, 100);
        grid.add(_messageConsole, 0, 3, 4, 3);

        _cancelButton = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(_cancelButton);
        grid.add(hbBtn, 3, 7);
        _cancelButton.setOnAction(onCancelAction());
        _cancelButton.setDisable(disactivateCancelButton(cancelButtonStatus));

        _progressIndicator.setMaxSize(20, 20);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();

        Stage stage = new Stage();
        stage.setMinWidth(650);
        stage.setResizable(false);
        stage.setScene(new GLTScene(grid, 650, 350));
        stage.setTitle(title);
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

    private boolean disactivateCancelButton(CancelButtonStatus status) {
        return status == CancelButtonStatus.DEACTIVATED ? true : false;
    }

    private ListCell<OperationMessage> getNewListCell() {
        return new ListCell<OperationMessage>() {

            @Override
            protected void updateItem(OperationMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                }

                if (item != null) {
                    setStyle(item.getCSSForStatus());
                    setText(item.getMessage());
                }
            }
        };
    }

    /**
     * Returns {@link EventHandler} that holds the event for Cancel button.<br>
     * It shows what should be done after Cancel button pressing.
     *
     * @return EventHandler of onCancelAction
     */
    protected EventHandler<ActionEvent> onCancelAction() {
        return event -> {
            // apply onCancel behavior here
        };
    }

    /**
     * Handles the event for OK button
     */
    protected void onOkButton(ActionEvent event) {
        ((Stage) _cancelButton.getScene().getWindow()).close();
    }

    protected Button getCancelButton() {
        return _cancelButton;
    }

    public void addMessageToConcole(String message, MessageType status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _messageConsole.getItems().add(new OperationMessage(currentDateToString() + message, status));
                _messageConsole.refresh();
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
    public void setStartAction(Runnable action) {
        if (action != null) {
            _startAction = action;
        }
    }

    /**
     * The method displays a dialog.
     * You should use it instead of the standard {@link Dialog#showAndWait()} and {@link Dialog#show()} methods
     */
    public void showDialog() {
        if (_startAction != null) {
            _backgroundService.runInBackgroundThread(_startAction);
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
        _cancelButton.setOnAction(this::onOkButton);
    }

    private String currentDateToString() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return "[" + dateFormat.format(date) + "] ";
    }

    private class OperationMessage {
        private final StringProperty _message;
        private final MessageType _status;

        private OperationMessage(String message, MessageType status) {
            if (message == null || status == null) {
                throw new IllegalAccessError("Invalid parameters");
            }
            _message = new SimpleStringProperty(message);
            _status = status;
        }

        public String getMessage() {
            return _message.get();
        }

        public String getCSSForStatus() {
            return MessageType.getCSSForStatus(_status);
        }
    }

    /**
     * Shows if we need to activate Cancel button for the {@link ProgressDialog}
     */
    public enum CancelButtonStatus {
        ACTIVATED,
        DEACTIVATED
    }
}
