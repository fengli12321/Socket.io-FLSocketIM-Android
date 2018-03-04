package com.foxpower.flchatofandroid.db;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.foxpower.flchatofandroid.db.dbObject.ConversationDbObject;
import com.foxpower.flchatofandroid.db.dbObject.MessageDbObject;
import com.foxpower.flchatofandroid.enums.ChatType;
import com.foxpower.flchatofandroid.enums.MessageSendStatus;
import com.foxpower.flchatofandroid.model.MessageBody;
import com.foxpower.flchatofandroid.model.MessageModel;
import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.other.FLLog;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by fengli on 2018/2/8.
 */

public class DbManager {

    private static RealmConfiguration config = null;

    private static Realm mRealm = null;

    private static String dbName = null;

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    // 创建数据库
    public static void createDb(String userName) {


        if (dbName != null && dbName.equals(userName)) {
            return;
        }
        dbName = userName;
        config = new RealmConfiguration.Builder()
                .name( dbName + ".realm") //文件名
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(0) //版本号
                .build();
        if (mRealm != null) {
            mRealm.close();
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                mRealm = Realm.getInstance(config);
            }
        });
    }

    /*
    *
    * 插入消息数据
    * */
    public static void insertMessage(final MessageModel messageModel) {


        mainHandler.post(new Runnable() {
            @Override
            public void run() {



                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        MessageDbObject dbObject = messageToDbObject(messageModel);
                        realm.copyToRealm(dbObject);

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        FLLog.i("新增消息成功");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        FLLog.i("新增消息失败");
                    }
                });
            }
        });

    }

    /*
    * 更新消息数据
    * */
    public static void updateMessage(final MessageModel messageModel) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        MessageDbObject object = realm.where(MessageDbObject.class).equalTo("localtime", messageModel.getSendTime()).findFirst();
                        if (object != null) {
                            object.setId(messageModel.getMsg_id());
                            object.setTimestamp(messageModel.getTimestamp());
                            object.setBodies(JSON.toJSONString(messageModel.getBodies()));
                            int status = 0;
                            switch (messageModel.getSendStatus()) {
                                case MessageSending:
                                    status = 0;
                                    break;
                                case MessageSendFail:
                                    status = 1;
                                    break;

                                case MessageSendSuccess:
                                    status = 2;
                                    break;
                            }
                            object.setSendStatus(status);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        FLLog.i("消息更新成功");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        FLLog.i("消息更新失败");
                    }
                });
            }
        });
    }

    /*
    *
    * 消息数据转数据库
    * */
    private static MessageDbObject messageToDbObject(MessageModel messageModel) {

        MessageDbObject dbObject = new MessageDbObject();
        dbObject.setId(messageModel.getMsg_id());
        dbObject.setLocaltime(messageModel.getSendTime());
        dbObject.setTimestamp(messageModel.getTimestamp());
        boolean isReceiver = dbName.equals(messageModel.getTo_user());
        dbObject.setConversation(isReceiver?messageModel.getFrom_user():messageModel.getTo_user());
        dbObject.setReceiver(isReceiver);
        dbObject.setChatType("chat");
        String bodies = JSON.toJSONString(messageModel.getBodies());
        dbObject.setBodies(bodies);
        int statusId = 0;
        switch (messageModel.getSendStatus()){
            case MessageSending:
                statusId = 0;
                break;
            case MessageSendFail:
                statusId = 1;
                break;
            case MessageSendSuccess:
                statusId = 2;
                break;
        }
        dbObject.setSendStatus(statusId);

        return dbObject;
    }

    /*
    * 插入或者更新会话消息
    * */
    public static void insertOrUpdateConversation(final MessageModel messageModel) {

        String conversationName = dbName.endsWith(messageModel.getFrom_user()) ? messageModel.getTo_user() : messageModel.getFrom_user();

        conversationIsExist(conversationName, new SearchExistCallBack() {
            @Override
            public void findResult(boolean isExist) {

                if (isExist) { // 会话存在， 更新最新消息及时间

                    updateConversation(messageModel);
                } else { // 会话不存在，新增会话

                    insertConversation(messageModel);
                }
            }
        });
    }

    /*
    * 新增会话
    * */
    private static void insertConversation(final MessageModel messageModel) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        ConversationDbObject object = messageToConversationDb(messageModel);
                        realm.copyToRealm(object);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        FLLog.i("新增会话数据成功");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        FLLog.i("新增会话数据失败");
                    }
                });

            }
        });
    }

    /*
    * 消息模型转会话
    * */
    public static ConversationDbObject messageToConversationDb(MessageModel messageModel) {

        ConversationDbObject dbObject = new ConversationDbObject();

        String conversationName = messageModel.getFrom_user().endsWith(dbName)?messageModel.getTo_user():messageModel.getFrom_user();
        dbObject.setId(conversationName);
        boolean isChatting = ClientManager.isChattingWithUser(conversationName);
        int unreadCount = isChatting?0:1;
        dbObject.setUnreadcount(unreadCount);
        dbObject.setLatestmsgtext(messageModel.getMessageTip());
        dbObject.setLatestmsgtimestamp(messageModel.getTimestamp());
        return dbObject;
    }

    /*
    * 更新会话
    * */
    public static void updateConversation(final MessageModel messageModel) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String conversationName = messageModel.getFrom_user().endsWith(dbName) ? messageModel.getTo_user() : messageModel.getFrom_user();
                        ConversationDbObject object = realm.where(ConversationDbObject.class).equalTo("id", conversationName).findFirst();
                        object.setLatestmsgtext(messageModel.getMessageTip());
                        object.setLatestmsgtimestamp(messageModel.getTimestamp());

                        boolean isChatting = ClientManager.isChattingWithUser(conversationName);
                        object.setUnreadcount(isChatting?0:object.getUnreadcount()+1);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        FLLog.i("会话更新成功");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        FLLog.i("会话更新事变");
                    }
                });
            }
        });
    }


    /*
    *
    * 判断会话是否存在
    * @param
    * */
    private static void conversationIsExist(final String name, final SearchExistCallBack callBack) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                ConversationDbObject object = mRealm.where(ConversationDbObject.class).equalTo("id", name).findFirst();
                callBack.findResult(object != null);
            }
        });

    }

    /*
    * 查询所有会话
    * */
    public static void queryAllConversation(final QueryDbCallBack<ConversationDbObject> callBack) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                final RealmResults<ConversationDbObject> results = mRealm.where(ConversationDbObject.class).findAllAsync();
                results.addChangeListener(new RealmChangeListener<RealmResults<ConversationDbObject>>() {
                    @Override
                    public void onChange(RealmResults<ConversationDbObject> element) {

                        element = element.sort("latestmsgtimestamp");

                        List<ConversationDbObject> objects = mRealm.copyFromRealm(element);

                        callBack.querySuccess(objects, false);
                        results.removeAllChangeListeners();
                    }
                });
            }
        });
    }

    // 分页查询与某个用户的聊天信息
    public static void queryMessages(final String username, final int page, final int limit, final QueryDbCallBack<MessageModel> callBack) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                final RealmResults<MessageDbObject> results = mRealm.where(MessageDbObject.class).equalTo("conversation", username).findAllAsync();
                results.addChangeListener(new RealmChangeListener<RealmResults<MessageDbObject>>() {
                    @Override
                    public void onChange(RealmResults<MessageDbObject> element) {

                        element = element.sort("timestamp", Sort.DESCENDING);

                        int startIndex = page * limit;
                        if (element.size() < startIndex) { // 没有分页数据了

                            callBack.querySuccess(null, false);
                        } else {


                            if (startIndex + limit < element.size()) { // 还有更多数据

                                List<MessageModel> messages = new ArrayList<>();
                                for (int i = 0; i < limit; i++) {


                                    MessageDbObject dbObject = mRealm.copyFromRealm(element.get(i + startIndex));
                                    messages.add(0, DBModelToMessageModel(dbObject));
                                }
                                callBack.querySuccess(messages, true);
                            } else { // 没有更多数据了

                                List<MessageModel> messages = new ArrayList<>();
                                for (int i = 0; i < element.size() - startIndex; i++) {


                                    MessageDbObject dbObject = mRealm.copyFromRealm(element.get(i + startIndex));
                                    messages.add(0, DBModelToMessageModel(dbObject));
                                }
                                callBack.querySuccess(messages, false);
                            }
                        }

                        results.removeAllChangeListeners();
                    }
                });

            }
        });
    }

    /*
    * 数据库消息转聊天消息模型
    * */
    private static MessageModel DBModelToMessageModel(MessageDbObject dbObject) {

        MessageModel msg = new MessageModel();

        msg.setMsg_id(dbObject.getId());
        msg.setSendTime(dbObject.getLocaltime());
        msg.setTimestamp(dbObject.getTimestamp());

        boolean isReceiver = dbObject.isReceiver();
        String conversation = dbObject.getConversation();
        String currentUser = ClientManager.currentUserId;
        msg.setFrom_user(isReceiver?conversation:currentUser);
        msg.setTo_user(isReceiver?currentUser:conversation);
        msg.setChat_type(ChatType.chat);

        MessageSendStatus status = MessageSendStatus.MessageSendFail;
        switch (dbObject.getSendStatus()) {
            case 0:
                status = MessageSendStatus.MessageSending;
                break;
            case 1:
                status = MessageSendStatus.MessageSendFail;
                break;
            case 2:
                status = MessageSendStatus.MessageSendSuccess;
                break;
        }
        msg.setSendStatus(status);
        MessageBody body = JSON.parseObject(dbObject.getBodies(), MessageBody.class);
        msg.setBodies(body);

        return msg;
    }

    /*
    * 清空会话未读消息
    * */
    public static void clearConversationUnreadCount(final String conversationId) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        ConversationDbObject dbObject = realm.where(ConversationDbObject.class).equalTo("id", conversationId).findFirst();
                        if (dbObject != null) {
                            dbObject.setUnreadcount(0);
                        }
                    }
                });
            }
        });
    }
    // 回调
    private interface SearchExistCallBack {

        void findResult(boolean isExist);
    }

    public interface QueryDbCallBack<T>{

        void querySuccess(List<T> items, boolean hasMore);
    }
}
