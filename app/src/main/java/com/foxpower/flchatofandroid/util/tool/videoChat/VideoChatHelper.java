package com.foxpower.flchatofandroid.util.tool.videoChat;

import android.content.Context;

import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.manager.SocketManager;
import com.foxpower.flchatofandroid.util.other.FLLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.microedition.khronos.egl.EGLContext;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by fengli on 2018/3/2.
 */

public class VideoChatHelper{


    // 房间ID
    private String room;
    // 回调
    private VideoChatCallBack callBack;
    // 所有的连接ID
//    private JSONArray connectionIdArray;
    private HashMap<String, Peer> peers = new HashMap<>();
    // 所有连接的映射
//    private HashMap<String, PeerConnection> connectionsMap = new HashMap<>();
    // 自己的ID
    private String myId;
    // context
    private Context mContext;
    // factory
    private PeerConnectionFactory factory;


    // 本地音视频流
    private MediaStream localMediaStream;

    private EGLContext mEGLContext;

    private LinkedList<PeerConnection.IceServer> ICEServers;

    public VideoChatHelper(Context mContext, EGLContext mEGLContext, VideoChatCallBack callBack) {
        this.mContext = mContext;
        this.callBack = callBack;
        this.mEGLContext = mEGLContext;
    }

    public void connectRoom(String room) {

        this.room = room;
        addSocketHandles();
        joinRoom(room);
    }


    public void addSocketHandles() {

        final Socket socket = SocketManager.socket;

        socket.on("_peers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];

                JSONArray connectionIds = null;
                try {
                    JSONArray connections = dataObject.getJSONArray("connections");
                    connectionIds = connections;

                    myId = dataObject.getString("you");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (myId == null) {

                    FLLog.i("myID 是空的，请检查");
                }

                if (factory == null) {

                    FLLog.i("注意下方this可能错误");
                    PeerConnectionFactory.initializeAndroidGlobals(mContext, true, true, true, mEGLContext);
                    factory = new PeerConnectionFactory();
                }

                if (localMediaStream == null) {

                    createLocalStream();
                }

                if (connectionIds == null) {
                    FLLog.i("错误，connectionIds为空");
                }
                // 创建连接
                createPeerConnections(connectionIds);

                // 连接添加流
                addStream();
                // 生成offer
                createOffers();
            }
        });

