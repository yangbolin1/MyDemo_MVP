//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.Interpolator;

import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;

public class Elastic {
    public Elastic() {
    }

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float t, float b, float c, float d) {
            if(t == 0.0F) {
                return b;
            } else if((t /= d) == 1.0F) {
                return b + c;
            } else {
                float p = d * 0.3F;
                float s = p / 4.0F;
                return -(c * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p))) + b;
            }
        }
    }

    public static class easeInOut extends TweenInterpolator {
        public easeInOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            if(t == 0.0F) {
                return b;
            } else if((t /= d / 2.0F) == 2.0F) {
                return b + c;
            } else {
                float p = d * 0.45000002F;
                float s = p / 4.0F;
                return t < 1.0F?-0.5F * c * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + b:c * (float)Math.pow(2.0D, (double)(-10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) * 0.5F + c + b;
            }
        }
    }

    public static class easeOut extends TweenInterpolator {
        public easeOut() {
        }

        public float interpolation(float t, float b, float c, float d) {
            if(t == 0.0F) {
                return b;
            } else if((t /= d) == 1.0F) {
                return b + c;
            } else {
                float p = d * 0.3F;
                float s = p / 4.0F;
                return c * (float)Math.pow(2.0D, (double)(-10.0F * t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + c + b;
            }
        }
    }
}
