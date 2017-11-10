package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.jgit.ChangedFile;

/**
 * Comparator for ListView<ChangedFile> which sorting item by file extensions in alphabetical order with ignore case.
 *
 * @author Lyudmila Lyska
 */
public class ComparatorExtensionsType implements Comparator<ChangedFile> {

    @Override
    public int compare(ChangedFile o1, ChangedFile o2) {
        return o1.getFileExtension().compareToIgnoreCase(o2.getFileExtension());
    }
}
