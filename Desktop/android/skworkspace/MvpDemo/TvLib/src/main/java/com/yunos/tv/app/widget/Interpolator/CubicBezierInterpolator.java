//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import android.graphics.PointF;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator implements Interpolator {
    protected PointF start;
    protected PointF end;
    protected PointF a;
    protected PointF b;
    protected PointF c;

    public CubicBezierInterpolator(PointF start, PointF end) throws IllegalArgumentException {
        this.a = new PointF();
        this.b = new PointF();
        this.c = new PointF();
        if(start.x >= 0.0F && start.x <= 1.0F) {
            if(end.x >= 0.0F && end.x <= 1.0F) {
                this.start = start;
                this.end = end;
            } else {
                throw new IllegalArgumentException("endX value must be in the range [0, 1]");
            }
        } else {
            throw new IllegalArgumentException("startX value must be in the range [0, 1]");
        }
    }

    public CubicBezierInterpolator(float startX, float startY, float endX, float endY) {
        this(new PointF(startX, startY), new PointF(endX, endY));
    }

    public CubicBezierInterpolator(double startX, double startY, double endX, double endY) {
        this((float)startX, (float)startY, (float)endX, (float)endY);
    }

    public float getInterpolation(float time) {
        return this.getBezierCoordinateY(this.getXForTime(time));
    }

    protected float getBezierCoordinateY(float time) {
        this.c.y = 3.0F * this.start.y;
        this.b.y = 3.0F * (this.end.y - this.start.y) - this.c.y;
        this.a.y = 1.0F - this.c.y - this.b.y;
        return time * (this.c.y + time * (this.b.y + time * this.a.y));
    }

    protected float getXForTime(float time) {
        float x = time;

        for(int i = 1; i < 14; ++i) {
            float z = this.getBezierCoordinateX(x) - time;
            if((double)Math.abs(z) < 0.001D) {
                break;
            }

            x -= z / this.getXDerivate(x);
        }

        return x;
    }

    private float getXDerivate(float t) {
        return this.c.x + t * (2.0F * this.b.x + 3.0F * this.a.x * t);
    }

    private float getBezierCoordinateX(float time) {
        this.c.x = 3.0F * this.start.x;
        this.b.x = 3.0F * (this.end.x - this.start.x) - this.c.x;
        this.a.x = 1.0F - this.c.x - this.b.x;
        return time * (this.c.x + time * (this.b.x + time * this.a.x));
    }
}
