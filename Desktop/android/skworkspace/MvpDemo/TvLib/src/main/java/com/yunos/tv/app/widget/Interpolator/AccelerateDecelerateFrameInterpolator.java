//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class AccelerateDecelerateFrameInterpolator extends TweenInterpolator {
    private float mScale = 10.0F;
    private double mCoef;
    private float mExponent = 2.0F;

    public AccelerateDecelerateFrameInterpolator() {
        this.init();
    }

    public AccelerateDecelerateFrameInterpolator(float scale, float exponent) {
        this.mScale = scale;
        this.mExponent = exponent;
        this.init();
    }

    public float getInterpolation(float input) {
        return (float)(Math.atan(Math.pow((double)input, (double)this.mExponent) * (double)this.mScale) * this.mCoef);
    }

    private void init() {
        this.mCoef = 1.0D / Math.atan((double)this.mScale);
    }

    public float interpolation(float t, float b, float c, float d) {
        return this.getInterpolation(1.0F - (d - t) / d) * c + b;
    }
}
