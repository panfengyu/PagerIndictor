package fy.learn.com.pagerindictor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import fy.learn.com.pagerindictor.adapter.BaseAdapter;
import fy.learn.com.pagerindictor.util.SizeUtil;


/**
 * Created by Panfengyu on 2018/12/18.
 */
public class CommonTitleScrollView extends HorizontalScrollView {

    private LinearLayout    mLlContainer;
    private Context mContext;
    // 跟踪触摸实际事件，用来获取速率
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;

    private int mFingerDownX;
    private int mFingerDownY;

    private int mItemWidth;
    private int mCurrentItem;

    private WrapperAdapter mAdapter;

    private OnItemSelectedListener mOnItemSelectedListener;

    private int mWidth;
    private int mLastSelectedWidthDetal;

    public CommonTitleScrollView(Context context) {
        this(context, null);
    }

    public CommonTitleScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mVelocityTracker = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mItemWidth = SizeUtil.dp2px(context, 100);

        mLlContainer = new LinearLayout(context);
        mLlContainer.setOrientation(LinearLayout.HORIZONTAL);
        mLlContainer.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mLlContainer, params);
    }

    public void setAdapter(BaseAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter is null...");
        }
        mAdapter = new WrapperAdapter(adapter);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i, null, mLlContainer);
            mLlContainer.addView(view);
        }
        mCurrentItem = 0;
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(mLlContainer.getChildAt(mCurrentItem + 1), mCurrentItem);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnItemSelectedListener is null...");
        }
        mOnItemSelectedListener = listener;

        mOnItemSelectedListener.onItemSelected(mLlContainer.getChildAt(mCurrentItem + 1), mCurrentItem);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                mFingerDownX = (int) ev.getRawX();
                mFingerDownY = (int) ev.getRawY();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                mVelocityTracker.clear();
                int fingerUpX = (int) ev.getRawX();
                int fingerUpY = (int) ev.getRawY();
                int detalX = mFingerDownX - fingerUpX;
                int detalY = mFingerDownY - fingerUpY;

                if (Math.abs(velocityX) > 400 && Math.abs(detalX) > Math.abs(detalY)) {
                    flingScroll(velocityX);
                } else {
                    if (detalX == 0) {
                        break;
                    } else {
                        scrollItem(detalX);
                    }
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    private void scrollItem(int detal) {
        int scrollX = getScrollX();
        int currentItem = 0;
        int detalSize = 0;
        int childCount = mLlContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int left = mLlContainer.getChildAt(i).getLeft();
            int right = mLlContainer.getChildAt(i).getRight();
            if (scrollX >= left && scrollX < right) {
                currentItem = i;
                detalSize = scrollX - left;
                break;
            }
        }

        int currentItemWidth = mLlContainer.getChildAt(currentItem).getWidth();

        if (detal > 0) {
            if (detalSize > currentItemWidth / 2) {
                currentItem += 1;
            }
        } else {
            if (detalSize > currentItemWidth / 2) {
                currentItem += 1;
            }
        }
        scrollToItem(currentItem);
    }

    private void flingScroll(int velocity) {
        int currentItem = mCurrentItem;
        if (velocity < 0) {
            currentItem += 1;
        } else {
            currentItem -= 1;
        }

        if (mAdapter == null || currentItem >= mAdapter.getDataCount() || currentItem < 0) {
            return;
        }
        scrollToItem(currentItem);
    }

    private void scrollToItem(int currentItem) {

        if (currentItem != mCurrentItem) {
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemDelected(mLlContainer.getChildAt(mCurrentItem + 1), mCurrentItem);
                mOnItemSelectedListener.onItemSelected(mLlContainer.getChildAt(currentItem + 1), currentItem);
            }
        }


        View selectChild = mLlContainer.getChildAt(currentItem + 1);
        selectChild.measure(0, 0);
        int selectedWidthDetal = selectChild.getMeasuredWidth() - selectChild.getWidth();
        Log.d("pfy","selectChild.getMeasuredWidth() = " + selectChild.getMeasuredWidth() + " getWidth() = " + selectChild.getWidth() +
        " selectChild.getLeft() = " + selectChild.getLeft() + " mLastSelectedWidthDetal = "+ mLastSelectedWidthDetal);
        if (selectChild.getMeasuredWidth() > getWidth()) {
            if (currentItem > mCurrentItem) {
                smoothScrollTo(selectChild.getLeft()
                        + (selectChild.getMeasuredWidth() - getWidth()) / 2 - mLastSelectedWidthDetal, 0);
            } else {
                smoothScrollTo(selectChild.getLeft()
                        + (selectChild.getMeasuredWidth() - getWidth()) / 2, 0);
            }
            mLastSelectedWidthDetal = selectedWidthDetal;
            mCurrentItem = currentItem;
            return;
        }
        if (currentItem > mCurrentItem) {
            smoothScrollTo(selectChild.getLeft() - (getWidth() - selectChild.getMeasuredWidth()) / 2 - mLastSelectedWidthDetal, 0);
        } else {
            smoothScrollTo(selectChild.getLeft() - (getWidth() - selectChild.getMeasuredWidth()) / 2, 0);
        }
        mLastSelectedWidthDetal = selectedWidthDetal;
        mCurrentItem = currentItem;
    }

    private class WrapperAdapter implements BaseAdapter {

        private BaseAdapter mBaseAdapter;
        private int mAddCount;

        WrapperAdapter(BaseAdapter adapter) {
            mBaseAdapter = adapter;
        }

        @Override
        public int getCount() {
            mAddCount = mBaseAdapter.getSpaceView() == null ? 0 : 2;
            return mBaseAdapter.getCount() + mAddCount;
        }

        @Override
        public int getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == getCount() - 1) {
                return "";
            }
            return mBaseAdapter.getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mBaseAdapter.getSpaceView() != null && (position == 0 || position == getCount() - 1)) {
                View spaceView = mBaseAdapter.getSpaceView();
                spaceView.setMinimumWidth(mWidth / 3);
                return spaceView;
            }

            View view = mBaseAdapter.getView(position - 1, convertView, parent);
            view.setMinimumWidth(mWidth / 3);
            return view;
        }

        @Override
        public View getSpaceView() {
            return null;
        }

        public int getAddCount() {
            return mAddCount;
        }

        int getDataCount() {
            return mBaseAdapter.getCount();
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(View view, int index);

        void onItemDelected(View view, int index);
    }

}
