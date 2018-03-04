package com.foxpower.flchatofandroid.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Created by fengli on 2018/3/1.
 */

public class EmotionPagerAdapter extends PagerAdapter{

    private List<GridView> gvs;

    public EmotionPagerAdapter(List<GridView> gvs) {
        this.gvs = gvs;
    }

    @Override
    public int getCount() {
        return gvs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ((ViewPager)container).removeView(gvs.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ((ViewPager)container).addView(gvs.get(position));
        return gvs.get(position);
    }
}
