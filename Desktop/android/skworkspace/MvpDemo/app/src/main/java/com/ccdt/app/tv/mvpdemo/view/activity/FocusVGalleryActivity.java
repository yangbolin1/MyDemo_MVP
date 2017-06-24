package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.ComplexListViewAdapter;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusVGallery;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/27.
 */

public class FocusVGalleryActivity extends Activity {
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager focusPositionManager;
    @BindView(R.id.id_vg)
    FocusVGallery vg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_vgallery);
        ButterKnife.bind(this);

        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        focusPositionManager.requestFocus(vg, View.FOCUS_DOWN);
        vg.setAdapter(new ComplexListViewAdapter(this,20,focusPositionManager));

    }
}
