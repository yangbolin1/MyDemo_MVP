//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import com.yunos.tv.app.widget.utils.Poolable;

public interface PoolableManager<T extends Poolable<T>> {
    T newInstance();

    void onAcquired(T var1);

    void onReleased(T var1);
}
