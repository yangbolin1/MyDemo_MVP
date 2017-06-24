package com.ccdt.app.tv.mvpdemo.template;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by wudz on 2017/3/29.
 */

public class Singleton<T extends Singleton> {
    public interface ISingleton
    {
        void init(Context context);
    }
    private static HashMap<String,ISingleton> gSingletonMap=new HashMap<>();
    public static <E extends ISingleton> E getInstance() {

      return null;
    }
}
