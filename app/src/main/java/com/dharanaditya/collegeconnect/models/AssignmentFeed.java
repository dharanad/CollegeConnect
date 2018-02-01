/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by dharanaditya on 30/01/18.
 */

@Parcel
@IgnoreExtraProperties
public class AssignmentFeed {
    @Exclude
    public static final String DATABASE_PATH = "feed/assignment";
    private @ServerTimestamp
    Date timestamp;
    private String title;
    private String message;
    private String author;
    private String uid;
    private @Exclude
    String documentId;

    private String branch;
    private String year;
    private String section;
    private String subject;
    private Date dueDate;

    public AssignmentFeed() {
    }

    public AssignmentFeed(String title, String message, String author, String uid, String branch, String year, String section, String subject, Date dueDate) {
        this.title = title;
        this.message = message;
        this.author = author;
        this.uid = uid;
        this.branch = branch;
        this.year = year;
        this.section = section;
        this.subject = subject;
        this.dueDate = dueDate;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "AssignmentFeed{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", uid='" + uid + '\'' +
                ", branch='" + branch + '\'' +
                ", year='" + year + '\'' +
                ", section='" + section + '\'' +
                ", subject='" + subject + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}
