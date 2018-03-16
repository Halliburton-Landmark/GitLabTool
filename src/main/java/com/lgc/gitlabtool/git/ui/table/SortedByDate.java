package com.lgc.gitlabtool.git.ui.table;

import java.util.Comparator;

/**
 * This class represent custom comparator that sorting by date descendingly.
 *
 * Created by Oleksandr Kozlov on 24.02.2018.
 */
public class SortedByDate implements Comparator<Commit> {

    @Override
    public int compare(Commit obj1, Commit obj2) {
        if (obj1.getDate() == null || obj2.getDate() == null)
            return 0;
        long thisTime = obj1.getDate().getTime();
        long anotherTime = obj2.getDate().getTime();
        return (thisTime<anotherTime ? 1 : (thisTime==anotherTime ? 0 : -1));
    }
}
