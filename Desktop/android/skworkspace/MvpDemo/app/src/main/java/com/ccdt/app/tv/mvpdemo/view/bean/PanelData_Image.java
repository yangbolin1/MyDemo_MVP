package com.ccdt.app.tv.mvpdemo.view.bean;

/**
 * Created by wudz on 2017/1/13.
 */

public class PanelData_Image extends PanelData {
    private String mUrl = "";
    private String mName="";

    public PanelData_Image(String id, String imageUrl, String name) {
        super(id);
        mUrl = imageUrl;
        mName=name;
    }
    public String getUrl()
    {
        return mUrl;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "PanelData_Image{" +
                "mUrl='" + mUrl + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }
}
