//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.params;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.yunos.tv.app.widget.focus.params.AlphaParams;
import com.yunos.tv.app.widget.focus.params.FocusParams;
import com.yunos.tv.app.widget.focus.params.ScaleParams;

public class Params {
    ScaleParams mScaleParams = null;
    FocusParams mFocusParams = null;
    AlphaParams mAlphaParams = new AlphaParams(20, 0.0F, 1.0F, new AccelerateDecelerateInterpolator());

    /**
     * 设置item的所有变量 在自定义控件初始化时候可以 设置
     * @param scaleX 放大参数x
     * @param scaleY 放大参数y
     * @param scaleFrameRate 放大动画帧率
     * @param scaleInterpolator 放大动画差值器
     * @param focusVisible 焦点是否可见
     * @param focusFrameRate 焦点帧率
     * @param focusInterpolator 焦点动画插值器
     */
    public Params(float scaleX, float scaleY, int scaleFrameRate, Interpolator scaleInterpolator, boolean focusVisible, int focusFrameRate, Interpolator focusInterpolator) {
        this.mScaleParams = new ScaleParams(scaleX, scaleY, scaleFrameRate, scaleInterpolator);
        this.mFocusParams = new FocusParams(focusVisible, focusFrameRate, focusInterpolator);
    }

    public Params(float scaleX, float scaleY, int scaleFrameRate, Interpolator scaleInterpolator, boolean focusVisible, int focusFrameRate, Interpolator focusInterpolator, int alphaFrameRate, float fromAlpha, float toAlpha, Interpolator alphaInterpolator) {
        this.mScaleParams = new ScaleParams(scaleX, scaleY, scaleFrameRate, scaleInterpolator);
        this.mFocusParams = new FocusParams(focusVisible, focusFrameRate, focusInterpolator);
        this.mAlphaParams = new AlphaParams(alphaFrameRate, fromAlpha, toAlpha, alphaInterpolator);
    }

    public Params(ScaleParams scaleParams, FocusParams focusParams, AlphaParams alphaParams) {
        this.mScaleParams = scaleParams;
        this.mFocusParams = focusParams;
        this.mAlphaParams = alphaParams;
    }

    public ScaleParams getScaleParams() {
        return this.mScaleParams;
    }

    public FocusParams getFocusParams() {
        return this.mFocusParams;
    }

    public AlphaParams getAlphaParams() {
        return this.mAlphaParams;
    }
}
