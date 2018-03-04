package com.foxpower.flchatofandroid.ui.fragment;

import android.widget.TextView;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.common.BaseFragment;
import com.foxpower.flchatofandroid.enums.SocketConnectStatus;
import com.foxpower.flchatofandroid.util.manager.ChatManager;
import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.manager.SocketManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fengli on 2018/2/7.
 */

public class HomeMeFragment extends BaseFragment {

    @BindView(R.id.me_current_user)
    TextView currentUserView;

    @BindView(R.id.me_connect_status)
    TextView connectStatusView;

    @OnClick(R.id.me_exit_login)
    void exitLogin() {

//        SocketManager.socket.disconnect();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_me;
    }

    @Override
    protected void initView() {
        super.initView();

        currentUserView.setText("登录用户：" + ClientManager.currentUserId);

        connectStatusView.setText(connectStatusDes(SocketManager.connectStatus));

    }

    private String connectStatusDes(SocketConnectStatus status) {
        String des = "";
        switch (status){
            case SocketConnected:
                des = "连接成功";
                break;
            case SocketConnecting:
                des = "连接中";
                break;
            case SocketConnectError:
                des = "连接失败";
                break;
            case SocketDisconnected:
                des = "连接断开";
                break;
        }
        return "连接状态：" + des;
    }

    @Override
    protected void initData() {
        super.initData();

//        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
}
