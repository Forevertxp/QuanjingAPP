package com.quanjing.quanjing.app.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.quanjing.weitu.app.ui.circle.NewCircleFragment;
import com.quanjing.weitu.app.ui.community.MWTCommunityFragment;
import com.quanjing.weitu.app.ui.community.MWTNewCommunityFragment;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowFragment;
import com.quanjing.weitu.app.ui.found.FoundFragment;
import com.quanjing.weitu.app.ui.user.MWTUserMeFragment;

public class MQJMainPagerAdapter extends FragmentPagerAdapter {
    private MQJHomeFragment _homeSearchFragment;
    //private MWTListCategoryFlowFragment _exploreFragment;  //原始的发现页面
    //private FoundFragment _exploreFragment;  // 新的发现页面
    private FoundFragment _exploreFragment;  // 临时发现页面
    private MWTFeedFlowFragment _latestPicturesFragment;
    private MWTFeedFlowFragment _hottestPicturesFragment;
    //private MWTDualFragment _selectedFragment; // 原始的美图页面
    //private BeautyFragment _selectedFragment; //新的美图页面
    private NewCircleFragment _newCircleFragment; //美图替换为圈子
    private MWTUserMeFragment _userMeFragment;
    //private MWTCommunityFragment _communityFragemnt;
    private MWTNewCommunityFragment _communityFragemnt;

    public MQJMainPagerAdapter(FragmentManager fm) {
        super(fm);
        construct();
    }

    private void construct() {
        _homeSearchFragment = MQJHomeFragment.newInstance();
        //_exploreFragment = MWTListCategoryFlowFragment.newInstance();
        //_exploreFragment = FoundFragment.newInstance(MWTFeedManager.kWTFeedIDHome);
        _exploreFragment = new FoundFragment();
        //_latestPicturesFragment = MWTFeedFlowFragment.newInstance(MWTFeedManager.kWTFeedIDLatestUpload);
        //_hottestPicturesFragment = MWTFeedFlowFragment.newInstance(MWTFeedManager.kWTFeedIDWallpaper);
        //_selectedFragment = new MWTDualFragment("最新美图", _latestPicturesFragment, "最热美图", _hottestPicturesFragment);
        //_selectedFragment.setButtonBackgroundDrawable(R.drawable.btn_orange);

        _communityFragemnt = new MWTNewCommunityFragment();
        _newCircleFragment = new NewCircleFragment();
        _userMeFragment = new MWTUserMeFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return _homeSearchFragment;
            case 1:
                return _exploreFragment;
//            case 2:
//                return _communityFragemnt;
            case 2:
                return _newCircleFragment;
            case 3:
                return _userMeFragment;
            default:
                return null;
        }
    }

    public MWTUserMeFragment getUserMeFragment() {
        return _userMeFragment;
    }
}
