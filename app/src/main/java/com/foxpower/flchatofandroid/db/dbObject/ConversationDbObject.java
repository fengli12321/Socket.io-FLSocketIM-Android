package com.foxpower.flchatofandroid.db.dbObject;

import com.foxpower.flchatofandroid.model.MessageModel;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by fengli on 2018/2/8.
 */

public class ConversationDbObject extends RealmObject{

    @Required
    private String id;
    private String ext;
    private int unreadcount;
    private String latestmsgtext;
    private long latestmsgtimestamp;

    public String getId() {
        return id;
    }

    public String getExt() {
        return ext;
    }

    public int getUnreadcount() {
        return unreadcount;
    }

    public String getLatestmsgtext() {
        return latestmsgtext;
    }

    public long getLatestmsgtimestamp() {
        return latestmsgtimestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public void setUnreadcount(int unreadcount) {
        this.unreadcount = unreadcount;
    }

    public void setLatestmsgtext(String latestmsgtext) {
        this.latestmsgtext = latestmsgtext;
    }

    public void setLatestmsgtimestamp(long latestmsgtimestamp) {
        this.latestmsgtimestamp = latestmsgtimestamp;
    }



}
