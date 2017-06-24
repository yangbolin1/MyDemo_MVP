//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.app.widget.round.RoundedImageView;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_ENTER;

public class FocusImageView extends RoundedImageView implements FocusListener, ItemListener {
    boolean mAimateWhenGainFocusFromDown = true;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mFocusBackground = false;
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    boolean mIsAnimate = true;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());

    public FocusImageView(Context context) {
        super(context);
    }

    public FocusImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FocusImageView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    private boolean checkAnimate(int direction) {
        boolean res;
        switch(direction) {
            case FOCUS_LEFT:
                res = this.mAimateWhenGainFocusFromRight;
                break;
            case FOCUS_UP:
                res = this.mAimateWhenGainFocusFromDown;
                break;
            case FOCUS_RIGHT:
                res = this.mAimateWhenGainFocusFromLeft;
                break;
            case FOCUS_DOWN:
                res = this.mAimateWhenGainFocusFromUp;
                break;
            default:
                res = true;
        }

        return res;
    }

    public boolean canDraw() {
        return true;
    }

    public void drawAfterFocus(Canvas var1) {
    }

    public void drawBeforeFocus(Canvas var1) {
    }

    public FocusRectParams getFocusParams() {
        Rect var1 = new Rect();
        this.getFocusedRect(var1);
        this.mFocusRectparams.set(var1, 0.5F, 0.5F);
        return this.mFocusRectparams;
    }

    public ItemListener getItem() {
        return this;
    }

    public int getItemHeight() {
        return this.getHeight();
    }

    public int getItemWidth() {
        return this.getWidth();
    }

    public Rect getManualPadding() {
        return null;
    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public boolean isAnimate() {
        return this.mIsAnimate;
    }

    public boolean isFinished() {
        return true;
    }

    public boolean isFocusBackground() {
        return this.mFocusBackground;
    }

    public boolean isScale() {
        return true;
    }

    public boolean isScrolling() {
        return false;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mIsAnimate = this.checkAnimate(direction);
    }

    public void onFocusFinished() {
    }

    public void onFocusStart() {
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        boolean var3;
        switch(keyCode) {
            case KEYCODE_DPAD_CENTER:
            case KEYCODE_ENTER:
                var3 = true;
                break;
            default:
                var3 = false;
        }

        return var3;
    }

    public void setAnimateWhenGainFocus(boolean var1, boolean var2, boolean var3, boolean var4) {
        this.mAimateWhenGainFocusFromLeft = var1;
        this.mAimateWhenGainFocusFromUp = var2;
        this.mAimateWhenGainFocusFromRight = var3;
        this.mAimateWhenGainFocusFromDown = var4;
    }

    public void setFocusBackground(boolean var1) {
        this.mFocusBackground = var1;
    }

    @Override
    public Rect getClipFocusRect() {
        return null;
    }
}
