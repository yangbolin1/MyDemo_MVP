package com.ccdt.app.tv.mvpdemo.template;

import android.util.Log;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wudz on 2017/3/30.
 */

public class RxFactory {

    public class ObjOiSm extends Observable {
        protected ObjOiSm(OnSubscribe f) {
            super(f);
            Log.i("RxFactory","ObjOiSm");
            observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
        }
    }
    public class ObjSiOm extends Observable{
        protected ObjSiOm(OnSubscribe f) {
            super(f);
            Log.i("RxFactory","ObjSiOm");
            observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        }
    }

}
