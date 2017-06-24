//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import android.view.animation.Interpolator;

public abstract class TweenInterpolator implements Interpolator {
    private static final String TAG = "TweenInterpolator";
    private float mStart;
    private float mTarget;
    private int mDuration;
    private float mChange;

    public TweenInterpolator() {
    }

    public TweenInterpolator(float start, float target, int duration) {
        this.mStart = start;
        this.mTarget = target;
        this.mDuration = duration;
        this.mChange = this.mTarget - this.mStart;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public float getStart() {
        return this.mStart;
    }

    public float getTarget() {
        return this.mTarget;
    }

    public void setStartAndTarget(float start, float target) {
        this.mStart = start;
        this.mTarget = target;
        this.mChange = this.mTarget - this.mStart;
    }

    public float getInterpolation(float input) {
        return this.interpolation(input, 0.0F, 1.0F, 1.0F);
    }

    public float getValue(int current) {
        return this.interpolation((float)current, this.mStart, this.mChange, (float)this.mDuration);
    }

    public float getReverseValue(int current) {
        return this.interpolation((float)(this.mDuration - current), this.mStart, this.mChange, (float)this.mDuration);
    }

    public abstract float interpolation(float var1, float var2, float var3, float var4);
}
