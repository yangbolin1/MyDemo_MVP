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

public abstract class AbsHListView extends AbsBaseListView {
    private static final String TAG = "AbsHListView";
    private static final boolean DEBUG = false;
    protected boolean mStackFromBottom = false;
    AbsHListView.FlingRunnable mFlingRunnable = new AbsHListView.FlingRunnable();
    int mSelectedLeft;
    boolean unhandleFullVisible;
    protected boolean mItemsCanFocus = false;
    private AbsHListView.PositionScroller mPositionScroller;
    int mSpecificLeft;
    private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
        public void run() {
            AbsHListView.this.mSuppressSelectionChanged = false;
            AbsHListView.this.selectionChanged();
        }
    };
    protected boolean mShouldStopFling;
    private boolean mSuppressSelectionChanged;
    protected boolean mReceivedInvokeKeyDown;
    int mMotionX;
    int mMotionY;
    int mMotionCorrection;
    int mLastX;
    private int mActivePointerId = -1;
    private int mDirection = 0;
    private Runnable mPendingCheckForTap;
    int mMotionViewOriginalLeft;
    private Runnable mTouchModeReset;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private float mVelocityScale = 1.0F;
    int mOverscrollDistance;
    private EdgeEffect mEdgeGlowLeft;
    private EdgeEffect mEdgeGlowRight;
    private static final boolean PROFILE_SCROLLING = false;
    private boolean mScrollProfilingStarted = false;
    private int mTouchSlop;
    private boolean mFlingProfilingStarted = false;
    int mOverflingDistance;
    static final Interpolator sLinearInterpolator = new LinearInterpolator();
    Interpolator mInterpolator = null;

    public AbsHListView(Context context) {
        super(context);
        this.needMeasureSelectedView = false;
    }

    public AbsHListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.needMeasureSelectedView = false;
    }

    public AbsHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.needMeasureSelectedView = false;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        this.layoutChildren();
        this.mInLayout = false;
    }

    @Override //viewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if(this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }

        if(!this.mIsAttached) {
            return false;
        } else {
            int pointerIndex;
            int x;
            switch(action & 255) {
                case 0:
                    pointerIndex = this.mTouchMode;
                    if(pointerIndex != 6 && pointerIndex != 5) {
                        x = (int)ev.getX();
                        int y = (int)ev.getY();
                        this.mActivePointerId = ev.getPointerId(0);
                        int motionPosition = this.findMotionRow(x);
                        if(pointerIndex != 4 && motionPosition >= 0) {
                            View v = this.getChildAt(motionPosition - this.mFirstPosition);
                            this.mMotionViewOriginalLeft = v.getLeft();
                            this.mMotionX = x;
                            this.mMotionY = y;
                            this.mMotionPosition = motionPosition;
                            this.mTouchMode = 0;
                            this.clearScrollingCache();
                        }

                        this.mLastX = -2147483648;
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

                            x = (int)ev.getX(pointerIndex);
                            this.initVelocityTrackerIfNotExists();
                            this.mVelocityTracker.addMovement(ev);
                            if(this.startScrollIfNeeded(x)) {
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
                                this.mMotionX = this.mLastX = (int)ev.getX();
                                this.mMotionY = (int)ev.getY();
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
                                            this.mPendingCheckForTap = new AbsHListView.CheckForTap();
                                        }

                                        this.postDelayed(this.mPendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
                                    } else if(this.mTouchMode == 4) {
                                        this.createScrollingCache();
                                        this.mTouchMode = 3;
                                        this.mMotionCorrection = 0;
                                        x = this.findMotionRow(index);
                                        this.mFlingRunnable.flywheelTouch();
                                    }
                                }

                                if(x >= 0) {
                                    v = this.getChildAt(x - this.mFirstPosition);
                                    this.mMotionViewOriginalLeft = v.getLeft();
                                }

                                this.mMotionX = index;
                                this.mMotionY = id;
                                this.mMotionPosition = x;
                                this.mLastX = -2147483648;
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
                                                    AbsHListView.this.mTouchMode = -1;
                                                    id2.setPressed(false);
                                                    AbsHListView.this.setPressed(false);
                                                    if(!AbsHListView.this.mDataChanged) {
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
                                    int velocityTracker2 = this.getChildAt(0).getLeft();
                                    initialVelocity = this.getChildAt(motionPosition - 1).getRight();
                                    int contentLeft = this.mListPadding.left;
                                    int contentRight = this.getWidth() - this.mListPadding.right;
                                    if(this.mFirstPosition == 0 && velocityTracker2 >= contentLeft && this.mFirstPosition + motionPosition < this.mItemCount && initialVelocity <= this.getWidth() - contentRight) {
                                        this.mTouchMode = -1;
                                        this.reportScrollStateChange(0);
                                    } else {
                                        VelocityTracker velocityTracker1 = this.mVelocityTracker;
                                        velocityTracker1.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                                        int initialVelocity1 = (int)(velocityTracker1.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
                                        if(Math.abs(initialVelocity1) <= this.mMinimumVelocity || this.mFirstPosition == 0 && velocityTracker2 == contentLeft - this.mOverscrollDistance || this.mFirstPosition + motionPosition == this.mItemCount && initialVelocity == contentRight + this.mOverscrollDistance) {
                                            this.mTouchMode = -1;
                                            this.reportScrollStateChange(0);
                                            if(this.mFlingRunnable != null) {
                                                this.mFlingRunnable.endFling();
                                            }

                                            if(this.mPositionScroller != null) {
                                                this.mPositionScroller.stop();
                                            }
                                        } else {
                                            if(this.mFlingRunnable == null) {
                                                this.mFlingRunnable = new AbsHListView.FlingRunnable();
                                            }

                                            this.reportScrollStateChange(2);
                                            this.mFlingRunnable.start(-initialVelocity1);
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
                                    this.mFlingRunnable = new AbsHListView.FlingRunnable();
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
                        if(this.mEdgeGlowLeft != null) {
                            this.mEdgeGlowLeft.onRelease();
                            this.mEdgeGlowRight.onRelease();
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

                        id = (int)ev.getX(index);
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
                                    this.mFlingRunnable = new AbsHListView.FlingRunnable();
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

                        if(this.mEdgeGlowLeft != null) {
                            this.mEdgeGlowLeft.onRelease();
                            this.mEdgeGlowRight.onRelease();
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
                            this.mMotionViewOriginalLeft = v.getLeft();
                            this.mMotionPosition = motionPosition;
                        }

                        this.mLastX = x;
                        break;
                    case 6:
                        this.onSecondaryPointerUp(ev);
                        index = this.mMotionX;
                        id = this.mMotionY;
                        x = this.pointToPosition(index, id);
                        if(x >= 0) {
                            v = this.getChildAt(x - this.mFirstPosition);
                            this.mMotionViewOriginalLeft = v.getLeft();
                            this.mMotionPosition = x;
                        }

                        this.mLastX = index;
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

    private void scrollIfNeeded(int x) {
        int rawDeltaX = x - this.mMotionX;
        int deltaX = rawDeltaX - this.mMotionCorrection;
        int incrementalDeltaX = this.mLastX != -2147483648?x - this.mLastX:deltaX;
        int newScroll;
        int motionPosition;
        int oldScroll1;
        if(this.mTouchMode == 3) {
            if(x != this.mLastX) {
                if((this.getGroupFlags() & 524288) == 0 && Math.abs(rawDeltaX) > this.mTouchSlop) {
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
                    newScroll = newDirection.getLeft();
                }

                boolean overScrollDistance = false;
                if(incrementalDeltaX != 0) {
                    overScrollDistance = this.trackMotionScroll(deltaX, incrementalDeltaX);
                }

                newDirection = this.getChildAt(oldScroll1);
                if(newDirection != null) {
                    motionPosition = newDirection.getLeft();
                    if(overScrollDistance) {
                        int motionView = -incrementalDeltaX - (motionPosition - newScroll);
                        this.overScrollBy(motionView, 0, this.getScrollX(), 0, 0, 0, this.mOverscrollDistance, 0, true);
                        if(Math.abs(this.mOverscrollDistance) == Math.abs(this.getScrollX()) && this.mVelocityTracker != null) {
                            this.mVelocityTracker.clear();
                        }

                        int overscrollMode = this.getOverScrollMode();
                        if(overscrollMode == 0 || overscrollMode == 1 && !this.contentFits()) {
                            this.mDirection = 0;
                            this.mTouchMode = 5;
                            if(rawDeltaX > 0) {
                                this.mEdgeGlowLeft.onPull((float)motionView / (float)this.getWidth());
                                if(!this.mEdgeGlowRight.isFinished()) {
                                    this.mEdgeGlowRight.onRelease();
                                }
                            } else if(rawDeltaX < 0) {
                                this.mEdgeGlowRight.onPull((float)motionView / (float)this.getWidth());
                                if(!this.mEdgeGlowLeft.isFinished()) {
                                    this.mEdgeGlowLeft.onRelease();
                                }
                            }
                        }
                    }

                    this.mMotionX = x;
                }

                this.mLastX = x;
            }
        } else if(this.mTouchMode == 5 && x != this.mLastX) {
            oldScroll1 = this.getScrollX();
            newScroll = oldScroll1 - incrementalDeltaX;
            int newDirection1 = x > this.mLastX?1:-1;
            if(this.mDirection == 0) {
                this.mDirection = newDirection1;
            }

            int overScrollDistance1 = -incrementalDeltaX;
            if((newScroll >= 0 || oldScroll1 < 0) && (newScroll <= 0 || oldScroll1 > 0)) {
                incrementalDeltaX = 0;
            } else {
                overScrollDistance1 = -oldScroll1;
                incrementalDeltaX += overScrollDistance1;
            }

            if(overScrollDistance1 != 0) {
                this.overScrollBy(overScrollDistance1, 0, this.getScrollX(), 0, 0, 0, this.mOverscrollDistance, 0, true);
                motionPosition = this.getOverScrollMode();
                if(motionPosition == 0 || motionPosition == 1 && !this.contentFits()) {
                    if(rawDeltaX > 0) {
                        this.mEdgeGlowLeft.onPull((float)overScrollDistance1 / (float)this.getWidth());
                        if(!this.mEdgeGlowRight.isFinished()) {
                            this.mEdgeGlowRight.onRelease();
                        }
                    } else if(rawDeltaX < 0) {
                        this.mEdgeGlowRight.onPull((float)overScrollDistance1 / (float)this.getWidth());
                        if(!this.mEdgeGlowLeft.isFinished()) {
                            this.mEdgeGlowLeft.onRelease();
                        }
                    }
                }
            }

            if(incrementalDeltaX != 0) {
                if(this.getScrollX() != 0) {
                    this.scrollTo(0, this.getScrollY());
                    this.invalidateParentIfNeeded();
                }

                this.trackMotionScroll(incrementalDeltaX, incrementalDeltaX);
                this.mTouchMode = 3;
                motionPosition = this.findClosestMotionRow(x);
                this.mMotionCorrection = 0;
                View motionView1 = this.getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalLeft = motionView1 != null?motionView1.getLeft():0;
                this.mMotionX = x;
                this.mMotionPosition = motionPosition;
            }

            this.mLastX = x;
            this.mDirection = newDirection1;
        }

    }

    int findClosestMotionRow(int x) {
        int childCount = this.getChildCount();
        if(childCount == 0) {
            return -1;
        } else {
            int motionRow = this.findMotionRow(x);
            return motionRow != -1?motionRow:this.mFirstPosition + childCount - 1;
        }
    }

    private boolean contentFits() {
        int childCount = this.getChildCount();
        return childCount == 0?true:(childCount != this.mItemCount?false:this.getChildAt(0).getLeft() >= this.mListPadding.left && this.getChildAt(childCount - 1).getRight() <= this.getWidth() - this.mListPadding.right);
    }

    private boolean startScrollIfNeeded(int x) {
        int deltaX = x - this.mMotionX;
        int distance = Math.abs(deltaX);
        boolean overscroll = this.getScrollX() != 0;
        if(!overscroll && distance <= this.mTouchSlop) {
            return false;
        } else {
            this.createScrollingCache();
            if(overscroll) {
                this.mTouchMode = 5;
                this.mMotionCorrection = 0;
            } else {
                this.mTouchMode = 3;
                this.mMotionCorrection = deltaX > 0?this.mTouchSlop:-this.mTouchSlop;
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

            this.scrollIfNeeded(x);
            return true;
        }
    }

    abstract void fillGap(boolean var1);

    abstract int findMotionRow(int var1);

    boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
        if(this.getChildCount() == 0) {
            return true;
        } else {
            boolean isRight = deltaX < 0;
            if(deltaX != deltaX) {
                this.mFlingRunnable.endFling();
            }

            this.offsetChildrenLeftAndRight(deltaX);
            this.detachOffScreenChildren(isRight);
            this.fillGap(isRight);
            this.onScrollChanged(0, 0, 0, 0);
            this.invalidate();
            return false;
        }
    }

    protected boolean detachOffScreenChildren(boolean isRight) {
        int numChildren = this.getChildCount();
        int firstPosition = this.mFirstPosition;
        int start = 0;
        int count = 0;
        int right;
        int i;
        View child;
        if(isRight) {
            right = (this.getGroupFlags() & 34) == 34?this.getPaddingLeft():0;

            for(i = 0; i < numChildren; ++i) {
                child = this.getChildAt(i);
                if(child.getRight() >= right) {
                    break;
                }

                ++count;
                this.mRecycler.addScrapView(child, firstPosition + i);
            }

            start = 0;
        } else {
            right = (this.getGroupFlags() & 34) == 34?this.getWidth() - this.getPaddingRight():this.getWidth();

            for(i = numChildren - 1; i >= 0; --i) {
                child = this.getChildAt(i);
                if(child.getLeft() <= right) {
                    break;
                }

                start = i;
                ++count;
                this.mRecycler.addScrapView(child, firstPosition + i);
            }
        }

        this.detachViewsFromParent(start, count);
        if(isRight) {
            this.mFirstPosition += count;
        }

        return count > 0;
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

    public void setSelectionFromLeft(int position, int y) {
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
                this.mSpecificLeft = this.mListPadding.top + y;
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
                        if(view != null && view.isPressed()) {
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
            int selectedLeft = 0;
            int childrenLeft = this.mListPadding.top;
            int childrenRight = this.getRight() - this.getLeft() - this.mListPadding.right;
            int firstPosition = this.mFirstPosition;
            int toPosition = this.mResurrectToPosition;
            boolean isRight = true;
            int var16;
            int selectedPos;
            if(toPosition >= firstPosition && toPosition < firstPosition + childCount) {
                selectedPos = toPosition;
                View var14 = this.getChildAt(toPosition - this.mFirstPosition);
                selectedLeft = var14.getLeft();
                var16 = var14.getRight();
                if(selectedLeft < childrenLeft) {
                    selectedLeft = childrenLeft + this.getHorizontalFadingEdgeLength();
                } else if(var16 > childrenRight) {
                    selectedLeft = childrenRight - var14.getMeasuredWidth() - this.getHorizontalFadingEdgeLength();
                }
            } else {
                int itemCount;
                if(toPosition < firstPosition) {
                    selectedPos = firstPosition;

                    for(itemCount = 0; itemCount < childCount; ++itemCount) {
                        View i = this.getChildAt(itemCount);
                        int v = i.getLeft();
                        if(itemCount == 0) {
                            selectedLeft = v;
                            if(firstPosition > 0 || v < childrenLeft) {
                                childrenLeft += this.getHorizontalFadingEdgeLength();
                            }
                        }

                        if(v >= childrenLeft) {
                            selectedPos = firstPosition + itemCount;
                            selectedLeft = v;
                            break;
                        }
                    }
                } else {
                    itemCount = this.mItemCount;
                    isRight = false;
                    selectedPos = firstPosition + childCount - 1;

                    for(var16 = childCount - 1; var16 >= 0; --var16) {
                        View var15 = this.getChildAt(var16);
                        int left = var15.getLeft();
                        int right = var15.getRight();
                        if(var16 == childCount - 1) {
                            selectedLeft = left;
                            if(firstPosition + childCount < itemCount || right > childrenRight) {
                                childrenRight -= this.getHorizontalFadingEdgeLength();
                            }
                        }

                        if(right <= childrenRight) {
                            selectedPos = firstPosition + var16;
                            selectedLeft = left;
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
            this.mSpecificLeft = selectedLeft;
            selectedPos = this.lookForSelectablePosition(selectedPos, isRight);
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
            this.mFlingRunnable = new AbsHListView.FlingRunnable();
        }

        int firstPos = this.mFirstPosition;
        int childCount = this.getChildCount();
        int lastPos = firstPos + childCount;
        int leftLimit = this.getPaddingLeft();
        int rightLimit = this.getWidth() - this.getPaddingRight();
        if(distance != 0 && this.mItemCount != 0 && childCount != 0 && (firstPos != 0 || this.getChildAt(0).getLeft() != leftLimit || distance >= 0) && (lastPos != this.mItemCount || this.getChildAt(childCount - 1).getRight() != rightLimit || distance <= 0)) {
            this.reportScrollStateChange(2);
            this.mFlingRunnable.startScroll(distance, linear);
        } else {
            this.mFlingRunnable.endFling();
            if(this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
        }

    }

    public void postOnAnimation(Runnable action) {
        this.post(action);
    }

    public void setFlipScrollFrameCount(int frameCount) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setFrameCount(frameCount);
        }

    }

    public void setFlingScrollMaxStep(float maxStep) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setMaxStep(maxStep);
        }

    }

    public void setFlingSlowDownRatio(float ratio) {
        if(this.mFlingRunnable != null) {
            this.mFlingRunnable.setSlowDownRatio(ratio);
        }

    }

    public int getLeftScrollDistance() {
        return this.mFlingRunnable != null?this.mFlingRunnable.getLeftScrollDistance():0;
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
            if(AbsHListView.this.mTouchMode == 0) {
                AbsHListView.this.mTouchMode = 1;
                View child = AbsHListView.this.getChildAt(AbsHListView.this.mMotionPosition - AbsHListView.this.mFirstPosition);
                if(child != null && !child.hasFocusable()) {
                    AbsHListView.this.mLayoutMode = 0;
                    if(!AbsHListView.this.mDataChanged) {
                        child.setPressed(true);
                        AbsHListView.this.setPressed(true);
                        AbsHListView.this.layoutChildren();
                        AbsHListView.this.positionSelector(AbsHListView.this.mMotionPosition, child);
                        AbsHListView.this.refreshDrawableState();
                        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        boolean longClickable = AbsHListView.this.isLongClickable();
                        if(AbsHListView.this.mSelector != null) {
                            Drawable d = AbsHListView.this.mSelector.getCurrent();
                            if(d != null && d instanceof TransitionDrawable) {
                                if(longClickable) {
                                    ((TransitionDrawable)d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable)d).resetTransition();
                                }
                            }
                        }

                        if(longClickable) {
                            if(AbsHListView.this.mPendingCheckForLongPress == null) {
                                AbsHListView.this.mPendingCheckForLongPress = new CheckForLongPress();
                            }

                            AbsHListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
                            AbsHListView.this.postDelayed(AbsHListView.this.mPendingCheckForLongPress, (long)longPressTimeout);
                        } else {
                            AbsHListView.this.mTouchMode = 2;
                        }
                    } else {
                        AbsHListView.this.mTouchMode = 2;
                    }
                }
            }

        }
    }

    private class FlingRunnable implements Runnable {
        private final OverScroller mScroller = new OverScroller(AbsHListView.this.getContext());
        private int mLastFlingX;
        private ListLoopScroller mListLoopScroller = new ListLoopScroller();
        private int mFrameCount;
        private float mDefatultScrollStep = 5.0F;
        private final Runnable mCheckFlywheel = new Runnable() {
            public void run() {
                int activeId = AbsHListView.this.mActivePointerId;
                VelocityTracker vt = AbsHListView.this.mVelocityTracker;
                OverScroller scroller = FlingRunnable.this.mScroller;
                if(vt != null && activeId != -1) {
                    vt.computeCurrentVelocity(1000, (float)AbsHListView.this.mMaximumVelocity);
                    float yvel = -vt.getYVelocity(activeId);
                    if(Math.abs(yvel) >= (float)AbsHListView.this.mMinimumVelocity && scroller.isScrollingInDirection(0.0F, yvel)) {
                        AbsHListView.this.postDelayed(this, 40L);
                    } else {
                        FlingRunnable.this.endFling();
                        AbsHListView.this.mTouchMode = 3;
                        AbsHListView.this.reportScrollStateChange(1);
                    }

                }
            }
        };
        private static final int FLYWHEEL_TIMEOUT = 40;

        FlingRunnable() {
        }

        void start(int initialVelocitx) {
            int initialX = initialVelocitx < 0?2147483647:0;
            this.mLastFlingX = initialX;
            this.mScroller.setInterpolator((Interpolator)null);
            this.mScroller.fling(initialX, 0, initialVelocitx, 0, 0, 2147483647, 0, 2147483647);
            AbsHListView.this.mTouchMode = 4;
            AbsHListView.this.postOnAnimation(this);
        }

        void startSpringback() {
            if(this.mScroller.springBack(AbsHListView.this.getScrollX(), 0, 0, 0, 0, 0)) {
                AbsHListView.this.mTouchMode = 6;
                AbsHListView.this.invalidate();
                AbsHListView.this.postOnAnimation(this);
            } else {
                AbsHListView.this.mTouchMode = -1;
                AbsHListView.this.reportScrollStateChange(0);
            }

        }

        void startOverfling(int initialVelocitx) {
            this.mScroller.setInterpolator((Interpolator)null);
            this.mScroller.fling(AbsHListView.this.getScrollX(), 0, initialVelocitx, 0, 0, 0, -2147483648, 2147483647, AbsHListView.this.getWidth(), 0);
            AbsHListView.this.mTouchMode = 6;
            AbsHListView.this.invalidate();
            AbsHListView.this.postOnAnimation(this);
        }

        void edgeReached(int delta) {
            this.mScroller.notifyHorizontalEdgeReached(AbsHListView.this.getScrollX(), 0, AbsHListView.this.mOverflingDistance);
            int overscrollMode = AbsHListView.this.getOverScrollMode();
            if(overscrollMode == 0 || overscrollMode == 1 && !AbsHListView.this.contentFits()) {
                AbsHListView.this.mTouchMode = 6;
                int vel = (int)this.mScroller.getCurrVelocity();
                if(delta > 0) {
                    AbsHListView.this.mEdgeGlowLeft.onAbsorb(vel);
                } else {
                    AbsHListView.this.mEdgeGlowRight.onAbsorb(vel);
                }
            } else {
                AbsHListView.this.mTouchMode = -1;
                if(AbsHListView.this.mPositionScroller != null) {
                    AbsHListView.this.mPositionScroller.stop();
                }
            }

            AbsHListView.this.invalidate();
            AbsHListView.this.postOnAnimation(this);
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

            this.mLastFlingX = 0;
            if(this.mListLoopScroller.isFinished()) {
                this.mListLoopScroller.startScroll(0, distance, frameCount);
                AbsHListView.this.mTouchMode = 4;
                AbsHListView.this.postOnAnimation(this);
            } else {
                this.mListLoopScroller.startScroll(0, distance, frameCount);
            }

        }

        void endFling() {
            AbsHListView.this.mTouchMode = -1;
            AbsHListView.this.removeCallbacks(this);
            AbsHListView.this.removeCallbacks(this.mCheckFlywheel);
            AbsHListView.this.reportScrollStateChange(0);
            AbsHListView.this.clearScrollingCache();
            this.mScroller.abortAnimation();
            this.mListLoopScroller.finish();
        }

        int getLeftScrollDistance() {
            return this.mListLoopScroller.getFinal() - this.mListLoopScroller.getCurr();
        }

        public void setFrameCount(int frameCount) {
            this.mFrameCount = frameCount;
        }

        public void setMaxStep(float maxStep) {
            this.mListLoopScroller.setMaxStep(maxStep);
        }

        public void setSlowDownRatio(float ratio) {
            this.mListLoopScroller.setSlowDownRatio(ratio);
        }

        void flywheelTouch() {
            AbsHListView.this.postDelayed(this.mCheckFlywheel, 40L);
        }

        public void run() {
            int scrollX;
            int currX;
            int deltaX;
            boolean crossUp;
            switch(AbsHListView.this.mTouchMode) {
                case 3:
                    if(this.mScroller.isFinished()) {
                        return;
                    }
                case 4:
                    if(AbsHListView.this.mDataChanged) {
                        AbsHListView.this.layoutChildren();
                    }

                    if(AbsHListView.this.mItemCount != 0 && AbsHListView.this.getChildCount() != 0) {
                        boolean scroller1 = this.mListLoopScroller.computeScrollOffset();
                        scrollX = this.mListLoopScroller.getCurr();
                        currX = this.mLastFlingX - scrollX;
                        View deltaX1;
                        if(currX > 0) {
                            AbsHListView.this.mMotionPosition = AbsHListView.this.mFirstPosition;
                            deltaX1 = AbsHListView.this.getChildAt(0);
                            AbsHListView.this.mMotionViewOriginalLeft = deltaX1.getLeft();
                            currX = Math.min(AbsHListView.this.getWidth() - AbsHListView.this.getPaddingRight() - AbsHListView.this.getLeft() - 1, currX);
                        } else {
                            deltaX = AbsHListView.this.getChildCount() - 1;
                            AbsHListView.this.mMotionPosition = AbsHListView.this.mFirstPosition + deltaX;
                            View crossDown1 = AbsHListView.this.getChildAt(deltaX);
                            AbsHListView.this.mMotionViewOriginalLeft = crossDown1.getLeft();
                            currX = Math.max(-(AbsHListView.this.getWidth() - AbsHListView.this.getPaddingLeft() - AbsHListView.this.getPaddingRight() - 1), currX);
                        }

                        deltaX1 = AbsHListView.this.getChildAt(AbsHListView.this.mMotionPosition - AbsHListView.this.mFirstPosition);
                        int crossDown2 = 0;
                        if(deltaX1 != null) {
                            crossDown2 = deltaX1.getLeft();
                        }

                        crossUp = AbsHListView.this.trackMotionScroll(currX, currX);
                        boolean velocity1 = crossUp && currX != 0;
                        if(velocity1) {
                            if(deltaX1 != null) {
                                int overshoot = -(currX - (deltaX1.getLeft() - crossDown2));
                                AbsHListView.this.overScrollBy(overshoot, 0, AbsHListView.this.getScrollX(), 0, 0, 0, AbsHListView.this.mOverflingDistance, 0, false);
                            }

                            if(scroller1) {
                                this.edgeReached(currX);
                            }
                        } else if(scroller1 && !velocity1) {
                            if(crossUp) {
                                AbsHListView.this.invalidate();
                            }

                            this.mLastFlingX = scrollX;
                            AbsHListView.this.postOnAnimation(this);
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
                        scrollX = AbsHListView.this.getScrollX();
                        currX = scroller.getCurrX();
                        deltaX = currX - scrollX;
                        if(AbsHListView.this.overScrollBy(deltaX, 0, scrollX, 0, 0, 0, AbsHListView.this.mOverflingDistance, 0, false)) {
                            boolean crossDown = scrollX <= 0 && currX > 0;
                            crossUp = scrollX >= 0 && currX < 0;
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
                            AbsHListView.this.invalidate();
                            AbsHListView.this.postOnAnimation(this);
                        }
                    } else {
                        this.endFling();
                    }
            }

        }
    }

    class PositionScroller implements Runnable {
        private static final int SCROLL_DURATION = 400;
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
        private final int mExtraScroll = ViewConfiguration.get(AbsHListView.this.getContext()).getScaledFadingEdgeLength();
        private int mOffsetFromLeft;

        PositionScroller() {
        }

        void start(int position) {
            this.stop();
            int firstPos = AbsHListView.this.mFirstPosition;
            int lastPos = firstPos + AbsHListView.this.getChildCount() - 1;
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
            AbsHListView.this.post(this);
        }

        void start(int position, int boundPosition) {
            this.stop();
            if(boundPosition == -1) {
                this.start(position);
            } else {
                int firstPos = AbsHListView.this.mFirstPosition;
                int lastPos = firstPos + AbsHListView.this.getChildCount() - 1;
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
                AbsHListView.this.post(this);
            }
        }

        void startWithOffset(int position, int offset) {
            this.startWithOffset(position, offset, 400);
        }

        void startWithOffset(int position, int offset, int duration) {
            this.stop();
            this.mTargetPos = position;
            this.mOffsetFromLeft = offset;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            this.mMode = 5;
            int firstPos = AbsHListView.this.mFirstPosition;
            int childCount = AbsHListView.this.getChildCount();
            int lastPos = firstPos + childCount - 1;
            int viewTravelCount;
            if(position < firstPos) {
                viewTravelCount = firstPos - position;
            } else {
                if(position <= lastPos) {
                    int screenTravelCount1 = AbsHListView.this.getChildAt(position - firstPos).getLeft();
                    AbsHListView.this.smoothScrollBy(screenTravelCount1 - offset);
                    return;
                }

                viewTravelCount = position - lastPos;
            }

            float screenTravelCount = (float)viewTravelCount / (float)childCount;
            this.mScrollDuration = screenTravelCount < 1.0F?(int)(screenTravelCount * (float)duration):(int)((float)duration / screenTravelCount);
            this.mLastSeenPos = -1;
            AbsHListView.this.post(this);
        }

        void stop() {
            AbsHListView.this.removeCallbacks(this);
        }

        public void run() {
            if(AbsHListView.this.mTouchMode == 4 || this.mLastSeenPos == -1) {
                int listWidth = AbsHListView.this.getWidth();
                int firstPos = AbsHListView.this.mFirstPosition;
                int modifier1;
                int childCount;
                int position;
                int lastPos;
                int viewTravelCount;
                int targetLeft;
                int distance;
                View lastPos1;
                int screenTravelCount1;
                switch(this.mMode) {
                    case 1:
                        childCount = AbsHListView.this.getChildCount() - 1;
                        position = firstPos + childCount;
                        if(childCount < 0) {
                            return;
                        }

                        if(position == this.mLastSeenPos) {
                            AbsHListView.this.post(this);
                            return;
                        }

                        lastPos1 = AbsHListView.this.getChildAt(childCount);
                        viewTravelCount = lastPos1.getWidth();
                        screenTravelCount1 = lastPos1.getLeft();
                        modifier1 = listWidth - screenTravelCount1;
                        targetLeft = position < AbsHListView.this.mItemCount - 1?this.mExtraScroll:AbsHListView.this.mListPadding.bottom;
                        AbsHListView.this.smoothScrollBy(viewTravelCount - modifier1 + targetLeft);
                        this.mLastSeenPos = position;
                        if(position < this.mTargetPos) {
                            AbsHListView.this.post(this);
                        }
                        break;
                    case 2:
                        if(firstPos == this.mLastSeenPos) {
                            AbsHListView.this.post(this);
                            return;
                        }

                        View childCount2 = AbsHListView.this.getChildAt(0);
                        if(childCount2 == null) {
                            return;
                        }

                        position = childCount2.getLeft();
                        lastPos = firstPos > 0?this.mExtraScroll:AbsHListView.this.mListPadding.top;
                        AbsHListView.this.smoothScrollBy(position - lastPos);
                        this.mLastSeenPos = firstPos;
                        if(firstPos > this.mTargetPos) {
                            AbsHListView.this.post(this);
                        }
                        break;
                    case 3:
                        boolean childCount1 = true;
                        position = AbsHListView.this.getChildCount();
                        if(firstPos == this.mBoundPos || position <= 1 || firstPos + position >= AbsHListView.this.mItemCount) {
                            return;
                        }

                        lastPos = firstPos + 1;
                        if(lastPos == this.mLastSeenPos) {
                            AbsHListView.this.post(this);
                            return;
                        }

                        View viewTravelCount1 = AbsHListView.this.getChildAt(1);
                        screenTravelCount1 = viewTravelCount1.getWidth();
                        modifier1 = viewTravelCount1.getLeft();
                        targetLeft = this.mExtraScroll;
                        if(lastPos < this.mBoundPos) {
                            AbsHListView.this.smoothScrollBy(Math.max(0, screenTravelCount1 + modifier1 - targetLeft));
                            this.mLastSeenPos = lastPos;
                            AbsHListView.this.post(this);
                        } else if(modifier1 > targetLeft) {
                            AbsHListView.this.smoothScrollBy(modifier1 - targetLeft);
                        }
                        break;
                    case 4:
                        childCount = AbsHListView.this.getChildCount() - 2;
                        if(childCount < 0) {
                            return;
                        }

                        position = firstPos + childCount;
                        if(position == this.mLastSeenPos) {
                            AbsHListView.this.post(this);
                            return;
                        }

                        lastPos1 = AbsHListView.this.getChildAt(childCount);
                        viewTravelCount = lastPos1.getWidth();
                        screenTravelCount1 = lastPos1.getLeft();
                        modifier1 = viewTravelCount - screenTravelCount1;
                        this.mLastSeenPos = position;
                        if(position > this.mBoundPos) {
                            AbsHListView.this.smoothScrollBy(-(modifier1 - this.mExtraScroll));
                            AbsHListView.this.post(this);
                        } else {
                            targetLeft = listWidth - this.mExtraScroll;
                            distance = screenTravelCount1 + viewTravelCount;
                            if(targetLeft > distance) {
                                AbsHListView.this.smoothScrollBy(-(targetLeft - distance));
                            }
                        }
                        break;
                    case 5:
                        if(this.mLastSeenPos == firstPos) {
                            AbsHListView.this.post(this);
                            return;
                        }

                        this.mLastSeenPos = firstPos;
                        childCount = AbsHListView.this.getChildCount();
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
                            AbsHListView.this.smoothScrollBy((int)((float)(-AbsHListView.this.getWidth()) * modifier));
                            AbsHListView.this.post(this);
                        } else if(position > lastPos) {
                            AbsHListView.this.smoothScrollBy((int)((float)AbsHListView.this.getWidth() * modifier));
                            AbsHListView.this.post(this);
                        } else {
                            targetLeft = AbsHListView.this.getChildAt(position - firstPos).getLeft();
                            distance = targetLeft - this.mOffsetFromLeft;
                            AbsHListView.this.smoothScrollBy(distance);
                        }
                }

            }
        }
    }
}
