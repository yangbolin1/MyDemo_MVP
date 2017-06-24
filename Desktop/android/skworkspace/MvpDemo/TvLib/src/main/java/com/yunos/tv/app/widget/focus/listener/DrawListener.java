//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 选择器绘制接口，当FocusPointManager需要绘制焦点时，会回调此接口
 */
public interface DrawListener {
    /**
     * 通过返回结果告诉焦点绘制器是否是动画焦点，
     * 如果返回真，无论焦点是否移动，draw一直被触发，可以通过此返回值设计呼吸效果焦点。<br>
     * 如果返回假，焦点在绘制过程中，draw被触发，否则停止触发。
     * @return
     */
    boolean isDynamicFocus();

    /**
     * 通知选择器，改变绘制焦点矩形大小
     * @param rect
     */
    void setRect(Rect rect);

    /**
     * 通知选择器，改变绘制焦点半径
     * @param radius
     */
    void setRadius(int radius);

    /**
     * 通知选择器，焦点准备开始绘制
     */
    void start();

    /**
     * 通知选择器，焦点停止绘制
     */
    void stop();

    /**
     * 通知选择器，焦点是否显示
     * @param visible
     */
    void setVisible(boolean visible);

    /**
     * 通知选择器，焦点的透明度发生变化
     * @param alpha
     */
    void setAlpha(float alpha);

    /**
     * 通知选择器，焦点需要绘制。<br>
     * 一般情况下，我们需要保存上面回调函数的rect,radius,alpha三要素。
     * 在回调绘制中设置这3要素。详细例子请看StaticFocusDrawable。
     * @param canvas
     */
    void draw(Canvas canvas);
}
