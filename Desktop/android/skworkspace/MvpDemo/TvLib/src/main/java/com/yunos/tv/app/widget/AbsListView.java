//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EdgeEffect;
import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tv.app.widget.ListLoopScroller;
import com.yunos.tv.app.widget.OverScroller;
import com.yunos.tv.app.widget.AbsBaseListView.CheckForLongPress;
import com.yunos.tv.app.widget.AbsBaseListView.PerformClick;

public abstract class AbsListView extends AbsBaseListView {
    private static final String TAG = "AbsListView";
    private static final boolean DEBUG = true;
    AbsListView.FlingRunnable mFlingRunnable = new AbsListView.FlingRunnable();
    int mSelectedLeft;
    boolean unhandleFullVisible;
    protected boolean mItemsCanFocus = false;
    private AbsListView.PositionScroller mPositionScroller;
    protected boolean mStackFromBottom = false;
    private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
        public void run() {
            AbsListView.this.mSuppressSelectionChanged = false;
            AbsListView.this.selectionChanged();
        }
    };
    protected boolean mShouldStopFling;
    private boolean mSuppressSelectionChanged;
    protected boolean mReceivedInvokeKeyDown;
    int mMotionX;
    int mMotionY;
    int mMotionCorrection;
    int mLastY;
    private int mActivePointerId = -1;
    private int mDirection = 0;
    private Runnable mPendingCheckForTap;
    int mMotionViewOriginalTop;
    private Runnable mTouchModeReset;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private float mVelocityScale = 1.0F;
    int mOverscrollDistance;
    private EdgeEffect mEdgeGlowTop;
    private EdgeEffect mEdgeGlowBottom;
    private static final boolean PROFILE_SCROLLING = false;
    private boolean mScrollProfilingStarted = false;
    private int mTouchSlop;
    private boolean mFlingProfilingStarted = false;
    int mOverflingDistance;
    static final Interpolator sLinearInterpolator = new LinearInterpolator();
    protected int mUpPreLoadedCount = 0;
    protected int mDownPreLoadedCount = 0;
    Interpolator mInterpolator = null;

    public AbsListView(Context context) {
        super(context);
        this.needMeasureSelectedView = false;
    }

    public AbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.needMeasureSelectedView = false;
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.needMeasureSelectedView = false;
    }

    public int getUpPreLoadedCount() {
        return this.mUpPreLoadedCount;
    }

    public int getDownPreLoadedCount() {
        return this.mDownPreLoadedCount;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(this.isLayoutRequested()) {
            this.mInLayout = true;
            this.layoutChildren();
            this.mInLayout = false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if(this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }

        if(!this.mIsAttached) {
            return false;
        } else {
            int pointerIndex;
            int y;
            switch(action & 255) {
                case 0:
                    pointerIndex = this.mTouchMode;
                    if(pointerIndex != 6 && pointerIndex != 5) {
                        y = (int)ev.getX();
                        int y1 = (int)ev.getY();
                        this.mActivePointerId = ev.getPointerId(0);
                        int motionPosition = this.findMotionRow(y1);
                        if(pointerIndex != 4 && motionPosition >= 0) {
                            View v = this.getChildAt(motionPosition - this.mFirstPosition);
                            this.mMotionViewOriginalTop = v.getTop();
                            this.mMotionX = y;
                            this.mMotionY = y1;
                            this.mMotionPosition = motionPosition;
                            this.mTouchMode = 0;
                            this.clearScrollingCache();
                        }

                        this.mLastY = -2147483648;
                        this.initOrResetVelocityTracker();
                        this.mVelocityTracker.addMovement(ev);
                        if(pointerIndex == 4) {
                            return true;
                        }
                        break;
                    }

                    this.mMotionCorrection = 0;
                    return true;
                case 1:
                case 3:
                    this.mTouchMode = -1;
                    this.mActivePointerId = -1;
                    this.recycleVelocityTracker();
                    this.reportScrollStateChange(0);
                    break;
                case 2:
                    switch(this.mTouchMode) {
                        case 0:
                            pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                            if(pointerIndex == -1) {
                                pointerIndex = 0;
                                this.mActivePointerId = ev.getPointerId(pointerIndex);
                            }

                            y = (int)ev.getY(pointerIndex);
                            this.initVelocityTrackerIfNotExists();
                            this.mVelocityTracker.addMovement(ev);
                            if(this.startScrollIfNeeded(y)) {
                                return true;
                            }
                    }
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if(!this.isEnabled()) {
            return this.isClickable() || this.isLongClickable();
        } else {
            if(this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }

            if(!this.mIsAttached) {
                return false;
            } else {
                int action = ev.getAction();
                this.initVelocityTrackerIfNotExists();
                this.mVelocityTracker.addMovement(ev);
                View v;
                int index;
                int id;
                int x;
                int motionPosition;
                switch(action & 255) {
                    case 0:
                        switch(this.mTouchMode) {
                            case 6:
                                this.mFlingRunnable.endFling();
                                if(this.mPositionScroller != null) {
                                    this.mPositionScroller.stop();
                                }

                                this.mTouchMode = 5;
                                this.mMotionX = (int)ev.getX();
                                this.mMotionY = this.mLastY = (int)ev.getY();
                                this.mMotionCorrection = 0;
                                this.mActivePointerId = ev.getPointerId(0);
                                this.mDirection = 0;
                                break;
                            default:
                                this.mActivePointerId = ev.getPointerId(0);
                                index = (int)ev.getX();
                                id = (int)ev.getY();
                                x = this.pointToPosition(index, id);
                                if(!this.mDataChanged) {
                                    if(this.mTouchMode != 4 && x >= 0 && this.getAdapter().isEnabled(x)) {
                                        this.mTouchMode = 0;
                                        if(this.mPendingCheckForTap == null) {
                                            this.mPendingCheckForTap = new AbsListView.CheckForTap();
                                        }

                                        this.postDelayed(this.mPendingCheckForTap, (long)ViewConfiguration.getTapTimeout());
                                    } else if(this.mTouchMode == 4) {
                                        this.createScrollingCache();
                                        this.mTouchMode = 3;
                                        this.mMotionCorrection = 0;
                                        x = this.findMotionRow(id);
                                        this.mFlingRunnable.flywheelTouch();
                                    }
                                }

                                if(x >= 0) {
                                    v = this.getChildAt(x - this.mFirstPosition);
                                    this.mMotionViewOriginalTop = v.getTop();
                                }

                                this.mMotionX = index;
                                this.mMotionY = id;
                                this.mMotionPosition = x;
                                this.mLastY = -2147483648;
                        }

                        if(this.performButtonActionOnTouchDown(ev) && this.mTouchMode == 0) {
                            this.removeCallbacks(this.mPendingCheckForTap);
                        }
                        break;
                    case 1:
                        int initialVelocity;
                        switch(this.mTouchMode) {
                            case 0:
                            case 1:
                            case 2:
                                index = this.mMotionPosition;
                                final View id2 = this.getChildAt(index - this.mFirstPosition);
                                float x1 = ev.getX();
                                boolean y1 = x1 > (float)this.mListPadding.left && x1 < (float)(this.getWidth() - this.mListPadding.right);
                                if(id2 != null && !id2.hasFocusable() && y1) {
                                    if(this.mTouchMode != 0) {
                                        id2.setPressed(false);
                                    }

                                    if(this.mPerformClick == null) {
                                        this.mPerformClick = new PerformClick();
                                    }

                                    final PerformClick motionPosition1 = this.mPerformClick;
                                    motionPosition1.mClickMotionPosition = index;
                                    motionPosition1.rememberWindowAttachCount();
                                    this.mResurrectToPosition = index;
                                    if(this.mTouchMode == 0 || this.mTouchMode == 1) {
                                        Handler velocityTracker3 = this.getHandler();
                                        if(velocityTracker3 != null) {
                                            velocityTracker3.removeCallbacks((Runnable)(this.mTouchMode == 0?this.mPendingCheckForTap:this.mPendingCheckForLongPress));
                                        }

                                        this.mLayoutMode = 0;
                                        if(!this.mDataChanged && this.mAdapter.isEnabled(index)) {
                                            this.mTouchMode = 1;
                                            this.setSelectedPositionInt(this.mMotionPosition);
                                            this.layoutChildren();
                                            id2.setPressed(true);
                                            this.positionSelector(this.mMotionPosition, id2);
                                            this.setPressed(true);
                                            if(this.mSelector != null) {
                                                Drawable initialVelocity2 = this.mSelector.getCurrent();
                                                if(initialVelocity2 != null && initialVelocity2 instanceof TransitionDrawable) {
                                                    ((TransitionDrawable)initialVelocity2).resetTransition();
                                                }
                                            }

                                            if(this.mTouchModeReset != null) {
                                                this.removeCallbacks(this.mTouchModeReset);
                                            }

                                            this.mTouchModeReset = new Runnable() {
                                                public void run() {
                                                    AbsListView.this.mTouchMode = -1;
                                                    id2.setPressed(false);
                                                    AbsListView.this.setPressed(false);
                                                    if(!AbsListView.this.mDataChanged) {
                                                        motionPosition1.run();
                                                    }

                                                }
                                            };
                                            this.postDelayed(this.mTouchModeReset, (long)ViewConfiguration.getPressedStateDuration());
                                        } else {
                                            this.mTouchMode = -1;
                                            this.updateSelectorState();
                                        }

                                        return true;
                                    }

                                    if(!this.mDataChanged && this.mAdapter.isEnabled(index)) {
                                        motionPosition1.run();
                                    }
                                }

                                this.mTouchMode = -1;
                                this.updateSelectorState();
                                break;
                            case 3:
                                motionPosition = this.getChildCount();
                                if(motionPosition > 0) {
                                    int velocityTracker2 = this.getChildAt(0).getTop();
                                    initialVelocity = this.getChildAt(motionPosition - 1).getBottom();
                                    int contentTop = this.mListPadding.top;
                                    int contentBottom = this.getHeight() - this.mListPadding.bottom;
                                    if(this.mFirstPosition == 0 && velocityTracker2 >= contentTop && this.mFirstPosition + motionPosition < this.mItemCount && initialVelocity <= this.getHeight() - contentBottom) {
                                        this.mTouchMode = -1;
                                        this.reportScrollStateChange(0);
                                    } else {
                                        VelocityTracker velocityTracker1 = this.mVelocityTracker;
                                        velocityTracker1.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                                        int initialVelocity1 = (int)(velocityTracker1.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
                                        if(Math.abs(initialVelocity1) > this.mMinimumVelocity && (this.mFirstPosition != 0 || velocityTracker2 != contentTop - this.mOverscrollDistance) && (this.mFirstPosition + motionPosition != this.mItemCount || initialVelocity != contentBottom + this.mOverscrollDistance)) {
                                            if(this.mFlingRunnable == null) {
                                                this.mFlingRunnable = new AbsListView.FlingRunnable();
                                            }

                                            this.reportScrollStateChange(2);
                                            this.mFlingRunnable.start(-initialVelocity1);
                                        } else {
                                            this.mTouchMode = -1;
                                            this.reportScrollStateChange(0);
                                            if(this.mFlingRunnable != null) {
                                                this.mFlingRunnable.endFling();
                                            }

                                            if(this.mPositionScroller != null) {
                                                this.mPositionScroller.stop();
                                            }
                                        }
                                    }
                                } else {
                                    this.mTouchMode = -1;
                                    this.reportScrollStateChange(0);
                                }
                            case 4:
                            default:
                                break;
                            case 5:
                                if(this.mFlingRunnable == null) {
                                    this.mFlingRunnable = new AbsListView.FlingRunnable();
                                }

                                VelocityTracker velocityTracker = this.mVelocityTracker;
                                velocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                                initialVelocity = (int)velocityTracker.getYVelocity(this.mActivePointerId);
                                this.reportScrollStateChange(2);
                                if(Math.abs(initialVelocity) > this.mMinimumVelocity) {
                                    this.mFlingRunnable.startOverfling(-initialVelocity);
                                } else {
                                    this.mFlingRunnable.startSpringback();
                                }
                        }

                        this.setPressed(false);
                        if(this.mEdgeGlowTop != null) {
                            this.mEdgeGlowTop.onRelease();
                            this.mEdgeGlowBottom.onRelease();
                        }

                        this.invalidate();
                        Handler index2 = this.getHandler();
                        if(index2 != null) {
                            index2.removeCallbacks(this.mPendingCheckForLongPress);
                        }

                        this.recycleVelocityTracker();
                        this.mActivePointerId = -1;
                        break;
                    case 2:
                        index = ev.findPointerIndex(this.mActivePointerId);
                        if(index == -1) {
                            index = 0;
                            this.mActivePointerId = ev.getPointerId(index);
                        }

                        id = (int)ev.getY(index);
                        if(this.mDataChanged) {
                            this.layoutChildren();
                        }

                        switch(this.mTouchMode) {
                            case 0:
                            case 1:
                            case 2:
                                this.startScrollIfNeeded(id);
                                return true;
                            case 3:
                            case 5:
                                this.scrollIfNeeded(id);
                                return true;
                            case 4:
                            default:
                                return true;
                        }
                    case 3:
                        switch(this.mTouchMode) {
                            case 5:
                                if(this.mFlingRunnable == null) {
                                    this.mFlingRunnable = new AbsListView.FlingRunnable();
                                }

                                this.mFlingRunnable.startSpringback();
                            case 6:
                                break;
                            default:
                                this.mTouchMode = -1;
                                this.setPressed(false);
                                View index1 = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
                                if(index1 != null) {
                                    index1.setPressed(false);
                                }

                                this.clearScrollingCache();
                                Handler id1 = this.getHandler();
                                if(id1 != null) {
                                    id1.removeCallbacks(this.mPendingCheckForLongPress);
                                }

                                this.recycleVelocityTracker();
                        }

                        if(this.mEdgeGlowTop != null) {
                            this.mEdgeGlowTop.onRelease();
                            this.mEdgeGlowBottom.onRelease();
                        }

                        this.mActivePointerId = -1;
                    case 4:
                    default:
                        break;
                    case 5:
                        index = ev.getActionIndex();
                        id = ev.getPointerId(index);
                        x = (int)ev.getX(index);
                        int y = (int)ev.getY(index);
                        this.mMotionCorrection = 0;
                        this.mActivePointerId = id;
                        this.mMotionX = x;
                        this.mMotionY = y;
                        motionPosition = this.pointToPosition(x, y);
                        if(motionPosition >= 0) {
                            v = this.getChildAt(motionPosition - this.mFirstPosition);
                            this.mMotionViewOriginalTop = v.getTop();
                            this.mMotionPosition = motionPosition;
                        }

                        this.mLastY = y;
                        break;
                    case 6:
                        this.onSecondaryPointerUp(ev);
                        index = this.mMotionX;
                        id = this.mMotionY;
                        x = this.pointToPosition(index, id);
                        if(x >= 0) {
                            v = this.getChildAt(x - this.mFirstPosition);
                            this.mMotionViewOriginalTop = v.getTop();
                            this.mMotionPosition = x;
                        }

                        this.mLastY = id;
                }

                return true;
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & '\uff00') >> 8;
        int pointerId = ev.getPointerId(pointerIndex);
        if(pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0?1:0;
            this.mMotionX = (int)ev.getX(newPointerIndex);
            this.mMotionY = (int)ev.getY(newPointerIndex);
            this.mMotionCorrection = 0;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }

    }

    private void scrollIfNeeded(int y) {
        int rawDeltaY = y - this.mMotionY;
        int deltaY = rawDeltaY - this.mMotionCorrection;
        int incrementalDeltaY = this.mLastY != -2147483648?y - this.mLastY:deltaY;
        int newScroll;
        int motionPosition;
        int oldScroll1;
        if(this.mTouchMode == 3) {
            if(y != this.mLastY) {
                if((this.getGroupFlags() & 524288) == 0 && Math.abs(rawDeltaY) > this.mTouchSlop) {
                    ViewParent oldScroll = this.getParent();
                    if(oldScroll != null) {
                        oldScroll.requestDisallowInterceptTouchEvent(true);
                    }
                }

                if(this.mMotionPosition >= 0) {
                    oldScroll1 = this.mMotionPosition - this.mFirstPosition;
                } else {
                    oldScroll1 = this.getChildCount() / 2;
                }

                newScroll = 0;
                View newDirection = this.getChildAt(oldScroll1);
                if(newDirection != null) {
                    newScroll = newDirection.getTop();
                }

                boolean overScrollDistance = false;
                if(incrementalDeltaY != 0) {
                    overScrollDistance = this.trackMotionScroll(deltaY, incrementalDeltaY);
                }

                newDirection = this.getChildAt(oldScroll1);
                if(newDirection != null) {
                    motionPosition = newDirection.getTop();
                    if(overScrollDistance) {
                        int motionView = -incrementalDeltaY - (motionPosition - newScroll);
                        this.overScrollBy(0, motionView, 0, this.getScrollY(), 0, 0, 0, this.mOverscrollDistance, true);
                        if(Math.abs(this.mOverscrollDistance) == Math.abs(this.getScrollY()) && this.mVelocityTracker != null) {
                            this.mVelocityTracker.clear();
                        }

                        int overscrollMode = this.getOverScrollMode();
                        if(overscrollMode == 0 || overscrollMode == 1 && !this.contentFits()) {
                            this.mDirection = 0;
                            this.mTouchMode = 5;
                            if(rawDeltaY > 0) {
                                this.mEdgeGlowTop.onPull((float)motionView / (float)this.getHeight());
                                if(!this.mEdgeGlowBottom.isFinished()) {
                                    this.mEdgeGlowBottom.onRelease();
                                }
                            } else if(rawDeltaY < 0) {
                                this.mEdgeGlowBottom.onPull((float)motionView / (float)this.getHeight());
                                if(!this.mEdgeGlowTop.isFinished()) {
                                    this.mEdgeGlowTop.onRelease();
                                }
                            }
                        }
                    }

                    this.mMotionY = y;
                }

                this.mLastY = y;
            }
        } else if(this.mTouchMode == 5 && y != this.mLastY) {
            oldScroll1 = this.getScrollY();
            newScroll = oldScroll1 - incrementalDeltaY;
            int newDirection1 = y > this.mLastY?1:-1;
            if(this.mDirection == 0) {
                this.mDirection = newDirection1;
            }

            int overScrollDistance1 = -incrementalDeltaY;
            if((newScroll >= 0 || oldScroll1 < 0) && (newScroll <= 0 || oldScroll1 > 0)) {
                incrementalDeltaY = 0;
            } else {
                overScrollDistance1 = -oldScroll1;
                incrementalDeltaY += overScrollDistance1;
            }

            if(overScrollDistance1 != 0) {
                this.overScrollBy(0, overScrollDistance1, 0, this.getScrollY(), 0, 0, 0, this.mOverscrollDistance, true);
                motionPosition = this.getOverScrollMode();
                if(motionPosition == 0 || motionPosition == 1 && !this.contentFits()) {
                    if(rawDeltaY > 0) {
                        this.mEdgeGlowTop.onPull((float)overScrollDistance1 / (float)this.getHeight());
                        if(!this.mEdgeGlowBottom.isFinished()) {
                            this.mEdgeGlowBottom.onRelease();
                        }
                    } else if(rawDeltaY < 0) {
                        this.mEdgeGlowBottom.onPull((float)overScrollDistance1 / (float)this.getHeight());
                        if(!this.mEdgeGlowTop.isFinished()) {
                            this.mEdgeGlowTop.onRelease();
                        }
                    }
                }
            }

            if(incrementalDeltaY != 0) {
                if(this.getScrollY() != 0) {
                    this.scrollTo(this.getScrollX(), 0);
                    this.invalidateParentIfNeeded();
                }

                this.trackMotionScroll(incrementalDeltaY, incrementalDeltaY);
                this.mTouchMode = 3;
                motionPosition = this.findClosestMotionRow(y);
                this.mMotionCorrection = 0;
                View motionView1 = this.getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalTop = motionView1 != null?motionView1.getTop():0;
                this.mMotionY = y;
                this.mMotionPosition = motionPosition;
            }

            this.mLastY = y;
            this.mDirection = newDirection1;
        }

    }

    int findClosestMotionRow(int y) {
        int childCount = this.getChildCount();
        if(childCount == 0) {
            return -1;
        } else {
            int motionRow = this.findMotionRow(y);
            return motionRow != -1?motionRow:this.mFirstPosition + childCount - 1;
        }
    }

    private boolean contentFits() {
        int childCount = this.getChildCount();
        return childCount == 0?true:(childCount != this.mItemCount?false:this.getChildAt(0).getTop() >= this.mListPadding.top && this.getChildAt(childCount - 1).getBottom() <= this.getHeight() - this.mListPadding.bottom);
    }

    private boolean startScrollIfNeeded(int y) {
        int deltaY = y - this.mMotionY;
        int distance = Math.abs(deltaY);
        boolean overscroll = this.getScrollY() != 0;
        if(!overscroll && distance <= this.mTouchSlop) {
            return false;
        } else {
            this.createScrollingCache();
            if(overscroll) {
                this.mTouchMode = 5;
                this.mMotionCorrection = 0;
            } else {
                this.mTouchMode = 3;
                this.mMotionCorrection = deltaY > 0?this.mTouchSlop:-this.mTouchSlop;
            }

            Handler handler = this.getHandler();
            if(handler != null) {
                handler.removeCallbacks(this.mPendingCheckForLongPress);
            }

            this.setPressed(false);
            View motionView = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
            if(motionView != null) {
                motionView.setPressed(false);
            }

            this.reportScrollStateChange(1);
            ViewParent parent = this.getParent();
            if(parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }

            this.scrollIfNeeded(y);
            return true;
        }
    }

    abstract void fillGap(boolean var1);

    abstract int findMotionRow(int var1);

    boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
        if(this.getChildCount() == 0) {
            return true;
        } else {
            boolean isDown = deltaX < 0;
            if(deltaX != deltaX) {
                this.mFlingRunnable.endFling();
            }

            this.offsetChildrenTopAndBottom(deltaX);
            this.detachOffScreenChildren(isDown);
            this.fillGap(isDown);
            this.onScrollChanged(0, 0, 0, 0);
            this.invalidate();
            return false;
        }
    }

    protected void detachOffScreenChildren(boolean isDown) {
        int numChildren = this.getChildCount();
        int firstPosition = this.mFirstPosition;
        int start = 0;
        int count = 0;
        int bottom;
        int i;
        View child;
        if(isDown) {
            bottom = this.getPaddingTop();

            for(i = 0; i < numChildren; ++i) {
                child = this.getChildAt(i);
                if(child.getBottom() >= bottom) {
                    break;
                }

                ++count;
                this.mRecycler.addScrapView(child, firstPosition + i);
            }

            start = 0;
        } else {
            bottom = this.getHeight() - this.getPaddingBottom();

            for(i = numChildren - 1; i >= 0; --i) {
                child = this.getChildAt(i);
                if(child.getTop() <= bottom) {
                    break;
                }

                start = i;
                ++count;
                this.mRecycler.addScrapView(child, firstPosition + i);
            }
        }

        this.detachViewsFromParent(start, count);
        if(isDown) {
            this.mFirstPosition += count;
        }

    }

    protected void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        boolean clipToPadding = (this.getGroupFlags() & 34) == 34;
        int flags;
        if(clipToPadding) {
            saveCount = canvas.save();
            int drawSelectorOnTop = this.getScrollX();
            flags = this.getScrollY();
            canvas.clipRect(drawSelectorOnTop + this.getPaddingLeft(), flags + this.getPaddingTop(), drawSelectorOnTop + this.getRight() - this.getLeft() - this.getPaddingRight(), flags + this.getBottom() - this.getTop() - this.getPaddingBottom());
            int flags1 = this.getGroupFlags();
            flags1 &= -35;
            this.setGroupFlags(flags1);
        }

        boolean drawSelectorOnTop1 = this.drawSclectorOnTop();
        if(!drawSelectorOnTop1) {
            this.drawSelector(canvas);
        }

        super.dispatchDraw(canvas);
        if(drawSelectorOnTop1) {
            this.drawSelector(canvas);
        }

        if(clipToPadding) {
            canvas.restoreToCount(saveCount);
            flags = this.getGroupFlags();
            flags &= -35;
            this.setGroupFlags(flags);
        }

    }

    public void setSelectionFromTop(int position, int x) {
        if(this.mAdapter != null) {
            if(!this.isInTouchMode()) {
                position = this.lookForSelectablePosition(position, true);
                if(position >= 0) {
                    this.setNextSelectedPositionInt(position);
                }
            } else {
                this.mResurrectToPosition = position;
            }

            if(position >= 0) {
                this.mLayoutMode = 4;
                this.mSpecificTop = this.mListPadding.left + x;
                if(this.mNeedSync) {
                    this.mSyncPosition = position;
                    this.mSyncRowId = this.mAdapter.getItemId(position);
                }

                this.requestLayout();
            }

        }
    }

    public void setStackFromRight(boolean stackFromRight) {
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(this.mAdapter != null && this.mIsAttached) {
            switch(keyCode) {
                case 23:
                case 66:
                    if(!this.isEnabled()) {
                        return true;
                    } else if(this.mSelectedPosition >= 0 && this.mAdapter != null && this.mSelectedPosition < this.mAdapter.getCount()) {
                        View view = this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
                        if(view != null) {
                            this.performItemClick(view, this.mSelectedPosition, this.mSelectedRowId);
                            view.setPressed(false);
                        }

                        this.setPressed(false);
                        return true;
                    }
                default:
                    return super.onKeyUp(keyCode, event);
            }
        } else {
            return false;
        }
    }

    protected void setSelectedPositionInt(int position) {
        super.setSelectedPositionInt(position);
        this.setNextSelectedPositionInt(position);
    }

    static int getDistance(Rect source, Rect dest, int direction) {
        int sX;
        int sY;
        int dX;
        int dY;
        switch(direction) {
            case 1:
            case 2:
                sX = source.right + source.width() / 2;
                sY = source.top + source.height() / 2;
                dX = dest.left + dest.width() / 2;
                dY = dest.top + dest.height() / 2;
                break;
            case 17:
                sX = source.left;
                sY = source.top + source.height() / 2;
                dX = dest.right;
                dY = dest.top + dest.height() / 2;
                break;
            case 33:
                sX = source.left + source.width() / 2;
                sY = source.top;
                dX = dest.left + dest.width() / 2;
                dY = dest.bottom;
                break;
            case 66:
                sX = source.right;
                sY = source.top + source.height() / 2;
                dX = dest.left;
                dY = dest.top + dest.height() / 2;
                break;
            case 130:
                sX = source.left + source.width() / 2;
                sY = source.bottom;
                dX = dest.left + dest.width() / 2;
                dY = dest.top;
                break;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }

        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return deltaY * deltaY + deltaX * deltaX;
    }

    boolean resurrectSelection() {
        int childCount = this.getChildCount();
        if(childCount <= 0) {
            return false;
        } else {
            int selectedTop = 0;
            int childrenTop = this.mListPadding.top;
            int childrenBottom = this.getBottom() - this.getTop() - this.mListPadding.bottom;
            int firstPosition = this.mFirstPosition;
            int toPosition = this.mResurrectToPosition;
            boolean down = true;
            int selectedPos;
            int var15;
            if(toPosition >= firstPosition && toPosition < firstPosition + childCount) {
                selectedPos = toPosition;
                View var14 = this.getChildAt(toPosition - this.mFirstPosition);
                selectedTop = var14.getTop();
                var15 = var14.getBottom();
                if(selectedTop < childrenTop) {
                    selectedTop = childrenTop + this.getVerticalFadingEdgeLength();
                } else if(var15 > childrenBottom) {
                    selectedTop = childrenBottom - var14.getMeasuredHeight() - this.getVerticalFadingEdgeLength();
                }
            } else {
                int itemCount;
                if(toPosition < firstPosition) {
                    selectedPos = firstPosition;

                    for(itemCount = 0; itemCount < childCount; ++itemCount) {
                        View i = this.getChildAt(itemCount);
                        int v = i.getTop();
                        if(itemCount == 0) {
                            selectedTop = v;
                            if(firstPosition > 0 || v < childrenTop) {
                                childrenTop += this.getVerticalFadingEdgeLength();
                            }
                        }

                        if(v >= childrenTop) {
                            selectedPos = firstPosition + itemCount;
                            selectedTop = v;
                            break;
                        }
                    }
                } else {
                    itemCount = this.mItemCount;
                    down = false;
                    selectedPos = firstPosition + childCount - 1;

                    for(var15 = childCount - 1; var15 >= 0; --var15) {
                        View var16 = this.getChildAt(var15);
                        int top = var16.getTop();
                        int bottom = var16.getBottom();
                        if(var15 == childCount - 1) {
                            selectedTop = top;
                            if(firstPosition + childCount < itemCount || bottom > childrenBottom) {
                                childrenBottom -= this.getVerticalFadingEdgeLength();
                            }
                        }

                        if(bottom <= childrenBottom) {
                            selectedPos = firstPosition + var15;
                            selectedTop = top;
                            break;
                        }
                    }
                }
            }

            this.mResurrectToPosition = -1;
            this.removeCallbacks(this.mFlingRunnable);
            if(this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }

            this.mTouchMode = -1;
            this.clearScrollingCache();
            this.mSpecificTop = selectedTop;
            selectedPos = this.lookForSelectablePosition(selectedPos, down);
            if(selectedPos >= firstPosition && selectedPos <= this.getLastVisiblePosition()) {
                this.mLayoutMode = 4;
                this.updateSelectorState();
                this.setSelectionInt(selectedPos);
            } else {
                selectedPos = -1;
            }

            this.reportScrollStateChange(0);
            return selectedPos >= 0;
        }
    }

    public void smoothScrollBy(int distance) {
        this.smoothScrollBy(distance, false);
    }

    void smoothScrollBy(int distance, boolean linear) {
        if(this.mFlingRunnable == null) {
            this.mFlingRunnable = new AbsListView.FlingRunnable();
        }

        int firstPos = this.mFirstPosition;
        int childCount = this.getChildCount();
        int lastPos = firstPos + childCount;
        int topLimit = this.getPaddingTop();
        int bottomLimit = this.getHeight() - this.getPaddingBottom();
        if(distance != 0 && this.mItemCount != 0 && childCount != 0 && (firstPos != 0 || this.getChildAt(0).getTop() != topLimit || distance >= 0) && (lastPos != this.mItemCount || this.getChildAt(childCount - 1).getBottom() != bottomLimit || distance <= 0)) {
            this.reportScrollStateChange(2);
            this.mFlingRunnable.startScroll(distance, linear);
        } else {
            this.mFlingRunnable.endFling();
            if(this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
        }

    }

    /**
     * 设置listView滑行帧率
     * @param frameCount 每秒多少帧
     */
    public void setFlipScrollFrameCount(int frameCount) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setFrameCount(frameCount);
        }

    }

    public void setFlipScrollMaxStep(float maxStep) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setMaxStep(maxStep);
        }

    }

    public boolean isListLoopScrolling() {
        return this.mFlingRunnable != null?this.mFlingRunnable.isListLoopScrolling():false;
    }

    public int getLeftScrollDistance() {
        return this.mFlingRunnable != null?this.mFlingRunnable.getLeftScrollDistance():0;
    }

    public void postOnAnimation(Runnable action) {
        this.post(action);
    }

    protected OverScroller getOverScrollerFromFlingRunnable() {
        return this.mFlingRunnable != null?this.mFlingRunnable.mScroller:null;
    }

    public void setFlingInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    final class CheckForTap implements Runnable {
        CheckForTap() {
        }

        public void run() {
            if(AbsListView.this.mTouchMode == 0) {
                AbsListView.this.mTouchMode = 1;
                View child = AbsListView.this.getChildAt(AbsListView.this.mMotionPosition - AbsListView.this.mFirstPosition);
                if(child != null && !child.hasFocusable()) {
                    AbsListView.this.mLayoutMode = 0;
                    if(!AbsListView.this.mDataChanged) {
                        child.setPressed(true);
                        AbsListView.this.setPressed(true);
                        AbsListView.this.layoutChildren();
                        AbsListView.this.positionSelector(AbsListView.this.mMotionPosition, child);
                        AbsListView.this.refreshDrawableState();
                        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        boolean longClickable = AbsListView.this.isLongClickable();
                        if(AbsListView.this.mSelector != null) {
                            Drawable d = AbsListView.this.mSelector.getCurrent();
                            if(d != null && d instanceof TransitionDrawable) {
                                if(longClickable) {
                                    ((TransitionDrawable)d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable)d).resetTransition();
                                }
                            }
                        }

                        if(longClickable) {
                            if(AbsListView.this.mPendingCheckForLongPress == null) {
                                AbsListView.this.mPendingCheckForLongPress = new CheckForLongPress();
                            }

                            AbsListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
                            AbsListView.this.postDelayed(AbsListView.this.mPendingCheckForLongPress, (long)longPressTimeout);
                        } else {
                            AbsListView.this.mTouchMode = 2;
                        }
                    } else {
                        AbsListView.this.mTouchMode = 2;
                    }
                }
            }

        }
    }

    private class FlingRunnable implements Runnable {
        private final OverScroller mScroller = new OverScroller(AbsListView.this.getContext());
        private ListLoopScroller mListLoopScroller = new ListLoopScroller();
        private int mLastFlingY;
        private int mFrameCount;
        private float mDefatultScrollStep = 5.0F; //TODO res 5.0f
        private final Runnable mCheckFlywheel = new Runnable() {
            public void run() {
                int activeId = AbsListView.this.mActivePointerId;
                VelocityTracker vt = AbsListView.this.mVelocityTracker;
                OverScroller scroller = FlingRunnable.this.mScroller;
                if(vt != null && activeId != -1) {
                    vt.computeCurrentVelocity(1000, (float)AbsListView.this.mMaximumVelocity);
                    float yvel = -vt.getYVelocity(activeId);
                    if(Math.abs(yvel) >= (float)AbsListView.this.mMinimumVelocity && scroller.isScrollingInDirection(0.0F, yvel)) {
                        AbsListView.this.postDelayed(this, 40L);
                    } else {
                        FlingRunnable.this.endFling();
                        AbsListView.this.mTouchMode = 3;
                        AbsListView.this.reportScrollStateChange(1);
                    }

                }
            }
        };
        private static final int FLYWHEEL_TIMEOUT = 40;

        FlingRunnable() {
        }

        void start(int initialVelocity) {
            int initialY = initialVelocity < 0?2147483647:0;
            this.mLastFlingY = initialY;
            this.mScroller.setInterpolator((Interpolator)null);
            this.mScroller.fling(0, initialY, 0, initialVelocity, 0, 2147483647, 0, 2147483647);
            AbsListView.this.mTouchMode = 4;
            AbsListView.this.postOnAnimation(this);
        }

        void startSpringback() {
            if(this.mScroller.springBack(0, AbsListView.this.getScrollY(), 0, 0, 0, 0)) {
                AbsListView.this.mTouchMode = 6;
                AbsListView.this.invalidate();
                AbsListView.this.postOnAnimation(this);
            } else {
                AbsListView.this.mTouchMode = -1;
                AbsListView.this.reportScrollStateChange(0);
            }

        }

        void startOverfling(int initialVelocity) {
            this.mScroller.setInterpolator((Interpolator)null);
            this.mScroller.fling(0, AbsListView.this.getScrollY(), 0, initialVelocity, 0, 0, -2147483648, 2147483647, 0, AbsListView.this.getHeight());
            AbsListView.this.mTouchMode = 6;
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void edgeReached(int delta) {
            this.mScroller.notifyVerticalEdgeReached(AbsListView.this.getScrollY(), 0, AbsListView.this.mOverflingDistance);
            int overscrollMode = AbsListView.this.getOverScrollMode();
            if(overscrollMode == 0 || overscrollMode == 1 && !AbsListView.this.contentFits()) {
                AbsListView.this.mTouchMode = 6;
                int vel = (int)this.mScroller.getCurrVelocity();
                if(delta > 0) {
                    AbsListView.this.mEdgeGlowTop.onAbsorb(vel);
                } else {
                    AbsListView.this.mEdgeGlowBottom.onAbsorb(vel);
                }
            } else {
                AbsListView.this.mTouchMode = -1;
                if(AbsListView.this.mPositionScroller != null) {
                    AbsListView.this.mPositionScroller.stop();
                }
            }

            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void startScroll(int distance, boolean linear) {
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

            this.mLastFlingY = 0;
            if(this.mListLoopScroller.isFinished()) {
                this.mListLoopScroller.startScroll(0, distance, frameCount);
                AbsListView.this.mTouchMode = 4;
                AbsListView.this.postOnAnimation(this);
            } else {
                this.mListLoopScroller.startScroll(0, distance, frameCount);
            }

        }

        void endFling() {
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.removeCallbacks(this);
            AbsListView.this.removeCallbacks(this.mCheckFlywheel);
            AbsListView.this.reportScrollStateChange(0);
            AbsListView.this.clearScrollingCache();
            this.mScroller.abortAnimation();
            this.mListLoopScroller.finish();
        }

        void setFrameCount(int frameCount) {
            this.mFrameCount = frameCount;
        }

        void setMaxStep(float maxStep) {
            this.mListLoopScroller.setMaxStep(maxStep);
        }

        int getLeftScrollDistance() {
            return this.mListLoopScroller.getFinal() - this.mListLoopScroller.getCurr();
        }

        boolean isListLoopScrolling() {
            return AbsListView.this.mTouchMode == 4?!this.mListLoopScroller.isFinished():false;
        }

        void flywheelTouch() {
            AbsListView.this.postDelayed(this.mCheckFlywheel, 40L);
        }

        public void run() {
            int scrollY;
            int currY;
            int deltaY;
            boolean crossUp;
            switch(AbsListView.this.mTouchMode) {
                case 3:
                    if(this.mScroller.isFinished()) {
                        return;
                    }
                case 4:
                    if(AbsListView.this.mDataChanged) {
                        AbsListView.this.layoutChildren();
                    }

                    if(AbsListView.this.mItemCount != 0 && AbsListView.this.getChildCount() != 0) {
                        boolean scroller1 = this.mListLoopScroller.computeScrollOffset();
                        scrollY = this.mListLoopScroller.getCurr();
                        currY = this.mLastFlingY - scrollY;
                        View deltaY1;
                        if(currY > 0) {
                            AbsListView.this.mMotionPosition = AbsListView.this.mFirstPosition;
                            deltaY1 = AbsListView.this.getChildAt(0);
                            AbsListView.this.mMotionViewOriginalTop = deltaY1.getTop();
                            currY = Math.min(AbsListView.this.getHeight() - AbsListView.this.getPaddingBottom() - AbsListView.this.getTop() - 1, currY);
                        } else {
                            deltaY = AbsListView.this.getChildCount() - 1;
                            AbsListView.this.mMotionPosition = AbsListView.this.mFirstPosition + deltaY;
                            View crossDown1 = AbsListView.this.getChildAt(deltaY);
                            AbsListView.this.mMotionViewOriginalTop = crossDown1.getTop();
                            currY = Math.max(-(AbsListView.this.getHeight() - AbsListView.this.getPaddingBottom() - AbsListView.this.getPaddingTop() - 1), currY);
                        }

                        deltaY1 = AbsListView.this.getChildAt(AbsListView.this.mMotionPosition - AbsListView.this.mFirstPosition);
                        int crossDown2 = 0;
                        if(deltaY1 != null) {
                            crossDown2 = deltaY1.getTop();
                        }

                        crossUp = AbsListView.this.trackMotionScroll(currY, currY);
                        boolean velocity1 = crossUp && currY != 0;
                        if(velocity1) {
                            if(deltaY1 != null) {
                                int overshoot = -(currY - (deltaY1.getTop() - crossDown2));
                                AbsListView.this.overScrollBy(0, overshoot, 0, AbsListView.this.getScrollY(), 0, 0, 0, AbsListView.this.mOverflingDistance, false);
                            }

                            if(scroller1) {
                                this.edgeReached(currY);
                            }
                        } else if(scroller1 && !velocity1) {
                            if(crossUp) {
                                AbsListView.this.invalidate();
                            }

                            this.mLastFlingY = scrollY;
                            AbsListView.this.postOnAnimation(this);
                        } else {
                            this.endFling();
                        }
                        break;
                    }

                    this.endFling();
                    return;
                case 5:
                default:
                    this.endFling();
                    return;
                case 6:
                    OverScroller scroller = this.mScroller;
                    if(scroller.computeScrollOffset()) {
                        scrollY = AbsListView.this.getScrollY();
                        currY = scroller.getCurrY();
                        deltaY = currY - scrollY;
                        if(AbsListView.this.overScrollBy(0, deltaY, 0, scrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false)) {
                            boolean crossDown = scrollY <= 0 && currY > 0;
                            crossUp = scrollY >= 0 && currY < 0;
                            if(!crossDown && !crossUp) {
                                this.startSpringback();
                            } else {
                                int velocity = (int)scroller.getCurrVelocity();
                                if(crossUp) {
                                    velocity = -velocity;
                                }

                                scroller.abortAnimation();
                                this.start(velocity);
                            }
                        } else {
                            AbsListView.this.invalidate();
                            AbsListView.this.postOnAnimation(this);
                        }
                    } else {
                        this.endFling();
                    }
            }

        }
    }

    class PositionScroller implements Runnable {
        private static final int SCROLL_DURATION = 400;//orc 400 TODO wdz
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_UP_POS = 2;
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_OFFSET = 5;
        private int mMode;
        private int mTargetPos;
        private int mBoundPos;
        private int mLastSeenPos;
        private int mScrollDuration;
        private final int mExtraScroll = ViewConfiguration.get(AbsListView.this.getContext()).getScaledFadingEdgeLength();
        private int mOffsetFromTop;

        PositionScroller() {
        }

        void start(int position) {
            this.stop();
            int firstPos = AbsListView.this.mFirstPosition;
            int lastPos = firstPos + AbsListView.this.getChildCount() - 1;
            int viewTravelCount;
            if(position <= firstPos) {
                viewTravelCount = firstPos - position + 1;
                this.mMode = 2;
            } else {
                if(position < lastPos) {
                    return;
                }

                viewTravelCount = position - lastPos + 1;
                this.mMode = 1;
            }

            if(viewTravelCount > 0) {
                this.mScrollDuration = 400 / viewTravelCount;
            } else {
                this.mScrollDuration = 400;
            }

            this.mTargetPos = position;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            AbsListView.this.post(this);
        }

        void start(int position, int boundPosition) {
            this.stop();
            if(boundPosition == -1) {
                this.start(position);
            } else {
                int firstPos = AbsListView.this.mFirstPosition;
                int lastPos = firstPos + AbsListView.this.getChildCount() - 1;
                int viewTravelCount;
                int boundPosFromFirst;
                int posTravel;
                int boundTravel;
                if(position <= firstPos) {
                    boundPosFromFirst = lastPos - boundPosition;
                    if(boundPosFromFirst < 1) {
                        return;
                    }

                    posTravel = firstPos - position + 1;
                    boundTravel = boundPosFromFirst - 1;
                    if(boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = 4;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = 2;
                    }
                } else {
                    if(position < lastPos) {
                        return;
                    }

                    boundPosFromFirst = boundPosition - firstPos;
                    if(boundPosFromFirst < 1) {
                        return;
                    }

                    posTravel = position - lastPos + 1;
                    boundTravel = boundPosFromFirst - 1;
                    if(boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = 3;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = 1;
                    }
                }

                if(viewTravelCount > 0) {
                    this.mScrollDuration = 400 / viewTravelCount;
                } else {
                    this.mScrollDuration = 400;
                }

                this.mTargetPos = position;
                this.mBoundPos = boundPosition;
                this.mLastSeenPos = -1;
                AbsListView.this.post(this);
            }
        }

        void startWithOffset(int position, int offset) {
            this.startWithOffset(position, offset, 400);
        }

        void startWithOffset(int position, int offset, int duration) {
            this.stop();
            this.mTargetPos = position;
            this.mOffsetFromTop = offset;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            this.mMode = 5;
            int firstPos = AbsListView.this.mFirstPosition;
            int childCount = AbsListView.this.getChildCount();
            int lastPos = firstPos + childCount - 1;
            int viewTravelCount;
            if(position < firstPos) {
                viewTravelCount = firstPos - position;
            } else {
                if(position <= lastPos) {
                    int screenTravelCount1 = AbsListView.this.getChildAt(position - firstPos).getTop();
                    AbsListView.this.smoothScrollBy(screenTravelCount1 - offset);
                    return;
                }

                viewTravelCount = position - lastPos;
            }

            float screenTravelCount = (float)viewTravelCount / (float)childCount;
            this.mScrollDuration = screenTravelCount < 1.0F?(int)(screenTravelCount * (float)duration):(int)((float)duration / screenTravelCount);
            this.mLastSeenPos = -1;
            AbsListView.this.post(this);
        }

        void stop() {
            AbsListView.this.removeCallbacks(this);
        }

        public void run() {
            if(AbsListView.this.mTouchMode == 4 || this.mLastSeenPos == -1) {
                int listHeight = AbsListView.this.getHeight();
                int firstPos = AbsListView.this.mFirstPosition;
                int childCount;
                int position;
                int lastPos;
                int viewTravelCount;
                int targetTop;
                int distance;
                View lastPos1;
                int screenTravelCount1;
                int modifier1;
                switch(this.mMode) {
                    case 1:
                        childCount = AbsListView.this.getChildCount() - 1;
                        position = firstPos + childCount;
                        if(childCount < 0) {
                            return;
                        }

                        if(position == this.mLastSeenPos) {
                            AbsListView.this.post(this);
                            return;
                        }

                        lastPos1 = AbsListView.this.getChildAt(childCount);
                        viewTravelCount = lastPos1.getHeight();
                        screenTravelCount1 = lastPos1.getTop();
                        modifier1 = listHeight - screenTravelCount1;
                        targetTop = position < AbsListView.this.mItemCount - 1?this.mExtraScroll:AbsListView.this.mListPadding.bottom;
                        AbsListView.this.smoothScrollBy(viewTravelCount - modifier1 + targetTop);
                        this.mLastSeenPos = position;
                        if(position < this.mTargetPos) {
                            AbsListView.this.post(this);
                        }
                        break;
                    case 2:
                        if(firstPos == this.mLastSeenPos) {
                            AbsListView.this.post(this);
                            return;
                        }

                        View childCount2 = AbsListView.this.getChildAt(0);
                        if(childCount2 == null) {
                            return;
                        }

                        position = childCount2.getTop();
                        lastPos = firstPos > 0?this.mExtraScroll:AbsListView.this.mListPadding.top;
                        AbsListView.this.smoothScrollBy(position - lastPos);
                        this.mLastSeenPos = firstPos;
                        if(firstPos > this.mTargetPos) {
                            AbsListView.this.post(this);
                        }
                        break;
                    case 3:
                        boolean childCount1 = true;
                        position = AbsListView.this.getChildCount();
                        if(firstPos == this.mBoundPos || position <= 1 || firstPos + position >= AbsListView.this.mItemCount) {
                            return;
                        }

                        lastPos = firstPos + 1;
                        if(lastPos == this.mLastSeenPos) {
                            AbsListView.this.post(this);
                            return;
                        }

                        View viewTravelCount1 = AbsListView.this.getChildAt(1);
                        screenTravelCount1 = viewTravelCount1.getHeight();
                        modifier1 = viewTravelCount1.getTop();
                        targetTop = this.mExtraScroll;
                        if(lastPos < this.mBoundPos) {
                            AbsListView.this.smoothScrollBy(Math.max(0, screenTravelCount1 + modifier1 - targetTop));
                            this.mLastSeenPos = lastPos;
                            AbsListView.this.post(this);
                        } else if(modifier1 > targetTop) {
                            AbsListView.this.smoothScrollBy(modifier1 - targetTop);
                        }
                        break;
                    case 4:
                        childCount = AbsListView.this.getChildCount() - 2;
                        if(childCount < 0) {
                            return;
                        }

                        position = firstPos + childCount;
                        if(position == this.mLastSeenPos) {
                            AbsListView.this.post(this);
                            return;
                        }

                        lastPos1 = AbsListView.this.getChildAt(childCount);
                        viewTravelCount = lastPos1.getHeight();
                        screenTravelCount1 = lastPos1.getTop();
                        modifier1 = listHeight - screenTravelCount1;
                        this.mLastSeenPos = position;
                        if(position > this.mBoundPos) {
                            AbsListView.this.smoothScrollBy(-(modifier1 - this.mExtraScroll));
                            AbsListView.this.post(this);
                        } else {
                            targetTop = listHeight - this.mExtraScroll;
                            distance = screenTravelCount1 + viewTravelCount;
                            if(targetTop > distance) {
                                AbsListView.this.smoothScrollBy(-(targetTop - distance));
                            }
                        }
                        break;
                    case 5:
                        if(this.mLastSeenPos == firstPos) {
                            AbsListView.this.post(this);
                            return;
                        }

                        this.mLastSeenPos = firstPos;
                        childCount = AbsListView.this.getChildCount();
                        position = this.mTargetPos;
                        lastPos = firstPos + childCount - 1;
                        viewTravelCount = 0;
                        if(position < firstPos) {
                            viewTravelCount = firstPos - position + 1;
                        } else if(position > lastPos) {
                            viewTravelCount = position - lastPos;
                        }

                        float screenTravelCount = (float)viewTravelCount / (float)childCount;
                        float modifier = Math.min(Math.abs(screenTravelCount), 1.0F);
                        if(position < firstPos) {
                            AbsListView.this.smoothScrollBy((int)((float)(-AbsListView.this.getHeight()) * modifier));
                            AbsListView.this.post(this);
                        } else if(position > lastPos) {
                            AbsListView.this.smoothScrollBy((int)((float)AbsListView.this.getHeight() * modifier));
                            AbsListView.this.post(this);
                        } else {
                            targetTop = AbsListView.this.getChildAt(position - firstPos).getTop();
                            distance = targetTop - this.mOffsetFromTop;
                            AbsListView.this.smoothScrollBy(distance);
                        }
                }

            }
        }
    }
}
