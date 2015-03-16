package com.quanjing.weitu.app.ui.common;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTError;

public class MWTWaterFlowFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private PullToRefreshStaggeredGridView _pullToRefreshStaggeredGridView;
    private StaggeredGridView _gridView;

    private BaseAdapter _gridViewAdapter;
    private MWTDataRetriever _dataRetriver;
    private MWTItemClickHandler _itemClickHandler;

    private boolean _isPullUpRefreshEnabled = false;
    private boolean _isPullDownLoadMoreEnabled = false;

    private int _waterFlowLayoutID = R.layout.fragment_water_flow;
    private boolean _isSingleColumn = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setIsSingleColumn(boolean isSingleColumn)
    {
        _isSingleColumn = isSingleColumn;
        if (isSingleColumn)
        {
            _waterFlowLayoutID = R.layout.fragment_single_water_flow;
        }
        else
        {
            _waterFlowLayoutID = R.layout.fragment_water_flow;
        }
    }

    public boolean isSingleColumn()
    {
        return _isSingleColumn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _pullToRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) inflater.inflate(_waterFlowLayoutID, container, false);
        _pullToRefreshStaggeredGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>()
        {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView)
            {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView)
            {
                performLoadMore();
            }
        });
        updateGridViewPushToRefreshMode();

        _gridView = (StaggeredGridView) _pullToRefreshStaggeredGridView.getRefreshableView();
        if (_itemClickHandler != null)
        {
            _gridView.setOnItemClickListener(this);
        }

        return _pullToRefreshStaggeredGridView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (_gridViewAdapter != null)
        {
            _gridView.setAdapter(_gridViewAdapter);
        }
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

    public StaggeredGridView getGridView()
    {
        return _gridView;
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
            _itemClickHandler.handleItemClick(parent.getItemAtPosition(index));
        }
    }

    protected void notifyAdapterDataChanged()
    {
        if (_gridViewAdapter != null)
        {
            _gridViewAdapter.notifyDataSetChanged();
        }
    }

    public void scrollToTop()
    {
        if (_gridView != null)
        {
            _gridView.setSelection(0);
        }
    }
}
