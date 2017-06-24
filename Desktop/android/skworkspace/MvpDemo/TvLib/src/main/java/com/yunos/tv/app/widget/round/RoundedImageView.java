//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.round;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 圆角inmageView
 */
public class RoundedImageView extends ImageView {
    public static final String TAG = "RoundedImageView";
    protected Paint mBitmapPaint = new Paint();
    private RectF mBitmapRect = new RectF();
    private BitmapShader mBitmapShader;
    protected RectF mBounds = new RectF();
    private int mCornerRadius = 12;
    protected Drawable mDrawable;
    protected Rect mDrawableBounds;
    protected boolean mLayouted = false;
    private boolean mReset;
    private ScaleType mScaleType;
    private Matrix mShaderMatrix;
    RectF mannulBounds;
    Rect mannulPadding;
    protected boolean needHandleRoundImage;

    public synchronized boolean getReset()
    {
        Log.i(TAG,"getReset:"+mReset);
        return mReset;
    }
    public synchronized void setReset(boolean reset)
    {
        this.mReset =reset;
        Log.i(TAG,"setReset:"+mReset);
    }
    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.init();
    }

    /**
     * mScaleType默认ScaleType.FIT_XY
     */
    private void init() {
        this.mScaleType = ScaleType.FIT_XY;
        this.mShaderMatrix = new Matrix();
        this.mannulPadding = new Rect();
        this.needHandleRoundImage = true;
        this.mBitmapPaint.setStyle(Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        Log.i(TAG,"mDrawable:"+mDrawable);
    }

    /**
     * 计算bounds给onDraw绘制
     */
    void computeBounds() {
        if(this.mScaleType.equals(ScaleType.CENTER_INSIDE)) {
            if(this.mDrawable == null) {
                this.mBounds.setEmpty();
                if(this.mDrawableBounds != null) {
                    this.mDrawableBounds.setEmpty();
                }

                if(this.mannulBounds != null) {
                    this.mannulBounds.setEmpty();
                }

                return;
            }

            float scaleParam = 1.0F;
            int picWidth = this.mDrawable.getIntrinsicWidth();
            int picHeight = this.mDrawable.getIntrinsicHeight();
            int showWidth = this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
            int showHeight = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
            if(picWidth > showWidth || picHeight > showHeight) {
                //取出居中方式最大拉伸比例尺
                scaleParam = Math.min((float)showWidth / (float)picWidth, (float)showHeight / (float)picHeight);
            }

            int picScaleWidth = (int)(scaleParam * (float)picWidth);
            int picScaleHeight = (int)(scaleParam * (float)picHeight);
            int widthPadding = (int)(0.5F + 0.5F * (float)(showWidth - picWidth));
            int heightPadding = (int)(0.5F + 0.5F * (float)(showHeight - picHeight));
            int boundLeft = widthPadding + this.getPaddingLeft();
            int boundTop = heightPadding + this.getPaddingTop();
            this.mBounds.set((float)boundLeft, (float)boundTop, (float)(picScaleWidth + boundLeft), (float)(picScaleHeight + boundTop));
        } else {
            this.mBounds.set((float)this.getPaddingLeft(), (float)this.getPaddingTop(), (float)(this.getWidth() - this.getPaddingRight()), (float)(this.getHeight() - this.getPaddingBottom()));
            Log.i(TAG,"computeBounds:mBounds:"+mBounds+" getWidth:"+this.getWidth()+" getPaddingRight:"+this.getPaddingRight());
        }

        if(this.mDrawableBounds != null) {
            this.mDrawableBounds.set((int)this.mBounds.left, (int)this.mBounds.top, (int)this.mBounds.right, (int)this.mBounds.bottom);
        }

        if(this.mannulBounds != null) {
            this.mannulBounds.set(this.mBounds.left + (float)this.mannulPadding.left, this.mBounds.top + (float)this.mannulPadding.top, this.mBounds.right - (float)this.mannulPadding.right, this.mBounds.bottom - (float)this.mannulPadding.bottom);
        }

    }

    /**
     * 由于此处无论如何都会启动invalidate重绘
     */
    @Override //imageView
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }

    /**
     * 获取圆角半径
     * @return
     */
    public int getCornerRadius() {
        return this.mCornerRadius;
    }

    /**
     * 线程安全修正
     */
    @Override //view
    public void invalidate() {
        if(Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            super.invalidate();
        } else {
            super.postInvalidate();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG,"drawable:"+mDrawable);
    }



    @Override //view
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG,"onLayout:"+changed+" l:"+left
                +" t:"+top+" r:"+right+" b:"+bottom+ " getWidth:"+getWidth()+" "+getMeasuredWidth());

    }
    @Override //view
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        this.computeBounds();
        this.mLayouted = true;
        if(this.mReset) {
            this.reset();
            setReset(false);
        }

    }

    @Override //view
    protected void onDraw(Canvas canvas) {
        Log.i(TAG,"onDraw:mDrawable:"+mDrawable);
        if(this.mDrawable != null) {
            if(this.needHandleRoundImage) {
                RectF bounds = this.mBounds;
                //手动边距优先自动计算的边距
                if(this.mannulBounds != null && !mannulBounds.isEmpty()) {
                    bounds = this.mannulBounds;
                }
                Log.i(TAG,"onDraw:mDrawable.draw(canvas)1:bounds:"+bounds);
                if(this.mCornerRadius >= 0) {
                    canvas.drawRoundRect(bounds, (float)this.mCornerRadius, (float)this.mCornerRadius, this.mBitmapPaint);
                } else {
                    canvas.drawRect(bounds, this.mBitmapPaint);
                }
            } else {
                if(this.mDrawableBounds != null) {
                    this.mDrawable.setBounds(this.mDrawableBounds);
                }
                Log.i(TAG,"onDraw:mDrawable.draw(canvas)2");
                this.mDrawable.draw(canvas);
            }
        }

    }

    /**
     * 释放控件 mDrawable=null
     */
    public void release() {
        if(this.mDrawable != null) {
            this.mDrawable.setCallback((Drawable.Callback)null);
            this.mDrawable = null;
        }

        this.mBitmapShader = null;
        this.mShaderMatrix = null;
        this.mBitmapPaint = null;
        this.mBounds = null;
        this.mBitmapRect = null;
    }

    /**
     * 当调用requestLayout，mLayouted=false,避免重复的在onlayout中计算两次绘图参数
     */
    @Override //view
    public void requestLayout() {
        super.requestLayout();
        this.mLayouted = false;
    }

    /**
     * 重新绘制 内部调用invalidate
     */
    public void reset() {
        Log.i(TAG,"reset:mDrawable:"+mDrawable
        +" needHandleRoundImage:"+needHandleRoundImage
        +" ");
        if(this.mDrawable != null && this.needHandleRoundImage && this.mDrawable instanceof BitmapDrawable) {
            Log.i(TAG,"reset:");
            Bitmap bitmap = ((BitmapDrawable)this.mDrawable).getBitmap();
            this.mBitmapRect.set(0.0F, 0.0F, (float)bitmap.getWidth(), (float)bitmap.getHeight());
            this.mShaderMatrix.set((Matrix)null);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, ScaleToFit.FILL);
            //选用边际像素重复的方式，符合.9的规则
            this.mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
            this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
            this.mBitmapPaint.setShader(this.mBitmapShader);
        }

        this.invalidate();
    }

    /**
     * 设置圆角半径，如果不设置此值，默认为12
     * 如果设置为0，为直角
     * @param radius
     */
    public void setCornerRadius(int radius) {
        if(this.needHandleRoundImage && this.mCornerRadius != radius) {
            this.mCornerRadius = radius;
            this.invalidate();
        }

    }

    /**
     * 通过bitmap设置mDarwable
     * @param bitmap
     */
    public void setImageBitmap(Bitmap bitmap) {
        if(bitmap != null) {
            this.mDrawable = new BitmapDrawable(this.getResources(), bitmap);
            //wdz 修改兼容xml
            super.setImageDrawable(mDrawable);
            /////////////
            if(this.mLayouted) {
                this.reset();
            }
            setReset(true);
        }

    }


    /**
     * 圆角ImageView设置图片入口 mDrawable 继承来自父类的imageview
     * @param drawable
     */
    @Override //imageView
    public void setImageDrawable(Drawable drawable) {
        Log.i(TAG,"setImageDrawable:"+drawable);
        this.mDrawable = drawable;
        //wdz 修改兼容xml
        super.setImageDrawable(drawable);
        //////////////////////
        if(this.mLayouted) {
            this.computeBounds();
            this.reset();
        } else {
            setReset(true);
            Log.i(TAG,"setImageDrawable:mReset:"+ mReset);
        }

    }

    @Override
    public void setImageResource(int id)
    {
        super.setImageResource(id);
        mDrawable=super.getDrawable();

        if(this.mLayouted) {
            this.computeBounds();
            this.reset();
        } else {
            setReset(true);
            Log.i(TAG,"setImageDrawable:mReset:"+ mReset);
        }
    }

    /**
     * 设置手动边距，一旦设置此值，自动计算出的边距无效
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setMannulPadding(int left, int top, int right, int bottom) {
        this.mannulPadding.set(left, top, right, bottom);
        if(bottom + right + left + top > 0 && this.mannulBounds == null) {
            this.mannulBounds = new RectF();
        } else {
            this.mannulBounds = null;
        }

    }

    /**
     * 此参数默认初始化为真
     * @param bHandleBound
     */
    public void setNeedHandleRoundImage(boolean bHandleBound) {
        this.needHandleRoundImage = bHandleBound;
        if(!bHandleBound && this.mDrawableBounds == null) {
            this.mDrawableBounds = new Rect();
        } else {
            this.mDrawableBounds = null;
        }

    }

    /**
     * 重置拉伸方式
     * @param scaleType
     */
    public void setScaleType(ScaleType scaleType) {
        this.mScaleType = scaleType;
        if(this.mLayouted && !this.isLayoutRequested()) {
            this.computeBounds();
        }

    }
}