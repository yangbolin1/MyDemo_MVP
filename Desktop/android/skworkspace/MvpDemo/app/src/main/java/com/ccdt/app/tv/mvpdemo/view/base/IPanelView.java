package com.ccdt.app.tv.mvpdemo.view.base;

/**
 * Created by wudz on 2017/1/12.
 * 实现坑位业务的接口
 */

public interface IPanelView<T extends IViewDataBean> {
     void  updateViewData(T bean);
     T getViewData();
}
