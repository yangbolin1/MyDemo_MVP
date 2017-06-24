package com.ccdt.app.tv.mvpdemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.bean.GridViewBean;
import com.ccdt.app.tv.mvpdemo.view.widget.layout.LocalFocusLinearLayout;

/**
 * Created by wudz on 2017/5/17.
 */

public class MvpSimple1GridViewAdapter extends BaseAdapter {
    private GridViewBean bean;
    private Context context;
    public MvpSimple1GridViewAdapter(Context context,GridViewBean bean)
    {
        this.bean=bean;
        this.context=context;
    }
    @Override
    public int getCount() {
        return bean.getGridList().size();
    }

    @Override
    public Object getItem(int i) {
        return bean.getGridList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View res;
        if(convertView!=null)
            res=convertView;
        else
            res= LayoutInflater.from(context).inflate(R.layout.griditem_type1, null);
        setView(res,i);
        return res;
    }

    private void setView(View res,int position)
    {
        GridViewBean.GridItemBean itemBean=bean.getGridList().get(position);
        ImageView iv=(ImageView) res.findViewById(R.id.id_grid_iv);
        TextView tv=(TextView) res.findViewById(R.id.id_grid_tv);
        iv.setImageResource(itemBean.getImageId());
        tv.setText(itemBean.getText());

    }
}
