package com.lgc.gitlabtool.git.ui.table;

import java.text.SimpleDateFormat;

 /**
  * This class need for represent Date column with suitable date/format.
  *
  * Subclassing the Date class in order to override the toString() method.
  * There is a caveat here though: the TableView uses java.sql.Date instead of java.util.Date;
  * so need to subclass the former.
  *
 * Created by Oleksandr Kozlov on 24.02.2018.
 */
public class CustomDate extends java.sql.Date {

    public CustomDate(long date) {
        super(date);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(this);
    }
}
