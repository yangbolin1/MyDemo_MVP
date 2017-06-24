//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.GridLayoutAnimationController.AnimationParameters;
import android.widget.Checkable;
import com.yunos.tv.app.widget.AbsListView;
import com.yunos.tv.app.widget.AbsBaseListView.FixedViewInfo;
import com.yunos.tv.app.widget.AbsBaseListView.LayoutParams;
import com.yunos.tv.app.widget.AbsBaseListView.RecycleBin;

public class GridView extends AbsListView {
    private static final String TAG = "GridView";
    private static final boolean DEBUG = true;
    public static final int AUTO_FIT = -1;
    public static final int NO_STRETCH = 0;
    public static final int STRETCH_SPACING = 1;
    public static final int STRETCH_COLUMN_WIDTH = 2;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    private int mNumColumns = -1;
    private int mVerticalSpacing = 0;
    private View mReferenceView = null;
    private View mReferenceViewInSelectedRow = null;
    private int mColumnWidth;
    private int mRequestedColumnWidth = -1;
    private int mRequestedNumColumns;
    private int mHorizontalSpacing = 0;
    private int mRequestedHorizontalSpacing;
    private int mStretchMode = 2;
    private int mMinLastPos = -1;
    private int mMinFirstPos = 2147483647;
    int firstColumnMarginleft = 0;

    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridView(Context context) {
        super(context);
    }

    public void setMinLastPos(int pos) {
        this.mMinLastPos = pos;
    }

    public void setMinFirstPos(int pos) {
        this.mMinFirstPos = pos;
    }

    public void setReferenceViewInSelectedRow(View selectedRowView) {
        this.mReferenceViewInSelectedRow = selectedRowView;
    }

    /**
     * 设置列数
     * @param numColumns
     */
    public void setNumColumns(int numColumns) {
        if(numColumns != this.mRequestedNumColumns) {
            this.mRequestedNumColumns = numColumns;
            this.requestLayoutIfNecessary();
        }

    }

    public void setColumnWidth(int columnWidth) {
        if(columnWidth != this.mRequestedColumnWidth) {
            this.mRequestedColumnWidth = columnWidth;
            this.requestLayoutIfNecessary();
        }

    }

    public void setVerticalSpacing(int verticalSpacing) {
        if(verticalSpacing != this.mVerticalSpacing) {
            this.mVerticalSpacing = verticalSpacing;
            this.requestLayoutIfNecessary();
        }

    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        if(horizontalSpacing != this.mRequestedHorizontalSpacing) {
            this.mRequestedHorizontalSpacing = horizontalSpacing;
            this.requestLayoutIfNecessary();
        }

    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    public int getRequestedHorizontalSpacing() {
        return this.mRequestedHorizontalSpacing;
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public int getColumnNum() {
        return this.mNumColumns;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        int closestChildIndex = -1;
        if(gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(this.getScrollX(), this.getScrollY());
            Rect otherRect = this.mTempRect;
            int minDistance = 2147483647;
            int childCount = this.getChildCount();

            for(int i = 0; i < childCount; ++i) {
                if(this.isCandidateSelection(i, direction)) {
                    View other = this.getChildAt(i);
                    boolean isHeaderOrFooter = false;
                    int distance;
                    FixedViewInfo headerorFooterCount;
                    if(this.mHeaderViewInfos != null && this.mHeaderViewInfos.size() > 0) {
                        for(distance = 0; distance < this.mHeaderViewInfos.size(); ++distance) {
                            headerorFooterCount = (FixedViewInfo)this.mHeaderViewInfos.get(distance);
                            if(headerorFooterCount != null && headerorFooterCount.view.equals(other)) {
                                isHeaderOrFooter = true;
                            }
                        }
                    }

                    if(!isHeaderOrFooter && this.mFooterViewInfos != null && this.mFooterViewInfos.size() > 0) {
                        for(distance = 0; distance < this.mFooterViewInfos.size(); ++distance) {
                            headerorFooterCount = (FixedViewInfo)this.mFooterViewInfos.get(distance);
                            if(headerorFooterCount != null && headerorFooterCount.view.equals(other)) {
                                isHeaderOrFooter = true;
                            }
                        }
                    }

                    if(isHeaderOrFooter && other instanceof ViewGroup) {
                        ViewGroup var16 = (ViewGroup)other;
                        int var17 = var16 == null?0:var16.getChildCount();

                        for(int childIndex = 0; childIndex < var17; ++childIndex) {
                            View childView = var16.getChildAt(childIndex);
                            childView.getDrawingRect(otherRect);
                            this.offsetDescendantRectToMyCoords(childView, otherRect);
                            int distance1 = getDistance(previouslyFocusedRect, otherRect, direction);
                            if(distance1 < minDistance) {
                                minDistance = distance1;
                                closestChildIndex = i;
                            }
                        }
                    } else {
                        other.getDrawingRect(otherRect);
                        this.offsetDescendantRectToMyCoords(other, otherRect);
                        distance = getDistance(previouslyFocusedRect, otherRect, direction);
                        if(distance < minDistance) {
                            minDistance = distance;
                            closestChildIndex = i;
                        }
                    }
                }
            }
        }

        if(closestChildIndex >= 0) {
            this.setHeaderSelection(closestChildIndex + this.mFirstPosition);
        } else if(this.getChildCount() > 0 && gainFocus) {
            this.setHeaderSelection(this.mFirstPosition);
        }

    }

    private void setHeaderSelection(int selectedPos) {
        int headerCount = this.getHeaderViewsCount();
        if(selectedPos < headerCount) {
            for(int i = selectedPos; i < headerCount; ++i) {
                View headerView = ((FixedViewInfo)this.mHeaderViewInfos.get(i)).view;
                if(headerView.isFocusable()) {
                    this.setSelection(i);
                    return;
                }
            }

            this.setSelection(headerCount);
        } else {
            this.setSelection(selectedPos);
        }

    }

    private boolean isCandidateSelection(int childIndex, int direction) {
        int count = this.getChildCount();
        int invertedIndex = count - 1 - childIndex;
        int rowStart;
        int rowEnd;
        if(!this.mStackFromBottom) {
            rowStart = childIndex - childIndex % this.mNumColumns;
            rowEnd = Math.max(rowStart + this.mNumColumns - 1, count);
        } else {
            rowEnd = count - 1 - (invertedIndex - invertedIndex % this.mNumColumns);
            rowStart = Math.max(0, rowEnd - this.mNumColumns + 1);
        }

        switch(direction) {
            case 1:
                if(childIndex == rowEnd && rowEnd == count - 1) {
                    return true;
                }

                return false;
            case 2:
                if(childIndex == rowStart && rowStart == 0) {
                    return true;
                }

                return false;
            case 17:
                if(childIndex == rowEnd) {
                    return true;
                }

                return false;
            case 33:
                if(rowEnd == count - 1) {
                    return true;
                }

                return false;
            case 66:
                if(childIndex == rowStart) {
                    return true;
                }

                return false;
            case 130:
                if(rowStart == 0) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
    }

    protected int getFillGapNextChildIndex(boolean isDown) {
        return isDown?this.getChildCount() - 1:0;
    }

    void fillGap(boolean isDown) {
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int count = this.getChildCount();
        boolean paddingBottom;
        int position;
        int var7;
        if(isDown) {
            paddingBottom = false;
            if((this.getGroupFlags() & 34) == 34) {
                var7 = this.getListPaddingTop();
            }

            position = this.mFirstPosition + count;
            if(this.mStackFromBottom) {
                position += numColumns - 1;
            }

            this.fillDown(position, this.getNextTop(position, this.getChildAt(this.getFillGapNextChildIndex(isDown))));
            this.correctTooHigh(numColumns, verticalSpacing, this.getChildCount());
        } else {
            paddingBottom = false;
            if((this.getGroupFlags() & 34) == 34) {
                var7 = this.getListPaddingBottom();
            }

            position = this.mFirstPosition;
            if(this.mStackFromBottom) {
                --position;
            }

            this.fillUpWithHeaderOrFooter(position, this.getNextBottom(position, this.getChildAt(this.getFillGapNextChildIndex(isDown))));
            this.correctTooLow(numColumns, verticalSpacing, this.getChildCount());
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == 0) {
            if(this.mColumnWidth > 0) {
                widthSize = this.mColumnWidth + this.mListPadding.left + this.mListPadding.right;
            } else {
                widthSize = this.mListPadding.left + this.mListPadding.right;
            }

            widthSize += this.getVerticalScrollbarWidth();
        }

        int childWidth = widthSize - this.mListPadding.left - this.mListPadding.right;
        int childHeight = 0;
        byte childState = 0;
        this.mItemCount = this.mAdapter == null?0:this.mAdapter.getCount();
        int count = this.mItemCount;
        int i;
        if(count > this.getHeaderViewsCount() + this.getFooterViewsCount()) {
            View didNotInitiallyFit = this.obtainView(this.getHeaderViewsCount(), this.mIsScrap);
            LayoutParams ourSize = (LayoutParams)didNotInitiallyFit.getLayoutParams();
            if(ourSize == null) {
                ourSize = (LayoutParams)this.generateDefaultLayoutParams();
                didNotInitiallyFit.setLayoutParams(ourSize);
            }

            ourSize.viewType = this.mAdapter.getItemViewType(0);
            ourSize.forceAdd = true;
            i = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, 0), 0, ourSize.height);
            int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, 0), 0, ourSize.width);
            didNotInitiallyFit.measure(childWidthSpec, i);
            childHeight = didNotInitiallyFit.getMeasuredHeight();
            if(this.mNumColumns == -1 && this.mRequestedColumnWidth < 0) {
                this.setColumnWidth(didNotInitiallyFit.getMeasuredWidth());
            }

            combineMeasuredStates(childState, didNotInitiallyFit.getMeasuredState());
            if(this.mRecycler.shouldRecycleViewType(ourSize.viewType)) {
                this.mRecycler.addScrapView(didNotInitiallyFit, -1);
            }
        }

