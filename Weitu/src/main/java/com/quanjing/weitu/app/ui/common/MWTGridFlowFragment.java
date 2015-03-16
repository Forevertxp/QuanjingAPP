package com.quanjing.weitu.app.ui.common;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTError;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

public class MWTGridFlowFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private PullToRefreshGridView _pullToRefreshStaggeredGridView;
    private StickyGridHeadersGridView _gridView;

    private BaseAdapter _gridViewAdapter;
    private MWTDataRetriever _dataRetriver;
    private MWTItemClickHandler _itemClickHandler;

    private boolean _isPullUpRefreshEnabled = false;
    private boolean _isPullDownLoadMoreEnabled = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _pullToRefreshStaggeredGridView = (PullToRefreshGridView) inflater.inflate(R.layout.fragment_grid_flow, container, false);
        _pullToRefreshStaggeredGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StickyGridHeadersGridView>()
        {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<StickyGridHeadersGridView> refreshView)
            {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<StickyGridHeadersGridView> refreshView)
            {
                performLoadMore();
            }
        });
        updateGridViewPushToRefreshMode();

        _gridView = (StickyGridHeadersGridView) _pullToRefreshStaggeredGridView.getRefreshableView();
        if (_itemClickHandler != null)
        {
            _gridView.setOnItemClickListener(this);
        }

        _gridView.setNumColumns(2);
        _gridView.setHorizontalSpacing(16);
        _gridView.setVerticalSpacing(16);
        _gridView.setPadding(16, 16, 16, 16);
        _gridView.setAreHeadersSticky(false);

        if (_gridViewAdapter != null)
        {
            _gridView.setAdapter(_gridViewAdapter);
        }

        return _pullToRefreshStaggeredGridView;
    }

    @Override
    public void onDestroyView()
    {
        _gridView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        _gridViewAdapter = null;
        super.onDestroy();
    }

    public void setPullToRefreshEnabled(boolean isPullUpRefreshEnabled, boolean isPullDownLoadMoreEnabled)
    {
        _isPullUpRefreshEnabled = isPullUpRefreshEnabled;
        _isPullDownLoadMoreEnabled = isPullDownLoadMoreEnabled;

        updateGridViewPushToRefreshMode();
    }

    public void setItemClickHandler(MWTItemClickHandler itemClickHandler)
    {
        _itemClickHandler = itemClickHandler;
        if (_gridView != null)
        {
            if (_itemClickHandler != null)
            {
                _gridView.setOnItemClickListener(this);
            }
            else
            {
                _gridView.setOnItemClickListener(null);
            }
        }
    }

    public BaseAdapter getGridViewAdapter()
    {
        return _gridViewAdapter;
    }

    public void setGridViewAdapter(BaseAdapter gridViewAdapter)
    {
        _gridViewAdapter = gridViewAdapter;
        if (_gridView != null)
        {
            _gridView.setAdapter(gridViewAdapter);
        }
    }

    public void setDataRetriver(MWTDataRetriever dataRetriver)
    {
        _dataRetriver = dataRetriver;
    }

    private void updateGridViewPushToRefreshMode()
    {
        if (_pullToRefreshStaggeredGridView == null)
        {
            return;
        }

        if (_isPullUpRefreshEnabled && _isPullDownLoadMoreEnabled)
        {
            _pullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        else if (_isPullUpRefreshEnabled)
        {
            _pullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        else if (_isPullDownLoadMoreEnabled)
        {
            _pullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }
        else
        {
            _pullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    public void manualRefresh()
    {
        startRefreshAnimation();
    }

    private void performRefresh()
    {
        if (_dataRetriver != null)
        {
            _dataRetriver.refresh(new MWTCallback()
            {
                @Override
                public void success()
                {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error)
                {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    private void performLoadMore()
    {
        if (_dataRetriver != null)
        {
            _dataRetriver.loadMore(new MWTCallback()
            {
                @Override
                public void success()
                {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error)
                {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    private void startRefreshAnimation()
    {
        if (_pullToRefreshStaggeredGridView != null)
        {
            _pullToRefreshStaggeredGridView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation()
    {
        if (_pullToRefreshStaggeredGridView != null)
        {
            _pullToRefreshStaggeredGridView.onRefreshComplete();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id)
    {
        if (_itemClickHandler != null)
        {
            _itemClickHandler.handleItemClick(_gridViewAdapter.getItem(index));
        }
    }

    protected void notifyAdapterDataChanged()
    {
        if (_gridViewAdapter != null)
        {
            _gridViewAdapter.notifyDataSetChanged();
        }
    }
}
