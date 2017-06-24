package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.ListViewAdapter;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/5/18.
 */

public class FocusTwoListViewActivity extends Activity {
    private static final String TAG="FocusListViewActivity";
    @BindView(R.id.id_lv1)
    FocusListView lv1;
    @BindView(R.id.id_lv2)
    FocusListView lv2;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_two_listview);
        ButterKnife.bind(this);
        lv1.setAdapter(new ListViewAdapter(this));
        lv1.setSelection(3);
        lv1.setFlipScrollFrameCount(15);

        lv2.setAdapter(new ListViewAdapter(this));
        lv2.setSelection(3);
        lv2.setFlipScrollFrameCount(15);

        lv1.setOnFocusStateListener(new FocusStateListener() {
            @Override
            public void onFocusStart(View selectedView, View adapterView) {

            }

            @Override
            public void onFocusFinished(View selectedView, View adapterView) {

            }
        });
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                ToastUtils.showShortToast("position:"+position);
                lv1.setSelection(position);
            }
        });

        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        fpm.requestFocus(lv1,View.FOCUS_DOWN);

    }
}
