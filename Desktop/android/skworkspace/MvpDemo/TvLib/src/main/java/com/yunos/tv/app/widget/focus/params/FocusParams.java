//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.params;

import android.view.animation.Interpolator;

public class FocusParams {
    private boolean mFocusVisible = true;
    private int mFocusFrameRate = 5;
    private Interpolator mFocusInterpolator = null;

    public FocusParams(boolean focusVisible, int focusFrameRate, Interpolator interpolator) {
        this.mFocusVisible = focusVisible;
        this.mFocusFrameRate = focusFrameRate;
        this.mFocusInterpolator = interpolator;
    }

    public void setFocusVisible(boolean visible) {
        this.mFocusVisible = visible;
    }

    public boolean isFocusVisible() {
        return this.mFocusVisible;
    }

    public void setFocusFrameRate(int rate) {
        this.mFocusFrameRate = rate;
    }

    public int getFocusFrameRate() {
        return this.mFocusFrameRate;
    }

    public Interpolator getFocusInterpolator() {
        return this.mFocusInterpolator;
    }
}
