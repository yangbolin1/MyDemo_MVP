package com.ccdt.app.tv.mvpdemo.presenter.someActivity1;



import com.ccdt.app.tv.mvpdemo.presenter.base.BasePresenter;
import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_MulImage;
import com.ccdt.app.tv.mvpdemo.presenter.base.BaseContract;
import com.ccdt.app.tv.mvpdemo.presenter.base.BaseView;
import com.ccdt.app.tv.mvpdemo.view.bean.PanelData_Image;


/**
 * Created width Android Studio
 * User:StormSun
 * Date:2017/1/13
 * Time:16:04
 * Description:
 */
public class MainActivityContract extends BaseContract{

        public interface View extends BaseView {
            void onUserLogin(String userName,String password);
            void onPanelShow(PanelData_Image pData);
            void onPanelMulShow(PanelData_MulImage pDataMul);
        }

    public static abstract class Presenter extends BasePresenter<View> {
        public abstract void getData();

    }
}
