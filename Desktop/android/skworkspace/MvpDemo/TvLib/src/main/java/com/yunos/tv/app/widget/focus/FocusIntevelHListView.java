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
import com.yunos.tv.app.widget.IntevelHListView;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

public class FocusIntevelHListView
        extends IntevelHListView
        implements DeepListener, ItemListener {
    protected static final String TAG = "FocusIntevelHListView";
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator) null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();
    boolean mIsAnimate = true;
    int mDistance = -1;
    boolean mDeepFocus = false;
    boolean mAutoSearch = false;
    ItemSelectedListener mItemSelectedListener;
    boolean mLayouted = false;
    boolean mReset = false;
    boolean mFocusBackground = false;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;

    public FocusIntevelHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FocusIntevelHListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusIntevelHListView(Context context) {
        super(context);
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setAutoSearch(boolean autoSearch) {
        this.mAutoSearch = autoSearch;
    }

    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mItemSelectedListener = listener;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.d("FocusIntevelHListView", "onFocusChanged");
        if (!this.mAutoSearch && this.getOnFocusChangeListener() != null) {
            this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        if (this.mAutoSearch) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }

        if (gainFocus && this.getChildCount() > 0 && this.mLayouted) {
            this.reset();
        }

        this.mIsAnimate = this.checkAnimate(direction);
    }

    private boolean checkAnimate(int direction) {
        switch (direction) {
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
        if (!this.hasFocus() && !this.hasDeepFocus()) {
            Rect rect = new Rect();
            this.getDrawingRect(rect);
            this.offsetDescendantRectToMyCoords(this, rect);
            this.mFocusRectparams.set(rect, 0.5F, 0.5F);
        } else {
            this.reset();
        }

        this.mLayouted = true;
        this.mClipFocusRect.set(0, 0, this.getWidth(), this.getBottom());
    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    public Params getParams() {
        if (this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public void getFocusedRect(Rect r) {
        if (!this.hasFocus() && !this.hasDeepFocus()) {
            this.getDrawingRect(r);
        } else {
            super.getFocusedRect(r);
        }
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (this.hasFocus()) {
            super.addFocusables(views, direction, focusableMode);
        } else if (views != null) {
            if (this.isFocusable()) {
                if ((focusableMode & 1) != 1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) {
                    views.add(this);
                }
            }
        }
    }

    private void reset() {
        ItemListener item = (ItemListener) this.getSelectedView();
        if (item != null) {
            this.mFocusRectparams.set(item.getFocusParams());
            this.offsetDescendantRectToMyCoords(this.getSelectedView(), this.mFocusRectparams.focusRect());
        }

    }

    public FocusRectParams getFocusParams() {
        View v = this.getSelectedView();
        if (v == null) {
            Rect r = new Rect();
            this.getFocusedRect(r);
            this.mFocusRectparams.set(r, 0.5F, 0.5F);
            return this.mFocusRectparams;
        } else {
            if (this.mFocusRectparams == null || this.isScrolling()) {
                this.reset();
            }

            return this.mFocusRectparams;
        }
    }

    public boolean canDraw() {
        View v = this.getSelectedView();
        if (v != null && this.mReset) {
            this.performSelect(true);
            this.mReset = false;
        }

        return this.getSelectedView() != null && this.mLayouted;
    }

    public boolean isAnimate() {
        return this.mIsAnimate;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("FocusIntevelHListView", "onKeyDown keyCode = " + keyCode);
        if (this.getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (this.mDistance < 0) {
                this.mDistance = this.getChildAt(0).getHeight();
            }

            switch (keyCode) {
                case 21:
                    if (this.moveLeft()) {
                        this.playSoundEffect(1);
                        return true;
                    }
                    break;
                case 22:
                    if (this.moveRight()) {
                        this.playSoundEffect(1);
                        return true;
                    }
            }

            Log.d("FocusIntevelHListView", "commonKey: list = " + this.convertListToString());
            return super.onKeyDown(keyCode, event);
        }
    }

    private boolean moveLeft() {
        if (Math.abs(this.getLeftScrollDistance()) > this.getChildAt(0).getHeight() * 3) {
            return true;
        } else {
            this.performSelect(false);
            this.mReset = false;
            int nextSelectedPosition = this.getSelectedItemPosition() - 1 >= 0 ? this.getSelectedItemPosition() - 1 : -1;
            if (nextSelectedPosition != -1) {
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if (this.canDraw()) {
                    this.mReset = false;
                    this.performSelect(true);
                } else {
                    this.mReset = true;
                }

                this.amountToCenterScroll(17, nextSelectedPosition);
                return true;
            } else {
                return false;
            }
        }
    }

    private void performSelect(boolean select) {
        if (this.mItemSelectedListener != null) {
            this.mItemSelectedListener.onItemSelected(this.getSelectedView(), this.getSelectedItemPosition(), select, this);
        }

    }

    private boolean moveRight() {
        if (this.getLeftScrollDistance() > this.getChildAt(0).getHeight() * 3) {
            return true;
        } else {
            this.performSelect(false);
            this.mReset = false;
            int nextSelectedPosition = this.getSelectedItemPosition() + 1 < this.mItemCount ? this.getSelectedItemPosition() + 1 : -1;
            if (nextSelectedPosition != -1) {
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if (this.canDraw()) {
                    this.mReset = false;
                    this.performSelect(true);
                } else {
                    this.mReset = true;
                }

                this.amountToCenterScroll(66, nextSelectedPosition);
                return true;
            } else {
                return false;
            }
        }
    }

    int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int var18;
        if (nextSelectedPosition == 7) {
            byte listRight = 0;
            var18 = listRight + 1;
            --var18;
        }

        var18 = this.getWidth() - this.mListPadding.right;
        int listLeft = this.mListPadding.left;
        int numChildren = this.getChildCount();
        int amountToScroll = 0;
        int distanceLeft = this.getLeftScrollDistance();
        int center = (this.getGroupFlags() & 34) == 34 ? (this.getWidth() - listLeft - var18) / 2 + listLeft : this.getWidth() / 2;
        View nextSelctedView;
        boolean nextSelectedCenter;
        boolean reset;
        int intevel;
        Object finalNextSelectedCenter;
        Object maxDiff;
        Object firstItem;
        Object spacing;
        int firstVisibleView;
        int var19;
        int var21;
        int var20;
        int var23;
        View var22;
        if (direction == 66) {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            nextSelectedCenter = false;
            reset = false;
            intevel = 0;
            if (nextSelctedView == null) {
                nextSelctedView = this.getChildAt(this.getChildCount() - 1);
                var19 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                finalNextSelectedCenter = this.getAdapter().getItem(this.getLastVisiblePosition());
                maxDiff = this.getAdapter().getItem(nextSelectedPosition - 1);
                firstItem = this.getAdapter().getItem(nextSelectedPosition);
                if (!maxDiff.equals(firstItem)) {
                    intevel = this.mIntevel;
                }

                var19 += nextSelctedView.getWidth() * (nextSelectedPosition - this.getLastVisiblePosition());

                for (firstVisibleView = this.getLastVisiblePosition() + 1; firstVisibleView <= nextSelectedPosition; ++firstVisibleView) {
                    firstItem = this.getAdapter().getItem(firstVisibleView);
                    if (!finalNextSelectedCenter.equals(firstItem)) {
                        var19 += this.mIntevel;
                    }

                    finalNextSelectedCenter = firstItem;
                }

                reset = false;
            } else {
                var19 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            var20 = var19 - distanceLeft;
            if (var20 > center) {
                amountToScroll = var20 - center;
                Log.d("FocusIntevelHListView", "amountToCenterScroll amountToScroll = " + amountToScroll + ", nextSelctedView = " + nextSelctedView + ", nextSelectedPosition = " + nextSelectedPosition + ", focus to right" + ", distanceLeft = " + distanceLeft);
                var21 = nextSelctedView.getWidth() * (this.mItemCount - this.getLastVisiblePosition() - 1);
                firstItem = this.getAdapter().getItem(this.getLastVisiblePosition());

                for (firstVisibleView = this.getLastVisiblePosition() + 1; firstVisibleView < this.mItemCount; ++firstVisibleView) {
                    spacing = this.getAdapter().getItem(firstVisibleView);
                    if (!firstItem.equals(spacing)) {
                        var21 += this.mIntevel;
                    }

                    firstItem = spacing;
                    if (var21 > amountToScroll) {
                        break;
                    }
                }

                var21 -= distanceLeft;
                var22 = this.getChildAt(numChildren - 1);
                var23 = 0;
                if (nextSelectedPosition != this.mItemCount - 1) {
                    var23 = this.mSpacing;
                }

                if (var22.getRight() + var23 > this.getWidth() - this.mListPadding.right) {
                    var21 += var22.getRight() + var23 - (this.getWidth() - this.mListPadding.right);
                }

                if (amountToScroll > var21) {
                    amountToScroll = var21;
                }

                if (reset) {
                    this.reset();
                    this.offsetFocusRectLeftAndRight(-distanceLeft, -distanceLeft);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        this.offsetFocusRectLeftAndRight(-amountToScroll, -amountToScroll);
                    } else {
                        this.offsetFocusRectLeftAndRight(nextSelctedView.getWidth() + this.mSpacing + intevel - amountToScroll, nextSelctedView.getWidth() + this.mSpacing + intevel - amountToScroll);
                    }

                    this.smoothScrollBy(amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if (!reset) {
                        this.offsetFocusRectLeftAndRight(nextSelctedView.getWidth() + this.mSpacing + intevel, nextSelctedView.getWidth() + this.mSpacing + intevel);
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.mIsAnimate = true;
            }

            return amountToScroll;
        } else if (direction != 17) {
            return 0;
        } else {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            nextSelectedCenter = false;
            reset = false;
            intevel = 0;
            if (nextSelctedView == null) {
                nextSelctedView = this.getChildAt(0);
                var19 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                finalNextSelectedCenter = this.getAdapter().getItem(this.getFirstVisiblePosition());
                maxDiff = this.getAdapter().getItem(nextSelectedPosition + 1);
                firstItem = this.getAdapter().getItem(nextSelectedPosition);
                if (!maxDiff.equals(firstItem)) {
                    intevel = this.mIntevel;
                }

                var19 -= (nextSelctedView.getWidth() + this.mSpacing) * (this.getFirstVisiblePosition() - nextSelectedPosition);

                for (firstVisibleView = this.getFirstVisiblePosition() - 1; firstVisibleView >= nextSelectedPosition; --firstVisibleView) {
                    firstItem = this.getAdapter().getItem(firstVisibleView);
                    if (!finalNextSelectedCenter.equals(firstItem)) {
                        var19 -= this.mIntevel;
                    }

                    finalNextSelectedCenter = firstItem;
                }

                reset = false;
            } else {
                var19 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            var20 = var19 - distanceLeft;
            if (var20 < center) {
                Log.d("FocusIntevelHListView", "amountToCenterScroll amountToScroll = " + amountToScroll + ", nextSelctedView = " + nextSelctedView + ", nextSelectedPosition = " + nextSelectedPosition + ", focus to left" + ", distanceLeft = " + distanceLeft);
                amountToScroll = center - var20;
                var21 = nextSelctedView.getWidth() * this.getFirstVisiblePosition();
                firstItem = this.getAdapter().getItem(this.getFirstVisiblePosition());

                for (firstVisibleView = this.getFirstVisiblePosition() - 1; firstVisibleView >= 0; --firstVisibleView) {
                    spacing = this.getAdapter().getItem(firstVisibleView);
                    if (!firstItem.equals(spacing)) {
                        var21 += this.mIntevel;
                    }

                    firstItem = spacing;
                    if (var21 > amountToScroll) {
                        break;
                    }
                }

                if (var21 < 0) {
                    var21 = 0;
                }

                var21 += distanceLeft;
                var22 = this.getChildAt(0);
                var23 = 0;
                if (nextSelectedPosition != 0) {
                    var23 = this.mSpacing;
                }

                if (var22.getLeft() - var23 < listLeft) {
                    var21 += listLeft - (var22.getLeft() - var23);
                }

                if (amountToScroll > var21) {
                    amountToScroll = var21;
                }

                if (reset) {
                    this.reset();
                    this.offsetFocusRectLeftAndRight(-distanceLeft, -distanceLeft);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        this.offsetFocusRectLeftAndRight(amountToScroll, amountToScroll);
                    } else {
                        this.offsetFocusRectLeftAndRight(-(nextSelctedView.getWidth() + this.mSpacing + intevel - amountToScroll), nextSelctedView.getWidth() + this.mSpacing + intevel - amountToScroll);
                    }

                    this.smoothScrollBy(-amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if (!reset) {
                        this.offsetFocusRectLeftAndRight(-(nextSelctedView.getWidth() + this.mSpacing + intevel), -(nextSelctedView.getWidth() + this.mSpacing + intevel));
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.mIsAnimate = true;
            }

            return amountToScroll;
        }
    }

    public ItemListener getItem() {
        return (ItemListener) this.getSelectedView();
    }

    public boolean isScrolling() {
        return this.mLastScrollState != 0;
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.d("FocusIntevelHListView", "preOnKeyDown keyCode = " + keyCode);
        switch (keyCode) {
            case 21:
            case 122:
                return this.getSelectedItemPosition() > 0;
            case 22:
            case 123:
                return this.getSelectedItemPosition() < this.mItemCount - 1;
            case 23:
            case 66:
                return true;
            default:
                return false;
        }
    }

    public boolean hasDeepFocus() {
        return this.mDeepFocus;
    }

    public boolean canDeep() {
        return true;
    }

    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mDeepFocus = gainFocus;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
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

    public void onItemSelected(boolean selected) {
        this.performSelect(selected);
    }

    public void onItemClick() {
        if (this.getSelectedView() != null) {
            this.performItemClick(this.getSelectedView(), this.getSelectedItemPosition(), 0L);
        }

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

    public void offsetFocusRectLeftAndRight(int left, int right) {
        if (SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = this.mFocusRectparams.focusRect();
            var10000.left += left;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.right += right;
        }

    }

    public Rect getClipFocusRect() {
        return this.mClipFocusRect;
    }
}
