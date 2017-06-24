//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Expo {
    public Expo() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return t == 0.0F?b:c * (float)Math.pow(2.0D, (double)(10.0F * (t / d - 1.0F))) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return t == 0.0F?b:(t == d?b + c:((t /= d / 2.0F) < 1.0F?c / 2.0F * (float)Math.pow(2.0D, (double)(10.0F * (t - 1.0F))) + b:c / 2.0F * (-((float)Math.pow(2.0D, (double)(-10.0F * --t))) + 2.0F) + b));
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return t == d?b + c:c * (-((float)Math.pow(2.0D, (double)(-10.0F * t / d))) + 1.0F) + b;
        }
    }
}
