package com.ccdt.app.tv.mvpdemo.view.widget.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * Created by wudz on 2017/4/20.
 */

public class LocalFocusLinearLayout extends FocusLinearLayout {
    private boolean hasFocus=false;
    public LocalFocusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LocalFocusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LocalFocusLinearLayout(Context context) {
        super(context);
        init();
    }
    private void init()
    {
    }

    /**
     * 继承isScale=true可以打开放大效果
     * @return
     */
    @Override //FocusLinearLayout
    public boolean isScale()
    {
        return true;
    }
}
