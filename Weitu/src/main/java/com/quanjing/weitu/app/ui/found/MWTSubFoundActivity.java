package com.quanjing.weitu.app.ui.found;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowFragment;

public class MWTSubFoundActivity extends MWTBase2Activity {

    private PullToRefreshListView foundListView;
    private MWTSubFoundAdapter adapter;
    private MWTDataRetriever dataRetriver;
    private MWTAutoSlidingPagerView _autoSlidingPagerView;

    private int subType;

    final public static int TRAVEL = 1;
    final public static int CAR = 2;
    final public static int LIFE = 3;
    final public static int FOOD = 4;
    final public static int FASHION = 5;
    final public static int ENCYCLOPEDIA = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtsub_found);


        subType = getIntent().getExtras().getInt("type");
        switch (subType) {
            case TRAVEL:
                setTitleText("旅游");
                break;
            case CAR:
                setTitleText("家居");
                break;
            case LIFE:
                setTitleText("汽车");
                break;
            case FOOD:
                setTitleText("美食");
                break;
            case FASHION:
                setTitleText("时尚");
                break;
            case ENCYCLOPEDIA:
                setTitleText("百科");
                break;
        }

        this.setDataRetriver(new MWTDataRetriever() {
            @Override
            public void refresh(MWTCallback callback) {
                if (adapter != null) {
                    adapter.refresh(subType, callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(MWTCallback callback) {
                if (adapter != null) {
                    adapter.loadMore(subType, callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });

        foundListView = (PullToRefreshListView) findViewById(R.id.subListView);
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
        adapter = new MWTSubFoundAdapter(this);
        foundListView.setAdapter(adapter);
        foundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MWTArticleData articleData = (MWTArticleData) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MWTSubFoundActivity.this, MWTContentActivity.class);
                intent.putExtra("contentUrl", articleData.Url);
                intent.putExtra("imageUrl", articleData.CoverUrl);
                intent.putExtra("caption", articleData.Caption);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        adapter.stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded(subType);
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
                    Toast.makeText(MWTSubFoundActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MWTSubFoundActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
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
