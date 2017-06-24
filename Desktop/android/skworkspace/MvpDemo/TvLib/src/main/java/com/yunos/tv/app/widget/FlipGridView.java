//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import com.yunos.tv.app.widget.GridView;
import com.yunos.tv.app.widget.ItemFlipScroller;
import com.yunos.tv.app.widget.AbsBaseListView.FixedViewInfo;
import com.yunos.tv.app.widget.AbsBaseListView.RecycleBin;
import com.yunos.tv.app.widget.ItemFlipScroller.ItemFlipScrollerListener;
import java.util.ArrayList;
import java.util.List;

public class FlipGridView extends GridView {
    private boolean DEBUG = true;
    private final String TAG = "FlipGridView";
    private FlipGridView.FlipRunnable mFlipRunnable;
    private List<Integer> mHeaderViewList;
    private List<Integer> mFooterViewList;
    private FlipGridView.OnFlipRunnableListener mFlipGridViewListener;
    private int mLayoutPreFirstPos;
    private int mLayoutPreLastPos;
    private boolean mLockOffset;
    private boolean mLockFlipAnim;
    private long mTestSpeedStartTime;
    private int mTestSpeedFrame;
    protected int mScrollOffset;

    public FlipGridView(Context context) {
        super(context);
        this.init();
    }

    public FlipGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public FlipGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void lockFlipAnimOnceLayout() {
        this.mLockFlipAnim = true;
    }

    public boolean lockKeyEvent(int keyCode) {
        return this.mFlipRunnable.lockKeyEvent(keyCode);
    }

    public boolean isFlipDown() {
        return !this.mFlipRunnable.isFinished()?this.mFlipRunnable.isDown():false;
    }

    public boolean isFlipFinished() {
        return this.mFlipRunnable.isFinished();
    }

    public void setDelayAnim(boolean delay) {
        this.mFlipRunnable.setDelayAnim(delay);
    }

    public void setFlipSelectedPosition(int pos) {
        this.mFlipRunnable.setSelectedPosition(pos);
    }

    public int getFastFlipStep() {
        return this.mFlipRunnable.getFastFlipStep();
    }

    public void stopFlip() {
        this.mFlipRunnable.stop();
    }

    public void startRealScroll(int delta) {
        this.mFlipRunnable.setSelectedPosition(this.mSelectedPosition);
        this.mFlipRunnable.startRealScroll(delta);
    }

    public int getFlipColumnFirstItemLeftMoveDistance(int index) {
        return this.mFlipRunnable.getFlipColumnFirstItemLeftMoveDistance(index);
    }

    protected void correctTooHigh(int numColumns, int verticalSpacing, int childCount) {
        this.mLockFlipAnim = true;
        super.correctTooHigh(numColumns, verticalSpacing, childCount);
        this.mLockFlipAnim = false;
    }

    protected void correctTooLow(int numColumns, int verticalSpacing, int childCount) {
        this.mLockFlipAnim = true;
        super.correctTooLow(numColumns, verticalSpacing, childCount);
        this.mLockFlipAnim = false;
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(this.DEBUG) {
            if(System.currentTimeMillis() - this.mTestSpeedStartTime >= 1000L) {
                Log.d("FlipGridView", "dispatchDraw frame = " + this.mTestSpeedFrame);
                this.mTestSpeedStartTime = System.currentTimeMillis();
                this.mTestSpeedFrame = 0;
            }

            ++this.mTestSpeedFrame;
        }

    }

    protected void startFlip(int offset) {
        this.mLockOffset = true;
        this.flipScrollBy(offset);
    }

    public void offsetChildrenTopAndBottom(int offset) {
        if(this.mTouchMode < 0 && !this.mLockFlipAnim) {
            if(this.mLockOffset) {
                return;
            }

            this.mScrollOffset = offset;
        } else {
            this.mScrollOffset = 0;
            super.offsetChildrenTopAndBottom(offset);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mTouchMode < 0 && this.mFlipRunnable.lockKeyEvent(keyCode)?true:super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!this.mFlipRunnable.isFinished() && (keyCode == 20 || keyCode == 19)) {
            this.mFlipRunnable.startComeDown();
        }

        return super.onKeyUp(keyCode, event);
    }

    protected void layoutChildren() {
        if(!this.mFlipRunnable.isFinished()) {
            this.mLayoutPreFirstPos = this.getFirstVisiblePosition();
            this.mLayoutPreLastPos = this.getLastVisiblePosition();
            if(this.mFlipRunnable.isDown()) {
                this.setMinLastPos(this.mLayoutPreLastPos);
            } else {
                this.setMinFirstPos(this.mLayoutPreFirstPos);
            }
        }

        this.gridViewLayoutChildren();
        this.setMinLastPos(-1);
        this.setMinFirstPos(2147483647);
        if(this.mTouchMode < 0) {
            this.onLayoutChildrenDone();
            if(this.mScrollOffset != 0) {
                this.mFlipRunnable.setStartingFlipScroll();
                this.mFlipRunnable.checkFlipFastScroll();
                this.mFlipRunnable.addView(this.getFirstVisiblePosition(), this.getLastVisiblePosition());
                this.mFlipRunnable.setSelectedPosition(this.mSelectedPosition);
                this.flipScrollBy(this.mScrollOffset);
                this.mScrollOffset = 0;
            } else {
                this.mFlipRunnable.checkFlipFastScroll();
            }
        }

        this.mLockFlipAnim = false;
    }

