//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import com.yunos.tv.app.widget.AbsGallery;
import com.yunos.tv.app.widget.ListLoopScroller;
import com.yunos.tv.app.widget.AbsGallery.LayoutParams;

/**
 * 水平长廊类 TODO 没有继承类
 */
public class VGallery extends AbsGallery {
    private static final String TAG = "VGallery";
    private static final boolean localLOGV = false;
    private int mTopMost;
    private int mBottomMost;
    private VGallery.FlingRunnable mFlingRunnable = new VGallery.FlingRunnable();
    private boolean mIsRtl = true;

    public VGallery(Context context) {
        super(context);
        this.init(context);
    }

    public VGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public VGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    private void init(Context context) {
        this.mGravity = 1;
    }

    protected int computeVerticalScrollExtent() {
        return 1;
    }

    protected int computeVerticalScrollOffset() {
        return this.mSelectedPosition;
    }

    protected int computeVerticalScrollRange() {
        return this.mItemCount;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        this.layout(0, false);
        this.mInLayout = false;
    }

    int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }

    void trackMotionScroll(int deltaY) {
        if(this.getChildCount() != 0) {
            boolean toUp = deltaY < 0;
            int limitedDeltaY = this.getLimitedMotionScrollAmount(toUp, deltaY);
            if(limitedDeltaY != deltaY) {
                this.mFlingRunnable.endFling(false);
            }

            this.offsetChildrenTopAndBottom(limitedDeltaY);
            this.detachOffScreenChildren(toUp);
            if(toUp) {
                this.fillToGalleryDown();
            } else {
                this.fillToGalleryUp();
            }

            if(!this.mByPosition) {
                this.mRecycler.clear();
            }

            View selChild = this.mSelectedChild;
            if(selChild != null) {
                int childTop = selChild.getTop();
                int childCenter = selChild.getHeight() / 2;
                int galleryCenter = this.getHeight() / 2;
                this.mSelectedCenterOffset = childTop + childCenter - galleryCenter;
            }

            this.onScrollChanged(0, 0, 0, 0);
            this.invalidate();
        }
    }

    int getLimitedMotionScrollAmount(boolean motionToUp, int deltaY) {
        int extremeItemPosition = motionToUp != this.mIsRtl?this.mItemCount - 1:0;
        View extremeChild = this.getChildAt(extremeItemPosition - this.mFirstPosition);
        if(extremeChild == null) {
            return deltaY;
        } else {
            int extremeChildCenter = getCenterOfView(extremeChild);
            int galleryCenter = this.getCenterOfGallery();
            if(motionToUp) {
                if(extremeChildCenter <= galleryCenter) {
                    return 0;
                }
            } else if(extremeChildCenter >= galleryCenter) {
                return 0;
            }

            int centerDifference = galleryCenter - extremeChildCenter;
            return motionToUp?Math.max(centerDifference, deltaY):Math.min(centerDifference, deltaY);
        }
    }

    private int getCenterOfGallery() {
        return (this.getHeight() - this.getPaddingTop() - this.getPaddingBottom()) / 2 + this.getPaddingBottom();
    }

    private static int getCenterOfView(View view) {
        return view.getTop() + view.getHeight() / 2;
    }

    private void detachOffScreenChildren(boolean toUp) {
        int numChildren = this.getChildCount();
        int firstPosition = this.mFirstPosition;
        int start = 0;
        int count = 0;
        int galleryBottom;
        int i;
        int n;
        View child;
        if(toUp) {
            galleryBottom = this.getPaddingTop();

            for(i = 0; i < numChildren; ++i) {
                n = this.mIsRtl?numChildren - 1 - i:i;
                child = this.getChildAt(n);
                if(child.getBottom() >= galleryBottom) {
                    break;
                }

                start = n;
                ++count;
                this.mRecycler.put(firstPosition + n, child);
            }

            if(!this.mIsRtl) {
                start = 0;
            }
        } else {
            galleryBottom = this.getHeight() - this.getPaddingBottom();

            for(i = numChildren - 1; i >= 0; --i) {
                n = this.mIsRtl?numChildren - 1 - i:i;
                child = this.getChildAt(n);
                if(child.getTop() <= galleryBottom) {
                    break;
                }

                start = n;
                ++count;
                this.mRecycler.put(firstPosition + n, child);
            }

            if(this.mIsRtl) {
                start = 0;
            }
        }

        this.detachViewsFromParent(start, count);
        if(toUp != this.mIsRtl) {
            this.mFirstPosition += count;
        }

    }

    private void scrollIntoSlots() {
        if(this.getChildCount() != 0 && this.mSelectedChild != null) {
            int selectedCenter = getCenterOfView(this.mSelectedChild);
            int targetCenter = this.getCenterOfGallery();
            int scrollAmount = targetCenter - selectedCenter;
            if(scrollAmount != 0) {
                this.mFlingRunnable.startUsingDistance(scrollAmount);
            } else {
                this.onFinishedMovement();
            }

        }
    }

    private void setSelectionToCenterChild() {
        View selView = this.mSelectedChild;
        if(this.mSelectedChild != null) {
            int galleryCenter = this.getCenterOfGallery();
            if(selView.getTop() > galleryCenter || selView.getBottom() < galleryCenter) {
                int closestEdgeDistance = 2147483647;
                int newSelectedChildIndex = 0;

                int newPos;
                for(newPos = this.getChildCount() - 1; newPos >= 0; --newPos) {
                    View child = this.getChildAt(newPos);
                    if(child.getTop() <= galleryCenter && child.getBottom() >= galleryCenter) {
                        newSelectedChildIndex = newPos;
                        break;
                    }

                    int childClosestEdgeDistance = Math.min(Math.abs(child.getTop() - galleryCenter), Math.abs(child.getBottom() - galleryCenter));
                    if(childClosestEdgeDistance < closestEdgeDistance) {
                        closestEdgeDistance = childClosestEdgeDistance;
                        newSelectedChildIndex = newPos;
                    }
                }

                newPos = this.mFirstPosition + newSelectedChildIndex;
                if(newPos != this.mSelectedPosition) {
                    this.setSelectedPositionInt(newPos);
                    this.setNextSelectedPositionInt(newPos);
                    this.checkSelectionChanged();
                }

            }
        }
    }

    protected void layout(int delta, boolean animate) {
        this.mIsRtl = false;
        int childrenTop = this.mSpinnerPadding.top;
        int childrenHeight = this.getBottom() - this.getTop() - this.mSpinnerPadding.top - this.mSpinnerPadding.bottom;
        if(this.mDataChanged) {
            this.handleDataChanged();
        }

        if(this.mItemCount == 0) {
            this.resetList();
        } else {
            if(this.mNextSelectedPosition >= 0) {
                this.setSelectedPositionInt(this.mNextSelectedPosition);
            }

            this.recycleAllViews();
            this.detachAllViewsFromParent();
            this.mBottomMost = 0;
            this.mTopMost = 0;
            this.mFirstPosition = this.mSelectedPosition;
            View sel = this.makeAndAddView(this.mSelectedPosition, 0, 0, true);
            int selectedOffset = childrenTop + childrenHeight / 2 - sel.getHeight() / 2;
            sel.offsetTopAndBottom(selectedOffset);
            if(sel != null) {
                this.positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
            }

            this.fillToGalleryDown();
            this.fillToGalleryUp();
            if(!this.mByPosition) {
                this.mRecycler.clear();
            }

            this.invalidate();
            this.checkSelectionChanged();
            this.mDataChanged = false;
            this.mNeedSync = false;
            this.setNextSelectedPositionInt(this.mSelectedPosition);
            this.updateSelectedItemMetadata();
        }
    }

    private void fillToGalleryUp() {
        if(this.mIsRtl) {
            this.fillToGalleryUpRtl();
        } else {
            this.fillToGalleryUpLtr();
        }

    }

    private void fillToGalleryUpRtl() {
        int itemSpacing = this.mSpacing;
        int galleryTop = this.getPaddingTop();
        int numChildren = this.getChildCount();
        int numItems = this.mItemCount;
        View prevIterationView = this.getChildAt(numChildren - 1);
        int curPosition;
        int curBottomEdge;
        if(prevIterationView != null) {
            curPosition = this.mFirstPosition + numChildren;
            curBottomEdge = prevIterationView.getTop() - itemSpacing;
        } else {
            this.mFirstPosition = curPosition = this.mItemCount - 1;
            curBottomEdge = this.getBottom() - this.getTop() - this.getPaddingBottom();
            this.mShouldStopFling = true;
        }

        while(curBottomEdge > galleryTop && curPosition < this.mItemCount) {
            prevIterationView = this.makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curBottomEdge, false);
            curBottomEdge = prevIterationView.getTop() - itemSpacing;
            ++curPosition;
        }

    }

    private void fillToGalleryUpLtr() {
        int itemSpacing = this.mSpacing;
        int galleryTop = this.getPaddingTop();
        View prevIterationView = this.getChildAt(0);
        int curPosition;
        int curBottomEdge;
        if(prevIterationView != null) {
            curPosition = this.mFirstPosition - 1;
            curBottomEdge = prevIterationView.getTop() - itemSpacing;
        } else {
            curPosition = 0;
            curBottomEdge = this.getBottom() - this.getTop() - this.getPaddingBottom();
            this.mShouldStopFling = true;
        }

        while(curBottomEdge > galleryTop && curPosition >= 0) {
            prevIterationView = this.makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curBottomEdge, false);
            this.mFirstPosition = curPosition;
            curBottomEdge = prevIterationView.getTop() - itemSpacing;
            --curPosition;
        }

    }

    private void fillToGalleryDown() {
        if(this.mIsRtl) {
            this.fillToGalleryDownRtl();
        } else {
            this.fillToGalleryDownLtr();
        }

    }

    private void fillToGalleryDownRtl() {
        int itemSpacing = this.mSpacing;
        int galleryBottom = this.getBottom() - this.getTop() - this.getPaddingBottom();
        View prevIterationView = this.getChildAt(0);
        int curPosition;
        int curTopEdge;
        if(prevIterationView != null) {
            curPosition = this.mFirstPosition - 1;
            curTopEdge = prevIterationView.getBottom() + itemSpacing;
        } else {
            curPosition = 0;
            curTopEdge = this.getPaddingTop();
            this.mShouldStopFling = true;
        }

        while(curTopEdge < galleryBottom && curPosition >= 0) {
            prevIterationView = this.makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curTopEdge, true);
            this.mFirstPosition = curPosition;
            curTopEdge = prevIterationView.getBottom() + itemSpacing;
            --curPosition;
        }

    }

    private void fillToGalleryDownLtr() {
        int itemSpacing = this.mSpacing;
        int galleryBottom = this.getBottom() - this.getTop() - this.getPaddingBottom();
        int numChildren = this.getChildCount();
        int numItems = this.mItemCount;
        View prevIterationView = this.getChildAt(numChildren - 1);
        int curPosition;
        int curTopEdge;
        if(prevIterationView != null) {
            curPosition = this.mFirstPosition + numChildren;
            curTopEdge = prevIterationView.getBottom() + itemSpacing;
        } else {
            this.mFirstPosition = curPosition = this.mItemCount - 1;
            curTopEdge = this.getPaddingTop();
            this.mShouldStopFling = true;
        }

        while(curTopEdge < galleryBottom && curPosition < numItems) {
            prevIterationView = this.makeAndAddView(curPosition, curPosition - this.mSelectedPosition, curTopEdge, true);
            curTopEdge = prevIterationView.getBottom() + itemSpacing;
            ++curPosition;
        }

    }

    private View makeAndAddView(int position, int offset, int y, boolean fromTop) {
        View child;
        if(!this.mDataChanged) {
            child = this.mRecycler.get(position);
            if(child != null) {
                int childTop = child.getTop();
                this.mBottomMost = Math.max(this.mBottomMost, childTop + child.getMeasuredHeight());
                this.mTopMost = Math.min(this.mTopMost, childTop);
                if(this.mByPosition) {
                    child = this.mAdapter.getView(position, child, this);
                }

                this.setUpChild(child, offset, y, fromTop);
                return child;
            }
        }

        if(this.mByPosition) {
            child = this.mRecycler.get(position);
        } else {
            child = null;
        }

        child = this.mAdapter.getView(position, child, this);
        this.setUpChild(child, offset, y, fromTop);
        return child;
    }

    private void setUpChild(View child, int offset, int y, boolean fromTop) {
        LayoutParams lp = (LayoutParams)child.getLayoutParams();
        if(lp == null) {
            lp = (LayoutParams)this.generateDefaultLayoutParams();
        }

        this.addViewInLayout(child, fromTop != this.mIsRtl?-1:0, lp);
        child.setSelected(offset == 0);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, lp.width);
        child.measure(childWidthSpec, childHeightSpec);
        int childLeft = this.calculateLeft(child, true);
        int childRight = childLeft + child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        int childTop;
        int childBottom;
        if(fromTop) {
            childTop = y;
            childBottom = y + height;
        } else {
            childTop = y - height;
            childBottom = y;
        }

        child.layout(childLeft, childTop, childRight, childBottom);
    }

    private int calculateLeft(View child, boolean duringLayout) {
        int myWidth = duringLayout?this.getMeasuredWidth():this.getWidth();
        int childWidth = duringLayout?child.getMeasuredWidth():child.getWidth();
        int childLeft = 0;
        switch(this.mGravity) {
            case 1:
                int availableSpace = myWidth - this.mSpinnerPadding.right - this.mSpinnerPadding.left - childWidth;
                childLeft = this.mSpinnerPadding.left + availableSpace / 2;
            case 2:
            case 4:
            default:
                break;
            case 3:
                childLeft = this.mSpinnerPadding.left;
                break;
            case 5:
                childLeft = myWidth - this.mSpinnerPadding.right - childWidth;
        }

        return childLeft;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        super.onFling(e1, e2, velocityX, velocityY);
        this.mFlingRunnable.startUsingVelocity((int)(-velocityX));
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        super.onScroll(e1, e2, distanceX, distanceY);
        this.trackMotionScroll(-1 * (int)distanceY);
        return true;
    }

    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        this.mFlingRunnable.stop(false);
        return true;
    }

    protected void onUp() {
        super.onUp();
        if(this.mFlingRunnable.isFinished()) {
            this.scrollIntoSlots();
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case 19:
                if(this.movePrevious()) {
                    this.playSoundEffect(2);
                    return true;
                }
                break;
            case 20:
                if(this.moveNext()) {
                    this.playSoundEffect(4);
                    return true;
                }
                break;
            case 23:
            case 66:
                this.mReceivedInvokeKeyDown = true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case 23:
            case 66:
                if(this.mReceivedInvokeKeyDown && this.mItemCount > 0) {
                    this.dispatchPress(this.mSelectedChild);
                    this.postDelayed(new Runnable() {
                        public void run() {
                            VGallery.this.dispatchUnpress();
                        }
                    }, (long)ViewConfiguration.getPressedStateDuration());
                    int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
                    this.performItemClick(this.getChildAt(selectedIndex), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
                }

                this.mReceivedInvokeKeyDown = false;
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void smoothScrollBy(int distance) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.startUsingDistance(distance);
        }

    }

    public void stopScroll(boolean scrollIntoSlots) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.stop(scrollIntoSlots);
        }

    }

    public void setFlingScrollFrameCount(int frameCount) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setFrameCount(frameCount);
        }

    }

    public void setFlingScrollPostDelay(int delay) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setPostDelay(delay);
        }

    }

    public void setFlingScrollMaxStep(float maxStep) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setMaxStep(maxStep);
        }

    }

    protected boolean scrollToChild(int childPosition) {
        View child = this.getChildAt(childPosition);
        if(child != null) {
            int distance = this.getCenterOfGallery() - getCenterOfView(child);
            this.mFlingRunnable.startUsingDistance(distance);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isFling() {
        return !this.mFlingRunnable.isFinished();
    }

    public VGallery.FlingRunnable getFlingRunnable() {
        return this.mFlingRunnable;
    }

    protected class FlingRunnable implements Runnable {
        private Scroller mScroller = new Scroller(VGallery.this.getContext(), new DecelerateInterpolator());
        private int mLastFlingY;
        private ListLoopScroller mListLoopScroller = new ListLoopScroller();
        private int mFrameCount;
        private float mDefatultScrollStep = 5.0F;
        private int mPostDelay = 0;

        public boolean isFinished() {
            return this.mScroller.isFinished() && this.mListLoopScroller.isFinished();
        }

        public FlingRunnable() {
        }

        private void startCommon() {
            VGallery.this.removeCallbacks(this);
        }

        public void startUsingVelocity(int initialVelocity) {
            if(initialVelocity != 0) {
                this.startCommon();
                int initialY = initialVelocity < 0?2147483647:0;
                this.mLastFlingY = initialY;
                this.mScroller.fling(0, initialY, 0, initialVelocity, 0, 2147483647, 0, 2147483647);
                VGallery.this.post(this);
            }
        }

        public void startUsingDistance(int distance) {
            if(distance != 0) {
                this.mLastFlingY = 0;
                int frameCount;
                if(this.mFrameCount <= 0) {
                    frameCount = (int)((float)distance / this.mDefatultScrollStep);
                    if(frameCount < 0) {
                        frameCount = -frameCount;
                    } else if(frameCount == 0) {
                        frameCount = 1;
                    }
                } else {
                    frameCount = this.mFrameCount;
                }

                if(this.mListLoopScroller.isFinished()) {
                    this.startCommon();
                    this.mListLoopScroller.startScroll(0, -distance, frameCount);
                    VGallery.this.post(this);
                } else {
                    this.mListLoopScroller.startScroll(0, -distance, frameCount);
                }

                VGallery.this.reportScrollStateChange(2);
            }
        }

        public void stop(boolean scrollIntoSlots) {
            VGallery.this.removeCallbacks(this);
            this.endFling(scrollIntoSlots);
        }

        public void setFrameCount(int frameCount) {
            this.mFrameCount = frameCount;
        }

        public void setMaxStep(float maxStep) {
            this.mListLoopScroller.setMaxStep(maxStep);
        }

        public void setPostDelay(int delay) {
            this.mPostDelay = delay;
        }

        private void endFling(boolean scrollIntoSlots) {
            VGallery.this.reportScrollStateChange(0);
            this.mScroller.forceFinished(true);
            this.mListLoopScroller.finish();
            if(scrollIntoSlots) {
                VGallery.this.scrollIntoSlots();
            }

        }

        public void run() {
            if(VGallery.this.mItemCount == 0) {
                this.endFling(true);
            } else {
                VGallery.this.mShouldStopFling = false;
                boolean more = this.mListLoopScroller.computeScrollOffset();
                int y = this.mListLoopScroller.getCurr();
                int delta = this.mLastFlingY - y;
                if(delta > 0) {
                    VGallery.this.mDownTouchPosition = VGallery.this.mIsRtl?VGallery.this.mFirstPosition + VGallery.this.getChildCount() - 1:VGallery.this.mFirstPosition;
                    delta = Math.min(VGallery.this.getHeight() - VGallery.this.getPaddingTop() - VGallery.this.getPaddingBottom() - 1, delta);
                } else {
                    int offsetToLast = VGallery.this.getChildCount() - 1;
                    VGallery.this.mDownTouchPosition = VGallery.this.mIsRtl?VGallery.this.mFirstPosition:VGallery.this.mFirstPosition + VGallery.this.getChildCount() - 1;
                    delta = Math.max(-(VGallery.this.getHeight() - VGallery.this.getPaddingTop() - VGallery.this.getPaddingBottom() - 1), delta);
                }

                VGallery.this.trackMotionScroll(delta);
                if(more && !VGallery.this.mShouldStopFling) {
                    this.mLastFlingY = y;
                    VGallery.this.postDelayed(this, (long)this.mPostDelay);
                } else {
                    this.endFling(true);
                }

            }
        }
    }
}
