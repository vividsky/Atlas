package com.example.atlas.Models;

public class Message {
    String sentBy;
    String text;
    long sentAt;

    public Message(String sentBy, long sentAt, String text) {
        this.sentAt = sentAt;
        this.sentBy = sentBy;
        this.text = text;
    }

    public Message() {
    }

    public String getSentBy() {
        return sentBy;
    }

    public long getSentAt() {
        return sentAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
