package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.PushProgressDialog;
import com.lgc.gitlabtool.git.util.NameValidator;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

public class CreateNewBranchDialog extends GLTDialog<String> {

    private static final String DIALOG_TITLE = "Create new branch";
    private static final String STATUS_DIALOG_TITLE = "Branch Creating status";
    private static final String STATUS_DIALOG_HEADER = "Branch creating info";
    private static final String CHOOSE_BRANCH_NAME_MESSAGE = "Please choose a new branch name";

    private static final GitService _gitService = ServiceProvider.getInstance().getService(GitService.class);

    private final Label _messageLabel;
    private final Label _textLabel;
    private final TextField _branchNameField;
    private final CheckBox _checkoutBox;
    private final CheckBox _pushToUpstreamBox;
    private final Button _createButton;
    private final Button _cancelButton;
    private final Label _startPointLabel;
    private final ComboBox<String> _comboBox;

    private List<Project> _projects;
    private final NameValidator _branchValidator = NameValidator.get();

    public List<Project> getProjects() {
        return _projects;
    }

    public void setProjects(List<Project> projects) {
        this._projects = projects;
    }

    public CreateNewBranchDialog(List<Project> projects) {
        super(DIALOG_TITLE);

        setProjects(projects);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        _messageLabel = new Label(CHOOSE_BRANCH_NAME_MESSAGE);
        grid.add(_messageLabel, 0, 1, 3, 1);

        _startPointLabel = new Label("Source: ");
        grid.add(_startPointLabel, 0, 2);
        ObservableList<String> options = getBoxOptions();
        _comboBox = new ComboBox<>(options);
        _comboBox.setValue(getDefaultBranchOption(options));
        _comboBox.valueProperty().addListener(getBoxItemsFilter());
        grid.add(_comboBox, 1, 2);

        _textLabel = new Label("New branch: ");
        grid.add(_textLabel, 0, 3);
        _branchNameField = new TextField();
        _branchNameField.textProperty().addListener(getInputFilter());
        grid.add(_branchNameField, 1, 3, 2, 1);

        _checkoutBox = new CheckBox("Checkout new branch");
        _checkoutBox.selectedProperty().addListener(getCheckoutListener());
        grid.add(_checkoutBox, 1, 4);
        _pushToUpstreamBox = new CheckBox("Push to upstream");
        _pushToUpstreamBox.selectedProperty().addListener(getPushListener());
        grid.add(_pushToUpstreamBox, 2, 4);

        _createButton = new Button("Create Branch");
        _createButton.setDisable(true);
        _createButton.setOnAction(this::onCreateButton);
        _createButton.setDefaultButton(true);

        _cancelButton = new Button("Cancel");
        _cancelButton.setOnAction(event -> {
            getStage().close();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(_createButton, _cancelButton);
        grid.add(hbBtn, 2, 7);

        setDialogContent(grid);

        initializeOnCloseEvent();
    }

    private ObservableList<String> getBoxOptions() {
        Set<Branch> branches = _gitService.getBranches(getProjects(), BranchType.ALL, true);
        Set<String> branchesNames = branches.stream()
                                            .map(branch -> branch.getBranchName())
                                            .collect(Collectors.toSet());
        ObservableList<String> options = FXCollections.observableArrayList(branchesNames);
        return options;
    }

    private String getDefaultBranchOption(ObservableList<String> options) {
        String currentBranchName = getCurrentCommonBranchName(getProjects());
        return options.stream()
                      .filter(element -> element.equals(currentBranchName))
                      .findFirst().orElse(StringUtils.EMPTY);
    }

    private String getCurrentCommonBranchName(List<Project> projects) {
        List<String> currentBranches = projects.stream()
                                               .map(project -> _gitService.getCurrentBranchName(project))
                                               .distinct()
                                               .collect(Collectors.toList());
        return currentBranches.size() > 1 ? StringUtils.EMPTY : currentBranches.get(0);
    }

    private void onCreateButton(ActionEvent event) {
        String newBranchName = _branchNameField.getText().trim();
        String startPoint = _comboBox.getSelectionModel().getSelectedItem();
        Map<Project, JGitStatus> results = _gitService.createBranch(getProjects(), newBranchName, startPoint, false);
        createAndShowStatusDialog(getProjects(), results);
        boolean isCheckoutBranch = _checkoutBox.isSelected();
        if (isCheckoutBranch) {
            checkoutBranch(getProjects(), newBranchName);
        }
        getStage().close();
    }

    private boolean isInputValid(String input) {
        return _branchValidator.validateBranchName(input);
    }

    private void initializeOnCloseEvent() {
        Window window = getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> {
            getStage().close();
        });
    }

    private ChangeListener<? super String> getInputFilter() {
        return (observable, oldValue, newValue) -> {
            if (!_branchNameField.getText().isEmpty()
                    && isInputValid(_branchNameField.getText())
                    && !_comboBox.getSelectionModel().getSelectedItem().isEmpty()) {
                _createButton.setDisable(false);
                _branchNameField.setStyle("-fx-border-color: green;");
            } else if (_comboBox.getSelectionModel().getSelectedItem().isEmpty()){
                _comboBox.setStyle("-fx-border-color: red;");
                _createButton.setDisable(true);
            } else {
                _branchNameField.setStyle("-fx-border-color: red;");
                _createButton.setDisable(true);
            }
        };
    }

    private ChangeListener<? super String> getBoxItemsFilter() {
        return (observableValue, oldValue, currentValue) -> {
            if (currentValue.isEmpty()) {
                _createButton.setDisable(true);
                _comboBox.setStyle("-fx-border-color: red;");
            } else {
                _comboBox.setStyle("-fx-border-color: lightgray;");
            }
        };
    }

    private ChangeListener<? super Boolean> getPushListener() {
        return (observableValue, oldValue, currentValue) -> {
            if (currentValue.booleanValue()) {
                _checkoutBox.setSelected(true);
            }
        };
    }

    private ChangeListener<? super Boolean> getCheckoutListener() {
        return (observableValue, oldValue, currentValue) -> {
            if (!currentValue.booleanValue()) {
                _pushToUpstreamBox.setSelected(false);
            }
        };
    }

    private void checkoutBranch(List<Project> projects, Object branchName) {
        List<Project> changedProjects = _gitService.getProjectsWithChanges(getProjects());

        if (changedProjects.isEmpty()) {
            // we do not show checkout statuses here
            // because we show the statuses of branches creation
            // In the same time we could see that branch is changed on the projects list panel
            Map<Project, JGitStatus> result = _gitService.checkoutBranch(projects, (String) branchName, false, null);
            changedProjects = result.entrySet().stream()
                                               .filter(entry -> entry.getValue() == JGitStatus.SUCCESSFUL)
                                               .map(Map.Entry::getKey)
                                               .collect(Collectors.toList());
            ProjectList.get(null).updateProjectStatuses(changedProjects);
            boolean pushToUpstream = _pushToUpstreamBox.isSelected();
            if (pushToUpstream) {
                pushBranches(getProjects());
            }
        } else {
            ChangesCheckDialog alert = new ChangesCheckDialog();
            alert.launchConfirmationDialog(changedProjects, projects, branchName, this::checkoutBranch);
        }
    }

    private void createAndShowStatusDialog(List<Project> projects, Map<Project, JGitStatus> results) {
        String collapsedMessage = "New branch has been created in %s selected projects";

        StatusDialog statusDialog = new StatusDialog(STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER);
        statusDialog.showMessage(results, projects.size(), collapsedMessage);
        statusDialog.show();
    }

    private void pushBranches(List<Project> projects) {
        this.close();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ProgressDialog progressDialog = new PushProgressDialog();
                ProgressListener progress = new OperationProgressListener(progressDialog, ApplicationState.PUSH);
                progressDialog.setStartAction(() -> pushAction(projects, progress));
                progressDialog.showDialog();
            }
        });
    }

    private void pushAction(List<Project> projects, ProgressListener progress) {
        _gitService.push(projects, progress);
    }

}
