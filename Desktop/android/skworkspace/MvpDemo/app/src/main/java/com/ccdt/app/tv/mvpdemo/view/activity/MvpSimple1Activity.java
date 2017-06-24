package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.BaseAdapter;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.presenter.MvpSimple1Activity.MvpSimpleActivityContract;
import com.ccdt.app.tv.mvpdemo.presenter.MvpSimple1Activity.MvpSimpleActivityPresenter;
import com.ccdt.app.tv.mvpdemo.presenter.someActivity1.MainActivityPresenter;
import com.ccdt.app.tv.mvpdemo.view.adapter.FlipGridAdapter;
import com.ccdt.app.tv.mvpdemo.view.adapter.ListViewAdapter;
import com.ccdt.app.tv.mvpdemo.view.adapter.MvpSimple1GridViewAdapter;
import com.ccdt.app.tv.mvpdemo.view.adapter.MvpSimple1ListViewAdapter;
import com.ccdt.app.tv.mvpdemo.view.bean.GridViewBean;
import com.ccdt.app.tv.mvpdemo.view.bean.MenuBean;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/5/17.
 */

public class MvpSimple1Activity extends Activity implements MvpSimpleActivityContract.View{
    @BindView(R.id.id_lv)
    FocusListView listView;
    @BindView(R.id.id_flip_grid)
    FocusFlipGridView gridview;
    @BindView(R.id.id_focus_root_view)
    FocusPositionManager fpm;
    MvpSimpleActivityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //借用复杂焦点设计控件架构
        setContentView(R.layout.activity_focus_complex);
        ButterKnife.bind(this);
        presenter= new MvpSimpleActivityPresenter(this);
        presenter.attachView(this);


        gridview.setNumColumns(5);
        gridview.setHorizontalSpacing(30);
        gridview.setVerticalSpacing(30);

        fpm.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                presenter.doMenuClick((MenuBean.MenuItemBean)(((FocusListView)var1).getAdapter().getItem(position)));
            }
        });
        listView.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
                if(selectStatus==true)
                presenter.getGridViewData((MenuBean.MenuItemBean)(((FocusListView)adapterView).getAdapter().getItem(position)));
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
                presenter.doGridClick((GridViewBean.GridItemBean) var1.getAdapter().getItem(position));
            }
        });
        presenter.getMenuData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onMenuData(MenuBean bean) {
        listView.setAdapter(new MvpSimple1ListViewAdapter(this,bean));
        listView.setSelection(0);
        presenter.getGridViewData(bean.getListItem().get(0));
        fpm.requestFocus(listView, View.FOCUS_DOWN);
    }

    @Override
    public void onGridViewData(GridViewBean bean) {
        MvpSimple1GridViewAdapter adapter=new MvpSimple1GridViewAdapter(this,bean);
        gridview.setAdapter(adapter);
    }
}
