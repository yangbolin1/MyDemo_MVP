//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;
import com.yunos.tv.app.widget.focus.listener.FocusListener;

public interface DeepListener extends FocusListener {
    boolean canDeep();

    boolean hasDeepFocus();

    void onFocusDeeped(boolean var1, int var2, Rect var3);

    void onItemSelected(boolean var1);

    void onItemClick();
}
