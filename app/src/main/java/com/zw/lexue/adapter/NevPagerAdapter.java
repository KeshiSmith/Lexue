package com.zw.lexue.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 本文档实现导航页面跳转适配器的编写。
 * Created by Keshi Smith on 2017/6/5.
 */

public class NevPagerAdapter extends PagerAdapter {

    private List<View> views = new ArrayList<View>();

    public NevPagerAdapter(List<View> views){
        super();
        this.views=views;
    }
    
    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }
}
