package com.example.cometimplementation.models;

public class ChatMessageModel {
    private String message;
    private String uid;

    public ChatMessageModel(String message, String uid) {
        this.message = message;
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
