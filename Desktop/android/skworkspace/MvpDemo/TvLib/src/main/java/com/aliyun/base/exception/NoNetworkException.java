//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.base.exception;

import android.content.Context;
import android.widget.Toast;
import com.aliyun.base.exception.BaseException;

public class NoNetworkException extends BaseException {
    public static final String NETWORK_UNCONNECTED = "Êú™ËøûÊé•ÁΩëÁªúÔºåËØ∑Ê£ÄÊü•ÁΩëÁªúËÆæÁΩÆ";
    public static NoNetworkException.NoNetworkHanler mNoNetworkHanler;

    public NoNetworkException() {
    }

    public NoNetworkException(String message) {
        super(message);
    }

    public NoNetworkException(Throwable cause) {
        super(cause);
    }

    public NoNetworkException(int code, String detailMessage, Throwable throwable) {
        super(code, detailMessage, throwable);
    }

    public NoNetworkException(int code, String errorMessage) {
        super(code, errorMessage);
    }

    public NoNetworkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public boolean handle(Context context) {
        if(mNoNetworkHanler != null) {
            return mNoNetworkHanler.handle(context);
        } else {
            Toast.makeText(context, "Êú™ËøûÊé•ÁΩëÁªúÔºåËØ∑Ê£ÄÊü•ÁΩëÁªúËÆæÁΩÆ", 0).show();
            return true;
        }
    }

    public static void setNoNetworkHanler(NoNetworkException.NoNetworkHanler handler) {
        mNoNetworkHanler = handler;
    }

    public interface NoNetworkHanler {
        boolean handle(Context var1);
    }
}
