//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Back {
    public Back() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158F;
            return c * (t /= d) * t * ((s + 1.0F) * t - s) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158F;
            return (t /= d / 2.0F) < 1.0F?c / 2.0F * t * t * (((s *= 1.525F) + 1.0F) * t - s) + b:c / 2.0F * ((t -= 2.0F) * t * (((s *= 1.525F) + 1.0F) * t + s) + 2.0F) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158F;
            return c * ((t = t / d - 1.0F) * t * ((s + 1.0F) * t + s) + 1.0F) + b;
        }
    }
}
