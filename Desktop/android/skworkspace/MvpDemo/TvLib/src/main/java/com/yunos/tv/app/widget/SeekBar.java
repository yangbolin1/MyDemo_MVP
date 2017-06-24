//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

public class SeekBar extends View {
    private int maxValue = 100;
    private SeekBar.SeekDrawable mBackgroundDrawable = new SeekBar.SeekDrawable();
    private SeekBar.SeekDrawable mProgressDrawable = new SeekBar.SeekDrawable();
    private SeekBar.SeekDrawable mSecProgressDrawable = new SeekBar.SeekDrawable();
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    public SeekBar(Context context) {
        super(context);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMaxValue(int max) {
        this.maxValue = max;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        this.mOnSeekBarChangeListener = l;
    }

    public void setDrawable(Drawable background, Drawable progress, Drawable secProgress) {
        this.mBackgroundDrawable.setDrawable(background);
        this.mBackgroundDrawable.setProgress(this.maxValue);
        this.mProgressDrawable.setDrawable(progress);
        this.mSecProgressDrawable.setDrawable(secProgress);
        this.invalidate();
    }

    public void enableRadius(boolean enable) {
        this.mBackgroundDrawable.enableRadius(enable);
        this.mProgressDrawable.enableRadius(enable);
        this.mSecProgressDrawable.enableRadius(enable);
    }

    public int getProgressRadius() {
        if(this.mProgressDrawable == null) {
            throw new NullPointerException("You did not set progress drawable by setDrawable");
        } else {
            return this.mProgressDrawable.getRadius();
        }
    }

    public int getSecondaryProgressRadius() {
        if(this.mSecProgressDrawable == null) {
            throw new NullPointerException("You did not set secondary progress drawable by setDrawable");
        } else {
            return this.mSecProgressDrawable.getRadius();
        }
    }

    public int getBackgroundRadius() {
        if(this.mBackgroundDrawable == null) {
            throw new NullPointerException("You did not set background drawable by setDrawable");
        } else {
            return this.mBackgroundDrawable.getRadius();
        }
    }

    public int getProgressWidth() {
        if(this.mProgressDrawable == null) {
            throw new NullPointerException("You did not set progress drawable by setDrawable");
        } else {
            return this.mProgressDrawable.getWidth();
        }
    }

    public int getSecondaryProgressWidth() {
        if(this.mSecProgressDrawable == null) {
            throw new NullPointerException("You did not set secondary progress drawable by setDrawable");
        } else {
            return this.mSecProgressDrawable.getWidth();
        }
    }

    public int getBackgroundWidth() {
        if(this.mBackgroundDrawable == null) {
            throw new NullPointerException("You did not set background drawable by setDrawable");
        } else {
            return this.mBackgroundDrawable.getWidth();
        }
    }

    public void setProgress(int progress) {
        if(progress < 0) {
            progress = 0;
        }

        if(progress > this.maxValue) {
            progress = this.maxValue;
        }

        if(this.mProgressDrawable.getProgress() != progress) {
            this.mProgressDrawable.setProgress(progress);
            this.refreshProgress();
            this.invalidate();
        }
    }

    public int getProgress() {
        return this.mProgressDrawable.getProgress();
    }

    public void setSecProgress(int progress) {
        if(this.mSecProgressDrawable.getProgress() != progress) {
            this.mSecProgressDrawable.setProgress(progress);
            this.invalidate();
        }
    }

    public void refreshProgress() {
        if(this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onProgressChanged(this, this.getProgress(), false);
        }

    }

    public void invalidate() {
        if(Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            super.invalidate();
        } else {
            super.postInvalidate();
        }

    }

    protected void onDraw(Canvas canvas) {
        this.mBackgroundDrawable.draw(canvas);
        this.mSecProgressDrawable.draw(canvas);
        this.mProgressDrawable.draw(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mBackgroundDrawable.onMessure();
        this.mSecProgressDrawable.onMessure();
        this.mProgressDrawable.onMessure();
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar var1, int var2, boolean var3);

        void onStartTrackingTouch(SeekBar var1);

        void onStopTrackingTouch(SeekBar var1);
    }

    class SeekDrawable {
        private Paint mBitmapPaint = new Paint();
        private BitmapShader mBitmapShader;
        private Matrix mShaderMatrix = new Matrix();
        BitmapDrawable mDrawable;
        RectF mRect = new RectF();
        RectF mBitmapRect = new RectF();
        int mProgress = 0;
        int mRadius = 50;
        boolean mEnableRadius = true;
        int mPadding = 0;
        int mLastProgress = -1;
        boolean mIsMessured = false;

        public SeekDrawable() {
        }

        public SeekDrawable(Drawable d) {
            this.mDrawable = (BitmapDrawable)d;
            this.initMatrix();
        }

        public void setDrawable(Drawable d) {
            this.mDrawable = (BitmapDrawable)d;
            this.initMatrix();
        }

        public void enableRadius(boolean enable) {
            this.mEnableRadius = enable;
        }

        public void onMessure() {
            this.mPadding = (SeekBar.this.getMeasuredHeight() - this.mDrawable.getBitmap().getHeight()) / 2;
            this.mRadius = this.mEnableRadius?this.mDrawable.getBitmap().getHeight() / 2:0;
            this.mIsMessured = true;
        }

        public int getRadius() {
            return this.mEnableRadius?this.mRadius:0;
        }

        public int getWidth() {
            return (int)this.mRect.width();
        }

        private void initMatrix() {
            this.mBitmapPaint.setStyle(Style.FILL);
            this.mBitmapPaint.setAntiAlias(true);
            this.mBitmapShader = new BitmapShader(this.mDrawable.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
            this.mShaderMatrix.set((Matrix)null);
            this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
            this.mBitmapPaint.setShader(this.mBitmapShader);
            this.mBitmapRect.set(0.0F, 0.0F, (float)this.mDrawable.getBitmap().getWidth(), (float)this.mDrawable.getBitmap().getHeight());
        }

        public void setProgress(int progress) {
            this.mProgress = progress;
            this.updateRect();
        }

        public int getProgress() {
            return this.mProgress;
        }

        private void updateRect() {
            if(this.mIsMessured) {
                if(this.mLastProgress != this.mProgress) {
                    int diameter = this.mRadius * 2;
                    int width = diameter + (SeekBar.this.getWidth() - diameter) * this.mProgress / SeekBar.this.maxValue;
                    int offset = SeekBar.this.getWidth() - width;
                    this.mRect.set((float)(-offset), (float)this.mPadding, (float)width, (float)(SeekBar.this.getHeight() - this.mPadding));
                    this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mRect, ScaleToFit.FILL);
                    this.mRect.left = 0.0F;
                    this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
                }

                this.mLastProgress = this.mProgress;
            }
        }

        public void draw(Canvas canvas) {
            if(this.mDrawable != null) {
                this.updateRect();
                if(this.mEnableRadius) {
                    canvas.drawRoundRect(this.mRect, (float)this.mRadius, (float)this.mRadius, this.mBitmapPaint);
                } else {
                    canvas.drawRect(this.mRect, this.mBitmapPaint);
                }

            }
        }
    }
}
