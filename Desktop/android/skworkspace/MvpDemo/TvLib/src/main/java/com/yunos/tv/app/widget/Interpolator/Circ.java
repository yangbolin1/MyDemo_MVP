package com.yunos.tv.app.widget.Interpolator;

public class Circ {

    public static class EaseIn extends TweenInterpolator {
        public EaseIn() {
        }

        public float interpolation(float var1, float var2, float var3, float var4) {
            float var5 = -var3;
            float var6 = var1 / var4;
            return var2 + var5 * ((float)Math.sqrt((double)(1.0F - var6 * var6)) - 1.0F);
        }
    }

    public static class EaseInOut extends TweenInterpolator {

        public float interpolation(float var1, float var2, float var3, float var4) {
            float var5 = var1 / (var4 / 2.0F);
            float var8;
            if(var5 < 1.0F) {
                var8 = var2 + -var3 / 2.0F * ((float)Math.sqrt((double)(1.0F - var5 * var5)) - 1.0F);
            } else {
                float var6 = var3 / 2.0F;
                float var7 = var5 - 2.0F;
                var8 = var2 + var6 * (1.0F + (float)Math.sqrt((double)(1.0F - var7 * var7)));
            }

            return var8;
        }
    }

    public static class EaseOut extends TweenInterpolator {
        public EaseOut() {
        }

        public float interpolation(float var1, float var2, float var3, float var4) {
            float var5 = var1 / var4 - 1.0F;
            return var2 + var3 * (float)Math.sqrt((double)(1.0F - var5 * var5));
        }
    }
}
