package com.foxpower.flchatofandroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.broadcast.BroadcastConstant;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.common.BaseFragment;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.db.dbObject.ConversationDbObject;
import com.foxpower.flchatofandroid.model.MessageEvent;
import com.foxpower.flchatofandroid.model.MessageModel;
import com.foxpower.flchatofandroid.ui.activity.ChatActivity;
import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.tool.TimeUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by FeLi on 2018/2/7.
 */

public class HomeMessageFragment extends BaseFragment {


    private LocalBroadcastManager localBroadcastManager;
    private MessageFragmentBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    List<ConversationDbObject> items = new ArrayList<ConversationDbObject>();

    private CommonAdapter adapter;

    @BindView(R.id.conversation_list_view)
    ListView listView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_message;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        super.initView();


        adapter = new CommonAdapter<ConversationDbObject>(mContext, R.layout.coversation_list_item, items) {
            @Override
            protected void convert(ViewHolder viewHolder, ConversationDbObject item, int position) {

                viewHolder.setText(R.id.list_user_name, item.getId());
                viewHolder.setText(R.id.list_content, item.getLatestmsgtext());

                String time = TimeUtil.getHourStrTime(item.getLatestmsgtimestamp());
                viewHolder.setText(R.id.list_time, time);
                viewHolder.setText(R.id.list_unreadCount, Integer.toString(item.getUnreadcount()));
                TextView unreadText = viewHolder.getView(R.id.list_unreadCount);
                if (item.getUnreadcount() == 0) {
                    unreadText.setVisibility(View.GONE);
                } else {
                    unreadText.setVisibility(View.VISIBLE);
                }
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                BaseActivity activity = (BaseActivity)mContext;
                Bundle bundle = new Bundle();
                bundle.putString("username", items.get(i).getId());
                activity.openActivity(ChatActivity.class, bundle);


                // 清空未读消息
                clearUnread(i);
            }
        });
    }


    private void clearUnread(int position){

        ConversationDbObject conversation = items.get(position);
        // 清空数据库未读消息
        DbManager.clearConversationUnreadCount(conversation.getId());

        // 清空UI
        conversation.setUnreadcount(0);
        adapter.notifyDataSetChanged();
    }

    private void clearUnread(String conversationId) {

        int i = 0;
        for(ConversationDbObject dbObject : items) {

            if (dbObject.getId().equals(conversationId)) {

                clearUnread(i);
                break;
            }
            i++;
        }
    }

    @Override
    protected void initData() {
        super.initData();


        queryData();

        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        broadcastReceiver = new MessageFragmentBroadcastReceiver();
        intentFilter = new IntentFilter();

        intentFilter.addAction(BroadcastConstant.clearUnreadMessage);
        intentFilter.addAction(BroadcastConstant.updateConversation);

        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void queryData() {

        // 查询会话数据
        DbManager.queryAllConversation(new DbManager.QueryDbCallBack<ConversationDbObject>() {
            @Override
            public void querySuccess(List<ConversationDbObject> items, boolean hasMore) {

                updateConversationList(items);
            }
        });
    }


    private void updateConversationList(List<ConversationDbObject> dbDatas) {

        long threadId = Thread.currentThread().getId();
        items.clear();
        items.addAll(dbDatas);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);


        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessageEvent(MessageEvent event){

        Object object = event.getMsg();
        switch (event.getType()){
            case EventMessage:

                receivedNewMessage((MessageModel) event.getMsg());
                break;
        }
    }

    /*
    * 收到新消息
    * */
    private void receivedNewMessage(MessageModel messageModel) {

        boolean hasEqual = false;
        String id = ClientManager.currentUserId.equals(messageModel.getFrom_user())?messageModel.getTo_user():messageModel.getFrom_user();
        for (ConversationDbObject conversation : items) {

            if (conversation.getId().equals(id)){

                boolean isChatting = ClientManager.isChattingWithUser(id);
                conversation.setUnreadcount(isChatting?0:conversation.getUnreadcount()+1);
                conversation.setLatestmsgtimestamp(messageModel.getTimestamp());
                conversation.setLatestmsgtext(messageModel.getMessageTip());

                items.remove(conversation);
                items.add(0, conversation);

                hasEqual = true;
                break;
            }
        }

        if (!hasEqual) {

            ConversationDbObject conversation = DbManager.messageToConversationDb(messageModel);

            items.add(0, conversation);
        }

        adapter.notifyDataSetChanged();
    }


    public class MessageFragmentBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BroadcastConstant.clearUnreadMessage)){

                String conversation = intent.getStringExtra("conversation");

                clearUnread(conversation);
            }
            else if (intent.getAction().equals(BroadcastConstant.updateConversation)) {

                queryData();
            }
            FLLog.i("收到广播信息");
        }


    }
}
