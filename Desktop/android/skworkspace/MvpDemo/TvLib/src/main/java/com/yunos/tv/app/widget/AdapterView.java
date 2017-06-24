package com.yunos.tv.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;

import com.yunos.tv.app.widget.focus.listener.FocusStateListener;

import java.lang.reflect.Field;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED;

/**
 * 阿里的AdapterView
 * @param <T>
 */
public abstract class AdapterView<T extends Adapter> extends ViewGroup {
    private String TAG = "AdapterView";
    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID = -9223372036854775808L;

    protected static final int TUI_TEXT_COLOR_GREY = -6710887;
    protected static final int TUI_TEXT_COLOR_WHITE = -1;
    protected static final int TUI_TEXT_SIZE_2 = 24;
    public static final int ITEM_VIEW_TYPE_IGNORE = -1;
    public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
    @ExportedProperty(
            category = "scrolling"
    )

    protected int mFirstPosition = 0;
    protected int mSpecificTop;
    protected int mSyncPosition;
    long mSyncRowId = INVALID_ROW_ID;
    long mSyncHeight;
    protected boolean mNeedSync = false;
    int mSyncMode;
    int mLayoutHeight;
    static final int SYNC_SELECTED_POSITION = 0;
    static final int SYNC_FIRST_POSITION = 1;
    static final int SYNC_MAX_DURATION_MILLIS = 100;
    protected boolean mInLayout = false;
    AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    AdapterView.OnItemClickListener mOnItemClickListener;
    AdapterView.OnItemLongClickListener mOnItemLongClickListener;
    protected boolean mDataChanged;
    @ExportedProperty(
            category = "list"
    )
    protected int mNextSelectedPosition = INVALID_POSITION;
    long mNextSelectedRowId = INVALID_ROW_ID;
    @ExportedProperty(
            category = "list"
    )
    protected int mSelectedPosition = INVALID_POSITION;
    protected long mSelectedRowId = INVALID_ROW_ID;
    private View mEmptyView;
    @ExportedProperty(
            category = "list"
    )
    protected int mItemCount;
    protected int mOldItemCount;

    protected int mOldSelectedPosition = INVALID_POSITION;
    protected long mOldSelectedRowId = INVALID_ROW_ID;
    private boolean mDesiredFocusableState;
    private boolean mDesiredFocusableInTouchModeState;
    private AdapterView<T>.SelectionNotifier mSelectionNotifier;
    boolean mBlockLayoutRequests = false;
    protected FocusStateListener mFocusStateListener = null;

    public AdapterView(Context context) {
        super(context);
    }

    public AdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 焦点状态改变接口
     * @param l
     */
    public void setOnFocusStateListener(FocusStateListener l) {
        this.mFocusStateListener = l;
    }