        if(heightMode == 0) {
            heightSize = this.mListPadding.top + this.mListPadding.bottom + childHeight + this.getVerticalFadingEdgeLength() * 2;
        }

        int ourSize1;
        if(heightMode == -2147483648) {
            int didNotInitiallyFit1 = this.mListPadding.top + this.mListPadding.bottom;
            ourSize1 = this.mNumColumns;

            for(i = 0; i < count; i += ourSize1) {
                didNotInitiallyFit1 += childHeight;
                if(i + ourSize1 < count) {
                    didNotInitiallyFit1 += this.mVerticalSpacing;
                }

                if(didNotInitiallyFit1 >= heightSize) {
                    didNotInitiallyFit1 = heightSize;
                    break;
                }
            }

            heightSize = didNotInitiallyFit1;
        }

        boolean didNotInitiallyFit2 = this.determineColumns(childWidth);
        if(widthMode == -2147483648 && this.mRequestedNumColumns != -1) {
            ourSize1 = this.mRequestedNumColumns * this.mColumnWidth + (this.mRequestedNumColumns - 1) * this.mHorizontalSpacing + this.mListPadding.left + this.mListPadding.right;
            if(ourSize1 > widthSize || didNotInitiallyFit2) {
                widthSize |= 16777216;
            }
        }

