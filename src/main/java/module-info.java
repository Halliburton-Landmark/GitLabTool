module gitlabtool {
    opens com.lgc.gitlabtool.git.ui.javafx to javafx.graphics;
    opens com.lgc.gitlabtool.git.xml to java.xml.bind;

    requires java.xml.bind;
    requires javafx.controls;
    requires log4j.api;
    requires javafx.fxml;
    requires org.eclipse.jgit;
    requires httpcore;
    requires gson;
    requires commons.io;
    requires vtd.xml;
    requires java.desktop;
    requires java.prefs;
    requires org.apache.commons.lang3;

}