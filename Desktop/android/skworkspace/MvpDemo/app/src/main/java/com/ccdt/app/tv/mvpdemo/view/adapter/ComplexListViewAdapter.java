package com.ccdt.app.tv.mvpdemo.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.yunos.tv.app.widget.focus.FocusFrameLayout;
import com.yunos.tv.app.widget.focus.FocusImageView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;

import java.util.HashMap;

/**
 * Created by wudz on 2017/4/27.
 */

public class ComplexListViewAdapter extends BaseAdapter {
    private static final String TAG="ComplexListViewAdapter";
    HashMap<Integer,Integer> itemType=new HashMap();
    private final static int TYPE_0=0;
    private final static int TYPE_1=1;
    private final static int TYPE_2=2;
    private int count;
    private Context context;
    private FocusPositionManager fpm;

    public ComplexListViewAdapter(Context context, int itemCount,FocusPositionManager fpm)
    {
        this.context=context;
        count=itemCount;
        this.fpm=fpm;
    }
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }
    @Override
    public int getItemViewType(int position) {
        return  position%3;
    }
    @Override
    public View getView(final int i, View view,final ViewGroup viewGroup) {
        switch (i%3)
        {
            case TYPE_0:
                view= LayoutInflater.from(context).inflate(R.layout.vgallery_item_type0, null);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setImageResource(R.drawable.grid_0);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShortToast("我是第"+i+"个Item ，id_iv_item0窗口");
                    }
                });
                ((FocusImageView)view.findViewById(R.id.id_iv_item1)).setImageResource(R.drawable.grid_1);
                ((FocusImageView)view.findViewById(R.id.id_iv_item2)).setImageResource(R.drawable.grid_0);
                ((FocusImageView)view.findViewById(R.id.id_iv_item3)).setImageResource(R.drawable.grid_1);
                break;
            case TYPE_1:
                view= LayoutInflater.from(context).inflate(R.layout.vgallery_item_type1, null);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setImageResource(R.drawable.grid_0);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShortToast("我是第"+i+"个Item ，id_iv_item0窗口");
                    }
                });
                ((ImageView)view.findViewById(R.id.id_iv_item1)).setImageResource(R.drawable.grid_1);
                ((ImageView)view.findViewById(R.id.id_iv_item2)).setImageResource(R.drawable.grid_0);
                break;
            case TYPE_2:
                view= LayoutInflater.from(context).inflate(R.layout.vgallery_item_type2, null);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setImageResource(R.drawable.grid_0);
                ((FocusImageView)view.findViewById(R.id.id_iv_item0)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShortToast("我是第"+i+"个Item ，id_iv_item0窗口");
                    }
                });
                ((ImageView)view.findViewById(R.id.id_iv_item1)).setImageResource(R.drawable.grid_1);
                FocusFrameLayout fv=(FocusFrameLayout)view.findViewById(R.id.id_ffl_item1);
                fv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.i(TAG,"onFocusChange:hasFocus:"+hasFocus);
                        if(hasFocus)
                        {
                             fpm.setConvertSelector(new StaticFocusDrawable(context.getResources().getDrawable(R.drawable.menu_focus)));
                        }
                        else {
                            fpm.setConvertSelector(new StaticFocusDrawable(context.getResources().getDrawable(R.drawable.focus)));
                        }
                    }
                });
                fv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showShortToast("我是向上放大的frame");
                    }
                });
                break;
        }

        return view;
    }
}