    private void gridViewLayoutChildren() {
        if(this.mNeedLayout) {
            boolean blockLayoutRequests = this.mBlockLayoutRequests;
            if(!blockLayoutRequests) {
                this.mBlockLayoutRequests = true;
            }

            label367: {
                try {
                    this.invalidate();
                    if(this.mAdapter == null) {
                        this.resetList();
                        return;
                    }

                    int childrenTop = this.mListPadding.top;
                    int childrenBottom = this.getBottom() - this.getTop() - this.mListPadding.bottom;
                    int childCount = this.getChildCount();
                    int index = 0;
                    int delta = 0;
                    View oldSel = null;
                    View oldFirst = null;
                    View newSel = null;
                    switch(this.mLayoutMode) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                            break;
                        case 2:
                        case 9:
                            index = this.mNextSelectedPosition - this.mFirstPosition;
                            if(index >= 0 && index < childCount) {
                                newSel = this.getChildAt(index);
                            }
                            break;
                        case 6:
                            if(this.mNextSelectedPosition >= 0) {
                                delta = this.mNextSelectedPosition - this.mSelectedPosition;
                            }
                            break;
                        case 7:
                        case 8:
                        default:
                            index = this.mSelectedPosition - this.mFirstPosition;
                            if(index >= 0 && index < childCount) {
                                oldSel = this.getChildAt(index);
                            }

                            oldFirst = this.getChildAt(this.getHeaderViewsCount());
                    }

                    boolean dataChanged = this.mDataChanged;
                    if(dataChanged) {
                        this.handleDataChanged();
                    }

                    if(this.mItemCount != 0) {
                        this.setSelectedPositionInt(this.mNextSelectedPosition);
                        if(oldSel != null && this.mSelectedPosition - this.mFirstPosition != index) {
                            index = this.mSelectedPosition - this.mFirstPosition;
                            oldSel = this.getChildAt(index);
                        }

                        int firstPosition = this.mFirstPosition;
                        RecycleBin recycleBin = this.mRecycler;
                        int child;
                        if(dataChanged) {
                            for(child = 0; child < childCount; ++child) {
                                recycleBin.addScrapView(this.getChildAt(child), firstPosition + child);
                            }
                        } else {
                            recycleBin.fillActiveViews(childCount, firstPosition);
                        }

                        this.detachAllViewsFromParent();
                        recycleBin.removeSkippedScrap();
                        View sel;
                        switch(this.mLayoutMode) {
                            case 1:
                                this.mFirstPosition = 0;
                                sel = this.fillFromTop(childrenTop);
                                this.adjustViewsUpOrDown();
                                break;
                            case 2:
                                if(newSel != null) {
                                    sel = this.fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                                } else {
                                    sel = this.fillSelection(childrenTop, childrenBottom);
                                }
                                break;
                            case 3:
                                sel = this.fillUp(this.mItemCount - 1, childrenBottom);
                                this.adjustViewsUpOrDown();
                                break;
                            case 4:
                                sel = this.fillSpecific(this.mSelectedPosition, this.mSpecificTop);
                                break;
                            case 5:
                                sel = this.fillSpecific(this.mSyncPosition, this.mSpecificTop);
                                break;
                            case 6:
                                sel = this.moveSelection(delta, childrenTop, childrenBottom);
                                break;
                            case 7:
                            case 8:
                            default:
                                if(childCount == 0) {
                                    if(!this.mStackFromBottom) {
                                        this.setSelectedPositionInt(this.mAdapter != null && !this.isInTouchMode()?0:-1);
                                        sel = this.fillFromTop(childrenTop);
                                    } else {
                                        child = this.mItemCount - 1;
                                        this.setSelectedPositionInt(this.mAdapter != null && !this.isInTouchMode()?child:-1);
                                        sel = this.fillFromBottom(child, childrenBottom);
                                    }
                                } else if(this.mSelectedPosition >= 0 && this.mSelectedPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mSelectedPosition, oldSel == null?childrenTop:oldSel.getTop());
                                } else if(this.mFirstPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mFirstPosition, oldFirst == null?childrenTop:oldFirst.getTop());
                                } else {
                                    sel = this.fillSpecific(0, childrenTop);
                                }
                                break;
                            case 9:
                                if(newSel != null) {
                                    sel = this.fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                                } else {
                                    sel = this.fillSelectionMiddle(childrenTop, childrenBottom);
                                }
                        }

                        recycleBin.scrapActiveViews();
                        if(sel != null) {
                            this.positionSelector(-1, sel);
                        } else if(this.mTouchMode > 0 && this.mTouchMode < 3) {
                            View var18 = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
                            if(var18 != null) {
                                this.positionSelector(this.mMotionPosition, var18);
                            }
                        } else {
                            this.mSelectorRect.setEmpty();
                        }

                        this.mLayoutMode = 0;
                        this.mDataChanged = false;
                        this.mNeedSync = false;
                        this.setNextSelectedPositionInt(this.mSelectedPosition);
                        if(this.mItemCount > 0) {
                            this.checkSelectionChanged();
                        }
                        break label367;
                    }

                    this.resetList();
                } finally {
                    if(!blockLayoutRequests) {
                        this.mBlockLayoutRequests = false;
                    }

                }

