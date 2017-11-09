package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.jgit.ChangedFile;

public class ComparatorProjectsType implements Comparator<ChangedFile> {

    @Override
    public int compare(ChangedFile o1, ChangedFile o2) {
        return o1.getProject().getName().compareTo(o2.getProject().getName());
    }
}
