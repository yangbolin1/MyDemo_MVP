//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Canvas;
import android.graphics.Rect;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

/**
 * 焦点选中时 特效接口
 */
public interface ItemListener {
    /**
     * 当此接口实现类返回真，setScaleX，getScaleX，setScaleY，getScaleY
     * 有效，并且根据返回的Scale值，放大或缩小控件。
     * @return
     */
    boolean isScale();
    /**
     * 覆盖View 的setScaleX函数。当isScale 返回true函数有效
     * @param scaleX
     */
    void setScaleX(float scaleX);
    /**
     * 覆盖View 的getScaleX函数。当isScale 返回true函数有效
     */
    float getScaleX();
    /**
     * 覆盖View 的setScaleY函数。当isScale 返回true函数有效
     * @param scaleY
     */
    void setScaleY(float scaleY);
    /**
     * 覆盖View 的getScaleY函数。当isScale 返回true函数有效
     */
    float getScaleY();

    FocusRectParams getFocusParams();

    int getItemWidth();

    int getItemHeight();

    Rect getManualPadding();

    /**
     * 在焦点绘制前回调
     * @param canvas
     */
    void drawBeforeFocus(Canvas canvas);

    /**
     * 在焦点绘制后回调
     * @param canvas
     */
    void drawAfterFocus(Canvas canvas);

    boolean isFinished();
}
