//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Linear {
    public Linear() {
    }

    public static class EaseNone extends TweenInterpolator {
        public EaseNone() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return c * t / d + b;
        }
    }
}
