//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.base.exception;

import android.content.Context;

public abstract class BaseException extends Exception {
    protected int code;
    protected String errorMessage;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorMessage, Throwable throwable) {
        super(throwable);
        this.errorMessage = errorMessage;
    }

    public BaseException(int code, String errorMessage, Throwable throwable) {
        super(throwable);
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return this.errorMessage != null?this.errorMessage:super.getMessage();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public abstract boolean handle(Context var1);
}
