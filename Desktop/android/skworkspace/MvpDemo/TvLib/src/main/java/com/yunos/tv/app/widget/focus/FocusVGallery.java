//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.VGallery;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

public class FocusVGallery extends VGallery implements DeepListener, ItemListener {
    protected static String TAG = "FocusVGallery";
    protected static boolean DEBUG = false;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();
    boolean mCanDraw = true;
    boolean mIsAnimate = true;
    public boolean mLayouted = false;
    boolean mDeepFocus = false;
    ItemSelectedListener mItemSelectedListener;
    FocusVGallery.GalleyPreKeyListener mGalleryPreKeyListener;
    boolean mReset = false;
    boolean mFocusBackground = false;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;

    public FocusVGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FocusVGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusVGallery(Context context) {
        super(context);
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }

    public void setPreKeyListener(FocusVGallery.GalleyPreKeyListener l) {
        this.mGalleryPreKeyListener = l;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mItemSelectedListener = listener;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d(TAG, "onFocusChanged");
        if(gainFocus && gainFocus && this.getChildCount() > 0 && this.mLayouted) {
            this.reset();
        }

        this.mIsAnimate = this.checkAnimate(direction);
        this.performSelect(gainFocus);
    }

    private boolean checkAnimate(int direction) {
        switch(direction) {
            case 17:
                return this.mAimateWhenGainFocusFromRight;
            case 33:
                return this.mAimateWhenGainFocusFromDown;
            case 66:
                return this.mAimateWhenGainFocusFromLeft;
            case 130:
                return this.mAimateWhenGainFocusFromUp;
            default:
                return true;
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mLayouted = true;
        if(this.isLayoutRequested()) {
            if((this.hasFocus() || this.hasDeepFocus()) && this.getChildCount() > 0 && this.mLayouted) {
                this.reset();
            }

            this.mClipFocusRect.set(0, 0, this.getWidth(), this.getHeight());
        }
    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    private void reset() {
        ItemListener item = (ItemListener)this.getSelectedView();
        if(item != null) {
            this.mFocusRectparams.set(item.getFocusParams());
            this.offsetDescendantRectToMyCoords(this.getSelectedView(), this.mFocusRectparams.focusRect());
        }

    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public boolean canDraw() {
        if(this.mItemCount <= 0) {
            return false;
        } else {
            View v = this.getSelectedView();
            if(v != null && this.mReset) {
                this.performSelect(true);
                this.mReset = false;
            }

            return this.getSelectedView() != null && this.mLayouted;
        }
    }

    public boolean isAnimate() {
        return this.mIsAnimate;
    }

    public ItemListener getItem() {
        return (ItemListener)this.getSelectedView();
    }

    public boolean isScrolling() {
        return this.isFling();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode = " + keyCode);
        if(this.checkState(keyCode)) {
            return true;
        } else {
            int distance;
            switch(keyCode) {
                case 19:
                    if(this.getSelectedItemPosition() <= 0) {
                        return true;
                    }

                    this.performSelect(false);
                    this.mIsAnimate = true;
                    this.setSelectedPositionInt(this.getSelectedItemPosition() - 1);
                    this.setNextSelectedPositionInt(this.getSelectedItemPosition() - 1);
                    distance = this.getChildAt(0).getHeight();
                    distance += this.mSpacing;
                    this.smoothScrollBy(distance);
                    if(this.canDraw()) {
                        this.mReset = false;
                        this.performSelect(true);
                    } else {
                        this.mReset = true;
                    }

                    return true;
                case 20:
                    if(this.getSelectedItemPosition() >= this.mItemCount - 1) {
                        return true;
                    } else {
                        int selectePos = this.getSelectedItemPosition();
                        if(selectePos == this.mItemCount - 1) {
                            return true;
                        }

                        this.performSelect(false);
                        this.mIsAnimate = true;
                        this.setSelectedPositionInt(this.getSelectedItemPosition() + 1);
                        this.setNextSelectedPositionInt(this.getSelectedItemPosition() + 1);
                        distance = this.getChildAt(0).getHeight();
                        distance += this.mSpacing;
                        this.smoothScrollBy(-distance);
                        if(this.canDraw()) {
                            this.mReset = false;
                            this.performSelect(true);
                        } else {
                            this.mReset = true;
                        }

                        return true;
                    }
                default:
                    this.mIsAnimate = false;
                    return super.onKeyDown(keyCode, event);
            }
        }
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
        if(this.checkState(keyCode)) {
            return true;
        } else if(this.getChildCount() <= 0) {
            return false;
        } else {
            switch(keyCode) {
                case 19:
                    return this.getSelectedItemPosition() > 0;
                case 20:
                    return this.getSelectedItemPosition() < this.mItemCount - 1;
                case 21:
                case 22:
                    if(this.mGalleryPreKeyListener != null) {
                        this.mGalleryPreKeyListener.preKeyDownListener(this, keyCode, event);
                    }
                default:
                    return false;
                case 23:
                case 66:
                    return true;
            }
        }
    }

    public boolean checkState(int keyCode) {
        return this.mLastScrollState == 2 && (keyCode == 21 || keyCode == 22);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if(views != null) {
            if(this.isFocusable()) {
                if((focusableMode & 1) != 1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) {
                    views.add(this);
                }
            }
        }
    }

    private void performSelect(boolean select) {
        if(this.mItemSelectedListener != null) {
            this.mItemSelectedListener.onItemSelected(this.getSelectedView(), this.getSelectedItemPosition(), select, this);
        }

    }

    /**
     * 获取焦点绘制参数
     * @return
     */
    @Override
    public FocusRectParams getFocusParams() {
        View v = this.getSelectedView();
        if(v == null) {
            Rect r = new Rect();
            this.getFocusedRect(r);
            this.mFocusRectparams.set(r, 0.5F, 0.5F);
            return this.mFocusRectparams;
        } else {
            if(this.mFocusRectparams == null || this.isScrolling()) {
                this.reset();
            }

            return this.mFocusRectparams;
        }
    }

    public boolean canDeep() {
        return true;
    }

    public boolean hasDeepFocus() {
        return this.mDeepFocus;
    }

    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mDeepFocus = gainFocus;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void onItemSelected(boolean selected) {
        this.performSelect(selected);
    }

    public void onItemClick() {
        if(this.getSelectedView() != null) {
            this.performItemClick(this.getSelectedView(), this.getSelectedItemPosition(), 0L);
        }

    }

    public boolean isScale() {
        return true;
    }

    public int getItemWidth() {
        return this.getWidth();
    }

    public int getItemHeight() {
        return this.getHeight();
    }

    public Rect getManualPadding() {
        return null;
    }

    public boolean isFocusBackground() {
        return this.mFocusBackground;
    }

    public void drawBeforeFocus(Canvas canvas) {
    }

    public void drawAfterFocus(Canvas canvas) {
    }

    public boolean isFinished() {
        return true;
    }

    public boolean isFlingRunnableFinish() {
        return this.getFlingRunnable() == null?true:this.getFlingRunnable().isFinished();
    }

    public Rect getClipFocusRect() {
        return this.mClipFocusRect;
    }

    public interface GalleyPreKeyListener {
        void preKeyDownListener(View var1, int var2, KeyEvent var3);
    }
}
