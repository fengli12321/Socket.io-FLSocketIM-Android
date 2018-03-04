package com.foxpower.flchatofandroid.model;

import com.foxpower.flchatofandroid.enums.ChatType;
import com.foxpower.flchatofandroid.enums.MessageSendStatus;

import io.realm.annotations.RealmClass;

/**
 * Created by fengli on 2018/2/8.
 */


public class MessageModel extends BaseModel {

    private String msg_id;
    private long timestamp;
    private long sendTime;
    private String from_user;
    private String to_user;
    private ChatType chat_type;

    private MessageBody bodies;

    private MessageSendStatus sendStatus;

    public String getMsg_id() {
        return msg_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFrom_user() {
        return from_user;
    }

    public String getTo_user() {
        return to_user;
    }

    public ChatType getChat_type() {
        return chat_type;
    }

    public MessageBody getBodies() {
        return bodies;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public void setChat_type(ChatType chat_type) {
        this.chat_type = chat_type;
    }

    public void setBodies(MessageBody bodies) {
        this.bodies = bodies;
    }

    public void setSendStatus(MessageSendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public MessageSendStatus getSendStatus() {

        return sendStatus;
    }

    public String getMessageTip() {

        String tip = "调试中";
        switch (bodies.getType()){
            case txt:
                tip = bodies.getMsg();
                break;
            case img:
                tip = "[图片]";
                break;
            case audio:
                tip = "[语音]";
                break;
            case loc:
                tip = "[位置]";
                break;
        }
        return tip;
    }
}
