package com.quanjing.dutu.app.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quanjing.dutu.R;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.ui.common.MWTTabBar;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;

public class MDTMainFragment extends Fragment
{
    private ViewPager _viewPager;
    private MDTMainPagerAdapter _viewPagerAdapter;
    private MWTTabBar _tabBar;

    public MDTMainFragment()
    {
        // Required empty public constructor
    }

    public static MDTMainFragment newInstance()
    {
        MDTMainFragment fragment = new MDTMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        _viewPager = (ViewPager) fragmentView.findViewById(R.id.MainPager);
        _viewPager.setOffscreenPageLimit(999);
        _viewPagerAdapter = new MDTMainPagerAdapter(getFragmentManager());
        _viewPager.setAdapter(_viewPagerAdapter);
        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int index)
            {
                handlePageSelected(index);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        _tabBar = (MWTTabBar) fragmentView.findViewById(R.id.TabBar);

        Typeface typeface = MWTThemer.getInstance().getWTFont();
        _tabBar.getItemViewAt(0).setIconTextWithTypeface("\uf112", typeface); // DT Home Icon
        _tabBar.getItemViewAt(1).setIconTextWithTypeface("\uf113", typeface); // DT Life Icon
        _tabBar.getItemViewAt(2).setIconTextWithTypeface("\uf115", typeface); // DT Wiki Icon
        _tabBar.getItemViewAt(3).setIconTextWithTypeface("\uf111", typeface); // DT App  Icon
        _tabBar.getItemViewAt(4).setIconTextWithTypeface("\uf114", typeface); // DT User Icon

        _tabBar.getItemViewAt(0).setTitleText("首页");
        _tabBar.getItemViewAt(1).setTitleText("生活");
        _tabBar.getItemViewAt(2).setTitleText("百科");
        _tabBar.getItemViewAt(3).setTitleText("应用");
        _tabBar.getItemViewAt(4).setTitleText("我");

        _tabBar.setIconTextSize(28);
        _tabBar.setTopPadding(5);

        _tabBar.setOnTabSelectionEventListener(new MWTTabBar.OnTabSelectionEventListener()
        {
            @Override
            public boolean onWillSelectTab(int tabIndex)
            {
                return handleOnWillSelectTab(tabIndex);
            }

            @Override
            public void onDidSelectTab(int tabIndex)
            {
                handleOnDidSelectTab(tabIndex);
            }
        });

        switchToPage(0);

        return fragmentView;
    }

    private void handlePageSelected(int index)
    {
        _tabBar.setSelectedTabIndex(index);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (_tabBar.getSelectedTabIndex() == 4)
        {
            if (!MWTAuthManager.getInstance().isAuthenticated())
            {
                _tabBar.setSelectedTabIndex(0);
            }
        }
    }

    private boolean handleOnWillSelectTab(int tabIndex)
    {
        if (tabIndex == 4)
        {
            MWTAuthManager am = MWTAuthManager.getInstance();
            if (!am.isAuthenticated())
            {
                Intent intent = new Intent(getActivity(), MWTAuthSelectActivity.class);
                startActivityForResult(intent, 1);
                return false;
            }
            else
            {
                return true;
            }
        }

        return true;
    }

    private void handleOnDidSelectTab(int tabIndex)
    {
        switchToPage(tabIndex);
    }

    private void switchToPage(int tabIndex)
    {
        switch (tabIndex)
        {
        case 0:
            _viewPager.setCurrentItem(tabIndex, false);
            getActivity().getActionBar().setTitle("首页");
            break;
        case 1:
            _viewPager.setCurrentItem(tabIndex, false);
            getActivity().getActionBar().setTitle("生活");
            break;
        case 2:
            _viewPager.setCurrentItem(tabIndex, false);
            getActivity().getActionBar().setTitle("百科");
            break;
        case 3:
            _viewPager.setCurrentItem(tabIndex, false);
            getActivity().getActionBar().setTitle("应用");
            break;
        case 4:
            _viewPager.setCurrentItem(tabIndex, false);
            getActivity().getActionBar().setTitle("我");
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
        case 1:
            MWTAuthManager am = MWTAuthManager.getInstance();
            if (am.isAuthenticated())
            {
                _tabBar.setSelectedTabIndex(4);
            }
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }


    public void switchToLifePage()
    {
        _viewPager.setCurrentItem(1, true);
    }

    public void switchToAppPage()
    {
        _viewPager.setCurrentItem(3, true);
    }

    public void switchToWikiPage()
    {
        _viewPager.setCurrentItem(2, true);
    }
}
