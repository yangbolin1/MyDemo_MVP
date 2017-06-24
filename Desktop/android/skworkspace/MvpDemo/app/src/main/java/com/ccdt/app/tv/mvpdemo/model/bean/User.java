package com.ccdt.app.tv.mvpdemo.model.bean;

/**
 * Created by wudz on 2017/3/30.
 */

public class User {
    public User(String id,String name,String password)
    {
        this.userId=id;
        this.name=name;
        this.password=password;
    }
    private String userId;
    private String name;
    private String password;

    /**
     * 获取用户的id
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取用户名
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 获取密码
     * @return
     */
    public String getPassword() {
        return password;
    }
}
