package com.foxpower.flchatofandroid.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.broadcast.BroadcastConstant;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.enums.MessageBtnType;
import com.foxpower.flchatofandroid.model.MessageEvent;
import com.foxpower.flchatofandroid.model.MessageModel;
import com.foxpower.flchatofandroid.ui.adapter.ChatInputPagerAdapter;
import com.foxpower.flchatofandroid.ui.fragment.ChatEmotionFragment;
import com.foxpower.flchatofandroid.ui.fragment.ChatInputOtherFragment;
import com.foxpower.flchatofandroid.ui.other.MsgImageLoader;
import com.foxpower.flchatofandroid.ui.view.AudioRecordPopupWindow;
import com.foxpower.flchatofandroid.ui.view.NoScrollViewPager;
import com.foxpower.flchatofandroid.util.constant.UrlConstant;
import com.foxpower.flchatofandroid.util.manager.ChatManager;
import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;
import com.foxpower.flchatofandroid.util.tool.SoftKeyBoardListener;
import com.foxpower.flchatofandroid.util.tool.AudioRecordUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.zhy.adapter.abslistview.MultiItemTypeAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fengli on 2018/2/8.
 */

public class ChatActivity extends BaseActivity {

    private int page = 0;
    private int limit = 5;

    private MultiItemTypeAdapter adapter;
    List<MessageModel> messageList = new ArrayList<MessageModel>();
    private ChatEmotionFragment chatEmotionFragment;
    private MessageBtnType btnType = MessageBtnType.MsgBtnText;

    private AudioRecordPopupWindow mVoicePop;

    private ChatInputPagerAdapter viewpagerAdapter;

