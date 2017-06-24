//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.ScaleParams;

public class SyncPositionManager extends PositionManager {
    protected static final String TAG = "SyncPositionManager";
    protected static final boolean DEBUG = false;
    private Rect mLastFocusRect = new Rect();
    private Rect mCurrFocusRect = new Rect();
    private boolean mReset = false;
    private boolean mIsAnimate = false;

    public SyncPositionManager(int focusMode, PositionListener l) {
        super(focusMode, l);
    }

    public void draw(Canvas canvas) {
        this.preDrawUnscale(canvas);
        super.draw(canvas);
        if(this.mStart) {
            if(!this.mFocus.canDraw()) {
                this.drawFocus(canvas);
                this.postDrawUnscale(canvas);
                this.mListener.postInvalidateDelayed(30L);
            } else {
                ItemListener item = this.mFocus.getItem();
                if(item != null) {
                    this.processReset();
                    this.drawBeforeFocus(item, canvas);
                    boolean isInvalidate = false;
                    if(!this.isFinished()) {
                        boolean isScrolling = this.mFocus.isScrolling();
                        if(this.mFrame == 1) {
                            this.mFocus.onFocusStart();
                            this.onFocusStart();
                        }

                        this.onFocusProcess();
                        boolean drawFocus = true;
                        if(this.mLastFocusRect.isEmpty()) {
                            drawFocus = false;
                        }

                        if(isScrolling && !this.isFocusFinished()) {
                            this.updateDstRect(item);
                        }

                        if(this.mIsAnimate) {
                            if(!this.isFocusFinished() && drawFocus) {
                                this.drawFocus(canvas, item);
                                ++this.mFocusFrame;
                            }
                        } else if(this.isScaleFinished()) {
                            if(isScrolling) {
                                this.updateDstRect(item);
                                this.mFocusRect.set(this.mCurrFocusRect);
                            }

                            this.drawFocus(canvas);
                        } else {
                            drawFocus = false;
                        }

                        if(!this.isScaleFinished()) {
                            if(item.isScale()) {
                                if(isScrolling) {
                                    this.updateDstRect(item);
                                    this.mFocusRect.set(this.mCurrFocusRect);
                                }

                                this.drawScale(canvas, item, !drawFocus);
                            } else if(!drawFocus) {
                                this.drawStaticFocus(canvas, item, this.mFocus.getParams().getScaleParams().getScaleX(), this.mFocus.getParams().getScaleParams().getScaleY());
                            }

                            ++this.mScaleFrame;
                        } else if(!drawFocus) {
                            if(isScrolling) {
                                this.updateDstRect(item);
                                this.mFocusRect.set(this.mCurrFocusRect);
                            }

                            this.drawFocus(canvas);
                        }

                        ++this.mFrame;
                        isInvalidate = true;
                        if(this.mFrame == this.getTotalFrame()) {
                            this.mFocus.onFocusFinished();
                            this.onFocusFinished();
                        }
                    } else if(this.mFocus.isScrolling()) {
                        this.drawStaticFocus(canvas, item);
                        this.mLastFocusRect.set(this.mFocusRect);
                        this.mCurrFocusRect.set(this.mFocusRect);
                        isInvalidate = true;
                    } else if(this.mForceDrawFocus) {
                        this.drawStaticFocus(canvas, item);
                        isInvalidate = true;
                        this.mForceDrawFocus = false;
                    } else {
                        this.drawFocus(canvas);
                    }

                    if(!item.isFinished()) {
                        isInvalidate = true;
                    }

                    if(!this.mPause && (isInvalidate || this.mSelector.isDynamicFocus())) {
                        this.mListener.invalidate();
                    }

                    this.drawAfterFocus(item, canvas);
                    this.postDrawUnscale(canvas);
                }
            }
        }
    }

    private void onFocusStart() {
        if(this.mSelectorPolator == null) {
            this.mSelectorPolator = new AccelerateDecelerateFrameInterpolator();
        }

    }

    private void onFocusFinished() {
        this.resetSelector();
    }

    public void resetSelector() {
        if(this.mConvertSelector != null) {
            this.mSelector = this.mConvertSelector;
            this.setConvertSelector((DrawListener)null);
        }

    }