    /**
     * 点击事件接口
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 选项点击
     * @param view
     * @param position
     * @param id
     * @return
     */
    public boolean performItemClick(View view, int position, long id) {
        Log.i(this.TAG, this.TAG + ".performItemClick.view = " + view + ".positon = " + position + ".id = " + id);
        if (this.mOnItemClickListener != null) {
            this.playSoundEffect(0);
            if (view != null) {
                view.sendAccessibilityEvent(1);
            }

            this.mOnItemClickListener.onItemClick(this, view, position, id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 长按事件接口
     * @param listener
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        if (!this.isLongClickable()) {
            this.setLongClickable(true);
        }
        this.mOnItemLongClickListener = listener;
    }

    /**
     * 选中接口
     * @param listener
     */
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    /**
     * 继承类实现 获取Adapter
     * @return
     */
    public abstract T getAdapter();

    /**
     * 设置 Adapter
     * @param var1
     */
    public abstract void setAdapter(T var1);

    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     * @param child
     */
    public void addView(View child) {
        throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     * @param child
     */
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     * @param child
     */
    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     * @param child
     */
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     * @param child
     */
    public void removeView(View child) {
        throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     */
    public void removeViewAt(int index) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
    }
    /**
     * 注意此函数已经被设置无效，窗口只能在adapter内部设置
     */
    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
    }

    /**
     * 覆盖了ViewGroup接口，经过此次调用，mLayoutHeight 包内有效，也可以被继承类使用
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutHeight = this.getHeight();
    }

    /**
     * 获取选中项目的位置
     * @return
     */
    @CapturedViewProperty
    public int getSelectedItemPosition() {
        return this.mNextSelectedPosition;
    }

    /**
     * 获取选中项目的ID
     * @return
     */
    @CapturedViewProperty
    public long getSelectedItemId() {
        return this.mNextSelectedRowId;
    }

    /**
     * 获取选中的窗口，继承类实现
     * @return
     */
    public abstract View getSelectedView();

    /**
     * 获取选中项目的数据内容，内部调用adapter.getItem(selection) 实现
     * @return
     */
    public Object getSelectedItem() {
        Adapter adapter = this.getAdapter();
        int selection = this.getSelectedItemPosition();
        return adapter != null && adapter.getCount() > 0 && selection >= 0 ? adapter.getItem(selection) : null;
    }

    /**
     * 获取选项总数
     * @return
     */
    @CapturedViewProperty
    public int getCount() {
        return this.mItemCount;
    }

    /**
     * 查找窗口的位置，这个函数可以查询布局内所有子窗口的position,因为内部调用了getParent（）去匹配父布局。
     * 也就是说，可以查询布局内任意窗口所在的viewholder的position.
     * @param view
     * @return
     */
    public int getPositionForView(View view) {
        View listItem = view;
        View childCount;
        try {
            while (!(childCount = (View) listItem.getParent()).equals(this)) {
                listItem = childCount;
            }
        } catch (ClassCastException var5) {
            return INVALID_POSITION;
        }
        int count = this.getChildCount();
        for (int i = 0; i < count; ++i) {
            if (this.getChildAt(i).equals(listItem)) {
                return this.mFirstPosition + i;
            }
        }
        return INVALID_POSITION;
    }

    /**
     * 第一可见的位置 左 或者 上
     * @return
     */
    public int getFirstVisiblePosition() {
        return this.mFirstPosition;
    }

    /**
     * 最后可见的位置 右 或者 下
     * @return
     */
    public int getLastVisiblePosition() {
        return this.mFirstPosition + this.getChildCount() - 1;
    }

    /**
     * 选中一个选项，由于继承类实现
     * @param selection
     */
    public abstract void setSelection(int selection);

    /**
     * 大体的意思是 设置一个备用窗口，一旦设置这个备用窗口，
     * 选项窗口被强制关闭（setVisable=false）
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        Adapter adapter = this.getAdapter();
        boolean empty = adapter == null || adapter.isEmpty();
        this.updateEmptyStatus(empty);
    }

    /**
     * 获取备用空白窗口对象
     * @return
     */
    public View getEmptyView() {
        return this.mEmptyView;
    }

    /**
     * 未知
     * @return
     */
    boolean isInFilterMode() {
        return false;
    }

    /**
     * 设置focusable 主要是设置自定义mDesiredFocusableState 变量 和mDesiredFocusableInTouchModeState 变量
     * @param focusable
     */
    public void setFocusable(boolean focusable) {
        Adapter adapter = this.getAdapter();
        boolean var10000;
        if (adapter != null && adapter.getCount() != 0) {
            var10000 = false;
        } else {
            var10000 = true;
        }

        this.mDesiredFocusableState = focusable;
        if (!focusable) {
            this.mDesiredFocusableInTouchModeState = false;
        }

        super.setFocusable(focusable);
    }
    /**
     * 设置focusable 主要是设置自定义mDesiredFocusableState 变量 和mDesiredFocusableInTouchModeState 变量
     * @param focusable
     */
    public void setFocusableInTouchMode(boolean focusable) {
        Adapter adapter = this.getAdapter();
        boolean var10000;
        if (adapter != null && adapter.getCount() != 0) {
            var10000 = false;
        } else {
            var10000 = true;
        }

        this.mDesiredFocusableInTouchModeState = focusable;
        if (focusable) {
            this.mDesiredFocusableState = true;
        }

        super.setFocusableInTouchMode(focusable);
    }

    /**
     * 核对焦点坐标 如果emptyView不为空 显示emptyview
     */
    void checkFocus() {
        Adapter adapter = this.getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        boolean var10000;
        if (empty && !this.isInFilterMode()) {
            var10000 = false;
        } else {
            var10000 = true;
        }

        super.setFocusableInTouchMode(this.mDesiredFocusableInTouchModeState);
        super.setFocusable(this.mDesiredFocusableState);
        if (this.mEmptyView != null) {
            this.updateEmptyStatus(adapter == null || adapter.isEmpty());
        }

    }

    @SuppressLint({"WrongCall"})
    private void updateEmptyStatus(boolean empty) {
        if (this.isInFilterMode()) {
            empty = false;
        }

        if (empty) {
            if (this.mEmptyView != null) {
                this.mEmptyView.setVisibility(View.VISIBLE);
                this.setVisibility(View.GONE);
            } else {
                this.setVisibility(View.VISIBLE);
            }

            if (this.mDataChanged) {
                this.onLayout(false, this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
            }
        } else {
            if (this.mEmptyView != null) {
                this.mEmptyView.setVisibility(View.GONE);
            }

            this.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 通过位置 获取adapter的Item
     * @param position
     * @return
     */
    public Object getItemAtPosition(int position) {
        Adapter adapter = this.getAdapter();
        return adapter != null && position >= 0 ? adapter.getItem(position) : null;
    }

    /**
     * 通过位置 获取Item的id
     * @param position
     * @return
     */
    public long getItemIdAtPosition(int position) {
        Adapter adapter = this.getAdapter();
        return adapter != null && position >= 0 ? adapter.getItemId(position) : INVALID_ROW_ID;
    }

    /**
     * 强制删除setOnClickListener接口
     * @param l
     */
    public void setOnClickListener(OnClickListener l) {
        throw new RuntimeException("Don\'t call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
    }

    /**
     * 储存窗口内容的派发函数
     * @param container
     */
    @Override //viewGroup
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        this.dispatchFreezeSelfOnly(container);
    }

    /**
     * 回复窗口内容的派发函数
     * @param container
     */
    @Override //viewGroup
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        this.dispatchThawSelfOnly(container);
    }

    /**
     * 调用removeCallbacks 消息循环中移除mSelectionNotifier，相当于mOnItemSelectedListener 接口失效
     * ，注意这里的mOnItemSelectedListener对象并没有移除。
     */
    @Override //viewGroup
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks(this.mSelectionNotifier);
    }

    /**
     * 基本上就是为了调用选中事件接口
     */
    void selectionChanged() {
        if (this.mOnItemSelectedListener != null) {
            if (!this.mInLayout && !this.mBlockLayoutRequests) {
                this.fireOnSelected();
            } else {
                //准备布局走异步
                if (this.mSelectionNotifier == null) {
                    this.mSelectionNotifier = new AdapterView.SelectionNotifier();
                }

                this.post(this.mSelectionNotifier);
            }
        }
        //这里调用了辅助事件 如果没启动这个服务 无效，否则会向AccessibilityServiece发送消息
        if (this.mSelectedPosition != INVALID_POSITION && this.isShown() && !this.isInTouchMode()) {
            this.sendAccessibilityEvent(TYPE_VIEW_SELECTED);
        }

    }

    /**
     * 通知mOnItemSelectedListener 接口触发选项选中事件
     */
    private void fireOnSelected() {
        if (this.mOnItemSelectedListener != null) {
            int selection = this.getSelectedItemPosition();
            if (selection >= 0 && selection < this.getCount()) {
                View v = this.getSelectedView();
                this.mOnItemSelectedListener.onItemSelected(this, v, selection, this.getAdapter().getItemId(selection));
            } else {
                this.mOnItemSelectedListener.onNothingSelected(this);
            }

        }
    }

    /**
     * 无障碍服务使用
     * @param event
     * @return
     */
    @Override //view
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        View selectedView = this.getSelectedView();
        return selectedView != null && selectedView.getVisibility() == View.VISIBLE && selectedView.dispatchPopulateAccessibilityEvent(event);
    }
    /**
     * 无障碍服务使用
     */
    @Override
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEvent(child, event)) {
            AccessibilityEvent record = AccessibilityEvent.obtain();
            this.onInitializeAccessibilityEvent(record);
            child.dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        } else {
            return false;
        }
    }
    /**
     * 无障碍服务使用
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setScrollable(this.isScrollableForAccessibility());
        View selectedView = this.getSelectedView();
        if (selectedView != null) {
            info.setEnabled(selectedView.isEnabled());
        }

    }
    /**
     * 无障碍服务使用
     */
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setScrollable(this.isScrollableForAccessibility());
        View selectedView = this.getSelectedView();
        if (selectedView != null) {
            event.setEnabled(selectedView.isEnabled());
        }

