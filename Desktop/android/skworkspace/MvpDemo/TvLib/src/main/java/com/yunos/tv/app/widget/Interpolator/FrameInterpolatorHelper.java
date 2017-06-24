//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import android.util.Log;
import com.yunos.tv.app.widget.Interpolator.AbsInterpolatorHelper;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class FrameInterpolatorHelper extends AbsInterpolatorHelper {
    private static final String TAG = "FrameInterpolator";
    private int mStartFrame = 0;
    private int mTotalFrames = 0;

    public FrameInterpolatorHelper() {
    }

    public FrameInterpolatorHelper(TweenInterpolator interpolator) {
        super(interpolator);
    }

    public boolean track() {
        if(this.isRunning()) {
            ++this.mTotalFrames;
            boolean isTrack = this.mCurrent < this.mInterpolator.getDuration();
            if(isTrack) {
                this.mCurrent = this.mTotalFrames - this.mStartFrame;
                if(this.mCurrent < 0) {
                    this.mCurrent = 0;
                }

                if(this.mCurrent >= this.mInterpolator.getDuration()) {
                    this.mCurrent = this.mInterpolator.getDuration();
                }
            } else {
                Log.d("FrameInterpolator", "totalFrames:" + (this.mTotalFrames - 1) + ",mStartFrame:" + this.mStartFrame);
                this.mStatus = this.mStatus == 1?2:4;
            }

            return isTrack;
        } else {
            return false;
        }
    }

    public synchronized boolean start() {
        if(this.mStatus != 2 && this.mStatus != 1) {
            this.mStartFrame = 0;
            if(this.mStatus == 3) {
                int passed = this.mInterpolator.getDuration() - this.mCurrent;
                this.mStartFrame = -passed;
            } else {
                this.mStartFrame = this.mForwardDelay;
            }

            this.mStatus = 1;
            this.mCurrent = 0;
            this.mTotalFrames = 0;
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean reverse() {
        if(this.mStatus != 3 && this.mStatus != 4) {
            if(this.mStatus == 1) {
                int passed = this.mInterpolator.getDuration() - this.mCurrent;
                this.mStartFrame = -passed;
            } else {
                this.mStartFrame = this.mReverseDelay;
            }

            this.mStatus = 3;
            this.mCurrent = 0;
            this.mTotalFrames = 0;
            return true;
        } else {
            return false;
        }
    }
}
