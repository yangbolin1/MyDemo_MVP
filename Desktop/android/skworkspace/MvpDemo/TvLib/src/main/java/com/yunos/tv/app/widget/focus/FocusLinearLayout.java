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
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.yunos.tv.app.widget.FocusFinder;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.AnimateWhenGainFocusListener;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FocusLinearLayout extends LinearLayout implements DeepListener, ItemListener {
    protected static final String TAG = "FocusLinearLayout";
    protected static final boolean DEBUG = false;
    private boolean mOnFocused = false;
    protected int mIndex = -1;
    private ItemSelectedListener mOnItemSelectedListener = null;
    private boolean mAutoSearchFocus = true;
    protected Map<View, NodeInfo> mNodeMap = new HashMap();
    private View mLastSelectedView = null;
    private FocusFinder mFocusFinder;
    protected boolean mNeedInit = true;
    protected Params mParams;
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    View mNextFocus = null;
    DeepListener mDeep = null;
    DeepListener mLastDeep = null;
    int mNextDirection;
    boolean mLayouted = false;
    boolean mNeedReset = false;
    boolean mNeedInitNode = true;
    boolean mDeepFocus = false;
    FocusStateListener mFocusStateListener = null;
    FocusLinearLayout.OnItemClickListener mOnItemClickListener;
    ViewGroup mFindRootView;
    boolean mNeedFocused = true;
    boolean mFocusBackground = false;
    boolean mClearDataDetachedFromWindow = true;
    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;
    boolean mIsAnimate = true;
    private boolean mUpdateIndexBySelectView;
    View mFirstSelectedView;

    public FocusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.mFirstSelectedView = null;
        this.initLayout(context);
    }

    public FocusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.mFirstSelectedView = null;
        this.initLayout(context);
    }

    public FocusLinearLayout(Context context) {
        super(context);
        this.mUpdateIndexBySelectView = this.mIndex < 0;
        this.mFirstSelectedView = null;
        this.initLayout(context);
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        this.mAimateWhenGainFocusFromLeft = fromleft;
        this.mAimateWhenGainFocusFromUp = fromUp;
        this.mAimateWhenGainFocusFromRight = fromRight;
        this.mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setClearDataDetachedFromWindowEnable(boolean enable) {
        this.mClearDataDetachedFromWindow = enable;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mOnFocused = false;
        if(this.mClearDataDetachedFromWindow) {
            this.mLayouted = false;
            this.mNeedInitNode = true;
            if(this.mNodeMap != null) {
                this.mNodeMap.clear();
            }

            if(this.mFocusFinder != null) {
                this.mFocusFinder.clearFocusables();
            }
        }

    }

    public void setFocusBackground(boolean back) {
        this.mFocusBackground = back;
    }

    public void setOnItemClickListener(FocusLinearLayout.OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    private void performItemClick() {
        if(this.mDeep != null) {
            this.mDeep.onItemClick();
        } else {
            if(this.mOnItemClickListener != null) {
                this.mOnItemClickListener.onItemClick(this, this.getSelectedView());
            }

        }
    }

    private void performItemSelect(View v, boolean isSelected, boolean isLocal) {
        if(!isLocal && this.mDeep != null) {
            this.mDeep.onItemSelected(isSelected);
        } else {
            if(v != null) {
                v.setSelected(isSelected);
                if(this.mOnItemSelectedListener != null) {
                    this.mOnItemSelectedListener.onItemSelected(v, this.mIndex, isSelected, this);
                }
            }

        }
    }

    public void setAutoSearchFocus(boolean autoSearchFocus) {
        this.mAutoSearchFocus = autoSearchFocus;
    }

    public void setOnFocusStateListener(FocusStateListener l) {
        this.mFocusStateListener = l;
    }

    private void initLayout(Context conext) {
        this.mFocusFinder = new FocusFinder();
        mParams=new Params(1.05F, 1.05F, 10, (Interpolator)null, true, 20, new AccelerateDecelerateFrameInterpolator());
    }

    /**
     * 注意：这个地方，父布局把子布局放到焦点map里 用来处理子焦点的查找
     */
    @Override
    protected void onFinishInflate() {
        Log.i(TAG,"onFinishInflate");
        super.onFinishInflate();
        this.forceInitNode();
        this.setNeedInitNode(true);
    }

    public boolean isNeedFocusItem() {
        return this.mNeedFocused;
    }

    public void release() {
        this.mNodeMap.clear();
    }

    public void clearFocusedIndex() {
        this.mIndex = -1;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus()) {
            return this.mDeep.onKeyUp(keyCode, event);
        } else if((23 == keyCode || 66 == keyCode || keyCode == 160) && this.getSelectedView() != null) {
            if(this.isPressed()) {
                this.setPressed(false);
                this.performItemClick();
                if(this.getSelectedView() != null) {
                    this.getSelectedView().performClick();
                }
            }

            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void setSelected(boolean selected) {
        if(!selected) {
            this.mNeedFocused = true;
        }
        super.setSelected(selected);
    }

    private void performSelected(View selectedView, boolean isSelect, boolean isLocal) {
        if(selectedView != null) {
            selectedView.setSelected(isSelect);
            this.performItemSelect(selectedView, isSelect, isLocal);
            OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
            if(listener != null) {
                listener.onFocusChange(selectedView, isSelect);
            }
        }

    }

    private boolean performDeepAction(DeepListener deep, boolean focus, int direction, Rect previouslyFocusedRect) {
        if(deep != null && deep.hasDeepFocus()) {
            deep.onFocusDeeped(focus, direction, previouslyFocusedRect);
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("FocusLinearLayout", "onKeyDown keyCode = " + keyCode);
        if(this.mDeep != null && this.mDeep.canDeep() && this.mDeep.hasDeepFocus() && this.mDeep.onKeyDown(keyCode, event)) {
            this.reset();
            return true;
        } else if(keyCode != 23 && keyCode != 66 && keyCode != 160) {
            short direction = 130;
            switch(keyCode) {
                case 19:
                    direction = 33;
                    break;
                case 20:
                    direction = 130;
                    break;
                case 21:
                    direction = 17;
                    break;
                case 22:
                    direction = 66;
            }

            if(this.mNextFocus != null) {
                if(this.mDeep != null) {
                    this.mLastDeep = this.mDeep;
                    this.mDeep = null;
                }

                if(this.mNextFocus instanceof DeepListener) {
                    this.mDeep = (DeepListener)this.mNextFocus;
                    if(!this.mDeep.canDeep()) {
                        this.mDeep = null;
                    }
                }
            }

            if(this.mNextFocus != null && this.mNodeMap.containsKey(this.mNextFocus) && this.mNextFocus.isFocusable()) {
                this.mIsAnimate = true;
                if(this.mDeep != null && this.mDeep.canDeep()) {

                    if(!this.mDeep.hasDeepFocus()) {
                        Rect info1 = this.getFocusedRect(this.getSelectedView(), this.mNextFocus);
                        if(this.performDeepAction(this.mLastDeep, false, direction, (Rect)null)) {
                            this.mLastDeep = null;
                        }

                        this.mDeep.onFocusDeeped(true, this.mNextDirection, info1);
                        this.mLastSelectedView = this.getSelectedView();
                        this.performSelected(this.mLastSelectedView, false, true);
                        FocusLinearLayout.NodeInfo selectedView3 = (FocusLinearLayout.NodeInfo)this.mNodeMap.get(this.mNextFocus);
                        this.mIndex = selectedView3.index;
                        View selectedView1 = this.getSelectedView();
                        this.performSelected(selectedView1, true, true);
                        this.reset();
                    }

                    this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                    return true;
                } else {

                    this.performDeepAction(this.mLastDeep, false, direction, (Rect)null);
                    this.mLastSelectedView = this.getSelectedView();
                    this.performSelected(this.mLastSelectedView, false, false);
                    FocusLinearLayout.NodeInfo info = (FocusLinearLayout.NodeInfo)this.mNodeMap.get(this.mNextFocus);
                    this.mIndex = info.index;
                    AnimateWhenGainFocusListener selectedView;
                    switch(keyCode) {
                        case 19:
                            info.fromDown = this.mLastSelectedView;
                            if(this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                selectedView = (AnimateWhenGainFocusListener)this.mNextFocus;
                                this.mIsAnimate = selectedView.fromBottom();
                            }
                            break;
                        case 20:
                            info.fromUp = this.mLastSelectedView;
                            if(this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                selectedView = (AnimateWhenGainFocusListener)this.mNextFocus;
                                this.mIsAnimate = selectedView.fromTop();
                            }
                            break;
                        case 21:
                            info.fromRight = this.mLastSelectedView;
                            if(this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                selectedView = (AnimateWhenGainFocusListener)this.mNextFocus;
                                this.mIsAnimate = selectedView.fromRight();
                            }
                            break;
                        case 22:
                            info.fromLeft = this.mLastSelectedView;
                            if(this.mNextFocus instanceof AnimateWhenGainFocusListener) {
                                selectedView = (AnimateWhenGainFocusListener)this.mNextFocus;
                                this.mIsAnimate = selectedView.fromLeft();
                            }
                    }

                    this.mLastDeep = null;
                    View selectedView2 = this.getSelectedView();
                    this.performSelected(selectedView2, true, false);
                    this.reset();
                    this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(this.mNextDirection));
                    return true;
                }
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            this.setPressed(true);
            return true;
        }
    }

    protected void reset() {
        if(!this.hasFocusChild()) {
            Rect left = new Rect();
            this.getFocusedRect(left);
            this.mFocusRectparams.set(left, 0.5F, 0.5F);
        } else {
            if(this.getSelectedView() == null) {
                return;
            }

            if(this.mDeep != null) {
                this.mFocusRectparams.set(this.mDeep.getFocusParams());
            } else {
                if(this.mIndex == -1 && this.getChildCount() > 0) {
                    this.mIndex = 0;
                }

                ItemListener left1 = (ItemListener)this.getSelectedView();
                if(left1 != null) {
                    this.mFocusRectparams.set(left1.getFocusParams());
                }
            }

            int left2 = this.getSelectedView().getLeft();
            int scrollX = this.getScrollX();
            this.offsetDescendantRectToMyCoords(this.getSelectedView(), this.mFocusRectparams.focusRect());
        }

    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, ViewGroup findRoot) {
        this.mFindRootView = findRoot;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mFindRootView = null;
    }

    @Override //view
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.d("FocusLinearLayout", "onFocusChanged");
        this.mOnFocused = gainFocus;
        if(this.getOnFocusChangeListener() != null) {
            this.getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        this.doFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    protected void doFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if(gainFocus) {
            if(!this.hasFocusChild()) {
                this.mNeedReset = true;
                return;
            }

            this.mNeedFocused = false;
            if(this.mAutoSearchFocus && previouslyFocusedRect != null) {
                if(this.mFindRootView == null) {
                    this.mFindRootView = this;
                }

                View isDeep = this.mFocusFinder.findNextFocusFromRect(this.mFindRootView, previouslyFocusedRect, direction);
                if(this.mNodeMap.containsKey(isDeep)) {
                    FocusLinearLayout.NodeInfo isNeedReset = (FocusLinearLayout.NodeInfo)this.mNodeMap.get(isDeep);
                    this.mIndex = isNeedReset.index;
                } else if(this.mIndex < 0) {
                    this.mIndex = this.getFocusableItemIndex();
                }
            } else if(this.mIndex < 0) {
                this.mIndex = this.getFocusableItemIndex();
            }

            boolean isDeep1 = false;
            boolean isNeedReset1 = true;
            Log.i("FocusLinearLayout", "FocusLinearLayout.doFocusChanged.getSelectedView = " + this.getSelectedView());
            if(this.getSelectedView() instanceof DeepListener) {
                this.mDeep = (DeepListener)this.getSelectedView();
                if(this.mDeep.canDeep()) {
                    isDeep1 = true;
                    Rect rect = null;
                    if(previouslyFocusedRect != null) {
                        rect = new Rect(previouslyFocusedRect);
                        this.offsetRectIntoDescendantCoords((View)this.mDeep, rect);
                    }

                    this.mDeep.onFocusDeeped(gainFocus, direction, rect);
                    this.reset();
                    isNeedReset1 = false;
                }
            }

            if(!this.mLayouted) {
                this.mNeedReset = true;
            } else {
                if(isNeedReset1) {
                    this.reset();
                }
                //TODO
                this.performSelected(this.getSelectedView(),gainFocus,true);
               // this.performItemSelect(this.getSelectedView(), gainFocus, true);
            }
        } else if(this.mDeep != null && this.mDeep.canDeep()) {
            this.mDeep.onFocusDeeped(gainFocus, direction, (Rect)null);
        } else if(this.mLayouted) {
            //TODO
            this.performSelected(this.getSelectedView(),gainFocus,true);
            //this.performItemSelect(this.getSelectedView(), gainFocus, true);
        } else {
            this.mNeedReset = true;
        }

        this.mIsAnimate = this.checkAnimate(direction);
        this.invalidate();
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

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if(views != null) {
            if(this.isFocusable()) {
                if((focusableMode & 1) != 1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) {
                    views.add(this);
                }
            }
        }
    }

    public void getFocusedRect(Rect r) {
        View item = this.getSelectedView();
        if(this.isFocused() && item != null && item != null && item != this) {
            item.getFocusedRect(r);
            this.offsetDescendantRectToMyCoords(item, r);
        } else {
            super.getFocusedRect(r);
        }
    }

    public void setSelectedView(View v, int direction) {
        if(!this.mNodeMap.containsKey(v)) {
            throw new IllegalArgumentException("Parent does\'t contain this view");
        } else {
            this.mIsAnimate = false;
            View lastSelectedView = this.getSelectedView();
            this.mIndex = ((FocusLinearLayout.NodeInfo)this.mNodeMap.get(v)).index;
            this.mNextFocus = v;
            Rect previouslyFocusedRect = null;
            if(this.mDeep != null) {
                previouslyFocusedRect = new Rect();
                previouslyFocusedRect.set(this.mDeep.getFocusParams().focusRect());
                this.offsetDescendantRectToMyCoords(lastSelectedView, previouslyFocusedRect);
                this.mDeep.onFocusDeeped(false, 0, (Rect)null);
                this.mLastDeep = this.mDeep;
                this.mDeep = null;
            }

            if(v instanceof DeepListener) {
                this.mDeep = (DeepListener)v;
                if(!this.mDeep.canDeep()) {
                    this.mDeep = null;
                } else {
                    this.offsetRectIntoDescendantCoords(v, previouslyFocusedRect);
                    this.mDeep.onFocusDeeped(true, direction, previouslyFocusedRect);
                }
            }

        }
    }

    public View getSelectedView() {
        if(!this.hasFocusChild()) {
            return null;
        } else {
            int indexOfView = this.mIndex;
            View selectedView = this.getChildAt(indexOfView);
            return selectedView;
        }
    }

    public boolean isLayouted() {
        return this.mLayouted;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.initNode();
        this.mLayouted = true;
        this.reset();
        if(this.mNeedReset && this.hasFocusChild()) {
            this.performItemSelect(this.getSelectedView(), this.isFocused(), true);
            this.mNeedReset = false;
        }

    }

    public void setFirstSelectedView(View v) {
        this.mFirstSelectedView = v;
    }

    protected void initNode() {
        if(this.mNeedInitNode) {
            this.mFocusFinder.clearFocusables();
            this.mFocusFinder.initFocusables(this);
            this.mNodeMap.clear();

            for(int index = 0; index < this.getChildCount(); ++index) {
                View child = this.getChildAt(index);
                if(child.isFocusable() && child instanceof ItemListener) {
                    if(this.mFirstSelectedView == child && this.isUpdateIndexBySelectView()) {
                        this.mIndex = index;
                        this.setNeedUpdateIndexBySelectView(false);
                    }

                    if(!this.mNodeMap.containsKey(child)) {
                        FocusLinearLayout.NodeInfo info = new FocusLinearLayout.NodeInfo();
                        info.index = index;
                        this.mNodeMap.put(child, info);
                    }
                }
            }

            this.mNeedInitNode = false;
        }

    }

    public void forceInitNode() {
        this.mNeedInitNode = true;
        this.initNode();
    }

    public void notifyLayoutChanged() {
        this.mNeedInitNode = true;
        this.requestLayout();
    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = this.mIndex;
        return selectedIndex < 0?i:(i < selectedIndex?i:(i >= selectedIndex?childCount - 1 - i + selectedIndex:i));
    }

    public boolean isUpdateIndexBySelectView() {
        return this.mUpdateIndexBySelectView;
    }

    public void setNeedUpdateIndexBySelectView(boolean needResetIndex) {
        this.mUpdateIndexBySelectView = needResetIndex;
    }

    public FocusRectParams getFocusParams() {
        View v = this.getSelectedView();
        if(this.isFocusOrSlected() && v != null) {
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

    public boolean canDraw() {
        return this.mDeep != null?this.mDeep.canDraw():this.getSelectedView() != null || this.mOnFocused || this.isSelected();
    }

    public boolean isAnimate() {
        return this.mDeep != null?this.mDeep.isAnimate():this.mIsAnimate;
    }

    public ItemListener getItem() {
        View v = this.getSelectedView();
        if(this.isFocusOrSlected() && v != null) {
            ItemListener item = (ItemListener)v;
            DeepListener deep = this.mDeep;
            DeepListener lastDeep = this.mLastDeep;
            if(deep != null && deep.hasDeepFocus()) {
                item = deep.getItem();
            } else if(lastDeep != null && lastDeep.hasDeepFocus()) {
                item = lastDeep.getItem();
            }

            return (ItemListener)(item == null?this:item);
        } else {
            return this;
        }
    }

    public boolean isScrolling() {
        return this.mDeep != null?this.mDeep.isScrolling():false;
    }

    public Params getParams() {
        if(this.mDeep != null) {
            return this.mDeep.getParams();
        } else if(this.mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it\'s running");
        } else {
            //wdz TODO
            View view=getSelectedView();
            if(view!=null && view instanceof FocusListener)
            {
                return ((FocusListener)view).getParams();
            }
            ///
            return this.mParams;
        }
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.d("FocusLinearLayout", "preOnKeyDown keyCode = " + keyCode);
        if(this.mDeep != null && this.mDeep.preOnKeyDown(keyCode, event)) {
            return true;
        } else {
            View selectedView = this.getSelectedView();
            FocusLinearLayout.NodeInfo nodeInfo = (FocusLinearLayout.NodeInfo)this.mNodeMap.get(selectedView);
            View nextFocus = null;
            switch(keyCode) {
                case 19:
                    this.mNextDirection = 33;
                    if(nodeInfo != null && nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable()) {
                        nextFocus = nodeInfo.fromUp;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 20:
                    this.mNextDirection = 130;
                    if(nodeInfo != null && nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable()) {
                        nextFocus = nodeInfo.fromDown;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 21:
                    this.mNextDirection = 17;
                    if(nodeInfo != null && nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable()) {
                        nextFocus = nodeInfo.fromLeft;
                    } else {
                        nextFocus = this.mFocusFinder.findNextFocus(this, selectedView, this.mNextDirection);
                    }
                    break;
                case 22:
                    this.mNextDirection = 66;
                    if(nodeInfo != null && nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable()) {
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
            return nextFocus != null;
        }
    }

    private Rect getFocusedRect(View from, View to) {
        if(from != null && to != null) {
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
        } else {
            return null;
        }
    }

    public boolean hasDeepFocus() {
        return this.mDeepFocus;
    }

    public boolean canDeep() {
        return this.hasFocusChild();
    }

    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mDeepFocus = gainFocus;
        this.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public boolean isScale() {
        return false;
    }

    public int getItemWidth() {
        View v = this.getSelectedView();
        return this.isFocusOrSlected() && v != null?v.getWidth():this.getWidth();
    }

    public int getItemHeight() {
        View v = this.getSelectedView();
        return this.isFocusOrSlected() && v != null?v.getHeight():this.getHeight();
    }

    public Rect getManualPadding() {
        return null;
    }

    public void onFocusStart() {
        if(this.mDeep != null) {
            this.mDeep.onFocusStart();
        } else {
            if(this.mFocusStateListener != null) {
                this.mFocusStateListener.onFocusStart(this.getSelectedView(), this);
            }

        }
    }

    public void onFocusFinished() {
        if(this.mDeep != null) {
            this.mDeep.onFocusFinished();
        } else {
            if(this.mFocusStateListener != null) {
                this.mFocusStateListener.onFocusFinished(this.getSelectedView(), this);
            }

        }
    }

    public void onItemSelected(boolean selected) {
        this.performItemSelect(this.getSelectedView(), selected, false);
    }

    public void onItemClick() {
        this.performClick();
    }

    private int getFocusableItemIndex() {
        int childCount = this.getChildCount();
        int result = -1;

        for(int i = 0; i < childCount; ++i) {
            View childView = this.getChildAt(i);
            if(childView.isFocusable() && childView.getVisibility() == VISIBLE && childView instanceof ItemListener) {
                if(childView == this.mFirstSelectedView) {
                    return i;
                }

                if(result == -1) {
                    result = i;
                }
            }
        }

        return result;
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

    public boolean hasFocusChild() {
        return this.mNodeMap != null && !this.mNodeMap.isEmpty();
    }

    public void setNeedInitNode(boolean needInitNode) {
        this.mNeedInitNode = needInitNode;
    }

    public boolean isNeedInitNode() {
        return this.mNeedInitNode;
    }

    public Map<View, NodeInfo> getNodeMap() {
        return this.mNodeMap;
    }

    private boolean isFocusOrSlected() {
        return this.isFocused() || this.isSelected();
    }

    public boolean isFocused() {
        return super.isFocused() || this.hasFocus() || this.hasDeepFocus() || this.mOnFocused;
    }

    public Rect getClipFocusRect() {
        if(this.mDeep != null && this.isFocusOrSlected() && this.isScrolling()) {
            Rect clipFocusRect = new Rect();
            Rect deepRect = this.mDeep.getClipFocusRect();
            if(deepRect != null) {
                clipFocusRect.set(deepRect);
                this.offsetDescendantRectToMyCoords((View)this.mDeep, clipFocusRect);
            }

            return clipFocusRect;
        } else {
            return null;
        }
    }

    class NodeInfo {
        public int index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;

        NodeInfo() {
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ViewGroup var1, View var2);
    }
}
