package com.foxpower.flchatofandroid.util.manager;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.foxpower.flchatofandroid.app.App;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.enums.MessageEventType;
import com.foxpower.flchatofandroid.enums.SocketConnectStatus;
import com.foxpower.flchatofandroid.model.MessageEvent;
import com.foxpower.flchatofandroid.model.MessageModel;
import com.foxpower.flchatofandroid.ui.activity.ChatActivity;
import com.foxpower.flchatofandroid.ui.activity.VideoChatActivity;
import com.foxpower.flchatofandroid.util.constant.UrlConstant;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by fengli on 2018/2/6.
 */

public class SocketManager {


    public static Socket socket;

    public static SocketConnectStatus connectStatus = SocketConnectStatus.SocketDisconnected;

    private static SocketCallBack callBack;
    public static void connect(String token, final SocketCallBack callBack){

        SocketManager.callBack = callBack;
        if (socket == null) {
            initSocket(token);
        }
        else {
            socket = null;
            initSocket(token);
        }

        socket.once(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                callBack.success();
            }
        });
        socket.connect();


        addHandles();
    }


    private static void initSocket(String token) {
        IO.Options opts = new IO.Options();
        opts.forceNew = false;
        opts.reconnection = true;
        opts.reconnectionDelay = 2000;      //延迟
        opts.reconnectionDelayMax = 6000;
        opts.reconnectionAttempts = -1;
        opts.timeout = 6000;
        opts.query = "auth_token=" + token;
        try {
            socket = IO.socket(UrlConstant.baseUrl, opts);
        } catch (Exception e) {
        }
    }

    public static abstract class SocketCallBack {

        public abstract void success();
        public abstract void fail();
    }

    private static void addHandles(){
        /*
        * 断开连接
        * */
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                FLLog.i("连接断开");
                connectStatus = SocketConnectStatus.SocketDisconnected;
                EventBus.getDefault().post(new MessageEvent(MessageEventType.EventConnectStatus, SocketConnectStatus.SocketDisconnected));
            }
        });

        socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                FLLog.i("连接中");
                connectStatus = SocketConnectStatus.SocketConnecting;
                EventBus.getDefault().post(new MessageEvent(MessageEventType.EventConnectStatus, SocketConnectStatus.SocketConnecting));
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                FLLog.i("连接失败");
                connectStatus = SocketConnectStatus.SocketConnectError;
                EventBus.getDefault().post(new MessageEvent(MessageEventType.EventConnectStatus, SocketConnectStatus.SocketConnectError));
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                FLLog.i("连接成功");
                connectStatus = SocketConnectStatus.SocketConnected;
                EventBus.getDefault().post(new MessageEvent(MessageEventType.EventConnectStatus, SocketConnectStatus.SocketConnected));
            }
        });

        // 收到信消息
        socket.on("chat", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                FLLog.i("收到消息");
                Ack ack = (Ack) args[1];
                ack.call("我已收到消息");
                JSONObject msg = (JSONObject) args[0];


                byte[] file = null;
                try {
                    JSONObject msgBody = msg.getJSONObject("bodies");

                    file = (byte[]) msgBody.get("fileData");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final MessageModel message = JSON.parseObject(msg.toString(), MessageModel.class);

                if (file != null) {

                    String fileName = message.getBodies().getFileName();
                    String savePath = "";
                    switch (message.getBodies().getType()) {
                        case img:
                            savePath = FLUtil.imageSavePath() + fileName;
                            break;
                        case audio:
                            savePath = FLUtil.audioSavePath() + fileName;
                            break;
                    }
                    File imageFile = new File(savePath);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(imageFile);
                        outputStream.write(file);
                        outputStream.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                // 消息保存数据库
                DbManager.insertMessage(message);

                // 更新会话
                DbManager.insertOrUpdateConversation(message);

                // 发送事件
                MessageEvent event = new MessageEvent(MessageEventType.EventMessage, message);
                EventBus.getDefault().post(event);
            }
        });

        // 视频通话请求
        socket.on("videoChat", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];

                try {
                    String fromUser = dataObject.getString("from_user");
                    String room = dataObject.getString("room");

                    Bundle bundle = new Bundle();
                    bundle.putString("fromUser", fromUser);
                    bundle.putString("toUser", ClientManager.currentUserId);
                    bundle.putString("room", room);
                    bundle.putInt("type", 1);

                    BaseActivity topActivity = (BaseActivity) ActivityUtils.getTopActivity();

                        topActivity.openActivity(VideoChatActivity.class, bundle);
                    if (topActivity != null) {

                    } else {

                        FLLog.i("获取栈顶activity失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // 用户上线
        socket.on("onLine", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject msg = (JSONObject) args[0];
                try {
                    MessageEvent event = new MessageEvent(MessageEventType.EventUserOnline, msg.getString("user"));
                    EventBus.getDefault().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // 用户下线
        socket.on("offLine", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject msg = (JSONObject) args[0];
                try {
                    MessageEvent event = new MessageEvent(MessageEventType.EventUserOffLine, msg.getString("user"));
                    EventBus.getDefault().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // 连接状态改变
        socket.on("statusChange", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                FLLog.i("连接状态确实改变了......");
            }
        });
    }


}
