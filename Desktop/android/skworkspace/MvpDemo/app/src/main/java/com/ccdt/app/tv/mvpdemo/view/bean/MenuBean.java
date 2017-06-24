package com.ccdt.app.tv.mvpdemo.view.bean;


import com.ccdt.app.tv.mvpdemo.view.base.ViewData;

import java.util.ArrayList;

/**
 * Created by wudz on 2017/5/12.
 * 菜单数据bean
 */
public class MenuBean extends ViewData {
    private ArrayList<MenuItemBean> listItem;
    public MenuBean(String id, ArrayList<MenuItemBean> listItem) {
        super(id);
        this.listItem =listItem;
    }

    public ArrayList<MenuItemBean> getListItem() {
        return listItem;
    }

    /**
     * 菜单选项的数据bean
     */
    public static class MenuItemBean extends ViewData {
        private String itemText ="";
        public MenuItemBean(String id, String tabText) {
            super(id);
            this.itemText =tabText;
        }
        public String getItemText() {
            return itemText;
        }

        @Override
        public String toString() {
            return "MenuItemBean{" +
                    "itemText='" + itemText + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MenuBean{" +
                "listItem=" + listItem +
                '}';
    }
}
