package com.quanjing.weitu.app.ui.community;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.category.MWTCategoriesAdapter;
import com.quanjing.weitu.app.ui.category.MWTDynamicCategoriesAdapter;
import com.quanjing.weitu.app.ui.category.MWTTalentAdapter;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.photo.AlbumActivity;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.user.MWTUserInfoEditActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MWTNewCommunityFragment extends Fragment {


    private PullToRefreshListView talentListView;
    private MWTNewCommunityAdapter adapter;
    private MWTDataRetriever dataRetriver;

    private MenuItem photoMenuItem;
    private final static int MENU_PHOTO = 0x9999;

    public MWTNewCommunityFragment() {
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
                    adapter.loadmore(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_mwtnew_community, container, false);
        talentListView = (PullToRefreshListView) mMainView.findViewById(R.id.talentListView);
        talentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new MWTNewCommunityAdapter(getActivity());
        talentListView.setAdapter(adapter);
        return mMainView;
    }

    private float convertDP2PX(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        IconicFontDrawable iconDrawable = new IconicFontDrawable(getActivity());
        iconDrawable.setIcon(FontAwesomeIcon.COG);
        iconDrawable.setIconColor(MWTThemer.getInstance().getActionBarForegroundColor());
        iconDrawable.setIntrinsicHeight((int) convertDP2PX(24));
        iconDrawable.setIntrinsicWidth((int) convertDP2PX(24));

        photoMenuItem = menu.add(Menu.NONE, MENU_PHOTO, 2, "相册");
        photoMenuItem.setIcon(R.drawable.ic_add_photo).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(MWTBaseSearchActivity.MENU_SEARCH) != null)
            menu.findItem(MWTBaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_PHOTO) {
            MWTUserManager userManager = MWTUserManager.getInstance();
            if (userManager.getCurrentUser() != null && userManager.getCurrentUser().getNickname().equals("")) {
                Intent intent = new Intent(getActivity(), MWTUserInfoEditActivity.class);
                Toast.makeText(getActivity(), "请先完善个人信息", 100).show();
                intent.putExtra(MWTUserInfoEditActivity.ARG_USER_ID, userManager.getCurrentUser().getUserID());
                startActivity(intent);
                return false;
            }
            Bimp.tempSelectBitmap.clear();
            Intent intent = new Intent(getActivity(),
                    AlbumActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
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
        if (talentListView != null) {
            talentListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (talentListView != null) {
            talentListView.onRefreshComplete();
        }
    }

}

