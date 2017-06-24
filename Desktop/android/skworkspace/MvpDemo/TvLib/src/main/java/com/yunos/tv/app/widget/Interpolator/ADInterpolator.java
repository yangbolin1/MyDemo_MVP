//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class ADInterpolator extends TweenInterpolator {
    private float mFactor = 0.5F;

    public ADInterpolator() {
    }

    public ADInterpolator(float factor) {
        this.mFactor = factor;
    }

    private float calcValue(float input) {
        return this.mFactor == 0.5F?input:(input < this.mFactor?input * 0.5F / this.mFactor:0.5F + 0.5F * (input - this.mFactor) / (1.0F - this.mFactor));
    }

    public float getInterpolation(float input) {
        return (float)(Math.cos((double)(this.calcValue(input) + 1.0F) * 3.141592653589793D) / 2.0D) + 0.5F;
    }

    public float interpolation(float t, float b, float c, float d) {
        return this.getInterpolation(1.0F - (d - t) / d) * c + b;
    }
}