        event.setCurrentItemIndex(this.getSelectedItemPosition());
        event.setFromIndex(this.getFirstVisiblePosition());
        event.setToIndex(this.getLastVisiblePosition());
        event.setItemCount(this.getCount());
    }
    /**
     * 无障碍服务使用
     */
    private boolean isScrollableForAccessibility() {
        Adapter adapter = this.getAdapter();
        if (adapter == null) {
            return false;
        } else {
            int itemCount = adapter.getCount();
            return itemCount > 0 && (this.getFirstVisiblePosition() > 0 || this.getLastVisiblePosition() < itemCount - 1);
        }
    }

    /**
     *  增加判断 this.mItemCount > 0 时动画有效。
     * @return
     */
    @Override //viewGroup
    protected boolean canAnimate() {
        return this.getLayoutAnimation() != null && this.mItemCount > 0;
    }

    /**
     * 新增函数 控制数据改变
     */
    protected void handleDataChanged() {
        int count = this.mItemCount;
        boolean found = false;
        if (count > 0) {
            int newPos;
            int selectablePos;
            if (this.mNeedSync) {
                this.mNeedSync = false;
                newPos = this.findSyncPosition();
                if (newPos >= 0) {
                    selectablePos = this.lookForSelectablePosition(newPos, true);
                    if (selectablePos == newPos) {
                        this.setNextSelectedPositionInt(newPos);
                        found = true;
                    }
                }
            }

            if (!found) {
                newPos = this.getSelectedItemPosition();
                if (newPos >= count) {
                    newPos = count - 1;
                }

                if (newPos < 0) {
                    newPos = 0;
                }

                selectablePos = this.lookForSelectablePosition(newPos, true);
                if (selectablePos < 0) {
                    selectablePos = this.lookForSelectablePosition(newPos, false);
                }

                if (selectablePos >= 0) {
                    this.setNextSelectedPositionInt(selectablePos);
                    this.checkSelectionChanged();
                    found = true;
                }
            }
        }

        if (!found) {
            this.mSelectedPosition = INVALID_POSITION;
            this.mSelectedRowId = INVALID_ROW_ID;
            this.mNextSelectedPosition = INVALID_POSITION;
            this.mNextSelectedRowId = INVALID_ROW_ID;
            this.mNeedSync = false;
            this.checkSelectionChanged();
        }

    }

    protected void checkSelectionChanged() {
        if (this.mSelectedPosition != this.mOldSelectedPosition || this.mSelectedRowId != this.mOldSelectedRowId) {
            this.selectionChanged();
            this.mOldSelectedPosition = this.mSelectedPosition;
            this.mOldSelectedRowId = this.mSelectedRowId;
        }

    }

    int findSyncPosition() {
        int count = this.mItemCount;
        if (count == 0) {
            return INVALID_POSITION;
        } else {
            long idToMatch = this.mSyncRowId;
            int seed = this.mSyncPosition;
            if (idToMatch == INVALID_ROW_ID) {
                return INVALID_POSITION;
            } else {
                seed = Math.max(0, seed);
                seed = Math.min(count - 1, seed);
                long endTime = SystemClock.uptimeMillis() + 100L;
                int first = seed;
                int last = seed;
                boolean next = false;
                Adapter adapter = this.getAdapter();
                if (adapter == null) {
                    return INVALID_POSITION;
                } else {
                    while (true) {
                        if (SystemClock.uptimeMillis() <= endTime) {
                            long rowId = adapter.getItemId(seed);
                            if (rowId == idToMatch) {
                                return seed;
                            }

                            boolean hitLast = last == count - 1;
                            boolean hitFirst = first == 0;
                            if (!hitLast || !hitFirst) {
                                if (!hitFirst && (!next || hitLast)) {
                                    if (hitLast || !next && !hitFirst) {
                                        --first;
                                        seed = first;
                                        next = true;
                                    }
                                } else {
                                    ++last;
                                    seed = last;
                                    next = false;
                                }
                                continue;
                            }
                        }

                        return INVALID_POSITION;
                    }
                }
            }
        }
    }

    /**
     * 返回当前位置 不知道什么意思
     * @param position
     * @param lookDown
     * @return
     */
    int lookForSelectablePosition(int position, boolean lookDown) {
        return position;
    }

    /**
     * 设置变量mSelectedPosition mSelectedRowId 的值为当前位置
     * @param position
     */
    void setSelectedPositionInt(int position) {
        this.mSelectedPosition = position;
        this.mSelectedRowId = this.getItemIdAtPosition(position);
    }

    protected void setNextSelectedPositionInt(int position) {
        this.mNextSelectedPosition = position;
        this.mNextSelectedRowId = this.getItemIdAtPosition(position);
        if (this.mNeedSync && this.mSyncMode == SYNC_SELECTED_POSITION && position >= 0) {
            this.mSyncPosition = position;
            this.mSyncRowId = this.mNextSelectedRowId;
        }

    }

    void rememberSyncState() {
        if (this.getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = (long) this.mLayoutHeight;
            View v;
            if (this.mSelectedPosition >= 0) {
                v = this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
                this.mSyncRowId = this.mNextSelectedRowId;
                this.mSyncPosition = this.mNextSelectedPosition;
                if (v != null) {
                    this.mSpecificTop = v.getTop();
                }

                this.mSyncMode = SYNC_SELECTED_POSITION;
            } else {
                v = this.getChildAt(0);
                Adapter adapter = this.getAdapter();
                if (this.mFirstPosition >= 0 && this.mFirstPosition < adapter.getCount()) {
                    this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
                } else {
                    this.mSyncRowId = -1L;
                }

                this.mSyncPosition = this.mFirstPosition;
                if (v != null) {
                    this.mSpecificTop = v.getTop();
                }

                this.mSyncMode = SYNC_FIRST_POSITION;
            }
        }

    }

    /**
     * 反射获取 mGroupFlags的值 根据文档，这个值用来决定padding的绘制
     * @return
     */
    protected int getGroupFlags() {
        try {
            Class e = Class.forName("android.view.ViewGroup");
            Field flags = e.getDeclaredField("mGroupFlags");
            flags.setAccessible(true);
            return flags.getInt(this);
        } catch (SecurityException var3) {
            var3.printStackTrace();
        } catch (NoSuchFieldException var4) {
            var4.printStackTrace();
        } catch (IllegalArgumentException var5) {
            var5.printStackTrace();
        } catch (IllegalAccessException var6) {
            var6.printStackTrace();
        } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
        }

        return 0;
    }

    /**
     * 通过反射 设置mGroupFlags的值，用来决定padding的绘制
     * @param f
     */
    protected void setGroupFlags(int f) {
        try {
            Class e = Class.forName("android.view.ViewGroup");
            Field flags = e.getDeclaredField("mGroupFlags");
            flags.setAccessible(true);
            flags.setInt(this, f);
        } catch (SecurityException var4) {
            var4.printStackTrace();
        } catch (NoSuchFieldException var5) {
            var5.printStackTrace();
        } catch (IllegalArgumentException var6) {
            var6.printStackTrace();
        } catch (IllegalAccessException var7) {
            var7.printStackTrace();
        } catch (ClassNotFoundException var8) {
            var8.printStackTrace();
        }

    }

    /**
     * 触发mFocusStateListener事件onFocusStart
     */
    public void onFocusStart() {
        if (this.mFocusStateListener != null) {
            this.mFocusStateListener.onFocusStart(this.getSelectedView(), this);
        }

    }
    /**
     * 触发mFocusStateListener事件onFocusFinished
     */
    public void onFocusFinished() {
        if (this.mFocusStateListener != null) {
            this.mFocusStateListener.onFocusFinished(this.getSelectedView(), this);
        }

    }

    public static class AdapterContextMenuInfo implements ContextMenuInfo {
        public View targetView;
        public int position;
        public long id;

        public AdapterContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }
    }

    /**
     * 数据改变观察者 用于子类实现使用
     */
    class AdapterDataSetObserver extends DataSetObserver {
        private Parcelable mInstanceState = null;

        AdapterDataSetObserver() {
        }

        /**
         * 数据改变
         */
        public void onChanged() {
            AdapterView.this.mDataChanged = true;
            AdapterView.this.mOldItemCount = AdapterView.this.mItemCount;
            AdapterView.this.mItemCount = AdapterView.this.getAdapter().getCount();
            if (AdapterView.this.getAdapter().hasStableIds()
                    && this.mInstanceState != null
                    && AdapterView.this.mOldItemCount == 0
                    && AdapterView.this.mItemCount > 0) {
                AdapterView.this.onRestoreInstanceState(this.mInstanceState);
                this.mInstanceState = null;
            } else {
                AdapterView.this.rememberSyncState();
            }

            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        /**
         * 数据无效
         */
        public void onInvalidated() {
            AdapterView.this.mDataChanged = true;
            if (AdapterView.this.getAdapter().hasStableIds()) {
                this.mInstanceState = AdapterView.this.onSaveInstanceState();
            }

            AdapterView.this.mOldItemCount = AdapterView.this.mItemCount;
            AdapterView.this.mItemCount = 0;
            AdapterView.this.mSelectedPosition = INVALID_POSITION;
            AdapterView.this.mSelectedRowId = INVALID_ROW_ID;
            AdapterView.this.mNextSelectedPosition = INVALID_POSITION;
            AdapterView.this.mNextSelectedRowId = INVALID_ROW_ID;
            AdapterView.this.mNeedSync = false;
            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        public void clearSavedState() {
            this.mInstanceState = null;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> var1, View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterView<?> var1, View view, int position, long id);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView<?> var1, View view, int position, long id);

        void onNothingSelected(AdapterView<?> var1);
    }

    private class SelectionNotifier implements Runnable {
        private SelectionNotifier() {
        }

        public void run() {
            if (AdapterView.this.mDataChanged) {
                if (AdapterView.this.getAdapter() != null) {
                    AdapterView.this.post(this);
                }
            } else {
                AdapterView.this.fireOnSelected();
            }

        }
    }
}
