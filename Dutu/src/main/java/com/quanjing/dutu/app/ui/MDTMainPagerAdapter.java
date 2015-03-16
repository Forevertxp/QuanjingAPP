package com.quanjing.dutu.app.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTCategoryManager;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.ui.category.MWTStaggeredCategoryFlowFragment;
import com.quanjing.weitu.app.ui.common.MWTItemClickHandler;
import com.quanjing.weitu.app.ui.user.MWTUserMeFragment;

public class MDTMainPagerAdapter extends FragmentPagerAdapter
{
    private MDTHomeFragment _homeFragment;
    private MWTStaggeredCategoryFlowFragment _lifeFragment;
    private MWTStaggeredCategoryFlowFragment _wikiFragment;
    private MWTStaggeredCategoryFlowFragment _appFragment;
    private MWTUserMeFragment _userMeFragment;

    public MDTMainPagerAdapter(FragmentManager fm)
    {
        super(fm);
        construct();
    }

    private void construct()
    {
        MWTCategory lifeCategory = MWTCategoryManager.getInstance().createMultiLevelCategory("life", "生活");
        MWTCategory wikiCategory = MWTCategoryManager.getInstance().createMultiLevelCategory("wiki", "百科");
        MWTCategory appCategory = MWTCategoryManager.getInstance().createMultiLevelCategory("app", "应用");

        _homeFragment = MDTHomeFragment.newInstance(MWTFeedManager.kWTFeedIDHome);
        _lifeFragment = MWTStaggeredCategoryFlowFragment.newInstance(lifeCategory.getCategoryID());
        _lifeFragment.setIsSingleColumn(true);

        _lifeFragment.setExtraItemClickHandler(new MWTItemClickHandler()
        {
            @Override
            public boolean handleItemClick(Object item)
            {
                if (item instanceof MWTCategory)
                {
                    MWTCategory category = (MWTCategory) item;
                    if (category.getCategoryName().equalsIgnoreCase("旅游"))
                    {
                        Intent intent = new Intent(_lifeFragment.getActivity(), MDTTravelCategoryActivity.class);
                        _lifeFragment.startActivity(intent);
                        return true;
                    }
                }

                return false;
            }
        });

        _wikiFragment = MWTStaggeredCategoryFlowFragment.newInstance(wikiCategory.getCategoryID());
        _wikiFragment.setIsSingleColumn(true);
        _appFragment = MWTStaggeredCategoryFlowFragment.newInstance(appCategory.getCategoryID());

        _userMeFragment = MWTUserMeFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return 5;
    }

    @Override
    public Fragment getItem(int index)
    {
        switch (index)
        {
            case 0:
                return _homeFragment;
            case 1:
                return _lifeFragment;
            case 2:
                return _wikiFragment;
            case 3:
                return _appFragment;
            case 4:
                return _userMeFragment;
            default:
                return null;
        }
    }
}
