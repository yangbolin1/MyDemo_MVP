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
import android.view.KeyEvent.DispatcherState;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;

public class FocusPositionManager extends FrameLayout implements PositionListener {
    protected static String TAG = "FocusPositionManager";
    protected static boolean DEBUG = false;
    View mFocused;
    PositionManager mPositionManager;
    boolean mLayouted = false;
    FocusPositionManager.FocusRequestRunnable mFocusRequestRunnable = new FocusPositionManager.FocusRequestRunnable();
    long time = 0L;
    int f = 0;
    private View mFirstRequestChild;

    public FocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public FocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FocusPositionManager(Context context) {
        super(context);
        this.init();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.mPositionManager != null) {
            this.mPositionManager.release();
        }

    }

    public View getFocused() {
        return this.mFocused;
    }

    private void init() {
        this.mPositionManager = PositionManager.createPositionManager(0, this);
    }

    public void setFocusMode(int focusMode) {
        if(this.mPositionManager != null) {
            this.mPositionManager.release();
            this.mPositionManager = null;
        }

        this.mPositionManager = PositionManager.createPositionManager(focusMode, this);
    }

    /**
     * 给全局焦点控制器设置一个selector
     * @param selector
     */
    public void setSelector(DrawListener selector) {
        this.mPositionManager.setSelector(selector);
    }

    /**
     * 设置后，焦点替换市会渐隐渐现的替换掉selector
     * @param convertSelector
     */
    public void setConvertSelector(DrawListener convertSelector) {
        this.mPositionManager.setConvertSelector(convertSelector);
    }

    /**
     * 指定控件设置焦点
     * @param v 控件 必须是Focus开头的阿里控件
     * @param direction 焦点进入方向
     */
    public void requestFocus(View v, int direction) {
        if(v == null) {
            throw new NullPointerException();
        } else if(!(v instanceof FocusListener)) {
            throw new IllegalArgumentException("The view you request focus must extend from FocusListener");
        } else {
            Log.d(TAG, TAG + ".requestFocus v = " + v + ", direction = " + direction);
            View rootFocus = this.findRootFocus(v);
            Rect previouslyFocusedRect = this.getFocusedRect(this.mFocused, rootFocus);
            Log.d(TAG, TAG + ".requestFocus rootFocus = " + rootFocus + ", mFocused = " + this.mFocused + ", previouslyFocusedRect = " + previouslyFocusedRect);
            if(!rootFocus.hasFocus()) {
                rootFocus.requestFocus(direction, previouslyFocusedRect);
            }

            Log.i(TAG, TAG + ".requestFocus.mPositionManager.stop()");
            this.mPositionManager.stop();
            if(this.mFocused != rootFocus) {
                this.mFocused = rootFocus;
                this.mPositionManager.reset((FocusListener)this.mFocused);
            } else {
                this.mPositionManager.reset();
            }

        }
    }

    private View findRootFocus(View child) {
        ViewParent temp = child.getParent();

        View rootFocus;
        for(rootFocus = child; temp != this; temp = temp.getParent()) {
            if(temp instanceof FocusListener) {
                rootFocus = (View)temp;
            }
        }

        return rootFocus;
    }

    public void focusShow() {
        Log.d(TAG, "focusShow");
        this.focusStart();
        this.invalidate();
    }

    public void focusHide() {
        Log.d(TAG, "focusHide");
        this.focusStop();
        this.invalidate();
    }

    public void focusPause() {
        Log.d(TAG, "focusPause");
        this.mPositionManager.focusPause();
    }

    public void focusStop() {
        Log.d(TAG, "focusStop");
        this.mPositionManager.focusStop();
    }

    public void focusStart() {
        Log.d(TAG, "focusStart");
        this.mPositionManager.focusStart();
    }

    public boolean IsFocusStarted() {
        return this.mPositionManager.isFocusStarted();
    }

    protected void dispatchDraw(Canvas canvas) {
        if(this.mLayouted) {
            if(this.mFocused == null) {
                super.dispatchDraw(canvas);
            } else {
                this.drawBackFocus(canvas);
                super.dispatchDraw(canvas);
                this.drawForeFocus(canvas);
                if(DEBUG) {
                    if(System.currentTimeMillis() - this.time >= 1000L) {
                        Log.d(TAG, "dispatchDraw f = " + this.f);
                        this.time = System.currentTimeMillis();
                        this.f = 0;
                    }

                    ++this.f;
                }

            }
        } else {
            this.postInvalidateDelayed(30L);
            super.dispatchDraw(canvas);
        }
    }

    private void drawBackFocus(Canvas canvas) {
        if(this.mPositionManager.isFocusBackground()) {
            this.drawFocus(canvas);
        }

    }

    private void drawForeFocus(Canvas canvas) {
        if(!this.mPositionManager.isFocusBackground()) {
            this.drawFocus(canvas);
        }

    }

    private void drawFocus(Canvas canvas) {
        if(DEBUG) {
            Log.i(TAG, TAG + ".drawFocus.mFocused = " + this.mFocused + ".mLayouted = " + this.mLayouted);
        }

        if(this.mFocused != null && this.mLayouted) {
            this.mPositionManager.draw(canvas);
        } else if(!this.mLayouted) {
            this.postInvalidateDelayed(30L);
        }

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if(this.checkValidKey(event.getKeyCode()) && (this.mFocused == null || !this.mFocused.hasFocus()) && this.mLayouted) {
            Log.w(TAG, "dispatchKeyEvent mFocused is null, may be no focusbale view in your layout, or mouse switch to key");
            this.deliverFocus();
            this.invalidate();
            return true;
        } else {
            return event.dispatch(this, (DispatcherState)null, this);
        }
    }

    @Override //view
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d(TAG, "onFocusChanged: child count = " + this.getChildCount() + ", gainFocus = " + gainFocus);
        if(gainFocus && this.mLayouted) {
            this.deliverFocus();
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i(TAG, TAG + ".onLayout");
        if(this.hasFocus() && !this.mLayouted && this.mFocused == null) {
            this.deliverFocus();
        }

        this.mLayouted = true;
    }

    private void deliverFocus() {
        if(this.mFocused == null) {
            this.mFocused = this.findFocusChild(this);
        }

        Log.d(TAG, "deliverFocus mFocused = " + this.mFocused);
        if(this.mFocused != null) {
            this.mFocused.requestFocus();
            this.mPositionManager.reset((FocusListener)this.mFocused);
        }

    }

    public void resetFocused() {
        if(this.mFocused == null) {
            this.mFocused = this.findFocusChild(this);
        }

        Log.d(TAG, "resetFocused mFocused = " + this.mFocused);
        if(this.mFocused != null) {
            if(!this.mFocused.hasFocus()) {
                this.mFocused.requestFocus();
            }

            this.mPositionManager.reset((FocusListener)this.mFocused);
        }

    }

    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    public void setFirstFocusChild(View firstRequestChild) {
        this.mFirstRequestChild = firstRequestChild;
    }

    private View findFocusChild(ViewGroup v) {
        View result = null;

        for(int index = 0; index < v.getChildCount(); ++index) {
            View child = v.getChildAt(index);
            if(child instanceof FocusListener) {
                if(child.isFocusable() && child.getVisibility() == VISIBLE) {
                    if(child == this.mFirstRequestChild) {
                        return child;
                    }

                    if(result == null) {
                        result = child;
                    }
                }
            } else if(child instanceof ViewGroup) {
                child = this.findFocusChild((ViewGroup)child);
                if(child != null) {
                    return child;
                }
            }
        }

        return result;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mFocused != null && this.mFocused.onKeyUp(keyCode, event)?true:super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode = " + keyCode + ".mFocused = " + this.mFocused);
        if(this.isNeedCheckValidKey(keyCode) && !this.checkValidKey(keyCode)) {
            return super.onKeyDown(keyCode, event);
        } else if(!this.mPositionManager.canDrawNext()) {
            return true;
        } else {
            if(this.mPositionManager.preOnKeyDown(keyCode, event)) {
                Log.i(TAG, TAG + "mPositionManager.preOnKeyDown==true");
                boolean focused = false;
                if(keyCode != 23 && keyCode != 66 && keyCode != 160) {
                    Log.i(TAG, TAG + ".onKeyDown.mPositionManager.stop()");
                    this.mPositionManager.stop();
                    focused = true;
                }

                if(this.mPositionManager.onKeyDown(keyCode, event)) {
                    Log.i(TAG, TAG + "mPositionManager.onKeyDown==true,return!");
                    if(focused) {
                        this.mPositionManager.reset();
                    }

                    return true;
                }
            }

            if(this.mFocused == null) {
                return true;
            } else {
                View focused1 = null;
                short direction = 0;
                switch(keyCode) {
                    case 4:
                    case 111:
                        return super.onKeyDown(keyCode, event);
                    case 19:
                        direction = 33;
                        focused1 = this.focusSearch(this.mFocused, direction);
                        break;
                    case 20:
                        direction = 130;
                        focused1 = this.focusSearch(this.mFocused, direction);
                        break;
                    case 21:
                        direction = 17;
                        focused1 = this.focusSearch(this.mFocused, direction);
                        break;
                    case 22:
                        direction = 66;
                        focused1 = this.focusSearch(this.mFocused, direction);
                }

                Log.i(TAG, "onKeyDown the new focused = " + focused1);
                if(focused1 != null && focused1 != this) {
                    if(!this.checkFocus(focused1, this.mFocused, direction)) {
                        Log.i(TAG, "onKeyDown: checkFocus failed  new focus = " + focused1 + ", old focus = " + this.mFocused);
                        this.mFocused = focused1;
                        return super.onKeyDown(keyCode, event);
                    } else {
                        if(focused1 instanceof FocusListener) {
                            View lastFocused = this.mFocused;
                            this.mFocused = focused1;
                            this.mPositionManager.stop();
                            this.mFocused.requestFocus(direction, this.getFocusedRect(lastFocused, this.mFocused));
                            this.mPositionManager.reset((FocusListener)this.mFocused);
                        } else {
                            Log.w(TAG, "onKeyDown the new focused is not instance of FocusListener");
                        }

                        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                        return true;
                    }
                } else {
                    Log.w(TAG, "onKeyDown can not find the new focus");
                    return true;
                }
            }
        }
    }

    boolean checkFocus(View newFocus, View oldFocus, int direction) {
        return this.isChild(this, newFocus);
    }

    boolean isChild(ViewGroup p, View newFocus) {
        for(int index = 0; index < p.getChildCount(); ++index) {
            View v = p.getChildAt(index);
            if(v == newFocus) {
                return true;
            }

            if(v instanceof ViewGroup && this.isChild((ViewGroup)v, newFocus)) {
                return true;
            }
        }

        return false;
    }

    Rect getFocusedRect(View from, View to) {
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

    public void offsetDescendantRectToItsCoords(View descendant, Rect rect) {
        this.offsetDescendantRectToMyCoords(descendant, rect);
    }

    public void reset() {
        Log.i(TAG, TAG + ".reset.mFocused = " + this.mFocused);
        boolean isInvalidate = true;
        if(this.mFocused != null) {
            this.mPositionManager.stop();
            this.mPositionManager.reset();
        } else {
            this.mFocused = this.findFocusChild(this);
            if(this.mFocused != null) {
                this.mFocused.requestFocus();
                this.mPositionManager.reset((FocusListener)this.mFocused);
            } else {
                isInvalidate = false;
            }
        }

        if(isInvalidate) {
            this.invalidate();
        }

    }

    private boolean checkValidKey(int keyCode) {
        switch(keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 66:
            case 67:
                return true;
            default:
                return false;
        }
    }

    public PositionManager getPositionManager() {
        return this.mPositionManager;
    }

    public int getFocusFrameRate() {
        return this.mPositionManager.getFocusFrameRate();
    }

    public int getCurFocusFrame() {
        return this.mPositionManager.getCurFocusFrame();
    }

    protected View getFocusedView() {
        return this.mFocused;
    }

    protected boolean isNeedCheckValidKey(int keyCode) {
        return true;
    }

    public DrawListener getSelector() {
        return this.mPositionManager.getSelector();
    }

    public DrawListener setConvertSelector() {
        return this.mPositionManager.getConvertSelector();
    }

    private class FocusRequestRunnable implements Runnable {
        View mView;

        public FocusRequestRunnable() {
        }

        public void start(View v) {
            this.mView = v;
            if(this.mView != null) {
                FocusPositionManager.this.removeCallbacks(this);
                FocusPositionManager.this.post(this);
            }

        }

        public void run() {
            this.mView.requestFocus();
            FocusPositionManager.this.invalidate();
        }
    }
}
