package com.example.atlas.Models;

public class Chatroom {

    private String name;
    private String chatId;
    private String userId;

    public Chatroom(String name, String chatId, String userId) {
        this.name = name;
        this.chatId = chatId;
        this.userId = userId;

    }

    public Chatroom() {
    }

    public String getName() {
        return name;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "name='" + name + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
