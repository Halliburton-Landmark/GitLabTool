package com.lgc.gitlabtool.git.ui.javafx;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class WorkIndicatorDialog {

    private final ProgressIndicator progressIndicator = new ProgressIndicator(INDETERMINATE_PROGRESS);
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private final Stage stage = new Stage();
    private final Label label = new Label();
    private final Group root = new Group();
    private final Scene scene = new Scene(root);
    private final BorderPane mainPane = new BorderPane();
    private final VBox vbox = new VBox();

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
        this.label.setText(label);
    }

    /**
     * Execute work and show work-indicator dialog
     *
     * @param title    text for the dialog
     * @param runnable work that should be done
     */
    public void executeAndShowDialog(String title, Runnable runnable) {
        stage.setTitle(title);
        stage.getIcons().add(_appIcon);
        stage.initStyle(StageStyle.DECORATED);

        execute(runnable);

        stage.show();
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
        vbox.setMinSize(300, 100);
        vbox.getChildren().addAll(label, progressIndicator);
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