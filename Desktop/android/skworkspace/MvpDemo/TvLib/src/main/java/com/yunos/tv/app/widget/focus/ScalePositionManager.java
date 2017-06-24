package com.yunos.tv.app.widget.focus;

import android.graphics.Rect;
import android.graphics.RectF;

public class ScalePositionManager {
    private static ScalePositionManager manager = null;

    public ScalePositionManager() {
    }

    public static ScalePositionManager instance() {
        if(manager == null) {
            manager = new ScalePositionManager();
        }

        return manager;
    }

    public Rect getScaledRect(Rect r, float scaleX, float scaleY) {
        Rect rScaled = new Rect();
        int imgW = r.width();
        int imgH = r.height();
        rScaled.left = (int)((float)r.left - (scaleX - 1.0F) * (float)imgW / 2.0F);
        rScaled.right = (int)((float)r.left + (float)imgW * scaleX);
        rScaled.top = (int)((float)r.top - (scaleY - 1.0F) * (float)imgH / 2.0F);
        rScaled.bottom = (int)((float)r.top + (float)imgH * scaleY);
        return rScaled;
    }

    public Rect getScaledRect(Rect r, float scaleX, float scaleY, float coefX, float coefY) {
        int width = r.width();
        int height = r.height();
        float diffScaleX = scaleX - 1.0F;
        float diffScaleY = scaleY - 1.0F;
        r.left = (int)((float)r.left - (float)width * coefX * diffScaleX);
        r.right = (int)((float)r.right + (float)width * (1.0F - coefX) * diffScaleX);
        r.top = (int)((float)r.top - (float)height * coefY * diffScaleY);
        r.bottom = (int)((float)r.bottom + (float)height * (1.0F - coefY) * diffScaleY);
        return r;
    }

    public void getScaledRect(Rect src, Rect dst, float scaleX, float scaleY, float coefX, float coefY) {
        int width = src.width();
        int height = src.height();
        float diffScaleX = scaleX - 1.0F;
        float diffScaleY = scaleY - 1.0F;
        dst.left = (int)((float)src.left - (float)width * coefX * diffScaleX);
        dst.right = (int)((float)src.right + (float)width * (1.0F - coefX) * diffScaleX);
        dst.top = (int)((float)src.left - (float)height * coefY * diffScaleY);
        dst.bottom = (int)((float)src.bottom + (float)height * (1.0F - coefY) * diffScaleY);
    }

    public void getScaledRect(RectF src, RectF dst, float scaleX, float scaleY, float coefX, float coefY) {
        float width = src.right - src.left;
        float height = src.bottom - src.top;
        float diffScaleX = scaleX - 1.0F;
        float diffScaleY = scaleY - 1.0F;
        dst.left = src.left - width * coefX * diffScaleX;
        dst.right = src.right + width * (1.0F - coefX) * diffScaleX;
        dst.top = src.left - height * coefY * diffScaleY;
        dst.bottom = src.bottom + height * (1.0F - coefY) * diffScaleY;
    }

    public void getScaledRectNoReturn(Rect r, float scaleX, float scaleY) {
        int imgW = r.width();
        int imgH = r.height();
        r.left = (int)((float)r.left - (scaleX - 1.0F) * (float)imgW / 2.0F);
        r.right = (int)((float)r.left + (float)imgW * scaleX);
        r.top = (int)((float)r.top - (scaleY - 1.0F) * (float)imgH / 2.0F);
        r.bottom = (int)((float)r.top + (float)imgH * scaleY);
    }
}
