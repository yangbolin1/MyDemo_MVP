package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * Created by wudz on 2017/4/27.
 */

public class FocusFrameLayout
        extends FrameLayout
        implements ItemListener,FocusListener
{
    private static final String TAG = "FocusFrameLayout";
    private int mPaddingBottom=0;
    private int mPaddingLeft=0;
    private int mPaddingRight=0;
    private int mPaddingTop=0;
    private boolean mScaleable = true;
    private FocusRectParams focusRectParams=new FocusRectParams();
    protected Params mParams =
            new Params(1.1F, 1.1F, 10, (Interpolator)new AccelerateDecelerateFrameInterpolator(), true, 20, new AccelerateDecelerateFrameInterpolator());
    public FocusFrameLayout(Context paramContext)
    {
        super(paramContext);
    }

    public FocusFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public FocusFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public void drawAfterFocus(Canvas paramCanvas) {}

    @Override
    public void drawBeforeFocus(Canvas paramCanvas) {}

    /**
     * 提供焦点绘制器 参数 系数0.5代表取半之后再放大
     * @return
     */
    @Override
    public FocusRectParams getFocusParams()
    {
        Log.i(TAG,"getPivotX(),getPivotY():"+getPivotX()+" "+getPivotY()+"/n"+
        " getScaleX():"+mParams.getScaleParams().getScaleX()+
        " getScaleY()"+mParams.getScaleParams().getScaleY());
        Rect localRect = getFocusedRect();
        focusRectParams.set(localRect,0.5f,0.5f);
        return focusRectParams;
    }

    @Override
    public Params getParams() {
        Log.i(TAG,"getParams:"+mParams);
        return mParams;
    }
    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public boolean isAnimate() {
        return true;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public boolean isScrolling() {
        return false;
    }



    @Override
    public boolean isFocusBackground() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onFocusStart() {

    }

    @Override
    public void onFocusFinished() {

    }

    @Override
    public Rect getClipFocusRect() {
        return getFocusedRect();
    }

    public void setScale(float scale)
    {
        mParams =
                new Params(scale, scale, 10, (Interpolator)new AccelerateDecelerateFrameInterpolator(), true, 20, new AccelerateDecelerateFrameInterpolator());

    }

    public Rect getFocusedRect()
    {
        Rect localRect = new Rect();
        getFocusedRect(localRect);
        localRect.left += this.mPaddingLeft;
        localRect.right -= this.mPaddingRight;
        localRect.top += this.mPaddingTop;
        localRect.bottom -= this.mPaddingBottom;
        return localRect;
    }

    /**
     * 控件宽等于测量后控件宽
     * @return
     */
    @Override
    public int getItemHeight()
    {
        return getHeight();
    }

    /**
     * 控件长等于测量后绘制长
     * @return
     */
    @Override
    public int getItemWidth()
    {
        return getWidth();
    }

    /**
     * 全界面焦点范围 没有padding
     * @return
     */
    @Override
    public Rect getManualPadding()
    {
        return null;
    }

    @Override
    public boolean isFinished()
    {
        return true;
    }

    /**
     * 覆盖放大效果
     * @return
     */
    @Override
    public boolean isScale()
    {
        return this.mScaleable;
    }



    /**
     * 开启放大效果
     * @return
     */
    public void setScaleable(boolean scaleable)
    {
        this.mScaleable = scaleable;
    }

    public void setSelectorPadding(int panddingLeft, int panddingTop, int panddingRight, int pandingBottom)
    {
        this.mPaddingLeft = panddingLeft;
        this.mPaddingTop = panddingTop;
        this.mPaddingRight = panddingRight;
        this.mPaddingBottom = pandingBottom;
    }

}