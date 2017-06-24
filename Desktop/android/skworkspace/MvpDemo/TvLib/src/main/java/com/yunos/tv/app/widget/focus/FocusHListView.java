//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.HListView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED;

public class FocusHListView extends HListView implements DeepListener, ItemListener {
    protected static final String TAG = "FocusHListView";
    protected static final boolean DEBUG = false;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();
    boolean mIsAnimate = true;
    int mDistance = -1;
    boolean mDeepFocus = false;
    boolean mAutoSearch = false;
    ItemSelectedListener mItemSelectedListener;
    boolean mLayouted = false;
    boolean mReset = false;
    boolean mFocusBackground = false;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;
    int mItemWidth;
    DeepListener mDeep = null;
    boolean mDeepMode = false;
    private FocusHListView.FocusSelectionNotifier mFocusSelectionNotifier;

    public FocusHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FocusHListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusHListView(Context context) {
        super(context);
    }

    public void setDeepMode(boolean mode) {
        this.mDeepMode = mode;
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mItemSelectedListener = listener;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.d("FocusHListView", "onFocusChanged");
        if(!this.mAutoSearch && this.getOnFocusChangeListener() != null) {
            this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        if(this.mAutoSearch) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }

        if(gainFocus) {
            if(this.getChildCount() > 0 && this.mLayouted) {
                if(this.getLeftScrollDistance() == 0) {
                    this.reset();
                }
            } else {
                this.mReset = true;
            }

            if(this.mDeepMode) {
                boolean isDeep = false;
                if(this.getSelectedView() instanceof DeepListener) {
                    this.mDeep = (DeepListener)this.getSelectedView();
                    if(this.mDeep.canDeep()) {
                        isDeep = true;
                        Rect focusRect = new Rect(previouslyFocusedRect);
                        this.offsetRectIntoDescendantCoords((View)this.mDeep, focusRect);
                        this.mDeep.onFocusDeeped(gainFocus, direction, focusRect);
                        this.reset();
                    }
                }

                if(!isDeep) {
                    if(!this.mLayouted) {
                        this.mReset = true;
                    } else {
                        this.reset();
                        this.performSelect(gainFocus);
                    }
                }
            } else {
                this.performSelect(gainFocus);
            }
        } else if(this.mDeepMode) {
            if(this.mDeep != null && this.mDeep.canDeep()) {
                this.mDeep.onFocusDeeped(gainFocus, direction, (Rect)null);
            } else if(this.mLayouted) {
                this.performSelect(gainFocus);
            } else {
                this.mReset = true;
            }
        } else {
            this.performSelect(gainFocus);
        }

        this.mIsAnimate = this.checkAnimate(direction);
    }

    private boolean checkAnimate(int direction) {
        switch(direction) {
            case 17:
                return this.mAimateWhenGainFocusFromRight;
            case 33:
                return this.mAimateWhenGainFocusFromDown;
            case 66:
                return this.mAimateWhenGainFocusFromLeft;
            case 130:
                return this.mAimateWhenGainFocusFromUp;
            default:
                return true;
        }
    }

    public void setSelection(int position) {
        this.setSelectedPositionInt(position);
        this.setNextSelectedPositionInt(position);
        if(this.getChildCount() > 0 && this.mLayouted) {
            this.mLayoutMode = 9;
            if(this.isLayoutRequested()) {
                return;
            }

            this.layoutChildren();
        }

        this.reset();
    }

