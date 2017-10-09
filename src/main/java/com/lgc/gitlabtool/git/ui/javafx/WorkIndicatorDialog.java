package com.lgc.gitlabtool.git.ui.javafx;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Dialog that indicate doing some background processes
 */
public class WorkIndicatorDialog {

    private final ProgressIndicator progressIndicator = new ProgressIndicator(INDETERMINATE_PROGRESS);
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private final Stage stage = new Stage();
    private final Label _progressLabel = new Label();
    private final Group root = new Group();
    private final Scene scene = new Scene(root);
    private final BorderPane mainPane = new BorderPane();
    private final VBox vbox = new VBox();
    private final int initWidth = 300;
    private final int initHeight = 100;
    /**
     * Constructor
     *
     * @param owner owner of the dialog
     * @param label text that describes loading process
     */
    public WorkIndicatorDialog(Window owner, String label) {
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);
        _progressLabel.setText(label);
    }

    /**
     * Updates the progress label
     *
     * @param progressLabel the new label
     */
    public void updateProjectLabel(String progressLabel) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StringProperty projectProperty = new SimpleStringProperty("");
                projectProperty.set(progressLabel);
                _progressLabel.textProperty().bind(projectProperty);
            }
        });
    }

    /**
     * Execute work and show work-indicator dialog
     *
     * @param title    text for the dialog
     * @param runnable work that should be done
     * @param stageStyle style of stage
     */
    public void executeAndShowDialog(String title, Runnable runnable, StageStyle stageStyle) {
        stage.setTitle(title);
        stage.getIcons().add(_appIcon);
        stage.initStyle(stageStyle);

        execute(runnable);

        stage.show();
    }

    /**
     * Execute work and show work-indicator dialog
     *
     * @param title       text for the dialog
     * @param runnable    work that should be done
     * @param stageStyle  style of stage
     * @param parentStage stage of parent window (for calculating start coordinates)
     */
    public void executeAndShowDialog(String title, Runnable runnable, StageStyle stageStyle, Stage parentStage) {
        double centerXPosition = parentStage.getX() + parentStage.getWidth() / 2;
        double centerYPosition = parentStage.getY() + parentStage.getHeight() / 2;

        stage.setX(centerXPosition - initWidth / 2);
        stage.setY(centerYPosition - initHeight / 2);

        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(Color.TRANSPARENT);
        }
        executeAndShowDialog(title, runnable, stageStyle);
    }

    /**
     * Execute work without showing dialog
     * Note: may be useful with the {@link #getStage()} getStage} method for custom containers (as example - loading group)
     *
     * @param runnable work that should be done
     */
    public void execute(Runnable runnable) {
        setupDialog();
        setupWorkerThread(runnable);
    }

    /**
     * Gets Stage of the dialog
     * Note: may be useful with the {@link #execute(Runnable)} execute} method for custom containers (as example - loading group)
     *
     * @return stage of dialog
     */
    public Stage getStage() {
        return stage;
    }

    private void setupDialog() {
        root.getChildren().add(mainPane);
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinSize(initWidth, initHeight);
        vbox.getChildren().addAll(_progressLabel, progressIndicator);
        mainPane.setTop(vbox);
        stage.setScene(scene);
    }

    private void setupWorkerThread(Runnable func) {
        Task<Void> taskWorker = new Task<Void>() {
            @Override
            protected Void call() {
                func.run();
                return null;
            }
        };

        EventHandler<WorkerStateEvent> eh = event -> {
            stage.close();
        };

        taskWorker.setOnSucceeded(eh);
        taskWorker.setOnFailed(eh);

        new Thread(taskWorker, "Worker thread").start();
    }

}