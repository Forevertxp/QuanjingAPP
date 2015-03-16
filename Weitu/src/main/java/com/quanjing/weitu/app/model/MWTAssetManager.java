package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.common.MWTCallback1;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTAssetsResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MWTAssetManager
{
    private static MWTAssetManager s_instance;
    private HashMap<String, MWTAsset> _assetsByID = new HashMap<>();
    private MWTAssetService _assetService;
    private RetrofitError retrofitError;

    private MWTAssetManager()
    {
        _assetService = MWTRestManager.getInstance().create(MWTAssetService.class);
    }

    public static MWTAssetManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTAssetManager();
        }

        return s_instance;
    }

    public List<MWTAsset> registerAssetDatas(List<MWTAssetData> assetDatas)
    {
        ArrayList<MWTAsset> assets = new ArrayList<>();
        for (MWTAssetData assetData : assetDatas)
        {
            MWTAsset asset = registerAssetData(assetData);
            assets.add(asset);
        }
        return assets;
    }

    public MWTAsset registerAssetData(MWTAssetData assetData)
    {
        if (assetData == null)
        {
            return null;
        }

        String assetID = assetData.assetID;
        if (assetID == null)
        {
            return null;
        }

        MWTAsset asset = _assetsByID.get(assetID);
        if (asset == null)
        {
            asset = new MWTAsset();
            _assetsByID.put(assetID, asset);
        }

        asset.mergeWithData(assetData);

        return asset;
    }

    public MWTAsset getAssetByID(String assetID)
    {
        return _assetsByID.get(assetID);
    }

    public MWTAssetService getAssetService()
    {
        return _assetService;
    }

    public void searchAssets(final String keyword, final int startIndex, int count, final MWTCallback1<List<MWTAsset>> callback)
    {
        getAssetService().search(keyword,
                                 startIndex,
                                 count,
                                 new Callback<MWTAssetsResult>()
                                 {

                                     @Override
                                     public void success(MWTAssetsResult result, Response response)
                                     {
                                         if (result == null)
                                         {
                                             if (callback != null)
                                             {
                                                 callback.failure(new MWTError(-1, "服务器返回错误"));
                                             }
                                             return;
                                         }

                                         if (result.error != null)
                                         {
                                             if (callback != null)
                                             {
                                                 callback.failure(result.error);
                                             }
                                             return;
                                         }

                                         if (result.assets == null)
                                         {
                                             if (callback != null)
                                             {
                                                 if (startIndex == 0)
                                                 {
                                                     callback.failure(new MWTError(1, "没有结果"));
                                                 }
                                                 else
                                                 {
                                                     callback.failure(new MWTError(1, "没有更多数据"));
                                                 }
                                             }
                                             return;
                                         }

                                         List<MWTAsset> assets = registerAssetDatas(result.assets);
                                         if (callback != null)
                                         {
                                             if (callback == null)
                                             {

                                             }
                                             callback.success(assets);
                                         }
                                     }

                                     @Override
                                     public void failure(RetrofitError retrofitError)
                                     {
                                         if (callback != null)
                                         {
                                             callback.failure(new MWTError(retrofitError));
                                         }
                                     }
                                 });
    }

    public List<MWTAsset> getAssetsByIDs(String[] assetIDs)
    {
        ArrayList<MWTAsset> assets = new ArrayList<>();

        if (assetIDs != null)
        {
            assets.ensureCapacity(assetIDs.length);
            for (String assetID : assetIDs)
            {
                MWTAsset asset = getAssetByID(assetID);
                if (asset != null)
                {
                    assets.add(asset);
                }
            }
        }

        return assets;
    }
}
