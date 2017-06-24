package com.ccdt.app.tv.mvpdemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ccdt.app.tv.mvpdemo.R;

import java.util.ArrayList;

/**
 * Created by wudz on 2017/4/24.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> nameList = new ArrayList<>();

    public ListViewAdapter(Context context) {
        mContext=context;
        for (int i = 0; i < 100; i++) {
            nameList.add("当前第" + i + "个");
        }
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;
        if (convertView != null)
            rootView = convertView;
        else
            rootView = LayoutInflater.from(mContext).inflate(R.layout.listitem_list_view, null);

        TextView textView = (TextView) rootView.findViewById(R.id.id_tv_listitem);
        textView.setText("这是第" + position + "个");
        return rootView;
    }
}
