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
    public Message() {}

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }
}
