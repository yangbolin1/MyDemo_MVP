package com.ccdt.app.tv.mvpdemo.view.base;


import java.util.HashMap;
import java.util.Objects;

/**
 * Created by wudz on 2017/1/13.
 */

public abstract class ViewData implements IViewDataBean {
    public String mId;
    public ViewData(String id)
    {
        mId=id;
    }
    public String getId()
    {
        return mId;
    }

    /**
     * 多个对象数据缓存区
     */
    HashMap<String,Object> dataMap;
    public <T extends Object> void putData(String key,T data)
    {
        if(dataMap==null)
            dataMap=new HashMap<String,Object>();
        dataMap.put(key,data);
    }

    public <T extends Object> T getData(String key)
    {
        if(dataMap==null)
            throw new RuntimeException("data is null!");
        Object res=dataMap.get(key);
        if(res==null)
            throw new RuntimeException("data is null!dataMap not found!");
        return (T)res;
    }

    /**
     * 单个对象数据缓存区
     */
    Object mData;
    public <T extends Object> void setData(T data)
    {
        mData=data;
    }
    public <T extends Object>T getData()
    {
        return (T)mData;
    }

}
