package com.ccdt.app.tv.mvpdemo.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.presenter.someActivity1.MainActivityContract;
import com.ccdt.app.tv.mvpdemo.presenter.someActivity1.MainActivityPresenter;
import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_Image;
import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_MulImage;
import com.ccdt.app.tv.mvpdemo.view.widget.view.ButtonEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements MainActivityContract.View{

    @BindView(R.id.id_tv_user)
    TextView tvUser;
    @BindView(R.id.id_tv_password)
    TextView tvPassword;
    @BindView(R.id.id_btn_test1)
    ButtonEx btnTest1;
    @BindView(R.id.id_btn_test2)
    ButtonEx btnTest2;

    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter=new MainActivityPresenter();
        presenter.attachView(this);
        presenter.getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();

    }

    @Override
    public void onUserLogin(String userName, String password) {
        tvUser.setText(userName);
        tvPassword.setText(password);
    }

    @Override
    public void onPanelShow(PanelData_Image pData) {

    }

    @Override
    public void onPanelMulShow(PanelData_MulImage pDataMul) {

    }

    @OnClick(R.id.id_btn_test1)
    void Test1Click1()
    {
        Intent i=new Intent(this,FocusManagerActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.id_btn_test2)
    void Test1Click2()
    {
        Intent i=new Intent(this,FocusHListViewActivity.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test3)
    void Test1Click3()
    {
        Intent i=new Intent(this,FocusGridViewActivty.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test4)
    void Test1Click4()
    {
        Intent i=new Intent(this,FocusListViewActivity.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test5)
    void Test1Click5()
    {
        Intent i=new Intent(this,FocusComplexActivity.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test6)
    void Test1Click6()
    {
        Intent i=new Intent(this,FocusVGalleryActivity.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test7)
    void Test1Click7()
    {
        Intent i=new Intent(this,FocusComplexActivity_lsitView.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test8)
    void Test1Click8()
    {
        Intent i=new Intent(this,MvpSimple1Activity.class);
        startActivity(i);
    }
    @OnClick(R.id.id_btn_test9)
    void Test1Click9()
    {
        Intent i=new Intent(this,FocusTwoListViewActivity.class);
        startActivity(i);
    }
}
