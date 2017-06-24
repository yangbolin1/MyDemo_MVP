//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.MeasureSpec;
import android.widget.Checkable;
import android.widget.ListAdapter;
import com.yunos.tv.app.widget.AbsListView;
import com.yunos.tv.app.widget.AbsBaseListView.LayoutParams;
import com.yunos.tv.app.widget.AbsBaseListView.RecycleBin;

public class ListView extends AbsListView {
    private static final String TAG = "ListView";
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
    int mDividerHeight;
    Drawable mDivider;
    private boolean mDividerIsOpaque;
    private final ListView.ArrowScrollFocusResult mArrowScrollFocusResult = new ListView.ArrowScrollFocusResult();

    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListView(Context context) {
        super(context);
    }

    public View getFirstVisibleChild() {
        return this.getChildAt(this.getFirsVisibletChildIndex());
    }

    public View getLastVisibleChild() {
        return this.getChildAt(this.getLastVisibleChildIndex());
    }

    public int getFirsVisibletChildIndex() {
        return this.getUpPreLoadedCount();
    }

    public int getLastVisibleChildIndex() {
        return this.getChildCount() - 1 - this.getDownPreLoadedCount();
    }

    public int getVisibleChildCount() {
        return this.getChildCount() - this.getUpPreLoadedCount() - this.getDownPreLoadedCount();
    }

    public int getFirstPosition() {
        return this.getFirstVisiblePosition() - this.getUpPreLoadedCount();
    }

    public int getLastPosition() {
        return this.getFirstPosition() + this.getChildCount() - 1;
    }

    public int getLastVisiblePosition() {
        return this.mFirstPosition + this.getVisibleChildCount() - 1;
    }

    void fillGap(boolean isDown) {
        int visibleChildCount = this.getVisibleChildCount();
        int paddingBottom;
        int startOffset;
        if(isDown) {
            paddingBottom = 0;
            if((this.getGroupFlags() & 34) == 34) {
                paddingBottom = this.getListPaddingTop();
            }

            startOffset = visibleChildCount > 0?this.getChildAt(this.getLastVisibleChildIndex()).getBottom() + this.mDividerHeight + this.mSpacing:paddingBottom;
            this.fillDown(this.getFirstVisiblePosition() + visibleChildCount, startOffset);
            this.correctTooHigh(visibleChildCount);
        } else {
            paddingBottom = 0;
            if((this.getGroupFlags() & 34) == 34) {
                paddingBottom = this.getListPaddingBottom();
            }

            startOffset = visibleChildCount > 0?this.getChildAt(this.getFirsVisibletChildIndex()).getTop() - this.mDividerHeight - this.mSpacing:this.getHeight() - paddingBottom;
            this.fillUp(this.getFirstVisiblePosition() - 1, startOffset);
            this.correctTooLow(visibleChildCount);
        }

    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ListAdapter adapter = this.mAdapter;
        int closetChildIndex = -1;
        int closestChildTop = 0;
        if(adapter != null && gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(this.getScrollX(), this.getScrollY());
            if(adapter.getCount() < this.getVisibleChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                this.layoutChildren();
            }

            Rect otherRect = this.mTempRect;
            int minDistance = 2147483647;
            int visibleChildCount = this.getVisibleChildCount();
            int firstPosition = this.getFirstVisiblePosition();

            for(int i = 0; i < visibleChildCount; ++i) {
                if(adapter.isEnabled(firstPosition + i)) {
                    View other = this.getChildAt(i);
                    other.getDrawingRect(otherRect);
                    this.offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = getDistance(previouslyFocusedRect, otherRect, direction);
                    if(distance < minDistance) {
                        minDistance = distance;
                        closetChildIndex = i;
                        closestChildTop = other.getTop();
                    }
                }
            }
        }

        if(closetChildIndex >= 0) {
            closestChildTop = closestChildTop > this.mListPadding.top?closestChildTop:this.mListPadding.top;
            this.setSelectionFromTop(closetChildIndex + this.getFirstVisiblePosition(), closestChildTop);
        }

    }

    public void setDivider(Drawable divider) {
        if(divider != null) {
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerHeight = 0;
        }

        this.mDivider = divider;
        this.mDividerIsOpaque = divider == null || divider.getOpacity() == -1;
        this.requestLayout();
        this.invalidate();
    }

