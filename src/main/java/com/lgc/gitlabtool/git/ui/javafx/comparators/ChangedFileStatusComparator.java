package com.lgc.gitlabtool.git.ui.javafx.comparators;

import java.util.Comparator;

import com.lgc.gitlabtool.git.jgit.ChangedFile;

/**
 * Comparator for ListView<ChangedFile> which sorting item by file status.
 *
 * @author Lyudmila Lyska
 */
public class ChangedFileStatusComparator implements Comparator<ChangedFile> {

    @Override
    public int compare(ChangedFile o1, ChangedFile o2) {
        return o1.getStatusFile().compareTo(o2.getStatusFile());
    }
}
