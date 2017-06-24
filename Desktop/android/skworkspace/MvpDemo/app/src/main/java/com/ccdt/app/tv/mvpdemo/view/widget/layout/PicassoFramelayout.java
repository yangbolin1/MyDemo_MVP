package com.ccdt.app.tv.mvpdemo.view.widget.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;


import com.ccdt.app.tv.mvpdemo.view.base.IPanelView;
import com.ccdt.app.tv.mvpdemo.view.widget.Config;
import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_Image;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by wudz on 2017/1/19.
 */

public class PicassoFramelayout extends FrameLayout implements IPanelView<PanelData_Image> {
    private static String TAG="PicassoFramelayout";
    private PanelData_Image mBean;
    private static int POST_TIME_SPAN =  Config.SCALE_DRAW_SAPN;
    private Drawable mDefaultBG=null;


    @Override
    public void updateViewData(PanelData_Image bean) {
        Log.i(TAG,"updateViewData:getUrl():"+bean.getUrl());
        this.setBackGroundFromWeb(bean.getUrl());
        mBean=bean;
    }

    @Override
    public PanelData_Image getViewData() {
        return mBean;
    }


    public enum ShowStatus {
        DEFAULT, NORMAL
    }
    public PicassoFramelayout(Context context) {
        super(context);
        init();
    }
    public PicassoFramelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public PicassoFramelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    void init()
    {
        mDefaultBG = getBackground();
    }
    public void setBackGroundFromWeb(String url) {
        if(url==null || "".equals(url))
            return;
        Drawable bkg = getBackground();
        if (mDefaultBG == null) {
            if (bkg != null)
                Picasso.with(getContext()).load(url).error(bkg)
                        .placeholder(bkg).into(mBackGroundTarget);
            else
                Picasso.with(getContext()).load(url).into(mBackGroundTarget);
            return;
        }
        else
        {
            Picasso.with(getContext()).load(url).error(mDefaultBG)
                    .placeholder(mDefaultBG).into(mBackGroundTarget);
            return;
        }
    }
    private Target mBackGroundTarget = new Target() {
        @Override
        public void onPrepareLoad(Drawable arg0) {
        }
        @Override
        public void onBitmapLoaded(Bitmap arg0, Picasso.LoadedFrom arg1) {

           final BitmapDrawable mNormalBG = new BitmapDrawable(getResources(), arg0);
            Observable.just(mNormalBG).subscribe(new Action1<Drawable>() {
                @Override
                public void call(Drawable drawable) {
                    PicassoFramelayout.this.setBackground(mNormalBG);
                }
            });
        }
        @Override
        public void onBitmapFailed(Drawable arg0) {

        }
    };
}
