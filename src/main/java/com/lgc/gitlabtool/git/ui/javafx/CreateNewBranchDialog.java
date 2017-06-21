package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CreateNewBranchDialog extends Dialog<String> {

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private final Label textLabel;
    private final TextField branchNameField;
    private final CheckBox checkoutBox;
    private final Button createButton;
    private final Button cancelButton;
    
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }
    
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public CreateNewBranchDialog() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        textLabel = new Label("New branch: ");
        grid.add(textLabel, 0, 1);
        branchNameField = new TextField();
        grid.add(branchNameField, 1, 1, 2, 1);
        checkoutBox = new CheckBox("Checkout new branch");
        grid.add(checkoutBox, 1, 3);

        createButton = new Button("Create Branch");
        createButton.setOnAction(this::onCreateButton);
        
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> {
            getStage().close();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(createButton, cancelButton);
        grid.add(hbBtn, 2, 5);

        getDialogPane().setContent(grid);
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = getStage();
        stage.setResizable(false);
        stage.setTitle("Create new branch");
        stage.getIcons().add(appIcon);
    }

    private Stage getStage() {
        return (Stage) getDialogPane().getScene().getWindow();
    }
    
    private void onCreateButton(ActionEvent event) {
        String newBranchName = branchNameField.getText();
        Map<Project, JGitStatus> results = 
                _gitService.createBranch(getProjects(), newBranchName, false);
        // TODO: show results somehow 
        
        boolean switchToBranch = checkoutBox.isSelected();
        if (switchToBranch) {
            _gitService.switchTo(getProjects(), getCreatedBranch(newBranchName, getProjects()));
        }
        
        getStage().close(); //TODO: modify project list in main window here!
    }
    
    private Branch getCreatedBranch(String name, List<Project> projects) {
        Set<Branch> allBranchesWithTypes = JGit.getInstance().getBranches(projects,
                BranchType.LOCAL, true);
        
        return allBranchesWithTypes.stream()
                .filter(branch -> branch.getBranchName().equals(name))
                .findFirst().get();
    }
}
