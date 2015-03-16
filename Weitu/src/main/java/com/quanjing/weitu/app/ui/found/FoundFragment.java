package com.quanjing.weitu.app.ui.found;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTArticleData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment {

    private PullToRefreshListView foundListView;
    private MWTFoundAdapter adapter;
    private MWTDataRetriever dataRetriver;
    private MWTAutoSlidingPagerView _autoSlidingPagerView;


    public FoundFragment() {
        // Required empty public constructor
        super();
        this.setDataRetriver(new MWTDataRetriever() {
            @Override
            public void refresh(MWTCallback callback) {
                if (adapter != null) {
                    adapter.refresh(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(MWTCallback callback) {
                if (adapter != null) {
                    adapter.loadMore(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });
    }

    public static FoundFragment newInstance(String feedID) {
        FoundFragment fragment = new FoundFragment();
        Bundle args = new Bundle();
        args.putString(MWTFeedFlowFragment.ARG_PARAM_FEEDID, feedID);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_talent, container, false);
        foundListView = (PullToRefreshListView) view.findViewById(R.id.talentListView);
        foundListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new MWTFoundAdapter(getActivity());
        foundListView.setAdapter(adapter);
        foundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MWTArticleData articleData = (MWTArticleData) adapterView.getItemAtPosition(i-1);
                Intent intent = new Intent(getActivity(), MWTContentActivity.class);
                intent.putExtra("contentUrl", articleData.Url);
                intent.putExtra("imageUrl", articleData.CoverUrl);
                intent.putExtra("caption", articleData.Caption);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
        adapter.startAutoScroll();
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

    private void startRefreshAnimation() {
        if (foundListView != null) {
            foundListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (foundListView != null) {
            foundListView.onRefreshComplete();
        }
    }

}
