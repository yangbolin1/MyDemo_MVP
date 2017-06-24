//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import android.os.SystemClock;
import com.yunos.tv.app.widget.Interpolator.AbsInterpolatorHelper;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class TimeInterpolatorHelper extends AbsInterpolatorHelper {
    private static final String TAG = "TimeInterpolator";
    private long mStartTime = 0L;

    public TimeInterpolatorHelper() {
    }

    public TimeInterpolatorHelper(TweenInterpolator interpolator) {
        super(interpolator);
    }

    public boolean track() {
        if(this.isRunning()) {
            boolean isTrack = this.mCurrent < this.mInterpolator.getDuration();
            if(isTrack) {
                this.mCurrent = (int)(SystemClock.elapsedRealtime() - this.mStartTime);
                if(this.mCurrent < 0) {
                    this.mCurrent = 0;
                }

                if(this.mCurrent >= this.mInterpolator.getDuration()) {
                    this.mCurrent = this.mInterpolator.getDuration();
                }
            } else {
                this.mStatus = this.mStatus == 1?2:4;
            }

            return isTrack;
        } else {
            return false;
        }
    }

    public synchronized boolean start() {
        if(this.mStatus != 2 && this.mStatus != 1) {
            this.mStartTime = SystemClock.elapsedRealtime();
            if(this.mStatus == 3) {
                int passed = this.mInterpolator.getDuration() - this.mCurrent;
                this.mStartTime -= (long)passed;
            } else {
                this.mStartTime += (long)this.mForwardDelay;
            }

            this.mStatus = 1;
            this.mCurrent = 0;
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean reverse() {
        if(this.mStatus != 3 && this.mStatus != 4) {
            this.mStartTime = SystemClock.elapsedRealtime();
            if(this.mStatus == 1) {
                int passed = this.mInterpolator.getDuration() - this.mCurrent;
                this.mStartTime -= (long)passed;
            } else {
                this.mStartTime += (long)this.mReverseDelay;
            }

            this.mStatus = 3;
            this.mCurrent = 0;
            return true;
        } else {
            return false;
        }
    }
}
