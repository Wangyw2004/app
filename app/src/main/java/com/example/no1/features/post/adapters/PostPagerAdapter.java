package com.example.no1.features.post.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PostPagerAdapter extends FragmentStateAdapter {

    private Fragment[] fragments;
    private String[] titles;

    // 构造函数1：接收 FragmentActivity
    public PostPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                            Fragment[] fragments, String[] titles) {
        super(fragmentActivity);
        this.fragments = fragments;
        this.titles = titles;
    }

    // 构造函数2：接收 Fragment（用于嵌套Fragment）
    public PostPagerAdapter(@NonNull Fragment fragment,
                            Fragment[] fragments, String[] titles) {
        super(fragment);
        this.fragments = fragments;
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    public String getPageTitle(int position) {
        return titles[position];
    }

    // 添加获取当前Fragment的方法
    public Fragment getCurrentFragment() {
        // 注意：ViewPager2 没有直接获取当前Fragment的方法
        // 这个方法需要在调用时传入当前position
        return null;
    }
}