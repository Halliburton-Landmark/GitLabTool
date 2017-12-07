package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.jgit.ChangedFile;

/**
 * Comparator for ListView<ChangedFile> which sorting item by project names in alphabetical order with ignore case.
 *
 * @author Lyudmila Lyska
 */
public class ProjectsTypeComparator implements Comparator<ChangedFile> {

    @Override
    public int compare(ChangedFile o1, ChangedFile o2) {
        String projectName = o1.getProject().getName();
        return projectName.compareToIgnoreCase(o2.getProject().getName());
    }
}
