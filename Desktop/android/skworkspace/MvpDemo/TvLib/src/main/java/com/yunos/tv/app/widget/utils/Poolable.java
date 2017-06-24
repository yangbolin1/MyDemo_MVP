//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

public interface Poolable<T> {
    void setNextPoolable(T var1);

    T getNextPoolable();

    boolean isPooled();

    void setPooled(boolean var1);
}
