package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.entities.Project;

/**
 * Comparator for ListView<Project> in Projects window.
 * It sorts projects of list by 'isCloned' parameter, because shadow projects should be at the end of list.
 *
 * @author Lyudmila Lyska
 */
public class ProjectListComparator implements Comparator<Project> {

    @Override
    public int compare(Project o1, Project o2) {
        return Boolean.compare(o2.isCloned(), o1.isCloned());
    }
}
