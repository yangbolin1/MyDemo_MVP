//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public abstract class AbsInterpolatorHelper {
    private static final String TAG = "InterpolatorUtil";
    public static final int STATUS_FORWARDING = 1;
    public static final int STATUS_FORWARD_FINISHED = 2;
    public static final int STATUS_REVERSEING = 3;
    public static final int STATUS_REVERSE_FINISHED = 4;
    protected int mStatus = 4;
    protected int mForwardDelay;
    protected int mReverseDelay;
    protected int mCurrent = 0;
    protected TweenInterpolator mInterpolator;

    public AbsInterpolatorHelper() {
    }

    public AbsInterpolatorHelper(TweenInterpolator interpolator) {
        this.setInterpolator(interpolator);
    }

    public void setInterpolator(TweenInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public TweenInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public void setForwardDelay(int delay) {
        this.mForwardDelay = delay;
    }

    public int getForwardDelay() {
        return this.mForwardDelay;
    }

    public void setReverseDelay(int delay) {
        this.mReverseDelay = delay;
    }

    public int getReverseDelay() {
        return this.mReverseDelay;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public boolean isRunning() {
        return this.mStatus == 1 || this.mStatus == 3;
    }

    public abstract boolean track();

    public abstract boolean start();

    public abstract boolean reverse();

    public float getCurrent() {
        return this.mStatus != 1 && this.mStatus != 2?this.mInterpolator.getReverseValue(this.mCurrent):this.mInterpolator.getValue(this.mCurrent);
    }

    public float getStatusTarget() {
        return this.mStatus != 1 && this.mStatus != 2?this.mInterpolator.getStart():this.mInterpolator.getTarget();
    }
}
