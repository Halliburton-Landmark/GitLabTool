package com.lgc.gitlabtool.git.ui.table;

/**
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class Commit {

    private String id;
    private String message;
    private String author;
    private String authoredDate;
    private String committer;
    private String committedDate;

    public Commit() {}

    public Commit(String id, String message, String author, String authoredDate, String committer, String committedDate) {
        this.id = id;
        this.message = message;
        this.author = author;
        this.authoredDate = authoredDate;
        this.committer = committer;
        this.committedDate = committedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCommittedDate() {
        return committedDate;
    }

    public void setCommittedDate(String committedDate) {
        this.committedDate = committedDate;
    }

}
