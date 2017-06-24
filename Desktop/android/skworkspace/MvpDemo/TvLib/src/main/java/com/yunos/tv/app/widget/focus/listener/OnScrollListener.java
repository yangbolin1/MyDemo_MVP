//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.view.ViewGroup;

public interface OnScrollListener {
    int SCROLL_STATE_IDLE = 0;
    int SCROLL_STATE_TOUCH_SCROLL = 1;
    int SCROLL_STATE_FLING = 2;

    void onScrollStateChanged(ViewGroup viewGroup, int newState);

    void onScroll(ViewGroup var1, int var2, int var3, int var4);
}
