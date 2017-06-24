//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.AlphaParams;
import com.yunos.tv.app.widget.focus.params.ScaleParams;

public class StaticPositionManager extends PositionManager {
    static String TAG = "StaticPositionManager";
    private boolean mReset = false;

    public StaticPositionManager(int focusMode, PositionListener l) {
        super(focusMode, l);
    }

    public void draw(Canvas canvas) {
        this.preDrawOut(canvas);
        super.draw(canvas);
        if(this.mStart) {
            if(!this.mFocus.canDraw()) {
                this.postDrawOut(canvas);
                this.mListener.postInvalidateDelayed(30L);
            } else {
                ItemListener item = this.mFocus.getItem();
                if(item != null) {
                    this.processReset();
                    this.drawBeforeFocus(item, canvas);
                    boolean isInvalidate = false;
                    if(!this.isFinished()) {
                        if(this.mFrame == 1) {
                            this.mFocus.onFocusStart();
                        }

                        if(!this.isAlphaFinished()) {
                            this.drawAlpha(item);
                            ++this.mAlphaFrame;
                        }

                        if(!this.isScaleFinished()) {
                            if(item.isScale()) {
                                this.drawScale(canvas, item);
                                this.drawStaticFocus(canvas, item, item.getScaleX(), item.getScaleY());
                            } else {
                                this.drawStaticFocus(canvas, item);
                            }

                            ++this.mScaleFrame;
                        } else if(this.mFocus.isScrolling()) {
                            this.drawStaticFocus(canvas, item);
                            isInvalidate = true;
                        } else if(this.mForceDrawFocus) {
                            this.drawStaticFocus(canvas, item);
                            isInvalidate = true;
                            this.mForceDrawFocus = false;
                        } else {
                            this.drawFocus(canvas);
                        }

                        ++this.mFrame;
                        isInvalidate = true;
                        if(this.mFrame == this.getTotalFrame()) {
                            this.mFocus.onFocusFinished();
                        }
                    } else if(this.mFocus.isScrolling()) {
                        this.drawStaticFocus(canvas, item);
                        isInvalidate = true;
                    } else if(this.mForceDrawFocus) {
                        this.drawStaticFocus(canvas, item);
                        isInvalidate = true;
                        this.mForceDrawFocus = false;
                    } else {
                        this.drawFocus(canvas);
                    }

                    if(!this.mPause && (isInvalidate || this.mSelector.isDynamicFocus())) {
                        this.mListener.invalidate();
                    }

                    if(this.isFinished()) {
                        this.addCurNode(item);
                        this.resetSelector();
                    } else {
                        this.mCurNodeAdded = false;
                    }

                    this.drawAfterFocus(item, canvas);
                    this.postDrawOut(canvas);
                    this.mLastItem = item;
                }
            }
        }
    }

    public void resetSelector() {
        if(this.mConvertSelector != null) {
            this.mSelector = this.mConvertSelector;
            this.setConvertSelector((DrawListener)null);
        }

    }

    private void processReset() {
        if(this.mReset) {
            ItemListener item = this.mFocus.getItem();
            if(item == null) {
                Log.e(TAG, "processReset: item is null! mFocus:" + this.mFocus);
                return;
            }

            this.removeNode(item);
            this.mReset = false;
        }

    }

    void drawAlpha(ItemListener item) {
        AlphaParams alphaParams = this.mFocus.getParams().getAlphaParams();
        float dstAlpha = alphaParams.getFromAlpha();
        float diffAlpha = alphaParams.getToAlpha() - alphaParams.getFromAlpha();
        float coef = (float)this.mFrame / (float)alphaParams.getAlphaFrameRate();
        Object alphaInterpolator = this.mFocus.getParams().getAlphaParams().getAlphaInteroplator();
        if(alphaInterpolator == null) {
            alphaInterpolator = new LinearInterpolator();
        }

        coef = ((Interpolator)alphaInterpolator).getInterpolation(coef);
        dstAlpha += diffAlpha * coef;
        if(this.mLastItem == item) {
            dstAlpha = alphaParams.getToAlpha();
        }

        if(this.mConvertSelector != null) {
            this.mConvertSelector.setAlpha(dstAlpha);
            this.mSelector.setAlpha(0.0F);
        } else {
            this.mSelector.setAlpha(dstAlpha);
        }

    }

    void drawScale(Canvas canvas, ItemListener item) {
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
        if(this.mLastItem == item) {
            dstScaleXValue = scaleParams.getScaleX();
            dstScaleYValue = scaleParams.getScaleY();
        }

        item.setScaleX(dstScaleXValue);
        item.setScaleY(dstScaleYValue);
    }

    public int getTotalFrame() {
        return Math.max(this.mScaleFrameRate, this.mAlphaFrameRate);
    }

    public boolean isFinished() {
        return this.isScaleFinished() && this.isAlphaFinished();
    }

    public boolean isScaleFinished() {
        return this.mFrame > this.mScaleFrameRate;
    }

    public boolean isAlphaFinished() {
        return this.mFrame > this.mAlphaFrameRate;
    }

    public void reset() {
        super.reset();
        this.mReset = true;
    }
}
