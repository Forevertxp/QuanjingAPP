package com.quanjing.weitu.app.ui.category;

import android.os.Bundle;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTCategoryManager;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;

public class MWTCategoryFlowActivity extends MWTBaseSearchActivity
{
    public final static String ARG_CATEGORY_ID = "ARG_CATEGORYID";
    public final static String ARG_IS_SINGLE_COLUMN = "ARG_IS_SINGLE_COLUMN";

    private MWTStaggeredCategoryFlowFragment _categoryFlowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (savedInstanceState == null)
        {
            if (_categoryFlowFragment == null)
            {
                String categoryID = getIntent().getStringExtra(ARG_CATEGORY_ID);

                if (categoryID != null)
                {
                    MWTCategory category = MWTCategoryManager.getInstance().getCategoryByID(categoryID);
                    setTitle(category.getCategoryName());
                }
                else
                {
                    setTitle("发现");
                }

                boolean isSingleColumn = getIntent().getBooleanExtra(ARG_IS_SINGLE_COLUMN, false);

                _categoryFlowFragment = MWTStaggeredCategoryFlowFragment.newInstance(categoryID);
                _categoryFlowFragment.setIsSingleColumn(isSingleColumn);
                getFragmentManager().beginTransaction()
                                    .add(R.id.container, _categoryFlowFragment)
                                    .commit();
            }
        }
    }
}
