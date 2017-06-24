
package com.ccdt.app.tv.mvpdemo.view.widget.view;


import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import android.util.Log;
import android.view.KeyEvent;

public class FlipLimitGridview extends FocusFlipGridView {
    private static final String TAG = "FlipLimitGridview";
    private boolean mIsScrolling = false;

    public FlipLimitGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public FlipLimitGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0x0);
    }

    public FlipLimitGridview(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mParams =new Params (1.05f,1.05f, 10, null, true, 10, new AccelerateDecelerateFrameInterpolator());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("FlipLimitGridview", "onLayout");
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        isFlipFinished();
        return super.preOnKeyDown(keyCode, event);
    }

//    @Override // FocusFlipGridView
//    public boolean isScrolling() {
//       // return (!mIsScrolling) && (!super.isScrolling());
//        return super.isScrolling();
//    }

    public void setScrolling(boolean scrolling) {
        mIsScrolling = scrolling;
    }

    public void resetFocusParams() {
        super.resetFocusParam();
    }
}
