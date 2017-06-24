//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.params;

import android.graphics.Rect;
import android.util.Log;

public class FocusRectParams {
    private static final String TAG = FocusRectParams.class.getSimpleName();

    public static final int CENTER_X = 1;
    public static final int CENTER_X_FOCUS = 4;
    public static final int CENTER_Y = 16;
    public static final int CENTER_Y_FOCUS = 64;
    Rect focusRect = new Rect();
    float coefX;
    float coefY;
    private int mCenterMode = CENTER_X_FOCUS | CENTER_Y;

    public FocusRectParams() {
    }

    public FocusRectParams(FocusRectParams p) {
        this.focusRect.set(p.focusRect());
        this.coefX = p.coefX();
        this.coefY = p.coefY();
        this.mCenterMode = p.centerMode();
    }

    public FocusRectParams(Rect r, float x, float y, int c) {
        this.focusRect.set(r);
        this.coefX = x;
        this.coefY = y;
        this.mCenterMode = c;
    }

    public FocusRectParams(Rect r, float x, float y) {
        this.focusRect.set(r);
        this.coefX = x;
        this.coefY = y;
    }

    public void set(Rect r, float x, float y, int c) {
        this.focusRect.set(r);
        this.coefX = x;
        this.coefY = y;
        this.mCenterMode = c;
    }

    public void set(Rect r, float x, float y) {
        this.focusRect.set(r);
        this.coefX = x;
        this.coefY = y;
    }

    public void set(FocusRectParams p) {
        this.focusRect.set(p.focusRect());
        this.coefX = p.coefX();
        this.coefY = p.coefY();
        this.mCenterMode = p.centerMode();
    }

    public Rect focusRect() {
//        Log.d(TAG, "focusRect " + focusRect.toString());
        return this.focusRect;
    }

    public float coefX() {
        return this.coefX;
    }

    public float coefY() {
        return this.coefY;
    }

    public void setCenterMode(int mode) {
        this.mCenterMode = mode;
    }

    public int centerMode() {
        return this.mCenterMode;
    }
}
