package com.foxpower.flchatofandroid.db.dbObject;

import com.foxpower.flchatofandroid.enums.MessageSendStatus;

import io.realm.RealmObject;

/**
 * Created by fengli on 2018/2/8.
 */

public class MessageDbObject extends RealmObject {

    private String id;
    private long localtime;
    private long timestamp;
    private String conversation;
    private boolean receiver;
    private String chatType;
    private String bodies;
    private int sendStatus;

    public void setId(String id) {
        this.id = id;
    }

    public void setLocaltime(long localtime) {
        this.localtime = localtime;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public void setBodies(String bodies) {
        this.bodies = bodies;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getId() {

        return id;
    }

    public long getLocaltime() {
        return localtime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getConversation() {
        return conversation;
    }

    public boolean isReceiver() {
        return receiver;
    }

    public String getChatType() {
        return chatType;
    }

    public String getBodies() {
        return bodies;
    }

    public int getSendStatus () {
        return sendStatus;
    }
}
