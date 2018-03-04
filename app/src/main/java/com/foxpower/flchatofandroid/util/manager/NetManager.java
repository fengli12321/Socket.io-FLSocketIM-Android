package com.foxpower.flchatofandroid.util.manager;

import android.content.Context;

import com.foxpower.flchatofandroid.callBack.NetCallBack;
import com.foxpower.flchatofandroid.util.constant.UrlConstant;
import com.foxpower.flchatofandroid.util.other.FLUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;


/**
 * Created by fengli on 2018/2/5.
 */

public class NetManager {


    public enum RequestType{
        Get, Post
    }

    public static void get(Context context, String url, Map<String, String>parameters, final NetCallBack callBack) {

        request(RequestType.Get, context, url, parameters, callBack);
    }

    public static void post(Context context, String url, Map<String, String>parameters, NetCallBack callBack) {

        request(RequestType.Post, context, url, parameters, callBack);
    }

    private static void request(RequestType requestType, Context context, String url, Map<String, String>parameters, final NetCallBack callBack) {
        if (!FLUtil.netIsConnect(context)) {

            FLUtil.showShortToast(context, "网络未连接！");
            return;
        }
        final String requestUrl = UrlConstant.baseUrl + "/" + url;
        if (requestType == RequestType.Get) {

            OkHttpUtils.get()
                    .tag(context)
                    .url(requestUrl)
                    .params(parameters)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                            callBack.closeProgressHud();
                            callBack.onError();
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            callBack.closeProgressHud();
                            callBack.onSuccess(response);
                        }
                    });
        } else if (requestType == RequestType.Post) {

            OkHttpUtils.post()
                    .url(requestUrl)
                    .tag(context)
                    .params(parameters)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                            callBack.closeProgressHud();
                            callBack.onError();
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            callBack.closeProgressHud();
                            callBack.onSuccess(response);
                        }
                    });
        }

    }
}
