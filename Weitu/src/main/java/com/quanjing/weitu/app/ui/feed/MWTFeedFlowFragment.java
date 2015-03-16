package com.quanjing.weitu.app.ui.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.common.MWTItemClickHandler;
import com.quanjing.weitu.app.ui.common.MWTWaterFlowFragment;

public class MWTFeedFlowFragment extends MWTWaterFlowFragment
{
    public static final String ARG_PARAM_FEEDID = "ARG_PARAM_FEEDID";

    private MWTFeed _feed;
    private MWTFeedAssetsAdapter _feedAssetsAdapter;

    public MWTFeedFlowFragment()
    {
        super();

        setPullToRefreshEnabled(true, true);

        setDataRetriver(new MWTDataRetriever()
        {
            @Override
            public void refresh(MWTCallback callback)
            {
                refreshFeed(callback);
            }

            @Override
            public void loadMore(MWTCallback callback)
            {
                loadMoreItemsOfFeed(callback);
            }
        });

        setItemClickHandler(new MWTItemClickHandler()
        {
            @Override
            public boolean handleItemClick(Object item)
            {
                if (item instanceof MWTAsset)
                {
                    MWTAsset asset = (MWTAsset) item;
                    Intent intent = new Intent(getActivity(), MWTAssetActivity.class);
                    intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                    startActivity(intent);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

    public static MWTFeedFlowFragment newInstance(String feedID)
    {
        MWTFeedFlowFragment fragment = new MWTFeedFlowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_FEEDID, feedID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            String feedID = getArguments().getString(ARG_PARAM_FEEDID);
            MWTFeedManager fm = MWTFeedManager.getInstance();
            _feed = fm.getFeedWithID(feedID);
            _feedAssetsAdapter = new MWTFeedAssetsAdapter(getActivity(), _feed);
            setGridViewAdapter(_feedAssetsAdapter);
        }
        else
        {
            assert false;
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (_feed.getItemNum() == 0)
        {
            refreshFeed(new MWTCallback()
            {
                @Override
                public void success()
                {

                }

                @Override
                public void failure(MWTError error)
                {

                }
            });
        }
    }

    public void refreshFeed(final MWTCallback callback)
    {
        if (_feed == null)
        {
            if (callback != null)
            {
                callback.success();
            }
            return;
        }

        _feed.refresh(new MWTCallback()
        {
            @Override
            public void success()
            {
                notifyAdapterDataChanged();
                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error)
            {
                notifyAdapterDataChanged();
                if (callback != null)
                {
                    callback.failure(error);
                }
            }
        });
    }

    public void loadMoreItemsOfFeed(final MWTCallback callback)
    {
        if (_feed == null)
        {
            if (callback != null)
            {
                callback.success();
            }
            return;
        }

        _feed.loadMore(new MWTCallback()
        {
            @Override
            public void success()
            {
                notifyAdapterDataChanged();
                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error)
            {
                notifyAdapterDataChanged();
                if (callback != null)
                {
                    callback.failure(error);
                }
            }
        });
    }
}