                return;
            }

            this.mNeedLayout = false;
        }
    }

    protected void onLayoutChildrenDone() {
    }

    protected int getFillGapNextChildIndex(boolean isDown) {
        if(this.mTouchMode < 0) {
            int first;
            int numColumn;
            int childIndex;
            int count;
            if(isDown) {
                first = this.getFirstVisiblePosition();
                numColumn = this.getLastVisiblePosition();
                if(first >= numColumn) {
                    return 0;
                } else {
                    childIndex = (numColumn - this.mHeaderViewList.size() + 1) % this.getColumnNum();
                    if(childIndex <= 0) {
                        childIndex = this.getColumnNum();
                    }

                    count = numColumn - (childIndex - 1) - first;
                    if(count >= this.getChildCount()) {
                        count = this.getChildCount() - 1;
                    }

                    return count;
                }
            } else {
                first = this.getFirstVisiblePosition();
                if(first >= this.mHeaderViewList.size()) {
                    numColumn = this.getColumnNum();
                    childIndex = numColumn - 1;
                    count = this.getChildCount();
                    if(count < numColumn) {
                        childIndex = count - 1;
                    }

                    return childIndex;
                } else {
                    return 0;
                }
            }
        } else {
            return super.getFillGapNextChildIndex(isDown);
        }
    }

    protected void onFlipItemRunnableStart() {
        if(this.mFlipGridViewListener != null) {
            this.mFlipGridViewListener.onStart();
        }

    }

    protected void onFlipItemRunnableRunning(float moveRatio, View itemView, int index) {
        if(this.mFlipGridViewListener != null) {
            this.mFlipGridViewListener.onFlipItemRunnable(moveRatio, itemView, index);
        }

    }

    protected void onFlipItemRunnableFinished() {
        this.mLockOffset = false;
        if(this.mFlipGridViewListener != null) {
            this.mFlipGridViewListener.onFinished();
        }

    }

    public void setOnFlipGridViewRunnableListener(FlipGridView.OnFlipRunnableListener l) {
        this.mFlipGridViewListener = l;
    }

    public void addHeaderView(View v) {
        if(v != null && v instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
            FlipGridView.FlipGridViewHeaderOrFooterInterface headView = (FlipGridView.FlipGridViewHeaderOrFooterInterface)v;
            int columnCount = headView.getHorCount();
            int verticalCount = headView.getVerticalCount();
            this.mHeaderViewList.add(Integer.valueOf(this.mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount)));
            super.addHeaderView(v);
        } else {
            throw new IllegalStateException("Cannot add FlipGridView header view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
        }
    }

    public void addHeaderViewInfo(View v) {
        if(v != null && v instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
            FlipGridView.FlipGridViewHeaderOrFooterInterface headView = (FlipGridView.FlipGridViewHeaderOrFooterInterface)v;
            int columnCount = headView.getHorCount();
            int verticalCount = headView.getVerticalCount();
            this.mHeaderViewList.add(Integer.valueOf(this.mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount)));
        } else {
            throw new IllegalStateException("Cannot add FlipGridView header view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
        }
    }

    public boolean removeHeaderView(View headerView) {
        if(this.isFlipFinished()) {
            int headerViewIndex = -1;

            for(int i = 0; i < this.mHeaderViewInfos.size(); ++i) {
                FixedViewInfo headerChildView = (FixedViewInfo)this.mHeaderViewInfos.get(i);
                if(headerChildView != null && headerChildView.view.equals(headerView)) {
                    headerViewIndex = i;
                    break;
                }
            }

            if(headerViewIndex >= 0) {
                this.lockFlipAnimOnceLayout();
                this.mHeaderViewList.remove(headerViewIndex);
                return super.removeHeaderView(headerView);
            }
        } else {
            Log.e("FlipGridView", "flip running can not remove view");
        }

        return false;
    }

    public void clearHeaderViewInfo() {
        this.mHeaderViewList.clear();
    }

    public void addFooterView(View v) {
        if(v != null && v instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
            FlipGridView.FlipGridViewHeaderOrFooterInterface headView = (FlipGridView.FlipGridViewHeaderOrFooterInterface)v;
            int columnCount = headView.getHorCount();
            int verticalCount = headView.getVerticalCount();
            this.mFooterViewList.add(Integer.valueOf(this.mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount)));
            super.addFooterView(v);
        } else {
            throw new IllegalStateException("Cannot add FlipGridView footer view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
        }
    }

    public void addFooterViewInfo(View v) {
        if(v != null && v instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
            FlipGridView.FlipGridViewHeaderOrFooterInterface footerView = (FlipGridView.FlipGridViewHeaderOrFooterInterface)v;
            int columnCount = footerView.getHorCount();
            int verticalCount = footerView.getVerticalCount();
            this.mFooterViewList.add(Integer.valueOf(this.mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount)));
        } else {
            throw new IllegalStateException("Cannot add FlipGridView footer view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
        }
    }

    public boolean removeFooterView(View footerView) {
        if(this.isFlipFinished()) {
            int footerViewIndex = -1;

            for(int i = 0; i < this.mFooterViewInfos.size(); ++i) {
                FixedViewInfo footerChildView = (FixedViewInfo)this.mFooterViewInfos.get(i);
                if(footerChildView != null && footerChildView.view.equals(footerView)) {
                    footerViewIndex = i;
                    break;
                }
            }

            if(footerViewIndex >= 0) {
                this.lockFlipAnimOnceLayout();
                this.mFooterViewInfos.remove(footerViewIndex);
                return super.removeFooterView(footerView);
            }
        } else {
            Log.e("FlipGridView", "flip running can not remove view");
        }

        return false;
    }

    public void clearFooterViewInfo() {
        this.mFooterViewList.clear();
    }

    public int getFlipItemLeftMoveDistance(int index, int secondIndex) {
        return !this.mFlipRunnable.isFinished()?this.mFlipRunnable.getFlipItemLeftMoveDistance(index, secondIndex):0;
    }

    protected boolean isFliping() {
        return !this.mFlipRunnable.isFinished();
    }

    private void flipScrollBy(int distance) {
        Log.i(TAG,"flipScrollBy:distance:"+distance);
        int firstPos = this.mFirstPosition;
        int childCount = this.getChildCount();
        int lastPos = firstPos + childCount;
        int topLimit = this.getPaddingTop();
        int bottomLimit = this.getHeight() - this.getPaddingBottom();
        if(distance != 0 && this.mItemCount != 0 && childCount != 0 && (firstPos != 0 || this.getChildAt(0).getTop() != topLimit || distance <= 0) && (lastPos != this.mItemCount || this.getChildAt(childCount - 1).getBottom() != bottomLimit || distance >= 0)) {
            this.reportScrollStateChange(2);
            this.mFlipRunnable.startScroll(distance);
        } else {
            this.mFlipRunnable.stop();
        }

    }

    private List<Integer> getHeaderViewInfo() {
        return this.mHeaderViewList;
    }

    private List<Integer> getFooterViewInfo() {
        return this.mFooterViewList;
    }

    private void init() {
        this.mFlipRunnable = new FlipGridView.FlipRunnable();
        this.mFlipRunnable.setOnFlipRunnableListener(new FlipGridView.OnFlipRunnableListener() {
            public void onFlipItemRunnable(float moveRatio, View itemView, int index) {
                FlipGridView.this.onFlipItemRunnableRunning(moveRatio, itemView, index);
            }

            public void onFinished() {
                FlipGridView.this.onFlipItemRunnableFinished();
            }

            public void onStart() {
                FlipGridView.this.onFlipItemRunnableStart();
            }
        });
        this.mHeaderViewList = new ArrayList();
        this.mFooterViewList = new ArrayList();
    }

    protected void detachOffScreenChildren(boolean isDown) {
        int numChildren = this.getChildCount();
        int firstPosition = this.mFirstPosition;
        int start = 0;
        int count = 0;
        int i;
        View child;
        if(isDown) {
            boolean bottom = false;

            for(i = 0; i < numChildren; ++i) {
                child = this.getChildAt(i);
                if(child.getBottom() >= 0) {
                    break;
                }

                ++count;
                this.mRecycler.addScrapView(child, firstPosition + i);
            }

            start = 0;
        } else {
            int var10 = this.getHeight();

            for(i = numChildren - 1; i >= 0; --i) {
                child = this.getChildAt(i);
                if(child.getTop() <= var10) {
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

    protected int getSelectedRowItemOffset(int rowStart, View selectedRowView) {
        int offset = 0;
        int tag = ((Integer)selectedRowView.getTag()).intValue();
        int columnCount = this.getColumnNum();
        int tagRowStart = this.getRowStart(tag);
        boolean upRowDelta = false;
        if(tagRowStart > rowStart + columnCount) {
            int upRowDelta1 = (tagRowStart - rowStart) / columnCount - 1;
            offset -= (selectedRowView.getHeight() + this.getVerticalSpacing()) * upRowDelta1;
            if(rowStart < this.mHeaderViewInfos.size()) {
                offset -= selectedRowView.getHeight() + this.getVerticalSpacing();
            }

            int rowStartLeft = this.getFlipColumnFirstItemLeftMoveDistance(rowStart);
            int tagRowLeft = this.getFlipColumnFirstItemLeftMoveDistance(tagRowStart);
            int tagLeft = this.getFlipItemLeftMoveDistance(tag, 0);
            if(this.DEBUG) {
                Log.i("FlipGridView", "getSelectedRowItemOffset mReferenceViewInSelectedRow=" + selectedRowView.getTag() + " tagRowStart=" + tagRowStart + " rowStart=" + rowStart + " offset=" + offset + " height=" + selectedRowView.getHeight() + " upRowDelta=" + upRowDelta1 + " rowStartLeft=" + rowStartLeft + " tagLeft=" + tagLeft + " tagRowLeft=" + tagRowLeft);
            }
        }

        return offset;
    }

    public void setHor_delay_distance(int hor_delay_distance) {
        this.mFlipRunnable.setHor_delay_distance(hor_delay_distance);
    }

    public void setVer_delay_distance(int ver_delay_distance) {
        this.mFlipRunnable.setVer_delay_distance(ver_delay_distance);
    }

    public void setMaxFastStep(int paramInt)
    {
        this.mFlipRunnable.setMaxFastStep(paramInt);
    }

    public void setMin_fast_setp_discance(int min_fast_setp_discance) {
        this.mFlipRunnable.setMin_fast_setp_discance(min_fast_setp_discance);
    }

    public void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
        this.mFlipRunnable.setFlip_scroll_frame_count(flip_scroll_frame_count);
    }

    public void setHor_delay_frame_count(int hor_delay_frame_count) {
        this.mFlipRunnable.setHor_delay_frame_count(hor_delay_frame_count);
    }

    public void setVer_delay_frame_count(int ver_delay_frame_count) {
        this.mFlipRunnable.setVer_delay_frame_count(ver_delay_frame_count);
    }

    public interface FlipGridViewHeaderOrFooterInterface {
        int getHorCount();

        int getVerticalCount();

        View getView(int var1);

        int getViewIndex(View var1);
    }

    private class FlipRunnable implements Runnable {
        private ItemFlipScroller mItemFlipScroller = new ItemFlipScroller();
        private SparseArray<Integer> mFlipItemBottomList = new SparseArray();
        private int mHeaderViewDelta = 0;
        private FlipGridView.OnFlipRunnableListener mOnFlipRunnableListener;

        FlipRunnable() {
            this.mItemFlipScroller.setItemFlipScrollerListener(new ItemFlipScrollerListener() {
                public void onOffsetNewChild(int childIndex, int secondIndex, int delta) {
                    int child = childIndex - FlipGridView.this.getFirstVisiblePosition();
                    View childView = FlipGridView.this.getChildAt(child);
                    if(FlipGridView.this.DEBUG) {
                        Log.i("FlipGridView", "onOffsetNewChild childIndex=" + childIndex + " secondIndex=" + secondIndex + " delta=" + delta + " before bottom=" + childView.getBottom());
                    }

                    if(childView != null && delta != 0) {
                        if(childIndex < FlipGridView.this.getHeaderViewsCount()) {
                            if(childView != null && childView instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
                                FlipGridView.FlipGridViewHeaderOrFooterInterface headerView = (FlipGridView.FlipGridViewHeaderOrFooterInterface)childView;
                                int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
                                if(secondIndex < headerChildCount) {
                                    if(secondIndex == headerChildCount - 1) {
                                        FlipRunnable.this.mHeaderViewDelta = delta;
                                        if(FlipGridView.this.DEBUG) {
                                            Log.i("FlipGridView", "onOffsetNewChild mHeaderViewDelta=" + FlipRunnable.this.mHeaderViewDelta + " secondIndex=" + secondIndex + " headerChildCount=" + headerChildCount);
                                        }

                                        childView.offsetTopAndBottom(delta);
                                        FlipRunnable.this.mFlipItemBottomList.put(FlipRunnable.this.getFlipItemMapKey(childIndex, -1), Integer.valueOf(childView.getBottom()));
                                    }

                                    View headerChildView = headerView.getView(secondIndex);
                                    if(headerChildView != null) {
                                        headerChildView.offsetTopAndBottom(delta - FlipRunnable.this.mHeaderViewDelta);
                                        FlipRunnable.this.mFlipItemBottomList.put(FlipRunnable.this.getFlipItemMapKey(childIndex, secondIndex), Integer.valueOf(headerChildView.getBottom()));
                                    }
                                }
                            } else {
                                childView.offsetTopAndBottom(delta);
                                FlipRunnable.this.mFlipItemBottomList.put(FlipRunnable.this.getFlipItemMapKey(childIndex, 0), Integer.valueOf(childView.getBottom()));
                            }
                        } else {
                            childView.offsetTopAndBottom(delta);
                            FlipRunnable.this.mFlipItemBottomList.put(FlipRunnable.this.getFlipItemMapKey(childIndex, 0), Integer.valueOf(childView.getBottom()));
                        }
                    }

                }

                public void onFinished() {
                    if(FlipRunnable.this.mOnFlipRunnableListener != null) {
                        FlipRunnable.this.mOnFlipRunnableListener.onFinished();
                    }

                }
            });
        }

        void setHor_delay_distance(int hor_delay_distance) {
            this.mItemFlipScroller.setHor_delay_distance(hor_delay_distance);
        }

        void setVer_delay_distance(int ver_delay_distance) {
            this.mItemFlipScroller.setVer_delay_distance(ver_delay_distance);
        }

        void setMin_fast_setp_discance(int min_fast_setp_discance) {
            this.mItemFlipScroller.setMin_fast_setp_discance(min_fast_setp_discance);
        }

        void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
            this.mItemFlipScroller.setFlip_scroll_frame_count(flip_scroll_frame_count);
        }

        void setHor_delay_frame_count(int hor_delay_frame_count) {
            this.mItemFlipScroller.setHor_delay_frame_count(hor_delay_frame_count);
        }

        public void setMaxFastStep(int paramInt)
        {
            this.mItemFlipScroller.setMaxFastStep(paramInt);
        }

        void setVer_delay_frame_count(int ver_delay_frame_count) {
            this.mItemFlipScroller.setVer_delay_frame_count(ver_delay_frame_count);
        }

        public boolean lockKeyEvent(int keyCode) {
            boolean isFinished = this.mItemFlipScroller.isFinished();
            if(FlipGridView.this.DEBUG) {
                Log.i("FlipGridView", "lockKeyEvent isFinished=" + isFinished + " keyCode=" + keyCode + " isDown=" + this.mItemFlipScroller.isDown());
            }

            if(!isFinished) {
                if(keyCode == 19 && this.mItemFlipScroller.isDown()) {
                    return true;
                }

                if(keyCode == 20 && !this.mItemFlipScroller.isDown()) {
                    return true;
                }

                if(keyCode == 21 || keyCode == 22) {
                    return true;
                }
            }

            return false;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.mItemFlipScroller.setSelectedPosition(selectedPosition);
        }

        public int getFastFlipStep() {
            return this.mItemFlipScroller.getFastFlipStep();
        }

        public void startComeDown() {
            this.mItemFlipScroller.startComeDown();
        }

        public void setDelayAnim(boolean delay) {
            this.mItemFlipScroller.setDelayAnim(delay);
        }

        public boolean isFinished() {
            return this.mItemFlipScroller.isFinished();
        }

        public void addView(int start, int end) {
            this.mItemFlipScroller.checkAddView(start, end);
        }

        public void setOnFlipRunnableListener(FlipGridView.OnFlipRunnableListener listener) {
            this.mOnFlipRunnableListener = listener;
        }

        public int getFlipItemLeftMoveDistance(int index, int secondIndex) {
            if(FlipGridView.this.DEBUG) {
                Log.i("FlipGridView", "getFlipItemLeftMoveDistance index=" + index + " secondIndex=" + secondIndex);
            }
            return this.mItemFlipScroller.getFlipItemLeftMoveDistance(index, secondIndex);
        }

        public void setStartingFlipScroll() {
            this.mItemFlipScroller.setStartingFlipScroll();
        }

        public boolean isDown() {
            return this.mItemFlipScroller.isDown();
        }

        public void checkFlipFastScroll() {
            if(!this.mItemFlipScroller.isFinished()) {
                int childCount = FlipGridView.this.getChildCount();

                for(int i = childCount - 1; i >= 0; --i) {
                    int index = i + FlipGridView.this.getFirstVisiblePosition();
                    View childView = FlipGridView.this.getChildAt(i);
                    Integer childBottom;
                    int delta;
                    if(index < FlipGridView.this.getHeaderViewsCount()) {
                        if(childView != null && childView instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
                            FlipGridView.FlipGridViewHeaderOrFooterInterface var11 = (FlipGridView.FlipGridViewHeaderOrFooterInterface)childView;
                            delta = var11.getHorCount() * var11.getVerticalCount();
                            Integer childBottom1 = (Integer)this.mFlipItemBottomList.get(this.getFlipItemMapKey(index, -1));
                            if(childBottom1 != null) {
                                int delta1 = childBottom1.intValue() - childView.getBottom();
                                if(FlipGridView.this.DEBUG) {
                                    Log.i("FlipGridView", "checkFlipFastScroll headerView childBottom=" + childBottom1 + " headerView bottom=" + childView.getBottom() + " delta=" + delta1);
                                }

                                if(delta1 != 0) {
                                    childView.offsetTopAndBottom(delta1);
                                }

                                for(int headerChildIndex = delta - 1; headerChildIndex >= 0; --headerChildIndex) {
                                    childBottom1 = (Integer)this.mFlipItemBottomList.get(this.getFlipItemMapKey(index, headerChildIndex));
                                    if(childBottom1 != null) {
                                        View headerChildView = var11.getView(headerChildIndex);
                                        if(headerChildView != null) {
                                            delta1 = childBottom1.intValue() - headerChildView.getBottom();
                                            if(FlipGridView.this.DEBUG) {
                                                Log.i("FlipGridView", "checkFlipFastScroll headerChild headerChildIndex=" + headerChildIndex + " childBottom=" + childBottom1 + " headerChildView bottom=" + headerChildView.getBottom());
                                            }

                                            if(delta1 != 0) {
                                                this.mItemFlipScroller.setFastScrollOffset(index, headerChildIndex, delta1);
                                                headerChildView.offsetTopAndBottom(delta1);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            childBottom = (Integer)this.mFlipItemBottomList.get(this.getFlipItemMapKey(index, 0));
                            if(childBottom != null) {
                                delta = childBottom.intValue() - childView.getBottom();
                                if(delta != 0) {
                                    this.mItemFlipScroller.setFastScrollOffset(index, 0, delta);
                                    childView.offsetTopAndBottom(delta);
                                }
                            }
                        }
                    } else {
                        childBottom = (Integer)this.mFlipItemBottomList.get(this.getFlipItemMapKey(index, 0));
                        if(FlipGridView.this.DEBUG) {
                            Log.i("FlipGridView", "checkFlipFastScroll adapter index=" + index + " childBottom=" + childBottom + " currBottom=" + childView.getBottom());
                        }

                        if(childBottom != null) {
                            delta = childBottom.intValue() - childView.getBottom();
                            if(delta != 0) {
                                this.mItemFlipScroller.setFastScrollOffset(index, 0, delta);
                                childView.offsetTopAndBottom(delta);
                            }
                        }
                    }
                }
            } else {
                this.mFlipItemBottomList.clear();
            }

        }

        public void startScroll(int distance) {
            if(this.mItemFlipScroller.isFinished()) {
                Log.i(TAG,"startScroll:distance:"+distance);
                this.mItemFlipScroller.clearChild();
                this.mItemFlipScroller.setColumnCount(FlipGridView.this.getColumnNum());
                this.mItemFlipScroller.setHeaderViewInfo(FlipGridView.this.getHeaderViewInfo());
                this.mItemFlipScroller.setFooterViewInfo(FlipGridView.this.getFooterViewInfo());
                this.mItemFlipScroller.setTotalItemCount(FlipGridView.this.getCount());
                this.mItemFlipScroller.addGridView(FlipGridView.this.getFirstVisiblePosition(), FlipGridView.this.getLastVisiblePosition(), distance < 0);
                this.mItemFlipScroller.startComputDistanceScroll(distance);
                FlipGridView.this.postOnAnimation(this);
                if(this.mOnFlipRunnableListener != null) {
                    this.mOnFlipRunnableListener.onStart();
                }
            } else {
                this.mItemFlipScroller.startComputDistanceScroll(distance);
                if(this.mOnFlipRunnableListener != null) {
                    this.mOnFlipRunnableListener.onStart();
                }
            }

        }

        public void startRealScroll(int distance) {
            if(!this.mItemFlipScroller.isFinished()) {
                this.mItemFlipScroller.startRealScroll(distance);
                if(this.mOnFlipRunnableListener != null) {
                    this.mOnFlipRunnableListener.onStart();
                }
            }

        }

        public int getFlipColumnFirstItemLeftMoveDistance(int index) {
            return !this.mItemFlipScroller.isFinished()?this.mItemFlipScroller.getFlipColumnFirstItemLeftMoveDistance(index):0;
        }

        public void run() {
            this.setEachFrame(false);
        }

        public void stop() {
            if(!this.mItemFlipScroller.isFinished()) {
                this.setEachFrame(true);
                this.mItemFlipScroller.finish();
            }

        }

        public int makeHeaderAndFooterViewInfo(int columnCount, int verticalCount) {
            return columnCount << 16 | verticalCount;
        }

        private int getFlipItemMapKey(int index, int secondIndex) {
            return index << 8 | secondIndex;
        }

        private void setEachFrame(boolean finishImmediately) {
            Log.i(TAG,"setEachFrame:finishImmediately:"+finishImmediately);
            int preFirst = FlipGridView.this.getFirstVisiblePosition();
            int preLast = FlipGridView.this.getLastVisiblePosition();
            if(FlipGridView.this.mDataChanged) {
                FlipGridView.this.layoutChildren();
            }

            if(FlipGridView.this.mItemCount != 0 && FlipGridView.this.getChildCount() != 0) {
                boolean more;
                if(!this.mItemFlipScroller.isFinished() && finishImmediately) {
                    more = true;
                } else {
                    more = this.mItemFlipScroller.computeScrollOffset();
                }
                Log.i(TAG,"setEachFrame2:more:"+more);
                if(more) {
                    Log.i(TAG,"setEachFrame2");
                    float moveRatio;
                    if(finishImmediately) {
                        moveRatio = 1.0F;
                    } else {
                        moveRatio = this.mItemFlipScroller.getFlipItemMoveRatio(0, 0);
                    }

                    int childCount = FlipGridView.this.getChildCount();

                    int currFirst;
                    int currLast;
                    int index;
                    int headerChildCount;
                    int delta;
                    for(currFirst = childCount - 1; currFirst >= 0; --currFirst) {
                        currLast = currFirst + FlipGridView.this.getFirstVisiblePosition();
                        boolean headerViewCount = false;
                        View i;
                        if(currLast < FlipGridView.this.getHeaderViewsCount()) {
                            i = FlipGridView.this.getChildAt(currFirst);
                            if(this.mOnFlipRunnableListener != null) {
                                this.mOnFlipRunnableListener.onFlipItemRunnable(moveRatio, i, currLast);
                            }

                            if(i != null && i instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
                                FlipGridView.FlipGridViewHeaderOrFooterInterface var19 = (FlipGridView.FlipGridViewHeaderOrFooterInterface)i;
                                int childView = var19.getHorCount() * var19.getVerticalCount();
                                int headerView = 0;

                                for(headerChildCount = childView - 1; headerChildCount >= 0; --headerChildCount) {
                                    if(finishImmediately) {
                                        delta = this.mItemFlipScroller.getFlipItemLeftMoveDistance(currLast, headerChildCount);
                                    } else {
                                        delta = this.mItemFlipScroller.getCurrDelta(currLast, headerChildCount);
                                    }

                                    if(headerChildCount >= childView - 1) {
                                        headerView = delta;
                                        i.offsetTopAndBottom(delta);
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(currLast, -1), Integer.valueOf(i.getBottom()));
                                    }

                                    View headerChildIndex = var19.getView(headerChildCount);
                                    if(headerChildIndex != null) {
                                        headerChildIndex.offsetTopAndBottom(delta - headerView);
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(currLast, headerChildCount), Integer.valueOf(headerChildIndex.getBottom()));
                                    }
                                }
                            } else {
                                if(finishImmediately) {
                                    delta = this.mItemFlipScroller.getFlipItemLeftMoveDistance(currLast, 0);
                                } else {
                                    delta = this.mItemFlipScroller.getCurrDelta(currLast, 0);
                                }

                                i.offsetTopAndBottom(delta);
                                this.mFlipItemBottomList.put(this.getFlipItemMapKey(currLast, 0), Integer.valueOf(i.getBottom()));
                            }
                        } else {
                            if(finishImmediately) {
                                delta = this.mItemFlipScroller.getFlipItemLeftMoveDistance(currLast, 0);
                            } else {
                                delta = this.mItemFlipScroller.getCurrDelta(currLast, 0);
                            }

                            i = FlipGridView.this.getChildAt(currFirst);
                            if(this.mOnFlipRunnableListener != null) {
                                this.mOnFlipRunnableListener.onFlipItemRunnable(moveRatio, i, currLast);
                            }

                            if(delta != 0) {
                                if(FlipGridView.this.DEBUG) {
                                    if(currLast != 19 && currLast != 13 && currLast != 24) {
                                        i.offsetTopAndBottom(delta);
                                    } else {
                                        index = i.getBottom();
                                        i.offsetTopAndBottom(delta);
                                        Log.i("FlipGridView", "run index=" + currLast + " before=" + index + " delta=" + delta + " after=" + i.getBottom());
                                    }
                                } else {
                                    i.offsetTopAndBottom(delta);
                                }
                            }

                            this.mFlipItemBottomList.put(this.getFlipItemMapKey(currLast, 0), Integer.valueOf(i.getBottom()));
                        }
                    }

                    FlipGridView.this.detachOffScreenChildren(this.mItemFlipScroller.isDown());
                    FlipGridView.this.fillGap(this.mItemFlipScroller.isDown());
                    FlipGridView.this.onScrollChanged(0, 0, 0, 0);
                    FlipGridView.this.invalidate();
                    FlipGridView.this.postOnAnimation(this);
                    this.mItemFlipScroller.addGridView(FlipGridView.this.getFirstVisiblePosition(), FlipGridView.this.getLastVisiblePosition(), this.mItemFlipScroller.isDown());
                    currFirst = FlipGridView.this.getFirstVisiblePosition();
                    currLast = FlipGridView.this.getLastVisiblePosition();
                    delta = FlipGridView.this.getHeaderViewsCount();
                    int var18;
                    if(currFirst < preFirst) {
                        View headerChildView;
                        View var20;
                        FlipGridView.FlipGridViewHeaderOrFooterInterface var21;
                        int var22;
                        if(preFirst <= delta - 1) {
                            for(var18 = preFirst; var18 <= delta - 1; ++var18) {
                                index = var18 - currFirst;
                                if(index >= 0) {
                                    var20 = FlipGridView.this.getChildAt(index);
                                    if(var20 != null && var20 instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
                                        var21 = (FlipGridView.FlipGridViewHeaderOrFooterInterface)var20;
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, -1), Integer.valueOf(var20.getBottom()));
                                        headerChildCount = var21.getHorCount() * var21.getVerticalCount();

                                        for(var22 = headerChildCount - 1; var22 >= 0; --var22) {
                                            headerChildView = var21.getView(var22);
                                            if(headerChildView != null) {
                                                this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, var22), Integer.valueOf(headerChildView.getBottom()));
                                            }
                                        }
                                    } else {
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, 0), Integer.valueOf(var20.getBottom()));
                                    }
                                }
                            }
                        } else if(preFirst > delta - 1 && currFirst <= delta - 1) {
                            var18 = currFirst;

                            label174:
                            while(true) {
                                if(var18 > delta - 1) {
                                    var18 = delta - 1 + 1;

                                    while(true) {
                                        if(var18 >= preFirst) {
                                            break label174;
                                        }

                                        index = var18 - currFirst;
                                        if(index >= 0) {
                                            this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, 0), Integer.valueOf(FlipGridView.this.getChildAt(index).getBottom()));
                                        }

                                        ++var18;
                                    }
                                }

                                index = var18 - currFirst;
                                if(index >= 0) {
                                    var20 = FlipGridView.this.getChildAt(index);
                                    if(var20 != null && var20 instanceof FlipGridView.FlipGridViewHeaderOrFooterInterface) {
                                        var21 = (FlipGridView.FlipGridViewHeaderOrFooterInterface)var20;
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, -1), Integer.valueOf(var20.getBottom()));
                                        headerChildCount = var21.getHorCount() * var21.getVerticalCount();

                                        for(var22 = headerChildCount - 1; var22 >= 0; --var22) {
                                            headerChildView = var21.getView(var22);
                                            if(headerChildView != null) {
                                                this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, var22), Integer.valueOf(headerChildView.getBottom()));
                                            }
                                        }
                                    } else {
                                        this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, 0), Integer.valueOf(var20.getBottom()));
                                    }
                                }

                                ++var18;
                            }
                        } else {
                            for(var18 = currFirst; var18 < preFirst; ++var18) {
                                index = var18 - currFirst;
                                if(index >= 0) {
                                    this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, 0), Integer.valueOf(FlipGridView.this.getChildAt(index).getBottom()));
                                }
                            }
                        }
                    }

                    if(currLast > preLast) {
                        for(var18 = preLast + 1; var18 <= currLast; ++var18) {
                            index = var18 - currFirst;
                            if(index >= 0) {
                                this.mFlipItemBottomList.put(this.getFlipItemMapKey(var18, 0), Integer.valueOf(FlipGridView.this.getChildAt(index).getBottom()));
                            }
                        }
                    }
                }

            }
        }
    }

    public interface OnFlipRunnableListener {
        void onStart();

        void onFlipItemRunnable(float var1, View var2, int var3);

        void onFinished();
    }
}