        // 接收到新加入的人发了ICE候选，（即经过ICEServer而获取到的地址）
        socket.on("_ice_candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];
                String socketId = null;
                String sdpMid = null;
                int sdpMLineIndex = -1111;
                String sdp = null;
                try {
                    socketId = dataObject.getString("socketId");
                    sdpMid = dataObject.getString("id");
                    sdpMLineIndex = dataObject.getInt("label");
                    sdp = dataObject.getString("candidate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (socketId == null || sdpMid == null || sdpMLineIndex == -1111 || sdp == null) {

                    FLLog.i("ice候选出了错");
                } else {


                    //生成远端网络地址对象
                    IceCandidate candidate = new IceCandidate(sdpMid, sdpMLineIndex, sdp);
                    //拿到当前对应的点对点连接
                    Peer peer = peers.get(socketId);
                    PeerConnection peerConnection = peer.pc;
                    //添加到点对点连接中
                    peerConnection.addIceCandidate(candidate);
                }
            }
        });

        // 其他新人加入房间的信息
        socket.on("_new_peer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {


                FLLog.i("_new_peer接收到");
                JSONObject dataObject = (JSONObject) args[0];
                String socketId = null;

                try {
                    socketId = dataObject.getString("socketId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (socketId == null) {
                    FLLog.i("错误: 新人加入房间获取错误");
                } else {

                    // 创建连接
                    Peer peer = new Peer(socketId);
                    PeerConnection peerConnection = peer.pc;
                    if (localMediaStream == null) {
                        FLLog.i("本地数据流为空");
                    } else {

                        peerConnection.addStream(localMediaStream);
                    }

                    peers.put(socketId, peer);
                }
            }
        });

        // 有人离开房间的事件(暂时处理为单聊，对象离开房间就关闭)
        socket.on("_remove_peer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];
                String socketId = null;

                try {
                    socketId = dataObject.getString("socketId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (socketId == null) {
                    FLLog.i("错误: 有人离开房间获取错误");
                } else {

                    closePeerConnection(socketId);
                    callBack.onCloseRoom();
                }
            }
        });

        // 新加入的人发送offer
        socket.on("_offer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];

                //拿到SDP
                JSONObject sdpDic;
                String sdp = null;
                String type = null;
                String socketId = null;

                try {
                    sdpDic = dataObject.getJSONObject("sdp");
                    sdp = sdpDic.getString("sdp");
                    type = sdpDic.getString("type");
                    socketId = dataObject.getString("socketId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (sdp == null || type == null || socketId == null) {
                    FLLog.i("检查新人发送offer的错误");
                } else {

                    //拿到这个点对点的连接
                    Peer peer = peers.get(socketId);
                    PeerConnection peerConnection = peer.pc;
                    //根据类型和SDP 生成SDP描述对象
                    SessionDescription remoteSdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp);
                    //设置给这个点对点连接
                    peerConnection.setRemoteDescription(peer, remoteSdp);

                    // 回应answer
//                    JSONObject answer = new JSONObject();
//                    JSONObject sdpa = new JSONObject();
//                    String typea = remoteSdp.type.canonicalForm();
//                    FLLog.i("查看type是否正确answer：" + type);
//                    try {
//                        sdpa.put("type", typea);
//                        sdpa.put("sdp", remoteSdp.description);
//                        FLLog.i("可能错误description");
//                        answer.put("socketId", peer.id);
//                        answer.put("sdp", sdpa);
//
//                        SocketManager.socket.emit("__answer", answer);
//                    } catch (JSONException e) {
//
//                        FLLog.i("错误：offer发送为空");
//                        e.printStackTrace();
//                    }

                    FLLog.i("createAnswer调用");
                    peerConnection.createAnswer(peer, offerOrAnswerConstraint());

                }
            }
        });

        //回应offer
        socket.on("_answer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataObject = (JSONObject) args[0];

                //拿到SDP
                JSONObject sdpDic;
                String sdp = null;
                String type = null;
                String socketId = null;

                try {
                    sdpDic = dataObject.getJSONObject("sdp");
                    sdp = sdpDic.getString("sdp");
                    type = sdpDic.getString("type");
                    socketId = dataObject.getString("socketId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (sdp == null || type == null || socketId == null) {
                    FLLog.i("检查回应offer的错误");
                } else {

                    //拿到这个点对点的连接
                    Peer peer = peers.get(socketId);
                    PeerConnection peerConnection = peer.pc;
                    //根据类型和SDP 生成SDP描述对象
                    SessionDescription remoteSdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp);
                    //设置给这个点对点连接
                    peerConnection.setRemoteDescription(peer, remoteSdp);
                }
            }
        });

        // 对方拒接了通话
        socket.on("cancelVideoChat", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                callBack.onCloseRoom();
            }
        });
    }


    /**
     * 加入房间
     *
     * @param room 房间号
     */
    private void joinRoom(String room) {

        Socket socket = SocketManager.socket;
        JSONObject object = new JSONObject();
        try {
            object.put("room", room);
        } catch (JSONException e) {

            FLLog.i("加入房间失败！！！");
            e.printStackTrace();
        }
        socket.emit("__join", object);
    }

    /**
     * 退出房间
     */
    public void exitRoom() {

//        for (Map.Entry<String, Peer> entry : peers.entrySet()){
//
//            closePeerConnection(entry.getKey());
//        }

        localMediaStream = null;


        SocketManager.socket.emit("closeRoom");
        factory = null;
    }



    /**
     * 关闭peerConnection
     *
     * @param connectionId 连接id
     */
    private void closePeerConnection(String connectionId) {

        Peer peer = peers.get(connectionId);
        PeerConnection connection = peer.pc;
        if (connection != null) {
            connection.close();
        }
        peers.remove(connectionId);

        callBack.onCloseWithUserId(connectionId);

    }

    /*
    * 创建本地流，并将流回调
    * */
    private void createLocalStream() {
        localMediaStream = factory.createLocalMediaStream("ARDAMS");

        // 音频
        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localMediaStream.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));

        // 视频
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        VideoCapturer capture = VideoCapturerAndroid.create(frontCameraDeviceName);
        VideoSource videoSource = factory.createVideoSource(capture, localVideoConstraints());
        localMediaStream.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));

        callBack.onSetLocalStream(localMediaStream, myId);
    }

    /*
    * 本地视频流约束
    * */

    private MediaConstraints localVideoConstraints() {

        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(640)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minWidth", Integer.toString(640)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(480)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minHeight", Integer.toString(480)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(15)));

        return videoConstraints;
    }

    /**
     * 为所有连接创建offer
     */
    private void createOffers() {

        for (Map.Entry<String, Peer> entry : peers.entrySet()) {

            Peer peer = entry.getValue();
            PeerConnection connection = peer.pc;
            FLLog.i("createoffer调用");
            connection.createOffer(peer, offerOrAnswerConstraint());
        }
    }

    /*
    * 为所有连接添加流
    * */
    private void addStream() {

        for (Map.Entry<String, Peer> entry : peers.entrySet()) {

            Peer peer = entry.getValue();
            PeerConnection connection = peer.pc;

            if (localMediaStream == null) {
                FLLog.i("添加本地流时，本地流为空");
            } else {

                connection.addStream(localMediaStream);
            }
        }
    }

    /*
    * 创建所有连接
    * */
    private void createPeerConnections(JSONArray connectionIds) {

        for (int i = 0; i < connectionIds.length(); i++) {

            String connectionId = null;
            try {
                connectionId = connectionIds.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (connectionId == null) {

                FLLog.i("错误：获取connectionId失败！");
            } else {

                Peer peer = new Peer(connectionId);
                peers.put(connectionId, peer);
            }
        }
    }

    /*
    * 创建点对点连接
    *
    * */
    private PeerConnection createPeerConnection(String connectionId, Peer peer) {

        if (factory == null) {
            FLLog.i("工厂为空");
            return null;
        }
        if (ICEServers == null) {
            ICEServers = new LinkedList<>();
            ICEServers.add(defaultSTUNServer("stun:23.21.150.121"));
            ICEServers.add(defaultSTUNServer("stun:stun.l.google.com:19302"));
        }

        PeerConnection peerConnection = factory.createPeerConnection(ICEServers, peerConnectionConstraints(), peer);
        return peerConnection;
    }



    // ICE服务器地址
    private PeerConnection.IceServer defaultSTUNServer(String stunURL) {

        return new PeerConnection.IceServer(stunURL);
    }


    private MediaConstraints peerConnectionConstraints() {

        MediaConstraints constraints = new MediaConstraints();
        FLLog.i("peerConnection约束是否要加东西????");
        constraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        return constraints;
    }


    /**
     * 设置offer/answer的约束
     */
    private MediaConstraints offerOrAnswerConstraint() {

        MediaConstraints constraints = new MediaConstraints();

        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        return constraints;
    }


    private String getKeyFromConnectionDic(PeerConnection peerConnection) {

        String socketId = null;
        for (Map.Entry<String, Peer>entry : peers.entrySet()) {

            if (peerConnection.equals(entry.getValue().pc)){

                socketId = entry.getKey();
            }
        }
        if (socketId == null) {
            FLLog.i("错误：未找到相应的socketId");
        }
        return socketId;
    }




    private class Peer implements SdpObserver, PeerConnection.Observer{

        private PeerConnection pc;
        private String id;

        public Peer(String id) {
            this.id = id;
            PeerConnection connection = createPeerConnection(id, this);
            this.pc = connection;
        }


        // ================================PeerConnection========================

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

            callBack.onAddRemoteStream(mediaStream, this.id);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        // =================================offer======================

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {

            this.pc.setLocalDescription(this, sessionDescription);

            // 向服务器发送offer
            JSONObject offer = new JSONObject();
            JSONObject sdp = new JSONObject();
            String type = sessionDescription.type.canonicalForm();
            FLLog.i("查看type是否正确：" + type);
            try {
                sdp.put("type", type);
                sdp.put("sdp", sessionDescription.description);
                FLLog.i("可能错误description");
                offer.put("socketId", this.id);
                offer.put("sdp", sdp);

                SocketManager.socket.emit("__offer", offer);
            } catch (JSONException e) {

                FLLog.i("错误：offer发送为空");
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }
    }

    public interface VideoChatCallBack {

        void onSetLocalStream(MediaStream localStream, String userId);

        void onCloseWithUserId(String userId);

        void onCloseRoom();

        void onAddRemoteStream(MediaStream remoteStream, String userId);
    }
}
