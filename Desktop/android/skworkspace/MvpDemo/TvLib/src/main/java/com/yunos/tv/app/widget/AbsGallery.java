package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent.DispatcherState;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Transformation;
import com.yunos.tv.app.widget.AbsSpinner;
import com.yunos.tv.app.widget.VGallery;
import com.yunos.tv.app.widget.AdapterView.AdapterContextMenuInfo;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.lib.SystemProUtils;

public abstract class AbsGallery extends AbsSpinner implements OnGestureListener {
    protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 1024;
    protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 2048;
    public static final int ACTION_SCROLL_FORWARD = 4096;
    public static final int ACTION_SCROLL_BACKWARD = 8192;
    static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
    protected int mSpacing = 0;
    int mGravity;
    int mAnimationDuration = 400;
    private float mUnselectedAlpha;
    GestureDetector mGestureDetector;
    int mDownTouchPosition;
    View mDownTouchView;
    Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
        public void run() {
            AbsGallery.this.mSuppressSelectionChanged = false;
            AbsGallery.this.selectionChanged();
        }
    };
    boolean mShouldStopFling;
    View mSelectedChild;
    boolean mShouldCallbackDuringFling = true;
    boolean mShouldCallbackOnUnselectedItemClick = true;
    boolean mSuppressSelectionChanged;
    boolean mReceivedInvokeKeyDown;
    private AdapterContextMenuInfo mContextMenuInfo;
    boolean mIsFirstScroll;
    int mSelectedCenterOffset;
    private OnScrollListener mOnScrollListener;
    protected int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    public AbsGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    public AbsGallery(Context context) {
        super(context);
        this.init(context);
    }

    public AbsGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
        int flags = this.getGroupFlags();
        flags |= FLAG_USE_CHILD_DRAWING_ORDER;
        flags |= FLAG_SUPPORT_STATIC_TRANSFORMATIONS;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
    }

    protected void reportScrollStateChange(int newState) {
        if(newState != this.mLastScrollState && this.mOnScrollListener != null) {
            this.mLastScrollState = newState;
            this.mOnScrollListener.onScrollStateChanged(this, newState);
        }

    }

    private void init(Context context) {
        this.mGestureDetector = new GestureDetector(context, this);
        //设置可以fling
        this.mGestureDetector.setIsLongpressEnabled(true);
    }

    public void setCallbackDuringFling(boolean shouldCallback) {
        this.mShouldCallbackDuringFling = shouldCallback;
    }

    public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
        this.mShouldCallbackOnUnselectedItemClick = shouldCallback;
    }

    /**
     * 设置动画的间隔 ms
     * @param animationDurationMillis
     */
    public void setAnimationDuration(int animationDurationMillis) {
        this.mAnimationDuration = animationDurationMillis;
    }

    /**
     * 设置长廊的间隙 TODO 目前不知道含义
     * @param spacing
     */
    public void setSpacing(int spacing) {
        this.mSpacing = spacing;
    }

    /**
     * 设置 Gravity
     * @param gravity
     */
    public void setGravity(int gravity) {
        if(this.mGravity != gravity) {
            this.mGravity = gravity;
            this.requestLayout();
        }

    }

    public void setUnselectedAlpha(float unselectedAlpha) {
        this.mUnselectedAlpha = unselectedAlpha;
    }

    protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.clear();
        t.setAlpha(child == this.mSelectedChild?1.0F:this.mUnselectedAlpha);
        return true;
    }

    public void offsetChildrenTopAndBottom(int offset) {
        for(int i = this.getChildCount() - 1; i >= 0; --i) {
            this.getChildAt(i).offsetTopAndBottom(offset);
        }

    }

    void offsetChildrenLeftAndRight(int offset) {
        for(int i = this.getChildCount() - 1; i >= 0; --i) {
            this.getChildAt(i).offsetLeftAndRight(offset);
        }

    }

    void dispatchPress(View child) {
        if(child != null) {
            child.setPressed(true);
        }

        this.setPressed(true);
    }

    void dispatchUnpress() {
        for(int i = this.getChildCount() - 1; i >= 0; --i) {
            this.getChildAt(i).setPressed(false);
        }

        this.setPressed(false);
    }

    void selectionChanged() {
        if(!this.mSuppressSelectionChanged) {
            super.selectionChanged();
        }

    }

    protected void dispatchSetPressed(boolean pressed) {
        if(this.mSelectedChild != null) {
            this.mSelectedChild.setPressed(pressed);
        }

    }

    void onFinishedMovement() {
        if(this.mSuppressSelectionChanged) {
            this.mSuppressSelectionChanged = false;
            super.selectionChanged();
        }

        this.mSelectedCenterOffset = 0;
        this.invalidate();
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = this.getPositionForView(originalView);
        if(longPressPosition < 0) {
            return false;
        } else {
            long longPressId = this.mAdapter.getItemId(longPressPosition);
            return this.dispatchLongPress(originalView, longPressPosition, longPressId);
        }
    }

    public boolean showContextMenu() {
        if(this.isPressed() && this.mSelectedPosition >= 0) {
            int index = this.mSelectedPosition - this.mFirstPosition;
            View v = this.getChildAt(index);
            return this.dispatchLongPress(v, this.mSelectedPosition, this.mSelectedRowId);
        } else {
            return false;
        }
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        boolean handled = false;
        if(this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, id);
        }

        if(!handled) {
            this.mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }

        if(handled) {
            this.performHapticFeedback(0);
        }

        return handled;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.dispatch(this, (DispatcherState)null, (Object)null);
    }

    public void dispatchSetSelected(boolean selected) {
    }

    public void onLongPress(MotionEvent e) {
        if(this.mDownTouchPosition >= 0) {
            this.performHapticFeedback(0);
            long id = this.getItemIdAtPosition(this.mDownTouchPosition);
            this.dispatchLongPress(this.mDownTouchView, this.mDownTouchPosition, id);
        }
    }

    public void onShowPress(MotionEvent e) {
    }

    protected void setSelectedPositionInt(int position) {
        super.setSelectedPositionInt(position);
        this.updateSelectedItemMetadata();
    }

    protected void updateSelectedItemMetadata() {
        View oldSelectedChild = this.mSelectedChild;
        View child = this.mSelectedChild = this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
        if(child != null) {
            child.setSelected(true);
            this.hasFocus();
            if(oldSelectedChild != null && oldSelectedChild != child) {
                oldSelectedChild.setSelected(false);
                oldSelectedChild.setFocusable(false);
            }

        }
    }

    protected abstract boolean scrollToChild(int var1);

    public boolean onSingleTapUp(MotionEvent e) {
        if(this.mDownTouchPosition < 0) {
            return false;
        } else {
            this.scrollToChild(this.mDownTouchPosition - this.mFirstPosition);
            if(this.mShouldCallbackOnUnselectedItemClick || this.mDownTouchPosition == this.mSelectedPosition) {
                this.performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
            }

            return true;
        }
    }

    boolean movePrevious() {
        if(this.mItemCount > 0 && this.mSelectedPosition > 0) {
            this.scrollToChild(this.mSelectedPosition - this.mFirstPosition - 1);
            return true;
        } else {
            return false;
        }
    }

    boolean moveNext() {
        if(this.mItemCount > 0 && this.mSelectedPosition < this.mItemCount - 1) {
            this.scrollToChild(this.mSelectedPosition - this.mFirstPosition + 1);
            return true;
        } else {
            return false;
        }
    }

    protected void onUp() {
        this.dispatchUnpress();
    }

    protected void onCancel() {
        this.onUp();
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean retValue = this.mGestureDetector.onTouchEvent(event);
        int action = event.getAction();
        if(action == 1) {
            this.onUp();
        } else if(action == 3) {
            this.onCancel();
        }

        return retValue;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(!this.mShouldCallbackDuringFling) {
            this.removeCallbacks(this.mDisableSuppressSelectionChangedRunnable);
            if(!this.mSuppressSelectionChanged) {
                this.mSuppressSelectionChanged = true;
            }
        }

        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        this.getParent().requestDisallowInterceptTouchEvent(true);
        if(!this.mShouldCallbackDuringFling) {
            if(this.mIsFirstScroll) {
                if(!this.mSuppressSelectionChanged) {
                    this.mSuppressSelectionChanged = true;
                }

                this.postDelayed(this.mDisableSuppressSelectionChangedRunnable, 250L);
            }
        } else if(this.mSuppressSelectionChanged) {
            this.mSuppressSelectionChanged = false;
        }

        this.mIsFirstScroll = false;
        return true;
    }

    public boolean onDown(MotionEvent e) {
        this.mDownTouchPosition = this.pointToPosition((int)e.getX(), (int)e.getY());
        if(this.mDownTouchPosition >= 0) {
            this.mDownTouchView = this.getChildAt(this.mDownTouchPosition - this.mFirstPosition);
            this.mDownTouchView.setPressed(true);
        }

        this.mIsFirstScroll = true;
        return true;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
        return selectedIndex < 0?i:(i == childCount - 1?selectedIndex:(i >= selectedIndex?i + 1:i));
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus && this.mSelectedChild != null) {
            this.mSelectedChild.setSelected(true);
        }

    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(VGallery.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(VGallery.class.getName());
        info.setScrollable(this.mItemCount > 1);
        if(this.isEnabled()) {
            if(this.mItemCount > 0 && this.mSelectedPosition < this.mItemCount - 1) {
                info.addAction(4096);
            }

            if(this.isEnabled() && this.mItemCount > 0 && this.mSelectedPosition > 0) {
                info.addAction(8192);
            }
        }

    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof AbsGallery.LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new AbsGallery.LayoutParams(p);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AbsGallery.LayoutParams(this.getContext(), attrs);
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new AbsGallery.LayoutParams(-2, -2);
    }

    public void offsetFocusRect(FocusRectParams rectParam, int offsetX, int offsetY) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            rectParam.focusRect().offset(offsetX, offsetY);
        }

    }

    public void offsetFocusRect(FocusRectParams rectParam, int left, int right, int top, int bottom) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = rectParam.focusRect();
            var10000.left += left;
            var10000 = rectParam.focusRect();
            var10000.right += right;
            var10000 = rectParam.focusRect();
            var10000.top += top;
            var10000 = rectParam.focusRect();
            var10000.bottom += bottom;
        }

    }

    public void offsetFocusRectLeftAndRight(FocusRectParams rectParam, int left, int right) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = rectParam.focusRect();
            var10000.left += left;
            var10000 = rectParam.focusRect();
            var10000.right += right;
        }

    }

    public void offsetFocusRectTopAndBottom(FocusRectParams rectParam, int top, int bottom) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = rectParam.focusRect();
            var10000.top += top;
            var10000 = rectParam.focusRect();
            var10000.bottom += bottom;
        }

    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
