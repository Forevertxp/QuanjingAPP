package com.quanjing.quanjing.app.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.quanjing.quanjing.app.R;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.ui.common.MWTTabBar;
import com.quanjing.weitu.app.ui.settings.MWTSettingsActivity;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;
import com.quanjing.weitu.app.ui.user.MWTUploadPicActivity;
import com.quanjing.weitu.app.ui.user.MWTUserInfoEditActivity;

public class MQJMainFragment extends Fragment {
    private ViewPager _viewPager;
    private MQJMainPagerAdapter _viewPagerAdapter;
    private MWTTabBar _tabBar;

    public MQJMainFragment() {
        // Required empty public constructor
    }

    public static MQJMainFragment newInstance() {
        MQJMainFragment fragment = new MQJMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupActionBar();

        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        _viewPager = (ViewPager) fragmentView.findViewById(R.id.MainPager);
        _viewPager.setOffscreenPageLimit(999);
        _viewPagerAdapter = new MQJMainPagerAdapter(getFragmentManager());
        _viewPager.setAdapter(_viewPagerAdapter);
        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int index) {
                handlePageSelected(index);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        _tabBar = (MWTTabBar) fragmentView.findViewById(R.id.TabBar);

        Typeface typeface = MWTThemer.getInstance().getWTFont();
        _tabBar.getItemViewAt(0).setIconTextWithTypeface("\uf10d", typeface); // Alt Home Icon
        _tabBar.getItemViewAt(1).setIconTextWithTypeface("\uf10f", typeface); // Compass Icon
        _tabBar.getItemViewAt(2).setIconTextWithTypeface("\uf110", typeface); // Alt Community Icon
        //_tabBar.getItemViewAt(3).setIconTextWithTypeface("\uf10a", typeface); // Picture Icon
        _tabBar.getItemViewAt(3).setIconTextWithTypeface("\uf10e", typeface); // Alt User Icon

        _tabBar.getItemViewAt(0).setTitleText("全景");
        _tabBar.getItemViewAt(1).setTitleText("发现");
        _tabBar.getItemViewAt(2).setTitleText("圈子");
        //_tabBar.getItemViewAt(3).setTitleText("圈子");
        _tabBar.getItemViewAt(3).setTitleText("我");

        _tabBar.setOnTabSelectionEventListener(new MWTTabBar.OnTabSelectionEventListener() {
            @Override
            public boolean onWillSelectTab(int tabIndex) {
                return handleOnWillSelectTab(tabIndex);
            }

            @Override
            public void onDidSelectTab(int tabIndex) {
                handleOnDidSelectTab(tabIndex);
            }
        });

        _tabBar.limitTabNum(4);
        switchToPage(0);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (_tabBar.getSelectedTabIndex() == 3) {
            if (!MWTAuthManager.getInstance().isAuthenticated()) {
                _tabBar.setSelectedTabIndex(0);
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getActivity().getActionBar();

        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            // Too bad, the animation will be run ;(
        }
    }

    private void handlePageSelected(int index) {
        _tabBar.setSelectedTabIndex(index);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean handleOnWillSelectTab(int tabIndex) {
        if (tabIndex == 2 || tabIndex == 3|| tabIndex == 4) {
            MWTAuthManager am = MWTAuthManager.getInstance();
            if (!am.isAuthenticated()) {
                Intent intent = new Intent(getActivity(), MWTAuthSelectActivity.class);
                startActivityForResult(intent, 1);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void handleOnDidSelectTab(int tabIndex) {
        switchToPage(tabIndex);
    }

    private void switchToPage(int tabIndex) {
        // 返回箭头（默认不显示）
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(true);
        // 使左上角图标(系统)是否显示
        actionBar.setDisplayShowHomeEnabled(false);
        // 显示标题
        actionBar.setDisplayShowTitleEnabled(false);
        //显示自定义视图
        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(getActivity()).inflate(
                com.quanjing.weitu.R.layout.actionbar_layout, null);
        TextView title = (TextView) actionbarLayout.findViewById(com.quanjing.weitu.R.id.title);
        ImageView left = (ImageView) actionbarLayout.findViewById(com.quanjing.weitu.R.id.left_imbt);
        actionBar.setCustomView(actionbarLayout);

        switch (tabIndex) {
            case 0:
                setActionBarVisible(false);
                _viewPager.setCurrentItem(tabIndex, false);
                break;
            case 1:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                title.setText("发 现");
                left.setVisibility(View.GONE);
                break;
            case 2:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                title.setText("圈 子");
                left.setVisibility(View.GONE);
                break;
//            case 3:
//                setActionBarVisible(true);
//                _viewPager.setCurrentItem(tabIndex, false);
//                title.setText("圈 子");
//                left.setVisibility(View.GONE);
//                break;
            case 3:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                title.setText("我");
                IconicFontDrawable iconDrawable = new IconicFontDrawable(getActivity());
                iconDrawable.setIcon(FontAwesomeIcon.COG);
                iconDrawable.setIconColor(MWTThemer.getInstance().getActionBarForegroundColor());
                iconDrawable.setIntrinsicHeight((int) convertDP2PX(24));
                iconDrawable.setIntrinsicWidth((int) convertDP2PX(24));
                left.setImageDrawable(iconDrawable);
                left.setVisibility(View.VISIBLE);
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), MWTSettingsActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
    }

    private float convertDP2PX(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                MWTAuthManager am = MWTAuthManager.getInstance();
                if (am.isAuthenticated()) {
                    _tabBar.setSelectedTabIndex(3);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void setActionBarVisible(boolean actionBarVisible) {
        ActionBar actionBar = getActivity().getActionBar();

        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            // Too bad, the animation will be run ;(
        }

        if (actionBarVisible) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }
}