        this.setMeasuredDimension(widthSize, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    private boolean determineColumns(int availableSpace) {
        int requestedHorizontalSpacing = this.mRequestedHorizontalSpacing;
        int stretchMode = this.mStretchMode;
        int requestedColumnWidth = this.mRequestedColumnWidth;
        boolean didNotInitiallyFit = false;
        if(this.mRequestedNumColumns == -1) {
            if(requestedColumnWidth > 0) {
                this.mNumColumns = (availableSpace + requestedHorizontalSpacing) / (requestedColumnWidth + requestedHorizontalSpacing);
            } else {
                this.mNumColumns = 2;
            }
        } else {
            this.mNumColumns = this.mRequestedNumColumns;
        }

        if(this.mNumColumns <= 0) {
            this.mNumColumns = 1;
        }

        switch(stretchMode) {
            case 0:
                this.mColumnWidth = requestedColumnWidth;
                this.mHorizontalSpacing = requestedHorizontalSpacing;
                break;
            default:
                int spaceLeftOver = availableSpace - this.mNumColumns * requestedColumnWidth - (this.mNumColumns - 1) * requestedHorizontalSpacing;
                if(spaceLeftOver < 0) {
                    didNotInitiallyFit = true;
                }

                switch(stretchMode) {
                    case 1:
                        this.mColumnWidth = requestedColumnWidth;
                        if(this.mNumColumns > 1) {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver / (this.mNumColumns - 1);
                        } else {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
                        }
                        break;
                    case 2:
                        this.mColumnWidth = requestedColumnWidth + spaceLeftOver / this.mNumColumns;
                        this.mHorizontalSpacing = requestedHorizontalSpacing;
                        break;
                    case 3:
                        this.mColumnWidth = requestedColumnWidth;
                        if(this.mNumColumns > 1) {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver / (this.mNumColumns + 1);
                        } else {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
                        }
                }
        }

        return didNotInitiallyFit;
    }

    protected void attachLayoutAnimationParameters(View child, android.view.ViewGroup.LayoutParams params, int index, int count) {
        AnimationParameters animationParams = (AnimationParameters)params.layoutAnimationParameters;
        if(animationParams == null) {
            animationParams = new AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
        animationParams.columnsCount = this.mNumColumns;
        animationParams.rowsCount = count / this.mNumColumns;
        if(!this.mStackFromBottom) {
            animationParams.column = index % this.mNumColumns;
            animationParams.row = index / this.mNumColumns;
        } else {
            int invertedIndex = count - 1 - index;
            animationParams.column = this.mNumColumns - 1 - invertedIndex % this.mNumColumns;
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / this.mNumColumns;
        }

    }

    protected void layoutChildren() {
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
                        default:
                            index = this.mSelectedPosition - this.mFirstPosition;
                            if(index >= 0 && index < childCount && this.mAdapter != null && index < this.mAdapter.getCount()) {
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

    protected void adjustViewsUpOrDown() {
        int childCount = this.getChildCount();
        if(childCount > 0) {
            int delta;
            View child;
            if(!this.mStackFromBottom) {
                child = this.getChildAt(0);
                delta = child.getTop() - this.mListPadding.top;
                if(this.mFirstPosition != 0) {
                    delta -= this.mVerticalSpacing;
                }

                if(delta < 0) {
                    delta = 0;
                }
            } else {
                child = this.getChildAt(childCount - 1);
                delta = child.getBottom() - (this.getHeight() - this.mListPadding.bottom);
                if(this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mVerticalSpacing;
                }

                if(delta > 0) {
                    delta = 0;
                }
            }

            if(delta != 0) {
                this.offsetChildrenTopAndBottom(-delta);
            }
        }

    }

    protected View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowStart = this.mSelectedPosition;
        int rowEnd = -1;
        if(!this.mStackFromBottom) {
            if(selectedPosition >= this.getHeaderViewsCount()) {
                selectedPosition -= this.getHeaderViewsCount();
                rowStart = selectedPosition - selectedPosition % numColumns;
                rowStart += this.getHeaderViewsCount();
            }
        } else {
            int sel = this.mItemCount - 1 - selectedPosition;
            rowEnd = this.mItemCount - 1 - (sel - sel % numColumns);
            rowStart = Math.max(0, rowEnd - numColumns + 1);
        }

        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = this.getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
        View sel1;
        if(rowStart >= this.getHeaderViewsCount() && rowStart <= this.mItemCount - this.getFooterViewsCount() - 1) {
            sel1 = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, selectedTop, true);
        } else {
            sel1 = this.makeHeaderOrFooter(this.mStackFromBottom?rowEnd:rowStart, selectedTop, true);
            numColumns = 1;
        }

        this.mFirstPosition = rowStart;
        View referenceView = this.mReferenceView;
        this.adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        this.adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        if(!this.mStackFromBottom) {
            this.fillUpWithHeaderOrFooter(rowStart, this.getNextBottom(rowStart, referenceView));
            this.adjustViewsUpOrDown();
            this.fillDownWithHeaderOrFooter(rowStart, this.getNextTop(rowStart, referenceView));
        } else {
            this.fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            this.adjustViewsUpOrDown();
            this.fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
        }

        return sel1;
    }

    protected void adjustForBottomFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        if(childInSelectedRow.getBottom() > bottomSelectionPixel) {
            int spaceAbove = childInSelectedRow.getTop() - topSelectionPixel;
            int spaceBelow = childInSelectedRow.getBottom() - bottomSelectionPixel;
            int offset = Math.min(spaceAbove, spaceBelow);
            this.offsetChildrenTopAndBottom(-offset);
        }

    }

    protected void adjustForTopFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        int top = childInSelectedRow.getTop();
        if(childInSelectedRow instanceof GridView.GridViewHeaderViewExpandDistance) {
            top += ((GridView.GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
        }

        int bottom = childInSelectedRow.getBottom();
        if(childInSelectedRow instanceof GridView.GridViewHeaderViewExpandDistance) {
            bottom -= ((GridView.GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
        }

        if(top < topSelectionPixel) {
            int spaceAbove = topSelectionPixel - top;
            int spaceBelow = bottomSelectionPixel - bottom;
            int offset = Math.min(spaceAbove, spaceBelow);
            this.offsetChildrenTopAndBottom(offset);
        }

    }

    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int numColumns, int rowStart) {
        int bottomSelectionPixel = childrenBottom;
        if(rowStart + numColumns - 1 < this.mItemCount - 1) {
            bottomSelectionPixel = childrenBottom - fadingEdgeLength;
        }

        return bottomSelectionPixel;
    }

    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int rowStart) {
        int topSelectionPixel = childrenTop;
        if(rowStart > 0) {
            topSelectionPixel = childrenTop + fadingEdgeLength;
        }

        return topSelectionPixel;
    }

    protected View fillSelection(int childrenTop, int childrenBottom) {
        int selectedPosition = this.reconcileSelectedPosition();
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        int rowStart;
        int fadingEdgeLength;
        if(!this.mStackFromBottom) {
            rowStart = this.getRowStart(selectedPosition);
        } else {
            fadingEdgeLength = this.mItemCount - 1 - selectedPosition;
            rowEnd = this.mItemCount - 1 - (fadingEdgeLength - fadingEdgeLength % numColumns);
            rowStart = Math.max(0, rowEnd - numColumns + 1);
        }

        fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        View sel = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, topSelectionPixel, true);
        this.mFirstPosition = rowStart;
        View referenceView = this.mReferenceView;
        if(!this.mStackFromBottom) {
            this.fillDownWithHeaderOrFooter(rowStart, this.getNextTop(rowStart, referenceView));
            this.pinToBottom(childrenBottom);
            this.fillUpWithHeaderOrFooter(rowStart, this.getNextBottom(rowStart, referenceView));
            this.adjustViewsUpOrDown();
        } else {
            int bottomSelectionPixel = this.getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
            int offset = bottomSelectionPixel - referenceView.getBottom();
            this.offsetChildrenTopAndBottom(offset);
            this.fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
            this.pinToTop(childrenTop);
            this.fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            this.adjustViewsUpOrDown();
        }

        return sel;
    }

    protected View fillSelectionMiddle(int childrenTop, int childrenBottom) {
        int height = this.getHeight();
        int selectedPosition = this.reconcileSelectedPosition();
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        int rowStart;
        int fadingEdgeLength;
        if(!this.mStackFromBottom) {
            rowStart = this.getRowStart(selectedPosition);
        } else {
            fadingEdgeLength = this.mItemCount - 1 - selectedPosition;
            rowEnd = this.mItemCount - 1 - (fadingEdgeLength - fadingEdgeLength % numColumns);
            rowStart = Math.max(0, rowEnd - numColumns + 1);
        }

        fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = this.getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
        View sel = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, topSelectionPixel, true);

        for(int referenceView = 0; referenceView < this.getChildCount(); ++referenceView) {
            View offset = this.getChildAt(referenceView);
            this.adjustForTopFadingEdge(offset, topSelectionPixel, bottomSelectionPixel);
            this.adjustForBottomFadingEdge(offset, topSelectionPixel, bottomSelectionPixel);
        }

        this.mFirstPosition = rowStart;
        View var15 = this.mReferenceView;
        if(!this.mStackFromBottom) {
            this.fillDownWithHeaderOrFooter(rowStart, this.getNextTop(rowStart, var15));
            this.pinToBottom(childrenBottom);
            this.fillUpWithHeaderOrFooter(rowStart, this.getNextBottom(rowStart, var15));
            this.adjustViewsUpOrDown();
        } else {
            int var16 = bottomSelectionPixel - var15.getBottom();
            this.offsetChildrenTopAndBottom(var16);
            this.fillUp(rowStart - 1, var15.getTop() - verticalSpacing);
            this.pinToTop(childrenTop);
            this.fillDown(rowEnd + numColumns, var15.getBottom() + verticalSpacing);
            this.adjustViewsUpOrDown();
        }

        return sel;
    }

