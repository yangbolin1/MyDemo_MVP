package com.ccdt.app.tv.mvpdemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.bean.MenuBean;

/**
 * Created by wudz on 2017/5/17.
 */

public class MvpSimple1ListViewAdapter extends BaseAdapter {
    private Context context;
    private MenuBean bean;

    public MvpSimple1ListViewAdapter(Context context, MenuBean bean)
    {
        this.context=context;
        this.bean=bean;
    }
    @Override
    public int getCount() {
        return bean.getListItem().size();
    }

    @Override
    public Object getItem(int i) {
        return bean.getListItem().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View rootView;
        if (convertView != null)
            rootView = convertView;
        else
            rootView = LayoutInflater.from(context).inflate(R.layout.listitem_list_view, null);
        TextView textView = (TextView) rootView.findViewById(R.id.id_tv_listitem);
        textView.setText("这是第" + position + "个,名字:"+bean.getListItem().get(position).getItemText());
        return rootView;
    }
}
