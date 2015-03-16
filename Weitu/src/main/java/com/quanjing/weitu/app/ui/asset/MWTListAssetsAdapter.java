package com.quanjing.weitu.app.ui.asset;

import android.content.Context;
import com.quanjing.weitu.app.model.MWTAsset;

import java.util.List;

public class MWTListAssetsAdapter extends MWTAssetsAdapter
{
    private List<MWTAsset> _assets;

    public MWTListAssetsAdapter(Context context)
    {
        super(context);
    }

    @Override
    public int getAssetCount()
    {
        if (_assets != null)
        {
            return _assets.size();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public MWTAsset getAsset(int index)
    {
        if (_assets != null)
        {
            return _assets.get(index);
        }
        else
        {
            return null;
        }
    }

    public void setAssets(List<MWTAsset> assets)
    {
        _assets = assets;
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    public void appendAssets(List<MWTAsset> assets)
    {
        if (assets == null)
        {
            return;
        }

        if (_assets == null)
        {
            _assets = assets;
        }
        else
        {
            _assets.addAll(assets);
        }
        notifyDataSetChanged();
    }
}
