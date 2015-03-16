/**
 * 达人页面
 */
package com.quanjing.weitu.app.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.model.MWTSquareFeed;
import com.quanjing.weitu.app.model.MWTSquareFeedManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.category.MWTCategoriesAdapter;
import com.quanjing.weitu.app.ui.category.MWTDynamicCategoriesAdapter;
import com.quanjing.weitu.app.ui.category.MWTTalentAdapter;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.common.MWTItemClickHandler;
import com.quanjing.weitu.app.ui.community.square.MWTSquareAdapter;
import com.quanjing.weitu.app.ui.feed.MWTFeedAssetsAdapter;


public class SquareFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private PullToRefreshStaggeredGridView talentListView;
    private MWTFeedAssetsAdapter adapter;
    private MWTDataRetriever dataRetriver;

    private MWTSquareFeed squareFeed;
    private MWTSquareAdapter feedAssetsAdapter;
    private StaggeredGridView gridView;
    private MWTItemClickHandler itemClickHandler;

    public SquareFragment() {
        super();

        setDataRetriver(new MWTDataRetriever() {
            @Override
            public void refresh(MWTCallback callback) {
                refreshFeed(callback);
            }

            @Override
            public void loadMore(MWTCallback callback) {
                loadMoreItemsOfFeed(callback);
            }
        });

        this.setItemClickHandler(new MWTItemClickHandler() {
            @Override
            public boolean handleItemClick(Object item) {
                if (item instanceof MWTAsset) {
                    MWTAsset asset = (MWTAsset) item;
                    Intent intent = new Intent(getActivity(), MWTAssetActivity.class);
                    intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mMainView != null) {
            ViewGroup parent = (ViewGroup) mMainView.getParent();
            if (parent != null)
                parent.removeView(mMainView);
        }

        mMainView = inflater.inflate(R.layout.fragment_square, container, false);
        talentListView = (PullToRefreshStaggeredGridView) mMainView.findViewById(R.id.squareGridView);

        talentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView) {
                performLoadMore();
            }
        });

        String feedID = MWTFeedManager.kWTFeedIDWallpaper;
        MWTSquareFeedManager fm = MWTSquareFeedManager.getInstance();
        squareFeed = fm.getSquareFeed();
        feedAssetsAdapter = new MWTSquareAdapter(getActivity(), squareFeed);
        gridView = (StaggeredGridView) talentListView.getRefreshableView();
        gridView.setAdapter(feedAssetsAdapter);
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (squareFeed.getItemNum() == 0) {
            refreshFeed(new MWTCallback() {
                @Override
                public void success() {

                }

                @Override
                public void failure(MWTError error) {

                }
            });
        }
    }

    private void performRefresh() {
        if (dataRetriver != null) {
            dataRetriver.refresh(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    private void performLoadMore() {
        if (dataRetriver != null) {
            dataRetriver.loadMore(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(MWTDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    public void setItemClickHandler(MWTItemClickHandler itemClickHandler) {
        this.itemClickHandler = itemClickHandler;
        if (gridView != null) {
            if (itemClickHandler != null) {
                gridView.setOnItemClickListener(this);
            } else {
                gridView.setOnItemClickListener(null);
            }
        }
    }

    private void startRefreshAnimation() {
        if (talentListView != null) {
            talentListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (talentListView != null) {
            talentListView.onRefreshComplete();
        }
    }

    public void refreshFeed(final MWTCallback callback) {
        if (squareFeed == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        squareFeed.refresh(new MWTCallback() {
            @Override
            public void success() {
                feedAssetsAdapter.notifyDataSetChanged();
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error) {
                feedAssetsAdapter.notifyDataSetChanged();
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public void loadMoreItemsOfFeed(final MWTCallback callback) {
        if (squareFeed == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        squareFeed.loadMore(new MWTCallback() {
            @Override
            public void success() {
                feedAssetsAdapter.notifyDataSetChanged();
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error) {
                feedAssetsAdapter.notifyDataSetChanged();
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if (itemClickHandler != null) {
            itemClickHandler.handleItemClick(parent.getItemAtPosition(index));
        }
    }

}
