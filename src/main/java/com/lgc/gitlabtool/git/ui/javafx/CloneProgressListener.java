package com.lgc.gitlabtool.git.ui.javafx;
import java.util.function.Consumer;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog.CloningMessageStatus;
import com.lgc.gitlabtool.git.util.NullCheckUtil;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Listener for responding to the process of cloning a group
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressListener implements ProgressListener {

    private final CloneProgressDialog _progressDialog;
    private Consumer<Object> _finishedAction;

    private static final String CLONING_STATUS_ALERT_TITLE = "Cloning info";
    private static final String CLONING_STATUS_ALERT_HEADER = "Cloning statuses: ";

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    public CloneProgressListener(CloneProgressDialog progressDialog) {
        if (progressDialog == null) {
            throw new IllegalAccessError("Invalid parameters");
        }
        _progressDialog = progressDialog;
    }

    public CloneProgressListener(CloneProgressDialog progressDialog, Consumer<Object> finishedAction) {
        this(progressDialog);
        _finishedAction = finishedAction;
    }

    @Override

    public void onSuccess(Object... t) {
        if (t[0] instanceof Project) {
            Project project = (Project) t[0];
            _progressDialog.addMessageToConcole(project.getName() + " project is successful cloned!",
                    CloningMessageStatus.SUCCESS);
        }
        if (t[1] instanceof Double) {
            double progress = (Double) t[1];
            _progressDialog.updateProgressBar(progress);
        }
        if (t[2] instanceof String) {
            _progressDialog.addMessageToConcole((String) t[2], CloningMessageStatus.SUCCESS);
        }
    }

    @Override
    public void onError(Object... t) {
        if (t[0] instanceof Double) {
            double progress = (Double) t[0];
            _progressDialog.updateProgressBar(progress);
        }
        if (t[1] instanceof String) {
            String message = (String) t[1];
            _progressDialog.addMessageToConcole(message, CloningMessageStatus.ERROR);
        }
    }

    @Override
    public void onStart(Object... t) {
        if (t[0] instanceof Project) {
            Project project = (Project) t[0];
            _progressDialog.updateProjectLabel(project.getName());
        }
    }

    @Override
    public void onFinish(Object... t) {
        _stateService.stateOFF(ApplicationState.CLONE);

        String messageStatus = t[0] instanceof String ? (String) t[0] : JGit.FINISH_CLONE_MESSAGE;
        _progressDialog.addMessageToConcole(messageStatus, CloningMessageStatus.SIMPLE);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _progressDialog.resetProgress();
                NullCheckUtil.acceptConsumer(_finishedAction, null);
                cloningStatusDialog(messageStatus);
            }
        });
    }

    private void cloningStatusDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(CLONING_STATUS_ALERT_TITLE);
        alert.setHeaderText(CLONING_STATUS_ALERT_HEADER);
        alert.setContentText(content);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 300);

        alert.showAndWait();
    }
}