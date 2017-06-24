//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.FlipGridView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
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

public class FocusFlipGridView extends FlipGridView implements FocusListener, DeepListener, ItemListener {
    private final boolean DEBUG = false;
    private static final String TAG = "FocusFlipGridView";
    private final boolean DYNAMIC_ADD_CHILD_VIEW = true;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    private FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();
    private boolean mIsAnimate = true;
    private int mDistance = -1;
    boolean mDeepFocus = false;
    private boolean mIsFirstLayout = true;
    private boolean mFirstAnimDone = true;
    private FocusFlipGridView.OnFocusFlipGridViewListener mOnFocusFlipGridViewListener;
    private FocusFlipGridView.OutAnimationRunnable mOutAnimationRunnable;
    private boolean mNeedResetParam = false;
    private int mOnKeyDirection = 130;
    private Rect mPreFocusRect = new Rect();
    private boolean mCenterFocus = true;
    private ItemSelectedListener mItemSelectedListener;
    private boolean mNeedAutoSearchFocused = true;
    private boolean mAnimAlpha = true;
    private RectF mAlphaRectF;
    private int mAnimAlphaValue = 255;
    private boolean mAimateWhenGainFocusFromLeft = true;
    private boolean mAimateWhenGainFocusFromRight = true;
    private boolean mAimateWhenGainFocusFromUp = true;
    private boolean mAimateWhenGainFocusFromDown = true;
    private boolean mGainFocus;

