package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yunos.tv.app.widget.focus.listener.DrawListener;

/**
 * 静态焦点选择器：<br>
 * 功能:内置一个焦点drawable对象,这个对象即焦点的图片。
 * 之所以叫静态焦点选择器，是相对于动态来说的。isDynamicFocus()永远返回false,
 * 从开始调用
 */
public class StaticFocusDrawable implements DrawListener {
    private static final String TAG="StaticFocusDrawable";
    private float mAlpha = 1.0F;
    Drawable mDrawable;
    Rect mPadding = new Rect();
    Rect mRect = new Rect();
    boolean mVisible = true;

    public StaticFocusDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        drawable.getPadding(this.mPadding);
    }

    public void draw(Canvas canvas) {
        if(this.mDrawable == null) {
            throw new IllegalArgumentException("StaticFocusDrawable: drawable is null");
        } else {
            if(this.mVisible) {
                this.mDrawable.draw(canvas);
            }

        }
    }

    public boolean isDynamicFocus() {
        return false;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
        if(this.mDrawable != null) {
            this.mDrawable.setAlpha((int)(255.0F * this.mAlpha));
        }

    }

    public void setRadius(int radius) {
    }

    public void setRect(Rect rect) {
        this.mRect.set(rect);
        Rect var2 = this.mRect;
        var2.top -= this.mPadding.top;
        Rect var3 = this.mRect;
        var3.left -= this.mPadding.left;
        Rect var4 = this.mRect;
        var4.right += this.mPadding.right;
        Rect var5 = this.mRect;
        var5.bottom += this.mPadding.bottom;
        this.mDrawable.setBounds(this.mRect);
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public void start() {
        Log.i(TAG,"start");
    }

    public void stop() {
        Log.i(TAG,"stop");
    }
}