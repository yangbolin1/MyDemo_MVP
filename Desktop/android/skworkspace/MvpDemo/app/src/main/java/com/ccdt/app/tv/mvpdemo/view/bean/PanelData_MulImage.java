package com.ccdt.app.tv.mvpdemo.view.bean;

import java.util.ArrayList;

/**
 * Created by wudz on 2017/1/13.
 */

public class PanelData_MulImage extends PanelData {
    private ArrayList<String> mImagesUrls;
    private ArrayList<String> mAppOpenProtocolJsons;
    public PanelData_MulImage(String id, ArrayList<String> imagesUrls, ArrayList<String> appOpenProtocolJsons)
    {
        super(id);
        if(imagesUrls==null || appOpenProtocolJsons==null || imagesUrls.size()!=appOpenProtocolJsons.size())
            throw new IllegalArgumentException("PanelData_MulImage args error!");
        mImagesUrls=imagesUrls;
        mAppOpenProtocolJsons=appOpenProtocolJsons;
    }
}
