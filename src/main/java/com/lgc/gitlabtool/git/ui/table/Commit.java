package com.lgc.gitlabtool.git.ui.table;

import java.util.Date;

/**
 * This pojo class keeps data about commit.
 *
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class Commit {

    private String hash;
    private String message;
    private String author;
    private Date authoredDate;
    private String committer;
    private Date date;
    private String project;

    public Commit() {}

    public Commit(String hash, String message, String author, Date authoredDate, String committer, Date date, String project) {
        this.hash = hash;
        this.message = message;
        this.author = author;
        this.authoredDate = authoredDate;
        this.committer = committer;
        this.date = date;
        this.project = project;
    }

    /**
     * Gets the hash of the commit
     *
     * @return hash of the commit
     */
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Gets the message of the commit
     *
     * @return the message of the commit
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the author of the commit
     *
     * @return the author of the commit
     */
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the authored date of the commit
     *
     * @return the authored date of the commit
     */
    public CustomDate getAuthoredDate() {
        return new CustomDate(authoredDate.getTime());
    }

    public void setAuthoredDate(Date authoredDate) {
        this.authoredDate = authoredDate;
    }

    /**
     * Gets the committer of the commit
     *
     * @return the committer of the commit
     */
    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    /**
     * Gets the date of the commit
     *
     * @return the date of the commit
     */
    public CustomDate getDate() {
        return new CustomDate(date.getTime());
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the project
     *
     * @return the project
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

}