    private void pinToTop(int childrenTop) {
        if(this.mFirstPosition == 0) {
            int top = this.getChildAt(0).getTop();
            int offset = childrenTop - top;
            if(offset < 0) {
                this.offsetChildrenTopAndBottom(offset);
            }
        }

    }

    private void pinToBottom(int childrenBottom) {
        int count = this.getChildCount();
        if(this.mFirstPosition + count == this.mItemCount) {
            int bottom = this.getChildAt(count - 1).getBottom();
            int offset = childrenBottom - bottom;
            if(offset > 0) {
                this.offsetChildrenTopAndBottom(offset);
            }
        }

    }

    int findMotionRow(int y) {
        int childCount = this.getChildCount();
        if(childCount > 0) {
            int numColumns = this.mNumColumns;
            int i;
            if(!this.mStackFromBottom) {
                for(i = 0; i < childCount; i += numColumns) {
                    if(y <= this.getChildAt(i).getBottom()) {
                        return this.mFirstPosition + i;
                    }
                }
            } else {
                for(i = childCount - 1; i >= 0; i -= numColumns) {
                    if(y >= this.getChildAt(i).getTop()) {
                        return this.mFirstPosition + i;
                    }
                }
            }
        }

        return -1;
    }

    protected View fillSpecific(int position, int top) {
        int numColumns = this.mNumColumns;
        int motionRowStart = position;
        int motionRowEnd = -1;
        if(!this.mStackFromBottom) {
            if(position >= this.getHeaderViewsCount()) {
                position -= this.getHeaderViewsCount();
                motionRowStart = position - position % numColumns;
                motionRowStart += this.getHeaderViewsCount();
            }
        } else {
            int temp = this.mItemCount - 1 - position;
            motionRowEnd = this.mItemCount - 1 - (temp - temp % numColumns);
            motionRowStart = Math.max(0, motionRowEnd - numColumns + 1);
        }

        View temp1 = null;
        if(motionRowStart >= this.getHeaderViewsCount() && motionRowStart <= this.mItemCount - this.getFooterViewsCount() - 1) {
            temp1 = this.makeRow(this.mStackFromBottom?motionRowEnd:motionRowStart, top, true);
        } else {
            temp1 = this.makeHeaderOrFooter(motionRowStart, top, true);
            numColumns = 1;
        }

        this.mFirstPosition = motionRowStart;
        View referenceView = this.mReferenceView;
        if(referenceView == null) {
            return null;
        } else {
            int verticalSpacing = this.mVerticalSpacing;
            View above;
            View below;
            int childCount;
            if(!this.mStackFromBottom) {
                above = this.fillUpWithHeaderOrFooter(motionRowStart, this.getNextBottom(motionRowStart, referenceView));
                this.adjustViewsUpOrDown();
                below = this.fillDownWithHeaderOrFooter(motionRowStart, this.getNextTop(motionRowStart, referenceView));
                childCount = this.getChildCount();
                if(childCount > 0) {
                    this.correctTooHigh(numColumns, verticalSpacing, childCount);
                }
            } else {
                below = this.fillDown(motionRowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
                this.adjustViewsUpOrDown();
                above = this.fillUp(motionRowStart - 1, referenceView.getTop() - verticalSpacing);
                childCount = this.getChildCount();
                if(childCount > 0) {
                    this.correctTooLow(numColumns, verticalSpacing, childCount);
                }
            }

            return temp1 != null?temp1:(above != null?above:below);
        }
    }

    protected void correctTooHigh(int numColumns, int verticalSpacing, int childCount) {
        int lastPosition = this.mFirstPosition + childCount - 1;
        if(lastPosition == this.mItemCount - 1 && childCount > 0) {
            View lastChild = this.getChildAt(childCount - 1);
            int lastBottom = lastChild.getBottom();
            if(lastChild instanceof GridView.GridViewHeaderViewExpandDistance) {
                lastBottom -= ((GridView.GridViewHeaderViewExpandDistance)lastChild).getDownExpandDistance();
            }

            int end = this.getBottom() - this.getTop() - this.mListPadding.bottom;
            int bottomOffset = end - lastBottom;
            View firstChild = this.getChildAt(0);
            int firstTop = firstChild.getTop();
            if(firstChild instanceof GridView.GridViewHeaderViewExpandDistance) {
                firstTop += ((GridView.GridViewHeaderViewExpandDistance)firstChild).getUpExpandDistance();
            }

            if(bottomOffset > 0 && (this.mFirstPosition > 0 || firstTop < this.mListPadding.top)) {
                if(this.mFirstPosition == 0) {
                    bottomOffset = Math.min(bottomOffset, this.mListPadding.top - firstTop);
                }

                this.offsetChildrenTopAndBottom(bottomOffset);
                if(this.mFirstPosition > 0) {
                    this.fillUp(this.mFirstPosition - this.mNumColumns, this.getNextBottom(this.mFirstPosition - 1, firstChild));
                    this.adjustViewsUpOrDown();
                }
            }
        }

    }

    protected void correctTooLow(int numColumns, int verticalSpacing, int childCount) {
        if(this.mFirstPosition == 0 && childCount > 0) {
            View firstChild = this.getChildAt(0);
            int firstTop = firstChild.getTop();
            if(firstChild instanceof GridView.GridViewHeaderViewExpandDistance) {
                firstTop += ((GridView.GridViewHeaderViewExpandDistance)firstChild).getUpExpandDistance();
            }

            int start = this.mListPadding.top;
            int end = this.getBottom() - this.getTop() - this.mListPadding.bottom;
            int topOffset = firstTop - start;
            View lastChild = this.getChildAt(childCount - 1);
            int lastBottom = lastChild.getBottom();
            if(lastChild instanceof GridView.GridViewHeaderViewExpandDistance) {
                lastBottom -= ((GridView.GridViewHeaderViewExpandDistance)lastChild).getDownExpandDistance();
            }

            int lastPosition = this.mFirstPosition + childCount - 1;
            if(topOffset > 0 && (lastPosition < this.mItemCount - 1 || lastBottom > end)) {
                if(lastPosition == this.mItemCount - 1) {
                    topOffset = Math.min(topOffset, lastBottom - end);
                }

                this.offsetChildrenTopAndBottom(-topOffset);
                if(lastPosition < this.mItemCount - 1) {
                    this.fillDown(lastPosition + this.mNumColumns, this.getNextTop(lastPosition + 1, lastChild));
                    this.adjustViewsUpOrDown();
                }
            }
        }

    }

    protected View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if(this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }

        this.mFirstPosition -= this.mFirstPosition % this.mNumColumns;
        return this.fillDown(this.mFirstPosition, nextTop);
    }

