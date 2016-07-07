package com.vaishali.admin.notetakingapp;

/**
 * Created by Admin on 7/5/2016.
 */
public class Notes
{
    private String title;
    private String content;
    private String email;
    public Notes() {
    }

    public Notes(String title, String content, String email) {
        this.title = title;
        this.content = content;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
