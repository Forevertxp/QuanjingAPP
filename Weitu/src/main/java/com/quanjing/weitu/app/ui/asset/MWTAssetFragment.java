package com.quanjing.weitu.app.ui.asset;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.etsy.android.grid.StaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTListAssetsAdapter;

public class MWTAssetFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_PARAM_ASSETID = "ARG_PARAM_ASSETID";

    private String _assetID;
    private MWTAsset _asset;

    private StaggeredGridView _gridView;
    private MWTAssetHeaderView _headerView;

    private MWTListAssetsAdapter _gridViewAdapter;

    private static boolean needToRefresh = true;

    public MWTAssetFragment() {
    }

    public static MWTAssetFragment newInstance(String assetID) {
        MWTAssetFragment fragment = new MWTAssetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ASSETID, assetID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            _assetID = getArguments().getString(ARG_PARAM_ASSETID);

            MWTAssetManager am = MWTAssetManager.getInstance();
            _asset = am.getAssetByID(_assetID);
            // 处理每日一图
            if (_asset == null) {
                _asset = new MWTAsset();
                _asset.setAssetID(_assetID);
                MWTImageInfo imageInfo = new MWTImageInfo();
                SharedPreferences sharedata = getActivity().getSharedPreferences("data", 0);
                imageInfo.url = sharedata.getString("dailyImageUrl", "");
                imageInfo.width = 640;
                imageInfo.height = (int) (640 * 0.6);
                _asset.setImageInfo(imageInfo);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset, container, false);

        _headerView = new MWTAssetHeaderView(getActivity());
        _headerView.setAsset(_asset);

        _gridView = (StaggeredGridView) view.findViewById(R.id.GridView);
        _gridView.setOnItemClickListener(this);
        _gridView.addHeaderView(_headerView);
        _gridView.setAdapter(_gridViewAdapter);

        if (_asset.getRelatedAssets() != null) {
            _gridViewAdapter.setAssets(_asset.getRelatedAssets());
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        _gridViewAdapter = new MWTListAssetsAdapter(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _gridViewAdapter = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
        if (_asset.getRelatedAssets() == null) {
            needToRefresh = true;
        } else {
            needToRefresh = false;
        }
    }

    private void refresh() {
        _asset.queryRelatedAssets(new MWTCallback() {
            @Override
            public void success() {
                if (_gridViewAdapter != null) {
                    // 只有第一次才加载图片，以后只要存在怎不自动刷新
                    if (needToRefresh) {
                        _gridViewAdapter.setAssets(_asset.getRelatedAssets());
                    }
                    _headerView.setAsset(_asset);
                }
            }

            @Override
            public void failure(MWTError error) {
                // TODO show error dialog
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        MWTAsset asset = (MWTAsset) parent.getItemAtPosition(index);
        if (asset != null) {
            Intent intent = new Intent(getActivity(), MWTAssetActivity.class);
            intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
            startActivity(intent);
        }
    }

    private View createHeaderView() {
        ViewGroup container = (ViewGroup) this.getView();
        MWTAssetHeaderView headerView = (MWTAssetHeaderView) getActivity().getLayoutInflater().inflate(R.layout.view_asset_header, container, false);
        return headerView;
    }
}
