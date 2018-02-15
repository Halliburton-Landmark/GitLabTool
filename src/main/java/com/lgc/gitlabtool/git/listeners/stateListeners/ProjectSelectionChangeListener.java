package com.lgc.gitlabtool.git.listeners.stateListeners;

import com.lgc.gitlabtool.git.entities.Project;

/**
 * Created by Oleksandr Kozlov on 2/15/2018.
 */
public interface ProjectSelectionChangeListener {

    public void onChanged(Project project);
}
