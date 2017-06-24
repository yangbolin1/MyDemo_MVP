package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.blankj.utilcode.utils.ActivityUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.FlipGridAdapter;
import com.ccdt.app.tv.mvpdemo.view.adapter.ListViewAdapter;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/24. new
 */

public class FocusComplexActivity extends Activity {
    private static final String TAG="FocusComplexActivity";

    @BindView(R.id.id_lv)
    FocusListView listView;
    @BindView(R.id.id_flip_grid)
    FocusFlipGridView gridview;

    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_complex);
        ButterKnife.bind(this);

        listView.setAdapter(new ListViewAdapter(this));
        listView.setSelection(0);

        gridview.setAdapter(new FlipGridAdapter(this));
        gridview.setNumColumns(5);
        gridview.setHorizontalSpacing(30);
        gridview.setVerticalSpacing(30);
        gridview.setMaxFastStep(50);

        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        fpm.requestFocus(gridview,View.FOCUS_DOWN);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> var1, View view, int position, long id) {
                ToastUtils.showShortToast("原版 接口 onItemSelected");
                Log.i(TAG,"原版 接口 onItemSelected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> var1) {
                ToastUtils.showShortToast("原版 接口 onNothingSelected");
                Log.i(TAG,"原版 接口 onNothingSelected");
            }
        });
        listView.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
                ToastUtils.showShortToast("阿里版 接口 onItemSelected");
                Log.i(TAG,"阿里版 接口 onItemSelected:view:"+view+
                        " position:"+position+" selectStatus:"+selectStatus+" adapterView:"+adapterView);
            }
        });


    }



}
