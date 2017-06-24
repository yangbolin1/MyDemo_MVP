//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_ENTER;

public class FocusTextView extends TextView implements FocusListener, ItemListener {
    private static final  String TAG = "FocusTextView";
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    boolean mFocusBackground = false;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;
    boolean mIsAnimate = true;

    public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context) {
        super(context);
    }

    /**
     * 设置从哪个角度获得焦点不显示动画
     * @param fromleft
     * @param fromUp
     * @param fromRight
     * @param fromDown
     */
    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    /**
     * 设置焦点是否在控件绘制前先绘制 如果为真，先绘制焦点。
     * @param back
     */
    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }



    private boolean checkAnimate(int direction) {
        switch(direction) {
            case FOCUS_LEFT:
                return this.mAimateWhenGainFocusFromRight;
            case FOCUS_UP:
                return this.mAimateWhenGainFocusFromDown;
            case FOCUS_RIGHT:
                return this.mAimateWhenGainFocusFromLeft;
            case FOCUS_DOWN:
                return this.mAimateWhenGainFocusFromUp;
            default:
                return true;
        }
    }

    @Override//ItemListener
    public boolean isScale() {
        return false;
    }

    @Override//ItemListener //View
    public float getScaleX() {
        Log.i(TAG,"getScaleX");
        return super.getScaleX();
    }

    @Override//ItemListener //View
    public void setScaleX(float scaleX) {
        Log.i(TAG,"setScaleX:"+scaleX);
        super.setScaleX(scaleX);
    }

    @Override//ItemListener //View
    public float getScaleY() {
        Log.i(TAG,"getScaleY");
        return super.getScaleY();
    }

    @Override//ItemListener //View
    public void setScaleY(float scaleY) {
        Log.i(TAG,"setScaleY:"+scaleY);
        super.setScaleY(scaleY);
    }


    public int getItemWidth() {
        return this.getWidth();
    }

    public int getItemHeight() {
        return this.getHeight();
    }

    public Rect getManualPadding() {
        return null;
    }

    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        this.getFocusedRect(r);
        this.mFocusRectparams.set(r, 0.5F, 0.5F);
        return this.mFocusRectparams;
    }

    public boolean canDraw() {
        return true;
    }

    public boolean isAnimate() {
        return this.mIsAnimate;
    }

    public ItemListener getItem() {
        return this;
    }

    public boolean isScrolling() {
        return false;
    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        boolean res;
        switch(keyCode) {
            case KEYCODE_DPAD_CENTER:
            case KEYCODE_ENTER:
                res = true;
                break;
            default:
                res = false;
        }
        return res;
    }

    public void onFocusStart() {
        Log.i(TAG,"onFocusStart");
    }

    public void onFocusFinished() {
        Log.i(TAG,"onFocusFinished");
    }

    @Override //FocusListener
    public boolean isFocusBackground() {
        return this.mFocusBackground;
    }
    @Override //View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mIsAnimate = this.checkAnimate(direction);
    }
    public void drawBeforeFocus(Canvas canvas) {
    }

    public void drawAfterFocus(Canvas canvas) {
    }

    public boolean isFinished() {
        return true;
    }

    public Rect getClipFocusRect() {
        return null;
    }


}
