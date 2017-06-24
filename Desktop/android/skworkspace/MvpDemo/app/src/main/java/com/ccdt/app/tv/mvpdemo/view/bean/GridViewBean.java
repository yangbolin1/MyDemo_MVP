package com.ccdt.app.tv.mvpdemo.view.bean;

import com.ccdt.app.tv.mvpdemo.view.base.ViewData;

import java.util.ArrayList;

/**
 * Created by wudz on 2017/5/17.
 */

public class GridViewBean extends ViewData {
    private ArrayList<GridItemBean> gridList;
    public GridViewBean(String id,ArrayList<GridItemBean> gridList) {
        super(id);
        this.gridList=gridList;
    }
    public static class GridItemBean extends ViewData
    {
        private String text;
        private int imageId;
        public GridItemBean(String id,String text,int defeltImageId)
        {
            super(id);
            this.text=text;
            this.imageId=defeltImageId;
        }

        public String getText() {
            return text;
        }

        public int getImageId() {
            return imageId;
        }
    }

    public ArrayList<GridItemBean> getGridList() {
        return gridList;
    }
}
