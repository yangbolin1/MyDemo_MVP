package com.ccdt.app.tv.mvpdemo.presenter.MvpSimple1Activity;

import com.ccdt.app.tv.mvpdemo.presenter.base.BaseContract;
import com.ccdt.app.tv.mvpdemo.presenter.base.BasePresenter;
import com.ccdt.app.tv.mvpdemo.presenter.base.BaseView;
import com.ccdt.app.tv.mvpdemo.view.bean.GridViewBean;
import com.ccdt.app.tv.mvpdemo.view.bean.MenuBean;

/**
 * Created by wudz on 2017/5/17.
 */

public class MvpSimpleActivityContract extends BaseContract {
    public interface View extends BaseView {
        /**
         * 显示菜单数据
         * @param bean
         */
        void onMenuData(MenuBean bean);

        /**
         * 显示表格数据
         * @param bean
         */
        void onGridViewData(GridViewBean bean);
    }

    public static abstract class Presenter extends BasePresenter<View> {
        /**
         * 获取菜单数据
         */
        public abstract void getMenuData();

        /**
         * 获取右侧表格数据
         * @param itemData 菜单数据对应项目
         */
        public abstract void getGridViewData(MenuBean.MenuItemBean itemData);

        /**
         * 点击事件
         * @param itemData
         */
        public abstract void doGridClick(GridViewBean.GridItemBean itemData);

        /**
         * 点击事件
         * @param itemData
         */
        public abstract void doMenuClick(MenuBean.MenuItemBean itemData);

    }
}