    public FocusFlipGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public FocusFlipGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FocusFlipGridView(Context context) {
        super(context);
        this.init();
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mItemSelectedListener = listener;
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void initFocused() {
        this.setNeedAutoSearchFocused(true);
        if(this.getHeaderViewsCount() > 0) {
            for(int i = 0; i < this.getHeaderViewsCount(); ++i) {
                View view = ((FixedViewInfo)this.mHeaderViewInfos.get(i)).view;
                if(view instanceof FocusRelativeLayout) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout)view;
                    headerView.notifyLayoutChanged();
                    headerView.clearFocusedIndex();
                }
            }
        }

    }

    public void setNeedAutoSearchFocused(boolean b) {
        this.mNeedAutoSearchFocused = b;
    }

    public void setSelection(int position) {
        if(this.isFlipFinished() && this.getChildCount() > 0 && !this.mIsFirstLayout) {
            View preSelectedView = this.getSelectedView();
            int preSelectedPos = this.getSelectedItemPosition();
            this.setSelectedPositionInt(position);
            this.setNextSelectedPositionInt(position);
            this.mLayoutMode = 9;
            this.lockFlipAnimOnceLayout();
            this.mNeedLayout = true;
            this.mNeedAutoSearchFocused = false;
            this.mNeedResetParam = true;
            this.layoutChildren();
            if(this.isFocused()) {
                this.checkSelected(preSelectedView, preSelectedPos);
            } else {
                View currSelectedView = this.getSelectedView();
                int currSelectedPos = this.getSelectedItemPosition();
                if(preSelectedPos != currSelectedPos) {
                    if(currSelectedView != null) {
                        currSelectedView.setSelected(true);
                    }

                    if(this.mItemSelectedListener != null) {
                        this.mItemSelectedListener.onItemSelected(currSelectedView, currSelectedPos, true, this);
                    }
                }
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(this.getAdapter() != null && this.getChildCount() > 0) {
            View preSelectedView = this.getSelectedView();
            int preSelectedPos = this.getSelectedItemPosition();
            this.mIsAnimate = true;
            boolean hasFocused = this.hasFocus();
            if(!hasFocused) {
                Log.i("FocusFlipGridView", "requestFocus for touch event to onKeyUp");
                this.requestFocus();
            }

            switch(keyCode) {
                case 19:
                    this.mOnKeyDirection = 33;
                    break;
                case 20:
                    this.mOnKeyDirection = 130;
                    break;
                case 21:
                    this.mOnKeyDirection = 17;
                    break;
                case 22:
                    this.mOnKeyDirection = 66;
                    break;
                default:
                    this.mOnKeyDirection = 130;
            }

            int selectedPos = this.getSelectedItemPosition();
            if(selectedPos < this.getHeaderViewsCount()) {
                View ret = this.getSelectedView();
                if(ret instanceof FocusRelativeLayout) {
                    FocusRelativeLayout nextSelectedIndex = (FocusRelativeLayout)ret;
                    boolean currSelectedView = nextSelectedIndex.onKeyDown(keyCode, event);
                    if(currSelectedView) {
                        this.mNeedResetParam = true;
                        this.layoutResetParam();
                        this.checkSelected(preSelectedView, preSelectedPos);
                        return currSelectedView;
                    }

                    nextSelectedIndex.clearSelectedView();
                }
            }

            View nextSelectedIndex1;
            int nextSelectedIndex2;
            int i;
            int var23;
            if(!this.isFlipFinished()) {
                Log.i(TAG,"isFlipFinished:false");
                int headerView;
                int rowDelta;
                int var19;
                int var22;
                switch(keyCode) {
                    case 19:
                        if(!this.lockKeyEvent(19)) {
                            var19 = this.getSelectedItemPosition();
                            var22 = this.mHeaderViewInfos.size();
                            if(var22 > 0) {
                                if(var19 >= var22) {
                                    var23 = var19 - this.getColumnNum();
                                    if(var23 < var22) {
                                        var23 = var22 - 1;
                                    }
                                } else {
                                    var23 = var19 - 1;
                                    if(var23 < 0) {
                                        var23 = -1;
                                    }
                                }
                            } else {
                                var23 = var19 - this.getColumnNum();
                                if(var23 < 0) {
                                    var23 = -1;
                                }
                            }

                            if(var23 != -1) {
                                this.setSelectedPositionInt(var23);
                                this.checkSelectionChanged();
                                headerView = this.getRowEnd(var23);
                                nextSelectedIndex1 = this.getChildAt(headerView - this.getFirstVisiblePosition());
                                if(nextSelectedIndex1 != null) {
                                    this.setReferenceViewInSelectedRow(nextSelectedIndex1);
                                }

                                if(var23 < var22) {
                                    View var28 = ((FixedViewInfo)this.mHeaderViewInfos.get(var23)).view;
                                    if(var28.getParent() == null) {
                                        i = MeasureSpec.makeMeasureSpec(var28.getWidth(), 1073741824);
                                        rowDelta = MeasureSpec.makeMeasureSpec(var28.getHeight(), 1073741824);
                                        var28.measure(i, rowDelta);
                                        var28.layout(0, 0, var28.getWidth(), var28.getHeight());
                                    }

                                    if(var28 instanceof FocusRelativeLayout && var28 instanceof FlipGridViewHeaderOrFooterInterface) {
                                        FocusRelativeLayout var29 = (FocusRelativeLayout)var28;
                                        if(var29.isNeedFocusItem()) {
                                            rowDelta = this.getRemainScrollUpDistance(var23);
                                            this.mPreFocusRect.top += rowDelta;
                                            this.mPreFocusRect.bottom += rowDelta;
                                            var29.onFocusChanged(true, this.mOnKeyDirection, this.mPreFocusRect, (ViewGroup)null);
                                        }

                                        var29.reset();
                                    }
                                }

                                this.amountToCenterScroll(33, var23);
                                this.checkSelected(preSelectedView, preSelectedPos);
                                return true;
                            }
                        }

                        return true;
                    case 20:
                        if(!this.lockKeyEvent(20)) {
                            var19 = this.getSelectedItemPosition() + this.getColumnNum() < this.mItemCount?this.getSelectedItemPosition() + this.getColumnNum():-1;
                            boolean var20 = false;
                            var22 = this.getRowStart(this.getSelectedItemPosition());
                            if(var19 == -1 && var22 + this.getColumnNum() < this.mItemCount) {
                                var19 = this.mItemCount - 1;
                                var20 = true;
                            }

                            if(var19 != -1) {
                                this.setSelectedPositionInt(var19);
                                this.checkSelectionChanged();
                                headerView = this.getRowEnd(var19);
                                nextSelectedIndex1 = this.getChildAt(headerView - this.getFirstVisiblePosition());
                                if(nextSelectedIndex1 != null) {
                                    this.setReferenceViewInSelectedRow(nextSelectedIndex1);
                                }

                                this.amountToCenterScroll(130, var19);
                                if(var20) {
                                    nextSelectedIndex2 = this.getRowStart(this.mFirstPosition);
                                    if(nextSelectedIndex2 < this.mHeaderViewInfos.size()) {
                                        nextSelectedIndex2 = this.mHeaderViewInfos.size();
                                    }

                                    if(nextSelectedIndex2 < this.mFirstPosition) {
                                        nextSelectedIndex2 += this.getColumnNum();
                                    }

                                    i = this.getRowStart(var19);
                                    rowDelta = var19 - i;
                                    View existItem = this.getChildAt(nextSelectedIndex2 + rowDelta - this.mFirstPosition);
                                    if(existItem != null && existItem instanceof ItemListener) {
                                        ItemListener item = (ItemListener)existItem;
                                        FocusRectParams rectParms = item.getFocusParams();
                                        if(rectParms != null) {
                                            Rect rect = rectParms.focusRect();
                                            this.offsetDescendantRectToMyCoords(existItem, rect);
                                            this.offsetFocusRectLeftAndRight(rect.left, rect.right);
                                        }
                                    }
                                }

                                this.checkSelected(preSelectedView, preSelectedPos);
                                return true;
                            }
                        }

                        return true;
                    case 21:
                        var19 = this.getSelectedItemPosition() - 1;
                        this.setSelectedPositionInt(var19);
                        this.checkSelectionChanged();
                        this.amountToCenterScroll(17, var19);
                        this.checkSelected(preSelectedView, preSelectedPos);
                        return true;
                    case 22:
                        var19 = this.getSelectedItemPosition() + 1;
                        this.setSelectedPositionInt(var19);
                        this.checkSelectionChanged();
                        this.amountToCenterScroll(66, var19);
                        this.checkSelected(preSelectedView, preSelectedPos);
                        return true;
                }
            } else {
                Log.i(TAG,"isFlipFinished:true");
                byte soundEffectType = SoundEffectConstants.NAVIGATION_LEFT;
                if(selectedPos < this.getHeaderViewsCount() && keyCode == 20) {
                    View var25 = this.getSelectedView();
                    FocusRelativeLayout var26 = null;
                    if(this.checkIsCanDown()) {
                        if(selectedPos == this.getHeaderViewsCount() - 1) {
                            if(var25 != null && var25 instanceof FocusRelativeLayout) {
                                var26 = (FocusRelativeLayout)var25;
                                nextSelectedIndex1 = this.focusSearch(var26.getSelectedView(), 130);
                                if(nextSelectedIndex1 != null) {
                                    nextSelectedIndex2 = -1;

                                    for(i = 0; i < this.getChildCount(); ++i) {
                                        if(this.getChildAt(i).equals(nextSelectedIndex1)) {
                                            nextSelectedIndex2 = i + this.getFirstVisiblePosition();
                                            break;
                                        }
                                    }

                                    if(nextSelectedIndex2 >= 0) {
                                        this.mNeedResetParam = true;
                                        this.setAdapterSelection(nextSelectedIndex2);
                                        this.checkSelected(preSelectedView, preSelectedPos);
                                        this.playSoundEffect(soundEffectType);
                                        return true;
                                    }
                                }
                            }
                        } else {
                            int var27 = selectedPos++;
                            if(var27 >= 0 && var27 < this.getHeaderViewsCount()) {
                                this.mNeedResetParam = true;
                                this.setAdapterSelection(var27);
                                this.checkSelected(preSelectedView, preSelectedPos);
                                this.playSoundEffect(soundEffectType);
                                return true;
                            }
                        }
                    }
                } else if(selectedPos <= this.getHeaderViewsCount() && keyCode == 19) {
                    var23 = selectedPos--;
                    if(var23 >= 0 && var23 < this.getHeaderViewsCount()) {
                        this.mNeedResetParam = true;
                        this.setAdapterSelection(var23);
                        this.checkSelected(preSelectedView, preSelectedPos);
                        this.playSoundEffect(soundEffectType);
                        return true;
                    }
                }
            }

            this.mNeedResetParam = true;
            boolean var24 = super.onKeyDown(keyCode, event);
            this.checkSelected(preSelectedView, preSelectedPos);
            return var24;
        } else {
            return false;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean hasFocused = this.hasFocus();
        if(!hasFocused) {
            Log.i("FocusFlipGridView", "requestFocus for touch event to onKeyUp");
            this.requestFocus();
        }

        if(this.getAdapter() != null && this.getChildCount() > 0) {
            int selectedPos = this.getSelectedItemPosition();
            if(selectedPos < this.getHeaderViewsCount()) {
                View view = this.getSelectedView();
                if(view instanceof FocusRelativeLayout) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout)view;
                    boolean headerViewRet = headerView.onKeyUp(keyCode, event);
                    return headerViewRet;
                }
            }

            return super.onKeyUp(keyCode, event);
        } else {
            return false;
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mGainFocus = gainFocus;
        if(this.mNeedAutoSearchFocused) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            if(gainFocus) {
                this.mNeedAutoSearchFocused = false;
            }
        } else {
            if(this.getOnFocusChangeListener() != null) {
                this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
            }

            if(gainFocus) {
                this.setSelection(this.getSelectedItemPosition());
            }
        }

        if(gainFocus && this.getChildCount() > 0) {
            int selectedPos = this.getSelectedItemPosition();
            if(selectedPos < this.getHeaderViewsCount()) {
                View view = this.getSelectedView();
                if(view instanceof FocusRelativeLayout) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout)view;
                    headerView.onFocusChanged(true, direction, previouslyFocusedRect, this);
                }
            }

            this.mNeedResetParam = true;
            this.layoutResetParam();
            this.performSelect(true);
        } else {
            this.performSelect(false);
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

    protected int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int verticalSpacing = this.getVerticalSpacing();
        int center = this.getHeight() / 2;
        int amountToScroll = 0;
        int distanceLeft = this.getFlipColumnFirstItemLeftMoveDistance(nextSelectedPosition);
        View lastVisiblePos;
        boolean firstVisiblePos;
        int selectedRowStart;
        int columnDelta;
        int delta;
        int offset1;
        int var22;
        switch(direction) {
            case 17:
            case 66:
                int var25 = this.getLastVisiblePosition();
                var22 = this.getFirstVisiblePosition();
                int var24;
                View var28;
                if(nextSelectedPosition > var25) {
                    var24 = this.getRowStart(var25);
                    if(var25 - var24 < this.getColumnNum() - 1) {
                        var24 = this.getRowStart(var25 - this.getColumnNum());
                    }

                    selectedRowStart = this.getRowStart(nextSelectedPosition);
                    columnDelta = nextSelectedPosition - selectedRowStart;
                    var28 = this.getChildAt(var24 + columnDelta - var22);
                    delta = (selectedRowStart - var24) / this.getColumnNum();
                    offset1 = (var28.getHeight() + verticalSpacing) * delta;
                    offset1 += this.getFlipItemLeftMoveDistance(var24 + columnDelta, 0);
                    this.resetParam(var28, offset1);
                } else if(nextSelectedPosition < var22) {
                    var24 = this.getRowStart(var22);
                    if(var24 != var22) {
                        var24 = this.getRowStart(var22 + this.getColumnNum());
                    }

                    selectedRowStart = this.getRowStart(nextSelectedPosition);
                    columnDelta = nextSelectedPosition - selectedRowStart;
                    var28 = this.getChildAt(var24 + columnDelta - var22);
                    delta = (var24 - selectedRowStart) / this.getColumnNum();
                    offset1 = -((var28.getHeight() + verticalSpacing) * delta);
                    offset1 += this.getFlipItemLeftMoveDistance(var24 + columnDelta, 0);
                    this.resetParam(var28, offset1);
                } else {
                    var24 = this.getFlipItemLeftMoveDistance(nextSelectedPosition, 0);
                    this.resetParam(this.getSelectedView(), var24);
                }
            default:
                return 0;
            case 33:
                lastVisiblePos = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
                firstVisiblePos = false;
                View var23 = this.getChildAt(0);
                selectedRowStart = var23.getTop();
                if(var23 instanceof GridViewHeaderViewExpandDistance) {
                    selectedRowStart += ((GridViewHeaderViewExpandDistance)var23).getUpExpandDistance();
                }

                boolean var26 = false;
                boolean var27 = false;
                int left;
                int headerCount;
                int secondIndex;
                int var31;
                if(lastVisiblePos == null) {
                    lastVisiblePos = this.getChildAt(0);
                    delta = this.getRowStart(this.getFirstVisiblePosition());
                    offset1 = this.getRowStart(nextSelectedPosition);
                    left = this.mHeaderViewInfos.size();
                    if(left <= 0) {
                        headerCount = (delta - offset1) / this.getColumnNum();
                        var22 = lastVisiblePos.getTop() + lastVisiblePos.getHeight() / 2;
                        var22 -= (lastVisiblePos.getHeight() + verticalSpacing) * headerCount;
                    } else if(offset1 >= left) {
                        headerCount = (delta - offset1) / this.getColumnNum();
                        var22 = lastVisiblePos.getTop() + lastVisiblePos.getHeight() / 2;
                        var22 -= (lastVisiblePos.getHeight() + verticalSpacing) * headerCount;
                    } else {
                        headerCount = (delta - left) / this.getColumnNum();
                        var22 = lastVisiblePos.getTop() - (lastVisiblePos.getHeight() + verticalSpacing) * headerCount;

                        int topOffset;
                        for(secondIndex = offset1; secondIndex < left; ++secondIndex) {
                            View childLeftDistance = ((FixedViewInfo)this.mHeaderViewInfos.get(secondIndex)).view;
                            topOffset = childLeftDistance.getHeight();
                            if(childLeftDistance instanceof GridViewHeaderViewExpandDistance) {
                                topOffset -= ((GridViewHeaderViewExpandDistance)childLeftDistance).getUpExpandDistance();
                                topOffset -= ((GridViewHeaderViewExpandDistance)childLeftDistance).getDownExpandDistance();
                            }

                            var22 -= verticalSpacing + topOffset / 2;
                        }

                        View var29 = ((FixedViewInfo)this.mHeaderViewInfos.get(0)).view;
                        if(var29 != null && var29 instanceof ItemListener) {
                            ItemListener var30 = (ItemListener)var29;
                            if(this.mFocusRectparams == null) {
                                this.mFocusRectparams = new FocusRectParams();
                            }

                            this.mFocusRectparams.set(var30.getFocusParams());
                        }

                        var31 = this.getHeaderViewLeft(0);
                        topOffset = this.mListPadding.top;
                        if(var29 instanceof GridViewHeaderViewExpandDistance) {
                            topOffset -= ((GridViewHeaderViewExpandDistance)var29).getUpExpandDistance();
                        }

                        int secondIndex1 = this.getHeaderViewSecondIndex(var29);
                        if(nextSelectedPosition >= this.getFirstVisiblePosition() && nextSelectedPosition <= this.getLastVisiblePosition()) {
                            int childLeftDistance1 = this.getFlipItemLeftMoveDistance(nextSelectedPosition, secondIndex1);
                            topOffset += distanceLeft - childLeftDistance1;
                        }

                        this.offsetFocusRect(var31, var31, topOffset, topOffset);
                        var27 = true;
                    }

                    var26 = false;
                } else {
                    if(lastVisiblePos instanceof GridViewHeaderViewExpandDistance) {
                        delta = ((GridViewHeaderViewExpandDistance)lastVisiblePos).getUpExpandDistance();
                        offset1 = ((GridViewHeaderViewExpandDistance)lastVisiblePos).getDownExpandDistance();
                        var22 = lastVisiblePos.getTop() + delta + (lastVisiblePos.getHeight() - delta - offset1) / 2;
                    } else {
                        var22 = lastVisiblePos.getTop() + lastVisiblePos.getHeight() / 2;
                    }

                    var26 = true;
                }

                delta = var22 + distanceLeft;
                if(delta < center) {
                    amountToScroll = center - delta;
                    offset1 = this.getTopLeftDistance(this.getFirstVisiblePosition());
                    offset1 = this.mListPadding.top - (selectedRowStart - offset1);
                    if(offset1 < 0) {
                        offset1 = 0;
                    }

                    left = this.getFlipColumnFirstItemLeftMoveDistance(this.getFirstVisiblePosition());
                    offset1 -= left;
                    if(offset1 < 0) {
                        offset1 = 0;
                    }

                    if(amountToScroll > offset1) {
                        amountToScroll = offset1;
                    }

                    if(var26) {
                        headerCount = this.mHeaderViewInfos.size();
                        if(headerCount > 0) {
                            if(nextSelectedPosition < headerCount) {
                                secondIndex = this.getHeaderViewSecondIndex(lastVisiblePos);
                                if(secondIndex >= 0) {
                                    var31 = this.getFlipItemLeftMoveDistance(nextSelectedPosition, secondIndex);
                                    this.resetParam(lastVisiblePos, var31);
                                }
                            } else {
                                this.resetParam(lastVisiblePos, 0);
                                this.offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                            }
                        } else {
                            this.resetParam(lastVisiblePos, 0);
                            this.offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                        }
                    }

                    if(amountToScroll > 0) {
                        if(!var27) {
                            if(var26) {
                                this.offsetFocusRectTopAndBottom(amountToScroll, amountToScroll);
                            } else {
                                this.offsetFocusRectTopAndBottom(-(lastVisiblePos.getHeight() + verticalSpacing - amountToScroll), -(lastVisiblePos.getHeight() + verticalSpacing - amountToScroll));
                            }
                        }

                        this.startRealScroll(amountToScroll);
                        this.mIsAnimate = true;
                    } else {
                        if(!var26 && !var27) {
                            this.offsetFocusRectTopAndBottom(-(lastVisiblePos.getHeight() + verticalSpacing), -(lastVisiblePos.getHeight() + verticalSpacing));
                        }

                        this.mIsAnimate = true;
                    }
                } else {
                    this.resetParam(this.getSelectedView(), 0);
                    this.mIsAnimate = true;
                }

                this.mPreFocusRect.set(this.mFocusRectparams.focusRect());
                return amountToScroll;
            case 130:
                lastVisiblePos = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
                firstVisiblePos = false;
                boolean offset = false;
                selectedRowStart = this.getChildAt(this.getChildCount() - 1).getBottom();
                int visibleView;
                if(lastVisiblePos == null) {
                    lastVisiblePos = this.getChildAt(this.getChildCount() - 1);
                    var22 = lastVisiblePos.getBottom() - lastVisiblePos.getHeight() / 2;
                    columnDelta = this.getRowStart(this.getLastVisiblePosition());
                    visibleView = this.getRowStart(nextSelectedPosition);
                    delta = (visibleView - columnDelta) / this.getColumnNum();
                    var22 += (lastVisiblePos.getHeight() + verticalSpacing) * delta;
                    offset = false;
                } else {
                    var22 = lastVisiblePos.getBottom() - lastVisiblePos.getHeight() / 2;
                    offset = true;
                }

                columnDelta = var22 + distanceLeft;
                if(columnDelta > center) {
                    amountToScroll = columnDelta - center;
                    visibleView = this.getBottomLeftDistance(this.getLastVisiblePosition());
                    visibleView = selectedRowStart + visibleView + this.mListPadding.bottom - this.getHeight();
                    if(visibleView < 0) {
                        visibleView = 0;
                    }

                    delta = this.getFlipColumnFirstItemLeftMoveDistance(this.getLastVisiblePosition());
                    visibleView += delta;
                    if(amountToScroll > visibleView) {
                        amountToScroll = visibleView;
                    }

                    if(offset) {
                        this.resetParam(lastVisiblePos, 0);
                        this.offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                    }

                    if(amountToScroll > 0) {
                        if(offset) {
                            this.offsetFocusRectTopAndBottom(-amountToScroll, -amountToScroll);
                        } else {
                            this.offsetFocusRectTopAndBottom(lastVisiblePos.getHeight() + verticalSpacing - amountToScroll, lastVisiblePos.getHeight() + verticalSpacing - amountToScroll);
                        }

                        this.startRealScroll(-amountToScroll);
                        this.mIsAnimate = true;
                    } else {
                        if(!offset) {
                            this.offsetFocusRectTopAndBottom(lastVisiblePos.getHeight() + verticalSpacing, lastVisiblePos.getHeight() + verticalSpacing);
                        }

                        this.mIsAnimate = true;
                    }
                } else {
                    this.resetParam(this.getSelectedView(), 0);
                    this.mIsAnimate = true;
                }

                return amountToScroll;
        }
    }

    protected void adjustForBottomFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        int top = childInSelectedRow.getTop();
        if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            top += ((GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
        }

        int bottom = childInSelectedRow.getBottom();
        if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            bottom -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
        }

        int tempTopSelectionPixel;
        int tempBottomSelectionPixel;
        int spaceAbove;
        int spaceBelow;
        if(this.mCenterFocus) {
            spaceAbove = this.getHeight() / 2;
            spaceBelow = childInSelectedRow.getHeight();
            if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
                spaceBelow -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
                spaceBelow -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
            }

            tempTopSelectionPixel = spaceAbove - spaceBelow / 2;
            tempBottomSelectionPixel = spaceAbove + spaceBelow / 2;
        } else {
            tempTopSelectionPixel = topSelectionPixel;
            tempBottomSelectionPixel = bottomSelectionPixel;
        }

        if(bottom > tempBottomSelectionPixel) {
            spaceAbove = top - tempTopSelectionPixel;
            spaceBelow = bottom - tempBottomSelectionPixel;
            int offset = Math.min(spaceAbove, spaceBelow);
            if(this.mCenterFocus) {
                int maxDiff = this.getBottomLeftDistance(this.getSelectedItemPosition());
                maxDiff = bottom + maxDiff + this.mListPadding.bottom - this.getHeight();
                if(maxDiff < 0) {
                    maxDiff = 0;
                }

                if(offset > maxDiff) {
                    offset = maxDiff;
                }
            }

            this.offsetChildrenTopAndBottom(-offset);
        }

    }

    protected void adjustForTopFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        int top = childInSelectedRow.getTop();
        if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            top += ((GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
        }

        int bottom = childInSelectedRow.getBottom();
        if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            bottom -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
        }

        int tempTopSelectionPixel;
        int tempBottomSelectionPixel;
        int spaceAbove;
        int spaceBelow;
        if(this.mCenterFocus) {
            spaceAbove = this.getHeight() / 2;
            spaceBelow = childInSelectedRow.getHeight();
            if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
                spaceBelow -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
                spaceBelow -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
            }

            tempTopSelectionPixel = spaceAbove - spaceBelow / 2;
            tempBottomSelectionPixel = spaceAbove + spaceBelow / 2;
        } else {
            tempTopSelectionPixel = topSelectionPixel;
            tempBottomSelectionPixel = bottomSelectionPixel;
        }

        if(top < tempTopSelectionPixel) {
            spaceAbove = tempTopSelectionPixel - top;
            spaceBelow = tempBottomSelectionPixel - bottom;
            int offset = Math.min(spaceAbove, spaceBelow);
            if(this.mCenterFocus) {
                int maxDiff = this.getTopLeftDistance(this.getSelectedItemPosition());
                maxDiff = this.mListPadding.top - (top - maxDiff);
                if(maxDiff < 0) {
                    maxDiff = 0;
                }

                if(offset > maxDiff) {
                    offset = maxDiff;
                }
            }

            this.offsetChildrenTopAndBottom(offset);
        }

    }

    protected void layoutChildren() {
        if(!this.isFlipFinished()) {
            Log.i("FocusFlipGridView", "layoutChildren flip is running can not layout");
        } else {
            super.layoutChildren();
            this.mClipFocusRect.set(0, 0, this.getWidth(), this.getHeight());
        }
    }

    protected void onLayoutChildrenDone() {
        boolean isFirst = false;
        if(this.mIsFirstLayout) {
            this.mIsFirstLayout = false;
            isFirst = true;
        }

        if(this.mOnFocusFlipGridViewListener != null) {
            this.mOnFocusFlipGridViewListener.onLayoutDone(isFirst);
        }

        this.resetFocusParam();
    }

    public void offsetChildrenTopAndBottom(int offset) {
        super.offsetChildrenTopAndBottom(offset);
    }

    protected void onFlipItemRunnableRunning(float moveRatio, View itemView, int index) {
        if(this.mAnimAlpha && !this.mFirstAnimDone && itemView != null) {
            this.mAnimAlphaValue = (int)(moveRatio * 255.0F);
            this.setAlpha(moveRatio);
        }

        super.onFlipItemRunnableRunning(moveRatio, itemView, index);
    }

    protected void dispatchDraw(Canvas canvas) {
        if(this.mAnimAlpha) {
            if(this.mAlphaRectF == null) {
                this.mAlphaRectF = new RectF(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
            }

            super.dispatchDraw(canvas);
        } else {
            super.dispatchDraw(canvas);
        }

    }

    protected void onFlipItemRunnableFinished() {
        if(!this.mFirstAnimDone) {
            this.setFocusable(true);
            this.mFirstAnimDone = true;
        }

        super.onFlipItemRunnableFinished();
    }

    public Params getParams() {
        if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public void getFocusedRect(Rect r) {
        if(this.hasFocus()) {
            super.getFocusedRect(r);
        } else {
            this.getDrawingRect(r);
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

    public FocusRectParams getFocusParams() {
        View v = this.getSelectedView();
        if(v == null) {
            Rect r = new Rect();
            this.getFocusedRect(r);
            this.mFocusRectparams.set(r, 0.5F, 0.5F);
            return this.mFocusRectparams;
        } else {
            if(this.mFocusRectparams == null || this.isScrolling()) {
                this.resetFocusParam();
            }

            return this.mFocusRectparams;
        }
    }

    public boolean canDraw() {
        return this.getSelectedView() != null;
    }

    public boolean isAnimate() {
        return this.mIsAnimate;
    }

    public ItemListener getItem() {
        View view = this.getSelectedView();
        if(view instanceof FocusRelativeLayout) {
            FocusRelativeLayout v1 = (FocusRelativeLayout)view;
            return v1.getItem();
        } else {
            View v = this.getSelectedView();
            if(v == null) {
                Log.e("FocusFlipGridView", "getItem: getSelectedView is null! this:" + this.toString());
            }

            return (ItemListener)v;
        }
    }

    public boolean isScrolling() {
        return this.isFliping();
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if(this.getAdapter() != null && this.getChildCount() > 0) {
            int selectedPos = this.getSelectedItemPosition();
            if(selectedPos < this.getHeaderViewsCount()) {
                View headerCount = this.getSelectedView();
                if(headerCount instanceof FocusRelativeLayout) {
                    if(!this.isFlipFinished()) {
                        return false;
                    }

                    FocusRelativeLayout isCan = (FocusRelativeLayout)headerCount;
                    boolean selected = isCan.preOnKeyDown(keyCode, event);
                    if(selected) {
                        return true;
                    }
                }
            }

            int adapterIndex;
            int selected1;
            switch(keyCode) {
                case KEYCODE_DPAD_UP:
                case KEYCODE_PAGE_UP:
                case KEYCODE_MOVE_HOME:
                    int headerCount1 = this.mHeaderViewInfos.size();
                    if(headerCount1 > 0) {
                        if(selectedPos > 0) {
                            int isCan2 = this.getRowStart(selectedPos);
                            if(isCan2 == headerCount1) {
                                View selected2 = ((FixedViewInfo)this.mHeaderViewInfos.get(headerCount1 - 1)).view;
                                if(!selected2.isFocusable()) {
                                    return false;
                                }
                            }

                            return true;
                        }

                        if(this.mOnFocusFlipGridViewListener != null) {
                            this.mOnFocusFlipGridViewListener.onReachGridViewTop();
                        }

                        return false;
                    } else {
                        if(this.getSelectedItemPosition() < this.getColumnNum()) {
                            if(this.mOnFocusFlipGridViewListener != null) {
                                this.mOnFocusFlipGridViewListener.onReachGridViewTop();
                            }

                            return false;
                        }

                        return true;
                    }
                case KEYCODE_DPAD_DOWN:
                case KEYCODE_PAGE_DOWN:
                case KEYCODE_MOVE_END:
                    boolean isCan1 = this.checkIsCanDown();
                    if(!isCan1) {
                        return false;
                    }
                    return this.getSelectedItemPosition() < this.mItemCount - 1;
                case KEYCODE_DPAD_LEFT:
                    selected1 = this.getSelectedItemPosition();
                    if(selected1 < this.getHeaderViewsCount()) {
                        return false;
                    }

                    adapterIndex = selected1 - this.getHeaderViewsCount();
                    return adapterIndex % this.getColumnNum() != 0;
                case KEYCODE_DPAD_RIGHT:
                    selected1 = this.getSelectedItemPosition();
                    if(selected1 < this.getHeaderViewsCount()) {
                        return false;
                    } else {
                        if(selected1 >= this.mItemCount - 1) {
                            return false;
                        }

                        adapterIndex = selected1 - this.getHeaderViewsCount() + 1;
                        return adapterIndex % this.getColumnNum() != 0;
                    }
                case KEYCODE_DPAD_CENTER:
                case KEYCODE_ENTER:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean hasFocus() {
        return super.hasFocus() || this.mGainFocus;
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
        return false;
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
        View view = this.getSelectedView();
        Log.v("FocusFlipGridView", "FocusFlipGridView.onItemClick.getSelectedView = " + view);
        if(this.getSelectedView() != null) {
            this.performItemClick(this.getSelectedView(), this.getSelectedItemPosition(), 0L);
        }

    }

    public void setOnFocusFlipGridViewListener(FocusFlipGridView.OnFocusFlipGridViewListener listener) {
        this.mOnFocusFlipGridViewListener = listener;
    }

    public void stopOutAnimation() {
        Log.i("FocusFlipGridView", "stopOutAnimation");
        this.mOutAnimationRunnable.stop();
    }

    public void startOutAnimation() {
        this.mOutAnimationRunnable.start();
    }

    public void startInAnimation() {
        if(this.mFirstAnimDone) {
            this.mFirstAnimDone = false;
            this.setFocusable(false);
            int count = this.getChildCount();
            int delta = this.getHeight() / 2;
            if(this.mAnimAlpha) {
                this.setAlpha(0.0F);
                this.mAnimAlphaValue = 0;
            }

            for(int i = 0; i < count; ++i) {
                View childView = this.getChildAt(i);
                childView.offsetTopAndBottom(delta);
            }

            this.startFlip(-delta);
        }

    }

    private void init() {
        this.mOutAnimationRunnable = new FocusFlipGridView.OutAnimationRunnable();
    }

    private int getHeaderViewLeft(int index) {
        int headerCount = this.mHeaderViewInfos.size();
        if(index < headerCount) {
            View headerView = ((FixedViewInfo)this.mHeaderViewInfos.get(index)).view;
            int childLeft = this.mListPadding.left;
            int width = this.getWidth() - this.mListPadding.left - this.mListPadding.right;
            boolean absoluteGravity = true;
            switch(1) {
                case 1:
                    childLeft += (width - headerView.getWidth()) / 2;
                case 2:
                case 3:
                case 4:
                default:
                    break;
                case 5:
                    childLeft += width - headerView.getWidth();
            }

            return childLeft;
        } else {
            return 0;
        }
    }

    private int getRowEnd(int position) {
        int rowEnd = position;
        int headerCount = this.getHeaderViewsCount();
        if(position >= headerCount) {
            if(position < this.mItemCount - this.getFooterViewsCount()) {
                int newPosition = position - headerCount;
                int left = this.getColumnNum() - (newPosition % this.getColumnNum() + 1);
                rowEnd = newPosition + left + headerCount;
                if(rowEnd >= this.mItemCount) {
                    rowEnd = this.mItemCount - 1;
                }
            } else {
                rowEnd = position;
            }
        }

        return rowEnd;
    }

    private void checkSelected(View preSelectedView, int preSelectedPos) {
        View currSelectedView = this.getSelectedView();
        int currSelectedPos = this.getSelectedItemPosition();
        if(preSelectedPos != currSelectedPos) {
            if(preSelectedPos >= 0 && preSelectedView != null) {
                preSelectedView.setSelected(false);
                if(this.mItemSelectedListener != null) {
                    this.mItemSelectedListener.onItemSelected(preSelectedView, preSelectedPos, false, this);
                }
            }

            if(currSelectedView != null) {
                currSelectedView.setSelected(true);
            }

            if(this.mItemSelectedListener != null) {
                this.mItemSelectedListener.onItemSelected(currSelectedView, currSelectedPos, true, this);
            }
        }

    }

    private void performSelect(boolean select) {
        View selectedView = this.getSelectedView();
        if(selectedView != null) {
            selectedView.setSelected(select);
            if(this.mItemSelectedListener != null) {
                this.mItemSelectedListener.onItemSelected(selectedView, this.getSelectedItemPosition(), select, this);
            }
        }

    }

    protected void resetFocusParam() {
        this.mNeedResetParam = true;
        this.layoutResetParam();
    }

    private void layoutResetParam() {
        if(this.mNeedResetParam) {
            this.mNeedResetParam = false;
            int scrollOffset = this.mScrollOffset;
            int selectedPos = this.getSelectedItemPosition();
            if(selectedPos < this.getHeaderViewsCount()) {
                View leftMove = this.getSelectedView();
                if(leftMove instanceof FocusRelativeLayout && leftMove instanceof FlipGridViewHeaderOrFooterInterface) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout)leftMove;
                    if(headerView.isNeedFocusItem() && (this.hasFocus() || this.hasDeepFocus())) {
                        headerView.onFocusChanged(true, this.mOnKeyDirection, this.mPreFocusRect, this);
                    }

                    FlipGridViewHeaderOrFooterInterface headerInt = (FlipGridViewHeaderOrFooterInterface)leftMove;
                    int validChildCount = headerInt.getHorCount() * headerInt.getVerticalCount();
                    int secondIndex = -1;

                    int leftMove1;
                    for(leftMove1 = 0; leftMove1 < validChildCount; ++leftMove1) {
                        View childView = headerInt.getView(leftMove1);
                        if(childView != null && childView.equals(headerView.getSelectedView())) {
                            secondIndex = leftMove1;
                            break;
                        }
                    }

                    if(secondIndex >= 0) {
                        leftMove1 = this.getFlipItemLeftMoveDistance(selectedPos, secondIndex);
                        if(scrollOffset == 0) {
                            scrollOffset = leftMove1;
                        }

                        headerView.reset();
                    }
                }
            } else {
                int var10 = this.getFlipItemLeftMoveDistance(selectedPos, 0);
                if(scrollOffset == 0) {
                    scrollOffset = var10;
                }
            }

            this.resetParam(this.getSelectedView(), scrollOffset);
        }
    }

    private void resetParam(View view, int offset) {
        if(view != null && view instanceof ItemListener) {
            ItemListener rect1 = (ItemListener)view;
            if(this.mFocusRectparams == null) {
                this.mFocusRectparams = new FocusRectParams();
            }

            this.mFocusRectparams.set(rect1.getFocusParams());
        } else {
            if(view == null || !(view instanceof FocusListener)) {
                Log.w("FocusFlipGridView", "resetParam error view=" + view + " mItemCount=" + this.mItemCount + " mFirstIndex=" + this.mFirstPosition + " mSelectedIndex=" + this.mSelectedPosition);
                return;
            }

            FocusListener rect = (FocusListener)view;
            if(this.mFocusRectparams == null) {
                this.mFocusRectparams = new FocusRectParams();
            }

            this.mFocusRectparams.set(rect.getFocusParams());
        }

        if(this.mFocusRectparams != null) {
            Rect rect2 = this.mFocusRectparams.focusRect();
            if(rect2 != null) {
                this.offsetFocusRectTopAndBottom(offset, offset);
            }

            this.offsetDescendantRectToMyCoords(view, this.mFocusRectparams.focusRect());
            this.mPreFocusRect.set(this.mFocusRectparams.focusRect());
        }

    }

    private int getTopLeftDistance(int itemIndex) {
        View itemView = this.getChildAt(itemIndex - this.getFirstVisiblePosition());
        if(itemView == null) {
            return 2147483647;
        } else {
            int bottomDistance = 0;
            int columnCount = this.getColumnNum();
            int headerCount = this.mHeaderViewInfos.size();
            int footerCount = this.mFooterViewInfos.size();
            int footerStart;
            if(itemIndex < headerCount) {
                for(footerStart = itemIndex - 1; footerStart >= 0; --footerStart) {
                    View preColumnIndex = ((FixedViewInfo)this.mHeaderViewInfos.get(footerStart)).view;
                    bottomDistance += preColumnIndex.getHeight() + this.getVerticalSpacing();
                    if(preColumnIndex instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)preColumnIndex).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)preColumnIndex).getDownExpandDistance();
                    }
                }
            } else {
                int i;
                int var13;
                if(itemIndex >= headerCount && itemIndex < this.mItemCount - footerCount) {
                    footerStart = itemIndex - (itemIndex - this.mHeaderViewInfos.size()) % columnCount - 1;
                    var13 = this.mHeaderViewInfos.size();
                    int var14;
                    if(footerStart >= var13) {
                        var14 = (footerStart - var13 + 1) / columnCount;
                        i = (footerStart - var13 + 1) % columnCount;
                        if(i > 0) {
                            ++var14;
                        }

                        if(var14 > 0) {
                            bottomDistance += (itemView.getHeight() + this.getVerticalSpacing()) * var14;
                        }
                    }

                    for(var14 = headerCount - 1; var14 >= 0; --var14) {
                        View var16 = ((FixedViewInfo)this.mHeaderViewInfos.get(var14)).view;
                        bottomDistance += var16.getHeight() + this.getVerticalSpacing();
                        if(var16 instanceof GridViewHeaderViewExpandDistance) {
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var16).getUpExpandDistance();
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var16).getDownExpandDistance();
                        }
                    }
                } else {
                    footerStart = itemIndex - (this.mItemCount - footerCount);
                    if(footerStart > 0) {
                        for(var13 = footerStart - 1; var13 >= 0; --var13) {
                            View firstAdapterIndex = ((FixedViewInfo)this.mFooterViewInfos.get(var13)).view;
                            bottomDistance += firstAdapterIndex.getHeight() + this.getVerticalSpacing();
                            if(firstAdapterIndex instanceof GridViewHeaderViewExpandDistance) {
                                bottomDistance -= ((GridViewHeaderViewExpandDistance)firstAdapterIndex).getUpExpandDistance();
                                bottomDistance -= ((GridViewHeaderViewExpandDistance)firstAdapterIndex).getDownExpandDistance();
                            }
                        }
                    }

                    var13 = this.mItemCount - footerCount - 1;
                    if(var13 >= headerCount) {
                        i = (var13 - headerCount + 1) / columnCount;
                        int headerView = (var13 - headerCount + 1) % columnCount;
                        if(headerView > 0) {
                            ++i;
                        }

                        if(i > 0) {
                            View adatperView = this.getChildAt(var13 - this.mFirstPosition);
                            if(adatperView == null) {
                                return 2147483647;
                            }

                            bottomDistance += (adatperView.getHeight() + this.getVerticalSpacing()) * i;
                        }
                    }

                    for(i = headerCount - 1; i >= 0; --i) {
                        View var15 = ((FixedViewInfo)this.mHeaderViewInfos.get(i)).view;
                        bottomDistance += var15.getHeight() + this.getVerticalSpacing();
                        if(var15 instanceof GridViewHeaderViewExpandDistance) {
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var15).getUpExpandDistance();
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var15).getDownExpandDistance();
                        }
                    }
                }
            }

            return bottomDistance;
        }
    }

    private int getBottomLeftDistance(int itemIndex) {
        View itemView = this.getChildAt(itemIndex - this.getFirstVisiblePosition());
        if(itemView == null) {
            return 2147483647;
        } else {
            int bottomDistance = 0;
            int columnCount = this.getColumnNum();
            int headerCount = this.mHeaderViewInfos.size();
            int footerCount = this.mFooterViewInfos.size();
            int footerStart;
            int footerView;
            int footerView1;
            int var12;
            View var13;
            if(itemIndex < headerCount) {
                for(footerStart = itemIndex + 1; footerStart < headerCount; ++footerStart) {
                    View i = ((FixedViewInfo)this.mHeaderViewInfos.get(footerStart)).view;
                    bottomDistance += i.getHeight() + this.getVerticalSpacing();
                    if(i instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)i).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)i).getDownExpandDistance();
                    }
                }

                var12 = this.mItemCount - this.mFooterViewInfos.size() - 1;
                if(headerCount <= var12) {
                    footerView = (var12 - headerCount + 1) / columnCount;
                    footerView1 = (var12 - headerCount + 1) % columnCount;
                    if(footerView1 > 0) {
                        ++footerView;
                    }

                    if(footerView > 0) {
                        View adatperView = this.getChildAt(headerCount - this.mFirstPosition);
                        if(adatperView == null) {
                            return 2147483647;
                        }

                        bottomDistance += (adatperView.getHeight() + this.getVerticalSpacing()) * footerView;
                    }
                }

                for(footerView = 0; footerView < footerCount; ++footerView) {
                    var13 = ((FixedViewInfo)this.mFooterViewInfos.get(footerView)).view;
                    bottomDistance += var13.getHeight() + this.getVerticalSpacing();
                    if(var13 instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)var13).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)var13).getDownExpandDistance();
                    }
                }
            } else if(itemIndex >= headerCount && itemIndex < this.mItemCount - footerCount) {
                footerStart = itemIndex + columnCount - (itemIndex - this.mHeaderViewInfos.size()) % columnCount;
                var12 = this.mItemCount - this.mFooterViewInfos.size() - 1;
                if(footerStart <= var12) {
                    footerView = (var12 - footerStart + 1) / columnCount;
                    footerView1 = (var12 - footerStart + 1) % columnCount;
                    if(footerView1 > 0) {
                        ++footerView;
                    }

                    if(footerView > 0) {
                        bottomDistance += (itemView.getHeight() + this.getVerticalSpacing()) * footerView;
                    }
                }

                for(footerView = 0; footerView < footerCount; ++footerView) {
                    var13 = ((FixedViewInfo)this.mFooterViewInfos.get(footerView)).view;
                    bottomDistance += var13.getHeight() + this.getVerticalSpacing();
                    if(var13 instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)var13).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance)var13).getDownExpandDistance();
                    }
                }
            } else {
                footerStart = footerCount - (this.mItemCount - 1 - itemIndex);
                if(footerStart > 0) {
                    for(var12 = footerStart; var12 < footerCount; ++var12) {
                        View var14 = ((FixedViewInfo)this.mFooterViewInfos.get(var12)).view;
                        bottomDistance += var14.getHeight() + this.getVerticalSpacing();
                        if(var14 instanceof GridViewHeaderViewExpandDistance) {
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var14).getUpExpandDistance();
                            bottomDistance -= ((GridViewHeaderViewExpandDistance)var14).getDownExpandDistance();
                        }
                    }
                }
            }

            return bottomDistance;
        }
    }

    private boolean checkIsCanDown() {
        int selectedIndex = this.getSelectedItemPosition();
        int headerCount = this.mHeaderViewInfos.size();
        int footerCount = this.mFooterViewInfos.size();
        if(selectedIndex < headerCount) {
            if(selectedIndex < this.mItemCount - 1) {
                return true;
            } else {
                if(this.mOnFocusFlipGridViewListener != null) {
                    this.mOnFocusFlipGridViewListener.onReachGridViewBottom();
                }

                return false;
            }
        } else if(footerCount > 0) {
            if(selectedIndex < this.mItemCount - 1) {
                return true;
            } else {
                if(this.mOnFocusFlipGridViewListener != null) {
                    this.mOnFocusFlipGridViewListener.onReachGridViewBottom();
                }

                return false;
            }
        } else {
            int columnCount = this.getColumnNum();
            int nextFirstIndex = this.getRowStart(selectedIndex) + columnCount;
            if(nextFirstIndex >= this.mItemCount) {
                if(this.mOnFocusFlipGridViewListener != null) {
                    this.mOnFocusFlipGridViewListener.onReachGridViewBottom();
                }

                return false;
            } else {
                return true;
            }
        }
    }

    private int getHeaderViewSecondIndex(View headerView) {
        int secondIndex = -1;
        if(headerView instanceof FocusRelativeLayout) {
            View selectedChildView = ((FocusRelativeLayout)headerView).getSelectedView();
            if(selectedChildView != null && headerView instanceof FlipGridViewHeaderOrFooterInterface) {
                FlipGridViewHeaderOrFooterInterface headerInterface = (FlipGridViewHeaderOrFooterInterface)headerView;
                secondIndex = headerInterface.getViewIndex(selectedChildView);
            }
        }

        return secondIndex;
    }

    private void setAdapterSelection(int index) {
        super.setSelection(index);
    }

    public void setOutAnimFrameCount(int outAnimFrameCount) {
        if(this.mOutAnimationRunnable != null) {
            this.mOutAnimationRunnable.setOutAnimFrameCount(outAnimFrameCount);
        }

    }

    public boolean isFocusBackground() {
        return false;
    }

    public void drawBeforeFocus(Canvas canvas) {
    }

    public void drawAfterFocus(Canvas canvas) {
    }

    public boolean isFinished() {
        return true;
    }

    private int getRemainScrollUpDistance(int nextSelectedPosition) {
        int remainAmountToScroll = 0;
        int verticalSpacing = this.getVerticalSpacing();
        int center = this.getHeight() / 2;
        int distanceLeft = this.getFlipColumnFirstItemLeftMoveDistance(nextSelectedPosition);
        View nextSelctedView = this.getChildAt(nextSelectedPosition - this.mFirstPosition);
        boolean nextSelectedCenter = false;
        View firstView = this.getChildAt(0);
        int firstTop = firstView.getTop();
        if(firstView instanceof GridViewHeaderViewExpandDistance) {
            firstTop += ((GridViewHeaderViewExpandDistance)firstView).getUpExpandDistance();
        }

        int finalNextSelectedCenter;
        int maxDiff;
        int left;
        int var17;
        if(nextSelctedView == null) {
            nextSelctedView = this.getChildAt(0);
            finalNextSelectedCenter = this.getRowStart(this.getFirstVisiblePosition());
            maxDiff = this.getRowStart(nextSelectedPosition);
            left = this.mHeaderViewInfos.size();
            int delta;
            if(left > 0) {
                if(maxDiff < left) {
                    delta = (finalNextSelectedCenter - left) / this.getColumnNum();
                    var17 = nextSelctedView.getTop() - (nextSelctedView.getHeight() + verticalSpacing) * delta;

                    for(int i = maxDiff; i < left; ++i) {
                        View headerView = ((FixedViewInfo)this.mHeaderViewInfos.get(i)).view;
                        int headerViewHeight = headerView.getHeight();
                        if(headerView instanceof GridViewHeaderViewExpandDistance) {
                            headerViewHeight -= ((GridViewHeaderViewExpandDistance)headerView).getUpExpandDistance();
                            headerViewHeight -= ((GridViewHeaderViewExpandDistance)headerView).getDownExpandDistance();
                        }

                        var17 -= verticalSpacing + headerViewHeight / 2;
                    }
                } else {
                    delta = (finalNextSelectedCenter - maxDiff) / this.getColumnNum();
                    var17 = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                    var17 -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
                }
            } else {
                delta = (finalNextSelectedCenter - maxDiff) / this.getColumnNum();
                var17 = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                var17 -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
            }
        } else if(nextSelctedView instanceof GridViewHeaderViewExpandDistance) {
            finalNextSelectedCenter = ((GridViewHeaderViewExpandDistance)nextSelctedView).getUpExpandDistance();
            maxDiff = ((GridViewHeaderViewExpandDistance)nextSelctedView).getDownExpandDistance();
            var17 = nextSelctedView.getTop() + finalNextSelectedCenter + (nextSelctedView.getHeight() - finalNextSelectedCenter - maxDiff) / 2;
        } else {
            var17 = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
        }

        finalNextSelectedCenter = var17 + distanceLeft;
        if(finalNextSelectedCenter < center) {
            remainAmountToScroll = center - finalNextSelectedCenter;
            maxDiff = this.getTopLeftDistance(this.getFirstVisiblePosition());
            maxDiff = this.mListPadding.top - (firstTop - maxDiff);
            if(maxDiff < 0) {
                maxDiff = 0;
            }

            left = this.getFlipColumnFirstItemLeftMoveDistance(this.getFirstVisiblePosition());
            maxDiff -= left;
            if(maxDiff < 0) {
                maxDiff = 0;
            }

            if(remainAmountToScroll > maxDiff) {
                remainAmountToScroll = maxDiff;
            }
        }

        return remainAmountToScroll;
    }

    public void offsetFocusRect(int offsetX, int offsetY) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            this.mFocusRectparams.focusRect().offset(offsetX, offsetY);
        }

    }

    public void offsetFocusRect(int left, int right, int top, int bottom) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = this.mFocusRectparams.focusRect();
            var10000.left += left;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.right += right;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.top += top;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.bottom += bottom;
        }

    }

    public void offsetFocusRectLeftAndRight(int left, int right) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = this.mFocusRectparams.focusRect();
            var10000.left += left;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.right += right;
        }

    }

    public void offsetFocusRectTopAndBottom(int top, int bottom) {
        if(SystemProUtils.getGlobalFocusMode() == 0) {
            Rect var10000 = this.mFocusRectparams.focusRect();
            var10000.top += top;
            var10000 = this.mFocusRectparams.focusRect();
            var10000.bottom += bottom;
        }

    }

    public Rect getClipFocusRect() {
        return this.mClipFocusRect;
    }

    public interface OnFocusFlipGridViewListener {
        void onLayoutDone(boolean var1);

        void onOutAnimationDone();

        void onReachGridViewTop();

        void onReachGridViewBottom();
    }

    private class OutAnimationRunnable implements Runnable {
        private int outAnimFrameCount;
        private int mCurrFrameCount;
        private boolean mIsFinished;

        private OutAnimationRunnable() {
            this.outAnimFrameCount = 15;
            this.mIsFinished = true;
        }

        public void setOutAnimFrameCount(int outAnimFrameCount) {
            this.outAnimFrameCount = outAnimFrameCount;
        }

        public void start() {
            if(this.mIsFinished) {
                FocusFlipGridView.this.setFocusable(false);
                this.mIsFinished = false;
                this.mCurrFrameCount = 0;
                FocusFlipGridView.this.post(this);
            }

        }

        public void stop() {
            if(!this.mIsFinished) {
                Log.i("FocusFlipGridView", "OutAnimationRunnable stop");
                FocusFlipGridView.this.setFocusable(true);
                this.mIsFinished = true;
                this.setChild(1.0F);
                if(FocusFlipGridView.this.mOnFocusFlipGridViewListener != null) {
                    FocusFlipGridView.this.mOnFocusFlipGridViewListener.onOutAnimationDone();
                }
            }

        }

        public void run() {
            if(!this.mIsFinished) {
                if(this.mCurrFrameCount > this.outAnimFrameCount) {
                    this.stop();
                } else {
                    ++this.mCurrFrameCount;
                    float scale = 1.0F - (float)this.mCurrFrameCount / (float)this.outAnimFrameCount;
                    this.setChild(scale);
                    FocusFlipGridView.this.post(this);
                }
            }
        }

        private void setChild(float scale) {
            int itemCount = FocusFlipGridView.this.getChildCount();
            if(FocusFlipGridView.this.mAnimAlpha) {
                FocusFlipGridView.this.setAlpha(scale);
                FocusFlipGridView.this.mAnimAlphaValue = (int)(scale * 255.0F);
            }

            for(int i = 0; i < itemCount; ++i) {
                View itemView = FocusFlipGridView.this.getChildAt(i);
                if(itemView instanceof FlipGridViewHeaderOrFooterInterface) {
                    FlipGridViewHeaderOrFooterInterface headerOrFooterView = (FlipGridViewHeaderOrFooterInterface)itemView;
                    int childCount = headerOrFooterView.getHorCount() * headerOrFooterView.getVerticalCount();

                    for(int j = 0; j < childCount; ++j) {
                        View childView = headerOrFooterView.getView(j);
                        if(childView != null) {
                            childView.setScaleX(scale);
                            childView.setScaleY(scale);
                        }
                    }
                } else {
                    itemView.setScaleX(scale);
                    itemView.setScaleY(scale);
                }
            }

        }
    }
}
