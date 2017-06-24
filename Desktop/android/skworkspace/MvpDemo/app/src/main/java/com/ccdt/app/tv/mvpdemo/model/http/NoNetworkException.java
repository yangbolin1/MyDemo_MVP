package com.ccdt.app.tv.mvpdemo.model.http;

/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:15:25
 * Description:无网络连接异常
 */
public class NoNetworkException extends RuntimeException {
    public NoNetworkException(String message) {
        super(message);
    }
}
