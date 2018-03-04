package com.foxpower.flchatofandroid.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.callBack.NetCallBack;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.util.constant.UrlConstant;
import com.foxpower.flchatofandroid.util.manager.ClientManager;
import com.foxpower.flchatofandroid.util.manager.NetManager;
import com.foxpower.flchatofandroid.util.manager.SocketManager;
import com.foxpower.flchatofandroid.util.manager.SocketManager.SocketCallBack;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fengli on 2018/2/5.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_username)
    EditText userName;

    @BindView(R.id.login_password)
    EditText password;

    @BindView(R.id.login_loading_view)
    AVLoadingIndicatorView loadingView;




    @OnClick(R.id.btn_login)
    void login (){
        if (checkInput()) {

            loadingView.show();
            Map parameters = new HashMap();
            final String user = userName.getText().toString();
            final String pwd = password.getText().toString();
            parameters.put("userName", user);
            parameters.put("password", pwd);
            NetManager.post(mContext, UrlConstant.login_url, parameters, new NetCallBack() {
                @Override
                public void onSuccess(String data) {

                    JSONObject object = JSON.parseObject(data);
                    if (object.getInteger("code") <= 0) {

                        String message = object.getString("message");
                        FLUtil.showShortToast(mContext, message);
                    } else {

                        String auth_token = object.getJSONObject("data").getString("auth_token");
                        UrlConstant.auth_token = auth_token;

                        SPUtils.getInstance().put("username", user);
                        SPUtils.getInstance().put("password", pwd);

                        SocketCallBack callBack = new SocketCallBack(){

                            @Override
                            public void success() {

                                FLLog.i("socket连接成功");

                                ClientManager.currentUserId = user;
                                // 连接成功，创建数据库
                                DbManager.createDb(user);

                                openActivity(HomeActivity.class);
                                finish();
                            }

                            @Override
                            public void fail() {

                            }
                        };
                        FLLog.i("获取登录信息成功");
                        SocketManager.connect(auth_token, callBack);
                    }
                }

                @Override
                public void onError() {

                    FLUtil.showShortToast(mContext,"登录失败！");
                }

                @Override
                public void closeProgressHud() {

                    loadingView.hide();
                }
            });
        }
    }

    @OnClick(R.id.btn_register)
    void register(){

        if (checkInput()) {
            loadingView.show();
            Map parameters = new HashMap();
            final String user = userName.getText().toString();
            final String pwd = password.getText().toString();
            parameters.put("userName", user);
            parameters.put("password", pwd);
            NetManager.post(mContext, UrlConstant.register_url, parameters, new NetCallBack() {
                @Override
                public void onSuccess(String data) {

                    JSONObject object = JSON.parseObject(data);
                    if (object.getInteger("code") < 0) {
                        String message = object.getString("message");
                        FLUtil.showShortToast(mContext, message);
                    }
                    else  {
                        FLUtil.showShortToast(mContext, "注册成功");
                    }
                }

                @Override
                public void onError() {

                    FLUtil.showShortToast(mContext, "注册失败");
                }

                @Override
                public void closeProgressHud() {

                    loadingView.hide();
                }
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }



    @Override
    protected void initView() {
        super.initView();


        userName.setText(SPUtils.getInstance().getString("username"));
        password.setText(SPUtils.getInstance().getString("password"));
    }

    @Override
    protected void initData() {
        super.initData();
        checkPermissions();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        KeyboardUtils.hideSoftInput(this);
        return super.onTouchEvent(event);
    }


    private void checkPermissions() {

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private boolean checkInput() {
        String user = userName.getText().toString();
        String pwd = password.getText().toString();
        if (user.isEmpty()) {

            FLUtil.showShortToast(mContext, "请输入用户名...");
            return false;
        } else if (pwd.isEmpty()) {
            FLUtil.showShortToast(mContext, "请输入密码...");
            return false;
        }
        else  {
            return true;
        }
    }
}
