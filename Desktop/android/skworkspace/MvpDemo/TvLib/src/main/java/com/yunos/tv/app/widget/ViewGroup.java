//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.action.IFocusAction;
import com.yunos.tv.app.widget.focus.listener.AnimateWhenGainFocusListener;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewGroup extends android.view.ViewGroup implements IFocusAction, ItemListener {
    private final String TAG = this.getClass().getSimpleName();
    protected static final boolean DEBUG = true;
    protected Params mParams = new Params(1.1F, 1.1F, 10, (Interpolator) null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    private FocusFinder mFocusFinder;
    private boolean mAimateWhenGainFocusFromLeft = true;
    private boolean mAimateWhenGainFocusFromRight = true;
    private boolean mAimateWhenGainFocusFromUp = true;
    private boolean mAimateWhenGainFocusFromDown = true;
    private boolean mIsAnimate = true;
    private boolean mDeepFocus = false;
    private boolean mOnFocused = false;
    private ItemSelectedListener mItemSelectedListener;
    private ViewGroup.OnItemClickListener mOnItemClickListener;
    private FocusStateListener mFocusStateListener = null;
    protected View mNextFocus = null;
    protected DeepListener mDeep = null;
    protected DeepListener mLastDeep = null;
    View mLastSelectedView = null;
    protected int mNextDirection;
    protected int mIndex = -1;
    private boolean mAutoSearchFocus = true;
    protected Map<View, ViewGroup.NodeInfo> mNodeMap = new HashMap();
    protected boolean mNeedInit = true;
    boolean mLayouted = false;
    boolean mNeedReset = false;
    boolean mNeedInitNode = true;
    android.view.ViewGroup mFindRootView;
    boolean mFocusBackground = false;
    boolean mNeedFocused = true;
    boolean mClearDataDetachedFromWindow = true;
    View mFirstSelectedView = null;
    private boolean mUpdateIndexBySelectView;

    public ViewGroup(Context context) {
        super(context);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.initFocusFinder();
    }

    public ViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.initFocusFinder();
    }

    public ViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.initFocusFinder();
    }

    public boolean isUpdateIndexBySelectView() {
        return this.mUpdateIndexBySelectView;
    }

    public void setNeedUpdateIndexBySelectView(boolean needResetIndex) {
        this.mUpdateIndexBySelectView = needResetIndex;
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views != null) {
            if (this.isFocusable()) {
                if ((focusableMode & 1) != 1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) {
                    views.add(this);
                }
            }
        }
    }

    protected void afterLayout(boolean changed, int l, int t, int r, int b) {
        this.initNode();
        this.mLayouted = true;
        this.reset();
        if (this.mNeedReset && this.hasFocusChild()) {
            this.performItemSelect(this.getSelectedView(), this.hasFocus() || this.hasDeepFocus(), true);
            this.mNeedReset = false;
        }

    }

    public boolean hasFocusChild() {
        return this.mNodeMap != null && !this.mNodeMap.isEmpty();
    }

    public boolean canDeep() {
        return this.hasFocusChild();
    }

    public boolean canDraw() {
        return this.mDeep != null && this.mDeep.canDraw() ? true : this.getSelectedView() != null || this.mOnFocused || this.isSelected();
    }

    public boolean checkAnimate(int direction) {
        switch (direction) {
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

    public void clearFocusedIndex() {
        this.mIndex = -1;
    }

    public void clearSelectedView() {
        View selectedView = this.getSelectedView();
        if (selectedView != null) {
            selectedView.setSelected(false);
            this.performItemSelect(selectedView, false, false);
            OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
            if (listener != null) {
                listener.onFocusChange(selectedView, false);
            }
        }

    }

    public void drawAfterFocus(Canvas canvas) {
    }

    public void drawBeforeFocus(Canvas canvas) {
    }

    public void forceInitNode() {
        this.mNeedInitNode = true;
        this.initNode();
    }

    protected boolean getAnimateByKeyCode(AnimateWhenGainFocusListener animateListener, int keyCode) {
        switch (keyCode) {
            case 19:
                return animateListener.fromBottom();
            case 20:
                return animateListener.fromTop();
            case 21:
                return animateListener.fromRight();
            case 22:
                return animateListener.fromLeft();
            default:
                return false;
        }
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = this.mIndex;
        return selectedIndex < 0 ? i : (i < selectedIndex ? i : (i >= selectedIndex ? childCount - 1 - i + selectedIndex : i));
    }

    public DeepListener getDeep() {
        return this.mDeep;
    }

    public int getDirectionByKeyCode(int keyCode) {
        boolean direction = true;
        short direction1;
        switch (keyCode) {
            case 19:
                direction1 = 33;
                break;
            case 20:
                direction1 = 130;
                break;
            case 21:
                direction1 = 17;
                break;
            case 22:
                direction1 = 66;
                break;
            default:
                direction1 = 130;
                Log.w(this.TAG, "direction is default value : View.FOCUS_DOWN");
        }

        return direction1;
    }

    protected int getFocusableItemIndex() {
        int childCount = this.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View childView = this.getChildAt(i);
            if (childView.isFocusable() && childView.getVisibility() == View.VISIBLE && childView instanceof ItemListener) {
                if (childView == this.mFirstSelectedView) {
                    return i;
                }

                return i;
            }
        }

        return -1;
    }

    public void getFocusedRect(Rect r) {
        View item = this.getSelectedView();
        if ((this.hasFocus() || this.hasDeepFocus()) && item != null) {
            item.getFocusedRect(r);
            this.offsetDescendantRectToMyCoords(item, r);
        } else {
            super.getFocusedRect(r);
        }
    }

    protected Rect getFocusedRect(View from, View to) {
        Rect rFrom = new Rect();
        from.getFocusedRect(rFrom);
        Rect rTo = new Rect();
        to.getFocusedRect(rTo);
        this.offsetDescendantRectToMyCoords(from, rFrom);
        this.offsetDescendantRectToMyCoords(to, rTo);
        int xDiff = rFrom.left - rTo.left;
        int yDiff = rFrom.top - rTo.top;
        int rWidth = rFrom.width();
        int rheight = rFrom.height();
        rFrom.left = xDiff;
        rFrom.right = rFrom.left + rWidth;
        rFrom.top = yDiff;
        rFrom.bottom = rFrom.top + rheight;
        return rFrom;
    }

    public FocusFinder getFocusFinder() {
        return this.mFocusFinder;
    }

    public FocusRectParams getFocusParams() {
        if (this.mFocusRectparams == null || this.isScrolling()) {
            this.reset();
        }

        return this.mFocusRectparams;
    }

    public FocusRectParams getFocusRectParams() {
        return this.mFocusRectparams;
    }

    public FocusStateListener getFocusStateListener() {
        return this.mFocusStateListener;
    }

    public ItemListener getItem() {
        return (ItemListener) (!this.hasDeepFocus() ? this : (this.mDeep != null && this.mDeep.hasDeepFocus() ? this.mDeep.getItem() : (this.mLastDeep != null ? this.mLastDeep.getItem() : (ItemListener) this.getSelectedView())));
    }

    public int getItemHeight() {
        return this.hasFocusChild() && this.getSelectedView() != null ? this.getSelectedView().getHeight() : this.getHeight();
    }

    public ItemSelectedListener getOnItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public int getItemWidth() {
        return this.hasFocusChild() && this.getSelectedView() != null ? this.getSelectedView().getWidth() : this.getWidth();
    }

    public DeepListener getLastDeep() {
        return this.mLastDeep;
    }

    public View getLastSelectedView() {
        return this.mLastSelectedView;
    }

    public Rect getManualPadding() {
        return null;
    }

    public View getNextFocus() {
        return this.mNextFocus;
    }

    public ViewGroup.OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public Params getParams() {
        if (this.mDeep != null) {
            return this.mDeep.getParams();
        } else if (this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            return this.mParams;
        }
    }

    public View getSelectedView() {
        if (!this.hasFocusChild()) {
            return null;
        } else {
            if (this.mIndex < 0) {
                this.mIndex = this.getFocusableItemIndex();
            }

            return this.getChildAt(this.mIndex);
        }
    }

    public boolean hasDeepFocus() {
        return this.mDeepFocus;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.forceInitNode();
        this.setNeedInitNode(true);
    }

    private void initFocusFinder() {
        this.mFocusFinder = new FocusFinder();
        this.mFocusFinder.clearFocusables();
    }

    public void initNode() {
        if (this.mNeedInitNode) {
            this.mFocusFinder.clearFocusables();
            this.mFocusFinder.initFocusables(this);
            this.mNodeMap.clear();

            for (int index = 0; index < this.getChildCount(); ++index) {
                View child = this.getChildAt(index);
                if (child.isFocusable() && child instanceof ItemListener) {
                    if (this.mFirstSelectedView == child && this.isUpdateIndexBySelectView()) {
                        this.mIndex = index;
                        this.setNeedUpdateIndexBySelectView(false);
                    }

                    if (!this.mNodeMap.containsKey(child)) {
                        ViewGroup.NodeInfo info = new ViewGroup.NodeInfo();
                        info.index = index;
                        this.mNodeMap.put(child, info);
                    }
                }
            }

            this.mNeedInitNode = false;
        }

    }

    public void setNeedInitNode(boolean initNode) {
        this.mNeedInitNode = initNode;
    }

    public boolean isNeedInitNode() {
        return this.mNeedInitNode;
    }

    public boolean isAnimate() {
        return this.mDeep != null ? this.mDeep.isAnimate() : this.mIsAnimate;
    }

    boolean isDirectionKeyCode(int keyCode) {
        return this.isHorizontalKeyCode(keyCode) || this.isVerticalKeyCode(keyCode);
    }

    boolean isEnterKeyCode(int keyCode) {
        return keyCode == 23 || keyCode == 66 || keyCode == 160;
    }

    public boolean isFinished() {
        return true;
    }

    public boolean isFocusBackground() {
        return this.mDeep != null ? this.mDeep.isFocusBackground() : this.mFocusBackground;
    }

    boolean isHorizontalKeyCode(int keyCode) {
        return keyCode == 21 || keyCode == 22;
    }

    boolean isItemClickSelf() {
        return this.mDeep == null;
    }

    boolean isItemSelectSelf() {
        return this.mDeep == null;
    }

    public boolean isNeedFocusItem() {
        return this.mNeedFocused;
    }

    public boolean isLayouted() {
        return this.mLayouted;
    }

    boolean isNeedOnFocusSelf() {
        return this.mDeep == null;
    }

    public boolean isScale() {
        return false;
    }

    public boolean isScrolling() {
        return false;
    }

    boolean isSelfChildren(View view) {
        return this.indexOfChild(view) >= 0;
    }

    boolean isVerticalKeyCode(int keyCode) {
        return keyCode == 19 || keyCode == 20;
    }

    public void notifyLayoutChanged() {
        Log.d(this.TAG, "notifyLayoutChanged");
        this.mNeedInitNode = true;
        this.requestLayout();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mOnFocused = false;
        if (this.mClearDataDetachedFromWindow) {
            this.mLayouted = false;
            this.mNeedInitNode = true;
            if (this.mNodeMap != null) {
                this.mNodeMap.clear();
            }

            if (this.mFocusFinder != null) {
                this.mFocusFinder.clearFocusables();
            }
        }

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mOnFocused = gainFocus;
        Log.d(this.TAG, "onFocusChanged");
        if (this.getOnFocusChangeListener() != null) {
            this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        if (gainFocus) {
            if (!this.hasFocusChild()) {
                this.mNeedReset = true;
                return;
            }

            this.mNeedFocused = false;
            if (this.mAutoSearchFocus && previouslyFocusedRect != null) {
                if (this.mFindRootView == null) {
                    this.mFindRootView = this;
                }

                View isDeep = this.mFocusFinder.findNextFocusFromRect(this.mFindRootView, previouslyFocusedRect, direction);
                if (this.mNodeMap.containsKey(isDeep)) {
                    ViewGroup.NodeInfo rect = (ViewGroup.NodeInfo) this.mNodeMap.get(isDeep);
                    this.mIndex = rect.index;
                } else if (this.mIndex < 0) {
                    this.mIndex = this.getFocusableItemIndex();
                }
            } else if (this.mIndex < 0) {
                this.mIndex = this.getFocusableItemIndex();
            }

            boolean isDeep1 = false;
            if (this.getSelectedView() instanceof DeepListener) {
                this.mDeep = (DeepListener) this.getSelectedView();
                if (this.mDeep.canDeep()) {
                    isDeep1 = true;
                    Rect rect1 = new Rect(previouslyFocusedRect);
                    this.offsetRectIntoDescendantCoords((View) this.mDeep, rect1);
                    this.mDeep.onFocusDeeped(gainFocus, direction, rect1);
                    this.reset();
                }
            }

            if (!isDeep1) {
                if (!this.mLayouted) {
                    this.mNeedReset = true;
                } else {
                    this.reset();
                    this.performItemSelect(this.getSelectedView(), gainFocus, true);
                }
            }
        } else if (this.mDeep != null && this.mDeep.canDeep()) {
            this.mDeep.onFocusDeeped(gainFocus, direction, (Rect) null);
        } else if (this.mLayouted) {
            this.performItemSelect(this.getSelectedView(), gainFocus, true);
        } else {
            this.mNeedReset = true;
        }

        this.mIsAnimate = this.checkAnimate(direction);
        this.invalidate();
    }

    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, android.view.ViewGroup findRoot) {
        this.mFindRootView = findRoot;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mFindRootView = null;
    }

    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mDeepFocus = gainFocus;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void onFocusFinished() {
        if (this.mDeep != null) {
            this.mDeep.onFocusFinished();
        } else {
            if (this.mFocusStateListener != null) {
                this.mFocusStateListener.onFocusFinished(this.getSelectedView(), this);
            }

        }
    }

    public void onFocusStart() {
        if (this.mDeep != null) {
            this.mDeep.onFocusStart();
        } else {
            if (this.mFocusStateListener != null) {
                this.mFocusStateListener.onFocusStart(this.getSelectedView(), this);
            }

        }
    }

    public void onItemClick() {
        this.performClick();
    }

    public void onItemSelected(boolean selected) {
        this.performItemSelect(this.getSelectedView(), selected, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(this.TAG, "onKeyDown keyCode = " + keyCode);
        if (keyCode != 23 && keyCode != 66 && keyCode != 160) {
            if (this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus() && this.mDeep.onKeyDown(keyCode, event)) {
                this.reset();
                return true;
            } else {
                int direction = 0;
                if (this.isDirectionKeyCode(keyCode)) {
                    direction = this.getDirectionByKeyCode(keyCode);
                }

                if (this.mNextFocus != null && this.mNodeMap.containsKey(this.mNextFocus) && this.mNextFocus.isFocusable()) {
                    this.mIsAnimate = true;
                    if (this.mDeep != null && this.mDeep.canDeep()) {
                        if (!this.mDeep.hasDeepFocus()) {
                            Rect info2 = this.getFocusedRect(this.getSelectedView(), this.mNextFocus);
                            if (this.mLastDeep != null && this.mLastDeep.hasDeepFocus()) {
                                this.mLastDeep.onFocusDeeped(false, direction, (Rect) null);
                                this.mLastDeep = null;
                            }

                            this.mDeep.onFocusDeeped(true, this.mNextDirection, info2);
                            ViewGroup.NodeInfo selectedView1 = (ViewGroup.NodeInfo) this.mNodeMap.get(this.mNextFocus);
                            this.mIndex = selectedView1.index;
                            this.reset();
                        }

                        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                        return true;
                    } else {
                        if (this.mLastDeep != null && this.mLastDeep.hasDeepFocus()) {
                            this.mLastDeep.onFocusDeeped(false, direction, (Rect) null);
                        }

                        this.mLastSelectedView = this.getSelectedView();
                        if (this.mLastSelectedView != null) {
                            this.mLastSelectedView.setSelected(false);
                            this.performItemSelect(this.mLastSelectedView, false, false);
                            OnFocusChangeListener info = this.mLastSelectedView.getOnFocusChangeListener();
                            if (info != null) {
                                info.onFocusChange(this.mLastSelectedView, false);
                            }
                        }

                        ViewGroup.NodeInfo info1 = (ViewGroup.NodeInfo) this.mNodeMap.get(this.mNextFocus);
                        this.mIndex = info1.index;
                        AnimateWhenGainFocusListener selectedView;
                        switch (keyCode) {
                            case 19:
                                info1.fromDown = this.mLastSelectedView;
                                if (this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                    selectedView = (AnimateWhenGainFocusListener) this.mNextFocus;
                                    this.mIsAnimate = selectedView.fromBottom();
                                }
                                break;
                            case 20:
                                info1.fromUp = this.mLastSelectedView;
                                if (this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                    selectedView = (AnimateWhenGainFocusListener) this.mNextFocus;
                                    this.mIsAnimate = selectedView.fromTop();
                                }
                                break;
                            case 21:
                                info1.fromRight = this.mLastSelectedView;
                                if (this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                    selectedView = (AnimateWhenGainFocusListener) this.mNextFocus;
                                    this.mIsAnimate = selectedView.fromRight();
                                }
                                break;
                            case 22:
                                info1.fromLeft = this.mLastSelectedView;
                                if (this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                    selectedView = (AnimateWhenGainFocusListener) this.mNextFocus;
                                    this.mIsAnimate = selectedView.fromLeft();
                                }
                        }

                        this.mLastDeep = null;
                        View selectedView2 = this.getSelectedView();
                        if (selectedView2 != null) {
                            selectedView2.setSelected(true);
                            this.performItemSelect(selectedView2, true, false);
                            OnFocusChangeListener listener = selectedView2.getOnFocusChangeListener();
                            if (listener != null) {
                                listener.onFocusChange(selectedView2, true);
                            }
                        }

                        this.reset();
                        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(this.mNextDirection));
                        return true;
                    }
                } else {
                    return super.onKeyDown(keyCode, event);
                }
            }
        } else {
            this.setPressed(true);
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(this.TAG, "onKeyUp keyCode = " + keyCode);
        if (this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus()) {
            return this.mDeep.onKeyUp(keyCode, event);
        } else if ((23 == keyCode || 66 == keyCode || keyCode == 160) && this.getSelectedView() != null) {
            if (this.isPressed()) {
                this.setPressed(false);
                this.performItemClick();
                this.getSelectedView().performClick();
            }

            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    protected void performItemClick() {
        if (this.mDeep != null) {
            this.mDeep.onItemClick();
        } else {
            if (this.mOnItemClickListener != null) {
                this.mOnItemClickListener.onItemClick(this, this.getSelectedView());
            }

        }
    }

    void performItemSelect(View v, boolean isSelected, boolean isLocal) {
        if (!isLocal && this.mDeep != null) {
            this.mDeep.onItemSelected(isSelected);
        } else {
            if (v != null) {
                v.setSelected(isSelected);
                if (this.mItemSelectedListener != null) {
                    this.mItemSelectedListener.onItemSelected(v, this.mIndex, isSelected, this);
                }
            }

        }
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.d(this.TAG, "preOnKeyDown keyCode = " + keyCode);
        if (this.mDeep != null && this.mDeep.preOnKeyDown(keyCode, event)) {
            return true;
        } else {
            View selectedView = this.getSelectedView();
            ViewGroup.NodeInfo nodeInfo = (ViewGroup.NodeInfo) this.mNodeMap.get(selectedView);
            View nextFocus = null;
            switch (keyCode) {
                case 19:
                    this.mNextDirection = 33;
                    if (nodeInfo != null && nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable() && !nodeInfo.fromUp.equals(selectedView)) {
                        nextFocus = nodeInfo.fromUp;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 20:
                    this.mNextDirection = 130;
                    if (nodeInfo != null && nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable() && !nodeInfo.fromDown.equals(selectedView)) {
                        nextFocus = nodeInfo.fromDown;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 21:
                    this.mNextDirection = 17;
                    if (nodeInfo != null && nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable() && !nodeInfo.fromLeft.equals(selectedView)) {
                        nextFocus = nodeInfo.fromLeft;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 22:
                    this.mNextDirection = 66;
                    if (nodeInfo != null && nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable() && !nodeInfo.fromRight.equals(selectedView)) {
                        nextFocus = nodeInfo.fromRight;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 23:
                case 66:
                case 160:
                    return true;
                default:
                    return false;
            }

            this.mNextFocus = nextFocus;
            if (nextFocus != null) {
                if (this.mDeep != null) {
                    this.mLastDeep = this.mDeep;
                    this.mDeep = null;
                }

                if (nextFocus instanceof DeepListener) {
                    this.mDeep = (DeepListener) nextFocus;
                    if (!this.mDeep.canDeep()) {
                        this.mDeep = null;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public void release() {
        this.mNodeMap.clear();
    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    public void reset() {
        if (!this.hasFocusChild()) {
            Rect item = new Rect();
            this.getFocusedRect(item);
            this.mFocusRectparams.set(item, 0.5F, 0.5F);
        } else {
            if (this.getSelectedView() == null) {
                return;
            }

            if (this.mDeep != null) {
                this.mFocusRectparams.set(this.mDeep.getFocusParams());
            } else {
                if (this.mIndex == -1 && this.getChildCount() > 0) {
                    this.mIndex = 0;
                }

                ItemListener item1 = (ItemListener) this.getSelectedView();
                if (item1 != null) {
                    this.mFocusRectparams.set(item1.getFocusParams());
                }
            }

            this.offsetDescendantRectToMyCoords(this.getSelectedView(), this.mFocusRectparams.focusRect());
        }

    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setAutoSearchFocus(boolean autoSearchFocus) {
        this.mAutoSearchFocus = autoSearchFocus;
    }

    public void setClearDataDetachedFromWindowEnable(boolean enable) {
        this.mClearDataDetachedFromWindow = enable;
    }

    public void setDeep(DeepListener deep) {
        this.mDeep = deep;
    }

    public void setDeepFocus(boolean deepFocus) {
        this.mDeepFocus = deepFocus;
    }

    public void setFirstSelectedView(View v) {
        this.mFirstSelectedView = v;
    }

    public void setFocusBackground(boolean focusBg) {
        this.mFocusBackground = focusBg;
    }

    public void setFocusRectParams(FocusRectParams params) {
        if (params == null) {
            Log.w(this.TAG, "AbstractViewGroupFocus setFocusRectParams \'params\' is null.");
        } else {
            this.mFocusRectparams.set(params);
        }

    }

    public void setLastDeep(DeepListener lastDeep) {
        this.mLastDeep = lastDeep;
    }

    public void setLastSelectedView(View lastSelectedView) {
        this.mLastSelectedView = lastSelectedView;
    }

    public void setNextFocus(View nextFocus) {
        this.mNextFocus = nextFocus;
    }

    public void setOnFocusStateListener(FocusStateListener focusStateListener) {
        this.mFocusStateListener = focusStateListener;
    }

    public void setOnItemClickListener(ViewGroup.OnItemClickListener lis) {
        this.mOnItemClickListener = lis;
    }

    public void setOnItemSelectedListener(ItemSelectedListener lis) {
        this.mItemSelectedListener = lis;
    }

    public void setParams(Params params) {
        if (params == null) {
            throw new NullPointerException("AbstractViewGroupFocus setParams params is null.");
        } else {
            this.mParams = params;
        }
    }

    public void setSelected(boolean selected) {
        if (!selected) {
            this.mNeedFocused = true;
        }

        super.setSelected(selected);
    }

    public void setSelectedView(View v) {
        if (!this.mNodeMap.containsKey(v)) {
            throw new IllegalArgumentException("Parent does\'t contain this view");
        }
    }

    public Map<View, ViewGroup.NodeInfo> getNodeMap() {
        return this.mNodeMap;
    }

    public class NodeInfo {
        public int index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;

        public NodeInfo() {
        }
    }

    public interface OnItemClickListener {
        void onItemClick(android.view.ViewGroup var1, View var2);
    }
}
