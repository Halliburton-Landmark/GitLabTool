package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.jgit.ChangedFile;

public class ComparatorExtensionsType implements Comparator<ChangedFile> {

    @Override
    public int compare(ChangedFile o1, ChangedFile o2) {
        return o1.getFileExtension().compareTo(o2.getFileExtension());
    }
}