    protected void layoutChildren() {
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if(!blockLayoutRequests) {
            this.mBlockLayoutRequests = true;

            try {
                this.invalidate();
                if(this.mAdapter != null) {
                    int childrenTop = this.mListPadding.top;
                    int childrenBottom = this.getBottom() - this.getTop() - this.mListPadding.bottom;
                    int visibleChildCount = this.getVisibleChildCount();
                    int childCount = this.getChildCount();
                    boolean index = false;
                    int delta = 0;
                    View oldSel = null;
                    View oldFirst = null;
                    View newSel = null;
                    View focusLayoutRestoreView = null;
                    int var23;
                    switch(this.mLayoutMode) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                            break;
                        case 2:
                            var23 = this.mNextSelectedPosition - this.mFirstPosition;
                            if(var23 >= 0 && var23 < visibleChildCount) {
                                newSel = this.getChildAt(var23 + this.getUpPreLoadedCount());
                            }
                            break;
                        case 6:
                        default:
                            var23 = this.mSelectedPosition - this.mFirstPosition;
                            if(var23 >= 0 && var23 < visibleChildCount) {
                                oldSel = this.getChildAt(var23 + this.getUpPreLoadedCount());
                            }

                            oldFirst = this.getFirstVisibleChild();
                            if(this.mNextSelectedPosition >= 0) {
                                delta = this.mNextSelectedPosition - this.mSelectedPosition;
                            }

                            newSel = this.getChildAt(var23 + delta + this.getUpPreLoadedCount());
                    }

                    boolean dataChanged = this.mDataChanged;
                    if(dataChanged) {
                        this.handleDataChanged();
                    }

                    if(this.mItemCount == 0) {
                        this.resetList();
                        return;
                    }

                    if(this.mItemCount != this.mAdapter.getCount()) {
                        throw new IllegalStateException("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. [in ListView(" + this.getId() + ", " + this.getClass() + ") with Adapter(" + this.mAdapter.getClass() + ")]");
                    }

                    this.setSelectedPositionInt(this.mNextSelectedPosition);
                    int firstPosition = this.getFirstVisiblePosition();
                    RecycleBin recycleBin = this.mRecycler;
                    View focusLayoutRestoreDirectChild = null;
                    int child;
                    if(dataChanged) {
                        int focusedChild = this.getFirsVisibletChildIndex();

                        for(child = focusedChild - 1; child >= 0; --child) {
                            recycleBin.addScrapView(this.getChildAt(child), firstPosition + (child - this.getUpPreLoadedCount()));
                        }

                        for(child = focusedChild; child < childCount; ++child) {
                            recycleBin.addScrapView(this.getChildAt(child), firstPosition + (child - this.getUpPreLoadedCount()));
                        }
                    } else {
                        recycleBin.fillActiveViews(childCount, firstPosition);
                    }

                    View var24 = this.getFocusedChild();
                    if(var24 != null) {
                        if(!dataChanged || this.isDirectChildHeaderOrFooter(var24)) {
                            focusLayoutRestoreDirectChild = var24;
                            focusLayoutRestoreView = this.findFocus();
                            if(focusLayoutRestoreView != null) {
                                focusLayoutRestoreView.onStartTemporaryDetach();
                            }
                        }

                        this.requestFocus();
                    }

                    this.detachAllViewsFromParent();
                    recycleBin.removeSkippedScrap();
                    this.mUpPreLoadedCount = 0;
                    this.mDownPreLoadedCount = 0;
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
                                sel = this.fillFromMiddle(childrenTop, childrenBottom);
                            }
                            break;
                        case 3:
                            sel = this.fillUp(this.mItemCount - 1, childrenBottom);
                            this.adjustViewsUpOrDown();
                            break;
                        case 4:
                            sel = this.fillSpecific(this.reconcileSelectedPosition(), this.mSpecificTop);
                            break;
                        case 5:
                            sel = this.fillSpecific(this.mSyncPosition, this.mSpecificTop);
                            break;
                        case 6:
                            sel = this.moveSelection(oldSel, newSel, delta, childrenTop, childrenBottom);
                            break;
                        default:
                            if(childCount == 0) {
                                if(!this.mStackFromBottom) {
                                    child = this.lookForSelectablePosition(0, true);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillFromTop(childrenTop);
                                } else {
                                    child = this.lookForSelectablePosition(this.mItemCount - 1, false);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillUp(this.mItemCount - 1, childrenBottom);
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
                        if(this.mItemsCanFocus && this.hasFocus() && !sel.hasFocus()) {
                            boolean var25 = sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus() || sel.requestFocus();
                            if(!var25) {
                                View focused = this.getFocusedChild();
                                if(focused != null) {
                                    focused.clearFocus();
                                }

                                this.positionSelector(-1, sel);
                            } else {
                                sel.setSelected(false);
                                this.mSelectorRect.setEmpty();
                            }
                        } else {
                            this.positionSelector(-1, sel);
                        }
                    } else {
                        if(this.mTouchMode > 0 && this.mTouchMode < 3) {
                            View var26 = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
                            if(var26 != null) {
                                this.positionSelector(this.mMotionPosition, var26);
                            }
                        } else {
                            this.mSelectorRect.setEmpty();
                        }

                        if(this.hasFocus() && focusLayoutRestoreView != null) {
                            focusLayoutRestoreView.requestFocus();
                        }
                    }

                    if(focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
                        focusLayoutRestoreView.onFinishTemporaryDetach();
                    }

                    this.mLayoutMode = 0;
                    this.mDataChanged = false;
                    this.mNeedSync = false;
                    this.setNextSelectedPositionInt(this.mSelectedPosition);
                    if(this.mItemCount > 0) {
                        this.checkSelectionChanged();
                    }

                    return;
                }

                this.resetList();
            } finally {
                if(!blockLayoutRequests) {
                    this.mBlockLayoutRequests = false;
                }

            }

        }
    }

    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = this.getBottomSelectionPixel(childrenBottom, fadingEdgeLength, selectedPosition);
        View sel = this.makeAndAddView(selectedPosition, selectedTop, true, this.mListPadding.left, true);
        int spaceAbove;
        int spaceBelow;
        int offset;
        if(sel.getBottom() > bottomSelectionPixel) {
            spaceAbove = sel.getTop() - topSelectionPixel;
            spaceBelow = sel.getBottom() - bottomSelectionPixel;
            offset = Math.min(spaceAbove, spaceBelow);
            sel.offsetTopAndBottom(-offset);
        } else if(sel.getTop() < topSelectionPixel) {
            spaceAbove = topSelectionPixel - sel.getTop();
            spaceBelow = bottomSelectionPixel - sel.getBottom();
            offset = Math.min(spaceAbove, spaceBelow);
            sel.offsetTopAndBottom(offset);
        }

        this.fillAboveAndBelow(sel, selectedPosition);
        if(!this.mStackFromBottom) {
            this.correctTooHigh(this.getVisibleChildCount());
        } else {
            this.correctTooLow(this.getVisibleChildCount());
        }

        return sel;
    }

    protected View fillFromMiddle(int childrenTop, int childrenBottom) {
        int height = childrenBottom - childrenTop;
        int position = this.reconcileSelectedPosition();
        View sel = this.makeAndAddView(position, childrenTop, true, this.mListPadding.left, true);
        this.mFirstPosition = position;
        int selHeight = sel.getMeasuredHeight();
        if(selHeight <= height) {
            sel.offsetTopAndBottom((height - selHeight) / 2);
        }

        this.fillAboveAndBelow(sel, position);
        if(!this.mStackFromBottom) {
            this.correctTooHigh(this.getVisibleChildCount());
        } else {
            this.correctTooLow(this.getVisibleChildCount());
        }

        return sel;
    }

    protected View fillSpecific(int position, int top) {
        boolean tempIsSelected = position == this.mSelectedPosition;
        View temp = this.makeAndAddView(position, top, true, this.mListPadding.left, tempIsSelected);
        this.mFirstPosition = position;
        int dividerHeight = this.mDividerHeight;
        View above;
        View below;
        int visibleChildCount;
        if(!this.mStackFromBottom) {
            above = this.fillUp(position - 1, temp.getTop() - dividerHeight - this.mSpacing);
            this.adjustViewsUpOrDown();
            below = this.fillDown(position + 1, temp.getBottom() + dividerHeight + this.mSpacing);
            visibleChildCount = this.getVisibleChildCount();
            if(visibleChildCount > 0) {
                this.correctTooHigh(visibleChildCount);
            }
        } else {
            below = this.fillDown(position + 1, temp.getBottom() + dividerHeight + this.mSpacing);
            this.adjustViewsUpOrDown();
            above = this.fillUp(position - 1, temp.getTop() - dividerHeight - this.mSpacing);
            visibleChildCount = this.getVisibleChildCount();
            if(visibleChildCount > 0) {
                this.correctTooLow(visibleChildCount);
            }
        }

        return tempIsSelected?temp:(above != null?above:below);
    }

    protected View fillUp(int pos, int nextBottom) {
        View selectedView = null;
        int end = 0;
        if((this.getGroupFlags() & 34) == 34) {
            end = this.mListPadding.top;
        }

        while(nextBottom > end && pos >= 0) {
            if(this.mUpPreLoadedCount > 0) {
                nextBottom = this.getChildAt(pos - this.getFirstPosition()).getTop() - this.mDividerHeight - this.mSpacing;
                --this.mUpPreLoadedCount;
                --pos;
            } else {
                boolean selected = pos == this.mSelectedPosition;
                View child = this.makeAndAddView(pos, nextBottom, false, this.mListPadding.left, selected);
                nextBottom = child.getTop() - this.mDividerHeight - this.mSpacing;
                if(selected) {
                    selectedView = child;
                }

                --pos;
            }
        }

        this.mFirstPosition = pos + 1;
        this.fillUpPreLoad();
        return selectedView;
    }

    protected void fillUpPreLoad() {
        if(this.mPreLoadCount > 0 && this.mUpPreLoadedCount < this.mPreLoadCount) {
            int pos = this.getFirstPosition() - 1;
            int nextBottom = this.getFirstChild().getTop() - this.mDividerHeight - this.mSpacing;

            for(int preLoadPos = pos - (this.mPreLoadCount - this.mUpPreLoadedCount); pos > preLoadPos && pos >= 0; ++this.mUpPreLoadedCount) {
                View child = this.makeAndAddView(pos, nextBottom, false, this.mListPadding.left, false);
                nextBottom = child.getTop() - this.mDividerHeight - this.mSpacing;
                --pos;
            }

        }
    }

    protected void adjustViewsUpOrDown() {
        int visibleChildCount = this.getVisibleChildCount();
        if(visibleChildCount > 0) {
            int delta;
            View child;
            if(!this.mStackFromBottom) {
                child = this.getFirstVisibleChild();
                delta = child.getTop() - this.mListPadding.top;
                if(this.mFirstPosition != 0) {
                    delta -= this.mDividerHeight - this.mSpacing;
                }

                if(delta < 0) {
                    delta = 0;
                }
            } else {
                child = this.getLastVisibleChild();
                delta = child.getBottom() - (this.getHeight() - this.mListPadding.bottom);
                if(this.mFirstPosition + visibleChildCount < this.mItemCount) {
                    delta += this.mDividerHeight + this.mSpacing;
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

    protected View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if(this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }

        return this.fillDown(this.mFirstPosition, nextTop);
    }

    private View moveSelection(View oldSel, View newSel, int delta, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = this.getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = this.getBottomSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        View sel;
        int oldTop;
        int newBottom;
        int halfVerticalSpace;
        int offset;
        if(delta > 0) {
            oldSel = this.makeAndAddView(selectedPosition - 1, oldSel.getTop(), true, this.mListPadding.left, false);
            oldTop = this.mDividerHeight;
            sel = this.makeAndAddView(selectedPosition, oldSel.getBottom() + oldTop + this.mSpacing, true, this.mListPadding.left, true);
            if(sel.getBottom() > bottomSelectionPixel) {
                newBottom = sel.getTop() - topSelectionPixel;
                halfVerticalSpace = sel.getBottom() - bottomSelectionPixel;
                offset = (childrenBottom - childrenTop) / 2;
                int offset1 = Math.min(newBottom, halfVerticalSpace);
                offset1 = Math.min(offset1, offset);
                oldSel.offsetTopAndBottom(-offset1);
                sel.offsetTopAndBottom(-offset1);
            }

            if(!this.mStackFromBottom) {
                this.fillUp(this.mSelectedPosition - 2, sel.getTop() - oldTop - this.mSpacing);
                this.adjustViewsUpOrDown();
                this.fillDown(this.mSelectedPosition + 1, sel.getBottom() + oldTop + this.mSpacing);
            } else {
                this.fillDown(this.mSelectedPosition + 1, sel.getBottom() + oldTop + this.mSpacing);
                this.adjustViewsUpOrDown();
                this.fillUp(this.mSelectedPosition - 2, sel.getTop() - oldTop - this.mSpacing);
            }
        } else if(delta < 0) {
            if(newSel != null) {
                sel = this.makeAndAddView(selectedPosition, newSel.getTop(), true, this.mListPadding.left, true);
            } else {
                sel = this.makeAndAddView(selectedPosition, oldSel.getTop(), false, this.mListPadding.left, true);
            }

            if(sel.getTop() < topSelectionPixel) {
                oldTop = topSelectionPixel - sel.getTop();
                newBottom = bottomSelectionPixel - sel.getBottom();
                halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                offset = Math.min(oldTop, newBottom);
                offset = Math.min(offset, halfVerticalSpace);
                sel.offsetTopAndBottom(offset);
            }

            this.fillAboveAndBelow(sel, selectedPosition);
        } else {
            oldTop = oldSel.getTop();
            sel = this.makeAndAddView(selectedPosition, oldTop, true, this.mListPadding.left, true);
            if(oldTop < childrenTop) {
                newBottom = sel.getBottom();
                if(newBottom < childrenTop + 20) {
                    sel.offsetTopAndBottom(childrenTop - sel.getTop());
                }
            }

            this.fillAboveAndBelow(sel, selectedPosition);
        }

        return sel;
    }

    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int selectedPosition) {
        int topSelectionPixel = childrenTop;
        if(selectedPosition > 0) {
            topSelectionPixel = childrenTop + fadingEdgeLength;
        }

        return topSelectionPixel;
    }

    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int selectedPosition) {
        int bottomSelectionPixel = childrenBottom;
        if(selectedPosition != this.mItemCount - 1) {
            bottomSelectionPixel = childrenBottom - fadingEdgeLength;
        }

        return bottomSelectionPixel;
    }

    private View fillDown(int pos, int nextTop) {
        View selectedView = null;
        int end = this.getBottom() - this.getTop();
        if((this.getGroupFlags() & 34) == 34) {
            end -= this.mListPadding.bottom;
        }

        while(nextTop < end && pos < this.mItemCount) {
            if(this.mDownPreLoadedCount > 0) {
                nextTop = this.getChildAt(pos - this.getFirstPosition()).getBottom() + this.mDividerHeight + this.mSpacing;
                ++pos;
                --this.mDownPreLoadedCount;
            } else {
                boolean selected = pos == this.mSelectedPosition;
                View child = this.makeAndAddView(pos, nextTop, true, this.mListPadding.left, selected);
                nextTop = child.getBottom() + this.mDividerHeight + this.mSpacing;
                if(selected) {
                    selectedView = child;
                }

                ++pos;
            }
        }

        this.fillDownPreLoad();
        return selectedView;
    }

    private void fillDownPreLoad() {
        if(this.mPreLoadCount > 0 && this.mDownPreLoadedCount < this.mPreLoadCount) {
            int nextTop = this.getLastChild().getBottom() + this.mDividerHeight + this.mSpacing;
            int pos = this.getLastPosition() + 1;

            for(int preLoadPos = pos + this.mPreLoadCount - this.mDownPreLoadedCount; pos < preLoadPos && pos < this.mItemCount; ++this.mDownPreLoadedCount) {
                boolean selected = pos == this.mSelectedPosition;
                View child = this.makeAndAddView(pos, nextTop, true, this.mListPadding.left, selected);
                nextTop = child.getBottom() + this.mDividerHeight + this.mSpacing;
                ++pos;
            }

        }
    }

    private void fillAboveAndBelow(View sel, int position) {
        int dividerHeight = this.mDividerHeight;
        if(!this.mStackFromBottom) {
            this.fillUp(position - 1, sel.getTop() - dividerHeight - this.mSpacing);
            this.adjustViewsUpOrDown();
            this.fillDown(position + 1, sel.getBottom() + dividerHeight + this.mSpacing);
        } else {
            this.fillDown(position + 1, sel.getBottom() + dividerHeight + this.mSpacing);
            this.adjustViewsUpOrDown();
            this.fillUp(position - 1, sel.getTop() - dividerHeight);
        }

    }

    private void correctTooHigh(int bisibleChildCount) {
        int lastPosition = this.getLastVisiblePosition();
        if(lastPosition == this.mItemCount - 1 && bisibleChildCount > 0) {
            View lastChild = this.getLastVisibleChild();
            int lastBottom = lastChild.getBottom();
            int end = this.getBottom() - this.getTop() - this.mListPadding.bottom;
            int bottomOffset = end - lastBottom;
            View firstChild = this.getFirstVisibleChild();
            int firstTop = firstChild.getTop();
            if(bottomOffset > 0 && (this.mFirstPosition > 0 || firstTop < this.mListPadding.top)) {
                if(this.mFirstPosition == 0) {
                    bottomOffset = Math.min(bottomOffset, this.mListPadding.top - firstTop);
                }

                this.offsetChildrenTopAndBottom(bottomOffset);
                if(this.mFirstPosition > 0) {
                    this.fillUp(this.mFirstPosition - 1, firstChild.getTop() - this.mDividerHeight - this.mSpacing);
                    this.adjustViewsUpOrDown();
                }
            }
        }

    }

    private void correctTooLow(int bisibleChildCount) {
        if(this.mFirstPosition == 0 && bisibleChildCount > 0) {
            View firstChild = this.getFirstVisibleChild();
            int firstTop = firstChild.getTop();
            int start = this.mListPadding.top;
            int end = this.getBottom() - this.getTop() - this.mListPadding.bottom;
            int topOffset = firstTop - start;
            View lastChild = this.getChildAt(this.getLastVisibleChildIndex());
            int lastBottom = lastChild.getBottom();
            int lastPosition = this.getLastVisiblePosition();
            if(topOffset > 0) {
                if(lastPosition >= this.mItemCount - 1 && lastBottom <= end) {
                    if(lastPosition == this.mItemCount - 1) {
                        this.adjustViewsUpOrDown();
                    }
                } else {
                    if(lastPosition == this.mItemCount - 1) {
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }

                    this.offsetChildrenTopAndBottom(-topOffset);
                    if(lastPosition < this.mItemCount - 1) {
                        this.fillDown(lastPosition + 1, lastChild.getBottom() + this.mDividerHeight + this.mSpacing);
                        this.adjustViewsUpOrDown();
                    }
                }
            }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;
        this.mItemCount = this.mAdapter == null?0:this.mAdapter.getCount();
        if(this.mItemCount > 0 && (widthMode == 0 || heightMode == 0)) {
            View child = this.obtainView(0, this.mIsScrap);
            this.measureScrapChild(child, 0, widthMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = combineMeasuredStates(childState, child.getMeasuredState());
            if(this.recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((LayoutParams)child.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(child, -1);
            }
        }

        if(widthMode == 0) {
            widthSize = this.mListPadding.left + this.mListPadding.right + childWidth + this.getVerticalScrollbarWidth();
        } else {
            widthSize |= childState & -16777216;
        }

        if(heightMode == 0) {
            heightSize = this.mListPadding.top + this.mListPadding.bottom + childHeight + this.getVerticalFadingEdgeLength() * 2;
        }

        if(heightMode == -2147483648 && this.isMeasueHeightOfChildren()) {
            heightSize = this.measureHeightOfChildren(widthMeasureSpec, 0, -1, heightSize, -1);
        }

        this.setMeasuredDimension(widthSize, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    protected boolean isMeasueHeightOfChildren() {
        return true;
    }

    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
        View child;
        if(!this.mDataChanged) {
            child = this.mRecycler.getActiveView(position);
            if(child != null) {
                this.setupChild(child, position, y, flow, childrenLeft, selected, true);
                return child;
            }
        }

        child = this.obtainView(position, this.mIsScrap);
        this.setupChild(child, position, y, flow, childrenLeft, selected, this.mIsScrap[0]);
        return child;
    }

    private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
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
        if(recycled && !p.forceAdd || p.recycledHeaderFooter && p.viewType == -2) {
            this.attachViewToParent(child, flowDown?-1:0, p);
        } else {
            p.forceAdd = false;
            if(p.viewType == -2) {
                p.recycledHeaderFooter = true;
            }

            this.addViewInLayout(child, flowDown?-1:0, p, true);
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
        int childTop;
        if(needToMeasure) {
            w = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
            h = p.height;
            if(h > 0) {
                childTop = MeasureSpec.makeMeasureSpec(h, 1073741824);
            } else {
                childTop = MeasureSpec.makeMeasureSpec(0, 0);
            }

            child.measure(w, childTop);
        } else {
            this.cleanupLayoutState(child);
        }

        w = child.getMeasuredWidth();
        h = child.getMeasuredHeight();
        childTop = flowDown?y:y - h;
        if(needToMeasure) {
            int childRight = childrenLeft + w;
            int childBottom = childTop + h;
            child.layout(childrenLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childrenLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }

        if(this.mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }

        if(recycled && ((LayoutParams)child.getLayoutParams()).scrappedFromPosition != position) {
            child.jumpDrawablesToCurrentState();
        }

    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        LayoutParams p = (LayoutParams)child.getLayoutParams();
        if(p == null) {
            p = (LayoutParams)this.generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }

        p.viewType = this.mAdapter.getItemViewType(position);
        p.forceAdd = true;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if(lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    protected boolean recycleOnMeasure() {
        return true;
    }

    final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition, int maxHeight, int disallowPartialChildPosition) {
        ListAdapter adapter = this.mAdapter;
        if(adapter == null) {
            return this.mListPadding.top + this.mListPadding.bottom;
        } else {
            int returnedHeight = this.mListPadding.top + this.mListPadding.bottom;
            boolean dividerHeight = false;
            int prevHeightWithoutPartialChild = 0;
            endPosition = endPosition == -1?adapter.getCount() - 1:endPosition;
            RecycleBin recycleBin = this.mRecycler;
            boolean recyle = this.recycleOnMeasure();
            boolean[] isScrap = this.mIsScrap;

            for(int i = startPosition; i <= endPosition; ++i) {
                View child = this.obtainView(i, isScrap);
                this.measureScrapChild(child, i, widthMeasureSpec);
                if(i > 0) {
                    returnedHeight += 0;
                }

                if(recyle && recycleBin.shouldRecycleViewType(((LayoutParams)child.getLayoutParams()).viewType)) {
                    recycleBin.addScrapView(child, -1);
                }

                returnedHeight += child.getMeasuredHeight();
                if(returnedHeight >= maxHeight) {
                    return disallowPartialChildPosition >= 0 && i > disallowPartialChildPosition && prevHeightWithoutPartialChild > 0 && returnedHeight != maxHeight?prevHeightWithoutPartialChild:maxHeight;
                }

                if(disallowPartialChildPosition >= 0 && i >= disallowPartialChildPosition) {
                    prevHeightWithoutPartialChild = returnedHeight;
                }
            }

            return returnedHeight;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if(!handled) {
            View focused = this.getFocusedChild();
            if(focused != null && event.getAction() == 0) {
                handled = this.onKeyDown(event.getKeyCode(), event);
            }
        }

        return handled;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if(this.mAdapter != null && this.mIsAttached) {
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
                            handled = this.resurrectSelectionIfNeeded();
                            if(!handled) {
                                while(count-- > 0 && this.arrowScroll(33)) {
                                    handled = true;
                                }
                            }
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(33);
                        }

                        navigation = 2;
                        break;
                    case 20:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if(!handled) {
                                while(count-- > 0 && this.arrowScroll(130)) {
                                    handled = true;
                                }
                            }
                        } else if(event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(130);
                        }

                        navigation = 4;
                        break;
                    case 21:
                        if(event.hasNoModifiers()) {
                            handled = this.handleHorizontalFocusWithinListItem(17);
                        }
                        break;
                    case 22:
                        if(event.hasNoModifiers()) {
                            handled = this.handleHorizontalFocusWithinListItem(66);
                        }

                        navigation = 3;
                        break;
                    case 23:
                    case 66:
                        if(event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if(!handled && event.getRepeatCount() == 0 && this.getVisibleChildCount() > 0) {
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
        } else {
            return false;
        }
    }

    boolean arrowScroll(int direction) {
        boolean var4;
        try {
            this.mInLayout = true;
            boolean handled = this.arrowScrollImpl(direction);
            if(handled) {
                this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }

            var4 = handled;
        } finally {
            this.mInLayout = false;
        }

        return var4;
    }

    private boolean arrowScrollImpl(int direction) {
        if(this.getVisibleChildCount() <= 0) {
            return false;
        } else {
            View selectedView = this.getSelectedView();
            int selectedPos = this.mSelectedPosition;
            int nextSelectedPosition = this.lookForSelectablePositionOnScreen(direction);
            int amountToScroll = this.amountToScroll(direction, nextSelectedPosition);
            ListView.ArrowScrollFocusResult focusResult = this.mItemsCanFocus?this.arrowScrollFocused(direction):null;
            if(focusResult != null) {
                nextSelectedPosition = focusResult.getSelectedPosition();
                amountToScroll = focusResult.getAmountToScroll();
            }

            boolean needToRedraw = focusResult != null;
            View focused;
            if(nextSelectedPosition != -1) {
                this.handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                selectedView = this.getSelectedView();
                selectedPos = nextSelectedPosition;
                if(this.mItemsCanFocus && focusResult == null) {
                    focused = this.getFocusedChild();
                    if(focused != null) {
                        focused.clearFocus();
                    }
                }

                needToRedraw = true;
                this.checkSelectionChanged();
            }

            if(amountToScroll > 0) {
                this.scrollListItemsBy(direction == 33?amountToScroll:-amountToScroll);
                needToRedraw = true;
            }

            if(this.mItemsCanFocus && focusResult == null && selectedView != null && selectedView.hasFocus()) {
                focused = selectedView.findFocus();
                if(!this.isViewAncestorOf(focused, this) || this.distanceToView(focused) > 0) {
                    focused.clearFocus();
                }
            }

            if(nextSelectedPosition == -1 && selectedView != null && !this.isViewAncestorOf(selectedView, this)) {
                selectedView = null;
                this.hideSelector();
                this.mResurrectToPosition = -1;
            }

            if(needToRedraw) {
                if(selectedView != null) {
                    this.positionSelector(selectedPos, selectedView);
                }

                if(!this.awakenScrollBars()) {
                    this.invalidate();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private void scrollListItemsBy(int amount) {
        this.offsetChildrenTopAndBottom(amount);
        int listBottom = this.getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        RecycleBin recycleBin = this.mRecycler;
        View lastVisibleChild;
        View var11;
        if(amount < 0) {
            int first = this.getVisibleChildCount();

            for(lastVisibleChild = this.getLastVisibleChild(); lastVisibleChild.getBottom() < listBottom; ++first) {
                int lastChild = this.getLastVisiblePosition();
                if(lastChild >= this.mItemCount - 1) {
                    break;
                }

                lastVisibleChild = this.addViewBelow(lastVisibleChild, lastChild);
            }

            if(lastVisibleChild.getBottom() < listBottom) {
                this.offsetChildrenTopAndBottom(listBottom - lastVisibleChild.getBottom());
            }

            for(var11 = this.getFirstVisibleChild(); var11.getBottom() < listTop; ++this.mFirstPosition) {
                View layoutParams = this.getFirstChild();
                LayoutParams layoutParams1 = (LayoutParams)var11.getLayoutParams();
                if(recycleBin.shouldRecycleViewType(layoutParams1.viewType)) {
                    this.detachViewFromParent(layoutParams);
                    recycleBin.addScrapView(layoutParams, this.getFirstPosition());
                } else {
                    this.removeViewInLayout(layoutParams);
                }

                var11 = this.getFirstVisibleChild();
            }
        } else {
            View var10;
            for(var10 = this.getFirstVisibleChild(); var10.getTop() > listTop && this.mFirstPosition > 0; --this.mFirstPosition) {
                var10 = this.addViewAbove(var10, this.mFirstPosition);
            }

            if(var10.getTop() > listTop) {
                this.offsetChildrenTopAndBottom(listTop - var10.getTop());
            }

            for(lastVisibleChild = this.getLastVisibleChild(); lastVisibleChild.getTop() > listBottom; lastVisibleChild = this.getLastVisibleChild()) {
                var11 = this.getLastChild();
                LayoutParams var12 = (LayoutParams)lastVisibleChild.getLayoutParams();
                if(recycleBin.shouldRecycleViewType(var12.viewType)) {
                    this.detachViewFromParent(var11);
                    recycleBin.addScrapView(var11, this.getLastPosition());
                } else {
                    this.removeViewInLayout(var11);
                }
            }
        }

    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position - 1 - this.mUpPreLoadedCount;
        View view = this.obtainView(abovePosition, this.mIsScrap);
        if(view != null) {
            int edgeOfNewChild = theView.getTop() - this.mDividerHeight - this.mSpacing;
            this.setupChild(view, abovePosition, edgeOfNewChild, false, this.mListPadding.left, false, this.mIsScrap[0]);
            return view;
        } else {
            --this.mUpPreLoadedCount;
            return this.getFirstChild();
        }
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1 + this.mDownPreLoadedCount;
        View view = this.obtainView(belowPosition, this.mIsScrap);
        if(view != null) {
            int edgeOfNewChild = this.getLastChild().getBottom() + this.mDividerHeight + this.mSpacing;
            this.setupChild(view, belowPosition, edgeOfNewChild, true, this.mListPadding.left, false, this.mIsScrap[0]);
            return view;
        } else {
            --this.mDownPreLoadedCount;
            return this.getLastChild();
        }
    }

    private int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(this.mTempRect);
        this.offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        int listBottom = this.getBottom() - this.getTop() - this.mListPadding.bottom;
        if(this.mTempRect.bottom < this.mListPadding.top) {
            distance = this.mListPadding.top - this.mTempRect.bottom;
        } else if(this.mTempRect.top > listBottom) {
            distance = this.mTempRect.top - listBottom;
        }

        return distance;
    }

    private boolean isViewAncestorOf(View child, View parent) {
        if(child == parent) {
            return true;
        } else {
            ViewParent theParent = child.getParent();
            return theParent instanceof ViewGroup && this.isViewAncestorOf((View)theParent, parent);
        }
    }

    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        if(newSelectedPosition == -1) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        } else {
            boolean topSelected = false;
            int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
            int nextSelectedIndex = newSelectedPosition - this.mFirstPosition;
            View topView;
            View bottomView;
            int topViewIndex;
            int bottomViewIndex;
            if(direction == 33) {
                topViewIndex = nextSelectedIndex;
                bottomViewIndex = selectedIndex;
                topView = this.getChildAt(nextSelectedIndex);
                bottomView = selectedView;
                topSelected = true;
            } else {
                topViewIndex = selectedIndex;
                bottomViewIndex = nextSelectedIndex;
                topView = selectedView;
                bottomView = this.getChildAt(nextSelectedIndex);
            }

            int numChildren = this.getChildCount();
            if(topView != null) {
                topView.setSelected(!newFocusAssigned && topSelected);
                this.measureAndAdjustDown(topView, topViewIndex, numChildren);
            }

            if(bottomView != null) {
                bottomView.setSelected(!newFocusAssigned && !topSelected);
                this.measureAndAdjustDown(bottomView, bottomViewIndex, numChildren);
            }

        }
    }

    private void measureAndAdjustDown(View child, int childIndex, int numChildren) {
        int oldHeight = child.getHeight();
        this.measureItem(child);
        if(child.getMeasuredHeight() != oldHeight) {
            this.relayoutMeasuredItem(child);
            int heightDelta = child.getMeasuredHeight() - oldHeight;

            for(int i = childIndex + 1; i < numChildren; ++i) {
                this.getChildAt(i).offsetTopAndBottom(heightDelta);
            }
        }

    }

    private void measureItem(View child) {
        android.view.ViewGroup.LayoutParams p = child.getLayoutParams();
        if(p == null) {
            p = new android.view.ViewGroup.LayoutParams(-1, -2);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if(lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    private void relayoutMeasuredItem(View child) {
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childLeft = this.mListPadding.left;
        int childRight = childLeft + w;
        int childTop = child.getTop();
        int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    private ListView.ArrowScrollFocusResult arrowScrollFocused(int direction) {
        View selectedView = this.getSelectedView();
        View newFocus;
        int focusScroll;
        int maxScrollAmount;
        if(selectedView != null && selectedView.hasFocus()) {
            View positionOfNewFocus1 = selectedView.findFocus();
            newFocus = FocusFinder.getInstance().findNextFocus(this, positionOfNewFocus1, direction);
        } else {
            boolean positionOfNewFocus;
            if(direction == 130) {
                positionOfNewFocus = this.mFirstPosition > 0;
                focusScroll = this.mListPadding.top + (positionOfNewFocus?this.getArrowScrollPreviewLength():0);
                maxScrollAmount = selectedView != null && selectedView.getTop() > focusScroll?selectedView.getTop():focusScroll;
                this.mTempRect.set(0, maxScrollAmount, 0, maxScrollAmount);
            } else {
                positionOfNewFocus = this.mFirstPosition + this.getChildCount() - 1 < this.mItemCount;
                focusScroll = this.getHeight() - this.mListPadding.bottom - (positionOfNewFocus?this.getArrowScrollPreviewLength():0);
                maxScrollAmount = selectedView != null && selectedView.getBottom() < focusScroll?selectedView.getBottom():focusScroll;
                this.mTempRect.set(0, maxScrollAmount, 0, maxScrollAmount);
            }

            newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, direction);
        }

        if(newFocus != null) {
            int positionOfNewFocus2 = this.positionOfNewFocus(newFocus);
            if(this.mSelectedPosition != -1 && positionOfNewFocus2 != this.mSelectedPosition) {
                focusScroll = this.lookForSelectablePositionOnScreen(direction);
                if(focusScroll != -1 && (direction == 130 && focusScroll < positionOfNewFocus2 || direction == 33 && focusScroll > positionOfNewFocus2)) {
                    return null;
                }
            }

            focusScroll = this.amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus2);
            maxScrollAmount = this.getMaxScrollAmount();
            if(focusScroll < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus2, focusScroll);
                return this.mArrowScrollFocusResult;
            }

            if(this.distanceToView(newFocus) < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus2, maxScrollAmount);
                return this.mArrowScrollFocusResult;
            }
        }

        return null;
    }

    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        int amountToScroll = 0;
        newFocus.getDrawingRect(this.mTempRect);
        this.offsetDescendantRectToMyCoords(newFocus, this.mTempRect);
        if(direction == 33) {
            if(this.mTempRect.top < this.mListPadding.top) {
                amountToScroll = this.mListPadding.top - this.mTempRect.top;
                if(positionOfNewFocus > 0) {
                    amountToScroll += this.getArrowScrollPreviewLength();
                }
            }
        } else {
            int listBottom = this.getHeight() - this.mListPadding.bottom;
            if(this.mTempRect.bottom > listBottom) {
                amountToScroll = this.mTempRect.bottom - listBottom;
                if(positionOfNewFocus < this.mItemCount - 1) {
                    amountToScroll += this.getArrowScrollPreviewLength();
                }
            }
        }

        return amountToScroll;
    }

    private int positionOfNewFocus(View newFocus) {
        int numChildren = this.getChildCount();

        for(int i = 0; i < numChildren; ++i) {
            View child = this.getChildAt(i);
            if(this.isViewAncestorOf(newFocus, child)) {
                return this.mFirstPosition + i;
            }
        }

        throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
    }

    protected int lookForSelectablePositionOnScreen(int direction) {
        int firstPosition = this.getFirstVisiblePosition();
        int last;
        int startPos;
        ListAdapter adapter;
        int pos;
        if(direction == 130) {
            last = this.mSelectedPosition != -1?this.mSelectedPosition + 1:firstPosition;
            if(last >= this.mAdapter.getCount()) {
                return -1;
            }

            if(last < firstPosition) {
                last = firstPosition;
            }

            startPos = this.getLastVisiblePosition();
            adapter = this.getAdapter();

            for(pos = last; pos <= startPos; ++pos) {
                if(adapter.isEnabled(pos) && this.getChildAt(pos - firstPosition).getVisibility() == 0) {
                    return pos;
                }
            }
        } else {
            last = this.getLastVisiblePosition();
            startPos = this.mSelectedPosition != -1?this.mSelectedPosition - 1:last;
            if(startPos < 0 || startPos >= this.mAdapter.getCount()) {
                return -1;
            }

            if(startPos > last) {
                startPos = last;
            }

            adapter = this.getAdapter();

            for(pos = startPos; pos >= firstPosition; --pos) {
                if(adapter.isEnabled(pos) && this.getChildAt(pos - firstPosition).getVisibility() == 0) {
                    return pos;
                }
            }
        }

        return -1;
    }

    int amountToScroll(int direction, int nextSelectedPosition) {
        int listBottom = this.getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        int numChildren = this.getVisibleChildCount();
        int indexToMakeVisible;
        int positionToMakeVisible;
        View viewToMakeVisible;
        int goalTop;
        int amountToScroll;
        int max;
        if(direction == 130) {
            indexToMakeVisible = numChildren - 1;
            if(nextSelectedPosition != -1) {
                indexToMakeVisible = nextSelectedPosition - this.getFirstVisiblePosition();
            }

            positionToMakeVisible = this.getFirstVisiblePosition() + indexToMakeVisible;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            goalTop = listBottom;
            if(positionToMakeVisible < this.mItemCount - 1) {
                goalTop = listBottom - this.getArrowScrollPreviewLength();
            }

            if(viewToMakeVisible.getBottom() <= goalTop) {
                return 0;
            } else if(nextSelectedPosition != -1 && goalTop - viewToMakeVisible.getTop() >= this.getMaxScrollAmount()) {
                return 0;
            } else {
                amountToScroll = viewToMakeVisible.getBottom() - goalTop;
                if(this.mFirstPosition + numChildren == this.mItemCount) {
                    max = this.getChildAt(numChildren - 1).getBottom() - listBottom;
                    amountToScroll = Math.min(amountToScroll, max);
                }

                return Math.min(amountToScroll, this.getMaxScrollAmount());
            }
        } else {
            indexToMakeVisible = 0;
            if(nextSelectedPosition != -1) {
                indexToMakeVisible = nextSelectedPosition - this.getFirstVisiblePosition();
            }

            positionToMakeVisible = this.getFirstVisiblePosition() + indexToMakeVisible;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            goalTop = listTop;
            if(positionToMakeVisible > 0) {
                goalTop = listTop + this.getArrowScrollPreviewLength();
            }

            if(viewToMakeVisible.getTop() >= goalTop) {
                return 0;
            } else if(nextSelectedPosition != -1 && viewToMakeVisible.getBottom() - goalTop >= this.getMaxScrollAmount()) {
                return 0;
            } else {
                amountToScroll = goalTop - viewToMakeVisible.getTop();
                if(this.getFirstVisiblePosition() == 0) {
                    max = listTop - this.getChildAt(0).getTop();
                    amountToScroll = Math.min(amountToScroll, max);
                }

                return Math.min(amountToScroll, this.getMaxScrollAmount());
            }
        }
    }

    protected int getArrowScrollPreviewLength() {
        return Math.max(2, this.getVerticalFadingEdgeLength());
    }

    public int getMaxScrollAmount() {
        return (int)(0.33F * (float)(this.getBottom() - this.getTop()));
    }

    boolean fullScroll(int direction) {
        boolean moved = false;
        int position;
        if(direction == 33) {
            if(this.mSelectedPosition != 0) {
                position = this.lookForSelectablePosition(0, true);
                if(position >= 0) {
                    this.mLayoutMode = 1;
                    this.setSelectionInt(position);
                }

                moved = true;
            }
        } else if(direction == 130 && this.mSelectedPosition < this.mItemCount - 1) {
            position = this.lookForSelectablePosition(this.mItemCount - 1, true);
            if(position >= 0) {
                this.mLayoutMode = 3;
                this.setSelectionInt(position);
            }

            moved = true;
        }

        if(moved && !this.awakenScrollBars()) {
            this.awakenScrollBars();
            this.invalidate();
        }

        return moved;
    }

    private boolean handleHorizontalFocusWithinListItem(int direction) {
        if(direction != 17 && direction != 66) {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
        } else {
            int numChildren = this.getVisibleChildCount();
            if(this.mItemsCanFocus && numChildren > 0 && this.mSelectedPosition != -1) {
                View selectedView = this.getSelectedView();
                if(selectedView != null && selectedView.hasFocus() && selectedView instanceof ViewGroup) {
                    View currentFocus = selectedView.findFocus();
                    View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup)selectedView, currentFocus, direction);
                    if(nextFocus != null) {
                        currentFocus.getFocusedRect(this.mTempRect);
                        this.offsetDescendantRectToMyCoords(currentFocus, this.mTempRect);
                        this.offsetRectIntoDescendantCoords(nextFocus, this.mTempRect);
                        if(nextFocus.requestFocus(direction, this.mTempRect)) {
                            return true;
                        }
                    }

                    View globalNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup)this.getRootView(), currentFocus, direction);
                    if(globalNextFocus != null) {
                        return this.isViewAncestorOf(globalNextFocus, this);
                    }
                }
            }

            return false;
        }
    }

    boolean pageScroll(int direction) {
        int nextPage = -1;
        boolean down = false;
        if(direction == 33) {
            nextPage = Math.max(0, this.mSelectedPosition - this.getVisibleChildCount() - 1);
        } else if(direction == 130) {
            nextPage = Math.min(this.mItemCount - 1, this.mSelectedPosition + this.getVisibleChildCount() - 1);
            down = true;
        }

        if(nextPage >= 0) {
            int position = this.lookForSelectablePosition(nextPage, down);
            if(position >= 0) {
                this.mLayoutMode = 4;
                this.mSpecificTop = this.getPaddingTop() + this.getVerticalFadingEdgeLength();
                if(down && position > this.mItemCount - this.getVisibleChildCount()) {
                    this.mLayoutMode = 3;
                }

                if(!down && position < this.getVisibleChildCount()) {
                    this.mLayoutMode = 1;
                }

                this.setSelectionInt(position);
                if(!this.awakenScrollBars()) {
                    this.invalidate();
                }

                return true;
            }
        }

        return false;
    }

    public void setSelection(int position) {
        this.setSelectionFromTop(position, this.mListPadding.top);
    }

    public void setSelectionFromTop(int position, int y) {
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
                this.mSpecificTop = y;
                if(this.mNeedSync) {
                    this.mSyncPosition = position;
                    this.mSyncRowId = this.mAdapter.getItemId(position);
                }

                this.layoutChildren();
            }

        }
    }

    protected int lookForSelectablePosition(int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if(adapter != null && !this.isInTouchMode()) {
            int count = adapter.getCount();
            if(this.mAreAllItemsSelectable) {
                return position >= 0 && position < count?position:-1;
            } else {
                if(lookDown) {
                    for(position = Math.max(0, position); position < count && !adapter.isEnabled(position); ++position) {
                        ;
                    }
                } else {
                    for(position = Math.min(position, count - 1); position >= 0 && !adapter.isEnabled(position); --position) {
                        ;
                    }
                }

                return position >= 0 && position < count?position:-1;
            }
        } else {
            return -1;
        }
    }

    int findMotionRow(int y) {
        int childCount = this.getVisibleChildCount();
        if(childCount > 0) {
            int i;
            View v;
            if(!this.mStackFromBottom) {
                for(i = this.mUpPreLoadedCount; i < childCount + this.mUpPreLoadedCount; ++i) {
                    v = this.getChildAt(i);
                    if(y <= v.getBottom()) {
                        return this.mFirstPosition + i;
                    }
                }
            } else {
                for(i = childCount + this.mUpPreLoadedCount - 1; i >= this.mUpPreLoadedCount; --i) {
                    v = this.getChildAt(i);
                    if(y >= v.getTop()) {
                        return this.mFirstPosition + i;
                    }
                }
            }
        }

        return -1;
    }

    protected void detachOffScreenChildren(boolean isDown) {
        int numChildren = this.getChildCount();
        int firstPosition = this.mFirstPosition;
        boolean start = false;
        int count = 0;
        int bottom;
        int firstVisibleIndex;
        int lastVisibleIndex;
        int var10000;
        if(isDown) {
            bottom = this.getPaddingTop();
            firstVisibleIndex = this.getLastVisibleChildIndex();

            for(lastVisibleIndex = this.getFirsVisibletChildIndex(); lastVisibleIndex <= firstVisibleIndex; ++lastVisibleIndex) {
                View index = this.getChildAt(lastVisibleIndex);
                if(index.getBottom() >= bottom) {
                    break;
                }

                ++count;
                var10000 = firstPosition + lastVisibleIndex;
                if(this.mUpPreLoadedCount >= this.mPreLoadCount) {
                    index = this.getFirstChild();
                    int child = this.getFirstPosition();
                    byte n = 0;
                    this.detachViewFromParent(n);
                    this.mRecycler.addScrapView(index, child);
                } else {
                    ++this.mUpPreLoadedCount;
                }
            }

            start = false;
        } else {
            bottom = this.getHeight() - this.getPaddingBottom();
            firstVisibleIndex = this.getFirsVisibletChildIndex();
            lastVisibleIndex = this.getLastVisibleChildIndex();

            for(int i = lastVisibleIndex; i >= firstVisibleIndex; --i) {
                View var15 = this.getChildAt(i);
                if(var15.getTop() <= bottom) {
                    break;
                }

                ++count;
                var10000 = firstPosition + i;
                if(this.mDownPreLoadedCount >= this.mPreLoadCount) {
                    var15 = this.getLastChild();
                    int pos = this.getLastPosition();
                    int var14 = this.getChildCount() - 1;
                    this.detachViewFromParent(var14);
                    this.mRecycler.addScrapView(var15, pos);
                } else {
                    ++this.mDownPreLoadedCount;
                }
            }
        }

        if(isDown) {
            this.mFirstPosition += count;
        }

    }

    public View getSelectedView() {
        if(this.mItemCount > 0 && this.mSelectedPosition >= 0) {
            return this.getChildAt(this.mSelectedPosition - this.getFirstPosition());
        } else {
            Log.e("ListView", "getSelectedView: return null! this:" + this.toString() + ", mItemCount:" + this.mItemCount + ", mSelectedPosition:" + this.mSelectedPosition);
            return null;
        }
    }

    private static class ArrowScrollFocusResult {
        private int mSelectedPosition;
        private int mAmountToScroll;

        private ArrowScrollFocusResult() {
        }

        void populate(int selectedPosition, int amountToScroll) {
            this.mSelectedPosition = selectedPosition;
            this.mAmountToScroll = amountToScroll;
        }

        public int getSelectedPosition() {
            return this.mSelectedPosition;
        }

        public int getAmountToScroll() {
            return this.mAmountToScroll;
        }
    }
}
