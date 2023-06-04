package com.rizal.antrianprinting.models.notification;

public class NotificationModel {
    String title;
    String message;
    String image;
    String action;
    String action_destination;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction_destination() {
        return action_destination;
    }

    public void setAction_destination(String action_destination) {
        this.action_destination = action_destination;
    }

}