    protected View fillFromBottom(int lastPosition, int nextBottom) {
        lastPosition = Math.max(lastPosition, this.mSelectedPosition);
        lastPosition = Math.min(lastPosition, this.mItemCount - 1);
        int invertedPosition = this.mItemCount - 1 - lastPosition;
        lastPosition = this.mItemCount - 1 - (invertedPosition - invertedPosition % this.mNumColumns);
        return this.fillUp(lastPosition, nextBottom);
    }

    private View makeHeaderOrFooter(int pos, int y, boolean flow) {
        int columnWidth = this.mColumnWidth;
        boolean isLayoutRtl = false;
        int horizontalSpacing = this.mHorizontalSpacing;
        int nextLeft = this.mListPadding.left;
        boolean selected = pos == this.mSelectedPosition;
        int where = flow?-1:0;
        this.mReferenceView = this.makeAndAddView(pos, y, flow, nextLeft, selected, where, this.getWidth() - this.mListPadding.left - this.mListPadding.right);
        boolean hasFocus = this.shouldShowSelector();
        boolean inClick = this.touchModeDrawsInPressedState();
        View selectedView = null;
        if(selected && (hasFocus || inClick)) {
            selectedView = this.mReferenceView;
        }

        if(selectedView != null) {
            this.mReferenceViewInSelectedRow = this.mReferenceView;
        }

        return this.mReferenceView;
    }

    public void setFirstColumnMarginleft(int firstColumnMarginleft) {
        this.firstColumnMarginleft = firstColumnMarginleft;
    }

    private View makeRow(int startPos, int y, boolean flow) {
        int columnWidth = this.mColumnWidth;
        int horizontalSpacing = this.mHorizontalSpacing;
        boolean isLayoutRtl = false;
        int nextLeft = this.firstColumnMarginleft + this.mListPadding.left + (this.mStretchMode == 3?horizontalSpacing:0);
        int last;
        if(!this.mStackFromBottom) {
            last = Math.min(startPos + this.mNumColumns, this.mItemCount - this.getFooterViewsCount());
        } else {
            last = startPos + 1;
            startPos = Math.max(0, startPos - this.mNumColumns + 1);
            if(last - startPos < this.mNumColumns) {
                int selectedView = (this.mNumColumns - (last - startPos)) * (columnWidth + horizontalSpacing);
                nextLeft += 1 * selectedView;
            }
        }

        View var17 = null;
        boolean hasFocus = this.shouldShowSelector();
        boolean inClick = this.touchModeDrawsInPressedState();
        int selectedPosition = this.mSelectedPosition;
        View child = null;

        for(int pos = startPos; pos < last; ++pos) {
            boolean selected = pos == selectedPosition;
            int where = flow?-1:pos - startPos;
            child = this.makeAndAddView(pos, y, flow, nextLeft, selected, where, this.mColumnWidth);
            nextLeft += 1 * columnWidth;
            if(pos < last - 1) {
                nextLeft += horizontalSpacing;
            }

            if(selected && (hasFocus || inClick)) {
                var17 = child;
            }
        }

        this.mReferenceView = child;
        if(var17 != null) {
            this.mReferenceViewInSelectedRow = this.mReferenceView;
        }

        return var17;
    }

    private View fillUpWithHeaderOrFooter(int rowStart, int nextBottom) {
        return rowStart > 0?(!this.isHeader(rowStart - 1) && !this.isFooter(rowStart - 1)?(this.isFooter(rowStart) && !this.isFooter(rowStart - 1)?this.fillUp(this.getRowStart(rowStart - 1), nextBottom):(!this.isHeader(rowStart) && this.isHeader(rowStart - 1)?this.fillUp(rowStart - 1, nextBottom):this.fillUp(rowStart - this.mNumColumns, nextBottom))):this.fillUp(rowStart - 1, nextBottom)):null;
    }

