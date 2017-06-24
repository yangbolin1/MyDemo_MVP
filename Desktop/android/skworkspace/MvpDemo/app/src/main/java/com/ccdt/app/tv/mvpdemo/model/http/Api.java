package com.ccdt.app.tv.mvpdemo.model.http;


import com.ccdt.app.tv.mvpdemo.model.bean.FriendMessageList;
import com.ccdt.app.tv.mvpdemo.model.bean.OneNameList;
import com.ccdt.app.tv.mvpdemo.model.bean.User;

import rx.Observable;

/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:15:29
 * Description:
 */
public class Api extends BaseApi {

    private static Api mInstance;
    private ApiService mService;

    private Api() {
        mService = getRetrofit().create(ApiService.class);
    }

    public static Api getInstance() {
        if (mInstance == null) {
            synchronized (Api.class) {
                if (mInstance == null) {
                    mInstance = new Api();
                }
            }
        }
        return mInstance;
    }

    /**
     * 通过id 查询用户信息
     * @param sId
     * @return
     */
    public Observable<User> getUser(String sId)
    {
        return mService.getUser(sId);
    }

    /**
     * 获取名字列表
     * @return
     */
    public Observable<OneNameList> getNames()
    {
        return mService.getNames();
    }

    /**
     * 获取朋友名字
     * @param uName
     * @return
     */
    public Observable<FriendMessageList> getFriendMessageList(String uName)
    {
        return mService.getFriendMessageList(uName);
    }

}
