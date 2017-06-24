//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;
import android.view.View;

public interface PositionListener {
    void offsetDescendantRectToItsCoords(View var1, Rect var2);

    void invalidate();

    void postInvalidate();

    void postInvalidateDelayed(long var1);

    void reset();
}
