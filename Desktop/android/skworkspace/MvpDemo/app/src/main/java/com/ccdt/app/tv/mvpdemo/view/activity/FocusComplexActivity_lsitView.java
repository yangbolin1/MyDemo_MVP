package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.ListViewAdapter;
import com.ccdt.app.tv.mvpdemo.view.adapter.ComplexListViewAdapter;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/27.
 */

public class FocusComplexActivity_lsitView extends Activity {
    private static final String TAG="FocusComplexActivity_lsitView";
    @BindView(R.id.id_lv)
    FocusListView listView;
    @BindView(R.id.id_list_right)
    FocusListView listViewRight;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;

    final int MSG_CHANGE_ADAPTER=1001;
    Handler mhandle=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
             switch (msg.what)
             {
                 case MSG_CHANGE_ADAPTER:

                     listViewRight.setAdapter(new ComplexListViewAdapter(FocusComplexActivity_lsitView.this,(msg.arg1+1)*10,fpm));
                     listViewRight.setVisibility(View.VISIBLE);
                     break;
             }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_complex_listview);
        ButterKnife.bind(this);

        listView.setAdapter(new ListViewAdapter(this));
        //设置初始选择位置
        listView.setSelection(0);
        //设置listView滑动的帧率 越大越慢
        listView.setFlipScrollFrameCount(15);
        //焦点在文字背后 必须是透明背景
        listView.setFocusBackground(true);
        //初始化焦点才菜单上 所以调用setSelector
        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.menu_focus)));
        fpm.requestFocus(listView, View.FOCUS_DOWN);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                ToastUtils.showShortToast((String)listView.getAdapter().getItem(position));
                listViewRight.setAdapter(new ComplexListViewAdapter(FocusComplexActivity_lsitView.this,(position+1)*10,fpm));
            }
        });
        listView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true)
                {
                    //当菜单获得焦点 调用覆盖selector 这样切换焦点时会启动渐隐渐现
                    fpm.setConvertSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.menu_focus)));
                }
            }
        });
        listView.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
                //listViewRight.setVisibility(View.INVISIBLE);

                View v=view.findViewById(R.id.id_tv_listitem);
                if(v instanceof TextView) {
                    if (selectStatus == true) {
                       // mhandle.sendMessageDelayed(mhandle.obtainMessage(MSG_CHANGE_ADAPTER,position,0),500);
                        ((TextView) v).setTextColor(Color.BLUE);
                    } else {
                        if(listView.hasFocus()==true)
                        ((TextView) v).setTextColor(Color.BLACK);
                    }
                }
            }
        });

        //设置这个模式 启动listview子布局可以被选中焦点 必须设置
        listViewRight.setDeepMode(true);
        listViewRight.setAdapter(new ComplexListViewAdapter(this,50,fpm));
        listViewRight.setFlipScrollFrameCount(10);

        listViewRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                //这个不会起作用，需要在Adatper里设置每个子窗口的焦点事件  注意
                ToastUtils.showShortToast("asdf");
            }
        });
        listViewRight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true)
                {
                    fpm.setConvertSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
                }
            }
        });

        //此处可以调用下面的这个 默认是第一个Focus开头的孩子窗口
        //fpm.requestFocus();
    }
}
