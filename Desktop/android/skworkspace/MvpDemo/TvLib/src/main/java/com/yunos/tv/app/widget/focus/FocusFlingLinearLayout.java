//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.ScalePositionManager;
import com.yunos.tv.app.widget.focus.FocusLinearLayout.NodeInfo;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

public class FocusFlingLinearLayout extends FocusLinearLayout {
    protected static final String TAG = "FocusScrollerLinearLayout";
    protected static final boolean DEBUG = true;
    public static final int MIN_VALUE = -2147483648;
    public static final int MAX_VALUE = 2147483647;
    private static final int SCROLL_DURATION = 100;
    public static final int HORIZONTAL_INVALID = -1;
    public static final int HORIZONTAL_SINGEL = 1;
    public static final int HORIZONTAL_FULL = 2;
    public static final int HORIZONTAL_OUTSIDE_SINGEL = 3;
    public static final int HORIZONTAL_OUTSIDE_FULL = 4;
    private int mScrollMode = 1;
    private OnScrollListener mScrollerListener = null;
    private int mMinLeft;
    private int mMinScaledLeft;
    private int mMaxLeft;
    private int mMaxScaledLeft;
    private int mMinRight;
    private int mMinScaledRight;
    private int mMaxRight;
    private int mMaxScaledRight;
    private int mMinTop;
    private int mMinScaledTop;
    private int mMaxTop;
    private int mMaxScaledTop;
    private int mMinBottom;
    private int mMinScaledBottom;
    private int mMaxBottom;
    private int mMaxScaledBottom;
    View mMinLeftView = null;
    View mMaxLeftView = null;
    View mMinRightView = null;
    View mMaxRightView = null;
    View mMinTopView = null;
    View maxTopView = null;
    View minBottomView = null;
    View maxBottomView = null;
    private FocusFlingLinearLayout.OutsideScrollListener mOutsideScrollListener = null;
    private Scroller mScroller;
    private int mLastScrollState = 0;
    private int mManualPaddingRight = 20;
    private int mManualPaddingBottom = 20;
    private int mCenterX = 640;
    private int mCenterY = 360;
    private int mLastHorizontalDirection = 0;
    private int mLastVerticalDirection = 0;
    private int mDuration = 200;
    private int mScrollX = 0;
    private int mScrollY = 0;
    protected int mScrollDistance = 0;
    private FocusFlingLinearLayout.FlingRunnable mFlingRunnable = new FocusFlingLinearLayout.FlingRunnable();

    public FocusFlingLinearLayout(Context context) {
        super(context);
        this.mScroller = new Scroller(context);
    }