    protected View fillUp(int pos, int nextBottom) {
        View selectedView = null;
        int end = 0;
        if((this.getGroupFlags() & 34) == 34) {
            end = this.mListPadding.top;
        }

        while((nextBottom > end || pos >= this.mMinFirstPos) && pos >= 0) {
            View temp;
            if(this.isHeader(pos)) {
                temp = this.makeHeaderOrFooter(pos, nextBottom, false);
                if(temp != null) {
                    selectedView = temp;
                }

                nextBottom = this.getNextBottom(pos, this.mReferenceView);
                this.mFirstPosition = pos--;
            } else if(this.isFooter(pos)) {
                temp = this.makeHeaderOrFooter(pos, nextBottom, false);
                if(temp != null) {
                    selectedView = temp;
                }

                nextBottom = this.getNextBottom(pos, this.mReferenceView);
                this.mFirstPosition = pos--;
                pos = this.getRowStart(pos);
            } else {
                temp = this.makeRow(pos, nextBottom, false);
                if(temp != null) {
                    selectedView = temp;
                }

                nextBottom = this.getNextBottom(pos, this.mReferenceView);
                this.mFirstPosition = pos;
                if(pos - this.mNumColumns > this.getHeaderViewsCount() - 1) {
                    pos -= this.mNumColumns;
                } else {
                    pos = this.getHeaderViewsCount() - 1;
                }
            }
        }

        if(this.mStackFromBottom) {
            this.mFirstPosition = Math.max(0, pos + 1);
        }

        return selectedView;
    }

    private int getNextBottom(int pos, View referenceView) {
        int nextBottom;
        if(pos == 0) {
            nextBottom = referenceView.getTop();
        } else {
            nextBottom = referenceView.getTop() - this.mVerticalSpacing;
        }

        if(referenceView instanceof GridView.GridViewHeaderViewExpandDistance) {
            nextBottom -= ((GridView.GridViewHeaderViewExpandDistance)referenceView).getDownExpandDistance();
        }

        return nextBottom;
    }

    private View fillDownWithHeaderOrFooter(int rowStart, int nextTop) {
        return !this.isHeader(rowStart + 1) && !this.isFooter(rowStart + 1)?(this.isHeader(rowStart) && !this.isHeader(rowStart + 1)?this.fillDown(rowStart + 1, nextTop):(!this.isFooter(rowStart) && this.isFooter(rowStart + 1)?this.fillDown(rowStart + 1, nextTop):this.fillDown(Math.min(rowStart + this.mNumColumns, this.mItemCount - this.getFooterViewsCount()), nextTop))):this.fillDown(rowStart + 1, nextTop);
    }

    private View fillDown(int pos, int nextTop) {
        View selectedView = null;
        int end = this.getBottom() - this.getTop();
        if((this.getGroupFlags() & 34) == 34) {
            end -= this.mListPadding.bottom;
        }

        while((nextTop < end || pos <= this.mMinLastPos) && pos < this.mItemCount) {
            View temp;
            if(pos >= this.getHeaderViewsCount() && pos <= this.mItemCount - this.getFooterViewsCount() - 1) {
                temp = this.makeRow(pos, nextTop, true);
                if(temp != null) {
                    selectedView = temp;
                }

                nextTop = this.getNextTop(pos, this.mReferenceView);
                if(pos + this.mNumColumns < this.mItemCount - this.getFooterViewsCount()) {
                    pos += this.mNumColumns;
                } else {
                    pos = this.mItemCount - this.getFooterViewsCount();
                }
            } else {
                if(pos < this.getHeaderViewsCount()) {
                    temp = ((FixedViewInfo)this.mHeaderViewInfos.get(pos)).view;
                    if(temp != null && temp instanceof GridView.GridViewHeaderViewExpandDistance) {
                        nextTop -= ((GridView.GridViewHeaderViewExpandDistance)temp).getUpExpandDistance();
                    }
                }

                temp = this.makeHeaderOrFooter(pos, nextTop, true);
                if(temp != null) {
                    selectedView = temp;
                }

                nextTop = this.getNextTop(pos, this.mReferenceView);
                ++pos;
            }
        }

        return selectedView;
    }

    private int getNextTop(int pos, View referenceView) {
        int nextTop = referenceView.getBottom() + this.mVerticalSpacing;
        if(referenceView instanceof GridView.GridViewHeaderViewExpandDistance) {
            nextTop -= ((GridView.GridViewHeaderViewExpandDistance)referenceView).getDownExpandDistance();
        }

        return nextTop;
    }

    protected int getRowStart(int position) {
        int rowStart;
        if(position < this.getHeaderViewsCount()) {
            rowStart = position;
        } else if(position < this.mItemCount - this.getFooterViewsCount()) {
            int newPosition = position - this.getHeaderViewsCount();
            rowStart = newPosition - newPosition % this.mNumColumns + this.getHeaderViewsCount();
        } else {
            rowStart = position;
        }

        return rowStart;
    }

