package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.yunos.tv.app.widget.focus.FocusImageView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusTextView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/6. ceshi
 */

public class FocusManagerActivity extends Activity {
    private static final String TAG="FocusManagerActivity";
    @BindView(R.id.id_txt_v12)
    FocusTextView animateTextView;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;
    @BindView(R.id.id_riv_15)
    FocusImageView fiv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focusmanager_test);
        ButterKnife.bind(this);

        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        fpm.requestFocus(animateTextView, View.FOCUS_DOWN);
        fpm.focusShow();

        animateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShortToast("animateTextView");
            }
        });

        fiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShortToast("fiv");
            }
        });

        animateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast("asdf");
            }
        });



    }
}
