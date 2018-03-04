package com.foxpower.flchatofandroid.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.ui.view.NavigationBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fengli on 2018/2/5.
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected Context mContext;

    private NavigationBar navigationBar;

    private Unbinder unbinder;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        init();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected abstract int getLayoutId();

    private void init(){
        mContext = this;
        navigationBar = (NavigationBar) findViewById(R.id.navigation_bar);
        if (navigationBar != null) {
            navigationBar.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }
        unbinder = ButterKnife.bind(this);
    }

    protected void initView() {

    }

    protected void initData() {

    }

    public void openActivity(Class aClass){

        openActivity(aClass, null);
    }

    public void openActivity(Class aClass, Bundle bundle) {

        Intent intent = new Intent(this, aClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);

    }

    protected void setTitle(String title) {

        if (navigationBar != null)navigationBar.setTitle(title);
    }

    protected <T> View addRight(T item, NavigationBar.clickCallBack callBack) {
        if (navigationBar != null) return navigationBar.addRight(item, callBack);
        return null;
    }

    protected <T> View addLeft(T item, NavigationBar.clickCallBack callBack) {
        if (navigationBar != null) return navigationBar.addLeft(item, callBack);
        return null;
    }

    protected void hasBack(boolean has) {

        if (navigationBar != null) navigationBar.setHasBack(has);
    }

    protected void goBack() {
        finish();
    }

}
