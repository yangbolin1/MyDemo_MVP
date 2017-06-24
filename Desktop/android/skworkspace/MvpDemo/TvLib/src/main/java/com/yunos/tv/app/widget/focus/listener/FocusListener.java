//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;
import android.view.KeyEvent;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * 允许焦点选中接口
 */
public interface FocusListener {
    /**
     * 焦点控制器调用接口：获取焦点参数对象。
     * 一般这个对象由用户设置，并保存在继承此接口类的对象里。
     * @return
     */
    FocusRectParams getFocusParams();

    /**
     *  焦点控制器调用接口：是否可以绘制焦点。
     *  一般情况下，继承类恒定返回真。
     * @return
     */
    boolean canDraw();

    /**
     * 焦点控制器调用接口：判断焦点似否已滑动方式显示。
     * 如果为否，焦点不滑动，直接跳到目标窗体上绘制。
     * @return
     */
    boolean isAnimate();

    /**
     * 焦点控制器调用接口：获取当前窗口正在绘制中的窗口。
     * 如果当前窗口不是不包含任何子窗口，返回当前窗口，
     * 如果当前窗口包含子窗口，一般返回选中的目标窗口。
     * @return
     */
    ItemListener getItem();

    boolean isScrolling();

    /**
     * 焦点控制器调用接口：获取动画物体参数对象。
     * 这个包含了FocusRectParams对象。
     * 注：这个对象个人感觉有点赶工的也是，正常应该和getFocusParams合并为一个。
     * @return
     */
    Params getParams();

    /**
     * 焦点控制器调用接口：焦点是否在背景绘制：
     * 如果设置为真，焦点在控件绘制前先绘制。否则先绘制控件，后绘制焦点。
     * @return
     */
    boolean isFocusBackground();

    /**
     * 焦点派发前的一个拦截函数
     * @param keyCode 按键键值
     * @param event 按键事件
     * @return 返回true 不拦截 返回false拦截
     */
    boolean preOnKeyDown(int keyCode, KeyEvent event);

    boolean onKeyDown(int keyCode, KeyEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);


    /**
     * callback:
     * 焦点控制器回调接口:
     * 焦点开始绘制
     */
    void onFocusStart();
    /**
     * callback:
     * 焦点控制器回调接口:
     * 焦点绘制绘制完毕
     */
    void onFocusFinished();

    Rect getClipFocusRect();
}
