package com.yunos.tv.app.widget;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ListAdapter;

import java.util.LinkedList;
import java.util.List;

public class IntevelHListView extends AbsHListView {
    protected static final String TAG = "IntevelHListView";
    protected static final boolean DEBUG = true;
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
    int mDividerWidth;
    Drawable mDivider;
    private boolean mDividerIsOpaque;
    int mSpecificLeft;
    protected int mIntevel = 50;
    String mItemRight = "";
    String mItemLeft = "";
    protected List<PositionInfo> list = new LinkedList();
    private final IntevelHListView.ArrowScrollFocusResult mArrowScrollFocusResult = new IntevelHListView.ArrowScrollFocusResult();

    public IntevelHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IntevelHListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntevelHListView(Context context) {
        super(context);
    }

    public void setIntevel(int intevel) {
        this.mIntevel = intevel;
    }

    void fillGap(boolean isRight) {
        int count = this.getChildCount();
        int paddingRight;
        int startOffset;
        if (isRight) {
            paddingRight = 0;
            if ((this.getGroupFlags() & 34) == 34) {
                paddingRight = this.getListPaddingLeft();
            }

            startOffset = count > 0 ? this.getChildAt(count - 1).getRight() + this.mDividerWidth + this.mSpacing : paddingRight;
            this.fillRight(this.mFirstPosition + count, startOffset);
            this.checkItemLeftIntevel(this.getFirstVisiblePosition(), false);
        } else {
            paddingRight = 0;
            if ((this.getGroupFlags() & 34) == 34) {
                paddingRight = this.getListPaddingRight();
            }

            startOffset = count > 0 ? this.getChildAt(0).getLeft() - this.mDividerWidth - this.mSpacing : this.getWidth() - paddingRight;
            this.fillLeft(this.mFirstPosition - 1, startOffset);
            this.checkItemRightIntevel(this.getLastVisiblePosition(), false);
        }

        Log.d("IntevelHListView", "fillGap: list = " + this.convertListToString());
    }

    protected boolean detachOffScreenChildren(boolean isRight) {
        boolean hr = super.detachOffScreenChildren(isRight);
        if (hr) {
            if (isRight) {
                if (this.list.size() > 0 && ((IntevelHListView.PositionInfo) this.list.get(0)).position() <= this.getFirstVisiblePosition()) {
                    this.list.remove(0);
                }
            } else if (this.list.size() > 0 && ((IntevelHListView.PositionInfo) this.list.get(this.list.size() - 1)).position() > this.getLastVisiblePosition()) {
                this.list.remove(this.list.size() - 1);
            }
        }

        return hr;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ListAdapter adapter = this.mAdapter;
        int closetChildIndex = -1;
        int closestChildLeft = 0;
        if (adapter != null && gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(this.getScrollX(), this.getScrollY());
            if (adapter.getCount() < this.getChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                this.layoutChildren();
            }

            Rect otherRect = this.mTempRect;
            int minDistance = 2147483647;
            int childCount = this.getChildCount();
            int firstPosition = this.mFirstPosition;

            for (int i = 0; i < childCount; ++i) {
                if (adapter.isEnabled(firstPosition + i)) {
                    View other = this.getChildAt(i);
                    other.getDrawingRect(otherRect);
                    this.offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = getDistance(previouslyFocusedRect, otherRect, direction);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closetChildIndex = i;
                        closestChildLeft = other.getLeft();
                    }
                }
            }
        }

