package com.ccdt.app.tv.mvpdemo.model.http;




import com.ccdt.app.tv.mvpdemo.model.bean.FriendMessageList;
import com.ccdt.app.tv.mvpdemo.model.bean.OneNameList;
import com.ccdt.app.tv.mvpdemo.model.bean.User;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:15:33
 * Description:
 */
public interface ApiService {
    @GET("user/search.do")
    Observable<User> getUser(@Query("userId") String uId);
    @GET("user/names")
    Observable<OneNameList> getNames();
    @GET("/friend/search.do")
    Observable<FriendMessageList> getFriendMessageList(@Query("userName") String uName);
}

