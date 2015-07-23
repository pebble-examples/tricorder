package com.pebble.tricorder.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class TricorderPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> mFragmentTitles = new ArrayList<String>();
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();

    public TricorderPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    public void addFragment(final Fragment fragment, final String fragmentTitle) {
        mFragmentList.add(fragment);
        mFragmentTitles.add(fragmentTitle);
    }
}
