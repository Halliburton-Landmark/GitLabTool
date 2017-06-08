package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.jgit.BranchType;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.jgit.JGitStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jgit.api.Status;

public class GitServiceImpl implements GitService {

    @Override
    public boolean containsBranches(Project project, List<Branch> branches, boolean isCommon) {
        if (project == null || branches == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branches.");
        }
        List<Branch> projectBranches = JGit.getInstance().getBranches(project, BranchType.ALL);

        boolean isContains;
        if (isCommon) {
            isContains = projectBranches.containsAll(branches);
        } else {
            isContains = !Collections.disjoint(projectBranches, branches);
        }

        return isContains;
    }

    @Override
    public Map<Project, JGitStatus> switchTo(List<Project> projects, Branch branch) {
        String selectedBranchName = branch.getBranchName();
        boolean isRemote = branch.getBranchType().equals(BranchType.REMOTE);

        Map<Project, JGitStatus> switchStatuses = new HashMap<>();
        for (Project project : projects) {
            JGitStatus status = JGit.getInstance().switchTo(project, selectedBranchName, isRemote);
            switchStatuses.put(project, status);
        }
        return switchStatuses;
    }

    @Override
    public List<Project> getChangedProjects(List<Project> projects) {
        if (projects == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branches.");
        }

        List<Project> changedProjects = new ArrayList<>();

        for (Project project : projects) {
            Optional<Status> status = JGit.getInstance().getStatusProject(project);
            if (status.isPresent() && status.get().hasUncommittedChanges()) {
                changedProjects.add(project);
            }
        }

        return changedProjects;
    }

    @Override
    public void commitChanges(List<Project> projects, String commitMessage, boolean isPushImmediately) {
        if (isPushImmediately) {
            JGit.getInstance().commitAndPush(projects, commitMessage, true, null, null, null, null,
                    new SuccessfulOperationHandler(), new UnsuccessfulOperationHandler());
        } else {
            JGit.getInstance().commit(projects, commitMessage, true, null, null, null, null,
                    new SuccessfulOperationHandler(), new UnsuccessfulOperationHandler());
        }
    }

    /**
     * Handler for successful operation
     *
     * @author Pavlo Pidhorniy
     */
    class SuccessfulOperationHandler implements Consumer<Integer> {

        @Override
        public void accept(Integer percentage) {
            System.out.println("Progress: " + percentage + "%");

        }
    }

    /**
     * Handler for unsuccessful operation
     *
     * @author Pavlo Pidhorniy
     */
    class UnsuccessfulOperationHandler implements BiConsumer<Integer, String> {

        @Override
        public void accept(Integer percentage, String message) {
            // TODO: in log or UI console
            System.err.println("!ERROR: " + message);
            System.out.println("Progress: " + percentage + "%");
        }

    }
}