    protected void layoutChildren() {
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if(!blockLayoutRequests) {
            this.mBlockLayoutRequests = true;

            try {
                this.invalidate();
                if(this.getAdapter() != null) {
                    int childrenLeft = this.mListPadding.left;
                    int childrenRight = this.getRight() - this.getLeft() - this.mListPadding.right;
                    int childCount = this.getChildCount();
                    boolean index = false;
                    int delta = 0;
                    View sel = null;
                    View oldSel = null;
                    View oldFirst = null;
                    View newSel = null;
                    View focusLayoutRestoreView = null;
                    int lastPosition = this.getLastVisiblePosition();
                    boolean dataChanged = this.mDataChanged;
                    if(dataChanged) {
                        this.mLayoutMode = 9;
                    }

                    int var23;
                    switch(this.mLayoutMode) {
                        case 2:
                            var23 = this.mNextSelectedPosition - this.mFirstPosition;
                            if(var23 >= 0 && var23 < childCount) {
                                newSel = this.getChildAt(var23);
                            }
                            break;
                        case 3:
                        case 6:
                        default:
                            var23 = this.mSelectedPosition - this.mFirstPosition;
                            if(var23 >= 0 && var23 < childCount) {
                                oldSel = this.getChildAt(var23);
                            }

                            oldFirst = this.getChildAt(0);
                            if(this.mNextSelectedPosition >= 0) {
                                delta = this.mNextSelectedPosition - this.mSelectedPosition;
                            }

                            newSel = this.getChildAt(var23 + delta);
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                    }

                    if(dataChanged) {
                        this.handleDataChanged();
                    }

                    if(this.mItemCount == 0) {
                        this.resetList();
                        return;
                    }

                    if(this.mItemCount != this.getAdapter().getCount()) {
                        throw new IllegalStateException("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. [in ListView(" + this.getId() + ", " + this.getClass() + ") with Adapter(" + this.getAdapter().getClass() + ")]");
                    }

                    this.setSelectedPositionInt(this.mNextSelectedPosition);
                    int firstPosition = this.mFirstPosition;
                    RecycleBin recycleBin = this.mRecycler;
                    View focusLayoutRestoreDirectChild = null;
                    if(dataChanged) {
                        for(int focusedChild = 0; focusedChild < childCount; ++focusedChild) {
                            recycleBin.addScrapView(this.getChildAt(focusedChild), firstPosition + focusedChild);
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
                    switch(this.mLayoutMode) {
                        case 2:
                            if(newSel != null) {
                                sel = this.fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
                            } else {
                                sel = this.fillFromMiddle(childrenLeft, childrenRight);
                            }
                            break;
                        case 3:
                        default:
                            int child;
                            if(childCount == 0) {
                                if(!this.mStackFromBottom) {
                                    child = this.lookForSelectablePosition(this.mSelectedPosition, true);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillFromLeft(childrenLeft);
                                } else {
                                    child = this.lookForSelectablePosition(this.mItemCount - 1, false);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillLeft(this.mItemCount - 1, childrenRight);
                                }
                            } else {
                                child = this.mSelectedPosition;
                                if(this.mSelectedPosition >= firstPosition + delta && this.mSelectedPosition <= lastPosition + delta) {
                                    child = this.mSelectedPosition;
                                } else {
                                    oldSel = oldFirst;
                                    child = this.mFirstPosition + delta;
                                }

                                if(this.mSelectedPosition >= 0 && this.mSelectedPosition < this.mItemCount) {
                                    sel = this.fillSpecific(child, oldSel == null?childrenLeft:oldSel.getLeft());
                                } else if(this.mFirstPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mFirstPosition, oldFirst == null?childrenLeft:oldFirst.getLeft());
                                } else {
                                    sel = this.fillSpecific(0, childrenLeft);
                                }
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
                            break;
                        case 9:
                            sel = this.fillFromMiddle(childrenLeft, childrenRight);
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

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if((this.hasFocus() || this.hasDeepFocus()) && this.getLeftScrollDistance() == 0) {
            this.reset();
        }

        if(this.getChildCount() > 0) {
            if(this.mReset) {
                this.performSelect(this.hasFocus() || this.hasDeepFocus());
                this.mReset = false;
            }

            if(this.hasFocus() && this.mDeepMode) {
                boolean isDeep = false;
                if(this.getSelectedView() instanceof DeepListener) {
                    this.mDeep = (DeepListener)this.getSelectedView();
                    if(this.mDeep.canDeep() && !this.mDeep.hasDeepFocus()) {
                        isDeep = true;
                        this.mDeep.onFocusDeeped(true, 17, (Rect)null);
                        this.reset();
                    }
                }

                if(!isDeep) {
                    this.reset();
                }
            }
        } else {
            this.mReset = true;
        }

        this.mLayouted = true;
        this.mClipFocusRect.set(0, 0, this.getWidth(), this.getHeight());
    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int index = -1;
        if(this.getAdapter() != null) {
            if(this.getAdapter().getCount() > 0) {
                index = 0;
            }

            if(this.getHeaderViewsCount() > 0 && this.getAdapter().getCount() > this.getHeaderViewsCount()) {
                index = this.getHeaderViewsCount();
            }

            if(index >= 0) {
                this.measureItemWidth(index);
            }
        }

    }

    protected void measureItemWidth(int index) {
        View child = this.obtainView(index, this.mIsScrap);
        int measureWidth = 0;
        if(child != null) {
            measureWidth = child.getMeasuredWidth();
            LayoutParams p = (LayoutParams)child.getLayoutParams();
            if(p == null) {
                p = (LayoutParams)this.generateDefaultLayoutParams();
                child.setLayoutParams(p);
            }

            if(measureWidth == 0 && child.getLayoutParams() != null) {
                byte unSpecified = 0;
                int w = MeasureSpec.makeMeasureSpec(0, unSpecified);
                int h = MeasureSpec.makeMeasureSpec(0, unSpecified);
                child.measure(w, h);
                measureWidth = child.getMeasuredWidth();
            }
        }

        this.mItemWidth = measureWidth;
    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            //wdz TODO
            if(mDeepMode==true && mDeep!=null)
            {
                Log.i(TAG,"getParams wdz:"+mDeep.getParams()+" mDeep:"+mDeep);
                return mDeep.getParams();
            }
            View view=getSelectedView();
            if(view!=null && view instanceof FocusListener)
            {
                return ((FocusListener)view).getParams();
            }
            //wdz end
            return this.mParams;
        }
    }

    public void getFocusedRect(Rect r) {
        if(!this.hasFocus() && !this.hasDeepFocus()) {
            this.getDrawingRect(r);
        } else {
            super.getFocusedRect(r);
        }
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if(this.hasFocus()) {
            super.addFocusables(views, direction, focusableMode);
        } else if(views != null) {
            if(this.isFocusable()) {
                if((focusableMode & 1) != 1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) {
                    views.add(this);
                }
            }
        }
    }

    protected void reset() {
        if(this.getSelectedView() != null) {
            if(this.mDeep != null) {
                this.mFocusRectparams.set(this.mDeep.getFocusParams());
            } else {
                ItemListener item = (ItemListener)this.getSelectedView();
                if(item != null) {
                    this.mFocusRectparams.set(item.getFocusParams());
                }
            }

            this.offsetDescendantRectToMyCoords(this.getSelectedView(), this.mFocusRectparams.focusRect());
        }
    }

    private void resetHeader(int nextSelectedPosition) {
        View header = this.getHeaderView(nextSelectedPosition);
        ItemListener item = (ItemListener)header;
        if(item != null) {
            this.mFocusRectparams.set(item.getFocusParams());
        }

        int left = this.getChildAt(0).getLeft();

        for(int index = this.getFirstVisiblePosition() - 1; index >= 0; --index) {
            if(index >= this.getHeaderViewsCount()) {
                if(this.mItemWidth <= 0) {
                    Log.e("FocusHListView", "FocusHList mItemWidth <= 0");
                }

                left -= this.mItemWidth;
            } else {
                left -= this.getHeaderView(index).getWidth();
            }
        }

        this.mFocusRectparams.focusRect().left = left;
        this.mFocusRectparams.focusRect().right = left + header.getWidth();
    }

    public FocusRectParams getFocusParams() {
        View v = this.getSelectedView();
        if(this.isFocusedOrSelected() && v != null) {
            if(this.mFocusRectparams == null || this.isScrolling()) {
                this.reset();
            }

            return this.mFocusRectparams;
        } else {
            Rect r = new Rect();
            this.getFocusedRect(r);
            this.mFocusRectparams.set(r, 0.5F, 0.5F);
            return this.mFocusRectparams;
        }
    }

    private boolean isFocusedOrSelected() {
        return this.isFocused() || this.isSelected();
    }

    public boolean isFocused() {
        return super.isFocused() || this.hasFocus() || this.hasDeepFocus();
    }

    public boolean canDraw() {
        if(this.mDeep != null) {
            return this.mDeep.canDraw();
        } else {
            View v = this.getSelectedView();
            if(v != null && this.mReset) {
                this.performSelect(true);
                this.mReset = false;
            }

            return this.getSelectedView() != null && this.mLayouted;
        }
    }

    public boolean isAnimate() {
        return this.mDeep != null?this.mDeep.isAnimate():this.mIsAnimate;
    }

    public boolean onKeyDownDeep(int keyCode, KeyEvent event) {
        if(this.mDeepMode && this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus() && this.mDeep.onKeyDown(keyCode, event)) {
            this.reset();
            this.offsetFocusRect(-this.getLeftScrollDistance(), 0);
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("FocusHListView", "onKeyDown keyCode = " + keyCode);
        if(this.onKeyDownDeep(keyCode, event)) {
            return true;
        } else if(this.getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        } else if(this.checkState(keyCode)) {
            return true;
        } else {
            if(this.mDistance < 0) {
                this.mDistance = this.getChildAt(0).getWidth();
            }

            switch(keyCode) {
                case 21:
                    if(this.moveLeft()) {
                        this.playSoundEffect(1);
                        return true;
                    }
                    break;
                case 22:
                    if(this.moveRight()) {
                        this.playSoundEffect(3);
                        return true;
                    }
            }

            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus()?this.mDeep.onKeyUp(keyCode, event):super.onKeyUp(keyCode, event);
    }

    private boolean moveLeft() {
        int nextSelectedPosition = this.getSelectedItemPosition() - 1 >= 0?this.getSelectedItemPosition() - 1:-1;
        if(this.mDeepMode && this.getChildAt(nextSelectedPosition - this.getFirstPosition()) == null) {
            return true;
        } else if(Math.abs(this.getLeftScrollDistance()) > this.getChildAt(0).getWidth() * 3) {
            return true;
        } else {
            this.performSelect(false);
            this.mReset = false;
            if(nextSelectedPosition != -1) {
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if(this.mDeepMode) {
                    View amountToScroll = (View)this.mDeep;
                    Rect focusRect = new Rect(this.mDeep.getFocusParams().focusRect());
                    this.mDeep.onFocusDeeped(false, 17, (Rect)null);
                    this.mDeep = null;
                    DeepListener deep = (DeepListener)this.getSelectedView();
                    if(deep.canDeep()) {
                        this.mDeep = deep;
                        this.offsetDescendantRectToMyCoords(amountToScroll, focusRect);
                        this.offsetRectIntoDescendantCoords(this.getSelectedView(), focusRect);
                        this.mDeep.onFocusDeeped(true, 17, focusRect);
                    }
                }

                if(this.canDraw()) {
                    this.mReset = false;
                    this.performSelect(true);
                } else {
                    this.mReset = true;
                }

                this.amountToCenterScroll(33, nextSelectedPosition);
                return true;
            } else {
                return false;
            }
        }
    }

    private void performSelect(boolean select) {
        if(this.mItemSelectedListener != null) {
            this.mItemSelectedListener.onItemSelected(this.getSelectedView(), this.getSelectedItemPosition(), select, this);
        }

    }

    private boolean moveRight() {
        int nextSelectedPosition = this.getSelectedItemPosition() + 1 < this.mItemCount?this.getSelectedItemPosition() + 1:-1;
        if(this.mDeepMode && this.getChildAt(nextSelectedPosition - this.getFirstPosition()) == null) {
            return true;
        } else if(this.getLeftScrollDistance() > this.getChildAt(0).getWidth() * 3) {
            return true;
        } else {
            this.performSelect(false);
            this.mReset = false;
            if(nextSelectedPosition != -1) {
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if(this.mDeepMode) {
                    View amountToScroll = (View)this.mDeep;
                    Rect focusRect = new Rect(this.mDeep.getFocusParams().focusRect());
                    this.mDeep.onFocusDeeped(false, 66, (Rect)null);
                    this.mDeep = null;
                    DeepListener deep = (DeepListener)this.getSelectedView();
                    if(deep.canDeep()) {
                        this.mDeep = deep;
                        this.offsetDescendantRectToMyCoords(amountToScroll, focusRect);
                        this.offsetRectIntoDescendantCoords(this.getSelectedView(), focusRect);
                        this.mDeep.onFocusDeeped(true, 66, focusRect);
                    }
                }

                if(this.canDraw()) {
                    this.mReset = false;
                    this.performSelect(true);
                } else {
                    this.mReset = true;
                }

                this.amountToCenterScroll(130, nextSelectedPosition);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean checkState(int keyCode) {
        return this.mLastScrollState == 2 && (keyCode == 19 || keyCode == 20);
    }

    int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int center = (this.getWidth() - this.mListPadding.left - this.mListPadding.right) / 2 + this.mListPadding.left;
        int listRight = this.getWidth() - this.mListPadding.right;
        int listLeft = this.mListPadding.left;
        int numChildren = this.getChildCount();
        int amountToScroll = 0;
        int distanceLeft = this.getLeftScrollDistance();
        View nextSelctedView;
        boolean nextSelectedCenter;
        boolean reset;
        int finalNextSelectedCenter;
        int maxDiff;
        int var16;
        if(direction == 130) {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            nextSelectedCenter = false;
            reset = false;
            if(nextSelctedView == null) {
                nextSelctedView = this.getChildAt(this.getChildCount() - 1);
                var16 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                var16 += (nextSelctedView.getWidth() + this.mSpacing) * (nextSelectedPosition - this.getLastVisiblePosition());
                reset = false;
            } else {
                var16 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            finalNextSelectedCenter = var16 - distanceLeft;
            if(finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;
                maxDiff = (nextSelctedView.getWidth() + this.mSpacing) * (this.mItemCount - this.getLastVisiblePosition() - 1);
                maxDiff -= distanceLeft;
                View var17 = this.getChildAt(numChildren - 1);
                if(var17.getRight() > this.getWidth() - this.mListPadding.right) {
                    maxDiff += var17.getRight() - (this.getWidth() - this.mListPadding.right);
                }

                if(amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if(reset) {
                    this.reset();
                    this.offsetFocusRect(-distanceLeft, 0);
                }

                if(amountToScroll > 0) {
                    if(reset) {
                        this.offsetFocusRect(-amountToScroll, 0);
                    } else {
                        this.offsetFocusRect(nextSelctedView.getWidth() + this.mSpacing - amountToScroll, 0);
                    }

                    this.smoothScrollBy(amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if(!reset) {
                        this.offsetFocusRect(nextSelctedView.getWidth() + this.mSpacing, 0);
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.offsetFocusRect(-distanceLeft, 0);
                this.mIsAnimate = true;
            }

            return amountToScroll;
        } else if(direction != 33) {
            return 0;
        } else {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            nextSelectedCenter = false;
            reset = false;
            if(nextSelctedView == null) {
                nextSelctedView = this.getChildAt(0);
                var16 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                if(nextSelectedPosition >= this.getHeaderViewsCount()) {
                    var16 -= (nextSelctedView.getWidth() + this.mSpacing) * (this.getFirstVisiblePosition() - nextSelectedPosition);
                } else {
                    var16 -= (nextSelctedView.getWidth() + this.mSpacing) * (this.getFirstVisiblePosition() - this.getHeaderViewsCount());

                    for(finalNextSelectedCenter = this.getHeaderViewsCount() - 1; finalNextSelectedCenter >= nextSelectedPosition; --finalNextSelectedCenter) {
                        var16 -= this.getHeaderView(finalNextSelectedCenter).getWidth();
                    }
                }

                reset = false;
            } else {
                var16 = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            finalNextSelectedCenter = var16 - distanceLeft;
            if(finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                maxDiff = 0;
                if(this.getFirstVisiblePosition() >= this.getHeaderViewsCount()) {
                    maxDiff = (nextSelctedView.getWidth() + this.mSpacing) * (this.getFirstVisiblePosition() - this.getHeaderViewsCount());
                }

                int start = this.getHeaderViewsCount() - 1;
                if(start > this.getFirstVisiblePosition() - 1) {
                    start = this.getFirstVisiblePosition() - 1;
                }

                for(int firstVisibleView = start; firstVisibleView >= 0; --firstVisibleView) {
                    maxDiff += this.getHeaderView(firstVisibleView).getWidth();
                }

                if(maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View var18 = this.getChildAt(0);
                if(var18.getLeft() < listLeft) {
                    maxDiff += listLeft - var18.getLeft();
                }

                if(amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if(reset) {
                    this.reset();
                    this.offsetFocusRect(-distanceLeft, 0);
                } else if(nextSelectedPosition < this.getHeaderViewsCount()) {
                    reset = true;
                    this.resetHeader(nextSelectedPosition);
                    this.offsetFocusRect(-distanceLeft, 0);
                }

                if(amountToScroll > 0) {
                    if(reset) {
                        this.offsetFocusRect(amountToScroll, 0);
                    } else {
                        this.offsetFocusRect(-(nextSelctedView.getWidth() + this.mSpacing - amountToScroll), 0);
                    }

                    this.smoothScrollBy(-amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if(!reset) {
                        this.offsetFocusRect(-nextSelctedView.getWidth() + this.mSpacing, 0);
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.offsetFocusRect(-distanceLeft, 0);
                this.mIsAnimate = true;
            }

            return amountToScroll;
        }
    }

    int amountToScroll(int direction, int nextSelectedPosition) {
        int listRight = this.getWidth() - this.mListPadding.right;
        int listLeft = this.mListPadding.left;
        int numChildren = this.getChildCount();
        int amountToScroll;
        int indexToMakeVisible;
        View viewToMakeVisible;
        View nextSelctedView;
        int width;
        if(direction == 66) {
            amountToScroll = 0;
            indexToMakeVisible = numChildren - 1;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            if(nextSelctedView != null) {
                if(nextSelectedPosition <= this.getLastVisiblePosition()) {
                    if(nextSelctedView.getRight() <= listRight) {
                        this.mIsAnimate = true;
                    } else {
                        amountToScroll = viewToMakeVisible.getWidth();
                        this.mIsAnimate = false;
                    }
                } else {
                    amountToScroll = viewToMakeVisible.getWidth();
                    this.mIsAnimate = false;
                }
            } else if(nextSelectedPosition < this.mItemCount - 1) {
                amountToScroll = viewToMakeVisible.getWidth();
                this.mIsAnimate = false;
            } else {
                width = this.mFocusRectparams.focusRect().width();
                this.mFocusRectparams.focusRect().right = listRight;
                this.mFocusRectparams.focusRect().left = listRight - width;
                amountToScroll = nextSelctedView.getRight() - listRight;
                this.mIsAnimate = true;
            }

            if(amountToScroll > 0) {
                Log.d("FocusHListView", "amountToScroll: amountToScroll = " + amountToScroll);
                this.smoothScrollBy(amountToScroll);
            }

            return amountToScroll;
        } else {
            amountToScroll = 0;
            indexToMakeVisible = numChildren - 1;
            viewToMakeVisible = this.getChildAt(indexToMakeVisible);
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
            if(nextSelctedView != null) {
                if(nextSelectedPosition <= this.getLastVisiblePosition()) {
                    if(nextSelctedView.getLeft() >= listLeft) {
                        this.mIsAnimate = true;
                    } else {
                        amountToScroll = viewToMakeVisible.getWidth();
                        this.mIsAnimate = false;
                    }
                } else {
                    amountToScroll = viewToMakeVisible.getWidth();
                    this.mIsAnimate = false;
                }
            } else if(nextSelectedPosition < this.mItemCount - 1) {
                amountToScroll = viewToMakeVisible.getWidth();
                this.mIsAnimate = false;
            } else {
                width = this.mFocusRectparams.focusRect().width();
                this.mFocusRectparams.focusRect().left = listLeft;
                this.mFocusRectparams.focusRect().right = listLeft + width;
                amountToScroll = listLeft - nextSelctedView.getLeft();
                this.mIsAnimate = true;
            }

            if(amountToScroll > 0) {
                Log.d("FocusHListView", "amountToScroll: amountToScroll = " + amountToScroll);
                this.smoothScrollBy(-amountToScroll);
            }

            return amountToScroll;
        }
    }

    public ItemListener getItem() {
        return this.mDeep != null && this.mDeep.hasDeepFocus()?this.mDeep.getItem():(ItemListener)this.getSelectedView();
    }

    public boolean isScrolling() {
        boolean isScrolling = this.mLastScrollState != 0;
        return this.mDeep != null?this.mDeep.isScrolling() || isScrolling:isScrolling;
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if(this.mDeep != null && this.mDeep.preOnKeyDown(keyCode, event)) {
            return true;
        } else {
            switch(keyCode) {
                case 19:
                case 20:
                    return true;
                case 21:
                case 122:
                    return this.getSelectedItemPosition() > 0;
                case 22:
                case 123:
                    return this.getSelectedItemPosition() < this.mItemCount - 1;
                case 23:
                case 66:
                    return true;
                default:
                    return false;
            }
        }
    }

    public boolean hasDeepFocus() {
        return this.mDeepFocus;
    }

    public boolean canDeep() {
        return true;
    }

    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mDeepFocus = gainFocus;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public boolean isScale() {
        return true;
    }

    public int getItemWidth() {
        return this.getWidth();
    }

    public int getItemHeight() {
        return this.getHeight();
    }

    public Rect getManualPadding() {
        return null;
    }

    public void onItemSelected(boolean selected) {
        this.performSelect(selected);
    }

    public void onItemClick() {
        if(this.getSelectedView() != null) {
            this.performItemClick(this.getSelectedView(), this.getSelectedItemPosition(), 0L);
        }

    }

    public boolean isFocusBackground() {
        return this.mDeep != null?this.mDeep.isFocusBackground():this.mFocusBackground;
    }

    public void drawBeforeFocus(Canvas canvas) {
    }

    public void drawAfterFocus(Canvas canvas) {
    }

    public boolean isFinished() {
        return true;
    }

    public void onFocusStart() {
        if(this.mDeep != null) {
            this.mDeep.onFocusStart();
        } else {
            super.onFocusStart();
        }
    }

    public void onFocusFinished() {
        if(this.mDeep != null) {
            this.mDeep.onFocusFinished();
        } else {
            super.onFocusFinished();
        }
    }

    public boolean performItemClick(View view, int position, long id) {
        if(this.mDeep != null) {
            this.mDeep.onItemClick();
            return true;
        } else {
            return super.performItemClick(view, position, id);
        }
    }

    public void offsetFocusRect(int offsetX, int offsetY) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            this.mFocusRectparams.focusRect().offset(offsetX, offsetY);
        }

    }

    public Rect getClipFocusRect() {
        return this.mClipFocusRect;
    }

    /**
     * 修改导致第一次 select选中事件失败的情景
     */
    @Override
    protected void  checkSelectionChanged()
    {
        super.checkSelectionChanged();
        if (this.mSelectedPosition != this.mOldSelectedPosition || this.mSelectedRowId != this.mOldSelectedRowId) {
            this.selectionChanged();
            this.mOldSelectedPosition = this.mSelectedPosition;
            this.mOldSelectedRowId = this.mSelectedRowId;
        }
    }
    /**
     * 基本上就是为了调用选中事件接口
     */
    void selectionChanged() {
        if (this.mItemSelectedListener != null) {
            if (!this.mInLayout && !this.mBlockLayoutRequests) {
                FocusHListView.this.fireOnSelected();
            } else {
                //准备布局走异步
                if (this.mFocusSelectionNotifier == null) {
                    this.mFocusSelectionNotifier = new FocusHListView.FocusSelectionNotifier();
                }
                this.post(this.mFocusSelectionNotifier);
            }
        }
        //这里调用了辅助事件 如果没启动这个服务 无效，否则会向AccessibilityServiece发送消息
        if (this.mSelectedPosition != INVALID_POSITION && this.isShown() && !this.isInTouchMode()) {
            this.sendAccessibilityEvent(TYPE_VIEW_SELECTED);
        }

    }

    private class FocusSelectionNotifier implements Runnable {
        private FocusSelectionNotifier() {
        }

        public void run() {
            if (mDataChanged) {
                if (FocusHListView.this.getAdapter() != null) {
                    FocusHListView.this.post(this);
                }
            } else {
                FocusHListView.this.fireOnSelected();
            }

        }
    }

    /**
     * 通知mOnItemSelectedListener 接口触发选项选中事件
     */
    private void fireOnSelected() {
        if (this.mItemSelectedListener != null) {
            int selection = this.getSelectedItemPosition();
            if (selection >= 0 && selection < this.getCount()) {
                View v = this.getSelectedView();
                this.mItemSelectedListener.onItemSelected(v,selection,true,this);
            }
        }
    }
}
