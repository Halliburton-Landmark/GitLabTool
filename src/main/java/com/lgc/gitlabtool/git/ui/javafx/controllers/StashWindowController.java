package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lgc.gitlabtool.git.entities.ProjectList;

public class StashWindowController {


    private final ProjectList _projectList = ProjectList.get(null);
    private final List<Integer> _projectsIds = new ArrayList<>();

    public void beforeShowing(List<Integer> projectsIds) {
        _projectsIds.addAll(projectsIds);
    }
}
