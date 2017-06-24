//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.Circ;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;

public class RotateLayout extends FocusRelativeLayout {
    private static final String TAG = "RotateLayout";
    private static final boolean DEBUG = true;
    private RotateLayout.Info mInfo = new RotateLayout.Info();
    private RotateLayout.ClickInfo mClickInfo = new RotateLayout.ClickInfo();
    private boolean isRun;
    private Camera camera;
    private RectF dstRectF;
    private Matrix comMatrix;
    private DisplayMetrics metrics;
    private RotateLayout.State mState;
    protected int curVisibleIndex;
    protected int mFirstVisiblePositionOnFlow;
    private RotateLayout.RotateListener rotateListener;
    private int originLeft;
    private int originCenter;
    private int shift;
    private static int[] xShift = new int[]{100, 75, 50, 25, 0, 0};
    private static int[] adminDelayList = null;
    private static int adminDelayDetla = 0;
    private int admitDelay;
    private static float[] scaleParams = new float[]{0.9F, 0.85F, 0.8F, 0.75F, 0.7F, 0.65F};
    private int frames;
    public boolean mWaitStartFlow;

    public RotateLayout(Context context) {
        super(context);
        this.mState = RotateLayout.State.normal;
        this.curVisibleIndex = 0;
        this.mFirstVisiblePositionOnFlow = 0;
        this.originLeft = 0;
        this.originCenter = 0;
        this.admitDelay = 0;
        this.frames = 0;
        this.mWaitStartFlow = false;
        this.initView(context);
    }

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = RotateLayout.State.normal;
        this.curVisibleIndex = 0;
        this.mFirstVisiblePositionOnFlow = 0;
        this.originLeft = 0;
        this.originCenter = 0;
        this.admitDelay = 0;
        this.frames = 0;
        this.mWaitStartFlow = false;
        this.initView(context);
    }

    public RotateLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mState = RotateLayout.State.normal;
        this.curVisibleIndex = 0;
        this.mFirstVisiblePositionOnFlow = 0;
        this.originLeft = 0;
        this.originCenter = 0;
        this.admitDelay = 0;
        this.frames = 0;
        this.mWaitStartFlow = false;
        this.initView(context);
    }

    private void initView(Context context) {
        this.camera = new Camera();
        this.metrics = this.getResources().getDisplayMetrics();
    }

    public void setAnimateMoveRadius(int xMoveRadius) {
        this.mInfo.xMoveRadius = xMoveRadius;
    }

    public void setRotateParams(int rotateDegree, int rotateDuration) {
        this.mInfo.rotateDegree = rotateDegree;
        this.mInfo.rotateDuration = rotateDuration;
    }

    public void setAlphaDuration(int alphaDuration) {
        this.mInfo.alphaDuration = alphaDuration;
    }

    public void setScaleDuration(int scaleDuration) {
        this.mInfo.scaleDuration = scaleDuration;
    }

    public void setTranslateDuration(int translateDuration) {
        this.mInfo.transDuration = translateDuration;
    }

    public void setTranslateZ(int translateZ) {
        this.mInfo.translateZ = translateZ;
    }

    public void setRotateListener(RotateLayout.RotateListener l) {
        this.rotateListener = l;
    }

    private int getDelay() {
        int delay = this.admitDelay - this.getAdmitDelayByPosition(this.mFirstVisiblePositionOnFlow);
        return delay > 0?delay:0;
    }

    public void setRotateDelay(int delay) {
        this.admitDelay = delay;
    }

    public static void setRotateDelayList(int[] delayList, int detla) {
        adminDelayList = delayList;
        adminDelayDetla = detla;
    }

    public int getAdmitDelayByPosition(int pos) {
        return adminDelayList != null && pos >= 0?(pos < adminDelayList.length?adminDelayList[pos]:adminDelayList[adminDelayList.length - 1] + (pos - adminDelayList.length + 1) * adminDelayDetla):adminDelayDetla;
    }

    public void setRotateX(float rotateX) {
        this.mInfo.rotateX = rotateX;
    }

    public void setFirstVisiblePositionOnFlow(int pos) {
        this.mFirstVisiblePositionOnFlow = pos;
    }

    public void setCurrentVisibleIndex(int i) {
        this.curVisibleIndex = i;
    }

    public void setShiftX(int shift) {
        this.shift = shift;
    }

    public void setClickParams(int clickScaleDuration, int midScale) {
        this.mClickInfo.clickScaleDuration = clickScaleDuration;
        this.mClickInfo.midScale = (float)midScale;
    }

    public boolean IsRunning() {
        return this.isRun;
    }

    public void start() {
        if(this.isRun && this.mState == RotateLayout.State.click) {
            this.isRun = false;
            this.mState = RotateLayout.State.normal;
            this.mClickInfo.stop();
        }

        if(!this.isRun) {
            this.isRun = true;
            this.mState = RotateLayout.State.rotate;
            this.originLeft = this.getLeft();
            this.originCenter = (this.getLeft() + this.getRight()) / 2;
            int delay = this.getDelay();
            this.postDelayed(new Runnable() {
                public void run() {
                    RotateLayout.this.startRotate();
                }
            }, (long)delay);
        }

        this.invalidate();
    }

    public void stop() {
        this.isRun = false;
        this.mState = RotateLayout.State.normal;
        this.mWaitStartFlow = false;
        this.offsetLeftAndRight(this.originLeft - this.getLeft());
        this.invalidate();
        this.rotateStop();
    }

    public void click() {
        if(!this.isRun) {
            this.isRun = true;
            this.mState = RotateLayout.State.click;
            this.frames = 0;
            this.mClickInfo.start();
        }

        this.invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void startRotate() {
        Log.d("RotateLayout", "index:" + this.curVisibleIndex + ",startRotate:" + this.curVisibleIndex);
        if(this.isRun) {
            if(this.rotateListener != null) {
                this.rotateListener.rotateStart();
            }

            this.frames = 0;
            this.mInfo.start();
        }

    }

    protected void rotateStop() {
        if(this.rotateListener != null) {
            this.rotateListener.rotateStop();
        }

    }

    protected void dispatchDraw(Canvas canvas) {
        if(this.mWaitStartFlow) {
            Log.d("RotateLayout", "forgive invilidate  because wait startflow!!!" + this.curVisibleIndex);
        } else if(this.isRun && !this.mInfo.started && !this.mClickInfo.started) {
            Log.d("RotateLayout", "forgive invilidate because delay!" + this.curVisibleIndex);
        } else {
            if(this.isRun && this.mInfo.started) {
                this.drawRotate(canvas);
            } else if(this.isRun && this.mClickInfo.started) {
                this.drawClick(canvas);
            } else {
                super.dispatchDraw(canvas);
            }

        }
    }

    private void drawClick(Canvas canvas) {
        if(this.mClickInfo.computeOffset()) {
            ++this.frames;
            this.setScaleX(this.mClickInfo.currentS);
            this.setScaleY(this.mClickInfo.currentS);
            super.dispatchDraw(canvas);
            this.invalidate();
        } else {
            Log.d("RotateLayout", "index:" + this.curVisibleIndex + "interval,finished:frames:" + this.frames * 1000 / this.mClickInfo.clickScaleDuration);
            super.dispatchDraw(canvas);
            this.isRun = false;
            this.mState = RotateLayout.State.normal;
            if(this.rotateListener != null) {
                this.rotateListener.clickFinish();
            }
        }

        Log.d("RotateLayout", "index:" + this.curVisibleIndex + "Info:" + this.mClickInfo.toString());
    }

    public boolean isRotateLayoutFinished() {
        return !this.mInfo.started;
    }

    public int getRotateLayoutOffset() {
        boolean hr = this.mInfo.computeOffset();
        if(hr) {
            this.mWaitStartFlow = false;
            return this.mInfo.currentX - (this.getLeft() - this.originLeft);
        } else {
            this.isRun = false;
            this.mState = RotateLayout.State.normal;
            this.rotateStop();
            return 0;
        }
    }

    private void drawRotate(Canvas canvas) {
        if(this.mInfo.started) {
            ++this.frames;
            int saveCount = canvas.save();
            this.rotate(canvas);
            if(this.mInfo.isAlphaRun) {
                canvas.saveLayerAlpha(this.dstRectF, this.mInfo.currentA, 31);
            }

            super.dispatchDraw(canvas);
            canvas.restoreToCount(saveCount);
        } else {
            Log.d("RotateLayout", "index:" + this.curVisibleIndex + "interval,finished:frames:" + this.frames * 1000 / this.mInfo.rotateDuration);
            super.dispatchDraw(canvas);
        }

    }

    private void rotate(Canvas canvas) {
        int centerY = this.getHeight() / 2;
        int centerX = (int)((float)this.getWidth() * this.mInfo.rotateX);
        if(this.dstRectF == null) {
            this.dstRectF = new RectF(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
        }

        this.comMatrix = new Matrix();
        this.comMatrix.reset();
        this.camera.save();
        this.camera.translate(0.0F, 0.0F, (float)this.mInfo.currentZ);
        this.camera.rotateY(this.mInfo.currentDegree);
        this.camera.getMatrix(this.comMatrix);
        this.camera.restore();
        this.comMatrix.preTranslate((float)(-centerX), (float)(-centerY));
        this.comMatrix.postTranslate((float)centerX, (float)centerY);
        canvas.concat(this.comMatrix);
        this.onRotate(this.mInfo.currentDegree);
    }

    protected void onRotate(float angle) {
    }

    public class ClickInfo {
        int clickScaleDuration = 300;
        float startScale = 1.0F;
        float midScale = 0.8F;
        float currentS;
        boolean started;
        Scroller clickScroller = new Scroller(RotateLayout.this.getContext(), new LinearInterpolator());

        public ClickInfo() {
        }

        public void start() {
            this.started = true;
            this.clickScroller.startScroll((int)((this.startScale - this.midScale) * 1000.0F), 0, (int)((this.midScale - this.startScale) * 2000.0F), 0, this.clickScaleDuration);
        }

        public boolean computeOffset() {
            boolean hr = false;
            if(this.clickScroller.computeScrollOffset()) {
                hr = true;
                int x = this.clickScroller.getCurrX();
                if(x > 0) {
                    this.currentS = this.midScale + (float)x / 1000.0F;
                } else {
                    this.currentS = this.midScale - (float)x / 1000.0F;
                }
            }

            this.started = hr;
            return hr;
        }

        public void stop() {
            this.clickScroller.forceFinished(true);
            RotateLayout.this.setScaleX(this.startScale);
            RotateLayout.this.setScaleY(this.startScale);
            this.started = false;
        }

        public String toString() {
            return "S" + this.currentS;
        }
    }

    public class Info {
        int rotateDuration = 1000;
        int alphaDuration = 533;
        int scaleDuration = 900;
        int transDuration = 900;
        int rotateDegree = 60;
        float rotateX = 0.5F;
        int translateZ;
        int zMoveRadius;
        int xMoveRadius;
        int startAlpha = 0;
        int finalAlpha = 255;
        float startScale = 1.0F;
        float finalScale = 1.0F;
        int translateX = 0;
        int currentX = 0;
        float currentDegree = 0.0F;
        int currentZ = 0;
        int currentA = 0;
        float currentS = 0.0F;
        boolean isAlphaRun = false;
        boolean started = false;
        TweenInterpolator degreeInterpolator = new Circ.EaseOut();
        Scroller alphaScroller = new Scroller(RotateLayout.this.getContext(), new LinearInterpolator());
        Scroller scaleScroller = new Scroller(RotateLayout.this.getContext(), new LinearInterpolator());
        Scroller translateScroller = new Scroller(RotateLayout.this.getContext(), new AccelerateDecelerateInterpolator());
        Scroller rotateScroller = new Scroller(RotateLayout.this.getContext(), new LinearInterpolator());

        public Info() {
        }

        public void start() {
            if(this.translateZ != 0 && this.rotateDegree != 0) {
                this.zMoveRadius = (int)((double)this.translateZ / (1.0D - Math.cos((double)this.rotateDegree * 3.141592653589793D / 180.0D)));
            } else {
                this.zMoveRadius = this.xMoveRadius;
            }

            this.translateX = this.getXshift(RotateLayout.this.curVisibleIndex);
            this.isAlphaRun = true;
            this.started = true;
            this.startScale = RotateLayout.scaleParams[0];
            this.alphaScroller.startScroll(this.startAlpha, 0, this.finalAlpha - this.startAlpha, 0, this.alphaDuration);
            this.scaleScroller.startScroll((int)(this.startScale * 1000.0F), 0, (int)((this.finalScale - this.startScale) * 1000.0F), 0, this.scaleDuration);
            this.translateScroller.startScroll(0, 0, this.translateX, 0, this.transDuration);
            this.degreeInterpolator.setDuration(this.rotateDuration);
            this.degreeInterpolator.setStartAndTarget((float)this.rotateDegree, 0.0F);
            this.rotateScroller.startScroll(0, 0, this.rotateDuration, 0, this.rotateDuration);
            this.currentA = this.startAlpha;
            this.currentS = this.startScale * 1000.0F;
            this.currentDegree = (float)this.rotateDegree;
            this.currentX = (int)((double)this.xMoveRadius * Math.sin((double)this.currentDegree * 3.141592653589793D / 180.0D));
            this.currentZ = this.zMoveRadius - (int)((double)this.zMoveRadius * Math.cos((double)this.currentDegree * 3.141592653589793D / 180.0D));
        }

        public int getXshift(int index) {
            return index >= 0 && index < RotateLayout.xShift.length?RotateLayout.xShift[index]:RotateLayout.xShift[RotateLayout.xShift.length - 1];
        }

        public boolean computeOffset() {
            boolean hr = false;
            this.currentX = 0;
            if(this.alphaScroller.computeScrollOffset()) {
                this.currentA = this.alphaScroller.getCurrX();
                hr = true;
            } else {
                this.isAlphaRun = false;
            }

            if(this.scaleScroller.computeScrollOffset()) {
                this.currentS = (float)this.scaleScroller.getCurrX() / 1000.0F;
                hr = true;
            } else {
                this.currentS = 1.0F;
            }

            if(this.rotateScroller.computeScrollOffset()) {
                this.currentDegree = this.degreeInterpolator.getValue(this.rotateScroller.getCurrX());
                this.currentX = (int)((double)this.xMoveRadius * Math.sin((double)this.currentDegree * 3.141592653589793D / 180.0D));
                this.currentZ = this.zMoveRadius - (int)((double)this.zMoveRadius * Math.cos((double)this.currentDegree * 3.141592653589793D / 180.0D));
                hr = true;
            }

            if(this.translateScroller.computeScrollOffset()) {
                hr = true;
            }

            this.started = hr;
            return hr;
        }

        public String toString() {
            return "x:" + this.currentX + ";z:" + this.currentZ + ";a:" + this.currentA + ";d:" + this.currentDegree + ";s:" + this.currentS;
        }
    }

    public interface RotateListener {
        void rotateStart();

        void rotateStop();

        void clickFinish();
    }

    public enum State {
        normal,
        rotate,
        click
    }
}
