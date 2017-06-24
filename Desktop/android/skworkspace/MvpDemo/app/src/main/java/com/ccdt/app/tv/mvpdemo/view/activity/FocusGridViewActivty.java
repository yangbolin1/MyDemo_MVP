package com.ccdt.app.tv.mvpdemo.view.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.ccdt.app.tv.mvpdemo.R;
import com.ccdt.app.tv.mvpdemo.view.adapter.FlipGridAdapter;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.FlipGridView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudz on 2017/4/19.
 */

public class FocusGridViewActivty extends Activity {

    @BindView(R.id.id_focus_root_view)
    FocusPositionManager focusPositionManager;
    @BindView(R.id.id_flip_grid)
    FocusFlipGridView gridview;

    FlipGridAdapter mAdapter;

    private static final String TAG="FocusGridViewActivty";
    private boolean hasFocused=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);
        ButterKnife.bind(this);

        gridview.setNumColumns(6);
        gridview.setHorizontalSpacing(30);
        gridview.setVerticalSpacing(30);
        gridview.setOnItemSelectedListener(mItemSelectedListener);
        gridview.setOnItemClickListener(mOnItemClickListener);
        gridview.setOnFocusFlipGridViewListener(mOnFocusFlipGridViewListener);
        gridview.setOnFlipGridViewRunnableListener(this.mOnFlipRunnableListener);
        mAdapter=new FlipGridAdapter(this);
        gridview.setAdapter(mAdapter);
        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.focus)));
        focusPositionManager.requestFocus(gridview,View.FOCUS_DOWN);
        this.gridview.setMaxFastStep(50);

        gridview.setOnFocusFlipGridViewListener(new FocusFlipGridView.OnFocusFlipGridViewListener() {
            @Override
            public void onLayoutDone(boolean var1) {

            }

            @Override
            public void onOutAnimationDone() {
                Log.i(TAG,"onOutAnimationDone");
            }

            @Override
            public void onReachGridViewTop() {

            }

            @Override
            public void onReachGridViewBottom() {
                Log.i(TAG,"onReachGridViewBottom");
                ArrayList<FlipGridAdapter.ObjInfo> objInfos=new ArrayList<FlipGridAdapter.ObjInfo>();
                for(int i=0;i<20;i++)
                {
                    objInfos.add(new FlipGridAdapter.ObjInfo());
                }
                mAdapter.addItem(objInfos);
            }
        });
    }


    ItemSelectedListener mItemSelectedListener=new ItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, boolean selectStatus, View adapterView) {
            Log.i(TAG,"onItemSelected");
        }
    };
    AdapterView.OnItemClickListener mOnItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> var1, View view, int position, long id) {
            Log.i(TAG,"onItemClick");
        }
    };
    FocusFlipGridView.OnFocusFlipGridViewListener mOnFocusFlipGridViewListener=new FocusFlipGridView.OnFocusFlipGridViewListener() {
        @Override
        public void onLayoutDone(boolean var1) {
            Log.i(TAG,"onLayoutDone");
            if ((!var1) && (!hasFocused) && (gridview.getChildCount() > 0))
            {
                Log.i(TAG,"onLayoutDone 1");
                hasFocused = true;
                //gridview.resetFocusParams();
                focusPositionManager.focusStart();
                focusPositionManager.invalidate();
                View selectedView = gridview.getSelectedView();
                int selectedItemPosition = gridview.getSelectedItemPosition();
                Log.d( TAG, "onLayoutDone selectedView=" + selectedView + " selectedItemPosition=" + selectedItemPosition);
                if ((selectedView != null) && (selectedItemPosition >= 0) && (mItemSelectedListener != null)) {
                    mItemSelectedListener.onItemSelected(selectedView, selectedItemPosition, true, gridview);
                }
            }

        }

        @Override
        public void onOutAnimationDone() {
            Log.i(TAG,"onOutAnimationDone");
        }

        @Override
        public void onReachGridViewTop() {
          Log.i(TAG,"onReachGridViewTop");
        }

        @Override
        public void onReachGridViewBottom() {
            Log.i(TAG,"onReachGridViewBottom");

        }
    };
    FlipGridView.OnFlipRunnableListener mOnFlipRunnableListener=new FlipGridView.OnFlipRunnableListener() {
        @Override
        public void onStart() {
            Log.i(TAG,"mOnFlipRunnableListener:start");
        }

        @Override
        public void onFlipItemRunnable(float var1, View var2, int var3) {

        }

        @Override
        public void onFinished() {
            Log.i(TAG,"mOnFlipRunnableListener:onFinished");
            Rect gridFocusRect = gridview.getFocusParams().focusRect();
            Rect focusRect = gridview.getItem().getFocusParams().focusRect();
            int i1 = gridFocusRect.right - gridFocusRect.left;
            int n = focusRect.right - focusRect.left;
            int j = gridFocusRect.bottom;
            int k = gridFocusRect.top;
            int m = focusRect.bottom;
            int i = focusRect.top;

            if ((i1 != n) || (j - k != m - i))
            {
//                gridview.resetFocusParams();
//                gridview.setScrolling(true);
                focusPositionManager.invalidate();
            }
            if (gridview.hasFocus())
            {
                Log.d("FileExplorerActivity", "flipfinished, has focus");
                //FileExplorerActivity.this.selectItem(true);
            }
            else
            {
                Log.d("FileExplorerActivity", "flipfinished, lost focus");
               // FileExplorerActivity.this.selectItem(false);
            }

        }
    };
}
