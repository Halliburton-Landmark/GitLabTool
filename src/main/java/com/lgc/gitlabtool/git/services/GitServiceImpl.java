package com.lgc.gitlabtool.git.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Status;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;

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

        return switchTo(projects, selectedBranchName, isRemote);
    }

    @Override
    public Map<Project, JGitStatus> switchTo(List<Project> projects, String branchName, boolean isRemote) {
        Map<Project, JGitStatus> switchStatuses = new HashMap<>();
        for (Project project : projects) {
            JGitStatus status = JGit.getInstance().switchTo(project, branchName, isRemote);
            switchStatuses.put(project, status);
        }
        return switchStatuses;
    }

    @Override
    public List<Project> getProjectsWithChanges(List<Project> projects) {
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
    public Map<Project, JGitStatus> discardChanges(List<Project> projects) {
        if (projects == null) {
            throw new IllegalArgumentException("Wrong parameters for discarding projects.");
        }

        Map<Project, JGitStatus> results = new LinkedHashMap<>();
        for (Project project : projects) {
            if(project == null){
                results.put(project, JGitStatus.FAILED);
                continue;
            }
            JGitStatus status = JGit.getInstance().discardChanges(project);
            results.put(project, status);
        }

        return results;
    }

    @Override
    public Map<Project, JGitStatus> commitChanges(List<Project> projects, String commitMessage, boolean isPushImmediately,
                    ProgressListener progressListener) {
        Map<Project, JGitStatus> results;
        if (isPushImmediately) {
            //use null for getting default user-info
            results = JGit.getInstance().commitAndPush(projects, commitMessage, true, null,
                    null, null, null, progressListener);
        } else {
            //use null for getting default user-info
            results = JGit.getInstance().commit(projects, commitMessage, true, null,
                    null, null, null, progressListener);
        }

        return results;
    }

    @Override
    public Map<Project, JGitStatus> createBranch(List<Project> projects, String branchName, boolean force) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        List<Project> localProjects =
                projects.stream()
                .filter(prj -> prj.isCloned())
                .collect(Collectors.toList());
        for (Project project : localProjects) {
            statuses.put(project, JGit.getInstance().createBranch(project, branchName, force));
        }
        return statuses;
    }

}