    private void onFocusProcess() {
        int totalFrame = this.getTotalFrame();
        float alpha1 = this.mSelectorPolator.getInterpolation((float)this.mFrame * 1.0F / (float)totalFrame);
        float alpha2 = 1.0F - this.mSelectorPolator.getInterpolation((float)this.mFrame * 1.0F / (float)totalFrame);
        if(this.mFrame >= totalFrame || !this.mIsAnimate) {
            this.resetSelector();
        }

        if(this.mConvertSelector != null && this.mFrame < totalFrame && this.mIsAnimate) {
            this.mConvertSelector.setAlpha(alpha1);
            if(this.mSelector != null) {
                this.mSelector.setAlpha(alpha2);
            }
        } else if(this.mSelector != null) {
            this.mSelector.setAlpha(1.0F);
        }

    }

    private void processReset() {
        if(this.mReset) {
            ScaleParams scaleParams = this.mFocus.getParams().getScaleParams();
            ItemListener item = this.mFocus.getItem();
            if(item == null) {
                Log.e("SyncPositionManager", "processReset: item is null! mFocus:" + this.mFocus);
                return;
            }

            this.removeScaleNode(item);
            scaleParams.computeScaleXY(item.getItemWidth(), item.getItemHeight());
            this.mLastFocusRect.set(this.mFocusRect);
            this.updateDstRect(item);
            this.mIsAnimate = this.mFocus.isAnimate();
            this.mReset = false;
        }

    }

    private void updateDstRect(ItemListener item) {
        this.mCurrFocusRect.set(this.getFinalRect(item));
    }

    private void drawFocus(Canvas canvas, ItemListener item) {
        int focusFrameRate = this.mFocus.getParams().getFocusParams().getFocusFrameRate();
        float coef = (float)this.mFrame / (float)focusFrameRate;
        Object focusInterpolator = this.mFocus.getParams().getFocusParams().getFocusInterpolator();
        if(focusInterpolator == null) {
            focusInterpolator = new LinearInterpolator();
        }

        coef = ((Interpolator)focusInterpolator).getInterpolation(coef);
        this.mFocusRect.left = (int)((float)this.mLastFocusRect.left + (float)(this.mCurrFocusRect.left - this.mLastFocusRect.left) * coef);
        this.mFocusRect.right = (int)((float)this.mLastFocusRect.right + (float)(this.mCurrFocusRect.right - this.mLastFocusRect.right) * coef);
        this.mFocusRect.top = (int)((float)this.mLastFocusRect.top + (float)(this.mCurrFocusRect.top - this.mLastFocusRect.top) * coef);
        this.mFocusRect.bottom = (int)((float)this.mLastFocusRect.bottom + (float)(this.mCurrFocusRect.bottom - this.mLastFocusRect.bottom) * coef);
        this.drawFocus(canvas);
    }

    private void drawScale(Canvas canvas, ItemListener item, boolean drawFocus) {
        ScaleParams scaleParams = this.mFocus.getParams().getScaleParams();
        float dstScaleXValue = 1.0F;
        float dstScaleYValue = 1.0F;
        float itemDiffScaleXValue = scaleParams.getScaleX() - 1.0F;
        float itemDiffScaleYValue = scaleParams.getScaleY() - 1.0F;
        float coef = (float)this.mFrame / (float)scaleParams.getScaleFrameRate();
        Object scaleInterpolator = this.mFocus.getParams().getScaleParams().getScaleInterpolator();
        if(scaleInterpolator == null) {
            scaleInterpolator = new LinearInterpolator();
        }

        coef = ((Interpolator)scaleInterpolator).getInterpolation(coef);
        dstScaleXValue = 1.0F + itemDiffScaleXValue * coef;
        dstScaleYValue = 1.0F + itemDiffScaleYValue * coef;
        item.setScaleX(dstScaleXValue);
        item.setScaleY(dstScaleYValue);
        if(drawFocus) {
            this.drawStaticFocus(canvas, item, dstScaleXValue, dstScaleYValue);
        }

    }

    public boolean isFinished() {
        return this.mFrame > this.getTotalFrame();
    }

    public int getTotalFrame() {
        return Math.max(this.mFocusFrameRate, this.mScaleFrameRate);
    }

    private boolean isFocusFinished() {
        return this.mFrame > this.mFocusFrameRate;
    }

    private boolean isScaleFinished() {
        return this.mFrame > this.mScaleFrameRate;
    }

    public void reset() {
        super.reset();
        this.mReset = true;
    }
}