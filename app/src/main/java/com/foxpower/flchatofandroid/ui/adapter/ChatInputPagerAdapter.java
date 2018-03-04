package com.foxpower.flchatofandroid.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by fengli on 2018/3/1.
 */

public class ChatInputPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> list;

    public ChatInputPagerAdapter(FragmentManager fm, ArrayList<Fragment>list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }
}
