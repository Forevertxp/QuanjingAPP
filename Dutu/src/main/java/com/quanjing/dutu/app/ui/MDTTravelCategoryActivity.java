package com.quanjing.dutu.app.ui;

import android.os.Bundle;
import com.quanjing.dutu.R;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.common.MWTDualFragment;
import com.quanjing.weitu.app.ui.category.MWTSectionedGridCategoryFlowFragment;
import com.quanjing.weitu.app.ui.category.MWTStaggeredCategoryFlowFragment;

public class MDTTravelCategoryActivity extends MWTBaseSearchActivity
{
    private MWTDualFragment _dualFragment;
    private MWTSectionedGridCategoryFlowFragment _internationalTravelFragment;
    private MWTStaggeredCategoryFlowFragment _domesticTravelFragment;

    public MDTTravelCategoryActivity()
    {
        super();
        setTitle("旅游");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_travel);
        if (savedInstanceState == null)
        {
            _internationalTravelFragment = MWTSectionedGridCategoryFlowFragment.newInstance("travel-international");
            _domesticTravelFragment = MWTStaggeredCategoryFlowFragment.newInstance("travel-domestic");
            _dualFragment = new MWTDualFragment("国外", _internationalTravelFragment,
                                                "国内", _domesticTravelFragment);
            _dualFragment.setButtonBackgroundDrawable(R.drawable.btn_blue);

            getFragmentManager().beginTransaction()
                                .add(R.id.container, _dualFragment)
                                .commit();
        }
    }
}
