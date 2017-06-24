//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import com.yunos.tv.app.widget.utils.Poolable;

public interface Pool<T extends Poolable<T>> {
    T acquire();

    void release(T var1);
}