        if (closetChildIndex >= 0) {
            this.setSelectionFromLeft(closetChildIndex + this.mFirstPosition, closestChildLeft - this.mListPadding.left);
        }

    }

    public void setDivider(Drawable divider) {
        if (divider != null) {
            this.mDividerWidth = divider.getIntrinsicWidth();
        } else {
            this.mDividerWidth = 0;
        }

        this.mDivider = divider;
        this.mDividerIsOpaque = divider == null || divider.getOpacity() == -1;
        this.requestLayout();
        this.invalidate();
    }

    protected void layoutChildren() {
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            this.mBlockLayoutRequests = true;
            this.mItemRight = "";
            this.mItemLeft = "";
            this.list.clear();

            label408:
            {
                try {
                    this.invalidate();
                    if (this.mAdapter == null) {
                        this.resetList();
                        return;
                    }

                    int childrenLeft = this.mListPadding.left;
                    int childrenRight = this.getRight() - this.getLeft() - this.mListPadding.right;
                    int childCount = this.getChildCount();
                    boolean index = false;
                    int delta = 0;
                    View oldSel = null;
                    View oldFirst = null;
                    View newSel = null;
                    View focusLayoutRestoreView = null;
                    int var22;
                    switch (this.mLayoutMode) {
                        case 2:
                            var22 = this.mNextSelectedPosition - this.mFirstPosition;
                            if (var22 >= 0 && var22 < childCount) {
                                newSel = this.getChildAt(var22);
                            }
                            break;
                        case 3:
                        case 6:
                        default:
                            var22 = this.mSelectedPosition - this.mFirstPosition;
                            if (var22 >= 0 && var22 < childCount) {
                                oldSel = this.getChildAt(var22);
                            }

                            oldFirst = this.getChildAt(0);
                            if (this.mNextSelectedPosition >= 0) {
                                delta = this.mNextSelectedPosition - this.mSelectedPosition;
                            }

                            newSel = this.getChildAt(var22 + delta);
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                    }

                    boolean dataChanged = this.mDataChanged;
                    if (dataChanged) {
                        this.handleDataChanged();
                    }

                    if (this.mItemCount != 0) {
                        if (this.mItemCount != this.mAdapter.getCount()) {
                            throw new IllegalStateException("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. [in ListView(" + this.getId() + ", " + this.getClass() + ") with Adapter(" + this.mAdapter.getClass() + ")]");
                        }

                        this.setSelectedPositionInt(this.mNextSelectedPosition);
                        int firstPosition = this.mFirstPosition;
                        AbsBaseListView.RecycleBin recycleBin = this.mRecycler;
                        View focusLayoutRestoreDirectChild = null;
                        if (dataChanged) {
                            for (int focusedChild = 0; focusedChild < childCount; ++focusedChild) {
                                recycleBin.addScrapView(this.getChildAt(focusedChild), firstPosition + focusedChild);
                            }
                        } else {
                            recycleBin.fillActiveViews(childCount, firstPosition);
                        }

                        View var23 = this.getFocusedChild();
                        if (var23 != null) {
                            if (!dataChanged || this.isDirectChildHeaderOrFooter(var23)) {
                                focusLayoutRestoreDirectChild = var23;
                                focusLayoutRestoreView = this.findFocus();
                                if (focusLayoutRestoreView != null) {
                                    focusLayoutRestoreView.onStartTemporaryDetach();
                                }
                            }

                            this.requestFocus();
                        }

                        this.detachAllViewsFromParent();
                        recycleBin.removeSkippedScrap();
                        View sel;
                        switch (this.mLayoutMode) {
                            case 2:
                                if (newSel != null) {
                                    sel = this.fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
                                } else {
                                    sel = this.fillFromMiddle(childrenLeft, childrenRight);
                                }
                                break;
                            case 3:
                            default:
                                if (childCount == 0) {
                                    int child;
                                    if (!this.mStackFromBottom) {
                                        child = this.lookForSelectablePosition(0, true);
                                        this.setSelectedPositionInt(child);
                                        sel = this.fillFromLeft(childrenLeft);
                                    } else {
                                        child = this.lookForSelectablePosition(this.mItemCount - 1, false);
                                        this.setSelectedPositionInt(child);
                                        sel = this.fillLeft(this.mItemCount - 1, childrenRight);
                                    }
                                } else if (this.mSelectedPosition >= 0 && this.mSelectedPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mSelectedPosition, oldSel == null ? childrenLeft : oldSel.getLeft());
                                } else if (this.mFirstPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mFirstPosition, oldFirst == null ? childrenLeft : oldFirst.getLeft());
                                } else {
                                    sel = this.fillSpecific(0, childrenLeft);
                                }
                                break;
                            case 4:
                                sel = this.fillSpecific(this.reconcileSelectedPosition(), this.mSpecificLeft);
                                break;
                            case 5:
                                sel = this.fillSpecific(this.mSyncPosition, this.mSpecificLeft);
                                break;
                            case 6:
                                sel = this.moveSelection(oldSel, newSel, delta, childrenLeft, childrenRight);
                                break;
                            case 7:
                                this.mFirstPosition = 0;
                                sel = this.fillFromLeft(childrenLeft);
                                this.adjustViewsLeftOrRight();
                                break;
                            case 8:
                                sel = this.fillLeft(this.mItemCount - 1, childrenRight);
                                this.adjustViewsLeftOrRight();
                        }

                        recycleBin.scrapActiveViews();
                        if (sel != null) {
                            if (this.mItemsCanFocus && this.hasFocus() && !sel.hasFocus()) {
                                boolean var25 = sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus() || sel.requestFocus();
                                if (!var25) {
                                    View focused = this.getFocusedChild();
                                    if (focused != null) {
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
                            if (this.mTouchMode > 0 && this.mTouchMode < 3) {
                                View var24 = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
                                if (var24 != null) {
                                    this.positionSelector(this.mMotionPosition, var24);
                                }
                            } else {
                                this.mSelectorRect.setEmpty();
                            }

                            if (this.hasFocus() && focusLayoutRestoreView != null) {
                                focusLayoutRestoreView.requestFocus();
                            }
                        }

                        if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
                            focusLayoutRestoreView.onFinishTemporaryDetach();
                        }

                        this.mLayoutMode = 0;
                        this.mDataChanged = false;
                        this.mNeedSync = false;
                        this.setNextSelectedPositionInt(this.mSelectedPosition);
                        if (this.mItemCount > 0) {
                            this.checkSelectionChanged();
                        }
                        break label408;
                    }

                    this.resetList();
                } finally {
                    if (!blockLayoutRequests) {
                        this.mBlockLayoutRequests = false;
                    }

                }

                return;
            }

            Log.d("IntevelHListView", "layoutChildren: list = " + this.convertListToString());
        }
    }

    protected String convertListToString() {
        String s = "";

        for (int i = 0; i < this.list.size(); ++i) {
            s = s + ((IntevelHListView.PositionInfo) this.list.get(i)).toString();
        }

        return s;
    }

    private View fillFromSelection(int selectedLeft, int childrenLeft, int childrenRight) {
        int fadingEdgeLength = this.getHorizontalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int leftSelectionPixel = this.getLeftSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);
        int rightSelectionPixel = this.getRightSelectionPixel(childrenRight, fadingEdgeLength, selectedPosition);
        View sel = this.makeAndAddView(selectedPosition, selectedLeft, true, this.mListPadding.top, true);
        int spaceLeft;
        int spaceRight;
        int offset;
        if (sel.getRight() > rightSelectionPixel) {
            spaceLeft = sel.getLeft() - leftSelectionPixel;
            spaceRight = sel.getRight() - rightSelectionPixel;
            offset = Math.min(spaceLeft, spaceRight);
            sel.offsetLeftAndRight(-offset);
        } else if (sel.getLeft() < leftSelectionPixel) {
            spaceLeft = leftSelectionPixel - sel.getLeft();
            spaceRight = rightSelectionPixel - sel.getRight();
            offset = Math.min(spaceLeft, spaceRight);
            sel.offsetLeftAndRight(offset);
        }

        this.fillLeftAndRight(sel, selectedPosition);
        if (!this.mStackFromBottom) {
            this.correctTooWide(this.getChildCount());
        } else {
            this.correctTooNarrow(this.getChildCount());
        }

        return sel;
    }

    private View fillFromMiddle(int childrenLeft, int childrenRight) {
        int width = childrenRight - childrenLeft;
        int position = this.reconcileSelectedPosition();
        View sel = this.makeAndAddView(position, childrenLeft, true, this.mListPadding.top, true);
        this.mFirstPosition = position;
        int selWidth = sel.getMeasuredWidth();
        if (selWidth <= width) {
            sel.offsetLeftAndRight((width - selWidth) / 2);
        }

        this.fillLeftAndRight(sel, position);
        if (!this.mStackFromBottom) {
            this.correctTooWide(this.getChildCount());
        } else {
            this.correctTooNarrow(this.getChildCount());
        }

        return sel;
    }

    private View fillSpecific(int position, int left) {
        boolean tempIsSelected = position == this.mSelectedPosition;
        View temp = this.makeAndAddView(position, left, true, this.mListPadding.top, tempIsSelected);
        String item = this.mAdapter.getItem(position).toString();
        this.mItemLeft = item;
        this.mItemRight = item;
        String tempItem = item;

        int tempPosition;
        for (tempPosition = position - 1; tempItem.equals(this.mAdapter.getItem(tempPosition).toString()) && tempPosition >= 0; --tempPosition) {
            ;
        }

        ++tempPosition;
        if (tempPosition >= this.getFirstVisiblePosition() && tempPosition <= this.getLastVisiblePosition()) {
            this.list.add(new IntevelHListView.PositionInfo(tempPosition, item));
        }

        this.mFirstPosition = position;
        int dividerWidth = this.mDividerWidth;
        View leftTowards;
        View rightTowards;
        int childCount;
        if (!this.mStackFromBottom) {
            leftTowards = this.fillLeft(position - 1, temp.getLeft() - dividerWidth - this.mSpacing);
            this.adjustViewsLeftOrRight();
            rightTowards = this.fillRight(position + 1, temp.getRight() + dividerWidth + this.mSpacing);
            childCount = this.getChildCount();
            if (childCount > 0) {
                this.correctTooWide(childCount);
            }
        } else {
            rightTowards = this.fillRight(position + 1, temp.getRight() + dividerWidth);
            this.adjustViewsLeftOrRight();
            leftTowards = this.fillLeft(position - 1, temp.getLeft() - dividerWidth);
            childCount = this.getChildCount();
            if (childCount > 0) {
                this.correctTooNarrow(childCount);
            }
        }

        return tempIsSelected ? temp : (leftTowards != null ? leftTowards : rightTowards);
    }

    private View fillLeft(int pos, int nextRight) {
        View selectedView = null;
        int end = 0;
        if ((this.getGroupFlags() & 34) == 34) {
            end = this.mListPadding.top;
        }

        for (; nextRight > end && pos >= 0; --pos) {
            boolean selected = pos == this.mSelectedPosition;
            String item = this.mAdapter.getItem(pos).toString();
            if (!this.mItemLeft.equals(item)) {
                nextRight -= this.mIntevel;
                if (nextRight <= end) {
                    break;
                }

                this.list.add(0, new IntevelHListView.PositionInfo(pos + 1, this.mAdapter.getItem(pos + 1).toString()));
                this.mItemLeft = item.toString();
            }

            if (pos == 0) {
                this.list.add(0, new IntevelHListView.PositionInfo(pos, this.mAdapter.getItem(pos).toString()));
            }

            View child = this.makeAndAddView(pos, nextRight, false, this.mListPadding.top, selected);
            nextRight = child.getLeft() - this.mDividerWidth - this.mSpacing;
            if (selected) {
                selectedView = child;
            }
        }

        this.mFirstPosition = pos + 1;
        return selectedView;
    }

    private void adjustViewsLeftOrRight() {
        int childCount = this.getChildCount();
        if (childCount > 0) {
            int delta;
            View child;
            if (!this.mStackFromBottom) {
                child = this.getChildAt(0);
                delta = child.getLeft() - this.mListPadding.left;
                if (this.mFirstPosition != 0) {
                    delta -= this.mDividerWidth;
                }

                if (delta < 0) {
                    delta = 0;
                }
            } else {
                child = this.getChildAt(childCount - 1);
                delta = child.getRight() - (this.getWidth() - this.mListPadding.right);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mDividerWidth;
                }

                if (delta > 0) {
                    delta = 0;
                }
            }

            if (delta != 0) {
                this.offsetChildrenLeftAndRight(-delta);
            }
        }

    }

    private View fillFromLeft(int nextLeft) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }

        this.mItemLeft = this.mAdapter.getItem(this.mFirstPosition).toString();
        return this.fillRight(this.mFirstPosition, nextLeft);
    }

    private View moveSelection(View oldSel, View newSel, int delta, int childrenLeft, int childrenRight) {
        int fadingEdgeLength = this.getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int leftSelectionPixel = this.getLeftSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);
        int rightSelectionPixel = this.getRightSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);
        View sel;
        int oldLeft;
        int newRight;
        int halfHorizontalSpace;
        int offset;
        if (delta > 0) {
            oldSel = this.makeAndAddView(selectedPosition - 1, oldSel.getLeft(), true, this.mListPadding.top, false);
            oldLeft = this.mDividerWidth;
            sel = this.makeAndAddView(selectedPosition, oldSel.getRight() + oldLeft, true, this.mListPadding.top, true);
            if (sel.getRight() > rightSelectionPixel) {
                newRight = sel.getLeft() - leftSelectionPixel;
                halfHorizontalSpace = sel.getRight() - rightSelectionPixel;
                offset = (childrenRight - childrenLeft) / 2;
                int offset1 = Math.min(newRight, halfHorizontalSpace);
                offset1 = Math.min(offset1, offset);
                oldSel.offsetLeftAndRight(-offset1);
                sel.offsetLeftAndRight(-offset1);
            }

            if (!this.mStackFromBottom) {
                this.fillLeft(this.mSelectedPosition - 2, sel.getLeft() - oldLeft);
                this.adjustViewsLeftOrRight();
                this.fillRight(this.mSelectedPosition + 1, sel.getRight() + oldLeft);
            } else {
                this.fillRight(this.mSelectedPosition + 1, sel.getRight() + oldLeft);
                this.adjustViewsLeftOrRight();
                this.fillLeft(this.mSelectedPosition - 2, sel.getLeft() - oldLeft);
            }
        } else if (delta < 0) {
            if (newSel != null) {
                sel = this.makeAndAddView(selectedPosition, newSel.getLeft(), true, this.mListPadding.top, true);
            } else {
                sel = this.makeAndAddView(selectedPosition, oldSel.getLeft(), false, this.mListPadding.top, true);
            }

            if (sel.getLeft() < leftSelectionPixel) {
                oldLeft = leftSelectionPixel - sel.getLeft();
                newRight = rightSelectionPixel - sel.getRight();
                halfHorizontalSpace = (childrenRight - childrenLeft) / 2;
                offset = Math.min(oldLeft, newRight);
                offset = Math.min(offset, halfHorizontalSpace);
                sel.offsetLeftAndRight(offset);
            }

            this.fillLeftAndRight(sel, selectedPosition);
        } else {
            oldLeft = oldSel.getLeft();
            sel = this.makeAndAddView(selectedPosition, oldLeft, true, this.mListPadding.top, true);
            if (oldLeft < childrenLeft) {
                newRight = sel.getRight();
                if (newRight < childrenLeft + 20) {
                    sel.offsetLeftAndRight(childrenLeft - sel.getLeft());
                }
            }

            this.fillLeftAndRight(sel, selectedPosition);
        }

        return sel;
    }

    private int getLeftSelectionPixel(int childrenLeft, int fadingEdgeLength, int selectedPosition) {
        int leftSelectionPixel = childrenLeft;
        if (selectedPosition > 0) {
            leftSelectionPixel = childrenLeft + fadingEdgeLength;
        }

        return leftSelectionPixel;
    }

    private int getRightSelectionPixel(int childrenRight, int fadingEdgeLength, int selectedPosition) {
        int rightSelectionPixel = childrenRight;
        if (selectedPosition != this.mItemCount - 1) {
            rightSelectionPixel = childrenRight - fadingEdgeLength;
        }

        return rightSelectionPixel;
    }

    private View fillRight(int pos, int nextLeft) {
        View selectedView = null;
        int end = this.getRight() - this.getLeft();
        if ((this.getGroupFlags() & 34) == 34) {
            end -= this.mListPadding.right;
        }

        for (; nextLeft < end && pos < this.mItemCount; ++pos) {
            boolean selected = pos == this.mSelectedPosition;
            String item = this.mAdapter.getItem(pos).toString();
            if (!this.mItemRight.equals(item)) {
                if (pos != 0) {
                    nextLeft += this.mIntevel;
                }

                if (nextLeft >= end) {
                    break;
                }

                this.list.add(new IntevelHListView.PositionInfo(pos, item));
                this.mItemRight = item.toString();
            }

            View child = this.makeAndAddView(pos, nextLeft, true, this.mListPadding.top, selected);
            nextLeft = child.getRight() + this.mDividerWidth + this.mSpacing;
            if (selected) {
                selectedView = child;
            }
        }

        return selectedView;
    }

    int checkItemRightIntevel(int position, boolean add) {
        String item = this.mAdapter.getItem(position).toString();
        int intevel = 0;
        if (!this.mItemRight.equals(item)) {
            if (add) {
                this.list.add(new IntevelHListView.PositionInfo(position, item));
            }

            this.mItemRight = item.toString();
            intevel = this.mIntevel;
        }

        if (position == 0) {
            intevel = 0;
        }

        return intevel;
    }

    int checkItemLeftIntevel(int position, boolean add) {
        String item = this.mAdapter.getItem(position).toString();
        int intevel = 0;
        if (!this.mItemLeft.equals(item)) {
            if (add) {
                this.list.add(0, new IntevelHListView.PositionInfo(position + 1, this.mAdapter.getItem(position + 1).toString()));
            }

            this.mItemLeft = item.toString();
            intevel = this.mIntevel;
        }

        if (position == 0) {
            if (add) {
                this.list.add(0, new IntevelHListView.PositionInfo(position, this.mAdapter.getItem(position).toString()));
            }

            intevel = 0;
        }

        return intevel;
    }

    private void fillLeftAndRight(View sel, int position) {
        int dividerWidth = this.mDividerWidth;
        if (!this.mStackFromBottom) {
            this.fillLeft(position - 1, sel.getLeft() - dividerWidth);
            this.adjustViewsLeftOrRight();
            this.fillRight(position + 1, sel.getRight() + dividerWidth);
        } else {
            this.fillRight(position + 1, sel.getRight() + dividerWidth);
            this.adjustViewsLeftOrRight();
            this.fillLeft(position - 1, sel.getLeft() - dividerWidth);
        }

    }

    private void correctTooWide(int childCount) {
        int lastPosition = this.mFirstPosition + childCount - 1;
        if (lastPosition == this.mItemCount - 1 && childCount > 0) {
            View lastChild = this.getChildAt(childCount - 1);
            int lastRight = lastChild.getRight();
            int end = this.getRight() - this.getLeft() - this.mListPadding.right;
            int rightOffset = end - lastRight;
            View firstChild = this.getChildAt(0);
            int firstLeft = firstChild.getLeft();
            if (rightOffset > 0 && (this.mFirstPosition > 0 || firstLeft < this.mListPadding.left)) {
                if (this.mFirstPosition == 0) {
                    rightOffset = Math.min(rightOffset, this.mListPadding.left - firstLeft);
                }

                this.offsetChildrenLeftAndRight(rightOffset);
                if (this.mFirstPosition > 0) {
                    this.fillLeft(this.mFirstPosition - 1, firstChild.getLeft() - this.mDividerWidth);
                    this.adjustViewsLeftOrRight();
                }
            }
        }

    }

    private void correctTooNarrow(int childCount) {
        if (this.mFirstPosition == 0 && childCount > 0) {
            View firstChild = this.getChildAt(0);
            int firstLeft = firstChild.getLeft();
            int start = this.mListPadding.left;
            int end = this.getRight() - this.getLeft() - this.mListPadding.right;
            int leftOffset = firstLeft - start;
            View lastChild = this.getChildAt(childCount - 1);
            int lastRight = lastChild.getRight();
            int lastPosition = this.mFirstPosition + childCount - 1;
            if (leftOffset > 0) {
                if (lastPosition >= this.mItemCount - 1 && lastRight <= end) {
                    if (lastPosition == this.mItemCount - 1) {
                        this.adjustViewsLeftOrRight();
                    }
                } else {
                    if (lastPosition == this.mItemCount - 1) {
                        leftOffset = Math.min(leftOffset, lastRight - end);
                    }

                    this.offsetChildrenLeftAndRight(-leftOffset);
                    if (lastPosition < this.mItemCount - 1) {
                        this.fillRight(lastPosition + 1, lastChild.getRight() + this.mDividerWidth);
                        this.adjustViewsLeftOrRight();
                    }
                }
            }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (this.mItemCount > 0 && (widthMode == 0 || heightMode == 0)) {
            View child = this.obtainView(0, this.mIsScrap);
            this.measureScrapChild(child, 0, heightMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = combineMeasuredStates(childState, child.getMeasuredState());
            if (this.recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((AbsBaseListView.LayoutParams) child.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(child, -1);
            }
        }

        if (heightMode == 0) {
            heightSize = this.mListPadding.top + this.mListPadding.bottom + childHeight + this.getHorizontalFadingEdgeLength();
        } else {
            heightSize |= childState & -16777216;
        }

        if (widthMode == 0) {
            widthSize = this.mListPadding.left + this.mListPadding.right + childWidth + this.getHorizontalFadingEdgeLength() * 2;
        }

        if (heightMode == -2147483648) {
            heightSize = this.measureWidthOfChildren(heightMeasureSpec, 0, -1, widthSize, -1);
        }

        this.setMeasuredDimension(widthSize, heightSize);
        this.mHeightMeasureSpec = heightMeasureSpec;
    }

    private View makeAndAddView(int position, int x, boolean flow, int childrenLeft, boolean selected) {
        View child;
        if (!this.mDataChanged) {
            child = this.mRecycler.getActiveView(position);
            if (child != null) {
                this.setupChild(child, position, x, flow, childrenLeft, selected, true);
                return child;
            }
        }

        child = this.obtainView(position, this.mIsScrap);
        this.setupChild(child, position, x, flow, childrenLeft, selected, this.mIsScrap[0]);
        return child;
    }

    private void setupChild(View child, int position, int x, boolean flowLeft, int childrenTop, boolean selected, boolean recycled) {
        boolean isSelected = selected && this.shouldShowSelector();
        boolean updateChildSelected = isSelected ^ child.isSelected();
        int mode = this.mTouchMode;
        boolean isPressed = mode > 0 && mode < 3 && this.mMotionPosition == position;
        boolean updateChildPressed = isPressed ^ child.isPressed();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();
        AbsBaseListView.LayoutParams p = (AbsBaseListView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsBaseListView.LayoutParams) this.generateDefaultLayoutParams();
        }

        p.viewType = this.mAdapter.getItemViewType(position);
        if (recycled && !p.forceAdd || p.recycledHeaderFooter && p.viewType == -2) {
            this.attachViewToParent(child, flowLeft ? -1 : 0, p);
        } else {
            p.forceAdd = false;
            if (p.viewType == -2) {
                p.recycledHeaderFooter = true;
            }

            this.addViewInLayout(child, flowLeft ? -1 : 0, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
        }

        if (updateChildPressed) {
            child.setPressed(isPressed);
        }

        if (this.mChoiceMode != 0 && this.mCheckStates != null) {
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(this.mCheckStates.get(position));
            } else if (this.getContext().getApplicationInfo().targetSdkVersion >= 11) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }

        int w;
        int childLeft;
        int h;
        if (needToMeasure) {
            w = android.view.ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mListPadding.top + this.mListPadding.bottom, p.width);
            h = p.width;
            if (h > 0) {
                childLeft = View.MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            } else {
                childLeft = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }

            child.measure(childLeft, w);
        } else {
            this.cleanupLayoutState(child);
        }

        w = child.getMeasuredWidth();
        h = child.getMeasuredHeight();
        childLeft = flowLeft ? x : x - w;
        if (needToMeasure) {
            int childRight = childLeft + w;
            int childBottom = childrenTop + h;
            child.layout(childLeft, childrenTop, childRight, childBottom);
        } else {
            child.offsetTopAndBottom(childrenTop - child.getTop());
            child.offsetLeftAndRight(childLeft - child.getLeft());
        }

        if (this.mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }

        if (recycled && ((AbsBaseListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition != position) {
            child.jumpDrawablesToCurrentState();
        }

    }

    private void measureScrapChild(View child, int position, int heightMeasureSpec) {
        AbsBaseListView.LayoutParams p = (AbsBaseListView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsBaseListView.LayoutParams) this.generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }

        p.viewType = this.mAdapter.getItemViewType(position);
        p.forceAdd = true;
        int childHeightSpec = android.view.ViewGroup.getChildMeasureSpec(heightMeasureSpec, this.mListPadding.top + this.mListPadding.bottom, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    protected boolean recycleOnMeasure() {
        return true;
    }

    final int measureWidthOfChildren(int heightMeasureSpec, int startPosition, int endPosition, int maxWidth, int disallowPartialChildPosition) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return this.mListPadding.left + this.mListPadding.right;
        } else {
            int returnedWidth = this.mListPadding.left + this.mListPadding.right;
            boolean dividerHeight = false;
            int prevWidthWithoutPartialChild = 0;
            endPosition = endPosition == -1 ? adapter.getCount() - 1 : endPosition;
            AbsBaseListView.RecycleBin recycleBin = this.mRecycler;
            boolean recyle = this.recycleOnMeasure();
            boolean[] isScrap = this.mIsScrap;

            for (int i = startPosition; i <= endPosition; ++i) {
                View child = this.obtainView(i, isScrap);
                this.measureScrapChild(child, i, heightMeasureSpec);
                if (i > 0) {
                    returnedWidth += 0;
                }

                if (recyle && recycleBin.shouldRecycleViewType(((AbsBaseListView.LayoutParams) child.getLayoutParams()).viewType)) {
                    recycleBin.addScrapView(child, -1);
                }

                returnedWidth += child.getMeasuredWidth();
                if (returnedWidth >= maxWidth) {
                    return disallowPartialChildPosition >= 0 && i > disallowPartialChildPosition && prevWidthWithoutPartialChild > 0 && returnedWidth != maxWidth ? prevWidthWithoutPartialChild : maxWidth;
                }

                if (disallowPartialChildPosition >= 0 && i >= disallowPartialChildPosition) {
                    prevWidthWithoutPartialChild = returnedWidth;
                }
            }

            return returnedWidth;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled) {
            View focused = this.getFocusedChild();
            if (focused != null && event.getAction() == 0) {
                handled = this.onKeyDown(event.getKeyCode(), event);
            }
        }

        return handled;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (this.mAdapter != null && this.mIsAttached) {
            if (this.mDataChanged) {
                this.layoutChildren();
            }

            boolean handled = false;
            int action = event.getAction();
            if (action != 1) {
                switch (keyCode) {
                    case 19:
                        if (event.hasNoModifiers()) {
                            handled = this.handleVerticalFocusWithinListItem(33);
                        }
                        break;
                    case 20:
                        if (event.hasNoModifiers()) {
                            handled = this.handleVerticalFocusWithinListItem(130);
                        }
                        break;
                    case 21:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if (!handled) {
                                while (count-- > 0 && this.arrowScroll(17)) {
                                    this.checkItemRightIntevel(this.getLastVisiblePosition(), false);
                                    handled = true;
                                }
                            }
                        } else if (event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(17);
                        }
                        break;
                    case 22:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if (!handled) {
                                while (count-- > 0 && this.arrowScroll(66)) {
                                    this.checkItemLeftIntevel(this.getFirstVisiblePosition(), false);
                                    handled = true;
                                }
                            }
                        } else if (event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(66);
                        }
                        break;
                    case 23:
                    case 66:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded();
                            if (!handled && event.getRepeatCount() == 0 && this.getChildCount() > 0) {
                                this.keyPressed();
                                handled = true;
                            }
                        }
                    case 61:
                    case 62:
                    default:
                        break;
                    case 92:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.pageScroll(17);
                        } else if (event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(17);
                        }
                        break;
                    case 93:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.pageScroll(66);
                        } else if (event.hasModifiers(2)) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(66);
                        }
                        break;
                    case 122:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(17);
                        }
                        break;
                    case 123:
                        if (event.hasNoModifiers()) {
                            handled = this.resurrectSelectionIfNeeded() || this.fullScroll(66);
                        }
                }
            }

            Log.d("IntevelHListView", "commonKey: list = " + this.convertListToString());
            if (handled) {
                return true;
            } else {
                switch (action) {
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
            if (handled) {
                this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }

            var4 = handled;
        } finally {
            this.mInLayout = false;
        }

        return var4;
    }

    private boolean arrowScrollImpl(int direction) {
        if (this.getChildCount() <= 0) {
            return false;
        } else {
            View selectedView = this.getSelectedView();
            int selectedPos = this.mSelectedPosition;
            int nextSelectedPosition = this.lookForSelectablePositionOnScreen(direction);
            int amountToScroll = this.amountToScroll(direction, nextSelectedPosition);
            IntevelHListView.ArrowScrollFocusResult focusResult = this.mItemsCanFocus ? this.arrowScrollFocused(direction) : null;
            if (focusResult != null) {
                nextSelectedPosition = focusResult.getSelectedPosition();
                amountToScroll = focusResult.getAmountToScroll();
            }

            boolean needToRedraw = focusResult != null;
            View focused;
            if (nextSelectedPosition != -1) {
                this.handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                selectedView = this.getSelectedView();
                selectedPos = nextSelectedPosition;
                if (this.mItemsCanFocus && focusResult == null) {
                    focused = this.getFocusedChild();
                    if (focused != null) {
                        focused.clearFocus();
                    }
                }

                needToRedraw = true;
                this.checkSelectionChanged();
            }

            if (amountToScroll > 0) {
                this.scrollListItemsBy(direction == 17 ? amountToScroll : -amountToScroll);
                needToRedraw = true;
            }

            if (this.mItemsCanFocus && focusResult == null && selectedView != null && selectedView.hasFocus()) {
                focused = selectedView.findFocus();
                if (!this.isViewAncestorOf(focused, this) || this.distanceToView(focused) > 0) {
                    focused.clearFocus();
                }
            }

            if (nextSelectedPosition == -1 && selectedView != null && !this.isViewAncestorOf(selectedView, this)) {
                selectedView = null;
                this.hideSelector();
                this.mResurrectToPosition = -1;
            }

            if (needToRedraw) {
                if (selectedView != null) {
                    this.positionSelector(selectedPos, selectedView);
                }

                if (!this.awakenScrollBars()) {
                    this.invalidate();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private void scrollListItemsBy(int amount) {
        this.offsetChildrenLeftAndRight(amount);
        int listRight = this.getWidth() - this.mListPadding.right;
        int listLeft = this.mListPadding.left;
        AbsBaseListView.RecycleBin recycleBin = this.mRecycler;
        AbsBaseListView.LayoutParams layoutParams;
        View var11;
        if (amount < 0) {
            int first = this.getChildCount();

            View lastIndex;
            for (lastIndex = this.getChildAt(first - 1); lastIndex.getRight() < listRight; ++first) {
                int last = this.mFirstPosition + first - 1;
                if (last >= this.mItemCount - 1) {
                    break;
                }

                lastIndex = this.addViewRight(lastIndex, last);
            }

            if (lastIndex.getRight() < listRight) {
                this.offsetChildrenLeftAndRight(listRight - lastIndex.getRight());
            }

            for (var11 = this.getChildAt(0); var11.getRight() < listLeft; ++this.mFirstPosition) {
                if (this.list.size() > 0 && (((IntevelHListView.PositionInfo) this.list.get(0)).position() == this.getFirstVisiblePosition() || ((IntevelHListView.PositionInfo) this.list.get(0)).position() < this.getFirstVisiblePosition() || ((IntevelHListView.PositionInfo) this.list.get(0)).position() > this.getLastVisiblePosition())) {
                    this.list.remove(0);
                }

                layoutParams = (AbsBaseListView.LayoutParams) var11.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    this.detachViewFromParent(var11);
                    recycleBin.addScrapView(var11, this.mFirstPosition);
                } else {
                    this.removeViewInLayout(var11);
                }

                var11 = this.getChildAt(0);
            }
        } else {
            View var9;
            for (var9 = this.getChildAt(0); var9.getLeft() > listLeft && this.mFirstPosition > 0; --this.mFirstPosition) {
                var9 = this.addViewLeft(var9, this.mFirstPosition);
            }

            if (var9.getLeft() > listLeft) {
                this.offsetChildrenLeftAndRight(listLeft - var9.getLeft());
            }

            int var10 = this.getChildCount() - 1;

            for (var11 = this.getChildAt(var10); var11.getLeft() > listRight; var11 = this.getChildAt(var10)) {
                if (this.list.size() > 0 && (((IntevelHListView.PositionInfo) this.list.get(this.list.size() - 1)).position() == this.getLastVisiblePosition() || ((IntevelHListView.PositionInfo) this.list.get(this.list.size() - 1)).position() < this.getFirstVisiblePosition() || ((IntevelHListView.PositionInfo) this.list.get(this.list.size() - 1)).position() > this.getLastVisiblePosition())) {
                    this.list.remove(this.list.size() - 1);
                }

                layoutParams = (AbsBaseListView.LayoutParams) var11.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    this.detachViewFromParent(var11);
                    recycleBin.addScrapView(var11, this.mFirstPosition + var10);
                } else {
                    this.removeViewInLayout(var11);
                }

                --var10;
            }
        }

    }

    private View addViewLeft(View theView, int position) {
        int leftPosition = position - 1;
        View view = this.obtainView(leftPosition, this.mIsScrap);
        int edgeOfNewChild = theView.getLeft() - this.mDividerWidth - this.mSpacing;
        edgeOfNewChild -= this.checkItemLeftIntevel(leftPosition, true);
        this.setupChild(view, leftPosition, edgeOfNewChild, false, this.mListPadding.top, false, this.mIsScrap[0]);
        return view;
    }

    private View addViewRight(View theView, int position) {
        int rightPosition = position + 1;
        View view = this.obtainView(rightPosition, this.mIsScrap);
        int edgeOfNewChild = theView.getRight() + this.mDividerWidth + this.mSpacing;
        edgeOfNewChild += this.checkItemRightIntevel(rightPosition, true);
        this.setupChild(view, rightPosition, edgeOfNewChild, true, this.mListPadding.top, false, this.mIsScrap[0]);
        return view;
    }

    private int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(this.mTempRect);
        this.offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        int listRight = this.getRight() - this.getLeft() - this.mListPadding.right;
        if (this.mTempRect.right < this.mListPadding.left) {
            distance = this.mListPadding.left - this.mTempRect.right;
        } else if (this.mTempRect.left > listRight) {
            distance = this.mTempRect.left - listRight;
        }

        return distance;
    }

    private boolean isViewAncestorOf(View child, View parent) {
        if (child == parent) {
            return true;
        } else {
            ViewParent theParent = child.getParent();
            return theParent instanceof android.view.ViewGroup && this.isViewAncestorOf((View) theParent, parent);
        }
    }

    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        if (newSelectedPosition == -1) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        } else {
            boolean leftSelected = false;
            int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
            int nextSelectedIndex = newSelectedPosition - this.mFirstPosition;
            View leftView;
            View rightView;
            int leftViewIndex;
            int rightViewIndex;
            if (direction == 17) {
                leftViewIndex = nextSelectedIndex;
                rightViewIndex = selectedIndex;
                leftView = this.getChildAt(nextSelectedIndex);
                rightView = selectedView;
                leftSelected = true;
            } else {
                leftViewIndex = selectedIndex;
                rightViewIndex = nextSelectedIndex;
                leftView = selectedView;
                rightView = this.getChildAt(nextSelectedIndex);
            }

            int numChildren = this.getChildCount();
            if (leftView != null) {
                leftView.setSelected(!newFocusAssigned && leftSelected);
                this.measureAndAdjustRight(leftView, leftViewIndex, numChildren);
            }

            if (rightView != null) {
                rightView.setSelected(!newFocusAssigned && !leftSelected);
                this.measureAndAdjustRight(rightView, rightViewIndex, numChildren);
            }

        }
    }

    private void measureAndAdjustRight(View child, int childIndex, int numChildren) {
        int oldWidth = child.getWidth();
        this.measureItem(child);
        if (child.getMeasuredWidth() != oldWidth) {
            this.relayoutMeasuredItem(child);
            int widthDelta = child.getMeasuredWidth() - oldWidth;

            for (int i = childIndex + 1; i < numChildren; ++i) {
                this.getChildAt(i).offsetLeftAndRight(widthDelta);
            }
        }

    }

    private void measureItem(View child) {
        android.view.ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new android.view.ViewGroup.LayoutParams(-1, -2);
        }

        int childHeightSpec = android.view.ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mListPadding.top + this.mListPadding.bottom, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    private void relayoutMeasuredItem(View child) {
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childLeft = child.getLeft();
        int childRight = childLeft + w;
        int childTop = this.mListPadding.top;
        int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    private IntevelHListView.ArrowScrollFocusResult arrowScrollFocused(int direction) {
        View selectedView = this.getSelectedView();
        View newFocus;
        int focusScroll;
        int maxScrollAmount;
        if (selectedView != null && selectedView.hasFocus()) {
            View positionOfNewFocus1 = selectedView.findFocus();
            newFocus = android.view.FocusFinder.getInstance().findNextFocus(this, positionOfNewFocus1, direction);
        } else {
            boolean positionOfNewFocus;
            if (direction == 66) {
                positionOfNewFocus = this.mFirstPosition > 0;
                focusScroll = this.mListPadding.left + (positionOfNewFocus ? this.getArrowScrollPreviewLength() : 0);
                maxScrollAmount = selectedView != null && selectedView.getLeft() > focusScroll ? selectedView.getLeft() : focusScroll;
                this.mTempRect.set(maxScrollAmount, 0, maxScrollAmount, 0);
            } else {
                positionOfNewFocus = this.mFirstPosition + this.getChildCount() - 1 < this.mItemCount;
                focusScroll = this.getWidth() - this.mListPadding.right - (positionOfNewFocus ? this.getArrowScrollPreviewLength() : 0);
                maxScrollAmount = selectedView != null && selectedView.getRight() < focusScroll ? selectedView.getRight() : focusScroll;
                this.mTempRect.set(maxScrollAmount, 0, maxScrollAmount, 0);
            }

            newFocus = android.view.FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, direction);
        }

        if (newFocus != null) {
            int positionOfNewFocus2 = this.positionOfNewFocus(newFocus);
            if (this.mSelectedPosition != -1 && positionOfNewFocus2 != this.mSelectedPosition) {
                focusScroll = this.lookForSelectablePositionOnScreen(direction);
                if (focusScroll != -1 && (direction == 66 && focusScroll < positionOfNewFocus2 || direction == 17 && focusScroll > positionOfNewFocus2)) {
                    return null;
                }
            }

            focusScroll = this.amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus2);
            maxScrollAmount = this.getMaxScrollAmount();
            if (focusScroll < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus2, focusScroll);
                return this.mArrowScrollFocusResult;
            }

            if (this.distanceToView(newFocus) < maxScrollAmount) {
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
        if (direction == 17) {
            if (this.mTempRect.left < this.mListPadding.left) {
                amountToScroll = this.mListPadding.left - this.mTempRect.left;
                if (positionOfNewFocus > 0) {
                    amountToScroll += this.getArrowScrollPreviewLength();
                }
            }
        } else {
            int listRight = this.getWidth() - this.mListPadding.right;
            if (this.mTempRect.right > listRight) {
                amountToScroll = this.mTempRect.right - listRight;
                if (positionOfNewFocus < this.mItemCount - 1) {
                    amountToScroll += this.getArrowScrollPreviewLength();
                }
            }
        }

        return amountToScroll;
    }

    private int positionOfNewFocus(View newFocus) {
        int numChildren = this.getChildCount();

        for (int i = 0; i < numChildren; ++i) {
            View child = this.getChildAt(i);
            if (this.isViewAncestorOf(newFocus, child)) {
                return this.mFirstPosition + i;
            }
        }

        throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
    }

    private int lookForSelectablePositionOnScreen(int direction) {
        int firstPosition = this.mFirstPosition;
        int last;
        int startPos;
        ListAdapter adapter;
        int pos;
        if (direction == 66) {
            last = this.mSelectedPosition != -1 ? this.mSelectedPosition + 1 : firstPosition;
            if (last >= this.mAdapter.getCount()) {
                return -1;
            }

            if (last < firstPosition) {
                last = firstPosition;
            }

            startPos = this.getLastVisiblePosition();
            adapter = this.getAdapter();

            for (pos = last; pos <= startPos; ++pos) {
                if (adapter.isEnabled(pos) && this.getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
                    return pos;
                }
            }
        } else {
            last = firstPosition + this.getChildCount() - 1;
            startPos = this.mSelectedPosition != -1 ? this.mSelectedPosition - 1 : firstPosition + this.getChildCount() - 1;
            if (startPos < 0 || startPos >= this.mAdapter.getCount()) {
                return -1;
            }

            if (startPos > last) {
                startPos = last;
            }

            adapter = this.getAdapter();

            for (pos = startPos; pos >= firstPosition; --pos) {
                if (adapter.isEnabled(pos) && this.getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
                    return pos;
                }
            }
        }

        return -1;
    }

    private int amountToScroll(int direction, int nextSelectedPosition) {
        int listRight = this.getWidth() - this.mListPadding.right;
        int listLeft = this.mListPadding.left;
        int numChildren = this.getChildCount();
        int indexToMakeVisible;
        int positionToMakeVisible;
        View viewToMakeVisible;
        int goalLeft;
        int amountToScroll;
        int max;
        if (direction == 66) {
            indexToMakeVisible = numChildren - 1;
            if (nextSelectedPosition != -1) {
                indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
            }

            positionToMakeVisible = this.mFirstPosition + indexToMakeVisible;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            goalLeft = listRight;
            if (positionToMakeVisible < this.mItemCount - 1) {
                goalLeft = listRight - this.getArrowScrollPreviewLength();
            }

            if (viewToMakeVisible.getRight() <= goalLeft) {
                return 0;
            } else if (nextSelectedPosition != -1 && goalLeft - viewToMakeVisible.getLeft() >= this.getMaxScrollAmount()) {
                return 0;
            } else {
                amountToScroll = viewToMakeVisible.getRight() - goalLeft;
                if (this.mFirstPosition + numChildren == this.mItemCount) {
                    max = this.getChildAt(numChildren - 1).getRight() - listRight;
                    amountToScroll = Math.min(amountToScroll, max);
                }

                return Math.min(amountToScroll, this.getMaxScrollAmount());
            }
        } else {
            indexToMakeVisible = 0;
            if (nextSelectedPosition != -1) {
                indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
            }

            positionToMakeVisible = this.mFirstPosition + indexToMakeVisible;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            goalLeft = listLeft;
            if (positionToMakeVisible > 0) {
                goalLeft = listLeft + this.getArrowScrollPreviewLength();
            }

            if (viewToMakeVisible.getLeft() >= goalLeft) {
                return 0;
            } else if (nextSelectedPosition != -1 && viewToMakeVisible.getRight() - goalLeft >= this.getMaxScrollAmount()) {
                return 0;
            } else {
                amountToScroll = goalLeft - viewToMakeVisible.getLeft();
                if (this.mFirstPosition == 0) {
                    max = listLeft - this.getChildAt(0).getLeft();
                    amountToScroll = Math.min(amountToScroll, max);
                }

                return Math.min(amountToScroll, this.getMaxScrollAmount());
            }
        }
    }

    private int getArrowScrollPreviewLength() {
        return Math.max(2, this.getHorizontalFadingEdgeLength());
    }

    public int getMaxScrollAmount() {
        return (int) (0.33F * (float) (this.getRight() - this.getLeft()));
    }

    boolean fullScroll(int direction) {
        boolean moved = false;
        int position;
        if (direction == 17) {
            if (this.mSelectedPosition != 0) {
                position = this.lookForSelectablePosition(0, true);
                if (position >= 0) {
                    this.mLayoutMode = 7;
                    this.setSelectionInt(position);
                }

                moved = true;
            }
        } else if (direction == 66 && this.mSelectedPosition < this.mItemCount - 1) {
            position = this.lookForSelectablePosition(this.mItemCount - 1, true);
            if (position >= 0) {
                this.mLayoutMode = 8;
                this.setSelectionInt(position);
            }

            moved = true;
        }

        if (moved && !this.awakenScrollBars()) {
            this.awakenScrollBars();
            this.invalidate();
        }

        return moved;
    }

    private boolean handleVerticalFocusWithinListItem(int direction) {
        if (direction != 33 && direction != 130) {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_UP, View.FOCUS_DOWN}");
        } else {
            int numChildren = this.getChildCount();
            if (this.mItemsCanFocus && numChildren > 0 && this.mSelectedPosition != -1) {
                View selectedView = this.getSelectedView();
                if (selectedView != null && selectedView.hasFocus() && selectedView instanceof android.view.ViewGroup) {
                    View currentFocus = selectedView.findFocus();
                    View nextFocus = android.view.FocusFinder.getInstance().findNextFocus((android.view.ViewGroup) selectedView, currentFocus, direction);
                    if (nextFocus != null) {
                        currentFocus.getFocusedRect(this.mTempRect);
                        this.offsetDescendantRectToMyCoords(currentFocus, this.mTempRect);
                        this.offsetRectIntoDescendantCoords(nextFocus, this.mTempRect);
                        if (nextFocus.requestFocus(direction, this.mTempRect)) {
                            return true;
                        }
                    }

                    View globalNextFocus = android.view.FocusFinder.getInstance().findNextFocus((ViewGroup) this.getRootView(), currentFocus, direction);
                    if (globalNextFocus != null) {
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
        if (direction == 17) {
            nextPage = Math.max(0, this.mSelectedPosition - this.getChildCount() - 1);
        } else if (direction == 66) {
            nextPage = Math.min(this.mItemCount - 1, this.mSelectedPosition + this.getChildCount() - 1);
            down = true;
        }

        if (nextPage >= 0) {
            int position = this.lookForSelectablePosition(nextPage, down);
            if (position >= 0) {
                this.mLayoutMode = 4;
                this.mSpecificLeft = this.getPaddingLeft() + this.getHorizontalFadingEdgeLength();
                if (down && position > this.mItemCount - this.getChildCount()) {
                    this.mLayoutMode = 8;
                }

                if (!down && position < this.getChildCount()) {
                    this.mLayoutMode = 7;
                }

                this.setSelectionInt(position);
                if (!this.awakenScrollBars()) {
                    this.invalidate();
                }

                return true;
            }
        }

        return false;
    }

    void rememberSyncState() {
        if (this.getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = (long) this.mLayoutHeight;
            View v;
            if (this.mSelectedPosition >= 0) {
                v = this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
                this.mSyncRowId = this.mNextSelectedRowId;
                this.mSyncPosition = this.mNextSelectedPosition;
                if (v != null) {
                    this.mSpecificLeft = v.getLeft();
                }

                this.mSyncMode = 0;
            } else {
                v = this.getChildAt(0);
                ListAdapter adapter = this.getAdapter();
                if (this.mFirstPosition >= 0 && this.mFirstPosition < adapter.getCount()) {
                    this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
                } else {
                    this.mSyncRowId = -1L;
                }

                this.mSyncPosition = this.mFirstPosition;
                if (v != null) {
                    this.mSpecificLeft = v.getLeft();
                }

                this.mSyncMode = 1;
            }
        }

    }

    public void setSelection(int selection) {
        this.setSelectionFromLeft(selection, 0);
    }

    public void setSelectionFromLeft(int position, int x) {
        if (this.mAdapter != null) {
            if (!this.isInTouchMode()) {
                position = this.lookForSelectablePosition(position, true);
                if (position >= 0) {
                    this.setNextSelectedPositionInt(position);
                }
            } else {
                this.mResurrectToPosition = position;
            }

            if (position >= 0) {
                this.mLayoutMode = 4;
                this.mSpecificLeft = this.mListPadding.left + x;
                if (this.mNeedSync) {
                    this.mSyncPosition = position;
                    this.mSyncRowId = this.mAdapter.getItemId(position);
                }

                this.layoutChildren();
            }

        }
    }

    int lookForSelectablePosition(int position, boolean lookRight) {
        ListAdapter adapter = this.mAdapter;
        if (adapter != null && !this.isInTouchMode()) {
            int count = adapter.getCount();
            if (this.mAreAllItemsSelectable) {
                return position >= 0 && position < count ? position : -1;
            } else {
                if (lookRight) {
                    for (position = Math.max(0, position); position < count && !adapter.isEnabled(position); ++position) {
                        ;
                    }
                } else {
                    for (position = Math.min(position, count - 1); position >= 0 && !adapter.isEnabled(position); --position) {
                        ;
                    }
                }

                return position >= 0 && position < count ? position : -1;
            }
        } else {
            return -1;
        }
    }

    int findMotionRow(int x) {
        int childCount = this.getChildCount();
        if (childCount > 0) {
            int i;
            View v;
            if (!this.mStackFromBottom) {
                for (i = 0; i < childCount; ++i) {
                    v = this.getChildAt(i);
                    if (x <= v.getRight()) {
                        return this.mFirstPosition + i;
                    }
                }
            } else {
                for (i = childCount - 1; i >= 0; --i) {
                    v = this.getChildAt(i);
                    if (x >= v.getLeft()) {
                        return this.mFirstPosition + i;
                    }
                }
            }
        }

        return -1;
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

    protected class PositionInfo {
        int position;
        String item;

        public PositionInfo(int p, String i) {
            this.position = p;
            this.item = i;
        }

        public int position() {
            return this.position;
        }

        public String item() {
            return this.item;
        }

        public String toString() {
            return "[position = " + this.position + ", item = " + this.item + "]";
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            } else if (o == this) {
                return true;
            } else if (o instanceof IntevelHListView.PositionInfo) {
                IntevelHListView.PositionInfo other = (IntevelHListView.PositionInfo) o;
                return this.item == null ? this.position == other.position && other.item == null : this.position == other.position && this.item.equals(other.item);
            } else {
                return false;
            }
        }
    }
}
