package com.ccdt.app.tv.mvpdemo.model.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:14:57
 * Description:Api基础类
 */
public class BaseApi {

    // 公司测试环境(亚敏服务器)
    // private static final String URL_BASE = "http://192.167.1.6:15414/";
    // 公司测试环境
    //private static final String URL_BASE = "http://10.10.6.62:15414/";
    //陕西现场

    private static final String TAG = "BaseApi";


    private static Retrofit mRetrofit;

    protected static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            OkHttpClient client;
            if (Config.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                client = (new OkHttpClient.Builder()).readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(logging).build();
            } else {
                client = (new OkHttpClient.Builder()).readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).build();
            }
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Config.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return mRetrofit;
    }

    private static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
//            if (!NetworkUtils.isConnected()) {
//                throw new NoNetworkException("无网络连接");
//            }
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "keep-alive")
                    .build();
            return chain.proceed(request);
        }
    };


}
