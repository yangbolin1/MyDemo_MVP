package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.Interpolator.Bounce;
import com.yunos.tv.app.widget.Interpolator.Elastic;
import com.yunos.tv.app.widget.Interpolator.Expo;
import com.yunos.tv.app.widget.Interpolator.Linear;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * Created by wudz on 2017/6/2.
 */

public class FocusPopupFrameLayout extends FocusFrameLayout {
    private static final String TAG="FocusPopupFrameLayout";
    private FocusRectParams focusRectParams=new FocusRectParams();
    private Params mParams =
            new Params(1.1F, 1.2F, 10, (Interpolator)new AccelerateDecelerateFrameInterpolator(), true, 20, new AccelerateDecelerateFrameInterpolator());

    public FocusPopupFrameLayout(Context paramContext) {
        super(paramContext);
        init();
    }

    public FocusPopupFrameLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public FocusPopupFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }
    private void init()
    {
        setClipChildren(false);
        setClipToPadding(false);
    }

    /**
     * 提供焦点绘制器 参数 系数0.5代表取半之后再放大
     * @return
     */
    @Override
    public FocusRectParams getFocusParams()
    {
        Log.i(TAG,"getPivotX(),getPivotY():"+getPivotX()+" "+getPivotY()+"\n"+
                " getScaleX():"+mParams.getScaleParams().getScaleX()+
                " getScaleY()"+mParams.getScaleParams().getScaleY());
        Rect localRect = getFocusedRect();
        Log.i(TAG,"localRect:"+localRect);
        Rect popRect=new Rect();
        popRect.set(localRect.left,localRect.top+(int)(localRect.height()*0.2f),localRect.right,localRect.bottom);
        Log.i(TAG,"popRect:"+popRect);
        setPivotX(getItemWidth()/2);
        setPivotY(getItemHeight());
        focusRectParams.set(popRect,0.5f,1f);
        return focusRectParams;
    }

    @Override
    public Params getParams() {
        return mParams;
    }

}
