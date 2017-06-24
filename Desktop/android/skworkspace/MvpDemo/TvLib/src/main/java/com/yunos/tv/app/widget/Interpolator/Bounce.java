//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

/**
 * 弹跳差值器
 */
public class Bounce {
    public Bounce() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return c - (new Bounce.easeOut()).interpolation(d - t, 0.0F, c, d) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return t < d / 2.0F?(new Bounce.EaseIn()).interpolation(t * 2.0F, 0.0F, c, d) * 0.5F + b:(new Bounce.EaseIn()).interpolation(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            return (t /= d) < 0.36363637F?c * 7.5625F * t * t + b:(t < 0.72727275F?c * (7.5625F * (t -= 0.54545456F) * t + 0.75F) + b:((double)t < 0.9090909090909091D?c * (7.5625F * (t -= 0.8181818F) * t + 0.9375F) + b:c * (7.5625F * (t -= 0.95454544F) * t + 0.984375F) + b));
        }
    }
}
