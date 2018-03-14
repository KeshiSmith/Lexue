package com.zw.lexue.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 本文档为自定义无滑动ViewPager控件Java程序，去除手势滑动和滑动效果
 * Created by Keshi Smith on 2017/7/12.
 */

public class NoScrollViewPager extends ViewPager {

    //滑动效果参数，为true时无滑动效果，默认为false
    private boolean noScroll =false;

    public NoScrollViewPager(Context context){
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置此控件是否支持滑动效果，当noScroll为true时，无滑动效果
     * @param noScroll
     */
    public void setNoScroll(boolean noScroll){
        this.noScroll=noScroll;
    }

    @Override
    public void scrollTo(int x,int y){
        super.scrollTo(x,y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(noScroll)
            return false;
        else
            return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }


    @Override
    public void setCurrentItem(int item) {
        //去除滑动效果
        super.setCurrentItem(item,false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
}
