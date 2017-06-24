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
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.ListView;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_MOVE_END;
import static android.view.KeyEvent.KEYCODE_MOVE_HOME;
import static android.view.KeyEvent.KEYCODE_PAGE_DOWN;
import static android.view.KeyEvent.KEYCODE_PAGE_UP;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED;

public class FocusListView extends ListView implements DeepListener, ItemListener {
    protected static String TAG = "FocusListView";
    protected static boolean DEBUG = false;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();
    protected boolean mIsAnimate = true;
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
    protected int mItemHeight = 0;
    DeepListener mDeep = null;
    boolean mNeedReset = false;
    boolean mDeepMode = false;
    private FocusSelectionNotifier mFocusSelectionNotifier;

    public FocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public FocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusListView(Context context) {
        super(context);
    }

    /**
     * 子布局是否可以选中焦点。<br>
     *     子布局需要是Focus开头的阿里控件，具体参考例子
     * @param mode
     */
    public void setDeepMode(boolean mode) {
        this.mDeepMode = mode;
    }

    public void setAutoSearch(boolean autoSearch) {
        this.mAutoSearch = autoSearch;
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    /**
     * 设置焦点背景模式：
     * 一般当Listview的背景为透明或者半透明，为了不遮挡前景而设置这个为真
     * @param back
     */
    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mItemSelectedListener = listener;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.d(TAG, "onFocusChanged :"+this.getChildCount());
        if(!this.mAutoSearch && this.getOnFocusChangeListener() != null) {
            this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        if(this.mAutoSearch) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }

        if(this.getChildCount() > 0) {
            Log.d(TAG, "onFocusChanged 1");
            if(gainFocus) {
                Log.d(TAG, "onFocusChanged 2");
                if(this.mLayouted && this.getLeftScrollDistance() == 0) {
                    this.reset();
                }

                if(this.mDeepMode) {
                    boolean isDeep = false;
                    if(this.getSelectedView() instanceof DeepListener) {
                        this.mDeep = (DeepListener)this.getSelectedView();
                        if(this.mDeep != null && this.mDeep.canDeep()) {
                            isDeep = true;
                            Rect focusRect = null;
                            if(previouslyFocusedRect != null) {
                                focusRect = new Rect(previouslyFocusedRect);
                                this.offsetRectIntoDescendantCoords((View)this.mDeep, focusRect);
                            }

                            this.mDeep.onFocusDeeped(gainFocus, direction, focusRect);
                            this.reset();
                        }
                    }

                    if(!isDeep) {
                        if(!this.mLayouted) {
                            this.mNeedReset = true;
                        } else {
                            this.reset();
                            this.performSelect(gainFocus);
                        }
                    }
                } else {
                    Log.d(TAG, "onFocusChanged 3");
                    this.performSelect(gainFocus);
                }
            } else if(this.mDeepMode) {
                if(this.mDeep != null && this.mDeep.canDeep()) {
                    this.mDeep.onFocusDeeped(gainFocus, direction, (Rect)null);
                } else if(this.mLayouted) {
                    this.performSelect(gainFocus);
                } else {
                    this.mNeedReset = true;
                }
            } else {
                this.performSelect(gainFocus);
            }
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
        if(this.getVisibleChildCount() > 0 && this.mLayouted) {
            this.mLayoutMode = 9;
            if(this.isLayoutRequested()) {
                return;
            }
            Log.i(TAG,"3");
            this.layoutChildren();
            Log.i(TAG,"4");
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
                    int childrenTop = this.mListPadding.top;
                    int childrenBottom = this.getBottom() - this.getTop() - this.mListPadding.bottom;
                    int visibleChildCount = this.getVisibleChildCount();
                    int childCount = this.getChildCount();
                    boolean index = false;
                    int delta = 0;
                    View sel = null;
                    View oldSel = null;
                    View oldFirst = null;
                    View newSel = null;
                    View focusLayoutRestoreView = null;
                    int lastPosition = this.getLastPosition();
                    boolean dataChanged = this.mDataChanged;
                    if(dataChanged) {
                        this.mLayoutMode = 9;
                    }

                    int var24;
                    switch(this.mLayoutMode) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                            break;
                        case 2:
                            var24 = this.mNextSelectedPosition - this.mFirstPosition;
                            if(var24 >= 0 && var24 < visibleChildCount) {
                                newSel = this.getChildAt(var24 + this.getUpPreLoadedCount());
                            }
                            break;
                        default:
                            var24 = this.mSelectedPosition - this.mFirstPosition;
                            if(var24 >= 0 && var24 < visibleChildCount) {
                                oldSel = this.getChildAt(var24 + this.getUpPreLoadedCount());
                            }

                            oldFirst = this.getFirstVisibleChild();
                            if(this.mNextSelectedPosition >= 0) {
                                delta = this.mNextSelectedPosition - this.mSelectedPosition;
                            }

                            this.getChildAt(var24 + delta + this.getUpPreLoadedCount());
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

                    View var25 = this.getFocusedChild();
                    if(var25 != null) {
                        if(!dataChanged || this.isDirectChildHeaderOrFooter(var25)) {
                            focusLayoutRestoreDirectChild = var25;
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
                    switch(this.mLayoutMode) {
                        case 1:
                            this.mFirstPosition = 0;
                            sel = this.fillFromTop(childrenTop);
                            this.adjustViewsUpOrDown();
                            break;
                        case 2:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        default:
                            if(childCount == 0) {
                                if(!this.mStackFromBottom) {
                                    child = this.lookForSelectablePosition(this.mSelectedPosition, true);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillFromTop(childrenTop);
                                } else {
                                    child = this.lookForSelectablePosition(this.mItemCount - 1, false);
                                    this.setSelectedPositionInt(child);
                                    sel = this.fillUp(this.mItemCount - 1, childrenBottom);
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
                                    sel = this.fillSpecific(child, oldSel == null?childrenTop:oldSel.getTop());
                                } else if(this.mFirstPosition < this.mItemCount) {
                                    sel = this.fillSpecific(this.mFirstPosition, oldFirst == null?childrenTop:oldFirst.getTop());
                                } else {
                                    sel = this.fillSpecific(0, childrenTop);
                                }
                            }
                            break;
                        case 3:
                            sel = this.fillUp(this.mItemCount - 1, childrenBottom);
                            this.adjustViewsUpOrDown();
                            break;
                        case 4:
                            sel = this.fillSpecific(this.reconcileSelectedPosition(), this.mSpecificTop);
                            break;
                        case 9:
                            sel = this.fillFromMiddle(childrenTop, childrenBottom);
                    }

                    recycleBin.scrapActiveViews();
                    if(sel != null) {
                        if(this.mItemsCanFocus && this.hasFocus() && !sel.hasFocus()) {
                            boolean var26 = sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus() || sel.requestFocus();
                            if(!var26) {
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
                            View var27 = this.getChildAt(this.mMotionPosition - this.mFirstPosition);
                            if(var27 != null) {
                                this.positionSelector(this.mMotionPosition, var27);
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
            if(this.mNeedReset) {
                this.performSelect(this.hasFocus() || this.hasDeepFocus());
                this.mNeedReset = false;
            }

            if(this.hasFocus() && this.mDeepMode) {
                boolean isDeep = false;
                if(this.getSelectedView() instanceof DeepListener) {
                    this.mDeep = (DeepListener)this.getSelectedView();
                    if(this.mDeep != null && this.mDeep.canDeep() && !this.mDeep.hasDeepFocus()) {
                        isDeep = true;
                        this.mDeep.onFocusDeeped(true, 17, (Rect)null);
                        this.reset();
                    }
                }

                if(!isDeep) {
                    this.reset();
                }
            }
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

            this.measureItemHeight(index);
        }

    }

    protected void measureItemHeight(int index) {
        View child = this.obtainView(index, this.mIsScrap);
        int measureHeight = 0;
        if(child != null) {
            measureHeight = child.getMeasuredHeight();
            LayoutParams p = (LayoutParams)child.getLayoutParams();
            if(p == null) {
                p = (LayoutParams)this.generateDefaultLayoutParams();
                child.setLayoutParams(p);
            }

            if(measureHeight == 0 && child.getLayoutParams() != null) {
                byte unSpecified = 0;
                int w = MeasureSpec.makeMeasureSpec(0, unSpecified);
                int h = MeasureSpec.makeMeasureSpec(0, unSpecified);
                child.measure(w, h);
                measureHeight = child.getMeasuredHeight();
            }
        }

        this.mItemHeight = measureHeight;
    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            //wdz TODO
            if(mDeepMode==true && mDeep!=null)
            {
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

    protected void resetHeader(int nextSelectedPosition) {
        View header = this.getHeaderView(nextSelectedPosition);
        ItemListener item = (ItemListener)header;
        if(item != null) {
            this.mFocusRectparams.set(item.getFocusParams());
        }

        int top = this.getChildAt(0).getTop();

        for(int index = this.getFirstPosition() - 1; index >= 0; --index) {
            if(index >= this.getHeaderViewsCount()) {
                top -= this.mItemHeight;
            } else {
                top -= this.getHeaderView(index).getHeight();
            }
        }

        this.mFocusRectparams.focusRect().top = top;
        this.mFocusRectparams.focusRect().bottom = top + header.getHeight();
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
        if(this.mDeepMode && this.mDeep != null
                && this.mDeep.canDeep() && this.mDeep.hasDeepFocus()
                && this.mDeep.onKeyDown(keyCode, event)) {
            this.reset();
            this.offsetFocusRect(0, -this.getLeftScrollDistance());
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode = " + keyCode);
        if(this.onKeyDownDeep(keyCode, event)) {
            Log.d(TAG, "onKeyDown:onKeyDownDeep" );
            return true;
        } else if(this.getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        } else if(this.checkState(keyCode)) {
            return true;
        } else {
            if(this.mDistance < 0) {
                this.mDistance = this.getChildAt(0).getHeight();
            }

            switch(keyCode) {
                case 19:
                    if(this.moveUp()) {
                        this.playSoundEffect(1);
                        return true;
                    }
                    break;
                case 20:
                    if(this.moveDown()) {
                        this.playSoundEffect(1);
                        return true;
                    }
            }

            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus()?this.mDeep.onKeyUp(keyCode, event):super.onKeyUp(keyCode, event);
    }

    private boolean moveUp() {
        int nextSelectedPosition = this.getNextSelectedPosition(33);
        if(this.mDeepMode && this.getChildAt(nextSelectedPosition - this.getFirstPosition()) == null) {
            return true;
        } else {
            this.performSelect(false);
            this.mReset = false;
            if(nextSelectedPosition != -1) {
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if(this.mDeepMode) {
                    View amountToScroll = (View)this.mDeep;
                    Rect focusRect = null;
                    if(this.mDeep != null) {
                        focusRect = new Rect(this.mDeep.getFocusParams().focusRect());
                        this.mDeep.onFocusDeeped(false, 33, (Rect)null);
                    }

                    this.mDeep = null;
                    DeepListener deep = (DeepListener)this.getSelectedView();
                    if(deep != null && deep.canDeep()) {
                        this.mDeep = deep;
                        if(focusRect != null && amountToScroll != null) {
                            this.offsetDescendantRectToMyCoords(amountToScroll, focusRect);
                            this.offsetRectIntoDescendantCoords(this.getSelectedView(), focusRect);
                        }

                        this.mDeep.onFocusDeeped(true, 33, focusRect);
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
        Log.i(TAG,"performSelect:select:"+select);
        if(this.mItemSelectedListener != null) {
            this.mItemSelectedListener.onItemSelected(this.getSelectedView(), this.getSelectedItemPosition(), select, this);
        }

    }

    public int getNextSelectedPosition(int direction) {
        boolean isDown = direction == 130;
        int nextSelectedPosition = this.lookForSelectablePosition(this.getSelectedItemPosition() + (isDown?1:-1), isDown);
        return nextSelectedPosition >= 0?nextSelectedPosition:-1;
    }

    private boolean moveDown() {
        Log.d(TAG, "moveDown1");
        int nextSelectedPosition = this.getNextSelectedPosition(130);
        if(this.mDeepMode && this.getChildAt(nextSelectedPosition - this.getFirstPosition()) == null) {
            Log.d(TAG, "moveDown2");
            //this.amountToCenterScroll(130, nextSelectedPosition);
            return true;
        } else {
            Log.d(TAG, "moveDown3");
            this.performSelect(false);
            this.mReset = false;
            if(nextSelectedPosition != -1) {
                Log.d(TAG, "moveDown4");
                this.setSelectedPositionInt(nextSelectedPosition);
                this.setNextSelectedPositionInt(nextSelectedPosition);
                if(this.mDeepMode) {
                    Log.d(TAG, "moveDown5");
                    View amountToScroll = (View)this.mDeep;
                    Rect focusRect = null;
                    if(this.mDeep != null) {
                        focusRect = new Rect(this.mDeep.getFocusParams().focusRect());
                        this.mDeep.onFocusDeeped(false, 130, (Rect)null);
                    }

                    this.mDeep = null;
                    DeepListener deep = (DeepListener)this.getSelectedView();
                    if(deep != null && deep.canDeep()) {
                        this.mDeep = deep;
                        if(focusRect != null && amountToScroll != null) {
                            this.offsetDescendantRectToMyCoords(amountToScroll, focusRect);
                            this.offsetRectIntoDescendantCoords(this.getSelectedView(), focusRect);
                        }
                        this.mDeep.onFocusDeeped(true, 130, focusRect);
                    }
                }

                if(this.canDraw()) {
                    this.mReset = false;
                    this.performSelect(true);
                } else {
                    this.mReset = true;
                }

                Log.d(TAG, "moveDown6");
                this.amountToCenterScroll(130, nextSelectedPosition);
                return true;
            } else {
                return false;
            }
        }
   }

    public boolean checkState(int keyCode) {
        return this.mLastScrollState == 2 && (keyCode == 21 || keyCode == 22);
    }

    protected int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int center = (this.getHeight() - this.mListPadding.top - this.mListPadding.bottom) / 2 + this.mListPadding.top;
        int listBottom = this.getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        int numChildren = this.getVisibleChildCount();
        int amountToScroll = 0;
        int distanceLeft = this.getLeftScrollDistance();
        View nextSelctedView;
        boolean nextSelectedCenter;
        boolean reset;
        int finalNextSelectedCenter;
        int maxDiff;
        int var17;
        if(direction == 130) {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.getFirstPosition());
            nextSelectedCenter = false;
            reset = false;
            if(nextSelctedView == null) {
                nextSelctedView = this.getLastChild();
                var17 = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                var17 += (nextSelctedView.getHeight() + this.mSpacing) * (nextSelectedPosition - this.getLastPosition());
                reset = false;
            } else {
                var17 = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            finalNextSelectedCenter = var17 - distanceLeft;
            if(finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;
                maxDiff = (nextSelctedView.getHeight() + this.mSpacing) * (this.mItemCount - this.getLastVisiblePosition() - 1);
                maxDiff -= distanceLeft;
                View var18 = this.getLastVisibleChild();
                if(var18.getBottom() > this.getHeight() - this.mListPadding.bottom) {
                    maxDiff += var18.getBottom() - (this.getHeight() - this.mListPadding.bottom);
                }

                if(amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if(reset) {
                    this.reset();
                    this.offsetFocusRect(0, -distanceLeft);
                    if(DEBUG) {
                        Log.i(TAG, "amountToCenterScroll: focus rect = " + this.mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
                    }
                }

                if(amountToScroll > 0) {
                    if(reset) {
                        this.offsetFocusRect(0, -amountToScroll);
                    } else {
                        this.offsetFocusRect(0, nextSelctedView.getHeight() + this.mSpacing - amountToScroll);
                    }

                    if(DEBUG) {
                        Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + this.mFocusRectparams.focusRect());
                    }

                    this.smoothScrollBy(amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if(!reset) {
                        this.offsetFocusRect(0, nextSelctedView.getHeight() + this.mSpacing);
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.offsetFocusRect(0, -distanceLeft);
                this.mIsAnimate = true;
            }

            return amountToScroll;
        } else if(direction != 33) {
            return 0;
        } else {
            nextSelctedView = this.getChildAt(nextSelectedPosition - this.getFirstPosition());
            nextSelectedCenter = false;
            reset = false;
            if(nextSelctedView == null) {
                nextSelctedView = this.getFirstVisibleChild();
                var17 = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                if(nextSelectedPosition >= this.getHeaderViewsCount()) {
                    var17 -= (nextSelctedView.getHeight() + this.mSpacing) * (this.getFirstVisiblePosition() - nextSelectedPosition);
                } else {
                    var17 -= (nextSelctedView.getHeight() + this.mSpacing) * (this.getFirstVisiblePosition() - this.getHeaderViewsCount());

                    for(finalNextSelectedCenter = this.getHeaderViewsCount() - 1; finalNextSelectedCenter >= nextSelectedPosition; --finalNextSelectedCenter) {
                        var17 -= this.getHeaderView(finalNextSelectedCenter).getHeight();
                    }
                }

                reset = false;
            } else {
                var17 = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            finalNextSelectedCenter = var17 - distanceLeft;
            if(finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                maxDiff = 0;
                int start = this.getHeaderViewsCount() - 1;
                if(this.getFirstVisiblePosition() >= this.getHeaderViewsCount()) {
                    maxDiff = (nextSelctedView.getHeight() + this.mSpacing) * (this.getFirstVisiblePosition() - this.getHeaderViewsCount());
                } else {
                    start = this.getFirstVisiblePosition() - 1;
                }

                for(int firstVisibleView = start; firstVisibleView >= 0; --firstVisibleView) {
                    maxDiff += this.getHeaderView(firstVisibleView).getHeight();
                }

                if(maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View var19 = this.getFirstVisibleChild();
                if(var19.getTop() < listTop) {
                    int firstOffset = this.getFirsVisibletChildIndex() - nextSelectedPosition;
                    if(firstOffset > 0) {
                        maxDiff += listTop - var19.getTop() - firstOffset * listTop;
                    } else {
                        maxDiff += listTop - var19.getTop();
                    }
                }

                if(amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if(reset) {
                    this.reset();
                    this.offsetFocusRect(0, -distanceLeft);
                    if(DEBUG) {
                        Log.i(TAG, "amountToCenterScroll: focus rect = " + this.mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
                    }
                } else if(nextSelectedPosition < this.getHeaderViewsCount()) {
                    reset = true;
                    this.resetHeader(nextSelectedPosition);
                    this.offsetFocusRect(0, -distanceLeft);
                }

                if(amountToScroll > 0) {
                    if(reset) {
                        this.offsetFocusRect(0, amountToScroll);
                    } else {
                        this.offsetFocusRect(0, -(nextSelctedView.getHeight() + this.mSpacing - amountToScroll));
                    }

                    if(DEBUG) {
                        Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + this.mFocusRectparams.focusRect());
                    }

                    this.smoothScrollBy(-amountToScroll);
                    this.mIsAnimate = true;
                } else {
                    if(!reset) {
                        this.offsetFocusRect(0, -(nextSelctedView.getHeight() + this.mSpacing));
                    }

                    this.mIsAnimate = true;
                }
            } else {
                this.reset();
                this.offsetFocusRect(0, -distanceLeft);
                this.mIsAnimate = true;
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
                case KEYCODE_DPAD_UP:
                case KEYCODE_PAGE_UP:
                case KEYCODE_MOVE_HOME:
                    return this.getNextSelectedPosition(FOCUS_UP) != -1;
                case KEYCODE_DPAD_DOWN:
                case KEYCODE_PAGE_DOWN:
                case KEYCODE_MOVE_END:
                    return this.getNextSelectedPosition(FOCUS_DOWN) != -1;
                case KEYCODE_DPAD_LEFT:
                case KEYCODE_DPAD_RIGHT:
                    return false;
                case KEYCODE_DPAD_CENTER:
                case KEYCODE_ENTER:
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
                FocusListView.this.fireOnSelected();
            } else {
                //准备布局走异步
                if (this.mFocusSelectionNotifier == null) {
                    this.mFocusSelectionNotifier = new FocusSelectionNotifier();
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
                if (FocusListView.this.getAdapter() != null) {
                    FocusListView.this.post(this);
                }
            } else {
                FocusListView.this.fireOnSelected();
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
