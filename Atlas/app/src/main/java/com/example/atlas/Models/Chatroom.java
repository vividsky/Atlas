package com.example.atlas.Models;

public class Chatroom {

    private String name;
    private String chatId;

    @Override
    public String toString() {
        return "Chatroom{" +
                "name='" + name + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }

    public Chatroom(String name, String chatId) {
        this.name = name;
        this.chatId = chatId;

    }
    public Chatroom() {}

    public String getName() {
        return name;
    }

    public String getChatId() {
        return chatId;
    }
}