    protected View moveSelection(int delta, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        int oldRowStart;
        int rowStart;
        int rowDelta;
        if(!this.mStackFromBottom) {
            oldRowStart = this.getRowStart(selectedPosition - delta);
            rowStart = this.getRowStart(selectedPosition);
        } else {
            rowDelta = this.mItemCount - 1 - selectedPosition;
            rowEnd = this.mItemCount - 1 - (rowDelta - rowDelta % numColumns);
            rowStart = Math.max(0, rowEnd - numColumns + 1);
            rowDelta = this.mItemCount - 1 - (selectedPosition - delta);
            oldRowStart = this.mItemCount - 1 - (rowDelta - rowDelta % numColumns);
            oldRowStart = Math.max(0, oldRowStart - numColumns + 1);
        }

        rowDelta = rowStart - oldRowStart;
        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = this.getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
        this.mFirstPosition = rowStart;
        View sel;
        View referenceView;
        int oldTop;
        if(rowDelta > 0) {
            oldTop = this.mReferenceViewInSelectedRow == null?0:this.mReferenceViewInSelectedRow.getBottom();
            if(this.mReferenceViewInSelectedRow != null && this.mReferenceViewInSelectedRow instanceof GridView.GridViewHeaderViewExpandDistance) {
                oldTop -= ((GridView.GridViewHeaderViewExpandDistance)this.mReferenceViewInSelectedRow).getDownExpandDistance();
            }

            if(rowStart >= this.getHeaderViewsCount() && rowStart <= this.mItemCount - 1 - this.getFooterViewsCount()) {
                sel = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, oldTop + verticalSpacing, true);
            } else {
                sel = this.makeHeaderOrFooter(rowStart, oldTop + verticalSpacing, true);
            }

            referenceView = this.mReferenceView;
            this.adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else if(rowDelta < 0) {
            oldTop = this.mReferenceViewInSelectedRow == null?0:this.mReferenceViewInSelectedRow.getTop();
            if(rowStart >= this.getHeaderViewsCount() && rowStart <= this.mItemCount - 1 - this.getFooterViewsCount()) {
                sel = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, oldTop - verticalSpacing, false);
            } else {
                sel = this.makeHeaderOrFooter(rowStart, oldTop - verticalSpacing, false);
            }

            referenceView = this.mReferenceView;
            this.adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else {
            oldTop = this.mReferenceViewInSelectedRow == null?0:this.mReferenceViewInSelectedRow.getTop();
            if(rowStart >= this.getHeaderViewsCount() && rowStart <= this.mItemCount - 1 - this.getFooterViewsCount()) {
                sel = this.makeRow(this.mStackFromBottom?rowEnd:rowStart, oldTop, true);
            } else {
                sel = this.makeHeaderOrFooter(rowStart, oldTop, true);
            }

            referenceView = this.mReferenceView;
        }

        if(!this.mStackFromBottom) {
            this.fillUpWithHeaderOrFooter(rowStart, this.getNextBottom(rowStart, referenceView));
            this.adjustViewsUpOrDown();
            this.fillDownWithHeaderOrFooter(rowStart, this.getNextTop(rowStart, referenceView));
        } else {
            this.fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            this.adjustViewsUpOrDown();
            this.fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
        }

        return sel;
    }

    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected, int where, int width) {
        View child;
        if(!this.mDataChanged) {
            child = this.mRecycler.getActiveView(position);
            if(child != null) {
                this.setupChild(child, position, y, flow, childrenLeft, selected, true, where, width);
                return child;
            }
        }

        child = this.obtainView(position, this.mIsScrap);
        this.setupChild(child, position, y, flow, childrenLeft, selected, this.mIsScrap[0], where, width);
        return child;
    }

    private void setupChild(View child, int position, int y, boolean flow, int childrenLeft, boolean selected, boolean recycled, int where, int width) {
        boolean isSelected = selected && this.shouldShowSelector();
        boolean updateChildSelected = isSelected ^ child.isSelected();
        int mode = this.mTouchMode;
        boolean isPressed = mode > 0 && mode < 3 && this.mMotionPosition == position;
        boolean updateChildPressed = isPressed ^ child.isPressed();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();
        LayoutParams p = (LayoutParams)child.getLayoutParams();
        if(p == null) {
            p = (LayoutParams)this.generateDefaultLayoutParams();
        }

        p.viewType = this.mAdapter.getItemViewType(position);
        if(recycled && !p.forceAdd) {
            this.attachViewToParent(child, where, p);
        } else {
            p.forceAdd = false;
            this.addViewInLayout(child, where, p, true);
        }

        if(updateChildSelected) {
            child.setSelected(isSelected);
        }

        if(updateChildPressed) {
            child.setPressed(isPressed);
        }

        if(this.mChoiceMode != 0 && this.mCheckStates != null) {
            if(child instanceof Checkable) {
                ((Checkable)child).setChecked(this.mCheckStates.get(position));
            } else if(this.getContext().getApplicationInfo().targetSdkVersion >= 11) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }

        int w;
        int h;
        if(needToMeasure) {
            w = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, 0), 0, p.height);
            h = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(width, 1073741824), 0, p.width);
            child.measure(h, w);
        } else {
            this.cleanupLayoutState(child);
        }

        w = child.getMeasuredWidth();
        h = child.getMeasuredHeight();
        int childTop = flow?y:y - h;
        if(!flow && child instanceof GridView.GridViewHeaderViewExpandDistance) {
            childTop += ((GridView.GridViewHeaderViewExpandDistance)child).getDownExpandDistance();
        }

        boolean absoluteGravity = true;
        int childLeft;
        switch(1) {
            case 1:
                childLeft = childrenLeft + (width - w) / 2;
                break;
            case 2:
            case 4:
            default:
                childLeft = childrenLeft;
                break;
            case 3:
                childLeft = childrenLeft;
                break;
            case 5:
                childLeft = childrenLeft + width - w;
        }

        if(needToMeasure) {
            int childRight = childLeft + w;
            int childBottom = childTop + h;
            child.layout(childLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }

        if(this.mCachingStarted) {
            child.setDrawingCacheEnabled(true);
        }

        if(recycled && ((LayoutParams)child.getLayoutParams()).scrappedFromPosition != position) {
            child.jumpDrawablesToCurrentState();
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if(this.mAdapter == null) {
            return false;
        } else {
            if(this.mDataChanged) {
                this.layoutChildren();
            }

            boolean handled = false;
            int action = event.getAction();
            byte navigation = 1;
            if(action != 1) {
                switch(keyCode) {
                    case 19:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.arrowScroll(33);
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(33);
                        }

                        navigation = 2;
                        break;
                    case 20:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.arrowScroll(130);
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(130);
                        }

                        navigation = 4;
                        break;
                    case 21:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.arrowScroll(17);
                        }
                        break;
                    case 22:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.arrowScroll(66);
                        }

                        navigation = 3;
                        break;
                    case 23:
                    case 66:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if(!handled && event.getRepeatCount() == 0 && this.getChildCount() > 0) {
                                this.keyPressed();
                                handled = true;
                            }
                        }
                    case 61:
                    case 62:
                    default:
                        break;
                    case 92:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.pageScroll(33);
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(33);
                        }

                        navigation = 2;
                        break;
                    case 93:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.pageScroll(130);
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(130);
                        }

                        navigation = 4;
                        break;
                    case 122:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(33);
                        }
                        break;
                    case 123:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(130);
                        }
                }
            }

            if(handled) {
                this.playSoundEffect(navigation);
                return true;
            } else {
                switch(action) {
                    case 0:
                        return super.onKeyDown(keyCode, event);
                    case 1:
                        return super.onKeyUp(keyCode, event);
                    case 2:
                        return super.onKeyMultiple(keyCode, count, event);
                    default:
                        return false;
                }
            }
        }
    }

    boolean arrowScroll(int direction) {
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        boolean moved = false;
        int startOfRowPos;
        int endOfRowPos;
        if(!this.mStackFromBottom) {
            if(!this.isHeader(selectedPosition) && !this.isFooter(selectedPosition)) {
                startOfRowPos = this.getRowStart(selectedPosition);
                endOfRowPos = Math.min(startOfRowPos + numColumns - 1, this.mItemCount - 1 - this.getFooterViewsCount());
            } else {
                startOfRowPos = selectedPosition;
                endOfRowPos = selectedPosition;
            }
        } else {
            int invertedSelection = this.mItemCount - 1 - selectedPosition;
            endOfRowPos = this.mItemCount - 1 - invertedSelection / numColumns * numColumns;
            startOfRowPos = Math.max(0, endOfRowPos - numColumns + 1);
        }

        switch(direction) {
            case 17:
                if(selectedPosition > startOfRowPos) {
                    this.mLayoutMode = 6;
                    this.setSelectionInt(Math.max(0, selectedPosition - 1));
                    moved = true;
                }
                break;
            case 33:
                if(startOfRowPos > 0 && selectedPosition > 0) {
                    this.mLayoutMode = 6;
                    if(!this.isHeader(selectedPosition - 1) && !this.isFooter(selectedPosition - 1)) {
                        if(this.isFooter(selectedPosition) && !this.isFooter(selectedPosition - 1)) {
                            this.setSelectionInt(Math.max(selectedPosition - 1, this.getHeaderViewsCount()));
                        } else if(!this.isHeader(selectedPosition) && this.isHeader(selectedPosition - this.mNumColumns)) {
                            this.setSelectionInt(Math.max(this.getHeaderViewsCount() - 1, 0));
                        } else {
                            this.setSelectionInt(Math.max(0, selectedPosition - this.mNumColumns));
                        }
                    } else {
                        this.setSelectionInt(Math.max(0, selectedPosition - 1));
                    }

                    moved = true;
                }
                break;
            case 66:
                if(selectedPosition < endOfRowPos) {
                    this.mLayoutMode = 6;
                    this.setSelectionInt(Math.min(selectedPosition + 1, this.mItemCount - 1));
                    moved = true;
                }
                break;
            case 130:
                if(endOfRowPos < this.mItemCount - 1 && selectedPosition < this.mItemCount) {
                    this.mLayoutMode = 6;
                    if(!this.isHeader(selectedPosition + 1) && !this.isFooter(selectedPosition + 1)) {
                        if(this.isHeader(selectedPosition) && !this.isHeader(selectedPosition + 1)) {
                            this.setSelectionInt(Math.min(selectedPosition + 1, this.mItemCount - 1 - this.getFooterViewsCount()));
                        } else if(!this.isFooter(selectedPosition) && this.isFooter(selectedPosition + this.mNumColumns)) {
                            this.setSelectionInt(Math.min(this.mItemCount - this.getFooterViewsCount(), this.mItemCount - 1));
                        } else {
                            this.setSelectionInt(Math.min(selectedPosition + this.mNumColumns, this.mItemCount - 1 - this.getFooterViewsCount()));
                        }
                    } else {
                        this.setSelectionInt(Math.min(selectedPosition + 1, this.mItemCount - 1));
                    }

                    moved = true;
                }
        }

        if(moved) {
            this.awakenScrollBars();
        }

        return moved;
    }

    boolean isHeader(int position) {
        return position < this.getHeaderViewsCount();
    }

    boolean isFooter(int position) {
        return position > this.mItemCount - this.getFooterViewsCount() - 1;
    }

    public void setSelection(int position) {
        if(!this.isInTouchMode()) {
            this.setNextSelectedPositionInt(position);
        } else {
            this.mResurrectToPosition = position;
        }

        this.mLayoutMode = 2;
        this.mNeedLayout = true;
        this.layoutChildren();
    }

    void setSelectionInt(int position) {
        int previousSelectedPosition = this.mNextSelectedPosition;
        this.setNextSelectedPositionInt(position);
        this.mNeedLayout = true;
        this.layoutChildren();
        int next = this.mStackFromBottom?this.mItemCount - 1 - this.mNextSelectedPosition:this.mNextSelectedPosition;
        int previous = this.mStackFromBottom?this.mItemCount - 1 - previousSelectedPosition:previousSelectedPosition;
        int nextRow = next / this.mNumColumns;
        int previousRow = previous / this.mNumColumns;
        if(nextRow != previousRow) {
            this.awakenScrollBars();
        }

    }

    boolean fullScroll(int direction) {
        boolean moved = false;
        if(direction == 33) {
            this.mLayoutMode = 2;
            this.setSelectionInt(0);
            moved = true;
        } else if(direction == 130) {
            this.mLayoutMode = 2;
            this.setSelectionInt(this.mItemCount - 1);
            moved = true;
        }

        if(moved) {
            this.awakenScrollBars();
        }

        return moved;
    }

    boolean pageScroll(int direction) {
        int nextPage = -1;
        if(direction == 33) {
            nextPage = Math.max(0, this.mSelectedPosition - this.getChildCount());
        } else if(direction == 130) {
            nextPage = Math.min(this.mItemCount - 1, this.mSelectedPosition + this.getChildCount());
        }

        if(nextPage >= 0) {
            this.setSelectionInt(nextPage);
            this.awakenScrollBars();
            return true;
        } else {
            return false;
        }
    }

    boolean sequenceScroll(int direction) {
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int count = this.mItemCount;
        int startOfRow;
        int endOfRow;
        if(!this.mStackFromBottom) {
            startOfRow = selectedPosition / numColumns * numColumns;
            endOfRow = Math.min(startOfRow + numColumns - 1, count - 1);
        } else {
            int moved = count - 1 - selectedPosition;
            endOfRow = count - 1 - moved / numColumns * numColumns;
            startOfRow = Math.max(0, endOfRow - numColumns + 1);
        }

        boolean moved1 = false;
        boolean showScroll = false;
        switch(direction) {
            case 1:
                if(selectedPosition > 0) {
                    this.mLayoutMode = 6;
                    this.setSelectionInt(selectedPosition - 1);
                    moved1 = true;
                    showScroll = selectedPosition == startOfRow;
                }
                break;
            case 2:
                if(selectedPosition < count - 1) {
                    this.mLayoutMode = 6;
                    this.setSelectionInt(selectedPosition + 1);
                    moved1 = true;
                    showScroll = selectedPosition == endOfRow;
                }
        }

        if(showScroll) {
            this.awakenScrollBars();
        }

        return moved1;
    }

    public void setStretchMode(int stretchMode) {
        if(stretchMode != this.mStretchMode) {
            this.mStretchMode = stretchMode;
            this.requestLayoutIfNecessary();
        }

    }

    public interface GridViewHeaderViewExpandDistance {
        int getUpExpandDistance();

        int getDownExpandDistance();
    }
}
