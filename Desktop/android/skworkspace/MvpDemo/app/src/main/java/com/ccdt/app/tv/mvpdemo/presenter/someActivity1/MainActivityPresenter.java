package com.ccdt.app.tv.mvpdemo.presenter.someActivity1;


import android.util.Log;

import com.ccdt.app.tv.mvpdemo.model.bean.User;
import com.ccdt.app.tv.mvpdemo.model.http.Api;
import com.ccdt.app.tv.mvpdemo.template.RxFactory;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:16:04
 * Description:
 */
public class MainActivityPresenter extends MainActivityContract.Presenter {

    private static String TAG = "MainActivityPresenter";

    @Override
    public void getData() {
        mSub.add(Observable.timer(2, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<User>>() {
            @Override
            public Observable<User> call(Long aLong) {
                Log.i(TAG,"do getuser!");
                Log.i(TAG,""+Thread.currentThread().getId());
                return Api.getInstance().getUser("123");
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                Log.i(TAG,"call:"+Thread.currentThread().getId());
               getView().onUserLogin(user.getName(),user.getPassword());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                if(getView()!=null)
                {
                    //show error!
                }
            }
        }));

    }
}
