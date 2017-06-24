package com.ccdt.app.tv.mvpdemo.presenter.MvpSimple1Activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.model.bean.FriendMessageList;
import com.ccdt.app.tv.mvpdemo.model.bean.OneNameList;
import com.ccdt.app.tv.mvpdemo.model.http.Api;
import com.ccdt.app.tv.mvpdemo.view.activity.FocusComplexActivity;
import com.ccdt.app.tv.mvpdemo.view.activity.FocusManagerActivity;
import com.ccdt.app.tv.mvpdemo.view.bean.GridViewBean;
import com.ccdt.app.tv.mvpdemo.view.bean.MenuBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wudz on 2017/5/17.
 */

public class MvpSimpleActivityPresenter extends MvpSimpleActivityContract.Presenter{
    private static final String TAG="MvpSimpleActivityPresenter";
    private WeakReference<Context> mContextWeak;
    public MvpSimpleActivityPresenter(Context context)
    {
        mContextWeak=new WeakReference<Context>(context);
    }
    @Override
    public void getMenuData() {
           //getView().onMenuData();
        mSub.add(Api.getInstance().getNames()
                .flatMap(new Func1<OneNameList, Observable<MenuBean>>() {
            @Override
            public Observable<MenuBean> call(OneNameList oneNameList) {
                Log.i(TAG,"oneNameList:"+oneNameList);
                ArrayList<MenuBean.MenuItemBean> arrayList=new ArrayList<MenuBean.MenuItemBean>();
                //这里拼装数据
                MenuBean.MenuItemBean item;
                Intent i;
                for (String name : oneNameList.names) {
                    item=new MenuBean.MenuItemBean("",name);
                    if(name.equals("foo0"))
                    {
                         i=new Intent(mContextWeak.get(),FocusManagerActivity.class);
                    }
                    else
                    {
                         i=new Intent(mContextWeak.get(),FocusComplexActivity.class);
                    }
                    item.putData("CLICK_INTENT",i);
                    arrayList.add(item);
                }
                MenuBean bean=new MenuBean("",arrayList);
                return Observable.just(bean);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MenuBean>() {
            @Override
            public void call(MenuBean bean) {
               getView().onMenuData(bean);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        }));
    }

    @Override
    public void getGridViewData(MenuBean.MenuItemBean itemData) {
        String name=itemData.getItemText();
        Log.i(TAG,"getGridViewData:namge:"+name);
        mSub.add(Api.getInstance().getFriendMessageList(name).flatMap(new Func1<FriendMessageList, Observable<GridViewBean>>() {
            @Override
            public Observable<GridViewBean> call(FriendMessageList friendMessageList) {
                ArrayList<GridViewBean.GridItemBean> gridList =new ArrayList<GridViewBean.GridItemBean>();
                GridViewBean.GridItemBean item;
                for(int i=0;i<friendMessageList.names.size();i++)
                {
                    item=new GridViewBean.GridItemBean("",friendMessageList.names.get(i),friendMessageList.picId.get(i));
                    switch (i%3)
                    {
                        case 0:
                            item.setData("打开 http://xxxx.xxxxx");
                            break;
                        case 1:
                            item.setData("打开 Activity 001");
                            break;
                        case 2:
                            item.setData("弹出吐司！");
                            break;
                    }
                    gridList.add(item);
                }
                return Observable.just(new GridViewBean("",gridList));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GridViewBean>() {
            @Override
            public void call(GridViewBean gridViewBean) {
                getView().onGridViewData(gridViewBean);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        }));

    }

    @Override
    public void doGridClick(GridViewBean.GridItemBean itemData) {
        String message=itemData.getData();
        ToastUtils.showShortToast(message);
    }

    @Override
    public void doMenuClick(MenuBean.MenuItemBean itemData) {
        Context context=mContextWeak.get();
        Intent it=itemData.getData("CLICK_INTENT");
        if(context!=null && it!=null)
        {
            context.startActivity(it);
        }
    }
}
