package com.ccdt.app.tv.mvpdemo.view.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_MulImage;
import com.ccdt.app.tv.mvpdemo.view.base.IPanelView;


/**
 * Created by wudz on 2017/3/21.
 */

public class PanelFrameLayout extends FrameLayout implements IPanelView<PanelData_MulImage> {
    private PanelData_MulImage bean;
    public PanelFrameLayout(Context context) {
        super(context);
    }

    public PanelFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void updateViewData(PanelData_MulImage bean) {
        this.bean=bean;
    }

    @Override
    public PanelData_MulImage getViewData() {
        return bean;
    }
}
