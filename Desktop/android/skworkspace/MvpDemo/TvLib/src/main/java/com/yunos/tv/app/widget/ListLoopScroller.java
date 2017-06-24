package com.yunos.tv.app.widget;

import android.util.Log;

public class ListLoopScroller {
    private final String TAG = "ListLoopScroller";
    private final boolean DEBUG = true;
    public final float DEFALUT_MAX_STEP = 50.0F;
    public final float SLOW_DOWN_RATIO = 3.0F;
    public final float SLOW_DOWN_DISTANCE_RATIO = 4.0F;
    private int mStart;
    private int mCurr;
    private int mFinal;
    private boolean mFinished = true;
    private float mMaxStep = 50.0F;
    private int mCurrFrameIndex;
    private int mTotalFrameCount;
    private float mStep;
    private int mSlowDownIndex;
    private float mSlowDownFrameCount;
    private int mSlowDownStart;
    private float mSlowDownDistance;
    private float mSlowDownStep;
    private long mStartTime;
    private float mSlowDownRatio = 4.0F;

    public ListLoopScroller() {
    }

    public int getCurr() {
        return this.mCurr;
    }

    public int getFinal() {
        return this.mFinal;
    }

    public int getStart() {
        return this.mStart;
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public void setMaxStep(float maxStep) {
        this.mMaxStep = maxStep;
    }

    public void setSlowDownRatio(float ratio) {
        if(!this.mFinished) {
            throw new IllegalStateException("setSlowDownRatio before start");
        } else {
            if(this.mSlowDownRatio > 1.0F) {
                this.mSlowDownRatio = ratio;
            } else {
                Log.e("ListLoopScroller", "setSlowDownRatio value must > 1.0");
            }

        }
    }

    public void startScroll(int start, int distance, int frameCount) {
        distance += this.mFinal - this.mCurr;
        this.mTotalFrameCount = frameCount;
        this.mStep = (float)distance / (float)this.mTotalFrameCount;
        if(this.mStep > this.mMaxStep) {
            this.mStep = this.mMaxStep;
            this.mTotalFrameCount = (int)((float)distance / this.mStep);
        } else if(this.mStep < -this.mMaxStep) {
            this.mStep = -this.mMaxStep;
            this.mTotalFrameCount = (int)((float)distance / this.mStep);
        }

        this.mCurr = start;
        this.mStart = start;
        this.mFinal = this.mStart + distance;
        this.mFinished = false;
        this.mCurrFrameIndex = 0;
        this.mSlowDownFrameCount = 0.0F;
        this.mSlowDownStep = this.mStep / 3.0F;
        this.computeSlowDownDistance();
    }

    public boolean computeScrollOffset() {
        if(this.mFinished) {
            return false;
        } else if(this.mCurrFrameIndex >= this.mTotalFrameCount) {
            this.finish();
            return false;
        } else {
            if(this.mSlowDownFrameCount > 0.0F) {
                if((float)this.mSlowDownIndex > this.mSlowDownFrameCount) {
                    this.finish();
                    return false;
                }

                ++this.mSlowDownIndex;
                if((float)this.mSlowDownIndex >= this.mSlowDownFrameCount) {
                    this.mCurr = this.mFinal;
                } else {
                    this.mCurr = this.mSlowDownStart + (int)(this.mSlowDownStep * (float)this.mSlowDownIndex - (float)(this.mSlowDownIndex * this.mSlowDownIndex) * this.mSlowDownStep / (2.0F * this.mSlowDownFrameCount));
                }
            } else {
                float p = (float)(this.mCurrFrameIndex + 1) / (float)this.mTotalFrameCount;
                this.mCurr = (int)((float)this.mStart + (float)(this.mFinal - this.mStart) * p);
                ++this.mCurrFrameIndex;
                int leftDistance = this.mFinal - this.mCurr;
                if(leftDistance < 0) {
                    leftDistance = -leftDistance;
                }

                if((float)leftDistance < this.mSlowDownDistance) {
                    this.setSlowDown(leftDistance);
                } else {
                    this.resetSlowDown();
                }
            }

            return true;
        }
    }

    public void finish() {
        if(!this.mFinished) {
            this.mCurr = this.mFinal;
            this.mCurrFrameIndex = this.mTotalFrameCount;
            this.mSlowDownFrameCount = 0.0F;
            this.mFinished = true;
        }

    }

    private void resetSlowDown() {
        this.mSlowDownStart = 0;
        this.mSlowDownFrameCount = 0.0F;
        this.mSlowDownIndex = 0;
    }

    private void setSlowDown(int distance) {
        this.mSlowDownStart = this.mCurr;
        this.mSlowDownIndex = 0;
        this.mSlowDownFrameCount = (float)(2 * distance) / this.mSlowDownStep;
        if(this.mSlowDownFrameCount < 0.0F) {
            this.mSlowDownFrameCount = -this.mSlowDownFrameCount;
        }

    }

    private void computeSlowDownDistance() {
        int distance = (this.mFinal - this.mStart) / 2;
        if(distance < 0) {
            distance = -distance;
        }

        this.mSlowDownDistance = this.mStep * this.mStep / this.mSlowDownRatio;
        if(this.mSlowDownDistance > (float)distance) {
            Log.w("ListLoopScroller", "computeSlowDownDistance mSlowDownDistance too big=" + this.mSlowDownDistance + " distance=" + distance + " mStep=" + this.mStep);
            this.mSlowDownDistance = (float)distance;
        }

    }
}
