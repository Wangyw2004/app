package com.example.no1.features.post.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.featured.views.DraftBoxFragment;
import com.example.no1.features.featured.views.FeaturedListFragment;
import com.example.no1.features.featured.views.MyApplicationFragment;
import com.example.no1.features.post.adapters.PostPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class PostTabFragment extends Fragment {

    private UserSessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PostPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_tab, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewPager();

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        boolean isAdmin = sessionManager.isAdmin();
        boolean isLoggedIn = sessionManager.isLoggedIn();

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        // 所有用户都能看到日常帖子
        fragments.add(new PostListFragment());
        titles.add("日常帖子");

        // 所有用户都能看到精品帖子
        fragments.add(new FeaturedListFragment());
        titles.add("精品帖子");

        if (isAdmin) {
            // 管理员：显示草稿箱
            fragments.add(new DraftBoxFragment());
            titles.add("草稿箱");
        } else if (isLoggedIn) {
            // 普通登录用户：显示我的申请
            fragments.add(new MyApplicationFragment());
            titles.add("我的申请");
        }
        // 游客：不显示第三个Tab

        pagerAdapter = new PostPagerAdapter(this,
                fragments.toArray(new Fragment[0]),
                titles.toArray(new String[0]));

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(pagerAdapter.getPageTitle(position));
        }).attach();
    }
}