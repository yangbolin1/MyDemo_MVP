package com.ccdt.app.tv.mvpdemo.app;

import android.app.Application;
import android.content.Intent;

import com.blankj.utilcode.utils.Utils;
import com.ccdt.app.tv.mvpdemo.service.MyService;

public class MyApp extends Application {
    private static MyApp instance;
    public static MyApp getInstance()
    {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Utils.init(this);

        Intent i = new Intent(this, MyService.class);
        this.startService(i);
    }

}
