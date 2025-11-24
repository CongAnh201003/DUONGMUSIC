package com.example.baicuoiky_nhom13.Model;

public class Message {
    private String content;
    private boolean isUser; // true = user gửi, false = AI gửi

    public Message(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    public String getContent() { return content; }
    public boolean isUser() { return isUser; }
}
