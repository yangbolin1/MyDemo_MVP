//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import android.util.Log;

class FinitePool<T extends Poolable<T>> implements Pool<T> {
    private static final String LOG_TAG = "FinitePool";
    private final PoolableManager<T> mManager;
    private final int mLimit;
    private final boolean mInfinite;
    private T mRoot;
    private int mPoolCount;

    FinitePool(PoolableManager<T> manager) {
        this.mManager = manager;
        this.mLimit = 0;
        this.mInfinite = true;
    }

    FinitePool(PoolableManager<T> manager, int limit) {
        if(limit <= 0) {
            throw new IllegalArgumentException("The pool limit must be > 0");
        } else {
            this.mManager = manager;
            this.mLimit = limit;
            this.mInfinite = false;
        }
    }

    public T acquire() {
        Poolable element;
        if(this.mRoot != null) {
            element = this.mRoot;
            this.mRoot = (T) element.getNextPoolable();
            --this.mPoolCount;
        } else {
            element = this.mManager.newInstance();
        }

        if(element != null) {
            element.setNextPoolable((Object)null);
            element.setPooled(false);
            this.mManager.onAcquired((T) element);
        }

        return (T)element;
    }

    public void release(T element) {
        if(!element.isPooled()) {
            if(this.mInfinite || this.mPoolCount < this.mLimit) {
                ++this.mPoolCount;
                element.setNextPoolable(this.mRoot);
                element.setPooled(true);
                this.mRoot = element;
            }

            this.mManager.onReleased(element);
        } else {
            Log.w("FinitePool", "Element is already in pool: " + element);
        }

    }
}
