//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import com.yunos.tv.app.widget.utils.FinitePool;
import com.yunos.tv.app.widget.utils.Pool;
import com.yunos.tv.app.widget.utils.Poolable;
import com.yunos.tv.app.widget.utils.PoolableManager;
import com.yunos.tv.app.widget.utils.SynchronizedPool;

public class Pools {
    private Pools() {
    }

    public static <T extends Poolable<T>> Pool<T> simplePool(PoolableManager<T> manager) {
        return new FinitePool(manager);
    }

    public static <T extends Poolable<T>> Pool<T> finitePool(PoolableManager<T> manager, int limit) {
        return new FinitePool(manager, limit);
    }

    public static <T extends Poolable<T>> Pool<T> synchronizedPool(Pool<T> pool) {
        return new SynchronizedPool(pool);
    }

    public static <T extends Poolable<T>> Pool<T> synchronizedPool(Pool<T> pool, Object lock) {
        return new SynchronizedPool(pool, lock);
    }
}
