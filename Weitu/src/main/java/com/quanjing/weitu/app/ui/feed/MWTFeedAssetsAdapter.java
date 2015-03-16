package com.quanjing.weitu.app.ui.feed;

import android.content.Context;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.ui.asset.MWTAssetsAdapter;

public class MWTFeedAssetsAdapter extends MWTAssetsAdapter
{
    private MWTFeed _feed;

    public MWTFeedAssetsAdapter(Context context, MWTFeed feed)
    {
        super(context);
        _feed = feed;
    }

    public MWTFeed getFeed()
    {
        return _feed;
    }

    public void setFeed(MWTFeed feed)
    {
        if (feed != _feed)
        {
            _feed = feed;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getAssetCount()
    {
        if (_feed != null)
        {
            return _feed.getItemNum();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public MWTAsset getAsset(int index)
    {
        if (_feed != null)
        {
            return _feed.getAsset(index);
        }
        else
        {
            return null;
        }
    }
}
