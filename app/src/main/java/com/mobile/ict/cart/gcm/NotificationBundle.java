package com.mobile.ict.cart.gcm;

/**
 * Created by vish on 13/5/16.
 */
public class NotificationBundle {

    private String mText;
    private String mTitle;
    private int userId;

    NotificationBundle(String mText, String mTitle) {
        this.mText = mText;
        this.mTitle = mTitle;
    }
    NotificationBundle(String mText, String mTitle, int userId) {
        this.mText = mText;
        this.mTitle = mTitle;
        this.userId = userId;

    }
    NotificationBundle() {

    }


    public String getText() {
        return this.mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String mTitle){
        this.mTitle = mTitle;
    }

    public int getId() {
        return this.userId;
    }

    public void setId(int userId) {
        this.userId = userId;
    }

}