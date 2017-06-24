//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import com.yunos.tv.app.widget.utils.Pool;
import com.yunos.tv.app.widget.utils.Poolable;

class SynchronizedPool<T extends Poolable<T>> implements Pool<T> {
    private final Pool<T> mPool;
    private final Object mLock;

    public SynchronizedPool(Pool<T> pool) {
        this.mPool = pool;
        this.mLock = this;
    }

    public SynchronizedPool(Pool<T> pool, Object lock) {
        this.mPool = pool;
        this.mLock = lock;
    }

    public T acquire() {
        Object var1 = this.mLock;
        synchronized(this.mLock) {
            return this.mPool.acquire();
        }
    }

    public void release(T element) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mPool.release(element);
        }
    }
}