    AudioRecordUtil mAudioRecordUtil;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.chat_list_view)
    ListView listView;

    @BindView(R.id.chat_voice)
    ImageView voiceBtn;

    @BindView(R.id.chat_emotion)
    ImageView emotionBtn;

    @BindView(R.id.chat_add_other)
    ImageView addOtherBtn;

    @BindView(R.id.chat_input_text)
    EditText inputTextMessage;


    @BindView(R.id.chat_send_voice)
    TextView sendVoiceBtn;

    @BindView(R.id.chat_text_send_btn)
    Button sendMessageBtn;

    @BindView(R.id.chat_msg_input_other_back)
    RelativeLayout inputBackLayout;

    @BindView(R.id.chat_msg_input_viewpager)
    NoScrollViewPager viewPager;


    @OnClick(R.id.chat_voice)
    void voiceClick() {
        if (btnType != MessageBtnType.MsgBtnVoice) {

            setBtnType(MessageBtnType.MsgBtnVoice);
        } else {

            setBtnType(MessageBtnType.MsgBtnText);
        }
    }

    @OnClick(R.id.chat_emotion)
    void emotionClick() {

        if (btnType != MessageBtnType.MsgBtnEmotion) {

            setBtnType(MessageBtnType.MsgBtnEmotion);
        } else {

            setBtnType(MessageBtnType.MsgBtnText);
        }
    }

    @OnClick(R.id.chat_add_other)
    void addOtherClick() {

        if (btnType != MessageBtnType.MsgBtnOther) {

            setBtnType(MessageBtnType.MsgBtnOther);

        } else {

            setBtnType(MessageBtnType.MsgBtnText);
        }
    }

    private void showEmotionOrOther(MessageBtnType type, boolean show) {

        if (show) {
            KeyboardUtils.hideSoftInput(this);
            inputBackLayout.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams par = (LinearLayout.LayoutParams) inputBackLayout.getLayoutParams();
            switch (type) {
                case MsgBtnEmotion:
                    int height = chatEmotionFragment.getChatEmotionHeight();
                    par.height = chatEmotionFragment.getChatEmotionHeight();
                    viewPager.setCurrentItem(0);
                    break;
                case MsgBtnOther:
                    par.height = FLUtil.dip2px(mContext, 150);
                    viewPager.setCurrentItem(1);
                    break;
            }
            inputBackLayout.setLayoutParams(par);
        } else {

            inputBackLayout.setVisibility(View.GONE);
        }
    }



    private void setBtnType(MessageBtnType btnType) {
        this.btnType = btnType;

        voiceBtn.setImageResource(R.drawable.keyboard_voice);
        emotionBtn.setImageResource(R.drawable.keyboard_emotion);
        addOtherBtn.setImageResource(R.drawable.keyboard_add);

        if (btnType == MessageBtnType.MsgBtnVoice) {

            inputTextMessage.setVisibility(View.GONE);
            sendVoiceBtn.setVisibility(View.VISIBLE);
        } else {
            inputTextMessage.setVisibility(View.VISIBLE);
            sendVoiceBtn.setVisibility(View.GONE);
        }

        switch (btnType){

            case MsgBtnVoice:
                voiceBtn.setImageResource(R.drawable.keyboard_keyboard);
                showEmotionOrOther(btnType, false);
                KeyboardUtils.hideSoftInput(this);
                break;

            case MsgBtnEmotion:
                emotionBtn.setImageResource(R.drawable.keyboard_keyboard);
                showEmotionOrOther(btnType,true);
                break;

            case MsgBtnOther:
                addOtherBtn.setImageResource(R.drawable.keyboard_keyboard);
                showEmotionOrOther(btnType, true);
                break;
            case MsgBtnText:
                showEmotionOrOther(btnType, false);
                break;
        }
    }

    @OnClick(R.id.chat_text_send_btn)
    void sendTextMsg(){

        if (inputTextMessage.getText().toString().trim().length() == 0){
            return;
        }
        MessageModel messageModel = ChatManager.sendTextMsg(inputTextMessage.getText().toString(), friendsUserName, new ChatManager.SendStatusCallBack() {
            @Override
            public void sendStatus(MessageModel messageModel) {
                sendMsgSuccess();
            }
        });
        inputTextMessage.setText("");

        // 刷新UI
        sendMsgAfter(messageModel);
    }


    private void sendMsgSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sendMsgAfter(MessageModel messageModel) {
        messageList.add(messageModel);
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(messageList.size() - 1);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(new Intent(BroadcastConstant.updateConversation));
    }

    private String friendsUserName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        super.initView();

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

                setBtnType(MessageBtnType.MsgBtnText);
            }

            @Override
            public void keyBoardHide(int height) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userName = bundle.getString("username");
            setTitle(userName);
            ClientManager.chattingUserId = userName;
            friendsUserName = userName;
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshMessageList(false);
            }
        });

        adapter = new MultiItemTypeAdapter(this, messageList);
        adapter.addItemViewDelegate(new MsgLeftTextItemDelegate());
        adapter.addItemViewDelegate(new MsgRightTextItemDelegate());

        listView.setAdapter(adapter);

        EventBus.getDefault().register(this);


        View view = View.inflate(mContext, R.layout.layout_microphone, null);
        mVoicePop = new AudioRecordPopupWindow(this, view);

        final ImageView voiceImageView = view.findViewById(R.id.iv_recording_icon);
        final TextView voiceTimeView = view.findViewById(R.id.tv_recording_time);
        final TextView voiceTextView = view.findViewById(R.id.tv_recording_text);

        inputTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    addOtherBtn.setVisibility(View.GONE);
                    sendMessageBtn.setVisibility(View.VISIBLE);
                }
                else {
                    addOtherBtn.setVisibility(View.VISIBLE);
                    sendMessageBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        sendVoiceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 获取x坐标
                int x = (int)motionEvent.getX();
                // 获取y坐标
                int y = (int) motionEvent.getY();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        FLLog.i("开始录音");
                        mVoicePop.showAtLocation(view, Gravity.CENTER, 0, 0);
                        sendVoiceBtn.setText("松开结束");
                        voiceTextView.setText("手指上滑，取消发送");
                        sendVoiceBtn.setTag("1");
                        mAudioRecordUtil.startRecord(mContext);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (wantToCancel(x, y)) {
                            FLLog.i("想要取消");
                            sendVoiceBtn.setText("松开结束");
                            voiceTextView.setText("松开手指，取消发送");
                            sendVoiceBtn.setTag("2");
                        } else {
                            FLLog.i("可以取消");
                            sendVoiceBtn.setText("松开结束");
                            voiceTextView.setText("手指上滑，取消发送");
                            sendVoiceBtn.setTag("1");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        FLLog.i("消息发送");
                        mVoicePop.dismiss();
                        if (sendVoiceBtn.getTag().equals("2")) {

                            mAudioRecordUtil.cancelRecord();
                        }else {

                            mAudioRecordUtil.stopRecord();
                        }
                        sendVoiceBtn.setText("按住说话");
                        sendVoiceBtn.setTag("3");
                        break;
                }

                return true;
            }
        });


        mAudioRecordUtil = new AudioRecordUtil();

        mAudioRecordUtil.setOnAudioStatusUpdateListener(new AudioRecordUtil.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {

                voiceImageView.getDrawable().setLevel((int)(3000+6000*db/100));
                voiceTimeView.setText(FLUtil.long2String(time));
            }

            @Override
            public void onStop(long time, String filePath, String audioName) {

                voiceTimeView.setText(FLUtil.long2String(0));
                if (time < 1) {
                    FLUtil.showShortToast(mContext, "录音时间过短");
                } else {

                    // 发送语音消息
                    sendAudioMsg(audioName, time);
                }
            }

            @Override
            public void onError() {

            }
        });


        // viewpager
        ArrayList<Fragment> fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        chatEmotionFragment.setClickCallBack(new ChatEmotionFragment.EmotionClickCallBack() {
            @Override
            public void onClickEmotion(String emotion) {
                int curPosition = inputTextMessage.getSelectionStart();
                StringBuilder sb = new StringBuilder(inputTextMessage.getText().toString());
                sb.insert(curPosition, emotion);
                inputTextMessage.setText(sb.toString());
                inputTextMessage.setSelection(curPosition + emotion.length());
            }

            @Override
            public void onClickDelete() {

                inputTextMessage.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });

        ChatInputOtherFragment otherFragment = new ChatInputOtherFragment();
        otherFragment.setClickCallBack(new ChatInputOtherFragment.AddItemClickCallBack() {
            @Override
            public void clickItemIndex(int index) {

                setBtnType(MessageBtnType.MsgBtnText);
                KeyboardUtils.hideSoftInput(inputTextMessage);
                switch (index){
                    case 0:
                        pickImage();
                        break;
                    case 1:
                        takeCamera();
                        break;
                    case 2:
                        pickLocation();
                        break;
                    case 3:
                        videoChat();
                        break;
                }
            }
        });
        fragments.add(chatEmotionFragment);
        fragments.add(otherFragment);
        viewpagerAdapter = new ChatInputPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(0);
    }

    /*
    * 发起视频通话
    * */

    private void videoChat() {

        Bundle bundle = new Bundle();
        bundle.putString("fromUser", ClientManager.currentUserId);
        bundle.putString("toUser", friendsUserName);
        bundle.putInt("type", 0);

        openActivity(VideoChatActivity.class, bundle);
    }

    /*
    * 发送音频
    * */
    private void sendAudioMsg(String audioName, long duration) {

        MessageModel messageModel = ChatManager.sendAudioMsg(audioName, duration, friendsUserName, new ChatManager.SendStatusCallBack() {
            @Override
            public void sendStatus(MessageModel messageModel) {

                sendMsgSuccess();
            }
        });

        sendMsgAfter(messageModel);
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > sendVoiceBtn.getWidth()) {
            return true;
        }
        if (y < -50 || y > sendVoiceBtn.getHeight() + 50) {
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {
        super.initData();

        refreshMessageList(true);


    }

    /*
    * 选择图片
    * */
    private void pickImage () {

        FLLog.i("去选择图片");
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new MsgImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(true);
        imagePicker.setSelectLimit(3);

        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    /*
    * 选择位置
    * */
    private void pickLocation() {

//        openActivity(LocationActivity.class);

        Intent intent = new Intent(this, LocationActivity.class);
        startActivityForResult(intent, 1000);
    }


    /*
    * 拍摄
    * */
    private void takeCamera() {

        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, 1001);
    }




    /*
    * 发送图片
    * */
    private void sendImage(String imagePath, String imageName, int width, int height) {

        HashMap size = new HashMap();
        size.put("width", width);
        size.put("height", height);
        MessageModel messageModel = ChatManager.sendImageMsg(imagePath, imageName, size, friendsUserName, new ChatManager.SendStatusCallBack() {
            @Override
            public void sendStatus(MessageModel messageModel) {

                sendMsgSuccess();
            }
        });

        sendMsgAfter(messageModel);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {

                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                for (ImageItem imageItem : images) {

                    HashMap size = new HashMap();
                    sendImage(imageItem.path, null,imageItem.width, imageItem.height);
                }
            }
        } else if (resultCode == 101) { // 发送定位的回调

            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            String location = data.getStringExtra("location");
            String detail = data.getStringExtra("detailLocation");
            FLLog.i("发送定位消息");
            sendLocationMsg(lat, lon, location, detail);
        } else if (resultCode == 102) { // 拍摄回调

            String imageName = data.getStringExtra("imageName");
            int imageWidth = data.getIntExtra("imageWidth", 0);
            int imageHeight = data.getIntExtra("imageHeight", 0);

            sendImage(null, imageName, imageWidth, imageHeight);
        }
    }

    private void sendLocationMsg(double lat, double lon, String location, String detail){

        MessageModel messageModel = ChatManager.sendLocationMsg(lat, lon, location, detail, friendsUserName, new ChatManager.SendStatusCallBack() {
            @Override
            public void sendStatus(MessageModel messageModel) {

                sendMsgSuccess();
            }
        });
        sendMsgAfter(messageModel);
    }

    private void refreshMessageList(final boolean scrollToBottom) {
        DbManager.queryMessages(friendsUserName, page, limit, new DbManager.QueryDbCallBack<MessageModel>() {
            @Override
            public void querySuccess(List<MessageModel> items, boolean hasMore) {

                refreshLayout.setRefreshing(false);
                page++;

                if (items.size() == 0) {

                    refreshLayout.setEnabled(false);
                    FLUtil.showShortToast(mContext, "没有更多消息");
                } else {

                    if (!hasMore) {
                        refreshLayout.setEnabled(false);
                    }

                    messageList.addAll(0, items);
                    adapter.notifyDataSetChanged();

                    if (scrollToBottom) {
                        listView.smoothScrollToPosition(messageList.size() - 1);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ClientManager.chattingUserId = "";
        EventBus.getDefault().unregister(this);
        KeyboardUtils.hideSoftInput(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MessageEvent event) {

        switch (event.getType()){
            case EventMessage: // 收到新消息


                MessageModel message = (MessageModel) event.getMsg();

                if (!message.getFrom_user().equals(friendsUserName) && !message.getTo_user().equals(friendsUserName)) { // 不属于该会话的消息
                    return;
                }
                messageList.add(message);
                adapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(messageList.size() - 1);
                break;
        }
    }

    public class MsgLeftTextItemDelegate implements ItemViewDelegate<MessageModel> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.left_chat_item_text;
        }

        @Override
        public boolean isForViewType(MessageModel item, int position) {
            return (item.getTo_user().equals(ClientManager.currentUserId));
        }

        @Override
        public void convert(ViewHolder holder, MessageModel messageModel, int position) {

            TextView msgTextView = holder.getView(R.id.message_text);
            ImageView imageView = holder.getView(R.id.message_img);
            ViewGroup voiceBack = holder.getView(R.id.message_voice);
            ViewGroup locationBack = holder.getView(R.id.message_location_back);
            switch (messageModel.getBodies().getType()) {
                case txt:
                    msgTextView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.GONE);
                    holder.setText(R.id.message_text, messageModel.getBodies().getMsg());
                    break;
                case img:
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.GONE);
                    String imagePath = FLUtil.imageSavePath() + messageModel.getBodies().getFileName();

                    Glide.with(mContext).load(imagePath).into(imageView);

                    int screenWidth = ScreenUtils.getScreenWidth();

                    int imageWidth = messageModel.getBodies().getSize().get("width").intValue();
                    int imageHeight = messageModel.getBodies().getSize().get("height").intValue();

                    imageWidth = SizeUtils.dp2px(imageWidth);
                    imageHeight = SizeUtils.dp2px(imageHeight);
                    float scale = (float)imageWidth/imageHeight;
                    if (imageWidth > screenWidth/2) {
                        imageWidth = screenWidth/2;
                        imageHeight = (int)(imageWidth/scale);
                    }

                    ViewGroup.LayoutParams layoutParams =  imageView.getLayoutParams();
                    layoutParams.width = imageWidth;
                    layoutParams.height = imageHeight;
                    break;
                case audio:
                    voiceBack.setVisibility(View.VISIBLE);
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    locationBack.setVisibility(View.GONE);
                    int minWidth = SizeUtils.dp2px(90);
                    int maxWidth = ScreenUtils.getScreenWidth()*2/3;

                    long duration = messageModel.getBodies().getDuration();
                    long maxDuration = 60;
                    if (duration > maxDuration) {
                        duration = maxDuration;
                    }
                    int width = (int) (minWidth + (float)(maxWidth - minWidth)/maxDuration*duration);
                    voiceBack.getLayoutParams().width = width;
                    holder.setText(R.id.message_voice_duration, FLUtil.long2String(duration));
                    break;
                case loc:
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.VISIBLE);

                    holder.setText(R.id.message_location_name, messageModel.getBodies().getLocationName());
                    holder.setText(R.id.message_location_detail, messageModel.getBodies().getDetailLocationName());

                    ImageView locationImage = holder.getView(R.id.message_location_img);
                    String path = UrlConstant.baseUrl + "/" + messageModel.getBodies().getFileRemotePath();
                    Glide.with(mContext).load(path).into(locationImage);

                    break;
            }



        }

    }

    public class MsgRightTextItemDelegate implements ItemViewDelegate<MessageModel> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.right_chat_item_text;
        }

        @Override
        public boolean isForViewType(MessageModel item, int position) {
            return item.getFrom_user().equals(ClientManager.currentUserId);
        }

        @Override
        public void convert(ViewHolder holder, MessageModel messageModel, int position) {

            holder.setText(R.id.message_text, messageModel.getBodies().getMsg());

            ProgressBar bar = holder.getView(R.id.msg_send_progress);
            ImageView failIcon = holder.getView(R.id.msg_send_fail);
            ViewGroup voiceBack = holder.getView(R.id.message_voice);
            ViewGroup locationBack = holder.getView(R.id.message_location_back);
            switch (messageModel.getSendStatus()){
                case MessageSendSuccess:
                    bar.setVisibility(View.GONE);
                    failIcon.setVisibility(View.GONE);
                    break;

                case MessageSending:
                    bar.setVisibility(View.VISIBLE);
                    failIcon.setVisibility(View.GONE);
                    break;

                case MessageSendFail:
                    bar.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    break;
            }


            TextView msgTextView = holder.getView(R.id.message_text);
            ImageView imageView = holder.getView(R.id.message_img);

            switch (messageModel.getBodies().getType()) {
                case txt:
                    msgTextView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.GONE);
                    holder.setText(R.id.message_text, messageModel.getBodies().getMsg());
                    break;
                case img:
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.GONE);
                    String imagePath = FLUtil.imageSavePath() + messageModel.getBodies().getFileName();

                    Glide.with(mContext).load(imagePath).into(imageView);

                    int screenWidth = ScreenUtils.getScreenWidth();

                    int imageWidth = messageModel.getBodies().getSize().get("width").intValue();
                    int imageHeight = messageModel.getBodies().getSize().get("height").intValue();

                    imageWidth = SizeUtils.dp2px(imageWidth);
                    imageHeight = SizeUtils.dp2px(imageHeight);
                    float scale = (float)imageWidth/imageHeight;
                    if (imageWidth > screenWidth/2) {
                        imageWidth = screenWidth/2;
                        imageHeight = (int)(imageWidth/scale);
                    }

                    ViewGroup.LayoutParams layoutParams =  imageView.getLayoutParams();
                    layoutParams.width = imageWidth;
                    layoutParams.height = imageHeight;
                    break;
                case audio:
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    voiceBack.setVisibility(View.VISIBLE);
                    locationBack.setVisibility(View.GONE);
                    int minWidth = SizeUtils.dp2px(90);
                    int maxWidth = ScreenUtils.getScreenWidth()*2/3;

                    long duration = messageModel.getBodies().getDuration();
                    long maxDuration = 60;
                    if (duration > maxDuration) {
                        duration = maxDuration;
                    }
                    int width = (int) (minWidth + (float)(maxWidth - minWidth)/maxDuration*duration);
                    voiceBack.getLayoutParams().width = width;
                    holder.setText(R.id.message_voice_duration, FLUtil.long2String(duration));
                    break;
                case loc:
                    msgTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    voiceBack.setVisibility(View.GONE);
                    locationBack.setVisibility(View.VISIBLE);

                    holder.setText(R.id.message_location_name, messageModel.getBodies().getLocationName());
                    holder.setText(R.id.message_location_detail, messageModel.getBodies().getDetailLocationName());

                    ImageView locationImage = holder.getView(R.id.message_location_img);
                    String path = UrlConstant.baseUrl + "/" + messageModel.getBodies().getFileRemotePath();
                    Glide.with(mContext).load(path).into(locationImage);

                    break;
            }
        }
    }

}
