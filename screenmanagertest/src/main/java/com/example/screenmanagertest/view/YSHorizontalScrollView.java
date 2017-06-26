package com.example.screenmanagertest.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by tianxuguang on 6/23/15.
 */
public class YSHorizontalScrollView extends HorizontalScrollView {

    private int mItemWidth;
    private int mItemNumber;

    private boolean isTouching = false;
    private boolean mScollable = true;

    private Runnable runnableCheckAlign = new Runnable() {
        @Override
        public void run() {
            checkAlign();
        }
    };

    public YSHorizontalScrollView(Context context) {
        super(context);
    }

    public YSHorizontalScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setItemWidth(int width){
        mItemWidth = width;
    }

    public void setItemNumber(int number){
        mItemNumber = number;
    }

    public void setScollable(boolean scollable){
        mScollable = scollable;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();

        if(!mScollable){
            return false;
        }

        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_SCROLL) {
            isTouching = true;
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            isTouching = false;
            removeCallbacks(runnableCheckAlign);
            postDelayed(runnableCheckAlign, 100);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!isTouching) {
            removeCallbacks(runnableCheckAlign);
            postDelayed(runnableCheckAlign, 100);
        }
    }

    private void checkAlign() {
        int currentOffset = getScrollX();

        if(currentOffset > mItemWidth * mItemNumber){
            scrollBy(mItemWidth * mItemNumber - currentOffset, 0);
            return;
        }

        int left = currentOffset % mItemWidth;
        int dlt = left > mItemWidth/2 ? (mItemWidth - left) : -left;

        if (dlt != 0) {
            scrollBy(dlt, 0);
            return;
        }
    }
}
