package com.foxpower.flchatofandroid.util.manager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.TimeUtils;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.enums.ChatType;
import com.foxpower.flchatofandroid.enums.MessageSendStatus;
import com.foxpower.flchatofandroid.enums.MessageType;
import com.foxpower.flchatofandroid.model.MessageBody;
import com.foxpower.flchatofandroid.model.MessageModel;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.util.HashMap;

import io.socket.client.Ack;

/**
 * Created by fengli on 2018/2/21.
 */

public class ChatManager {

    public static MessageModel sendTextMsg(String text, String toUser, SendStatusCallBack callBack) {
        MessageBody body = new MessageBody();
        body.setType(MessageType.txt);
        body.setMsg(text);
        MessageModel messageModel = new MessageModel();
        messageModel.setBodies(body);

        return sendMsg(messageModel, toUser, callBack);

    }

    public static MessageModel sendImageMsg(String imagePath, String imageName, HashMap size, String toUser, SendStatusCallBack callBack) {

        MessageBody body = new MessageBody();
        body.setType(MessageType.img);

        boolean saveImage = imageName == null;
        imageName = imageName == null ? FLUtil.createUUID() + ".jpg":imageName;
        body.setFileName(imageName);
        body.setSize(size);
        body.setOriginImagePath(imagePath);
        MessageModel messageModel = new MessageModel();
        messageModel.setBodies(body);

        return sendMsg(messageModel, saveImage, toUser, callBack);
    }

    public static MessageModel sendAudioMsg(String audioName, long duration, String toUser, SendStatusCallBack callBack) {

        MessageBody body = new MessageBody();
        body.setType(MessageType.audio);
        body.setFileName(audioName);
        body.setDuration(duration);
        MessageModel messageModel = new MessageModel();
        messageModel.setBodies(body);

        return sendMsg(messageModel, toUser, callBack);
    }

    public static MessageModel sendLocationMsg(double lat, double lon, String location, String detail, String toUser, SendStatusCallBack callBack) {

        MessageBody body = new MessageBody();
        body.setType(MessageType.loc);
        body.setLatitude(lat);
        body.setLongitude(lon);
        body.setLocationName(location);
        body.setDetailLocationName(detail);

        MessageModel messageModel = new MessageModel();
        messageModel.setBodies(body);

        return sendMsg(messageModel, toUser, callBack);
    }

    private static MessageModel sendMsg(final MessageModel messageModel, String toUser, final SendStatusCallBack callBack) {

        return sendMsg(messageModel, true, toUser, callBack);
    }
    private static MessageModel sendMsg(final MessageModel messageModel, boolean saveImage, String toUser, final SendStatusCallBack callBack) {


        messageModel.setSendStatus(MessageSendStatus.MessageSending);
        messageModel.setFrom_user(ClientManager.currentUserId);
        messageModel.setTo_user(toUser);
        messageModel.setChat_type(ChatType.chat);


        long currentTime = TimeUtils.getNowMills();
        messageModel.setTimestamp(currentTime);
        messageModel.setSendTime(currentTime);


        saveMessageAndConversationToDb(messageModel);
        String msgStr = JSON.toJSONString(messageModel);
        JSONObject message = null;
        try {
            message = new JSONObject(msgStr);

            String fileName = messageModel.getBodies().getFileName();
            if (fileName != null) { // 发送文件
                String filePath = "";
                switch (messageModel.getBodies().getType()) {
                    case img:
                        filePath = saveImage?messageModel.getBodies().getOriginImagePath():FLUtil.imageSavePath() + fileName;
                        break;
                    case audio:
                        filePath = FLUtil.audioSavePath() + fileName;
                        break;
                }

                FileInputStream inputStream = new FileInputStream(filePath);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);


                // 图片单独保存到本地
                {
                    if (messageModel.getBodies().getType() == MessageType.img && saveImage == true) {
                        String savePath = FLUtil.imageSavePath() + messageModel.getBodies().getFileName();

                        File file = new File(savePath);
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(buffer);
                        outputStream.close();
                    }

                }

                JSONObject object = message.getJSONObject("bodies");
                object.put("fileData", buffer);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (message != null) {

            SocketManager.socket.emit("chat", message, new Ack() {
                @Override
                public void call(Object... args) {

                    JSONObject successMsg = (JSONObject) args[0];
                    messageModel.setSendStatus(MessageSendStatus.MessageSendSuccess);
                    try {
                        messageModel.setMsg_id(successMsg.getString("msg_id"));
                        messageModel.setTimestamp(successMsg.getLong("timestamp"));


                        // 发送定位成功，拿到截图地址
                        {
                            if (messageModel.getBodies().getType() == MessageType.loc) {

                                String path = successMsg.getJSONObject("bodies").getString("fileRemotePath");
                                messageModel.getBodies().setFileRemotePath(path);
                            }
                        }

                        // 消息发送成功后更新数据库
                        DbManager.updateMessage(messageModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    callBack.sendStatus(messageModel);
                }
            });
        }

        return messageModel;
    }

    private static void saveMessageAndConversationToDb(MessageModel messageModel) {

        // 保存消息
        DbManager.insertMessage(messageModel);

        // 保存会话
        DbManager.insertOrUpdateConversation(messageModel);
    }


    public interface SendStatusCallBack{

        public void sendStatus(MessageModel messageModel);
    }
}
