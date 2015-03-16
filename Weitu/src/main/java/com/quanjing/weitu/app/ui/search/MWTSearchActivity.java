package com.quanjing.weitu.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTCallback1;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.asset.MWTListAssetsAdapter;
import com.quanjing.weitu.app.ui.common.*;
import org.lcsky.SVProgressHUD;

import java.util.List;

public class MWTSearchActivity extends MWTBaseSearchActivity
{
    public final static String ARG_KEYWORD = "ARG_KEYWORD";
    public final static String ARG_ASSETIDS = "ARG_ASSETIDS";

    private MWTWaterFlowFragment _assetFlowFragment;
    private MWTListAssetsAdapter _assetsAdapter;
    private String _keyword;

    public MWTSearchActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null)
        {
            _assetFlowFragment = new MWTWaterFlowFragment();
            _assetsAdapter = new MWTListAssetsAdapter(this);
            _assetFlowFragment.setGridViewAdapter(_assetsAdapter);
            getFragmentManager().beginTransaction()
                                .add(R.id.container, _assetFlowFragment)
                                .commit();

            _assetFlowFragment.setDataRetriver(new MWTDataRetriever()
            {
                @Override
                public void loadMore(MWTCallback callback)
                {
                    loadMoreSearchResults(callback);
                }
            });
            _assetFlowFragment.setPullToRefreshEnabled(true, true);

            _assetFlowFragment.setItemClickHandler(new MWTItemClickHandler()
            {
                @Override
                public boolean handleItemClick(Object item)
                {
                    if (item instanceof MWTAsset)
                    {
                        MWTAsset asset = (MWTAsset) item;
                        Intent intent = new Intent(MWTSearchActivity.this, MWTAssetActivity.class);
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


            String[] assetIDs = getIntent().getStringArrayExtra(ARG_ASSETIDS);
            if (assetIDs != null)
            {
                List<MWTAsset> assets = MWTAssetManager.getInstance().getAssetsByIDs(assetIDs);
                _assetsAdapter.setAssets(assets);
            }

            _keyword = getIntent().getStringExtra(ARG_KEYWORD);
        }
    }

    public void performSearch(String keyword)
    {
        if (keyword.isEmpty())
        {
            return;
        }

        _keyword = keyword;

        SVProgressHUD.showInView(this, "搜索中，请稍候...", true);

        MWTAssetManager am = MWTAssetManager.getInstance();
        am.searchAssets(keyword, 0, 50, new MWTCallback1<List<MWTAsset>>()
        {
            @Override
            public void success(final List<MWTAsset> assets)
            {
                SVProgressHUD.dismiss(MWTSearchActivity.this);
                _assetsAdapter.setAssets(null);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        _assetsAdapter.setAssets(assets);
                    }
                });
            }

            @Override
            public void failure(MWTError error)
            {
                SVProgressHUD.dismiss(MWTSearchActivity.this);
                Toast.makeText(MWTSearchActivity.this, error.getMessageWithPrompt("搜索失败"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreSearchResults(final MWTCallback callback)
    {
        if ((_keyword == null) || _keyword.isEmpty())
        {
            if (callback != null)
            {
                callback.success();
            }
            return;
        }

        if (_assetsAdapter == null)
        {
            if (callback != null)
            {
                callback.success();
            }
            return;
        }

        MWTAssetManager am = MWTAssetManager.getInstance();
        am.searchAssets(_keyword, _assetsAdapter.getCount(), 50, new MWTCallback1<List<MWTAsset>>()
        {
            @Override
            public void success(List<MWTAsset> assets)
            {
                _assetsAdapter.appendAssets(assets);
                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error)
            {
                if (callback != null)
                {
                    callback.failure(error);
                }
            }
        });
    }
}
