package com.lgc.gitlabtool.git.ui.table;

/**
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class Commit {

    private String hash;
    private String message;
    private String author;
    private String authoredDate;
    private String committer;
    private String date;
    private String project;

    public Commit() {}

    public Commit(String hash, String message, String author, String authoredDate, String committer, String date, String project) {
        this.hash = hash;
        this.message = message;
        this.author = author;
        this.authoredDate = authoredDate;
        this.committer = committer;
        this.date = date;
        this.project = project;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthoredDate() {
        return authoredDate;
    }

    public void setAuthoredDate(String authoredDate) {
        this.authoredDate = authoredDate;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

}
