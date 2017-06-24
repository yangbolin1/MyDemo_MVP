package com.yunos.tv.app.widget.focus.listener;

import android.view.View;

public interface FocusStateListener {
    void onFocusStart(View selectedView, View adapterView);

    void onFocusFinished(View selectedView, View adapterView);
}
