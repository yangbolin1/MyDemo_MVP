package com.ccdt.app.tv.mvpdemo.model.http;

import com.blankj.utilcode.utils.ToastUtils;
import rx.functions.Func1;


/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:15:29
 * Description:
 */
public class HttpErrorFunc<T> implements Func1<Throwable, T> {

    @Override
    public T call(Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof NoNetworkException){
            ToastUtils.showShortToastSafe("网络不可用");
        } else {
            ToastUtils.showShortToastSafe("网络连接错误，请稍后重试");
        }
        return null;
    }
}
