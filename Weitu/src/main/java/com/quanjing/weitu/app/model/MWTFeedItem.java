package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTFeedItemData;

public class MWTFeedItem implements Comparable<MWTFeedItem>
{
    private String _itemID;
    private long _timestamp;
    private MWTAsset _asset;

    public String getItemID()
    {
        return _itemID;
    }

    public long getTimestamp()
    {
        return _timestamp;
    }

    public MWTAsset getAsset()
    {
        return _asset;
    }

    public void mergeWithData(MWTFeedItemData itemData)
    {
        if (itemData == null)
        {
            return;
        }

        if (itemData != null)
        {
            _itemID = itemData.itemID;
        }

        if (itemData.timestamp != null)
        {
            _timestamp = itemData.timestamp;
        }

        if (itemData.asset != null)
        {
            _asset = MWTAssetManager.getInstance().registerAssetData(itemData.asset);
        }
    }

    @Override
    public int compareTo(MWTFeedItem rhs)
    {
        // Newest item comes first, i.e. item with bigger timestamp comes first

        if (rhs._timestamp < _timestamp)
        {
            return -1;
        }
        else if (_timestamp == rhs._timestamp)
        {
            return rhs._itemID.compareTo(_itemID);
        }
        else
        {
            return 1;
        }
    }
}
