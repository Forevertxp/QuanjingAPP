package com.quanjing.weitu.app.ui.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTCategoryManager;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.common.MWTGridFlowFragment;
import com.quanjing.weitu.app.ui.common.MWTItemClickHandler;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowActivity;

public class MWTSectionedGridCategoryFlowFragment extends MWTGridFlowFragment
{
    private static final String ARG_CATEGORY_ID = "ARG_CATEGORY_ID";

    private String _categoryID;
    private MWTCategory _category;
    private MWTSectionedCategoriesAdapter _categoriesAdapter;

    public static MWTSectionedGridCategoryFlowFragment newInstance()
    {
        MWTSectionedGridCategoryFlowFragment fragment = new MWTSectionedGridCategoryFlowFragment();
        return fragment;
    }

    public static MWTSectionedGridCategoryFlowFragment newInstance(String categoryID)
    {
        MWTSectionedGridCategoryFlowFragment fragment = new MWTSectionedGridCategoryFlowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryID);
        fragment.setArguments(args);
        return fragment;
    }

    public MWTSectionedGridCategoryFlowFragment()
    {
        super();

        setPullToRefreshEnabled(true, false);

        setDataRetriver(new MWTDataRetriever()
        {
            @Override
            public void refresh(MWTCallback callback)
            {
                if (_categoriesAdapter != null)
                {
                    _categoriesAdapter.refresh(callback);
                }
                else
                {
                    if (callback != null)
                    {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(MWTCallback callback)
            {
                if (callback != null)
                {
                    callback.success();
                }
            }
        });

        setItemClickHandler(new MWTItemClickHandler()
        {
            @Override
            public boolean handleItemClick(Object item)
            {
                if (item instanceof MWTCategory)
                {
                    MWTCategory category = (MWTCategory) item;
                    if (category.isMultiLevel())
                    {
                        Intent intent = new Intent(getActivity(), MWTCategoryFlowActivity.class);
                        intent.putExtra(MWTCategoryFlowActivity.ARG_CATEGORY_ID, category.getFeedID());
                        intent.putExtra(MWTCategoryFlowActivity.ARG_IS_SINGLE_COLUMN, true);
                        startActivity(intent);
                    }
                    else
                    {
                        MWTFeed feed = MWTFeedManager.getInstance().getFeedForCategory(category);

                        Intent intent = new Intent(getActivity(), MWTFeedFlowActivity.class);
                        intent.putExtra(MWTFeedFlowActivity.ARG_FEED_ID, feed.getFeedID());
                        startActivity(intent);
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null)
        {
            _categoryID = getArguments().getString(ARG_CATEGORY_ID);
        }

        if (_categoryID != null)
        {
            MWTCategoryManager cm = MWTCategoryManager.getInstance();
            _category = cm.getCategoryByID(_categoryID);
        }
        else
        {
            _category = null;
        }

        _categoriesAdapter = new MWTDynamicCategoriesAdapter(getActivity(), _category);
        setGridViewAdapter(_categoriesAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        _categoriesAdapter.refreshIfNeeded();
    }
}
