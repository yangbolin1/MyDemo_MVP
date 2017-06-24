package com.ccdt.app.tv.mvpdemo.presenter.base;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by sll on 05,七月,2016
 */
public abstract class BasePresenter<T extends BaseView> {
  protected CompositeSubscription mSub;
  private WeakReference<T> mView;
  public void attachView(@NonNull T view) {
    mView =new WeakReference<T>(view);
    mSub = new CompositeSubscription();
  }

  public void detachView() {
    if (mSub != null) {
      if (!mSub.isUnsubscribed()) {
        mSub.unsubscribe();
      }
      mSub = null;
    }
    mView = null;
  }
  public T getView()
  {
    return mView.get();
  }

}
