package com.ccdt.app.tv.mvpdemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccdt.app.tv.mvpdemo.R;

import java.util.ArrayList;

/**
 * Created by wudz on 2017/4/19.
 */

public class FlipGridAdapter extends BaseAdapter {
    private ArrayList<ObjInfo> mList=new ArrayList<>();
    private Context mContext;
    public FlipGridAdapter(Context context)
    {
        mContext=context;
        for(int i=0;i<200;i++)
        {
            mList.add(new ObjInfo());
        }
    }

    public void addItem(ArrayList<ObjInfo> objList)
    {
        mList.addAll(objList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
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
            res= LayoutInflater.from(mContext).inflate(R.layout.griditem_type1, null);
        setView(res,i);
        return res;
    }
    private void setView(View res,int position)
    {
        ImageView iv=(ImageView) res.findViewById(R.id.id_grid_iv);
        TextView tv=(TextView) res.findViewById(R.id.id_grid_tv);
        if(position%2==0)
        iv.setImageResource(R.drawable.grid_0);
        else
            iv.setImageResource(R.drawable.grid_1);
        tv.setText("position:"+position);
    }
    public static class ObjInfo
    {
        public String url="";
        public String text="测试测试测试";
    }
}
