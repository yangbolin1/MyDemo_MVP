package com.ccdt.app.tv.mvpdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.model.bean.FriendMessageList;
import com.ccdt.app.tv.mvpdemo.model.bean.OneNameList;
import com.ccdt.app.tv.mvpdemo.model.bean.User;
import com.ccdt.app.tv.mvpdemo.view.bean.GridViewBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;




/**
 * Created by wudz on 2017/3/29.
 */

public class MyService extends Service {
    private static final String TAG="MyService";
    private HttpServer httpServer;
    Gson gson = new Gson();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        httpServer=new HttpServer(8080);
        Log.i(TAG,"httpServer start!");
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class HttpServer extends NanoHTTPD
    {
        public HttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            Log.d(TAG, "serve:uri:" + uri + " Method:" + session.getMethod() +
                    " ip:" + session.getRemoteIpAddress() + " host name:" + session.getRemoteHostName()
            + " params:"+session.getParameters());
            switch (session.getMethod()) {
                case  GET:
                    switch (uri) {
                        case "/user/search.do": {
                            List<String> userIds=session.getParameters().get("userId");
                            if(userIds.size()==0)
                                break;
                            String json = gson.toJson(new User(userIds.get(0), "用户名：小明", "密码：123456"));
                            return Response.newFixedLengthResponse(json);
                        }
                        case "/user/names":{
                            OneNameList onnamelist=new OneNameList();
                            for(int i=0;i<20;i++)
                            {
                                onnamelist.names.add("foo"+(i%2));
                            }
                            String json=gson.toJson(onnamelist);
                            return Response.newFixedLengthResponse(json);
                        }
                        case "/friend/search.do":
                        {
                            List<String> userIds=session.getParameters().get("userName");
                            Log.i(TAG,"userIds:"+userIds);
                            if(userIds.size()==0)
                                break;
                            FriendMessageList list=new FriendMessageList();

                            switch (userIds.get(0))
                            {
                                case "foo0":
                                    for(int i=0;i<100;i++)
                                    {
                                        list.names.add("foo0的朋友："+i);
                                        list.picId.add(R.drawable.grid_0);
                                    }
                                   break;
                                case "foo1":
                                    for(int i=0;i<100;i++)
                                    {
                                        list.names.add("foo1的朋友："+i);
                                        list.picId.add(R.drawable.grid_1);
                                    }
                                   break;
                            }
                            String json=gson.toJson(list);
                            return Response.newFixedLengthResponse(json);
                        }
                    }
                    break;
                default:
                   break;
            }
            return Response.newFixedLengthResponse("{}");
        }
    }
}
