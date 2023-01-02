package com.nitish.gamershub.Pojo.FireBase;

public class FcmNotification {

    String imageLink;
    String type;
    String title;
    String message ;
    Object extra;

    public FcmNotification(String imageLink, String type, String title, String message, Object extra) {
        this.imageLink = imageLink;
        this.type = type;
        this.title = title;
        this.message = message;
        this.extra = extra;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
