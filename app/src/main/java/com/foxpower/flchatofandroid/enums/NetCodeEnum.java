package com.foxpower.flchatofandroid.enums;

/**
 * Created by fengli on 2018/2/5.
 */

public enum NetCodeEnum {

    NoneNetWork(0, "网络未连接");

    private int code;
    private String value;
    private NetCodeEnum(int code, String value) {

        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }


}
