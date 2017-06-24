package com.yunos.tv.app.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SpinnerAdapter;

public abstract class AbsSpinner extends AdapterView<SpinnerAdapter> {
    SpinnerAdapter mAdapter;
    int mHeightMeasureSpec;
    int mWidthMeasureSpec;
    int mSelectionLeftPadding;
    int mSelectionTopPadding;
    int mSelectionRightPadding;
    int mSelectionBottomPadding;
    protected final Rect mSpinnerPadding;
    protected final AbsSpinner.RecycleBin mRecycler;
    private DataSetObserver mDataSetObserver;
    boolean mByPosition;
    private Rect mTouchFrame;
    protected Drawable mSelector;
    private boolean mDrawSelectorOnTop;
    Rect mSelectorRect;

    public AbsSpinner(Context context) {
        super(context);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new AbsSpinner.RecycleBin();
        this.mByPosition = false;
        this.mDrawSelectorOnTop = true;
        this.mSelectorRect = new Rect();
        this.initAbsSpinner();
    }

    public AbsSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new AbsSpinner.RecycleBin();
        this.mByPosition = false;
        this.mDrawSelectorOnTop = true;
        this.mSelectorRect = new Rect();
        this.initAbsSpinner();
    }

    public void setRecycleByPosition(boolean byPosition) {
        this.mByPosition = byPosition;
    }

    public void setSelector(Drawable selector) {
        this.mSelector = selector;
        Rect padding = new Rect();
        this.mSelector.getPadding(padding);
        this.setSelectorPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    public void setSelector(int selectorId) {
        this.mSelector = this.getContext().getResources().getDrawable(selectorId);
        Rect padding = new Rect();
        this.mSelector.getPadding(padding);
        this.setSelectorPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    /**
     * 设置选择器的padding，此处四个变量在onMeasure调用是被成功设置。
     * @param leftPadding
     * @param topPadding
     * @param rightPadding
     * @param bottomPadding
     */
    public void setSelectorPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.mSelectionLeftPadding = leftPadding;
        this.mSelectionTopPadding = topPadding;
        this.mSelectionRightPadding = rightPadding;
        this.mSelectionBottomPadding = bottomPadding;
    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public boolean drawSclectorOnTop() {
        return this.mDrawSelectorOnTop;
    }

    protected void drawSelector(Canvas canvas) {
        if (this.hasFocus() && this.mSelector != null && this.mSelectorRect != null && !this.mSelectorRect.isEmpty()) {
            Rect selectorRect = new Rect(this.mSelectorRect);
            this.mSelector.setBounds(selectorRect);
            this.mSelector.draw(canvas);
        }

    }

    protected void positionSelector(int l, int t, int r, int b) {
        this.mSelectorRect.set(l - this.mSelectionLeftPadding, t - this.mSelectionTopPadding, r + this.mSelectionRightPadding, b + this.mSelectionBottomPadding);
    }

    protected void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        boolean clipToPadding = (this.getGroupFlags() & 34) == 34;
        int flags;
        if (clipToPadding) {
            saveCount = canvas.save();
            int drawSelectorOnTop = this.getScrollX();
            flags = this.getScrollY();
            canvas.clipRect(drawSelectorOnTop + this.getPaddingLeft(), flags + this.getPaddingTop(), drawSelectorOnTop + this.getRight() - this.getLeft() - this.getPaddingRight(), flags + this.getBottom() - this.getTop() - this.getPaddingBottom());
            int flags1 = this.getGroupFlags();
            flags1 &= -35;
            this.setGroupFlags(flags1);
        }

        boolean drawSelectorOnTop1 = this.drawSclectorOnTop();
        if (!drawSelectorOnTop1) {
            this.drawSelector(canvas);
        }

        super.dispatchDraw(canvas);
        if (drawSelectorOnTop1) {
            this.drawSelector(canvas);
        }

        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
            flags = this.getGroupFlags();
            flags &= -35;
            this.setGroupFlags(flags);
        }

    }

    /**
     * 初始化AbsSpinner 设置焦点可用 设置非绘制窗口状态
     */
    private void initAbsSpinner() {
        this.setFocusable(true);
        //此窗口非绘制窗口
        this.setWillNotDraw(false);
    }

    /**
     * 1 注册mDataSetObserver 观察者
     * 2 发起事件通知SelectionChanged
     * 3 发起重新布局通知requestLayout
     * @param adapter
     */
    @Override //AdapterView
    public void setAdapter(SpinnerAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
            this.resetList();
        }

        this.mAdapter = adapter;
        this.mOldSelectedPosition = AdapterView.INVALID_POSITION;
        this.mOldSelectedRowId = AdapterView.INVALID_ROW_ID;
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            this.checkFocus();
            this.mDataSetObserver = new AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            int position = this.mItemCount > 0 ? 0 : AdapterView.INVALID_POSITION;
            this.setSelectedPositionInt(position);
            this.setNextSelectedPositionInt(position);
            if (this.mItemCount == 0) {
                this.checkSelectionChanged();
            }
        } else {
            this.checkFocus();
            this.resetList();
            this.checkSelectionChanged();
        }

        this.requestLayout();
    }

    /**
     * 重置AdapterView初始列表状态，并通知视图无效请求重绘invalidate
     */
    protected void resetList() {
        this.mDataChanged = false;
        this.mNeedSync = false;
        this.removeAllViewsInLayout();
        this.mOldSelectedPosition = AdapterView.INVALID_POSITION;
        this.mOldSelectedRowId = AdapterView.INVALID_ROW_ID;
        this.setSelectedPositionInt(AdapterView.INVALID_POSITION);
        this.setNextSelectedPositionInt(AdapterView.INVALID_POSITION);
        this.invalidate();
    }

    /**
     * 测量窗口以及子窗口宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override //view
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        this.mSpinnerPadding.left = this.getPaddingLeft() + this.mSelectionLeftPadding;
        this.mSpinnerPadding.top = this.getPaddingTop() + this.mSelectionTopPadding;
        this.mSpinnerPadding.right = this.getPaddingRight() + this.mSelectionRightPadding;
        this.mSpinnerPadding.bottom = this.getPaddingBottom() + this.mSelectionBottomPadding;
        if (this.mDataChanged) {
            this.handleDataChanged();
        }

        int preferredHeight = 0;
        int preferredWidth = 0;
        boolean needsMeasuring = true;
        int selectedPosition = this.getSelectedItemPosition();
        if (selectedPosition >= 0 && this.mAdapter != null && selectedPosition < this.mAdapter.getCount()) {
            View view = this.mRecycler.get(selectedPosition);
            if (view == null) {
                view = this.mAdapter.getView(selectedPosition, (View) null, this);
            }

            if (view != null) {
                this.mRecycler.put(selectedPosition, view);
            }

            if (view != null) {
                if (view.getLayoutParams() == null) {
                    this.mBlockLayoutRequests = true;
                    view.setLayoutParams(this.generateDefaultLayoutParams());
                    this.mBlockLayoutRequests = false;
                }

                this.measureChild(view, widthMeasureSpec, heightMeasureSpec);
                preferredHeight = this.getChildHeight(view) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
                preferredWidth = this.getChildWidth(view) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
                needsMeasuring = false;
            }
        }

        if (needsMeasuring) {
            preferredHeight = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
            if (widthMode == 0) {
                preferredWidth = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
            }
        }

        preferredHeight = Math.max(preferredHeight, this.getSuggestedMinimumHeight());
        preferredWidth = Math.max(preferredWidth, this.getSuggestedMinimumWidth());
        int heightSize = resolveSizeAndState(preferredHeight, heightMeasureSpec, 0);
        int widthSize = resolveSizeAndState(preferredWidth, widthMeasureSpec, 0);
        this.setMeasuredDimension(widthSize, heightSize);
        this.mHeightMeasureSpec = heightMeasureSpec;
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    /**
     * 实际调用child.getMeasuredHeight()
     * @param child
     * @return
     */
    int getChildHeight(View child) {
        return child.getMeasuredHeight();
    }

    /**
     * 实际调用child.getMeasuredWidth()
     * @param child
     * @return
     */
    int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }

    /**
     * 返回 ViewGroup.LayoutParams ,通用的布局参数
     * @return
     */
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    /**
     * 把所有子窗口注册到回收MAP
     */
    protected void recycleAllViews() {
        int childCount = this.getChildCount();
        AbsSpinner.RecycleBin recycleBin = this.mRecycler;
        int position = this.mFirstPosition;

        for (int i = 0; i < childCount; ++i) {
            View v = this.getChildAt(i);
            int index = position + i;
            recycleBin.put(index, v);
        }

    }

    /**
     * 选中该选项
     * @param position 选项的位置
     * @param animate 是否启用动画
     */
    public void setSelection(int position, boolean animate) {
        boolean shouldAnimate = animate && this.mFirstPosition <= position && position <= this.mFirstPosition + this.getChildCount() - 1;
        this.setSelectionInt(position, shouldAnimate);
    }

    /**
     * 选中该选项
     * @param selection 选项的位置
     */
    public void setSelection(int selection) {
        this.setNextSelectedPositionInt(selection);
        this.requestLayout();
        this.invalidate();
    }

    /**
     * 注意 ：
     * delta 是目标位置与当前选择位置的差
     * animate 是否启用动画
     * @param position
     * @param animate
     */
    void setSelectionInt(int position, boolean animate) {
        if (position != this.mOldSelectedPosition) {
            this.mBlockLayoutRequests = true;
            int delta = position - this.mSelectedPosition;
            this.setNextSelectedPositionInt(position);
            this.layout(delta, animate);
            this.mBlockLayoutRequests = false;
        }
    }

    /**
     *
     * @param delta 位置的差值
     * @param animate 是否包含动画
     */
    protected abstract void layout(int delta,boolean animate);

    /**
     * 获取选中选项的窗口，注意：是第一层布局的孩子节点。
     * @return
     */
    public View getSelectedView() {
        return this.mItemCount > 0 && this.mSelectedPosition >= 0 ? this.getChildAt(this.mSelectedPosition - this.mFirstPosition) : null;
    }

    /**
     * 如果用户强制锁定布局 mBlockLayoutRequests 则不启用
     */
    @Override //View
    public void requestLayout() {
        if (!this.mBlockLayoutRequests) {
            super.requestLayout();
        }

    }

    /**
     * 获取适配器
     * @return
     */
    @Override //AdapterView
    public SpinnerAdapter getAdapter() {
        return this.mAdapter;
    }

    /**
     * 获取选项数目
     * @return
     */
    @Override  //AdapterView
    public int getCount() {
        return this.mItemCount;
    }

    /**
     * 通过像素返回目标position 注意这里的x y是相对坐标
     * @param x
     * @param y
     * @return
     */
    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if (frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }
        int count = this.getChildCount();
        for (int i = count - 1; i >= 0; --i) {
            View child = this.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return -1;
    }

    /**
     * 实现了 选中选项的id 位置 信息的记录
     * @return
     */
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        AbsSpinner.SavedState ss = new AbsSpinner.SavedState(superState);
        ss.selectedId = this.getSelectedItemId();
        if (ss.selectedId >= 0L) {
            ss.position = this.getSelectedItemPosition();
        } else {
            ss.position = -1;
        }

        return ss;
    }

    /**
     * 回复数据信息 注意此处重新布局
     * @param state
     */
    public void onRestoreInstanceState(Parcelable state) {
        AbsSpinner.SavedState ss = (AbsSpinner.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.selectedId >= 0L) {
            this.mDataChanged = true;
            this.mNeedSync = true;
            this.mSyncRowId = ss.selectedId;
            this.mSyncPosition = ss.position;
            this.mSyncMode = 0;
            this.requestLayout();
        }

    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AbsSpinner.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AbsSpinner.class.getName());
    }

    public class RecycleBin {
        private final SparseArray<View> mScrapHeap = new SparseArray();

        public RecycleBin() {
        }

        public void put(int position, View v) {
            this.mScrapHeap.put(position, v);
        }

        View get(int position) {
            View result = (View) this.mScrapHeap.get(position);
            if (result != null) {
                this.mScrapHeap.delete(position);
            }

            if (result == null && AbsSpinner.this.mByPosition && this.mScrapHeap.size() > 0) {
                position = this.mScrapHeap.keyAt(0);
                result = (View) this.mScrapHeap.get(position);
                if (result != null) {
                    this.mScrapHeap.delete(position);
                }
            }

            return result;
        }

        public void clear() {
            SparseArray scrapHeap = this.mScrapHeap;
            int count = scrapHeap.size();

            for (int i = 0; i < count; ++i) {
                View view = (View) scrapHeap.valueAt(i);
                if (view != null) {
                    AbsSpinner.this.removeDetachedView(view, true);
                }
            }

            scrapHeap.clear();
        }
    }

    static class SavedState extends BaseSavedState {
        long selectedId;
        int position;
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
//        public static final Creator<AbsSpinner.SavedState> CREATOR = new Creator() {
//            public AbsSpinner.SavedState createFromParcel(Parcel in) {
//                return new AbsSpinner.SavedState(in, (AbsSpinner.SavedState)null);
//            }
//
//            public AbsSpinner.SavedState[] newArray(int size) {
//                return new AbsSpinner.SavedState[size];
//            }
//        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedId = in.readLong();
            this.position = in.readInt();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.selectedId);
            out.writeInt(this.position);
        }

        public String toString() {
            return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " position=" + this.position + "}";
        }
    }
}
