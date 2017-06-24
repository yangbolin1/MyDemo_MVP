//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.util.Log;
import android.util.SparseArray;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemFlipScroller {
    private final String TAG = "ItemFlipScroller";
    private boolean DEBUG = true;
    private int hor_delay_distance = 50;
    private int ver_delay_distance = 25;
    private int min_fast_setp_discance = 16;
    private int flip_scroll_frame_count = 20;
    private int hor_delay_frame_count = 2;
    private int ver_delay_frame_count = 1;
    private int mFinalFrameCount;
    private int mCurrFrameIndex;
    private boolean mFinished = true;
    private List<List<ItemFlipScroller.FlipItem>> mFlipItemColumnList = new ArrayList();
    private SparseArray<ItemFlipScroller.FlipItem> mFlipItemMap = new SparseArray();
    private int mStartListIndex = -1;
    private int mEndListIndex = -1;
    private int mColumnCount;
    private boolean mIsDown;
    private ItemFlipScroller.ItemFlipScrollerListener mItemFlipScrollerListener;
    private int mPreSelectedPosition = -1;
    private int mCurrSelectedPosition = -1;
    private boolean mFastFlip;
    private boolean mStartingFlipScroll;
    private List<Integer> mHeaderViewList;
    private List<Integer> mFooterViewList;
    private int mTotalItemCount;
    private ItemFlipScroller.FastStep mFastStep = new ItemFlipScroller.FastStep();
    private boolean mItemDelayAnim = true;
    private boolean mIsFastStepComeDown = true;
    private int mPreAddTempItemIndex = -1;
    private AccelerateDecelerateFrameInterpolator mAccelerateDecelerateFrameInterpolator = new AccelerateDecelerateFrameInterpolator();

    public ItemFlipScroller() {
        mFlipItemMap = new SparseArray();
        mFlipItemColumnList = new ArrayList();
        mAccelerateDecelerateFrameInterpolator = new AccelerateDecelerateFrameInterpolator();
        mFastStep = new ItemFlipScroller.FastStep();
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
    }

    public void setDelayAnim(boolean delay) {
        if(this.mFinished) {
            this.mItemDelayAnim = delay;
        } else {
            Log.e("ItemFlipScroller", "setDelayAnim must in Finished use");
        }

    }

    public void setStartingFlipScroll() {
        this.mStartingFlipScroll = true;
    }

    public void setItemFlipScrollerListener(ItemFlipScroller.ItemFlipScrollerListener listener) {
        this.mItemFlipScrollerListener = listener;
    }

    public void setSelectedPosition(int selectedPosition) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "setSelectedPosition selectedPosition=" + selectedPosition + " mPreSelectedPosition=" + this.mPreSelectedPosition);
        }

        this.mPreSelectedPosition = this.mCurrSelectedPosition;
        this.mCurrSelectedPosition = selectedPosition;
    }

    public void setHeaderViewInfo(List<Integer> headerInfo) {
        this.mHeaderViewList = headerInfo;
    }

    public void setFooterViewInfo(List<Integer> footerInfo) {
        this.mFooterViewList = footerInfo;
    }

    public void setTotalItemCount(int totalCount) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "setTotalItemCount totalCount=" + totalCount);
        }

        this.mTotalItemCount = totalCount;
    }

    public void startComeDown() {
        if(this.mFastFlip && this.mIsFastStepComeDown) {
            ItemFlipScroller.FlipItem item;
            int distance;
            int lastRowStart;
            int selectedRowStart;
            int otherColumn;
            int headerCount;
            int footerCount;
            int deltaColumn;
            int i;
            int footer;
            int footerColumnCount;
            if(this.mIsDown) {
                item = this.getFlipItemColumn(0, 0);
                if(item != null) {
                    distance = 0;
                    if(this.mItemDelayAnim) {
                        lastRowStart = this.getRowStart(item.mIndex);
                        selectedRowStart = this.getRowStart(this.mCurrSelectedPosition);
                        otherColumn = 0;
                        headerCount = this.mHeaderViewList.size();
                        if(headerCount > 0 && lastRowStart < headerCount) {
                            footerCount = Math.min(headerCount - 1, selectedRowStart);

                            for(deltaColumn = lastRowStart; deltaColumn <= footerCount; ++deltaColumn) {
                                i = ((Integer)this.mHeaderViewList.get(deltaColumn)).intValue();
                                footer = i >> 16;
                                otherColumn = footer - 1;
                            }
                        }

                        footerCount = this.mFooterViewList.size();
                        if(footerCount > 0 && selectedRowStart >= this.mTotalItemCount - footerCount) {
                            deltaColumn = Math.max(this.mTotalItemCount - footerCount, lastRowStart);

                            for(i = selectedRowStart; i >= deltaColumn; --i) {
                                footer = ((Integer)this.mFooterViewList.get(i)).intValue();
                                footerColumnCount = footer >> 16;
                                otherColumn = footerColumnCount - 1;
                            }
                        }

                        deltaColumn = (selectedRowStart - lastRowStart) / this.mColumnCount + otherColumn;
                        distance = ((Math.abs(deltaColumn) + 1) * this.hor_delay_distance + (this.mColumnCount - 1) * this.ver_delay_distance) * 2;
                    }

                    this.mFastStep.resetComeDown(item.mFinalDistance - item.mLastDistance - distance);
                }
            } else {
                item = this.getLastColumnFirsterItem();
                if(item != null) {
                    distance = 0;
                    if(this.mItemDelayAnim) {
                        lastRowStart = this.getRowStart(item.mIndex);
                        selectedRowStart = this.getRowStart(this.mCurrSelectedPosition);
                        otherColumn = 0;
                        headerCount = this.mHeaderViewList.size();
                        if(headerCount > 0 && selectedRowStart < headerCount) {
                            footerCount = Math.min(headerCount - 1, selectedRowStart);

                            for(deltaColumn = selectedRowStart; deltaColumn <= footerCount; ++deltaColumn) {
                                i = ((Integer)this.mHeaderViewList.get(deltaColumn)).intValue();
                                footer = i >> 16;
                                otherColumn = footer - 1;
                            }
                        }

                        footerCount = this.mFooterViewList.size();
                        if(footerCount > 0 && lastRowStart >= this.mTotalItemCount - footerCount) {
                            deltaColumn = Math.max(this.mTotalItemCount - footerCount, selectedRowStart);

                            for(i = lastRowStart; i >= deltaColumn; --i) {
                                footer = ((Integer)this.mFooterViewList.get(i)).intValue();
                                footerColumnCount = footer >> 16;
                                otherColumn = footerColumnCount - 1;
                            }
                        }

                        deltaColumn = (lastRowStart - selectedRowStart) / this.mColumnCount + otherColumn;
                        distance = (deltaColumn + 1) * this.hor_delay_distance * 2;
                    }

                    this.mFastStep.resetComeDown(item.mFinalDistance - item.mLastDistance + distance);
                }
            }
        }

    }

    public void setFastScrollOffset(int index, int secondIndex, int offset) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "setFastScrollOffset index=" + index + " secondIndex=" + secondIndex + " offset=" + offset + " mStartingFlipScroll=" + this.mStartingFlipScroll);
        }

        if(this.mStartingFlipScroll) {
            ItemFlipScroller.FlipItem item = (ItemFlipScroller.FlipItem)this.mFlipItemMap.get(this.getFlipItemMapKey(index, secondIndex));
            if(item != null) {
                item.mFastScrollOffset = offset;
            }
        }

    }

    public int getFlipItemLeftMoveDistance(int index, int secondIndex) {
        ItemFlipScroller.FlipItem item = this.getFlipItem(index, secondIndex);
        if(item != null) {
            if(this.DEBUG) {
                Log.i("ItemFlipScroller", "getFlipItemLeftMoveDistance mFinalDistance=" + item.mFinalDistance + " mLastDistance=" + item.mLastDistance);
            }

            return item.mFinalDistance - item.mLastDistance;
        } else {
            return 0;
        }
    }

    public int getFlipColumnFirstItemLeftMoveDistance(int index) {
        if(index > this.mEndListIndex) {
            index = this.mEndListIndex;
        } else if(index < this.mStartListIndex) {
            index = this.mStartListIndex;
        }

        byte secondIndex = 0;
        if(this.mHeaderViewList.size() > 0 && index < this.mHeaderViewList.size()) {
            secondIndex = -1;
        } else if(this.mFooterViewList.size() > 0 && index >= this.mTotalItemCount - this.mFooterViewList.size()) {
            secondIndex = -1;
        }

        ItemFlipScroller.FlipItem item = this.getFlipItem(index, secondIndex);
        if(item != null) {
            if(this.DEBUG) {
                Log.i("ItemFlipScroller", "getFlipColumnFirstItemLeftMoveDistance mFinalDistance=" + item.mFinalDistance + " mLastDistance=" + item.mLastDistance);
            }

            ItemFlipScroller.FlipItem firstItem = this.getFlipItemColumn(item.mColumnIndex, 0);
            if(firstItem != null) {
                return item.mFinalDistance - item.mLastDistance;
            }
        }

        return 0;
    }

    public float getFlipItemMoveRatio(int index, int secondIndex) {
        ItemFlipScroller.FlipItem item = this.getFlipItem(index, secondIndex);
        return item != null?(float)item.mLastDistance / (float)item.mFinalDistance:1.0F;
    }

    public int getFastFlipStep() {
        return this.mFastStep.getCurrStep();
    }

    public void setMaxFastStep(int maxStep) {
        mFastStep.setMaxFastStep(maxStep);
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public void clearChild() {
        this.mFlipItemColumnList.clear();
        this.mFlipItemMap.clear();
        this.mStartListIndex = -1;
        this.mEndListIndex = -1;
    }

    public boolean isDown() {
        return this.mIsDown;
    }

    public void startScroll(int distance, int frameCount, boolean computerRealDistance) {
        if(this.mColumnCount <= 0) {
            Log.e("ItemFlipScroller", "error must set mColumnCount before start scroll");
        }

        this.mStartingFlipScroll = false;
        this.mCurrFrameIndex = 0;
        this.mFinalFrameCount = frameCount;
        if(distance < 0) {
            this.mIsDown = true;
        } else {
            this.mIsDown = false;
        }

        if(!this.mFinished) {
            this.mFastFlip = true;
        }

        int realDistance = distance;
        if(computerRealDistance) {
            realDistance = this.getRealFinalDistance(distance);
        }

        this.resetItemData(realDistance);
        this.mFinished = false;
        Log.i("ItemFlipScroller", "startScroll distance=" + distance + " frameCount=" + frameCount + " mFastFlip=" + this.mFastFlip);
    }

    public void startComputDistanceScroll(int distance, int frameCount) {
        this.startScroll(distance, frameCount, true);
    }

    public void startComputDistanceScroll(int distance) {
        this.startScroll(distance, this.flip_scroll_frame_count, true);
    }

    public void startRealScroll(int distance, int frameCount) {
        this.startScroll(distance, frameCount, false);
    }

    public void startRealScroll(int distance) {
        this.startScroll(distance, this.flip_scroll_frame_count, false);
    }

    public void finish() {
        this.mPreSelectedPosition = -1;
        this.mStartingFlipScroll = false;
        this.mFinalFrameCount = 0;
        if(this.mFastFlip) {
            this.mFastStep.finished();
        }

        this.mFastFlip = false;
        this.clearChild();
        this.mFinished = true;
        if(this.mItemFlipScrollerListener != null) {
            this.mItemFlipScrollerListener.onFinished();
        }

        Log.i("ItemFlipScroller", "finish");
    }

    public boolean computeScrollOffset() {
        if(this.mFinished) {
            return false;
        } else {
            if(this.mFastFlip) {
                this.mFastStep.computerOffset();
            }

            boolean finished;
            if(this.mIsDown) {
                finished = this.computerFlipScrollDown();
            } else {
                finished = this.computerFlipScrollUp();
            }

            boolean ret = true;
            if(finished) {
                this.finish();
                ret = false;
            }

            return ret;
        }
    }

    public int getCurrDelta(int index, int secondIndex) {
        if(this.mFinished) {
            return 0;
        } else {
            ItemFlipScroller.FlipItem item = (ItemFlipScroller.FlipItem)this.mFlipItemMap.get(this.getFlipItemMapKey(index, secondIndex));
            return item != null?item.mCurrDelta:0;
        }
    }

    public void addGridView(int start, int end, boolean isDown) {
        if(start <= end) {
            if(this.mStartListIndex >= 0 && this.mEndListIndex >= 0) {
                int headerSize;
                if(start < this.mStartListIndex) {
                    this.addBefore(start);
                } else if(start > this.mStartListIndex && this.mFastFlip) {
                    headerSize = start - this.mStartListIndex;
                    boolean adapter = false;
                    if((this.mStartListIndex < this.mHeaderViewList.size() || headerSize % this.mColumnCount == 0) && !adapter) {
                        if(this.DEBUG) {
                            Log.i("ItemFlipScroller", "clear start=" + start + " mEndListIndex=" + this.mEndListIndex);
                        }

                        this.mFlipItemColumnList.clear();
                        this.makeInitFlipItemList(start, this.mEndListIndex, this.mIsDown);
                        this.mStartListIndex = start;
                    }
                }

                if(end > this.mEndListIndex) {
                    this.addAfter(end);
                } else if(end < this.mEndListIndex && this.mFastFlip) {
                    headerSize = this.mHeaderViewList.size();
                    if(end >= headerSize) {
                        boolean lockClear = false;
                        if((this.mEndListIndex >= this.mTotalItemCount - this.mFooterViewList.size() || (end - headerSize + 1) % this.mColumnCount == 0) && !lockClear) {
                            if(this.DEBUG) {
                                Log.i("ItemFlipScroller", "clear mStartListIndex=" + this.mStartListIndex + " end=" + end + " mEndListIndex=" + this.mEndListIndex + " mTotalItemCount=" + this.mTotalItemCount);
                            }

                            this.mFlipItemColumnList.clear();
                            this.makeInitFlipItemList(this.mStartListIndex, end, this.mIsDown);
                            this.mEndListIndex = end;
                        }
                    }
                }
            } else {
                this.makeInitFlipItemList(start, end, isDown);
                this.mStartListIndex = start;
                this.mEndListIndex = end;
            }

        }
    }

    public void checkAddView(int start, int end) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "checkAddView start=" + start + " end=" + end + " mFinished=" + this.mFinished + " mIsDown=" + this.mIsDown);
        }

        if(!this.mFinished) {
            if(start < this.mStartListIndex && !this.mIsDown) {
                this.addBefore(start);
            }

            if(end > this.mEndListIndex && this.mIsDown) {
                this.addAfter(end);
            }
        }

    }

    private int getCurrDistance(int distance, float input, boolean needInterpolation) {
        float output;
        if(needInterpolation) {
            output = this.getInterpolation(input);
        } else {
            output = input;
        }

        return (int)((float)distance * output);
    }

    private float getInterpolation(float input) {
        return this.mAccelerateDecelerateFrameInterpolator.getInterpolation(input);
    }

    private boolean computerFlipScrollUp() {
        ++this.mCurrFrameIndex;
        return this.mFastFlip?this.computerUpFast():this.computerUpNormal();
    }

    private boolean computerUpNormal() {
        int horCount = 0;
        boolean verCount = false;
        boolean finished = true;
        int listCount = this.mFlipItemColumnList.size();

        for(int i = listCount - 1; i >= 0; --i) {
            List itemList = (List)this.mFlipItemColumnList.get(i);
            if(itemList == null) {
                return finished;
            }

            int var14 = 0;
            int itemCount = itemList.size();

            for(int j = itemCount - 1; j >= 0; --j) {
                ItemFlipScroller.FlipItem item = (ItemFlipScroller.FlipItem)itemList.get(j);
                int delayFrameCount = horCount * this.hor_delay_frame_count + var14 * this.ver_delay_frame_count;
                int itemCurrFrameCount = this.mCurrFrameIndex;
                if(this.mItemDelayAnim) {
                    itemCurrFrameCount -= delayFrameCount;
                }

                if(itemCurrFrameCount > 0) {
                    if(itemCurrFrameCount >= item.mFinalFrameCount) {
                        if(item.mLastDistance > 0 && item.mLastDistance < item.mFinalDistance) {
                            finished = false;
                        } else if(item.mLastDistance < 0 && item.mLastDistance > item.mFinalDistance) {
                            finished = false;
                        }

                        item.mCurrDelta = item.mFinalDistance - item.mLastDistance;
                        item.mLastDistance += item.mCurrDelta;
                        item.mCurrFrameCount = item.mFinalFrameCount;
                        item.mTotalMoveDistance += item.mCurrDelta;
                    } else {
                        float ratio = (float)itemCurrFrameCount / (float)item.mFinalFrameCount;
                        int currDistance = this.getCurrDistance(item.mFinalDistance, ratio, true);
                        item.mCurrDelta = currDistance - item.mLastDistance;
                        if(item.mCurrDelta < 0) {
                            item.mCurrDelta = 0;
                        }

                        item.mLastDistance += item.mCurrDelta;
                        item.mCurrFrameCount = itemCurrFrameCount;
                        item.mTotalMoveDistance += item.mCurrDelta;
                        finished = false;
                    }

                    ++var14;
                    if(this.mStartingFlipScroll) {
                        item.mFastScrollStartingOffset += item.mCurrDelta;
                    }
                }
            }

            ++horCount;
        }

        return finished;
    }

    private boolean computerUpFast() {
        boolean finished = true;
        boolean needDelay;
        if(this.mItemDelayAnim) {
            needDelay = true;
        } else {
            needDelay = false;
        }

        int preColumnDistance = 0;
        int preColumnDelta = 0;
        boolean preVerticalDistance = false;
        boolean preVerticalDelta = false;
        ItemFlipScroller.FlipItemFastStatus preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
        ItemFlipScroller.FlipItemFastStatus preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
        int listSize = this.mFlipItemColumnList.size();
        int columnIndex = listSize - 1;

        while(true) {
            List itemList;
            ItemFlipScroller.FlipItem columnLastItem;
            int verticalIndex;
            int itemFinished;
            ItemFlipScroller.FlipItem itemFinished1;
            boolean var21;
            label159: {
                if(columnIndex >= 0) {
                    itemList = (List)this.mFlipItemColumnList.get(columnIndex);
                    if(itemList == null || itemList.size() <= 0) {
                        return finished;
                    }

                    columnLastItem = (ItemFlipScroller.FlipItem)itemList.get(itemList.size() - 1);
                    columnLastItem.mCurrDelta = 0;
                    if(columnIndex == listSize - 1) {
                        if(columnLastItem.mLastDistance == 0) {
                            finished = this.computerUpFastEachItem(columnLastItem, 0, true);
                        } else if(columnLastItem.mLastDistance >= columnLastItem.mFinalDistance) {
                            columnLastItem.mCurrDelta = 0;
                        } else {
                            finished = this.computerUpFastEachItem(columnLastItem, 0, true);
                        }

                        if(columnLastItem.mFinalDistance <= this.hor_delay_distance * 2 || columnLastItem.mFinalDistance <= this.ver_delay_distance * 2) {
                            needDelay = false;
                        }
                        break label159;
                    }

                    if(preColumnItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.STOP) == 0) {
                        boolean var20 = this.computerUpFastEachItem(columnLastItem, 0, true);
                        if(!var20) {
                            finished = false;
                        }
                        break label159;
                    }

                    if(preColumnItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.START_UNSTOP) != 0) {
                        verticalIndex = preColumnDistance - columnLastItem.mTotalMoveDistance;
                        if(needDelay && verticalIndex > this.ver_delay_distance) {
                            boolean var22 = this.computerUpFastEachItem(columnLastItem, 0, true);
                            if(!var22) {
                                finished = false;
                            }
                        }
                        break label159;
                    }

                    verticalIndex = preColumnDistance - columnLastItem.mTotalMoveDistance;
                    int item;
                    if(!needDelay || verticalIndex > this.hor_delay_distance) {
                        item = verticalIndex - this.hor_delay_distance;
                        if(!needDelay) {
                            item = preColumnDelta;
                        }

                        var21 = this.computerUpFastEachItem(columnLastItem, item, false);
                        if(!var21) {
                            finished = false;
                        }
                        break label159;
                    }

                    for(item = columnIndex; item >= 0; --item) {
                        List diff = (List)this.mFlipItemColumnList.get(item);

                        for(itemFinished = diff.size() - 1; itemFinished >= 0; --itemFinished) {
                            itemFinished1 = (ItemFlipScroller.FlipItem)diff.get(itemFinished);
                            itemFinished1.mCurrDelta = 0;
                        }
                    }
                }

                return finished;
            }

            preColumnDelta = columnLastItem.mCurrDelta;
            int var19 = columnLastItem.mCurrDelta;
            preColumnDistance = columnLastItem.mTotalMoveDistance;
            int var18 = columnLastItem.mTotalMoveDistance;
            if(this.mStartingFlipScroll) {
                columnLastItem.mFastScrollStartingOffset += columnLastItem.mCurrDelta;
            }

            if(columnLastItem.mLastDistance >= columnLastItem.mFinalDistance) {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
            } else if(columnLastItem.mCurrTotalMoveDistance != 0) {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
            } else {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
            }

            label133:
            for(verticalIndex = itemList.size() - 2; verticalIndex >= 0; --verticalIndex) {
                ItemFlipScroller.FlipItem var23 = (ItemFlipScroller.FlipItem)itemList.get(verticalIndex);
                var23.mCurrDelta = 0;
                if(preVertiaclItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.STOP) == 0) {
                    var21 = this.computerUpFastEachItem(var23, 0, true);
                    if(!var21) {
                        finished = false;
                    }
                } else {
                    int var24;
                    if(preVertiaclItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.START_UNSTOP) == 0) {
                        var24 = var18 - var23.mTotalMoveDistance;
                        if(needDelay && var24 <= this.ver_delay_distance) {
                            itemFinished = verticalIndex;

                            while(true) {
                                if(itemFinished < 0) {
                                    break label133;
                                }

                                itemFinished1 = (ItemFlipScroller.FlipItem)itemList.get(itemFinished);
                                itemFinished1.mCurrDelta = 0;
                                --itemFinished;
                            }
                        }

                        itemFinished = var24 - this.ver_delay_distance;
                        if(!needDelay) {
                            itemFinished = var19;
                        }

                        boolean var25 = this.computerUpFastEachItem(var23, itemFinished, false);
                        if(!var25) {
                            finished = false;
                        }
                    } else {
                        var24 = var18 - var23.mTotalMoveDistance;
                        if(needDelay && var24 > this.ver_delay_distance) {
                            boolean var26 = this.computerUpFastEachItem(var23, 0, true);
                            if(!var26) {
                                finished = false;
                            }
                        }
                    }
                }

                var19 = var23.mCurrDelta;
                var18 = var23.mTotalMoveDistance;
                if(this.mStartingFlipScroll) {
                    var23.mFastScrollStartingOffset += var23.mCurrDelta;
                }

                if(var23.mLastDistance >= var23.mFinalDistance) {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
                } else if(var23.mCurrTotalMoveDistance != 0) {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
                } else {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
                }
            }

            --columnIndex;
        }
    }

    private boolean computerUpFastEachItem(ItemFlipScroller.FlipItem item, int moveDistance, boolean preItemStoped) {
        boolean finished = true;
        int currDistance;
        if(!preItemStoped) {
            item.mCurrDelta = moveDistance;
            ++item.mCurrFrameCount;
            if(item.mCurrFrameCount > item.mFinalFrameCount) {
                item.mCurrFrameCount = item.mFinalFrameCount;
            }

            currDistance = item.mFinalDistance - item.mLastDistance;
            if(currDistance < item.mCurrDelta) {
                item.mCurrDelta = currDistance;
            }

            item.mCurrTotalMoveDistance += item.mCurrDelta;
            item.mLastDistance += item.mCurrDelta;
            item.mTotalMoveDistance += item.mCurrDelta;
            finished = false;
        } else if(item.mLastDistance < item.mFinalDistance) {
            currDistance = item.mLastDistance + this.mFastStep.getCurrStep();
            if(currDistance > item.mFinalDistance) {
                currDistance = item.mFinalDistance;
            }

            item.mCurrDelta = currDistance - item.mLastDistance;
            item.mCurrTotalMoveDistance += item.mCurrDelta;
            item.mLastDistance += item.mCurrDelta;
            item.mTotalMoveDistance += item.mCurrDelta;
            ++item.mCurrFrameCount;
            if(item.mCurrFrameCount > item.mFinalFrameCount) {
                item.mCurrFrameCount = item.mFinalFrameCount;
            }

            finished = false;
        } else {
            item.mCurrDelta = 0;
            item.mCurrFrameCount = item.mFinalFrameCount;
            item.mLastDistance = item.mFinalDistance;
        }

        return finished;
    }

    private boolean computerFlipScrollDown() {
        ++this.mCurrFrameIndex;
        return this.mFastFlip?this.computerDownFast():this.computerDownNormal();
    }

    private boolean computerDownNormal() {
        boolean verCount = false;
        int horCount = 0;
        boolean finished = true;

        label59:
        for(Iterator var5 = this.mFlipItemColumnList.iterator(); var5.hasNext(); ++horCount) {
            List itemList = (List)var5.next();
            if(itemList == null) {
                return finished;
            }

            int var12 = 0;
            Iterator var7 = itemList.iterator();

            while(true) {
                ItemFlipScroller.FlipItem item;
                int itemCurrFrameCount;
                do {
                    if(!var7.hasNext()) {
                        continue label59;
                    }

                    item = (ItemFlipScroller.FlipItem)var7.next();
                    int delayFrameCount = horCount * this.hor_delay_frame_count + var12 * this.ver_delay_frame_count;
                    itemCurrFrameCount = this.mCurrFrameIndex;
                    if(this.mItemDelayAnim) {
                        itemCurrFrameCount -= delayFrameCount;
                    }
                } while(itemCurrFrameCount <= 0);

                if(itemCurrFrameCount < item.mFinalFrameCount) {
                    float ratio = (float)itemCurrFrameCount / (float)item.mFinalFrameCount;
                    int currDistance = this.getCurrDistance(item.mFinalDistance, ratio, true);
                    item.mCurrDelta = currDistance - item.mLastDistance;
                    if(item.mCurrDelta > 0) {
                        item.mCurrDelta = 0;
                    }

                    item.mLastDistance += item.mCurrDelta;
                    item.mCurrFrameCount = itemCurrFrameCount;
                    item.mTotalMoveDistance += item.mCurrDelta;
                    finished = false;
                } else {
                    if(item.mLastDistance > 0 && item.mLastDistance < item.mFinalDistance) {
                        finished = false;
                    } else if(item.mLastDistance < 0 && item.mLastDistance > item.mFinalDistance) {
                        finished = false;
                    }

                    item.mCurrDelta = item.mFinalDistance - item.mLastDistance;
                    item.mLastDistance += item.mCurrDelta;
                    item.mCurrFrameCount = item.mFinalFrameCount;
                    item.mTotalMoveDistance += item.mCurrDelta;
                }

                ++var12;
                if(this.mStartingFlipScroll) {
                    item.mFastScrollStartingOffset += item.mCurrDelta;
                }
            }
        }

        return finished;
    }

    private boolean computerDownFast() {
        boolean finished = true;
        boolean needDelay;
        if(this.mItemDelayAnim) {
            needDelay = true;
        } else {
            needDelay = false;
        }

        int preColumnDistance = 0;
        int preColumnDelta = 0;
        boolean preVerticalDistance = false;
        boolean preVerticalDelta = false;
        ItemFlipScroller.FlipItemFastStatus preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
        ItemFlipScroller.FlipItemFastStatus preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
        int columnIndex = 0;

        while(true) {
            List itemList;
            ItemFlipScroller.FlipItem columnFirstItem;
            int verticalIndex;
            int itemFinished;
            ItemFlipScroller.FlipItem itemFinished1;
            boolean var20;
            label159: {
                if(columnIndex < this.mFlipItemColumnList.size()) {
                    itemList = (List)this.mFlipItemColumnList.get(columnIndex);
                    if(itemList == null || itemList.size() <= 0) {
                        return finished;
                    }

                    columnFirstItem = (ItemFlipScroller.FlipItem)itemList.get(0);
                    columnFirstItem.mCurrDelta = 0;
                    if(columnIndex == 0) {
                        if(columnFirstItem.mLastDistance == 0) {
                            finished = this.computerDownFastEachItem(columnFirstItem, 0, true);
                        } else if(columnFirstItem.mLastDistance <= columnFirstItem.mFinalDistance) {
                            columnFirstItem.mCurrDelta = 0;
                        } else {
                            finished = this.computerDownFastEachItem(columnFirstItem, 0, true);
                        }

                        if(columnFirstItem.mFinalDistance >= -(this.hor_delay_distance * 2) || columnFirstItem.mFinalDistance >= -(this.ver_delay_distance * 2)) {
                            needDelay = false;
                        }
                        break label159;
                    }

                    if(preColumnItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.STOP) == 0) {
                        boolean var19 = this.computerDownFastEachItem(columnFirstItem, 0, true);
                        if(!var19) {
                            finished = false;
                        }
                        break label159;
                    }

                    if(preColumnItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.START_UNSTOP) != 0) {
                        verticalIndex = columnFirstItem.mTotalMoveDistance - preColumnDistance;
                        if(needDelay && verticalIndex > this.ver_delay_distance) {
                            boolean var21 = this.computerDownFastEachItem(columnFirstItem, 0, true);
                            if(!var21) {
                                finished = false;
                            }
                        }
                        break label159;
                    }

                    verticalIndex = columnFirstItem.mTotalMoveDistance - preColumnDistance;
                    int item;
                    if(!needDelay || verticalIndex > this.hor_delay_distance) {
                        item = this.hor_delay_distance - verticalIndex;
                        if(!needDelay) {
                            item = preColumnDelta;
                        }

                        var20 = this.computerDownFastEachItem(columnFirstItem, item, false);
                        if(!var20) {
                            finished = false;
                        }
                        break label159;
                    }

                    for(item = columnIndex; item < this.mFlipItemColumnList.size(); ++item) {
                        List diff = (List)this.mFlipItemColumnList.get(item);

                        for(itemFinished = 0; itemFinished < diff.size(); ++itemFinished) {
                            itemFinished1 = (ItemFlipScroller.FlipItem)diff.get(itemFinished);
                            itemFinished1.mCurrDelta = 0;
                        }
                    }
                }

                return finished;
            }

            preColumnDelta = columnFirstItem.mCurrDelta;
            int var18 = columnFirstItem.mCurrDelta;
            preColumnDistance = columnFirstItem.mTotalMoveDistance;
            int var17 = columnFirstItem.mTotalMoveDistance;
            if(this.mStartingFlipScroll) {
                columnFirstItem.mFastScrollStartingOffset += columnFirstItem.mCurrDelta;
            }

            if(columnFirstItem.mLastDistance <= columnFirstItem.mFinalDistance) {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
            } else if(columnFirstItem.mCurrTotalMoveDistance != 0) {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
            } else {
                preColumnItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
                preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
            }

            label133:
            for(verticalIndex = 1; verticalIndex < itemList.size(); ++verticalIndex) {
                ItemFlipScroller.FlipItem var22 = (ItemFlipScroller.FlipItem)itemList.get(verticalIndex);
                var22.mCurrDelta = 0;
                if(preVertiaclItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.STOP) == 0) {
                    var20 = this.computerDownFastEachItem(var22, 0, true);
                    if(!var20) {
                        finished = false;
                    }
                } else {
                    int var23;
                    if(preVertiaclItemStatus.compareTo(ItemFlipScroller.FlipItemFastStatus.START_UNSTOP) == 0) {
                        var23 = var22.mTotalMoveDistance - var17;
                        if(needDelay && var23 <= this.ver_delay_distance) {
                            itemFinished = verticalIndex;

                            while(true) {
                                if(itemFinished >= itemList.size()) {
                                    break label133;
                                }

                                itemFinished1 = (ItemFlipScroller.FlipItem)itemList.get(itemFinished);
                                itemFinished1.mCurrDelta = 0;
                                ++itemFinished;
                            }
                        }

                        itemFinished = this.ver_delay_distance - var23;
                        if(!needDelay) {
                            itemFinished = var18;
                        }

                        boolean var24 = this.computerDownFastEachItem(var22, itemFinished, false);
                        if(!var24) {
                            finished = false;
                        }
                    } else {
                        var23 = var22.mTotalMoveDistance - var17;
                        if(needDelay && var23 > this.ver_delay_distance) {
                            boolean var25 = this.computerDownFastEachItem(var22, 0, true);
                            if(!var25) {
                                finished = false;
                            }
                        }
                    }
                }

                var18 = var22.mCurrDelta;
                var17 = var22.mTotalMoveDistance;
                if(this.mStartingFlipScroll) {
                    var22.mFastScrollStartingOffset += var22.mCurrDelta;
                }

                if(var22.mLastDistance <= var22.mFinalDistance) {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.STOP;
                } else if(var22.mCurrTotalMoveDistance != 0) {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.START_UNSTOP;
                } else {
                    preVertiaclItemStatus = ItemFlipScroller.FlipItemFastStatus.UNSTART;
                }
            }

            ++columnIndex;
        }
    }

    private boolean computerDownFastEachItem(ItemFlipScroller.FlipItem item, int moveDistance, boolean preItemStoped) {
        boolean finished = true;
        int currDistance;
        if(!preItemStoped) {
            item.mCurrDelta = moveDistance;
            ++item.mCurrFrameCount;
            if(item.mCurrFrameCount > item.mFinalFrameCount) {
                item.mCurrFrameCount = item.mFinalFrameCount;
            }

            currDistance = item.mFinalDistance - item.mLastDistance;
            if(currDistance > item.mCurrDelta) {
                item.mCurrDelta = currDistance;
            }

            item.mCurrTotalMoveDistance += item.mCurrDelta;
            item.mLastDistance += item.mCurrDelta;
            item.mTotalMoveDistance += item.mCurrDelta;
            finished = false;
        } else if(item.mLastDistance > item.mFinalDistance) {
            currDistance = item.mLastDistance + this.mFastStep.getCurrStep();
            if(currDistance < item.mFinalDistance) {
                currDistance = item.mFinalDistance;
            }

            item.mCurrDelta = currDistance - item.mLastDistance;
            item.mCurrTotalMoveDistance += item.mCurrDelta;
            item.mLastDistance += item.mCurrDelta;
            item.mTotalMoveDistance += item.mCurrDelta;
            ++item.mCurrFrameCount;
            if(item.mCurrFrameCount > item.mFinalFrameCount) {
                item.mCurrFrameCount = item.mFinalFrameCount;
            }

            finished = false;
        } else {
            item.mCurrDelta = 0;
            item.mCurrFrameCount = item.mFinalFrameCount;
            item.mLastDistance = item.mFinalDistance;
        }

        return finished;
    }

    private void makeFlipItemList(ItemFlipScroller.FlipListChangeType type, int start, int end, Object obj) {
        int headerCount = this.mHeaderViewList != null ? this.mHeaderViewList.size() : 0;
        int footerCount = this.mFooterViewList != null ? this.mFooterViewList.size() : 0;
        int currAdapterColumn = -1;

        int firstIndex = -1;
        int lastIndex = -1;
        if (this.mFlipItemColumnList.size() > 0)
        {
            List<FlipItem> firstList = (List)this.mFlipItemColumnList.get(0);
            if (firstList != null)
            {
                FlipItem first = (FlipItem)firstList.get(0);
                if (first != null) {
                    firstIndex = first.mIndex;
                }
            }
            List<FlipItem> lastList = (List)this.mFlipItemColumnList.get(this.mFlipItemColumnList.size() - 1);
            if (lastList != null)
            {
                FlipItem last = (FlipItem)lastList.get(lastList.size() - 1);
                if (last != null) {
                    lastIndex = last.mIndex;
                }
            }
        }
        if (this.DEBUG) {
            Log.i("ItemFlipScroller", "makeFlipItemList firstIndex=" + firstIndex + " lastIndex=" + lastIndex +
                    " start=" + start + " end=" + end + " type=" + type);
        }
        for (int i = start; i <= end; i++) {
            if ((i >= firstIndex) && (i <= lastIndex))
            {
                if ((headerCount > 0) && (i < headerCount))
                {
                    int currHeader = ((Integer)this.mHeaderViewList.get(i)).intValue();
                    int headerColumnCount = currHeader >> 16;
                    int headerVerticalCount = currHeader & 0xFF;
                    for (int c = 0; c < headerColumnCount; c++) {
                        for (int v = 0; v < headerVerticalCount; v++)
                        {
                            int headIndex = c * headerVerticalCount + v;
                            refreshFlipItem(i, headIndex, obj, type);
                        }
                    }
                }
                else if ((footerCount > 0) && (i >= this.mTotalItemCount - footerCount))
                {
                    int currFooter = ((Integer)this.mFooterViewList.get(i)).intValue();
                    int footerColumnCount = currFooter >> 16;
                    int footerVerticalCount = currFooter & 0xFF;
                    for (int c = 0; c < footerColumnCount; c++)
                    {
                        List<FlipItem> columnFlipItemList = new ArrayList();
                        this.mFlipItemColumnList.add(columnFlipItemList);
                        for (int v = 0; v < footerVerticalCount; v++)
                        {
                            int footerIndex = c * footerVerticalCount + v;
                            refreshFlipItem(i, footerIndex, obj, type);
                        }
                    }
                }
                else
                {
                    refreshFlipItem(i, 0, obj, type);
                }
            }
            else if ((headerCount > 0) && (i < headerCount))
            {
                int currHeader = ((Integer)this.mHeaderViewList.get(i)).intValue();
                int headerColumnCount = currHeader >> 16;
                int headerVerticalCount = currHeader & 0xFF;
                int columnIndex = this.mFlipItemColumnList.size();
                for (int c = 0; c < headerColumnCount; c++)
                {
                    List<FlipItem> columnFlipItemList = new ArrayList();
                    this.mFlipItemColumnList.add(columnFlipItemList);
                    for (int v = 0; v < headerVerticalCount; v++)
                    {
                        int headIndex = c * headerVerticalCount + v;
                        FlipItem item = getMakedFlipItem(type, FlipItemPositionType.HEATER,
                                i, headIndex, c + columnIndex, v, obj);
                        if (item != null) {
                            columnFlipItemList.add(item);
                        }
                    }
                }
            }
            else if ((footerCount > 0) && (i >= this.mTotalItemCount - footerCount))
            {
                int currFooter = ((Integer)this.mFooterViewList.get(i)).intValue();
                int footerColumnCount = currFooter >> 16;
                int footerVerticalCount = currFooter & 0xFF;
                int columnIndex = this.mFlipItemColumnList.size();
                for (int c = 0; c < footerColumnCount; c++)
                {
                    List<FlipItem> columnFlipItemList = new ArrayList();
                    this.mFlipItemColumnList.add(columnFlipItemList);
                    for (int v = 0; v < footerVerticalCount; v++)
                    {
                        int footerIndex = c * footerVerticalCount + v;
                        FlipItem item = getMakedFlipItem(type, FlipItemPositionType.FOOTER,
                                i, footerIndex, c + columnIndex, v, obj);
                        if (item != null) {
                            columnFlipItemList.add(item);
                        }
                    }
                }
            }
            else
            {
                int adatperStart = start;
                if (type == FlipListChangeType.ADD_AFTER) {
                    adatperStart = this.mStartListIndex;
                }
                int inScreenHeaderCount = headerCount - adatperStart;
                if (inScreenHeaderCount <= 0) {
                    inScreenHeaderCount = 0;
                }
                int adapterColumnIndex = (i - adatperStart - inScreenHeaderCount) / this.mColumnCount;
                int adapterVerticalIntex = (i - adatperStart - inScreenHeaderCount) % this.mColumnCount;
                int columnIndex;
                List<FlipItem> columnFlipItemList;
                if (adapterColumnIndex != currAdapterColumn)
                {
                    currAdapterColumn = adapterColumnIndex;

                   columnFlipItemList = new ArrayList();
                    this.mFlipItemColumnList.add(columnFlipItemList);
                    columnIndex = this.mFlipItemColumnList.size() - 1;
                }
                else
                {
                    columnIndex = this.mFlipItemColumnList.size() - 1;
                    columnFlipItemList = (List)this.mFlipItemColumnList.get(columnIndex);
                }
                FlipItem item = getMakedFlipItem(type, FlipItemPositionType.ADAPTER,
                        i, 0, columnIndex, adapterVerticalIntex, obj);
                if (item != null) {
                    columnFlipItemList.add(item);
                }
            }
        }

    }

    private ItemFlipScroller.FlipItem getMakedFlipItem(ItemFlipScroller.FlipListChangeType changeType, ItemFlipScroller.FlipItemPositionType posType, int index, int secondIndex, int columnIndex, int verticalIndex, Object object) {
        ItemFlipScroller.FlipItem item = null;
        switch(changeType) {
            case INIT:
            case ADD_BEFORE:
                item = this.getFlipItem(index, secondIndex);
                if(item == null) {
                    item = new ItemFlipScroller.FlipItem(index, 0);
                    this.mFlipItemMap.put(this.getFlipItemMapKey(index, secondIndex), item);
                }

                item.mColumnIndex = columnIndex;
                item.mVerticalIndex = verticalIndex;
                break;
            case ADD_AFTER:
                if(object != null && object instanceof ItemFlipScroller.FlipItem) {
                    item = this.addAfterItem((ItemFlipScroller.FlipItem)object, index, secondIndex);
                    if(item == null) {
                        item = new ItemFlipScroller.FlipItem(index, 0);
                        this.mFlipItemMap.put(this.getFlipItemMapKey(index, secondIndex), item);
                    }

                    item.mColumnIndex = columnIndex;
                    item.mVerticalIndex = verticalIndex;
                }
                break;
            case TEMP:
                item = this.getFlipItem(index, secondIndex);
                if(item == null) {
                    item = new ItemFlipScroller.FlipItem(index, 0);
                    this.mFlipItemMap.put(this.getFlipItemMapKey(index, secondIndex), item);
                }

                item.mColumnIndex = columnIndex;
                item.mVerticalIndex = verticalIndex;
                ItemFlipScroller.FlipItem preItem;
                if(this.mIsDown) {
                    preItem = this.getFlipItemColumn(columnIndex - 1, 0);
                } else {
                    preItem = this.getFirstColumnLasterItem();
                }

                if(preItem != null) {
                    int left = preItem.mFinalDistance - preItem.mLastDistance;
                    int leftCount = preItem.mFinalFrameCount - preItem.mCurrFrameCount;
                    item.mFinalFrameCount = preItem.mFinalFrameCount;
                    item.mLastDistance = preItem.mLastDistance;
                    item.mFinalDistance = preItem.mFinalDistance;
                    item.mTotalMoveDistance = preItem.mTotalMoveDistance;
                    if(this.DEBUG) {
                        Log.i("ItemFlipScroller", "getMakedFlipItem unknown left=" + left + " index=" + preItem.mIndex + " mFinalDistance=" + preItem.mFinalDistance + " mLastDistance=" + preItem.mLastDistance + " leftCount=" + leftCount + " mFinalFrameCount=" + preItem.mFinalFrameCount + " mCurrFrameCount=" + preItem.mCurrFrameCount + " mTotalMoveDistance=" + preItem.mTotalMoveDistance);
                    }
                }
        }

        return item;

    }

    private void refreshFlipItem(int index, int secondIndex, Object obj, ItemFlipScroller.FlipListChangeType type) {
        if(type.compareTo(ItemFlipScroller.FlipListChangeType.ADD_AFTER) == 0) {
            this.addAfterItem((ItemFlipScroller.FlipItem)obj, index, 0);
        } else if(type.compareTo(ItemFlipScroller.FlipListChangeType.ADD_BEFORE) == 0) {
            this.addBeforeItem((ItemFlipScroller.FlipItem)obj, index, 0);
        } else if(type.compareTo(ItemFlipScroller.FlipListChangeType.TEMP) == 0 && !this.mIsDown) {
            ItemFlipScroller.FlipItem item = this.getFlipItem(index, secondIndex);
            if(item != null) {
                ItemFlipScroller.FlipItem preItem = null;

                int left;
                for(left = 0; left < this.mFlipItemColumnList.size(); ++left) {
                    List leftCount = (List)this.mFlipItemColumnList.get(left);
                    if(leftCount != null && leftCount.size() > 0) {
                        ItemFlipScroller.FlipItem first = (ItemFlipScroller.FlipItem)leftCount.get(0);
                        if(first.mIndex >= ((Integer)obj).intValue()) {
                            preItem = (ItemFlipScroller.FlipItem)leftCount.get(leftCount.size() - 1);
                            break;
                        }
                    }
                }

                if(preItem != null) {
                    left = preItem.mFinalDistance - preItem.mLastDistance;
                    int var10 = preItem.mFinalFrameCount - preItem.mCurrFrameCount;
                    item.mFinalFrameCount = preItem.mFinalFrameCount;
                    item.mLastDistance = preItem.mLastDistance;
                    item.mFinalDistance = preItem.mFinalDistance;
                    item.mTotalMoveDistance = preItem.mTotalMoveDistance;
                    if(this.DEBUG) {
                        Log.i("ItemFlipScroller", "getMakedFlipItem unknown left=" + left + " index=" + preItem.mIndex + " mFinalDistance=" + preItem.mFinalDistance + " mLastDistance=" + preItem.mLastDistance + " leftCount=" + var10 + " mFinalFrameCount=" + preItem.mFinalFrameCount + " mCurrFrameCount=" + preItem.mCurrFrameCount + " mTotalMoveDistance=" + preItem.mTotalMoveDistance);
                    }
                }
            }
        }

    }

    private void makeInitFlipItemList(int start, int end, boolean isDown) {
        if(isDown) {
            this.makeFlipItemList(ItemFlipScroller.FlipListChangeType.INIT, start, end + this.mColumnCount, (Object)null);
            this.mPreAddTempItemIndex = start;
        } else {
            int tempStart;
            if(start <= this.mHeaderViewList.size()) {
                tempStart = start - 1;
            } else {
                tempStart = start - this.mColumnCount;
            }

            if(tempStart < 0) {
                tempStart = 0;
            }

            this.makeFlipItemList(ItemFlipScroller.FlipListChangeType.INIT, tempStart, end, (Object)null);
            this.mPreAddTempItemIndex = tempStart;
        }

    }

    private void makeBeforeFlipItemList(int start, int end) {
        int tempStart;
        if(start <= this.mHeaderViewList.size()) {
            tempStart = start - 1;
        } else {
            tempStart = start - this.mColumnCount;
            if(tempStart < this.mHeaderViewList.size()) {
                tempStart = this.mHeaderViewList.size() - 1;
            }
        }

        if(tempStart < 0) {
            tempStart = 0;
        }

        this.makeFlipItemList(ItemFlipScroller.FlipListChangeType.ADD_BEFORE, tempStart, end, (Object)null);
        int tempEnd = Math.max(this.mPreAddTempItemIndex, start - 1);
        if(tempStart <= tempEnd && tempStart < this.mPreAddTempItemIndex) {
            this.makeFlipItemList(ItemFlipScroller.FlipListChangeType.TEMP, tempStart, tempEnd, Integer.valueOf(this.mPreAddTempItemIndex));
        }

        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "makeBeforeFlipItemList start=" + start + " end=" + end + " tempStart=" + tempStart + " tempEnd=" + tempEnd + " mPreAddTempItemIndex=" + this.mPreAddTempItemIndex);
        }

        this.mPreAddTempItemIndex = tempStart;
    }

    private void makeAfterFlipItemList(int start, int end, ItemFlipScroller.FlipItem lastHeaderItem) {
        this.makeFlipItemList(ItemFlipScroller.FlipListChangeType.ADD_AFTER, start, end, lastHeaderItem);
        this.makeTempFlipItemList(start, end, true, ItemFlipScroller.FlipListChangeType.TEMP);
    }

    private void makeTempFlipItemList(int start, int end, boolean after, ItemFlipScroller.FlipListChangeType type) {
        if(!after) {
            int tempStart = start - this.mColumnCount;
            if(tempStart < 0) {
                tempStart = 0;
            }

            int tempEnd = start - 1;
            if(tempEnd < 0) {
                tempEnd = 0;
            }

            if(tempEnd <= this.mHeaderViewList.size() - 1) {
                this.makeFlipItemList(type, tempEnd, tempEnd, (Object)null);
            } else {
                this.makeFlipItemList(type, tempStart, tempEnd, (Object)null);
            }
        } else {
            this.makeFlipItemList(type, end + 1, end + this.mColumnCount, (Object)null);
        }

    }

    private ItemFlipScroller.FlipItem getFlipItem(int index, int secondIndex) {
        if(secondIndex == -1) {
            int currFooter;
            int footerColumnCount;
            int footerVerticalCount;
            if(this.mHeaderViewList.size() > 0 && index < this.mHeaderViewList.size()) {
                currFooter = ((Integer)this.mHeaderViewList.get(index)).intValue();
                footerColumnCount = currFooter >> 16;
                footerVerticalCount = currFooter & 255;
                secondIndex = footerColumnCount * footerVerticalCount - 1;
            } else if(this.mFooterViewList.size() > 0 && index >= this.mTotalItemCount - this.mFooterViewList.size()) {
                currFooter = ((Integer)this.mFooterViewList.get(index)).intValue();
                footerColumnCount = currFooter >> 16;
                footerVerticalCount = currFooter & 255;
                secondIndex = footerColumnCount * footerVerticalCount - 1;
            }
        }

        return (ItemFlipScroller.FlipItem)this.mFlipItemMap.get(this.getFlipItemMapKey(index, secondIndex));
    }

    private ItemFlipScroller.FlipItem getFlipItemColumn(int column, int vertical) {
        List itemList = (List)this.mFlipItemColumnList.get(column);
        return itemList != null && itemList.size() > 0?(ItemFlipScroller.FlipItem)itemList.get(vertical):null;
    }

    private ItemFlipScroller.FlipItem getCurrColumnLastItem(int column) {
        List itemList = (List)this.mFlipItemColumnList.get(column);
        return itemList != null && itemList.size() > 0?(ItemFlipScroller.FlipItem)itemList.get(itemList.size() - 1):null;
    }

    private ItemFlipScroller.FlipItem getFirstColumnLasterItem() {
        for(int i = 0; i < this.mFlipItemColumnList.size(); ++i) {
            List itemList = (List)this.mFlipItemColumnList.get(i);
            if(itemList != null && itemList.size() > 0) {
                ItemFlipScroller.FlipItem first = (ItemFlipScroller.FlipItem)itemList.get(0);
                if(first.mIndex >= this.mStartListIndex) {
                    return (ItemFlipScroller.FlipItem)itemList.get(itemList.size() - 1);
                }
            }
        }

        return null;
    }

    private ItemFlipScroller.FlipItem getLastColumnFirsterItem() {
        for(int i = 1; i <= this.mFlipItemColumnList.size(); ++i) {
            List itemList = (List)this.mFlipItemColumnList.get(this.mFlipItemColumnList.size() - i);
            if(itemList != null && itemList.size() > 0) {
                ItemFlipScroller.FlipItem lastItem = (ItemFlipScroller.FlipItem)itemList.get(itemList.size() - 1);
                if(lastItem.mIndex <= this.mEndListIndex) {
                    return (ItemFlipScroller.FlipItem)itemList.get(0);
                }
            }
        }

        return null;
    }

    private int getFlipItemMapKey(int index, int secondIndex) {
        return index << 8 | secondIndex;
    }

    private void resetItemData(int newFinalDistance) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "resetItemData newFinalDistance=" + newFinalDistance);
        }

        boolean first = true;
        Iterator iterator = this.mFlipItemColumnList.iterator();

        while(iterator.hasNext()) {

            List itemList = (List)iterator.next();
            Iterator iterator1 = itemList.iterator();

            while(iterator1.hasNext()) {

                ItemFlipScroller.FlipItem item = (ItemFlipScroller.FlipItem)iterator1.next();
                item.mCurrDelta = 0;
                item.mCurrTotalMoveDistance = 0;
                item.mFinalFrameCount += this.mFinalFrameCount;
                item.mFastScrollStartingOffset = 0;
                item.mFastScrollOffset = 0;
                item.mFinalDistance += newFinalDistance;
                if(first && this.mFastFlip) {
                    int step = (int)((float)(item.mFinalDistance - item.mLastDistance) / (float)this.flip_scroll_frame_count);
                    if(this.mIsDown) {
                        if(step > -this.min_fast_setp_discance) {
                            step = -this.min_fast_setp_discance;
                        }
                    } else if(step < this.min_fast_setp_discance) {
                        step = this.min_fast_setp_discance;
                    }

                    this.mFastStep.setStartStep(step);
                    first = false;
                }

                if(this.DEBUG) {
                    Log.i("ItemFlipScroller", "resetItemData index=" + item.mIndex + " newFinalDistance=" + newFinalDistance + " mTotalMoveDistance=" + item.mTotalMoveDistance + " mLastDistance=" + item.mLastDistance + " mFinalDistance=" + item.mFinalDistance + " mFinalFrameCount=" + item.mFinalFrameCount + " mFastStep=" + this.mFastStep);
                }
            }
        }

    }

    private int getRealFinalDistance(int newFinalDistance) {
        int realFinalDistance = newFinalDistance;
        int preSelectedIndex = this.mPreSelectedPosition;
        if(preSelectedIndex > this.mEndListIndex) {
            preSelectedIndex = this.mEndListIndex;
        } else if(preSelectedIndex < this.mStartListIndex) {
            preSelectedIndex = this.mStartListIndex;
        }

        ItemFlipScroller.FlipItem preSelectedItem = (ItemFlipScroller.FlipItem)this.mFlipItemMap.get(this.getFlipItemMapKey(preSelectedIndex, 0));
        if(preSelectedItem != null) {
            ItemFlipScroller.FlipItem columnLastItem = this.getCurrColumnLastItem(preSelectedItem.mColumnIndex);
            if(columnLastItem != null) {
                int selectedItemLeft = columnLastItem.mFinalDistance - columnLastItem.mLastDistance;
                realFinalDistance = newFinalDistance - columnLastItem.mFastScrollStartingOffset - selectedItemLeft;
                if(this.DEBUG) {
                    Log.i("ItemFlipScroller", "getRealFinalDistance columnFirstItem=" + columnLastItem.mIndex + " selectedItemLeft=" + selectedItemLeft + " mFastOffset=" + columnLastItem.mFastScrollStartingOffset + " realFinalDistance=" + realFinalDistance + " newFinalDistance=" + newFinalDistance + " mFinalDistance=" + columnLastItem.mFinalDistance + " mLastDistance=" + columnLastItem.mLastDistance + " preSelectedIndex=" + preSelectedIndex);
                }
            }
        } else if(!this.mFinished) {
            Log.e("ItemFlipScroller", "error getRealFinalDistance selectedItem == null position=" + preSelectedIndex);
            return newFinalDistance;
        }

        return realFinalDistance;
    }

    private void addBefore(int start) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "addGridView start < mStartListIndex start=" + start + " mStartListIndex=" + this.mStartListIndex);
        }

        ItemFlipScroller.FlipItem firstFooterItem = this.getFirstColumnLasterItem();
        if(firstFooterItem != null) {
            this.mFlipItemColumnList.clear();
            this.makeBeforeFlipItemList(start, this.mEndListIndex);
            int headerCount = this.mHeaderViewList != null?this.mHeaderViewList.size():0;
            int footerCount = this.mFooterViewList != null?this.mFooterViewList.size():0;

            for(int i = this.mStartListIndex - 1; i >= start; --i) {
                int currFooter;
                int footerColumnCount;
                int footerVerticalCount;
                int c;
                int v;
                int footerIndex;
                if(headerCount > 0 && i < headerCount) {
                    currFooter = ((Integer)this.mHeaderViewList.get(i)).intValue();
                    footerColumnCount = currFooter >> 16;
                    footerVerticalCount = currFooter & 255;

                    for(c = footerColumnCount - 1; c >= 0; --c) {
                        for(v = footerVerticalCount - 1; v >= 0; --v) {
                            footerIndex = c * footerVerticalCount + v;
                            this.addBeforeItem(firstFooterItem, i, footerIndex);
                        }
                    }
                } else if(footerCount > 0 && i >= this.mTotalItemCount - footerCount) {
                    currFooter = ((Integer)this.mFooterViewList.get(i)).intValue();
                    footerColumnCount = currFooter >> 16;
                    footerVerticalCount = currFooter & 255;

                    for(c = footerColumnCount - 1; c >= 0; --c) {
                        for(v = footerVerticalCount - 1; v >= 0; --v) {
                            footerIndex = c * footerVerticalCount + v;
                            this.addBeforeItem(firstFooterItem, i, footerIndex);
                        }
                    }
                } else {
                    this.addBeforeItem(firstFooterItem, i, 0);
                }
            }

            this.mStartListIndex = start;
        }
    }

    private ItemFlipScroller.FlipItem addBeforeItem(ItemFlipScroller.FlipItem firstFooterItem, int index, int secondIndex) {
        ItemFlipScroller.FlipItem item = null;
        if(firstFooterItem == null) {
            return item;
        } else {
            item = this.getFlipItem(index, secondIndex);
            if(item != null) {
                int currLastDistance = item.mLastDistance;
                if(currLastDistance < 0) {
                    currLastDistance = 0;
                }

                if(this.mItemFlipScrollerListener != null) {
                    int delta = currLastDistance - firstFooterItem.mLastDistance + firstFooterItem.mFastScrollStartingOffset + firstFooterItem.mFastScrollOffset - item.mFastScrollStartingOffset;
                    if(this.DEBUG) {
                        Log.i("ItemFlipScroller", "addGridView  start < mStartListIndex index=" + index + " currLastDistance=" + currLastDistance + " firstFooterItem index=" + firstFooterItem.mIndex + " mLastDistance=" + firstFooterItem.mLastDistance + " mFastScrollStartingOffset=" + firstFooterItem.mFastScrollStartingOffset + " mFastScrollOffset=" + firstFooterItem.mFastScrollOffset + " item.mFastScrollStartingOffset=" + item.mFastScrollStartingOffset + " delta=" + delta);
                    }

                    if(delta != 0) {
                        this.mItemFlipScrollerListener.onOffsetNewChild(index, secondIndex, delta);
                    }
                }
            }

            return item;
        }
    }

    private void addAfter(int end) {
        if(this.DEBUG) {
            Log.i("ItemFlipScroller", "addGridView end > mEndListIndex end=" + end + " mEndListIndex=" + this.mEndListIndex + " mFastFlip=" + this.mFastFlip);
        }

        ItemFlipScroller.FlipItem lastHeaderItem = this.getLastColumnFirsterItem();
        if(lastHeaderItem != null) {
            this.makeAfterFlipItemList(this.mEndListIndex + 1, end, lastHeaderItem);
            this.mEndListIndex = end;
        }
    }

    private ItemFlipScroller.FlipItem addAfterItem(ItemFlipScroller.FlipItem lastHeaderItem, int index, int secondIndex) {
        ItemFlipScroller.FlipItem item = null;
        if(lastHeaderItem == null) {
            return item;
        } else {
            item = this.getFlipItem(index, secondIndex);
            if(item != null) {
                int currLastDistance = item.mLastDistance;
                if(currLastDistance > 0) {
                    currLastDistance = 0;
                }

                if(this.mItemFlipScrollerListener != null) {
                    int delta = currLastDistance - lastHeaderItem.mLastDistance + lastHeaderItem.mFastScrollStartingOffset + lastHeaderItem.mFastScrollOffset - item.mFastScrollStartingOffset;
                    if(this.DEBUG) {
                        Log.i("ItemFlipScroller", "addGridView  end > mEndListIndex onOffsetNewChild index=" + index + " currLastDistance=" + currLastDistance + " mLastDistance=" + lastHeaderItem.mLastDistance + " last Index=" + lastHeaderItem.mIndex + " curr Index=" + item.mIndex + " curr startingOffset=" + item.mFastScrollStartingOffset + " mFastScrollStartingOffset=" + lastHeaderItem.mFastScrollStartingOffset + " mFastScrollOffset=" + lastHeaderItem.mFastScrollOffset + " delta=" + delta);
                    }

                    if(delta != 0) {
                        this.mItemFlipScrollerListener.onOffsetNewChild(index, secondIndex, delta);
                    }
                }
            }

            return item;
        }
    }

    private boolean itemIsFinished(int index) {
        int item;
        int left;
        int footerVerticalCount;
        int secondIndex;
        ItemFlipScroller.FlipItem footerLastItem;
        int left1;
        if(index < this.mHeaderViewList.size()) {
            item = ((Integer)this.mHeaderViewList.get(index)).intValue();
            left = item >> 16;
            footerVerticalCount = item & 255;
            secondIndex = left * footerVerticalCount - 1;
            footerLastItem = this.getFlipItem(index, secondIndex);
            if(footerLastItem != null) {
                left1 = footerLastItem.mFinalDistance - footerLastItem.mLastDistance;
                if(left1 != 0) {
                    return false;
                }
            }
        } else if(index >= this.mTotalItemCount - this.mFooterViewList.size()) {
            item = ((Integer)this.mFooterViewList.get(index)).intValue();
            left = item >> 16;
            footerVerticalCount = item & 255;
            secondIndex = left * footerVerticalCount - 1;
            footerLastItem = this.getFlipItem(index, secondIndex);
            if(footerLastItem != null) {
                left1 = footerLastItem.mFinalDistance - footerLastItem.mLastDistance;
                if(left1 != 0) {
                    return false;
                }
            }
        } else {
            ItemFlipScroller.FlipItem item1 = this.getFlipItem(index, 0);
            if(item1 != null) {
                left = item1.mFinalDistance - item1.mLastDistance;
                if(left != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    protected int getRowStart(int position) {
        int rowStart;
        if(position < this.mHeaderViewList.size()) {
            rowStart = position;
        } else if(position < this.mTotalItemCount - this.mFooterViewList.size()) {
            int newPosition = position - this.mHeaderViewList.size();
            rowStart = newPosition - newPosition % this.mColumnCount + this.mHeaderViewList.size();
        } else {
            rowStart = position;
        }

        return rowStart;
    }

    public int getHor_delay_distance() {
        return this.hor_delay_distance;
    }

    public void setHor_delay_distance(int hor_delay_distance) {
        this.hor_delay_distance = hor_delay_distance;
    }

    public int getVer_delay_distance() {
        return this.ver_delay_distance;
    }

    public void setVer_delay_distance(int ver_delay_distance) {
        this.ver_delay_distance = ver_delay_distance;
    }

    public int getMin_fast_setp_discance() {
        return this.min_fast_setp_discance;
    }

    public void setMin_fast_setp_discance(int min_fast_setp_discance) {
        this.min_fast_setp_discance = min_fast_setp_discance;
    }

    public int getFlip_scroll_frame_count() {
        return this.flip_scroll_frame_count;
    }

    public void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
        this.flip_scroll_frame_count = flip_scroll_frame_count;
    }

    public int getHor_delay_frame_count() {
        return this.hor_delay_frame_count;
    }

    public void setHor_delay_frame_count(int hor_delay_frame_count) {
        this.hor_delay_frame_count = hor_delay_frame_count;
    }

    public int getVer_delay_frame_count() {
        return this.ver_delay_frame_count;
    }

    public void setVer_delay_frame_count(int ver_delay_frame_count) {
        this.ver_delay_frame_count = ver_delay_frame_count;
    }

    private class FastStep {
        private int mCurrDelta;
        private int mCurrDistance;
        private Interpolator mInterpolator;
        private boolean mIsComeDown;
        private boolean mIsPositive;
        private int mMaxFastStep;
        private int mStartDelta;
        private int mStopDelta;
        private int mStopDistance;

        private FastStep() {
            mMaxFastStep = 0x7fffffff;
            this.mInterpolator = new DecelerateInterpolator(0.4F);
        }

        private void setMaxFastStep(int maxStep) {
            mMaxFastStep = maxStep;
        }

        private void clear() {
            mCurrDelta = 0x0;
            mIsComeDown = false;
            mStartDelta = 0x0;
            mStopDelta = 0x0;
            mCurrDistance = 0x0;
            mStopDistance = 0x0;
        }

        private void setStartStep(int step) {
            this.mIsComeDown = false;
            this.mStartDelta = step;
            this.mCurrDelta = this.mStartDelta;
            if(mCurrDelta > mMaxFastStep) {
                mCurrDelta = mMaxFastStep;
                return;
            }
            if(mCurrDelta < -mMaxFastStep) {
                mCurrDelta = -mMaxFastStep;
            }
        }

        //TODO WDZ CHECK
        private void resetComeDown(int target) {
            if(DEBUG) {
                Log.i("ItemFlipScroller", target + " mCurrDelta=" + mCurrDelta);
            }
            if(mIsComeDown) {
                return;
            }
            mIsComeDown = true;
            mCurrDistance = mCurrDelta;
            mStopDistance = target;
            if(mStartDelta > 0) {
                mStopDelta = min_fast_setp_discance;
                mIsPositive = true;
                return;
            }
            mStopDelta = -min_fast_setp_discance;
            mIsPositive = false;
        }

        private boolean computerOffset() {
            if(!this.mIsComeDown) {
                return true;
            } else if(this.mIsPositive && this.mCurrDistance > this.mStopDistance || !this.mIsPositive && this.mCurrDistance < this.mStopDistance) {
                return false;
            } else {
                this.mCurrDelta = 0;
                float input = (float)this.mCurrDistance / (float)this.mStopDistance;
                float output = input;
                if(this.mInterpolator != null) {
                    output = this.mInterpolator.getInterpolation(input);
                }

                this.mCurrDelta = this.mStartDelta + (int)((float)(this.mStopDelta - this.mStartDelta) * output);
                if(this.mIsPositive && this.mCurrDelta < ItemFlipScroller.this.min_fast_setp_discance) {
                    this.mCurrDelta = ItemFlipScroller.this.min_fast_setp_discance;
                } else if(!this.mIsPositive && this.mCurrDelta > -ItemFlipScroller.this.min_fast_setp_discance) {
                    this.mCurrDelta = -ItemFlipScroller.this.min_fast_setp_discance;
                }
                if((mIsPositive) && (mCurrDelta > mMaxFastStep)) {
                    mCurrDelta = mMaxFastStep;
                } else if((!mIsPositive) && (mCurrDelta < -mMaxFastStep)) {
                    mCurrDelta = -mMaxFastStep;
                }

                this.mCurrDistance += this.mCurrDelta;
                if(ItemFlipScroller.this.DEBUG) {
                    Log.i("ItemFlipScroller", "computerOffset input=" + input + " output=" + output + " mCurrDelta=" + this.mCurrDelta + " mCurrDistance=" + this.mCurrDistance + " mStopDistance=" + this.mStopDistance);
                }

                return true;
            }
        }

        private int getCurrStep() {
            return this.mCurrDelta;
        }

        private void finished() {
            this.mCurrDelta = 0;
        }
    }

    private class FlipItem {
        public int mIndex;
        public int mColumnIndex;
        public int mVerticalIndex;
        public int mCurrFrameCount;
        public int mLastDistance;
        public int mCurrDelta;
        public int mFinalDistance;
        public int mFastScrollOffset;
        public int mFastScrollStartingOffset;
        public int mFinalFrameCount;
        public int mTotalMoveDistance;
        public int mCurrTotalMoveDistance;

        public FlipItem(int index, int finalDistance) {
            this.mIndex = index;
            this.mFinalDistance = finalDistance;
        }
    }

    private static enum FlipItemFastStatus {
        UNSTART,
        START_UNSTOP,
        STOP;

        private FlipItemFastStatus() {
        }
    }

    private static enum FlipItemPositionType {
        HEATER,
        ADAPTER,
        FOOTER;

        private FlipItemPositionType() {
        }
    }

    private static enum FlipListChangeType {
        INIT,
        ADD_BEFORE,
        ADD_AFTER,
        TEMP,
        REFRESH,
        UNKNOWN;

        private FlipListChangeType() {
        }
    }

    public interface ItemFlipScrollerListener {
        void onOffsetNewChild(int var1, int var2, int var3);

        void onFinished();
    }
}
