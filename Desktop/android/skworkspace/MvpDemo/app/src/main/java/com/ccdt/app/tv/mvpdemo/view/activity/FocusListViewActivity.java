package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.ListViewAdapter;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/18.
 */

public class FocusListViewActivity extends Activity {
    private static final String TAG="FocusListViewActivity";
    @BindView(R.id.id_lv)
    FocusListView lv;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_listview);
        ButterKnife.bind(this);
        lv.setAdapter(new ListViewAdapter(this));
        lv.setSelection(3);
        lv.setFlipScrollFrameCount(15);
        lv.setSelector(getResources().getDrawable(R.drawable.menu_focus));
        lv.setOnFocusStateListener(new FocusStateListener() {
            @Override
            public void onFocusStart(View selectedView, View adapterView) {

            }

            @Override
            public void onFocusFinished(View selectedView, View adapterView) {

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                ToastUtils.showShortToast("position:"+position);
                lv.setSelection(position);
            }
        });
//        lv.setAutoSearch(true);
//        lv.setDeepMode(true);
        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        fpm.requestFocus(lv,View.FOCUS_DOWN);

        lv.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
                ToastUtils.showShortToast("阿里版 接口 onItemSelected:position:");
                Log.i(TAG,"阿里版 接口 onItemSelected:view:"+view+
                " position:"+position+" selectStatus:"+selectStatus+" adapterView:"+adapterView);
            }
        });

    }
}
