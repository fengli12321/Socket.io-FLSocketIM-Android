package com.foxpower.flchatofandroid.model;

import com.foxpower.flchatofandroid.enums.MessageType;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by fengli on 2018/2/8.
 */

public class MessageBody extends BaseModel {

    /*公共*/
    private MessageType type;

    /*文本*/
    private String msg;
    /*图片*/
    private HashMap<String, Integer> size;
    private String thumbnailRemotePath;
    private String originImagePath;

    /*语音*/
    private long duration;

    /*位置*/
    private double latitude;
    private double longitude;
    private String locationName;
    private String detailLocationName;


    /*文件*/
    private String fileName;
    private String fileRemotePath;





    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setDetailLocationName(String detailLocationName) {
        this.detailLocationName = detailLocationName;
    }

    public double getLatitude() {

        return latitude;
    }
    public void setType(MessageType type) {
        this.type = type;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSize(HashMap size) {
        this.size = size;
    }

    public void setThumbnailRemotePath(String thumbnailRemotePath) {
        this.thumbnailRemotePath = thumbnailRemotePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileRemotePath(String fileRemotePath) {
        this.fileRemotePath = fileRemotePath;
    }

    public void setOriginImagePath(String originImagePath) {
        this.originImagePath = originImagePath;
    }

    public MessageType getType() {

        return type;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMsg() {
        return msg;
    }

    public HashMap<String, Integer> getSize() {
        return size;
    }

    public String getThumbnailRemotePath() {
        return thumbnailRemotePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileRemotePath() {
        return fileRemotePath;
    }

    public String getOriginImagePath() {
        return originImagePath;
    }


    public long getDuration() {

        return duration;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDetailLocationName() {
        return detailLocationName;
    }

}
