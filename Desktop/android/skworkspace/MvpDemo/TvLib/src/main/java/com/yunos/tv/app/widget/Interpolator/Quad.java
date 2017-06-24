//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Quad {
    public Quad() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return c * (t /= d) * t + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F?c / 2.0F * t * t + b:-c / 2.0F * (--t * (t - 2.0F) - 1.0F) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return -c * (t /= d) * (t - 2.0F) + b;
        }
    }
}
