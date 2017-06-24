//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.util.LongSparseArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ActionMode.Callback;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.AccessibilityDelegate;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import android.widget.AbsListView.SelectionBoundsAdjuster;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.AdapterContextMenuInfo;
import com.yunos.tv.app.widget.AdapterView.AdapterDataSetObserver;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.utils.ReflectUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbsBaseListView extends AdapterView<ListAdapter> {
    private static final String TAG = "AbsBaseListView";
    private static final boolean DEBUG = true;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    int mChoiceMode;
    static final int TOUCH_MODE_REST = -1;
    protected static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_TAP = 1;
    static final int TOUCH_MODE_DONE_WAITING = 2;
    protected static final int TOUCH_MODE_SCROLL = 3;
    static final int TOUCH_MODE_FLING = 4;
    static final int TOUCH_MODE_OVERSCROLL = 5;
    static final int TOUCH_MODE_OVERFLING = 6;
    static final int NO_POSITION = -1;
    static final int INVALID_POINTER = -1;
    protected static final int LAYOUT_NORMAL = 0;
    protected static final int LAYOUT_FORCE_TOP = 1;
    protected static final int LAYOUT_SET_SELECTION = 2;
    protected static final int LAYOUT_FORCE_BOTTOM = 3;
    protected static final int LAYOUT_SPECIFIC = 4;
    protected static final int LAYOUT_SYNC = 5;
    protected static final int LAYOUT_MOVE_SELECTION = 6;
    protected static final int LAYOUT_FORCE_LEFT = 7;
    protected static final int LAYOUT_FORCE_RIGHT = 8;
    protected static final int LAYOUT_FROM_MIDDLE = 9;
    static final int FLAG_DISALLOW_INTERCEPT = 524288;
    static final boolean PROFILE_FLINGING = false;
    protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 1024;
    protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 2048;
    static final float MAX_SCROLL_FACTOR = 0.33F;
    ListAdapter mAdapter;
    int mHeightMeasureSpec;
    int mWidthMeasureSpec;
    int mItemWidth;
    int mItemHeight;
    protected boolean mBlockLayoutRequests;
    int mSelectionLeftPadding;
    int mSelectionTopPadding;
    int mSelectionRightPadding;
    int mSelectionBottomPadding;
    protected final Rect mListPadding;
    protected Drawable mSelector;
    protected final AbsBaseListView.RecycleBin mRecycler;
    private DataSetObserver mDataSetObserver;
    private Rect mTouchFrame;
    boolean mAdapterHasStableIds;
    protected final boolean[] mIsScrap;
    int mDividerWidth;
    int mSelectedLeft;
    int mSelectorPosition;
    Adapter mScalableAdapter;
    View mScalableView;
    int mScalableViewSpacing;
    final AbsBaseListView.RecycleBin mScalableRecycler;
    protected int mSpacing;
    private Runnable mClearScrollingCache;
    private boolean mScrollingCacheEnabled;
    boolean mCachingStarted;
    boolean mCachingActive;
    protected Rect mSelectorRect;
    boolean mIsAttached;
    int mResurrectToPosition;
    protected int mLayoutMode;
    VelocityTracker mVelocityTracker;
    ContextMenuInfo mContextMenuInfo;
    AbsBaseListView.PerformClick mPerformClick;
    AbsBaseListView.CheckForLongPress mPendingCheckForLongPress;
    ActionMode mChoiceActionMode;
    AbsBaseListView.MultiChoiceModeWrapper mMultiChoiceModeCallback;
    SparseBooleanArray mCheckStates;
    LongSparseArray<Integer> mCheckedIdStates;
    private View mDownTouchView;
    int mCheckedItemCount;
    private int mDownTouchPosition;
    protected int mMotionPosition;
    protected int mTouchMode;
    boolean mAreAllItemsSelectable;
    private Rect mExactlyUserSelectedRect;
    private boolean mDrawSelectorOnTop;
    private OnScrollListener mOnScrollListener;
    protected int mLastScrollState;
    private boolean mIsChildViewEnabled;
    final Rect mTempRect;
    protected ArrayList<AbsBaseListView.FixedViewInfo> mHeaderViewInfos;
    protected ArrayList<AbsBaseListView.FixedViewInfo> mFooterViewInfos;
    protected boolean mNeedLayout;
    int mPreLoadCount;
    protected boolean needMeasureSelectedView;

    public void setPreLoadCount(int count) {
        this.mPreLoadCount = count;
    }

    public int getPreLoadCount() {
        return this.mPreLoadCount;
    }

    public int getFirstPosition() {
        return this.getFirstVisiblePosition();
    }

    public int getLastPosition() {
        return this.getLastVisiblePosition();
    }

    public int getFirsVisibletChildIndex() {
        return 0;
    }

    public int getLastVisibleChildIndex() {
        return this.getChildCount() - 1;
    }

    public int getVisibleChildCount() {
        return this.getChildCount();
    }

    public View getFirstChild() {
        return this.getChildAt(0);
    }

    public View getLastChild() {
        return this.getChildAt(this.getChildCount() - 1);
    }

    public View getFirstVisibleChild() {
        return this.getChildAt(0);
    }

    public View getLastVisibleChild() {
        return this.getChildAt(this.getChildCount() - 1);
    }

    protected boolean isDirectChildHeaderOrFooter(View child) {
        ArrayList headers = this.mHeaderViewInfos;
        int numHeaders = headers.size();

        for(int footers = 0; footers < numHeaders; ++footers) {
            if(child == ((AbsBaseListView.FixedViewInfo)headers.get(footers)).view) {
                return true;
            }
        }

        ArrayList var7 = this.mFooterViewInfos;
        int numFooters = var7.size();

        for(int i = 0; i < numFooters; ++i) {
            if(child == ((AbsBaseListView.FixedViewInfo)var7.get(i)).view) {
                return true;
            }
        }

        return false;
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if(this.mAdapter != null && !(this.mAdapter instanceof AbsBaseListView.HeaderViewListAdapter)) {
            throw new IllegalStateException("Cannot add header view to list -- setAdapter has already been called.");
        } else {
            AbsBaseListView.FixedViewInfo info = new AbsBaseListView.FixedViewInfo();
            info.view = v;
            info.data = data;
            info.isSelectable = isSelectable;
            this.mHeaderViewInfos.add(info);
            if(this.mAdapter != null && this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }

        }
    }

    public View getHeaderView(int index) {
        if(index <= this.getHeaderViewsCount() - 1 && index >= 0) {
            return ((AbsBaseListView.FixedViewInfo)this.mHeaderViewInfos.get(index)).view;
        } else {
            throw new IllegalArgumentException("Cannot get header");
        }
    }

    public void addHeaderView(View v) {
        this.addHeaderView(v, (Object)null, true);
    }

    public int getHeaderViewsCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if(this.mHeaderViewInfos.size() > 0) {
            boolean result = false;
            if(this.mAdapter != null && ((AbsBaseListView.HeaderViewListAdapter)this.mAdapter).removeHeader(v)) {
                if(this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }

                result = true;
            }

            this.removeFixedViewInfo(v, this.mHeaderViewInfos);
            return result;
        } else {
            return false;
        }
    }

    private void removeFixedViewInfo(View v, ArrayList<AbsBaseListView.FixedViewInfo> where) {
        int len = where.size();

        for(int i = 0; i < len; ++i) {
            AbsBaseListView.FixedViewInfo info = (AbsBaseListView.FixedViewInfo)where.get(i);
            if(info.view == v) {
                where.remove(i);
                break;
            }
        }

    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        AbsBaseListView.FixedViewInfo info = new AbsBaseListView.FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mFooterViewInfos.add(info);
        if(this.mAdapter != null && this.mDataSetObserver != null) {
            this.mDataSetObserver.onChanged();
        }

    }

    public void addFooterView(View v) {
        this.addFooterView(v, (Object)null, true);
    }

    public int getFooterViewsCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeFooterView(View v) {
        if(this.mFooterViewInfos.size() > 0) {
            boolean result = false;
            if(this.mAdapter != null && ((AbsBaseListView.HeaderViewListAdapter)this.mAdapter).removeFooter(v)) {
                if(this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }

                result = true;
            }

            this.removeFixedViewInfo(v, this.mFooterViewInfos);
            return result;
        } else {
            return false;
        }
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
    }

    boolean shouldShowSelector() {
        return this.hasFocus() && !this.isInTouchMode() || this.touchModeDrawsInPressedState();
    }

    void reportScrollStateChange(int newState) {
        if(newState != this.mLastScrollState) {
            this.mLastScrollState = newState;
            if(this.mOnScrollListener != null) {
                this.mLastScrollState = newState;
                this.mOnScrollListener.onScrollStateChanged(this, newState);
            }
        }

    }

    boolean touchModeDrawsInPressedState() {
        return false;
    }

    public AbsBaseListView(Context context) {
        super(context);
        this.mChoiceMode = 0;
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mRecycler = new AbsBaseListView.RecycleBin();
        this.mIsScrap = new boolean[1];
        this.mSelectedLeft = 0;
        this.mScalableRecycler = new AbsBaseListView.RecycleBin();
        this.mSpacing = 0;
        this.mSelectorRect = new Rect();
        this.mResurrectToPosition = 0;
        this.mLayoutMode = 0;
        this.mTouchMode = -1;
        this.mAreAllItemsSelectable = true;
        this.mDrawSelectorOnTop = true;
        this.mLastScrollState = 0;
        this.mTempRect = new Rect();
        this.mHeaderViewInfos = new ArrayList();
        this.mFooterViewInfos = new ArrayList();
        this.mNeedLayout = false;
        this.mPreLoadCount = 0;
        this.needMeasureSelectedView = true;
        this.initAbsSpinner();
    }

    public AbsBaseListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsBaseListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChoiceMode = 0;
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mRecycler = new AbsBaseListView.RecycleBin();
        this.mIsScrap = new boolean[1];
        this.mSelectedLeft = 0;
        this.mScalableRecycler = new AbsBaseListView.RecycleBin();
        this.mSpacing = 0;
        this.mSelectorRect = new Rect();
        this.mResurrectToPosition = 0;
        this.mLayoutMode = 0;
        this.mTouchMode = -1;
        this.mAreAllItemsSelectable = true;
        this.mDrawSelectorOnTop = true;
        this.mLastScrollState = 0;
        this.mTempRect = new Rect();
        this.mHeaderViewInfos = new ArrayList();
        this.mFooterViewInfos = new ArrayList();
        this.mNeedLayout = false;
        this.mPreLoadCount = 0;
        this.needMeasureSelectedView = true;
        this.initAbsSpinner();
    }

    protected void initOrResetVelocityTracker() {
        if(this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }

    }

    protected void initVelocityTrackerIfNotExists() {
        if(this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

    }

    protected void recycleVelocityTracker() {
        if(this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }

    }

    private void initAbsSpinner() {
        this.setFocusable(true);
        this.setWillNotDraw(false);
    }

    public void setAdapter(ListAdapter adapter) {
        if(this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
            this.resetList();
        }

        if(this.mHeaderViewInfos.size() <= 0 && this.mFooterViewInfos.size() <= 0) {
            this.mAdapter = adapter;
        } else {
            this.mAdapter = new AbsBaseListView.HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, adapter);
        }

        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = -9223372036854775808L;
        this.mRecycler.clear();
        if(this.mAdapter != null) {
            this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
            this.mOldItemCount = this.mItemCount;
            this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
            this.mItemCount = this.mAdapter.getCount();
            this.checkFocus();
            this.mDataSetObserver = new AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
            int position = this.initPosition();
            this.setSelectedPositionInt(position);
            this.setNextSelectedPositionInt(position);
            if(this.mItemCount == 0) {
                this.checkSelectionChanged();
            }
        } else {
            this.mAreAllItemsSelectable = true;
            this.checkFocus();
            this.resetList();
            this.checkSelectionChanged();
        }
        this.requestLayout();
    }

    protected void positionSelector(int position, View sel) {
        if(position != -1) {
            this.mSelectorPosition = position;
        }

        Rect selectorRect = this.mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if(sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster)sel).adjustListItemSelectionBounds(selectorRect);
        }

        this.positionSelector(selectorRect.left, selectorRect.top, selectorRect.right, selectorRect.bottom);
        boolean isChildViewEnabled = this.mIsChildViewEnabled;
        if(sel.isEnabled() != isChildViewEnabled) {
            this.mIsChildViewEnabled = !isChildViewEnabled;
            if(this.getSelectedItemPosition() != -1) {
                this.refreshDrawableState();
            }
        }

    }

    void positionSelector(int l, int t, int r, int b) {
        this.mSelectorRect.set(l - this.mSelectionLeftPadding, t - this.mSelectionTopPadding, r + this.mSelectionRightPadding, b + this.mSelectionBottomPadding);
    }

    protected View obtainView(int position, boolean[] isScrap) {
        isScrap[0] = false;
        View scrapView = this.mRecycler.getTransientStateView(position);
        if(scrapView != null) {
            return scrapView;
        } else {
            scrapView = this.mRecycler.getScrapView(position);
            View child;
            if(scrapView != null) {
                child = this.mAdapter.getView(position, scrapView, this);
                if(child != scrapView) {
                    this.mRecycler.addScrapView(scrapView, position);
                } else {
                    isScrap[0] = true;
                }
            } else {
                child = this.mAdapter.getView(position, (View)null, this);
            }

            if(this.mAdapterHasStableIds) {
                android.view.ViewGroup.LayoutParams vlp = child.getLayoutParams();
                AbsBaseListView.LayoutParams lp;
                if(vlp == null) {
                    lp = (AbsBaseListView.LayoutParams)this.generateDefaultLayoutParams();
                } else if(!this.checkLayoutParams(vlp)) {
                    lp = (AbsBaseListView.LayoutParams)this.generateLayoutParams(vlp);
                } else {
                    lp = (AbsBaseListView.LayoutParams)vlp;
                }

                lp.itemId = this.mAdapter.getItemId(position);
                child.setLayoutParams(lp);
            }

            return child;
        }
    }

    protected void invalidateParentIfNeeded() {
        if(this.isHardwareAccelerated() && this.getParent() instanceof View) {
            View parent = (View)this.getParent();
            parent.invalidate();
        }

    }

    int initPosition() {
        return this.mItemCount > 0?0:-1;
    }

    protected int getListLeft() {
        return this.mListPadding.left;
    }

    protected int getListTop() {
        return this.mListPadding.top;
    }

    protected int getListRight() {
        return this.getWidth() - this.mListPadding.right;
    }

    protected int getListBottom() {
        return this.getHeight() - this.mListPadding.bottom;
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
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

    protected void drawSelector(Canvas canvas) {
        if(this.hasFocus() && this.mSelector != null && this.mSelectorRect != null && !this.mSelectorRect.isEmpty()) {
            Rect selectorRect = new Rect(this.mExactlyUserSelectedRect != null?this.mExactlyUserSelectedRect:this.mSelectorRect);
            this.mSelector.setBounds(selectorRect);
            this.mSelector.draw(canvas);
        }

    }

    boolean resurrectSelectionIfNeeded() {
        if(this.mSelectedPosition < 0 && this.resurrectSelection()) {
            this.updateSelectorState();
            return true;
        } else {
            return false;
        }
    }

    void updateSelectorState() {
        if(this.mSelector != null) {
            if(this.shouldShowSelector()) {
                this.mSelector.setState(this.getDrawableState());
            } else {
                this.mSelector.setState(StateSet.NOTHING);
            }
        }

    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public boolean drawSclectorOnTop() {
        return this.mDrawSelectorOnTop;
    }

    public void setExactlyUserSelectedRect(int left, int top, int right, int bottom) {
        this.mExactlyUserSelectedRect = new Rect(left, top, right, bottom);
    }

    public void clearExactlyUserSelectedRect() {
        this.mExactlyUserSelectedRect = null;
    }

    public void setSelectorPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.mSelectionLeftPadding = leftPadding;
        this.mSelectionTopPadding = topPadding;
        this.mSelectionRightPadding = rightPadding;
        this.mSelectionBottomPadding = bottomPadding;
    }

    private void setupScalableView(View scalableView, View child) {
        AbsBaseListView.LayoutParams lp = (AbsBaseListView.LayoutParams)child.getLayoutParams();
        if(lp == null) {
            lp = (AbsBaseListView.LayoutParams)this.generateDefaultLayoutParams();
        }

        int childHeightSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mListPadding.top + this.mListPadding.bottom, lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, lp.width);
        scalableView.measure(childWidthSpec, childHeightSpec);
        int height = scalableView.getMeasuredHeight();
        int l = child.getLeft();
        int r = child.getRight();
        int t = child.getBottom() + this.mScalableViewSpacing;
        int b = t + height;
        scalableView.layout(l, t, r, b);
    }

    protected boolean performButtonActionOnTouchDown(MotionEvent event) {
        return (event.getButtonState() & 2) != 0 && this.showContextMenu();
    }

    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    public boolean showContextMenu() {
        if(this.isPressed() && this.mSelectedPosition >= 0) {
            int index = this.mSelectedPosition - this.mFirstPosition;
            View v = this.getChildAt(index);
            return this.dispatchLongPress(v, this.mSelectedPosition, this.mSelectedRowId);
        } else {
            return false;
        }
    }

    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = this.getPositionForView(originalView);
        if(longPressPosition < 0) {
            return false;
        } else {
            long longPressId = this.mAdapter.getItemId(longPressPosition);
            return this.dispatchLongPress(originalView, longPressPosition, longPressId);
        }
    }

    boolean performLongPress(View child, int longPressPosition, long longPressId) {
        if(this.mChoiceMode == 3) {
            if(this.mChoiceActionMode == null && (this.mChoiceActionMode = this.startActionMode(this.mMultiChoiceModeCallback)) != null) {
                this.setItemChecked(longPressPosition, true);
                this.performHapticFeedback(0);
            }

            return true;
        } else {
            boolean handled = false;
            if(this.mOnItemLongClickListener != null) {
                handled = this.mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
            }

            if(!handled) {
                this.mContextMenuInfo = this.createContextMenuInfo(child, longPressPosition, longPressId);
                handled = super.showContextMenuForChild(this);
            }

            if(handled) {
                this.performHapticFeedback(0);
            }

            return handled;
        }
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        boolean handled = false;
        if(this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, id);
        }

        if(!handled) {
            this.mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }

        if(handled) {
            this.performHapticFeedback(0);
        }

        return handled;
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }

    public boolean isItemChecked(int position) {
        return this.mChoiceMode != 0 && this.mCheckStates != null?this.mCheckStates.get(position):false;
    }

    public void setItemChecked(int position, boolean value) {
        if(this.mChoiceMode != 0) {
            if(value && this.mChoiceMode == 3 && this.mChoiceActionMode == null) {
                if(this.mMultiChoiceModeCallback == null || !this.mMultiChoiceModeCallback.hasWrappedCallback()) {
                    throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
                }

                this.mChoiceActionMode = this.startActionMode(this.mMultiChoiceModeCallback);
            }

            boolean updateIds;
            if(this.mChoiceMode != 2 && this.mChoiceMode != 3) {
                updateIds = this.mCheckedIdStates != null && this.mAdapter.hasStableIds();
                if(value || this.isItemChecked(position)) {
                    this.mCheckStates.clear();
                    if(updateIds) {
                        this.mCheckedIdStates.clear();
                    }
                }

                if(value) {
                    this.mCheckStates.put(position, true);
                    if(updateIds) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    }

                    this.mCheckedItemCount = 1;
                } else if(this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                    this.mCheckedItemCount = 0;
                }
            } else {
                updateIds = this.mCheckStates.get(position);
                this.mCheckStates.put(position, value);
                if(this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if(value) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                    }
                }

                if(updateIds != value) {
                    if(value) {
                        ++this.mCheckedItemCount;
                    } else {
                        --this.mCheckedItemCount;
                    }
                }

                if(this.mChoiceActionMode != null) {
                    long id = this.mAdapter.getItemId(position);
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, id, value);
                }
            }

            if(!this.mInLayout && !this.mBlockLayoutRequests) {
                this.mDataChanged = true;
                this.rememberSyncState();
                this.requestLayout();
            }

        }
    }

    public void getFocusedRect(Rect r) {
        View view = this.getSelectedView();
        if(view != null && view.getParent() == this) {
            view.getFocusedRect(r);
            this.offsetDescendantRectToMyCoords(view, r);
        } else {
            super.getFocusedRect(r);
        }

    }

    public void clearChoices() {
        if(this.mCheckStates != null) {
            this.mCheckStates.clear();
        }

        if(this.mCheckedIdStates != null) {
            this.mCheckedIdStates.clear();
        }

        this.mCheckedItemCount = 0;
    }

    void setScalableView(int position, View child) {
        if(this.mScalableAdapter != null) {
            View scalView = this.mScalableAdapter.getView(position, (View)null, this);
            Log.d("AbsBaseListView", " getScalableView position = " + position + " child = " + this.getChildAt(position));
            if(scalView != null && child != null) {
                this.setupScalableView(scalView, child);
                this.mScalableView = scalView;
            } else {
                this.mScalableView = null;
            }
        }

    }

    int getItemWidth() {
        return this.mItemWidth;
    }

    int getItemHeight() {
        return this.mItemHeight;
    }

    void clearScalableView() {
        this.mScalableView = null;
    }

    public void setScalableAdapter(Adapter adapter) {
        this.mScalableAdapter = adapter;
        this.mScalableRecycler.clear();
    }

    public void setScalableViewSpacing(int spacing) {
        this.mScalableViewSpacing = spacing;
    }

    @ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if(this.mScrollingCacheEnabled && !enabled) {
            this.clearScrollingCache();
        }

        this.mScrollingCacheEnabled = enabled;
    }

    protected void createScrollingCache() {
        if(this.mScrollingCacheEnabled && !this.mCachingStarted) {
            this.setChildrenDrawnWithCacheEnabled(true);
            this.setChildrenDrawingCacheEnabled(true);
            this.mCachingStarted = this.mCachingActive = true;
        }

    }

    protected void clearScrollingCache() {
        if(this.mClearScrollingCache == null) {
            this.mClearScrollingCache = new Runnable() {
                public void run() {
                    if(AbsBaseListView.this.mCachingStarted) {
                        AbsBaseListView.this.mCachingStarted = AbsBaseListView.this.mCachingActive = false;
                        AbsBaseListView.this.setChildrenDrawnWithCacheEnabled(false);
                        if((AbsBaseListView.this.getPersistentDrawingCache() & 2) == 0) {
                            AbsBaseListView.this.setChildrenDrawingCacheEnabled(false);
                        }

                        if(!AbsBaseListView.this.isAlwaysDrawnWithCacheEnabled()) {
                            AbsBaseListView.this.invalidate();
                        }
                    }

                }
            };
        }

        this.post(this.mClearScrollingCache);
    }

    protected void resetList() {
        this.mDataChanged = false;
        this.mNeedSync = false;
        this.removeAllViewsInLayout();
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = -9223372036854775808L;
        this.mOldItemCount = this.mItemCount;
        this.mItemCount = 0;
        this.setSelectedPositionInt(-1);
        this.setNextSelectedPositionInt(-1);
        this.invalidate();
    }

    void requestLayoutIfNecessary() {
        if(this.getChildCount() > 0) {
            this.resetList();
            this.requestLayout();
            this.invalidate();
        }

    }

    public void offsetChildrenTopAndBottom(int offset) {
        int childCount = this.getChildCount();

        for(int i = childCount - 1; i >= 0; --i) {
            this.getChildAt(i).offsetTopAndBottom(offset);
        }

    }

    protected void offsetChildrenLeftAndRight(int offset) {
        int childCount = this.getChildCount();

        for(int i = childCount - 1; i >= 0; --i) {
            this.getChildAt(i).offsetLeftAndRight(offset);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        this.mListPadding.left = this.getPaddingLeft() + this.mSelectionLeftPadding;
        this.mListPadding.top = this.getPaddingTop() + this.mSelectionTopPadding;
        this.mListPadding.right = this.getPaddingRight() + this.mSelectionRightPadding;
        this.mListPadding.bottom = this.getPaddingBottom() + this.mSelectionBottomPadding;
        if(this.mDataChanged) {
            this.handleDataChanged();
        }

        int preferredHeight = 0;
        int preferredWidth = 0;
        boolean needsMeasuring = true;
        if(this.needMeasureSelectedView) {
            int selectedPosition = this.getSelectedItemPosition();
            if(this.mAdapter != null) {
                int view = this.mAdapter.getCount();
                if(this.getHeaderViewsCount() < view) {
                    selectedPosition = this.getHeaderViewsCount();
                }
            }

            if(selectedPosition >= 0 && this.mAdapter != null && selectedPosition < this.mAdapter.getCount()) {
                View view1 = this.mRecycler.getScrapView(selectedPosition);
                if(view1 == null) {
                    view1 = this.mAdapter.getView(selectedPosition, (View)null, this);
                    if(view1 != null) {
                        this.mRecycler.addScrapView(view1, selectedPosition);
                    }
                }

                if(view1 != null) {
                    if(view1.getLayoutParams() == null) {
                        this.mBlockLayoutRequests = true;
                        view1.setLayoutParams(this.generateDefaultLayoutParams());
                        this.mBlockLayoutRequests = false;
                    }

                    this.measureChild(view1, widthMeasureSpec, heightMeasureSpec);
                    this.mItemHeight = this.getChildHeight(view1);
                    this.mItemWidth = this.getChildWidth(view1);
                    preferredHeight = this.getChildHeight(view1) + this.mListPadding.top + this.mListPadding.bottom;
                    preferredWidth = this.getChildWidth(view1) + this.mListPadding.left + this.mListPadding.right;
                    needsMeasuring = false;
                }
            }
        }

        if(needsMeasuring) {
            preferredHeight = this.mListPadding.top + this.mListPadding.bottom;
            if(widthMode == 0) {
                preferredWidth = this.mListPadding.left + this.mListPadding.right;
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

    int getChildHeight(View child) {
        return child.getMeasuredHeight();
    }

    protected int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }

    protected void keyPressed() {
        View child = this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
        if(child != null) {
            child.setPressed(true);
        }

        this.setPressed(true);
    }

    protected void dispatchUnpress() {
        for(int i = this.getChildCount() - 1; i >= 0; --i) {
            this.getChildAt(i).setPressed(false);
        }

        this.setPressed(false);
    }

    void recycleAllViews() {
        int childCount = this.getChildCount();
        AbsBaseListView.RecycleBin recycleBin = this.mRecycler;
        int position = this.mFirstPosition;

        for(int i = 0; i < childCount; ++i) {
            View v = this.getChildAt(i);
            int index = position + i;
            recycleBin.addScrapView(v, index);
        }

    }

    public void setSelection(int position, boolean animate) {
        boolean shouldAnimate = animate && this.mFirstPosition <= position && position <= this.mFirstPosition + this.getChildCount() - 1;
        this.setSelectionInt(position, shouldAnimate);
    }

    public void setSelection(int position) {
        this.setNextSelectedPositionInt(position);
        this.requestLayout();
        this.invalidate();
    }

    void setSelectionInt(int position) {
        this.setNextSelectedPositionInt(position);
        boolean awakeScrollbars = false;
        int selectedPosition = this.mSelectedPosition;
        if(selectedPosition >= 0) {
            if(position == selectedPosition - 1) {
                awakeScrollbars = true;
            } else if(position == selectedPosition + 1) {
                awakeScrollbars = true;
            }
        }

        if(awakeScrollbars) {
            this.awakenScrollBars();
        }

    }

    void setSelectionInt(int position, boolean animate) {
        if(position != this.mOldSelectedPosition) {
            this.mBlockLayoutRequests = true;
            int var10000 = position - this.mSelectedPosition;
            this.setNextSelectedPositionInt(position);
            this.mBlockLayoutRequests = false;
        }

    }

    protected abstract void layoutChildren();

    public void setSpacing(int spacing) {
        this.mSpacing = spacing;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof AbsBaseListView.LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new AbsBaseListView.LayoutParams(p);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AbsBaseListView.LayoutParams(this.getContext(), attrs);
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new AbsBaseListView.LayoutParams(-2, -2, 0);
    }

    public View getSelectedView() {
        if(this.mItemCount > 0 && this.mSelectedPosition >= 0) {
            return this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
        } else {
            Log.e("AbsBaseListView", "getSelectedView: return null! this:" + this.toString() + ", mItemCount:" + this.mItemCount + ", mSelectedPosition:" + this.mSelectedPosition);
            return null;
        }
    }

    public void requestLayout() {
        if(!this.mBlockLayoutRequests) {
            this.mNeedLayout = true;
            super.requestLayout();
        }

    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus && this.mSelectedPosition < 0 && !this.isInTouchMode()) {
            if(!this.mIsAttached && this.mAdapter != null) {
                this.mDataChanged = true;
                this.mOldItemCount = this.mItemCount;
                this.mItemCount = this.mAdapter.getCount();
            }

            this.resurrectSelection();
        }

    }

    boolean resurrectSelection() {
        return true;
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public int getCount() {
        return this.mItemCount;
    }

    void hideSelector() {
        if(this.mSelectedPosition != -1) {
            if(this.mLayoutMode != 4) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }

            if(this.mNextSelectedPosition >= 0 && this.mNextSelectedPosition != this.mSelectedPosition) {
                this.mResurrectToPosition = this.mNextSelectedPosition;
            }

            this.setSelectedPositionInt(-1);
            this.setNextSelectedPositionInt(-1);
            this.mSelectedLeft = 0;
        }

    }

    protected int reconcileSelectedPosition() {
        int position = this.mSelectedPosition;
        if(position < 0) {
            position = this.mResurrectToPosition;
        }

        position = Math.max(0, position);
        position = Math.min(position, this.mItemCount - 1);
        return position;
    }

    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if(frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }

        int count = this.getChildCount();

        for(int i = count - 1; i >= 0; --i) {
            View child = this.getChildAt(i);
            if(child.getVisibility() == VISIBLE) {
                child.getHitRect(frame);
                if(frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }

        return -1;
    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        AbsBaseListView.SavedState ss = new AbsBaseListView.SavedState(superState);
        ss.selectedId = this.getSelectedItemId();
        if(ss.selectedId >= 0L) {
            ss.position = this.getSelectedItemPosition();
        } else {
            ss.position = -1;
        }

        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        AbsBaseListView.SavedState ss = (AbsBaseListView.SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        if(ss.selectedId >= 0L) {
            this.mDataChanged = true;
            this.mNeedSync = true;
            this.mSyncRowId = ss.selectedId;
            this.mSyncPosition = ss.position;
            this.mSyncMode = 0;
            this.requestLayout();
        }

    }

    static View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
        int size = scrapViews.size();
        if(size <= 0) {
            return null;
        } else {
            for(int i = 0; i < size; ++i) {
                View view = (View)scrapViews.get(i);
                int fromPosition = ((AbsBaseListView.LayoutParams)view.getLayoutParams()).scrappedFromPosition;
                if(fromPosition == position) {
                    scrapViews.remove(i);
                    return view;
                }
            }

            return (View)scrapViews.remove(size - 1);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsAttached = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRecycler.clear();
        this.mIsAttached = false;
    }

    public static String getExceptionString(Throwable e) {
        if(e == null) {
            return "e==null";
        } else {
            StringBuffer err = new StringBuffer();
            err.append(e.toString());
            err.append("\n");
            err.append("at ");
            StackTraceElement[] stack = e.getStackTrace();
            if(stack != null) {
                for(int cause = 0; cause < stack.length; ++cause) {
                    err.append(stack[cause].toString());
                    err.append("\n");
                }
            }

            Throwable var4 = e.getCause();
            if(var4 != null) {
                err.append("\n");
                err.append("Caused by: ");
                err.append(getExceptionString(var4));
            }

            return err.toString();
        }
    }

    class CheckForLongPress extends AbsBaseListView.WindowRunnnable implements Runnable {
        CheckForLongPress() {
            super();
        }

        public void run() {
            int motionPosition = AbsBaseListView.this.mMotionPosition;
            View child = AbsBaseListView.this.getChildAt(motionPosition - AbsBaseListView.this.mFirstPosition);
            if(child != null) {
                int longPressPosition = AbsBaseListView.this.mMotionPosition;
                long longPressId = AbsBaseListView.this.mAdapter.getItemId(AbsBaseListView.this.mMotionPosition);
                boolean handled = false;
                if(this.sameWindow() && !AbsBaseListView.this.mDataChanged) {
                    handled = AbsBaseListView.this.performLongPress(child, longPressPosition, longPressId);
                }

                if(handled) {
                    AbsBaseListView.this.mTouchMode = -1;
                    AbsBaseListView.this.setPressed(false);
                    child.setPressed(false);
                } else {
                    AbsBaseListView.this.mTouchMode = 2;
                }
            }

        }
    }

    public class FixedViewInfo {
        public View view;
        public Object data;
        public boolean isSelectable;

        public FixedViewInfo() {
        }
    }

    public class HeaderViewListAdapter implements WrapperListAdapter, Filterable {
        private final ListAdapter mAdapter;
        ArrayList<AbsBaseListView.FixedViewInfo> mHeaderViewInfos;
        ArrayList<AbsBaseListView.FixedViewInfo> mFooterViewInfos;
        final ArrayList<AbsBaseListView.FixedViewInfo> EMPTY_INFO_LIST = new ArrayList();
        boolean mAreAllFixedViewsSelectable;
        private final boolean mIsFilterable;

        public HeaderViewListAdapter(ArrayList<AbsBaseListView.FixedViewInfo> headerViewInfos, ArrayList<AbsBaseListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            this.mAdapter = adapter;
            this.mIsFilterable = adapter instanceof Filterable;
            if(headerViewInfos == null) {
                this.mHeaderViewInfos = this.EMPTY_INFO_LIST;
            } else {
                this.mHeaderViewInfos = headerViewInfos;
            }

            if(footerViewInfos == null) {
                this.mFooterViewInfos = this.EMPTY_INFO_LIST;
            } else {
                this.mFooterViewInfos = footerViewInfos;
            }

            this.mAreAllFixedViewsSelectable = this.areAllListInfosSelectable(this.mHeaderViewInfos) && this.areAllListInfosSelectable(this.mFooterViewInfos);
        }

        public int getHeadersCount() {
            return this.mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return this.mFooterViewInfos.size();
        }

        public boolean isEmpty() {
            return this.mAdapter == null || this.mAdapter.isEmpty();
        }

        private boolean areAllListInfosSelectable(ArrayList<AbsBaseListView.FixedViewInfo> infos) {
            if(infos != null) {
                Iterator var3 = infos.iterator();

                while(var3.hasNext()) {
                    AbsBaseListView.FixedViewInfo info = (AbsBaseListView.FixedViewInfo)var3.next();
                    if(!info.isSelectable) {
                        return false;
                    }
                }
            }

            return true;
        }

        public boolean removeHeader(View v) {
            for(int i = 0; i < this.mHeaderViewInfos.size(); ++i) {
                AbsBaseListView.FixedViewInfo info = (AbsBaseListView.FixedViewInfo)this.mHeaderViewInfos.get(i);
                if(info.view == v) {
                    this.mHeaderViewInfos.remove(i);
                    this.mAreAllFixedViewsSelectable = this.areAllListInfosSelectable(this.mHeaderViewInfos) && this.areAllListInfosSelectable(this.mFooterViewInfos);
                    return true;
                }
            }

            return false;
        }

        public boolean removeFooter(View v) {
            for(int i = 0; i < this.mFooterViewInfos.size(); ++i) {
                AbsBaseListView.FixedViewInfo info = (AbsBaseListView.FixedViewInfo)this.mFooterViewInfos.get(i);
                if(info.view == v) {
                    this.mFooterViewInfos.remove(i);
                    this.mAreAllFixedViewsSelectable = this.areAllListInfosSelectable(this.mHeaderViewInfos) && this.areAllListInfosSelectable(this.mFooterViewInfos);
                    return true;
                }
            }

            return false;
        }

        public int getCount() {
            return this.mAdapter != null?this.getFootersCount() + this.getHeadersCount() + this.mAdapter.getCount():this.getFootersCount() + this.getHeadersCount();
        }

        public boolean areAllItemsEnabled() {
            return this.mAdapter != null?this.mAreAllFixedViewsSelectable && this.mAdapter.areAllItemsEnabled():true;
        }

        public boolean isEnabled(int position) {
            int numHeaders = this.getHeadersCount();
            if(position < numHeaders) {
                return ((AbsBaseListView.FixedViewInfo)this.mHeaderViewInfos.get(position)).isSelectable;
            } else {
                int adjPosition = position - numHeaders;
                int adapterCount = 0;
                if(this.mAdapter != null) {
                    adapterCount = this.mAdapter.getCount();
                    if(adjPosition < adapterCount) {
                        return this.mAdapter.isEnabled(adjPosition);
                    }
                }

                return ((AbsBaseListView.FixedViewInfo)this.mFooterViewInfos.get(adjPosition - adapterCount)).isSelectable;
            }
        }

        public Object getItem(int position) {
            int numHeaders = this.getHeadersCount();
            if(position < numHeaders) {
                return ((AbsBaseListView.FixedViewInfo)this.mHeaderViewInfos.get(position)).data;
            } else {
                int adjPosition = position - numHeaders;
                int adapterCount = 0;
                if(this.mAdapter != null) {
                    adapterCount = this.mAdapter.getCount();
                    if(adjPosition < adapterCount) {
                        return this.mAdapter.getItem(adjPosition);
                    }
                }

                return ((AbsBaseListView.FixedViewInfo)this.mFooterViewInfos.get(adjPosition - adapterCount)).data;
            }
        }

        public long getItemId(int position) {
            int numHeaders = this.getHeadersCount();
            if(this.mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = this.mAdapter.getCount();
                if(adjPosition < adapterCount) {
                    return this.mAdapter.getItemId(adjPosition);
                }
            }

            return -1L;
        }

        public boolean hasStableIds() {
            return this.mAdapter != null?this.mAdapter.hasStableIds():false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int numHeaders = this.getHeadersCount();
            if(position < numHeaders) {
                return ((AbsBaseListView.FixedViewInfo)this.mHeaderViewInfos.get(position)).view;
            } else {
                int adjPosition = position - numHeaders;
                int adapterCount = 0;
                if(this.mAdapter != null) {
                    adapterCount = this.mAdapter.getCount();
                    if(adjPosition < adapterCount) {
                        return this.mAdapter.getView(adjPosition, convertView, parent);
                    }
                }

                return ((AbsBaseListView.FixedViewInfo)this.mFooterViewInfos.get(adjPosition - adapterCount)).view;
            }
        }

        public int getItemViewType(int position) {
            int numHeaders = this.getHeadersCount();
            if(this.mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = this.mAdapter.getCount();
                if(adjPosition < adapterCount) {
                    return this.mAdapter.getItemViewType(adjPosition);
                }
            }

            return -2;
        }

        public int getViewTypeCount() {
            return this.mAdapter != null?this.mAdapter.getViewTypeCount():1;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if(this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(observer);
            }

        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if(this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(observer);
            }

        }

        public Filter getFilter() {
            return this.mIsFilterable?((Filterable)this.mAdapter).getFilter():null;
        }

        public ListAdapter getWrappedAdapter() {
            return this.mAdapter;
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        int viewType;
        int scrappedFromPosition;
        long itemId = -1L;
        @ExportedProperty(
                category = "list"
        )
        boolean recycledHeaderFooter;
        @ExportedProperty(
                category = "list"
        )
        boolean forceAdd;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface MultiChoiceModeListener extends Callback {
        void onItemCheckedStateChanged(ActionMode var1, int var2, long var3, boolean var5);
    }

    class MultiChoiceModeWrapper implements AbsBaseListView.MultiChoiceModeListener {
        private AbsBaseListView.MultiChoiceModeListener mWrapped;

        MultiChoiceModeWrapper() {
        }

        public void setWrapped(AbsBaseListView.MultiChoiceModeListener wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean hasWrappedCallback() {
            return this.mWrapped != null;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(this.mWrapped.onCreateActionMode(mode, menu)) {
                AbsBaseListView.this.setLongClickable(false);
                return true;
            } else {
                return false;
            }
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            AbsBaseListView.this.mChoiceActionMode = null;
            AbsBaseListView.this.clearChoices();
            AbsBaseListView.this.mDataChanged = true;
            AbsBaseListView.this.rememberSyncState();
            AbsBaseListView.this.requestLayout();
            AbsBaseListView.this.setLongClickable(true);
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            this.mWrapped.onItemCheckedStateChanged(mode, position, id, checked);
            if(AbsBaseListView.this.getCheckedItemCount() == 0) {
                mode.finish();
            }

        }
    }

    class PerformClick extends AbsBaseListView.WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        PerformClick() {
            super();
        }

        public void run() {
            if(!AbsBaseListView.this.mDataChanged) {
                ListAdapter adapter = AbsBaseListView.this.mAdapter;
                int motionPosition = this.mClickMotionPosition;
                if(adapter != null && AbsBaseListView.this.mItemCount > 0 && motionPosition != -1 && motionPosition < adapter.getCount() && this.sameWindow()) {
                    View view = AbsBaseListView.this.getChildAt(motionPosition - AbsBaseListView.this.mFirstPosition);
                    if(view != null) {
                        AbsBaseListView.this.performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                    }
                }

            }
        }
    }

    public class RecycleBin {
        private AbsBaseListView.RecyclerListener mRecyclerListener;
        private int mFirstActivePosition;
        private View[] mActiveViews = new View[0];
        private ArrayList<View>[] mScrapViews;
        private int mViewTypeCount;
        private ArrayList<View> mCurrentScrap;
        private ArrayList<View> mSkippedScrap;
        private SparseArray<View> mTransientStateViews;

        public RecycleBin() {
        }

        public void setViewTypeCount(int viewTypeCount) {
            if(viewTypeCount < 1) {
                throw new IllegalArgumentException("Can\'t have a viewTypeCount < 1");
            } else {
                ArrayList[] scrapViews = new ArrayList[viewTypeCount];

                for(int i = 0; i < viewTypeCount; ++i) {
                    scrapViews[i] = new ArrayList();
                }

                this.mViewTypeCount = viewTypeCount;
                this.mCurrentScrap = scrapViews[0];
                this.mScrapViews = scrapViews;
            }
        }

        public void markChildrenDirty() {
            int i;
            int var6;
            if(this.mViewTypeCount == 1) {
                ArrayList count = this.mCurrentScrap;
                i = count.size();

                for(int scrap = 0; scrap < i; ++scrap) {
                    ((View)count.get(scrap)).forceLayout();
                }
            } else {
                var6 = this.mViewTypeCount;

                for(i = 0; i < var6; ++i) {
                    ArrayList var7 = this.mScrapViews[i];
                    int scrapCount = var7.size();

                    for(int j = 0; j < scrapCount; ++j) {
                        ((View)var7.get(j)).forceLayout();
                    }
                }
            }

            if(this.mTransientStateViews != null) {
                var6 = this.mTransientStateViews.size();

                for(i = 0; i < var6; ++i) {
                    ((View)this.mTransientStateViews.valueAt(i)).forceLayout();
                }
            }

        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        void clear() {
            int i;
            if(this.mViewTypeCount == 1) {
                ArrayList typeCount = this.mCurrentScrap;
                i = typeCount.size();

                for(int scrap = 0; scrap < i; ++scrap) {
                    AbsBaseListView.this.removeDetachedView((View)typeCount.remove(i - 1 - scrap), false);
                }
            } else {
                int var6 = this.mViewTypeCount;

                for(i = 0; i < var6; ++i) {
                    ArrayList var7 = this.mScrapViews[i];
                    int scrapCount = var7.size();

                    for(int j = 0; j < scrapCount; ++j) {
                        AbsBaseListView.this.removeDetachedView((View)var7.remove(scrapCount - 1 - j), false);
                    }
                }
            }

            if(this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }

        }

        public void fillActiveViews(int childCount, int firstActivePosition) {
            if(this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }

            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;

            for(int i = 0; i < childCount; ++i) {
                View child = AbsBaseListView.this.getChildAt(i);
                AbsBaseListView.LayoutParams lp = (AbsBaseListView.LayoutParams)child.getLayoutParams();
                if(lp != null && lp.viewType != -2) {
                    activeViews[i] = child;
                }
            }

        }

        View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if(index >= 0 && index < activeViews.length) {
                View match = activeViews[index];
                activeViews[index] = null;
                return match;
            } else {
                return null;
            }
        }

        View getTransientStateView(int position) {
            if(this.mTransientStateViews == null) {
                return null;
            } else {
                int index = this.mTransientStateViews.indexOfKey(position);
                if(index < 0) {
                    return null;
                } else {
                    View result = (View)this.mTransientStateViews.valueAt(index);
                    this.mTransientStateViews.removeAt(index);
                    return result;
                }
            }
        }

        void clearTransientStateViews() {
            if(this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }

        }

        View getScrapView(int position) {
            if(this.mViewTypeCount == 1) {
                return AbsBaseListView.retrieveFromScrap(this.mCurrentScrap, position);
            } else {
                int whichScrap = AbsBaseListView.this.mAdapter.getItemViewType(position);
                return whichScrap >= 0 && whichScrap < this.mScrapViews.length?AbsBaseListView.retrieveFromScrap(this.mScrapViews[whichScrap], position):null;
            }
        }

        public void addScrapView(View scrap, int position) {
            AbsBaseListView.LayoutParams lp = (AbsBaseListView.LayoutParams)scrap.getLayoutParams();
            if(lp != null) {
                lp.scrappedFromPosition = position;
                int viewType = lp.viewType;
                boolean scrapHasTransientState = false;

                try {
                    scrapHasTransientState = ((Boolean)ReflectUtils.invokeMethod(scrap, "hasTransientState", new Class[0], new Object[0])).booleanValue();
                } catch (Exception var9) {
                    ;
                }

                if(this.shouldRecycleViewType(viewType) && !scrapHasTransientState) {
                    try {
                        ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }

                    if(this.mViewTypeCount == 1) {
                        this.mCurrentScrap.add(scrap);
                    } else {
                        this.mScrapViews[viewType].add(scrap);
                    }

                    scrap.setAccessibilityDelegate((AccessibilityDelegate)null);
                    if(this.mRecyclerListener != null) {
                        this.mRecyclerListener.onMovedToScrapHeap(scrap);
                    }

                } else {
                    if(viewType != -2 || scrapHasTransientState) {
                        if(this.mSkippedScrap == null) {
                            this.mSkippedScrap = new ArrayList();
                        }

                        this.mSkippedScrap.add(scrap);
                    }

                    if(scrapHasTransientState) {
                        if(this.mTransientStateViews == null) {
                            this.mTransientStateViews = new SparseArray();
                        }

                        try {
                            ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
                        } catch (Exception var8) {
                            var8.printStackTrace();
                        }

                        this.mTransientStateViews.put(position, scrap);
                    }

                }
            }
        }

        public void removeSkippedScrap() {
            if(this.mSkippedScrap != null) {
                int count = this.mSkippedScrap.size();

                for(int i = 0; i < count; ++i) {
                    AbsBaseListView.this.removeDetachedView((View)this.mSkippedScrap.get(i), false);
                }

                this.mSkippedScrap.clear();
            }
        }

        public void scrapActiveViews() {
            View[] activeViews = this.mActiveViews;
            boolean hasListener = this.mRecyclerListener != null;
            boolean multipleScraps = this.mViewTypeCount > 1;
            ArrayList scrapViews = this.mCurrentScrap;
            int count = activeViews.length;

            for(int i = count - 1; i >= 0; --i) {
                View victim = activeViews[i];
                if(victim != null) {
                    AbsBaseListView.LayoutParams lp = (AbsBaseListView.LayoutParams)victim.getLayoutParams();
                    int whichScrap = lp.viewType;
                    activeViews[i] = null;
                    boolean scrapHasTransientState = false;

                    try {
                        scrapHasTransientState = ((Boolean)ReflectUtils.invokeMethod(victim, "hasTransientState", new Class[0], new Object[0])).booleanValue();
                    } catch (Exception var12) {
                        ;
                    }

                    if(this.shouldRecycleViewType(whichScrap) && !scrapHasTransientState) {
                        if(multipleScraps) {
                            scrapViews = this.mScrapViews[whichScrap];
                        }

                        try {
                            ReflectUtils.invokeMethod(victim, "dispatchStartTemporaryDetach", new Object[0]);
                        } catch (Exception var13) {
                            var13.printStackTrace();
                        }

                        lp.scrappedFromPosition = this.mFirstActivePosition + i;
                        scrapViews.add(victim);
                        victim.setAccessibilityDelegate((AccessibilityDelegate)null);
                        if(hasListener) {
                            this.mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    } else {
                        if(whichScrap != -2 || scrapHasTransientState) {
                            AbsBaseListView.this.removeDetachedView(victim, false);
                        }

                        if(scrapHasTransientState) {
                            if(this.mTransientStateViews == null) {
                                this.mTransientStateViews = new SparseArray();
                            }

                            this.mTransientStateViews.put(this.mFirstActivePosition + i, victim);
                        }
                    }
                }
            }

            this.pruneScrapViews();
        }

        private void pruneScrapViews() {
            int maxViews = this.mActiveViews.length;
            int viewTypeCount = this.mViewTypeCount;
            ArrayList[] scrapViews = this.mScrapViews;

            for(int hasTransientState = 0; hasTransientState < viewTypeCount; ++hasTransientState) {
                ArrayList i = scrapViews[hasTransientState];
                int v = i.size();
                int extras = v - maxViews;
                --v;

                for(int j = 0; j < extras; ++j) {
                    AbsBaseListView.this.removeDetachedView((View)i.remove(v--), false);
                }
            }

            boolean var10 = false;
            if(this.mTransientStateViews != null) {
                for(int var11 = 0; var11 < this.mTransientStateViews.size(); ++var11) {
                    View var12 = (View)this.mTransientStateViews.valueAt(var11);

                    try {
                        var10 = ((Boolean)ReflectUtils.invokeMethod(var12, "hasTransientState", new Class[0], new Object[0])).booleanValue();
                    } catch (Exception var9) {
                        ;
                    }

                    if(!var10) {
                        this.mTransientStateViews.removeAt(var11);
                        --var11;
                    }
                }
            }

        }

        void reclaimScrapViews(List<View> views) {
            if(this.mViewTypeCount == 1) {
                views.addAll(this.mCurrentScrap);
            } else {
                int viewTypeCount = this.mViewTypeCount;
                ArrayList[] scrapViews = this.mScrapViews;

                for(int i = 0; i < viewTypeCount; ++i) {
                    ArrayList scrapPile = scrapViews[i];
                    views.addAll(scrapPile);
                }
            }

        }

        void setCacheColorHint(int color) {
            int count;
            int i;
            if(this.mViewTypeCount == 1) {
                ArrayList activeViews = this.mCurrentScrap;
                count = activeViews.size();

                for(i = 0; i < count; ++i) {
                    ((View)activeViews.get(i)).setDrawingCacheBackgroundColor(color);
                }
            } else {
                int var7 = this.mViewTypeCount;

                for(count = 0; count < var7; ++count) {
                    ArrayList var9 = this.mScrapViews[count];
                    int victim = var9.size();

                    for(int j = 0; j < victim; ++j) {
                        ((View)var9.get(j)).setDrawingCacheBackgroundColor(color);
                    }
                }
            }

            View[] var8 = this.mActiveViews;
            count = var8.length;

            for(i = 0; i < count; ++i) {
                View var10 = var8[i];
                if(var10 != null) {
                    var10.setDrawingCacheBackgroundColor(color);
                }
            }

        }
    }

    public interface RecyclerListener {
        void onMovedToScrapHeap(View var1);
    }

    static class SavedState extends BaseSavedState {
        long selectedId;
        int position;
        public static final Creator<AbsBaseListView.SavedState> CREATOR = new Creator() {
            public AbsBaseListView.SavedState createFromParcel(Parcel in) {
                return new AbsBaseListView.SavedState(in);
            }

            public AbsBaseListView.SavedState[] newArray(int size) {
                return new AbsBaseListView.SavedState[size];
            }
        };

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

    private class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = AbsBaseListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return AbsBaseListView.this.hasWindowFocus() && AbsBaseListView.this.getWindowAttachCount() == this.mOriginalAttachCount;
        }
    }
}
