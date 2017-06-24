package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ccdt.app.tv.mvpdemo.R;
import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/18.
 */

public class FocusHListViewActivity extends Activity {
    private static final String TAG="FocusHListViewActivity";
    @BindView(R.id.id_h_lv)
    FocusHListView hlv;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_h_listview);
        ButterKnife.bind(this);
        hlv.setSelection(3);
        hlv.setAdapter(new MyAdapter());
        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        fpm.requestFocus(hlv,View.FOCUS_DOWN);
        hlv.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
                Log.i(TAG,"onItemSelected:"+position);
            }
        });
    }
    private class MyAdapter extends BaseAdapter
    {
        ArrayList<String> nameList=new ArrayList<>();
        MyAdapter()
        {
            for(int i=0;i<10;i++)
            {
                nameList.add("当前第"+i+"个");
            }
        }
        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return "asdfsadf";
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView!=null)
                return convertView;
            return LayoutInflater.from(FocusHListViewActivity.this).inflate(R.layout.listitem_list_view,null);
        }
    }
}