    public FocusFlingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScroller = new Scroller(context);
    }

    public FocusFlingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScroller = new Scroller(context);
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mScrollerListener = l;
    }

    public void setHorizontalMode(int mode) {
        this.mScrollMode = mode;
    }

    public void setOutsideScrollListener(FocusFlingLinearLayout.OutsideScrollListener l) {
        this.mOutsideScrollListener = l;
    }

    public void setManualPaddingRight(int padding) {
        this.mManualPaddingRight = padding;
    }

    public void setManualPaddingBottom(int padding) {
        this.mManualPaddingBottom = padding;
    }

    public void setCenter(int centerX, int centerY) {
        this.mCenterX = centerX;
        this.mCenterY = centerY;
    }

    public void setScrollDuration(int duration) {
        this.mDuration = duration;
    }

    protected void initNode() {
        if(this.mNeedInitNode) {
            if(this.getChildCount() <= 0) {
                return;
            }

            this.mMinLeft = 2147483647;
            this.mMaxLeft = -2147483648;
            this.mMinRight = 2147483647;
            this.mMaxRight = -2147483648;
            this.mMinTop = 2147483647;
            this.mMaxTop = -2147483648;
            this.mMinBottom = 2147483647;
            this.mMaxBottom = -2147483648;

            for(int r = 0; r < this.getChildCount(); ++r) {
                View item = this.getChildAt(r);
                if(item.isFocusable() && item.getVisibility() != 8) {
                    if(item.getLeft() < this.mMinLeft) {
                        this.mMinLeftView = item;
                        this.mMinLeft = item.getLeft();
                    }

                    if(item.getLeft() > this.mMaxLeft) {
                        this.mMaxLeftView = item;
                        this.mMaxLeft = item.getLeft();
                    }

                    if(item.getRight() < this.mMinRight) {
                        this.mMinRightView = item;
                        this.mMinRight = item.getRight();
                    }

                    if(item.getRight() > this.mMaxRight) {
                        this.mMaxRightView = item;
                        this.mMaxRight = item.getRight();
                    }

                    if(item.getTop() < this.mMinTop) {
                        this.mMinTopView = item;
                        this.mMinTop = item.getTop();
                    }

                    if(item.getTop() > this.mMaxTop) {
                        this.maxTopView = item;
                        this.mMaxTop = item.getTop();
                    }

                    if(item.getBottom() < this.mMinBottom) {
                        this.minBottomView = item;
                        this.mMinBottom = item.getBottom();
                    }

                    if(item.getBottom() > this.mMaxBottom) {
                        this.maxBottomView = item;
                        this.mMaxBottom = item.getBottom();
                    }
                }
            }

            Log.d("FocusScrollerLinearLayout", "init: mMinLeft = " + this.mMinLeft + ", mMaxLeft = " + this.mMaxLeft + ", mMinRight = " + this.mMinRight + ", mMaxRight = " + this.mMaxRight + ", mMinTop = " + this.mMinTop + ", mMaxTop = " + this.mMaxTop + ", mMinBottom = " + this.mMinBottom + ", mMaxBottom = " + this.mMaxBottom);
            Log.d("FocusScrollerLinearLayout", "init: minLeftView = " + this.mMinLeftView + ", maxLeftView = " + this.mMaxLeftView + ", minRightView = " + this.mMinRightView + ", maxRightView = " + this.mMaxRightView + ", minTopView = " + this.mMinTopView + ", maxTopView = " + this.maxTopView + ", minBottomView = " + this.minBottomView + ", maxBottomView = " + this.maxBottomView);
            if(this.getChildCount() > 0) {
                Rect var4 = new Rect();
                ItemListener var5 = (ItemListener)this.mMinLeftView;
                FocusRectParams focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.mMinLeftView, var4);
                this.mMinScaledLeft = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).left;
                var5 = (ItemListener)this.mMaxLeftView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.mMaxLeftView, var4);
                this.mMaxScaledLeft = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).left;
                var5 = (ItemListener)this.mMinRightView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.mMinRightView, var4);
                this.mMinScaledRight = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).right;
                var5 = (ItemListener)this.mMaxRightView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.mMaxRightView, var4);
                this.mMaxScaledRight = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).right;
                var5 = (ItemListener)this.mMinTopView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.mMinTopView, var4);
                this.mMinScaledTop = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).top;
                var5 = (ItemListener)this.maxTopView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.maxTopView, var4);
                this.mMaxScaledTop = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).top;
                var5 = (ItemListener)this.minBottomView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.minBottomView, var4);
                this.mMinScaledBottom = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).bottom;
                var5 = (ItemListener)this.maxBottomView;
                focusParams = var5.getFocusParams();
                var4.set(focusParams.focusRect());
                this.offsetDescendantRectToMyCoords(this.maxBottomView, var4);
                this.mMaxScaledBottom = ScalePositionManager.instance().getScaledRect(var4, this.mParams.getScaleParams().getScaleX(), this.mParams.getScaleParams().getScaleY(), focusParams.coefX(), focusParams.coefY()).bottom;
                this.mMaxRight -= this.mScrollX;
                this.mMaxBottom -= this.mScrollY;
            }
        }

        super.initNode();
    }

    public void setSelectedView(View v, int keyCode) {
        if(!this.mNodeMap.containsKey(v)) {
            throw new IllegalArgumentException("Parent does\'t contain this view");
        } else {
            View selectedView = this.getSelectedView();
            if(v != null && selectedView != null) {
                OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
                if(selectedView instanceof DeepListener) {
                    DeepListener deep = (DeepListener)selectedView;
                    if(deep.canDeep() && deep.hasDeepFocus()) {
                        deep.onFocusDeeped(false, 0, (Rect)null);
                    } else {
                        listener.onFocusChange(selectedView, false);
                    }
                } else {
                    listener.onFocusChange(selectedView, false);
                }

                this.mIndex = ((NodeInfo)this.mNodeMap.get(v)).index;
                listener = v.getOnFocusChangeListener();
                if(v instanceof DeepListener) {
                    this.mDeep = (DeepListener)v;
                    if(!this.mDeep.canDeep()) {
                        this.mDeep = null;
                        listener.onFocusChange(v, true);
                    } else {
                        this.mDeep.onFocusDeeped(true, 0, (Rect)null);
                    }
                } else {
                    listener.onFocusChange(v, true);
                }

                this.reset();
                this.scrollSingel(keyCode);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean hr = super.onKeyDown(keyCode, event);
        if(hr) {
            this.scrollSingel(keyCode);
        }

        return hr;
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        if(dx != 0 || dy != 0) {
            this.mScrollDistance = dx;
            this.mFlingRunnable.startUsingDistance(dx, dy, duration);
        }
    }

    protected void reportScrollStateChange(int newState) {
        if(newState != this.mLastScrollState && this.mScrollerListener != null) {
            this.mLastScrollState = newState;
            this.mScrollerListener.onScrollStateChanged(this, newState);
        }

    }

    void scrollSingel(int keyCode) {
        View selectedView = this.getSelectedView();
        int diffY;
        int diffX;
        if(keyCode == 22) {
            diffY = this.getOffset(keyCode);
            diffX = 0;
            if(this.mLastVerticalDirection == 20 || this.mLastVerticalDirection == 19) {
                diffX = this.getOffset(this.mLastVerticalDirection);
            }

            this.smoothScrollBy(diffY, diffX, this.mDuration);
            this.mFocusRectparams.focusRect().offset(-diffY, -diffX);
            this.mLastHorizontalDirection = keyCode;
        } else if(keyCode == 21) {
            diffY = this.getOffset(keyCode);
            diffX = 0;
            if(this.mLastVerticalDirection == 20 || this.mLastVerticalDirection == 19) {
                diffX = this.getOffset(this.mLastVerticalDirection);
            }

            this.smoothScrollBy(diffY, diffX, this.mDuration);
            this.mFocusRectparams.focusRect().offset(-diffY, -diffX);
            this.mLastHorizontalDirection = keyCode;
        } else if(keyCode == 20) {
            diffY = this.getOffset(keyCode);
            diffX = 0;
            if(this.mLastHorizontalDirection == 21 || this.mLastHorizontalDirection == 22) {
                diffX = this.getOffset(this.mLastHorizontalDirection);
            }

            this.smoothScrollBy(diffX, diffY, this.mDuration);
            this.mFocusRectparams.focusRect().offset(-diffX, -diffY);
            this.mLastVerticalDirection = keyCode;
        } else if(keyCode == 19) {
            diffY = this.getOffset(keyCode);
            diffX = 0;
            if(this.mLastHorizontalDirection == 21 || this.mLastHorizontalDirection == 22) {
                diffX = this.getOffset(this.mLastHorizontalDirection);
            }

            this.smoothScrollBy(diffX, diffY, this.mDuration);
            this.mFocusRectparams.focusRect().offset(-diffX, -diffY);
            this.mLastVerticalDirection = keyCode;
        }

    }

    private int getOffset(int keyCode) {
        View selectedView = this.getSelectedView();
        int centerY;
        int selectCenterY;
        int diff;
        int currTopBind1;
        switch(keyCode) {
            case 19:
                centerY = this.mCenterY;
                selectCenterY = this.getCenterY(selectedView);
                diff = centerY - selectCenterY;
                currTopBind1 = this.mMinTop - this.mScrollY;
                if(diff > 0) {
                    if(currTopBind1 - diff < this.mMinTop) {
                        diff -= this.mMinTop - (currTopBind1 - diff);
                    }

                    if(diff > 0) {
                        return -diff;
                    }
                }

                return 0;
            case 20:
                centerY = this.mCenterY;
                selectCenterY = this.getCenterY(selectedView);
                diff = selectCenterY - centerY;
                boolean currTopBind2 = false;
                Rect visibleRect1 = new Rect();
                this.getGlobalVisibleRect(visibleRect1);
                int vheight = visibleRect1.height();
                if(diff > 0) {
                    if(vheight + diff > this.mMaxBottom + this.mManualPaddingBottom + this.mScrollY) {
                        diff -= vheight + diff - (this.mMaxBottom + this.mManualPaddingBottom + this.mScrollY);
                    }

                    if(diff > 0) {
                        return diff;
                    }
                }

                return 0;
            case 21:
                centerY = this.mCenterX;
                selectCenterY = this.getCenterX(selectedView);
                diff = centerY - selectCenterY;
                currTopBind1 = this.mMinLeft - this.mScrollX;
                if(diff > 0) {
                    if(currTopBind1 - diff < this.mMinLeft) {
                        diff -= this.mMinLeft - (currTopBind1 - diff);
                    }

                    if(diff > 0) {
                        return -diff;
                    }
                }

                return 0;
            case 22:
                centerY = this.mCenterX;
                selectCenterY = this.getCenterX(selectedView);
                diff = selectCenterY - centerY;
                Rect currTopBind = new Rect();
                this.getGlobalVisibleRect(currTopBind);
                int visibleRect = currTopBind.width();
                if(diff > 0) {
                    if(visibleRect + diff > this.mMaxRight + this.mManualPaddingRight + this.mScrollX) {
                        diff -= visibleRect + diff - (this.mMaxRight + this.mManualPaddingRight + this.mScrollX);
                    }

                    if(diff > 0) {
                        return diff;
                    }
                }

                return 0;
            default:
                return 0;
        }
    }

    private int getCenterX(View v) {
        boolean selectCenterX = false;
        int selectCenterX1;
        if((this.mFocusRectparams.centerMode() & 4) == 4) {
            selectCenterX1 = (this.mFocusRectparams.focusRect().left + this.mFocusRectparams.focusRect().right) / 2;
        } else {
            if((this.mFocusRectparams.centerMode() & 1) != 1) {
                throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterX: mFocusRectparams.centerMode() = " + this.mFocusRectparams.centerMode());
            }

            selectCenterX1 = (v.getLeft() + v.getRight()) / 2;
        }

        return selectCenterX1;
    }

    private int getCenterY(View v) {
        boolean selectCenterY = false;
        int selectCenterY1;
        if((this.mFocusRectparams.centerMode() & 64) == 64) {
            selectCenterY1 = (this.mFocusRectparams.focusRect().top + this.mFocusRectparams.focusRect().bottom) / 2;
        } else {
            if((this.mFocusRectparams.centerMode() & 16) != 16) {
                throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterY: mFocusRectparams.centerMode() = " + this.mFocusRectparams.centerMode());
            }

            selectCenterY1 = (v.getTop() + v.getBottom()) / 2;
        }

        return selectCenterY1;
    }

    void trackMotionScroll(int deltaX, int deltaY) {
        this.mScrollX += deltaX;
        this.mScrollY += deltaY;

        for(int index = 0; index < this.getChildCount(); ++index) {
            View child = this.getChildAt(index);
            if(deltaX != 0) {
                child.offsetLeftAndRight(deltaX);
            }

            if(deltaY != 0) {
                child.offsetTopAndBottom(deltaY);
            }
        }

    }

    public boolean isScrolling() {
        return this.mDeep != null?this.mDeep.isScrolling() || !this.mScroller.isFinished():!this.mScroller.isFinished();
    }

    private class FlingRunnable implements Runnable {
        private Scroller mScroller = new Scroller(FocusFlingLinearLayout.this.getContext(), new DecelerateInterpolator(0.6F));
        private int mLastFlingX;
        private int mLastFlingY;

        public FlingRunnable() {
        }

        public void startUsingDistance(int distanceX, int distanceY, int duration) {
            if(distanceX != 0 || distanceY != 0) {
                if(FocusFlingLinearLayout.this.getChildCount() > 0) {
                    if(!FocusFlingLinearLayout.this.isFinished()) {
                        this.endFling(true);
                    }

                    FocusFlingLinearLayout.this.reportScrollStateChange(2);
                    this.mScroller.startScroll(0, 0, distanceX, distanceY, duration);
                    this.mLastFlingX = 0;
                    this.mLastFlingY = 0;
                    FocusFlingLinearLayout.this.post(this);
                }
            }
        }

        private void endFling(boolean scrollIntoSlots) {
            FocusFlingLinearLayout.this.reportScrollStateChange(0);
            this.mScroller.forceFinished(true);
        }

        public void run() {
            boolean more = this.mScroller.computeScrollOffset();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            int deltaX = this.mLastFlingX - x;
            int deltaY = this.mLastFlingY - y;
            FocusFlingLinearLayout.this.trackMotionScroll(deltaX, deltaY);
            if(more) {
                this.mLastFlingX = x;
                this.mLastFlingY = y;
                FocusFlingLinearLayout.this.invalidate();
                FocusFlingLinearLayout.this.post(this);
            } else {
                this.endFling(true);
            }

        }
    }

    public interface OutsideScrollListener {
        int getCurrX();

        int getCurrY();

        void smoothOutsideScrollBy(int var1, int var2);
    }
}
