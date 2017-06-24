//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Sine {
    public Sine() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return -c * (float)Math.cos((double)(t / d) * 1.5707963267948966D) + c + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return -c / 2.0F * ((float)Math.cos(3.141592653589793D * (double)t / (double)d) - 1.0F) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return c * (float)Math.sin((double)(t / d) * 1.5707963267948966D) + b;
        }
    }
}
