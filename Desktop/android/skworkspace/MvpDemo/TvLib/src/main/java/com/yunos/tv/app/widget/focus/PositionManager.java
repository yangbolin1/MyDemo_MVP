//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.AlphaParams;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.ScaleParams;
import com.yunos.tv.lib.SystemProUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class PositionManager {
    protected static final String TAG = "PositionManager";
    protected static final boolean DEBUG = false;
    public static final int FOCUS_SYNC_DRAW = 0;
    public static final int FOCUS_ASYNC_DRAW = 1;
    public static final int FOCUS_STATIC_DRAW = 2;
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAWING = 1;
    PositionListener mListener;
    FocusListener mFocus = null;
    FocusListener mLastFocus = null;
    int mFrame = 1;
    int mScaleFrame = 1;
    int mFocusFrame = 1;
    int mAlphaFrame = 1;
    int mFocusFrameRate = 50;
    int mScaleFrameRate = 50;
    int mAlphaFrameRate = 50;
    DrawListener mSelector;
    DrawListener mConvertSelector;
    Rect mFocusRect = new Rect();
    Rect mClipFocusRect = new Rect();
    int mFocusMode = 2;
    private PositionManager.ScaledList mScaledList = new PositionManager.ScaledList();
    private PositionManager.AlphaList mAlphaList = new PositionManager.AlphaList();
    private PositionManager.NodeManager mNodeManager = new PositionManager.NodeManager();
    private PositionManager.NodeManager mCurNodeManager = new PositionManager.NodeManager();
    boolean mStart = true;
    boolean mPause = false;
    protected Interpolator mSelectorPolator = null;
    protected ItemListener mLastItem;
    protected boolean mForceDrawFocus = false;
    boolean mCurNodeAdded = false;

    public static PositionManager createPositionManager(int focusMode, PositionListener l) {
        Log.i("PositionManager", "createPositionManager focusMode = " + focusMode);
        if(focusMode == 0) {
            SystemProUtils.setGlobalFocusMode(focusMode);
            return new SyncPositionManager(focusMode, l);
        } else if(focusMode == 2) {
            SystemProUtils.setGlobalFocusMode(focusMode);
            return new StaticPositionManager(focusMode, l);
        } else {
            Log.e("PositionManager", "createPositionManager focusMode not support");
            return null;
        }
    }

    public boolean isFocusBackground() {
        return this.mFocus.isFocusBackground();
    }

    public PositionManager(int focusMode, PositionListener l) {
        this.mListener = l;
        this.mFocusMode = focusMode;
    }

    public boolean isFocusStarted() {
        return this.mStart;
    }

    public void focusStart() {
        if(!this.mStart || this.mPause) {
            this.mStart = true;
            this.mPause = false;
            this.reset();
            this.mListener.postInvalidate();
        }
    }

    public void focusPause() {
        if(!this.mPause) {
            this.mPause = true;
        }
    }

    public void focusStop() {
        Log.i("PositionManager", "PositionManager.focusStop");
        if(this.mStart) {
            this.mStart = false;
        }
    }

    public void draw(Canvas canvas) {
        this.drawOut(canvas);
        if(!this.mStart) {
            this.drawCurOut(canvas);
        }

    }

    public void drawBeforeFocus(ItemListener item, Canvas canvas) {
        item.drawBeforeFocus(canvas);
    }

    public void drawAfterFocus(ItemListener item, Canvas canvas) {
        item.drawAfterFocus(canvas);
    }

    public void release() {
        Log.d("PositionManager", "release");
        this.mScaledList.clear();
        this.mAlphaList.clear();
        this.mNodeManager.clear();
        this.mCurNodeManager.clear();
    }

    protected void drawCurOut(Canvas canvas) {
        this.mCurNodeManager.drawOut(canvas);
    }

    protected void drawOut(Canvas canvas) {
        this.mNodeManager.drawOut(canvas);
    }

    protected void preDrawOut(Canvas canvas) {
        this.mNodeManager.preDrawOut(canvas);
    }

    protected void postDrawOut(Canvas canvas) {
        this.mNodeManager.postDrawOut(canvas);
    }

    protected void drawUnscale(Canvas canvas) {
        this.mScaledList.drawOut(canvas);
    }

    protected void preDrawUnscale(Canvas canvas) {
        this.mScaledList.preDrawOut(canvas);
    }

    public void forceDrawFocus() {
        this.mForceDrawFocus = true;
        if(this.mListener != null) {
            this.mListener.invalidate();
        }

    }

    protected void postDrawUnscale(Canvas canvas) {
        this.mScaledList.postDrawOut(canvas);
    }

    void drawFocus(Canvas canvas) {
        if(this.mFocus != null && !this.mFocusRect.isEmpty()) {
            this.mSelector.setRect(this.mFocusRect);
            if(!this.mClipFocusRect.isEmpty() && this.clipCanvasIsNeeded(this.mFocusRect, this.mClipFocusRect)) {
                canvas.save();
                canvas.clipRect(this.mClipFocusRect);
                this.mSelector.draw(canvas);
                canvas.restore();
            } else {
                this.mSelector.draw(canvas);
            }

            if(this.mConvertSelector != null) {
                this.mConvertSelector.setRect(this.mFocusRect);
                this.mConvertSelector.draw(canvas);
            }
        } else {
            Log.w("PositionManager", "drawFocus mFocus = " + this.mFocus);
        }

    }

    void drawFocus(Canvas canvas, Rect rect, float alpha, DrawListener selector, Rect clipRect) {
        if(rect != null && selector != null) {
            if(this.mFocus != null && !rect.isEmpty()) {
                selector.setRect(rect);
                selector.setAlpha(alpha);
                if(!clipRect.isEmpty() && this.clipCanvasIsNeeded(rect, clipRect)) {
                    canvas.save(2);
                    canvas.clipRect(clipRect);
                    selector.draw(canvas);
                    canvas.restore();
                } else {
                    selector.draw(canvas);
                }
            } else {
                Log.w("PositionManager", "drawFocus mFocus = " + this.mFocus);
            }

        }
    }

    void drawStaticFocus(Canvas canvas, ItemListener item) {
        this.drawStaticFocus(canvas, item, this.mFocus.getParams().getScaleParams().getScaleX(), this.mFocus.getParams().getScaleParams().getScaleY());
    }

    void drawStaticFocus(Canvas canvas, ItemListener item, float scaleX, float scaleY) {
        this.mFocusRect.set(this.getDstRect(scaleX, scaleY, item.isScale()));
        this.mClipFocusRect.set(this.getClipFocusRect(this.mFocus));
        this.offsetManualPadding(this.mFocusRect, item);
        this.drawFocus(canvas);
    }

    protected Rect getFinalRect(ItemListener item) {
        Rect r = new Rect();
        ScaleParams scaleParams = this.mFocus.getParams().getScaleParams();
        r.set(this.getDstRect(scaleParams.getScaleX(), scaleParams.getScaleY(), item.isScale()));
        this.offsetManualPadding(r, item);
        return r;
    }

    public Rect getDstRect(float scaleX, float scaleY, boolean isScale) {
        FocusRectParams params = this.mFocus.getFocusParams();
        Rect r = new Rect();
        r.set(params.focusRect());
        this.mListener.offsetDescendantRectToItsCoords((View)this.mFocus, r);
        if(isScale) {
            ScalePositionManager.instance().getScaledRect(r, scaleX, scaleY, params.coefX(), params.coefY());
        }

        return r;
    }

    public Rect getClipFocusRect(FocusListener focus) {
        Rect r = new Rect();
        if(focus != null) {
            Rect clipRect = focus.getClipFocusRect();
            if(clipRect != null) {
                r.set(clipRect);
                this.mListener.offsetDescendantRectToItsCoords((View)focus, r);
            }
        }

        return r;
    }

    public Rect getAlphaListItemRect(ItemListener item, boolean isScale) {
        FocusRectParams params = item.getFocusParams();
        new Rect();
        Rect r = item.getFocusParams().focusRect();
        if(!(item instanceof View)) {
            return null;
        } else {
            ViewGroup group;
            for(Object thisView = (View)item; !(thisView instanceof FocusPositionManager) && thisView != null; thisView = group) {
                ViewParent parent = ((View)thisView).getParent();
                if(!(parent instanceof ViewGroup)) {
                    if(parent == null) {
                        return null;
                    }

                    return r;
                }

                group = (ViewGroup)parent;
                group.offsetDescendantRectToMyCoords((View)thisView, r);
            }

            if(isScale) {
                ScalePositionManager.instance().getScaledRect(r, item.getScaleX(), item.getScaleY(), params.coefX(), params.coefY());
            }

            return r;
        }
    }

    public void setSelector(DrawListener selector) {
        this.mSelector = selector;
    }

    public void setConvertSelector(DrawListener convertSelector) {
        this.mConvertSelector = convertSelector;
    }

    protected void removeScaleNode(ItemListener item) {
        this.mScaledList.remove(item);
    }

    private void addNode(ItemListener item) {
        Log.i("PositionManager", "PositionManager.addNode.item = " + item);
        if(item == null) {
            Log.w("PositionManager", "addNode: item is null");
        } else {
            this.mNodeManager.release();
            Object scaleInterpolator;
            if(SystemProUtils.getGlobalFocusMode() == 2) {
                AlphaParams scaledParams = this.mFocus.getParams().getAlphaParams();
                scaleInterpolator = scaledParams.getAlphaInteroplator();
                if(scaleInterpolator == null) {
                    scaleInterpolator = new LinearInterpolator();
                }

                Log.i("PositionManager", "PositionManager.addNode.mAlphaFrame = " + this.mAlphaFrame + ", mAlphaFrameRate = " + this.mAlphaFrameRate);
                PositionManager.AlphaInfo scaledInfo
                        = new PositionManager.AlphaInfo(item, this.mAlphaFrame, this.mAlphaFrameRate, scaledParams.getToAlpha(), (Interpolator)scaleInterpolator, this.mSelector, this.mFocus);
                this.mNodeManager.init(this.mAlphaList, scaledInfo);
            }

            ScaleParams scaledParams1 = this.mFocus.getParams().getScaleParams();
            scaleInterpolator = scaledParams1.getScaleInterpolator();
            if(scaleInterpolator == null) {
                scaleInterpolator = new LinearInterpolator();
            }
            Log.i(TAG,"mFocus:"+mFocus+" scaledParams1:"+scaledParams1);
            Log.i("PositionManager", "PositionManager.addNode.mScaleFrame = " + this.mScaleFrame + ", mScaleFrameRate = " + this.mScaleFrameRate);
            PositionManager.ScaledInfo scaledInfo1 = new PositionManager.ScaledInfo(item, this.mScaleFrame, this.mScaleFrameRate, scaledParams1.getScaleX(), scaledParams1.getScaleY(), (Interpolator)scaleInterpolator);
            this.mNodeManager.init(this.mScaledList, scaledInfo1);
        }
    }

    protected void addCurNode(ItemListener item) {
        if(item == null) {
            Log.w("PositionManager", "addCurNode: item is null");
        } else if(this.mCurNodeManager.mList.size() <= 0 || !this.mCurNodeAdded) {
            this.mCurNodeAdded = true;
            this.mCurNodeManager.release();
            PositionManager.ScaledList scaledList = new PositionManager.ScaledList();
            PositionManager.AlphaList alphaList = new PositionManager.AlphaList();
            Object scaleInterpolator;
            if(SystemProUtils.getGlobalFocusMode() == 2) {
                AlphaParams scaledParams = this.mFocus.getParams().getAlphaParams();
                scaleInterpolator = scaledParams.getAlphaInteroplator();
                if(scaleInterpolator == null) {
                    scaleInterpolator = new LinearInterpolator();
                }

                PositionManager.AlphaInfo scaledInfo =
                        new PositionManager.AlphaInfo(item, this.mAlphaFrame, this.mAlphaFrameRate, scaledParams.getToAlpha(), (Interpolator)scaleInterpolator, this.mSelector, this.mFocus);
                this.mCurNodeManager.init(alphaList, scaledInfo);
            }

            ScaleParams scaledParams1 = this.mFocus.getParams().getScaleParams();
            scaleInterpolator = scaledParams1.getScaleInterpolator();
            if(scaleInterpolator == null) {
                scaleInterpolator = new LinearInterpolator();
            }

            PositionManager.ScaledInfo scaledInfo1 = new PositionManager.ScaledInfo(item, this.mScaleFrame, this.mScaleFrameRate, scaledParams1.getScaleX(), scaledParams1.getScaleY(), (Interpolator)scaleInterpolator);
            this.mCurNodeManager.init(scaledList, scaledInfo1);
        }
    }

    protected void removeNode(ItemListener item) {
        this.mNodeManager.remove(item);
    }

    private void addScaledNode(ItemListener item) {
        if(item == null) {
            Log.w("PositionManager", "addScaledNode: item is null");
        } else {
            ScaleParams params = this.mFocus.getParams().getScaleParams();
            Object scaleInterpolator = params.getScaleInterpolator();
            if(scaleInterpolator == null) {
                scaleInterpolator = new LinearInterpolator();
            }

            this.mScaledList.add(new PositionManager.ScaledInfo(item, this.mScaleFrame, this.mScaleFrameRate, params.getScaleX(), params.getScaleY(), (Interpolator)scaleInterpolator));
        }
    }

    public void stop() {
        if(this.mFocus != null) {
            this.resetSelector();
            ItemListener item = this.mFocus.getItem();
            this.addNode(item);
        }
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.i("PositionManager", "PositionManager.preOnKeyDown.mFocus = " + this.mFocus);
        return this.mFocus == null?false:this.mFocus.preOnKeyDown(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mFocus.onKeyDown(keyCode, event);
    }

    public void reset() {
        this.mFrame = 1;
        this.mScaleFrame = 1;
        this.mFocusFrame = 1;
        this.mAlphaFrame = 1;
        this.mListener.invalidate();
    }

    public void reset(FocusListener focus) {
        Log.d("PositionManager", "reset focus = " + focus);
        if(focus != null) {
            this.mLastFocus = this.mFocus;
            this.mFocus = focus;
            if(this.mFocusMode == 2) {
                this.mFocusFrameRate = 0;
            } else {
                this.mFocusFrameRate = this.mFocus.getParams().getFocusParams().getFocusFrameRate();
            }

            this.mScaleFrameRate = this.mFocus.getParams().getScaleParams().getScaleFrameRate();
            this.mAlphaFrameRate = this.mFocus.getParams().getAlphaParams().getAlphaFrameRate();
            this.reset();
        } else {
            Log.e("PositionManager", "reset focus is null");
        }

        Log.i("PositionManager", "PositionManager.reset.mFrame = " + this.mFrame + ".mScaleFrame = " + this.mScaleFrame + ".mFocusFrame = " + this.mFocusFrame + ".mAlphaFrame = " + this.mAlphaFrame + ".mFocusFrameRate = " + this.mFocusFrameRate + ".mScaleFrameRate = " + this.mScaleFrameRate + ".mAlphaFrameRate = " + this.mAlphaFrameRate);
    }

    void offsetManualPadding(Rect r, ItemListener item) {
        Rect rPadding = item.getManualPadding();
        if(rPadding != null) {
            r.left += rPadding.left;
            r.right += rPadding.right;
            r.top += rPadding.top;
            r.bottom += rPadding.bottom;
        }

    }

    public boolean isFinished() {
        return this.mFrame > Math.max(this.mFocusFrameRate, this.mScaleFrameRate);
    }

    public boolean canDrawNext() {
        return true;
    }

    public int getFrame() {
        return this.mFrame;
    }

    public int getTotalFrame() {
        return Math.max(this.mFocusFrameRate, this.mScaleFrameRate);
    }

    public int getFocusFrameRate() {
        return this.mFocusFrameRate;
    }

    public int getCurFocusFrame() {
        return this.mFocusFrame;
    }

    public void resetSelector() {
    }

    public DrawListener getSelector() {
        return this.mSelector;
    }

    public DrawListener getConvertSelector() {
        return this.mConvertSelector;
    }

    public void setSelectorInterpolator(Interpolator selectorPolator) {
        this.mSelectorPolator = selectorPolator;
    }

    public Interpolator getSelectorInterpolator() {
        return this.mSelectorPolator;
    }

    public boolean clipCanvasIsNeeded(Rect focusRect, Rect clipRect) {
        return SystemProUtils.getGlobalFocusMode() != 2?false:focusRect.left < clipRect.left || focusRect.right > clipRect.right || focusRect.top < clipRect.top || focusRect.bottom > clipRect.bottom;
    }

    class AlphaInfo extends PositionManager.Info {
        DrawListener mSelector;
        float mAlpha;
        FocusListener mLastFocus = null;

        public AlphaInfo(ItemListener item, int frame, int frameRate, float alpha, Interpolator alphaInterpolator, DrawListener selector, FocusListener lastFocus) {
            super();
            this.mItem = item;
            AlphaParams alphaParams = PositionManager.this.mFocus.getParams().getAlphaParams();
            float fromAlpha = alphaParams.getFromAlpha();
            float toAlpha = alphaParams.getToAlpha();
            if(toAlpha <= 1.0F && fromAlpha >= 0.0F) {
                this.mFrame = frame - 1;
            } else {
                this.mFrame = -1;
            }

            this.mFrameRate = frameRate;
            this.mAlpha = alpha;
            this.mInterpolator = alphaInterpolator;
            this.mSelector = selector;
            this.mLastFocus = lastFocus;
        }

        public void setAlpha(float dstAlpha) {
            this.mAlpha = dstAlpha;
        }

        public float getAlpha() {
            return this.mAlpha;
        }
    }

    class AlphaList extends PositionManager.BaseList {
        AlphaList() {
            super();
        }

        protected void drawOut(PositionManager.Info info, Canvas canvas) {
            if(info.getFrame() > 0) {
                if(info instanceof PositionManager.AlphaInfo) {
                    PositionManager.AlphaInfo alphaInfo = (PositionManager.AlphaInfo)info;
                    float currentAlpha = alphaInfo.getAlpha();
                    float coef = (float)(alphaInfo.getFrame() - 1) / (float)alphaInfo.getFrameRate();
                    coef = alphaInfo.getInterpolator().getInterpolation(coef);
                    float dstAlpha = currentAlpha * coef;
                    Rect rect = PositionManager.this.getAlphaListItemRect(info.getItem(), true);
                    if(rect != null) {
                        PositionManager.this.offsetManualPadding(rect, info.getItem());
                    }

                    Rect clipFocusRect = PositionManager.this.getClipFocusRect(alphaInfo.mLastFocus);
                    if(PositionManager.this.mLastItem != info.mItem) {
                        PositionManager.this.drawFocus(canvas, rect, dstAlpha, alphaInfo.mSelector, clipFocusRect);
                    }

                    info.subFrame();
                } else {
                    info.subFrame();
                }
            }
        }
    }

    private abstract class BaseList {
        List<Info> mList;
        Object lock;
        boolean mIsBreak;

        private BaseList() {
            this.mList = new LinkedList();
            this.lock = new Object();
            this.mIsBreak = false;
        }

        public void add(PositionManager.Info info) {
            this.setBreak(true);
            synchronized(this) {
                this.mList.add(info);
            }

            PositionManager.this.mListener.invalidate();
        }

        public void clear() {
            synchronized(this) {
                this.mList.clear();
            }
        }

        public void remove(ItemListener item) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.Info info = (PositionManager.Info)this.mList.get(index);
                if(info.getItem() == item) {
                    this.mList.remove(index);
                    break;
                }
            }

        }

        public void preDrawOut(Canvas canvas) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.Info info = (PositionManager.Info)this.mList.get(index);
                if(info.getFrame() >= 0) {
                    PositionManager.this.drawBeforeFocus(info.getItem(), canvas);
                }
            }

        }

        public void drawOut(Canvas canvas) {
            this.setBreak(false);
            synchronized(this) {
                while(this.mList.size() > 0 && !this.isBreak()) {
                    PositionManager.Info index = (PositionManager.Info)this.mList.get(0);
                    if(!index.isFinished()) {
                        break;
                    }

                    this.mList.remove(0);
                }

                for(int var5 = 0; var5 < this.mList.size() && !this.isBreak(); ++var5) {
                    this.drawOut((PositionManager.Info)this.mList.get(var5), canvas);
                }

                if(this.mList.size() > 0) {
                    PositionManager.this.mListener.invalidate();
                }

            }
        }

        public void postDrawOut(Canvas canvas) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.Info info = (PositionManager.Info)this.mList.get(index);
                if(info.getFrame() >= 0) {
                    PositionManager.this.drawAfterFocus(info.getItem(), canvas);
                }
            }

        }

        protected void setBreak(boolean isBreak) {
            Object var2 = this.lock;
            synchronized(this.lock) {
                this.mIsBreak = isBreak;
            }
        }

        protected boolean isBreak() {
            Object var1 = this.lock;
            synchronized(this.lock) {
                return this.mIsBreak;
            }
        }

        protected abstract void drawOut(PositionManager.Info var1, Canvas var2);
    }

    private abstract class Info {
        protected ItemListener mItem;
        protected int mFrame;
        protected int mFrameRate;
        protected Interpolator mInterpolator;

        private Info() {
        }

        public boolean isFinished() {
            return this.getFrame() <= 0 && this.mItem.isFinished();
        }

        public ItemListener getItem() {
            return this.mItem;
        }

        public void subFrame() {
            --this.mFrame;
        }

        public int getFrame() {
            return this.mFrame;
        }

        public int getFrameRate() {
            return this.mFrameRate;
        }

        public Interpolator getInterpolator() {
            return this.mInterpolator;
        }
    }

    class NodeManager {
        List<BaseList> mList = new LinkedList();

        NodeManager() {
        }

        public void init(PositionManager.BaseList list, PositionManager.Info info) {
            synchronized(this) {
                this.mList.add(list);
                list.add(info);
            }

            PositionManager.this.mListener.invalidate();
        }

        public void clear() {
            synchronized(this) {
                for(int index = 0; index < this.mList.size(); ++index) {
                    PositionManager.BaseList list = (PositionManager.BaseList)this.mList.get(index);
                    list.clear();
                }

                this.mList.clear();
            }
        }

        public void release() {
            synchronized(this) {
                this.mList.clear();
            }
        }

        public void remove(ItemListener item) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.BaseList list = (PositionManager.BaseList)this.mList.get(index);
                list.remove(item);
            }

        }

        public void preDrawOut(Canvas canvas) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.BaseList list = (PositionManager.BaseList)this.mList.get(index);
                list.preDrawOut(canvas);
            }

        }

        public void drawOut(Canvas canvas) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.BaseList list = (PositionManager.BaseList)this.mList.get(index);
                list.drawOut(canvas);
            }

        }

        public void postDrawOut(Canvas canvas) {
            for(int index = 0; index < this.mList.size(); ++index) {
                PositionManager.BaseList list = (PositionManager.BaseList)this.mList.get(index);
                list.postDrawOut(canvas);
            }

        }
    }

    class ScaledInfo extends PositionManager.Info {
        float mScaleX;
        float mScaleY;

        public ScaledInfo() {
            super();
        }

        public ScaledInfo(ItemListener item, int frame, int frameRate, float scaleX, float scaleY, Interpolator scaleInterpolator) {
            super();
            this.mItem = item;
            if(item.getScaleX() <= 1.0F && item.getScaleY() <= 1.0F) {
                this.mFrame = -1;
            } else {
                this.mFrame = frame - 1;
            }

            this.mFrameRate = frameRate;
            this.mScaleX = scaleX;
            this.mScaleY = scaleY;
            Log.i(TAG,"scaleX:"+scaleX+" scaleY:"+scaleY);
            this.mInterpolator = scaleInterpolator;
        }

        public float getScaleX() {
            return this.mScaleX;
        }

        public float getScaleY() {
            return this.mScaleY;
        }
    }

    class ScaledList extends PositionManager.BaseList {
        ScaledList() {
            super();
        }

        protected void drawOut(PositionManager.Info info, Canvas canvas) {
            if(info.getFrame() > 0) {
                if(info instanceof PositionManager.ScaledInfo) {
                    PositionManager.ScaledInfo scaledInfo = (PositionManager.ScaledInfo)info;
                    float itemDiffScaleXValue = scaledInfo.getScaleX() - 1.0F;
                    float itemDiffScaleYValue = scaledInfo.getScaleY() - 1.0F;
                    float coef = (float)(scaledInfo.getFrame() - 1) / (float)scaledInfo.getFrameRate();
                    coef = scaledInfo.getInterpolator().getInterpolation(coef);
                    float dstScaleX = 1.0F + itemDiffScaleXValue * coef;
                    float dstScaleY = 1.0F + itemDiffScaleYValue * coef;
                    scaledInfo.getItem().setScaleX(dstScaleX);
                    scaledInfo.getItem().setScaleY(dstScaleY);
                    scaledInfo.subFrame();
                } else {
                    info.subFrame();
                }
            }
        }
    }
}
