package com.example.schan.model;

import java.util.Date;

/**
 * Created by schan on 12/15/16.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private int messageType;

    public ChatMessage(String messageText, String messageUser, int messageType) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = new Date().getTime();
        this.messageType = messageType;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;

    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;

    }
}