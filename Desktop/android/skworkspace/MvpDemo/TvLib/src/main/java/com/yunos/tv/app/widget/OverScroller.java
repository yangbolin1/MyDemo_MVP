package com.yunos.tv.app.widget;


import android.content.Context;
import android.util.FloatMath;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class OverScroller {
    private int mMode;
    private final OverScroller.SplineOverScroller mScrollerX;
    private final OverScroller.SplineOverScroller mScrollerY;
    private Interpolator mInterpolator;
    private final boolean mFlywheel;
    private static final int DEFAULT_DURATION = 250;
    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;
    private static float sViscousFluidScale = 8.0F;
    private static float sViscousFluidNormalize = 1.0F;

    static {
        sViscousFluidNormalize = 1.0F / viscousFluid(1.0F);
    }

    public OverScroller(Context context) {
        this(context, (Interpolator) null);
    }

    public OverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public OverScroller(Context context, Interpolator interpolator, boolean flywheel) {
        this.mInterpolator = interpolator;
        this.mFlywheel = flywheel;
        this.mScrollerX = new OverScroller.SplineOverScroller(context);
        this.mScrollerY = new OverScroller.SplineOverScroller(context);
    }

    public OverScroller(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY) {
        this(context, interpolator, true);
    }

    public OverScroller(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
        this(context, interpolator, flywheel);
    }

    void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public final void setFriction(float friction) {
        this.mScrollerX.setFriction(friction);
        this.mScrollerY.setFriction(friction);
    }

    public final boolean isFinished() {
        return this.mScrollerX.mFinished && this.mScrollerY.mFinished;
    }

    public final void forceFinished(boolean finished) {
        OverScroller.SplineOverScroller var10000 = this.mScrollerX;
        this.mScrollerY.mFinished = finished;
        var10000.mFinished = finished;
    }

    public final int getCurrX() {
        return this.mScrollerX.mCurrentPosition;
    }

    public final int getCurrY() {
        return this.mScrollerY.mCurrentPosition;
    }

    public float getCurrVelocity() {
        float squaredNorm = this.mScrollerX.mCurrVelocity * this.mScrollerX.mCurrVelocity;
        squaredNorm += this.mScrollerY.mCurrVelocity * this.mScrollerY.mCurrVelocity;
        return (float)Math.sqrt(squaredNorm);

//        return FloatMath.sqrt(squaredNorm);
    }

    public final int getStartX() {
        return this.mScrollerX.mStart;
    }

    public final int getStartY() {
        return this.mScrollerY.mStart;
    }

    public final int getFinalX() {
        return this.mScrollerX.mFinal;
    }

    public final int getFinalY() {
        return this.mScrollerY.mFinal;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public final int getDuration() {
        return Math.max(this.mScrollerX.mDuration, this.mScrollerY.mDuration);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void extendDuration(int extend) {
        this.mScrollerX.extendDuration(extend);
        this.mScrollerY.extendDuration(extend);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setFinalX(int newX) {
        this.mScrollerX.setFinalPosition(newX);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setFinalY(int newY) {
        this.mScrollerY.setFinalPosition(newY);
    }

    public boolean computeScrollOffset() {
        if (this.isFinished()) {
            return false;
        } else {
            switch (this.mMode) {
                case 0:
                    long time = AnimationUtils.currentAnimationTimeMillis();
                    long elapsedTime = time - this.mScrollerX.mStartTime;
                    int duration = this.mScrollerX.mDuration;
                    if (elapsedTime < (long) duration) {
                        float q = (float) elapsedTime / (float) duration;
                        if (this.mInterpolator == null) {
                            q = viscousFluid(q);
                        } else {
                            q = this.mInterpolator.getInterpolation(q);
                        }

                        this.mScrollerX.updateScroll(q);
                        this.mScrollerY.updateScroll(q);
                    } else {
                        this.abortAnimation();
                    }
                    break;
                case 1:
                    if (!this.mScrollerX.mFinished && !this.mScrollerX.update() && !this.mScrollerX.continueWhenFinished()) {
                        this.mScrollerX.finish();
                    }

                    if (!this.mScrollerY.mFinished && !this.mScrollerY.update() && !this.mScrollerY.continueWhenFinished()) {
                        this.mScrollerY.finish();
                    }
            }

            return true;
        }
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        this.startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mScrollerX.startScroll(startX, dx, duration);
        this.mScrollerY.startScroll(startY, dy, duration);
    }

    public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        this.mMode = 1;
        boolean spingbackX = this.mScrollerX.springback(startX, minX, maxX);
        boolean spingbackY = this.mScrollerY.springback(startY, minY, maxY);
        return spingbackX || spingbackY;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        this.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
        if (this.mFlywheel && !this.isFinished()) {
            float oldVelocityX = this.mScrollerX.mCurrVelocity;
            float oldVelocityY = this.mScrollerY.mCurrVelocity;
            if (Math.signum((float) velocityX) == Math.signum(oldVelocityX) && Math.signum((float) velocityY) == Math.signum(oldVelocityY)) {
                velocityX = (int) ((float) velocityX + oldVelocityX);
                velocityY = (int) ((float) velocityY + oldVelocityY);
            }
        }

        this.mMode = 1;
        this.mScrollerX.fling(startX, velocityX, minX, maxX, overX);
        this.mScrollerY.fling(startY, velocityY, minY, maxY, overY);
    }

    public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {
        this.mScrollerX.notifyEdgeReached(startX, finalX, overX);
    }

    public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
        this.mScrollerY.notifyEdgeReached(startY, finalY, overY);
    }

    public boolean isOverScrolled() {
        return !this.mScrollerX.mFinished && this.mScrollerX.mState != 0 || !this.mScrollerY.mFinished && this.mScrollerY.mState != 0;
    }

    public void abortAnimation() {
        this.mScrollerX.finish();
        this.mScrollerY.finish();
    }

    public int timePassed() {
        long time = AnimationUtils.currentAnimationTimeMillis();
        long startTime = Math.min(this.mScrollerX.mStartTime, this.mScrollerY.mStartTime);
        return (int) (time - startTime);
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        int dx = this.mScrollerX.mFinal - this.mScrollerX.mStart;
        int dy = this.mScrollerY.mFinal - this.mScrollerY.mStart;
        return !this.isFinished() && Math.signum(xvel) == Math.signum((float) dx) && Math.signum(yvel) == Math.signum((float) dy);
    }

    static float viscousFluid(float x) {
        x *= sViscousFluidScale;
        if (x < 1.0F) {
            x -= 1.0F - (float) Math.exp((double) (-x));
        } else {
            float start = 0.36787945F;
            x = 1.0F - (float) Math.exp((double) (1.0F - x));
            x = start + x * (1.0F - start);
        }

        x *= sViscousFluidNormalize;
        return x;
    }

    static class SplineOverScroller {
        private int mStart;
        private int mCurrentPosition;
        private int mFinal;
        private int mVelocity;
        private float mCurrVelocity;
        private float mDeceleration;
        private long mStartTime;
        private int mDuration;
        private int mSplineDuration;
        private int mSplineDistance;
        private boolean mFinished = true;
        private int mOver;
        private float mFlingFriction = ViewConfiguration.getScrollFriction();
        private int mState = 0;
        private static final float GRAVITY = 2000.0F;
        private float mPhysicalCoeff;
        private static float DECELERATION_RATE = (float) (Math.log(0.78D) / Math.log(0.9D));
        private static final float INFLEXION = 0.35F;
        private static final float START_TENSION = 0.5F;
        private static final float END_TENSION = 1.0F;
        private static final float P1 = 0.175F;
        private static final float P2 = 0.35000002F;
        private static final int NB_SAMPLES = 100;
        private static final float[] SPLINE_POSITION = new float[101];
        private static final float[] SPLINE_TIME = new float[101];
        private static final int SPLINE = 0;
        private static final int CUBIC = 1;
        private static final int BALLISTIC = 2;

        static {
            float x_min = 0.0F;
            float y_min = 0.0F;

            label36:
            for (int i = 0; i < 100; ++i) {
                float alpha = (float) i / 100.0F;
                float x_max = 1.0F;

                while (true) {
                    float x = x_min + (x_max - x_min) / 2.0F;
                    float coef = 3.0F * x * (1.0F - x);
                    float tx = coef * ((1.0F - x) * 0.175F + x * 0.35000002F) + x * x * x;
                    if ((double) Math.abs(tx - alpha) < 1.0E-5D) {
                        SPLINE_POSITION[i] = coef * ((1.0F - x) * 0.5F + x) + x * x * x;
                        float y_max = 1.0F;

                        while (true) {
                            float y = y_min + (y_max - y_min) / 2.0F;
                            coef = 3.0F * y * (1.0F - y);
                            float dy = coef * ((1.0F - y) * 0.5F + y) + y * y * y;
                            if ((double) Math.abs(dy - alpha) < 1.0E-5D) {
                                SPLINE_TIME[i] = coef * ((1.0F - y) * 0.175F + y * 0.35000002F) + y * y * y;
                                continue label36;
                            }

                            if (dy > alpha) {
                                y_max = y;
                            } else {
                                y_min = y;
                            }
                        }
                    }

                    if (tx > alpha) {
                        x_max = x;
                    } else {
                        x_min = x;
                    }
                }
            }

            SPLINE_POSITION[100] = SPLINE_TIME[100] = 1.0F;
        }

        void setFriction(float friction) {
            this.mFlingFriction = friction;
        }

        SplineOverScroller(Context context) {
            float ppi = context.getResources().getDisplayMetrics().density * 160.0F;
            this.mPhysicalCoeff = 386.0878F * ppi * 0.84F;
        }

        void updateScroll(float q) {
            this.mCurrentPosition = this.mStart + Math.round(q * (float) (this.mFinal - this.mStart));
        }

        private static float getDeceleration(int velocity) {
            return velocity > 0 ? -2000.0F : 2000.0F;
        }

        private void adjustDuration(int start, int oldFinal, int newFinal) {
            int oldDistance = oldFinal - start;
            int newDistance = newFinal - start;
            float x = Math.abs((float) newDistance / (float) oldDistance);
            int index = (int) (100.0F * x);
            if (index < 100) {
                float x_inf = (float) index / 100.0F;
                float x_sup = (float) (index + 1) / 100.0F;
                float t_inf = SPLINE_TIME[index];
                float t_sup = SPLINE_TIME[index + 1];
                float timeCoef = t_inf + (x - x_inf) / (x_sup - x_inf) * (t_sup - t_inf);
                this.mDuration = (int) ((float) this.mDuration * timeCoef);
            }

        }

        void startScroll(int start, int distance, int duration) {
            this.mFinished = false;
            this.mStart = start;
            this.mFinal = start + distance;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = duration;
            this.mDeceleration = 0.0F;
            this.mVelocity = 0;
        }

        void finish() {
            this.mCurrentPosition = this.mFinal;
            this.mFinished = true;
        }

        void setFinalPosition(int position) {
            this.mFinal = position;
            this.mFinished = false;
        }

        void extendDuration(int extend) {
            long time = AnimationUtils.currentAnimationTimeMillis();
            int elapsedTime = (int) (time - this.mStartTime);
            this.mDuration = elapsedTime + extend;
            this.mFinished = false;
        }

        boolean springback(int start, int min, int max) {
            this.mFinished = true;
            this.mStart = this.mFinal = start;
            this.mVelocity = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 0;
            if (start < min) {
                this.startSpringback(start, min, 0);
            } else if (start > max) {
                this.startSpringback(start, max, 0);
            }

            return !this.mFinished;
        }

        private void startSpringback(int start, int end, int velocity) {
            this.mFinished = false;
            this.mState = 1;
            this.mStart = start;
            this.mFinal = end;
            int delta = start - end;
            this.mDeceleration = getDeceleration(delta);
            this.mVelocity = -delta;
            this.mOver = Math.abs(delta);
            this.mDuration = (int) (1000.0D * Math.sqrt(-2.0D * (double) delta / (double) this.mDeceleration));
        }

        void fling(int start, int velocity, int min, int max, int over) {
            this.mOver = over;
            this.mFinished = false;
            this.mCurrVelocity = (float) (this.mVelocity = velocity);
            this.mDuration = this.mSplineDuration = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mCurrentPosition = this.mStart = start;
            if (start <= max && start >= min) {
                this.mState = 0;
                double totalDistance = 0.0D;
                if (velocity != 0) {
                    this.mDuration = this.mSplineDuration = this.getSplineFlingDuration(velocity);
                    totalDistance = this.getSplineFlingDistance(velocity);
                }

                this.mSplineDistance = (int) (totalDistance * (double) Math.signum((float) velocity));
                this.mFinal = start + this.mSplineDistance;
                if (this.mFinal < min) {
                    this.adjustDuration(this.mStart, this.mFinal, min);
                    this.mFinal = min;
                }

                if (this.mFinal > max) {
                    this.adjustDuration(this.mStart, this.mFinal, max);
                    this.mFinal = max;
                }

            } else {
                this.startAfterEdge(start, min, max, velocity);
            }
        }

        private double getSplineDeceleration(int velocity) {
            return Math.log((double) (0.35F * (float) Math.abs(velocity) / (this.mFlingFriction * this.mPhysicalCoeff)));
        }

        private double getSplineFlingDistance(int velocity) {
            double l = this.getSplineDeceleration(velocity);
            double decelMinusOne = (double) DECELERATION_RATE - 1.0D;
            return (double) (this.mFlingFriction * this.mPhysicalCoeff) * Math.exp((double) DECELERATION_RATE / decelMinusOne * l);
        }

        private int getSplineFlingDuration(int velocity) {
            double l = this.getSplineDeceleration(velocity);
            double decelMinusOne = (double) DECELERATION_RATE - 1.0D;
            return (int) (1000.0D * Math.exp(l / decelMinusOne));
        }

        private void fitOnBounceCurve(int start, int end, int velocity) {
            float durationToApex = (float) (-velocity) / this.mDeceleration;
            float distanceToApex = (float) (velocity * velocity) / 2.0F / Math.abs(this.mDeceleration);
            float distanceToEdge = (float) Math.abs(end - start);
            float totalDuration = (float) Math.sqrt(2.0D * (double) (distanceToApex + distanceToEdge) / (double) Math.abs(this.mDeceleration));
            this.mStartTime -= (long) ((int) (1000.0F * (totalDuration - durationToApex)));
            this.mStart = end;
            this.mVelocity = (int) (-this.mDeceleration * totalDuration);
        }

        private void startBounceAfterEdge(int start, int end, int velocity) {
            this.mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
            this.fitOnBounceCurve(start, end, velocity);
            this.onEdgeReached();
        }

        private void startAfterEdge(int start, int min, int max, int velocity) {
            if (start > min && start < max) {
                Log.e("OverScroller", "startAfterEdge called from a valid position");
                this.mFinished = true;
            } else {
                boolean positive = start > max;
                int edge = positive ? max : min;
                int overDistance = start - edge;
                boolean keepIncreasing = overDistance * velocity >= 0;
                if (keepIncreasing) {
                    this.startBounceAfterEdge(start, edge, velocity);
                } else {
                    double totalDistance = this.getSplineFlingDistance(velocity);
                    if (totalDistance > (double) Math.abs(overDistance)) {
                        this.fling(start, velocity, positive ? min : start, positive ? start : max, this.mOver);
                    } else {
                        this.startSpringback(start, edge, velocity);
                    }
                }

            }
        }

        void notifyEdgeReached(int start, int end, int over) {
            if (this.mState == 0) {
                this.mOver = over;
                this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                this.startAfterEdge(start, end, end, (int) this.mCurrVelocity);
            }

        }

        private void onEdgeReached() {
            float distance = (float) (this.mVelocity * this.mVelocity) / (2.0F * Math.abs(this.mDeceleration));
            float sign = Math.signum((float) this.mVelocity);
            if (distance > (float) this.mOver) {
                this.mDeceleration = -sign * (float) this.mVelocity * (float) this.mVelocity / (2.0F * (float) this.mOver);
                distance = (float) this.mOver;
            }

            this.mOver = (int) distance;
            this.mState = 2;
            this.mFinal = this.mStart + (int) (this.mVelocity > 0 ? distance : -distance);
            this.mDuration = -((int) (1000.0F * (float) this.mVelocity / this.mDeceleration));
        }

        boolean continueWhenFinished() {
            switch (this.mState) {
                case 0:
                    if (this.mDuration >= this.mSplineDuration) {
                        return false;
                    }

                    this.mStart = this.mFinal;
                    this.mVelocity = (int) this.mCurrVelocity;
                    this.mDeceleration = getDeceleration(this.mVelocity);
                    this.mStartTime += (long) this.mDuration;
                    this.onEdgeReached();
                    break;
                case 1:
                    return false;
                case 2:
                    this.mStartTime += (long) this.mDuration;
                    this.startSpringback(this.mFinal, this.mStart, 0);
            }

            this.update();
            return true;
        }

        boolean update() {
            long time = AnimationUtils.currentAnimationTimeMillis();
            long currentTime = time - this.mStartTime;
            if (currentTime > (long) this.mDuration) {
                return false;
            } else {
                double distance = 0.0D;
                float t;
                float sign;
                switch (this.mState) {
                    case 0:
                        t = (float) currentTime / (float) this.mSplineDuration;
                        int t21 = (int) (100.0F * t);
                        sign = 1.0F;
                        float velocityCoef = 0.0F;
                        if (t21 < 100) {
                            float t_inf = (float) t21 / 100.0F;
                            float t_sup = (float) (t21 + 1) / 100.0F;
                            float d_inf = SPLINE_POSITION[t21];
                            float d_sup = SPLINE_POSITION[t21 + 1];
                            velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
                            sign = d_inf + (t - t_inf) * velocityCoef;
                        }

                        distance = (double) (sign * (float) this.mSplineDistance);
                        this.mCurrVelocity = velocityCoef * (float) this.mSplineDistance / (float) this.mSplineDuration * 1000.0F;
                        break;
                    case 1:
                        t = (float) currentTime / (float) this.mDuration;
                        float t2 = t * t;
                        sign = Math.signum((float) this.mVelocity);
                        distance = (double) (sign * (float) this.mOver * (3.0F * t2 - 2.0F * t * t2));
                        this.mCurrVelocity = sign * (float) this.mOver * 6.0F * (-t + t2);
                        break;
                    case 2:
                        t = (float) currentTime / 1000.0F;
                        this.mCurrVelocity = (float) this.mVelocity + this.mDeceleration * t;
                        distance = (double) ((float) this.mVelocity * t + this.mDeceleration * t * t / 2.0F);
                }

                this.mCurrentPosition = this.mStart + (int) Math.round(distance);
                return true;
            }
        }
    }
}
