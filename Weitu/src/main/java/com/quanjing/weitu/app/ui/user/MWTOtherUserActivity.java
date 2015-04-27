package com.quanjing.weitu.app.ui.user;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserAssetsInfo;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.common.MWTListAssetsAdapter;
import com.quanjing.weitu.app.ui.common.MWTPageFragment;
import com.quanjing.weitu.app.ui.photo.AlbumHelper;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageBucket;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PublicWay;
import com.quanjing.weitu.app.ui.settings.MWTSettingsActivity;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MWTOtherUserActivity extends MWTBase2Activity implements AdapterView.OnItemClickListener {

    private final static int MENU_PHOTO = 0x9999;
    private final static int EDIT_USER_INFO = 0x123123;

    private PullToRefreshStaggeredGridView _pullToRefreshStaggeredGridView;
    private StaggeredGridView _gridView;
    private MWTOtherUserHeader _headerView;
    private MWTListAssetsAdapter _gridViewAdapter;
    private EContentMode _contentMode = EContentMode.nContentModeNone;

    private String otherUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtother_user);
        setTitleText("用户信息");
        _pullToRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) findViewById(R.id.GridView);
        _pullToRefreshStaggeredGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<StaggeredGridView> refreshView) {
                performLoadMore();
            }
        });
        _pullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.BOTH);

        _gridView = (StaggeredGridView) _pullToRefreshStaggeredGridView.getRefreshableView();
        _gridView.setOnItemClickListener(this);

        _headerView = new MWTOtherUserHeader(MWTOtherUserActivity.this);
        _gridView.addHeaderView(_headerView);

        _gridViewAdapter = new MWTListAssetsAdapter(this);
        _gridView.setAdapter(_gridViewAdapter);

        otherUserID = getIntent().getStringExtra("userID");

        // 初始化数据
        onUploadedButtonClicked();
    }


    protected void onSharedButtonClicked() {
        switchContentMode(EContentMode.nContentModeSharedAssets);
    }

    protected void onDownloadedButtonClicked() {
        switchContentMode(EContentMode.nContentModeDownloadedAssets);
    }

    protected void onLikedButtonClicked() {
        switchContentMode(EContentMode.nContentModeLikedAssets);
    }

    protected void onUploadedButtonClicked() {
        switchContentMode(EContentMode.nContentModeUploadedAssets);
    }

    private void switchContentMode(EContentMode contentMode) {
        if (contentMode == _contentMode) {
            return;
        }

        _contentMode = contentMode;

        syncGridViewAdapterData();
    }

    private void syncGridViewAdapterData() {
        MWTUser user = MWTUserManager.getInstance().getUserByID(otherUserID);
        if (user == null) {
            MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
            userService.queryUserPublicInfo(otherUserID, new Callback<MWTUserResult>() {
                @Override
                public void success(MWTUserResult result, Response response) {
                    if (result!=null){
                        MWTUserManager.getInstance().registerUserData(result.user);
                        syncGridViewAdapterData();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
            return;
        }

        List<MWTAsset> assets = null;
        MWTUserAssetsInfo assetsInfo = user.getAssetsInfo();
        if (assetsInfo == null) {
            SVProgressHUD.showInView(MWTOtherUserActivity.this, "获取用户数据中...", true);
            user.refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    SVProgressHUD.dismiss(MWTOtherUserActivity.this);
                    syncGridViewAdapterData();
                }

                @Override
                public void failure(MWTError error) {
                    _gridViewAdapter.setAssets(null);
                }
            });
        } else {
            _headerView.setUser(user);
            switch (_contentMode) {
                case nContentModeNone:
                    _gridViewAdapter.setAssets(null);
                    return;
                case nContentModeUploadedAssets:
                    assets = assetsInfo.getUploadedAssets();
                    break;
                case nContentModeLikedAssets:
                    assets = assetsInfo.getLikedAssets();
                    break;
                case nContentModeDownloadedAssets:
                    assets = assetsInfo.getDownloadedAssets();
                    break;
                case nContentModeSharedAssets:
                    assets = assetsInfo.getSharedAssets();
                    break;
                default:
                    break;
            }

            _gridViewAdapter.setAssets(assets);
            if (assets == null) {
                //manualRefresh(); //部分手机不生效，无法调用performRefresh()方法
                performRefresh();
            }
        }
    }

    public void manualRefresh() {
        startRefreshAnimation();
    }

    private void performRefresh() {
        refreshCurrentContent(new MWTCallback() {
            @Override
            public void success() {
                stopRefreshAnimation();
            }

            @Override
            public void failure(MWTError error) {
                Toast.makeText(MWTOtherUserActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                stopRefreshAnimation();
            }
        });
    }

    private void performLoadMore() {
        loadMoreForCurrentContent(new MWTCallback() {
            @Override
            public void success() {
                stopRefreshAnimation();
            }

            @Override
            public void failure(MWTError error) {
                Toast.makeText(MWTOtherUserActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                stopRefreshAnimation();
            }
        });
    }

    private void refreshCurrentContent(final MWTCallback callback) {
        final MWTUser user = MWTUserManager.getInstance().getUserByID(otherUserID);
        if (user == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        switch (_contentMode) {
            case nContentModeNone:
                if (callback != null) {
                    callback.success();
                }
                break;
            case nContentModeUploadedAssets:
                user.refreshUploadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getUploadedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeLikedAssets:
                user.refreshLikedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getLikedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeDownloadedAssets:
                user.refreshDownloadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getDownloadedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeSharedAssets:
                user.refreshSharedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getSharedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            default:
                if (callback != null) {
                    callback.success();
                }
                break;
        }
    }

    private void loadMoreForCurrentContent(final MWTCallback callback) {
        final MWTUser user = MWTUserManager.getInstance().getUserByID(otherUserID);
        if (user == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        switch (_contentMode) {
            case nContentModeNone:
                if (callback != null) {
                    callback.success();
                }
                break;
            case nContentModeUploadedAssets:
                user.loadMoreUploadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getUploadedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeLikedAssets:
                user.loadMoreDownloadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getLikedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeDownloadedAssets:
                user.loadMoreDownloadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getDownloadedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            case nContentModeSharedAssets:
                user.loadMoreSharedAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getSharedAssets());
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(MWTError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
                break;
            default:
                if (callback != null) {
                    callback.success();
                }
                break;
        }
    }

    private void startRefreshAnimation() {
        if (_pullToRefreshStaggeredGridView != null) {
            _pullToRefreshStaggeredGridView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (_pullToRefreshStaggeredGridView != null) {
            _pullToRefreshStaggeredGridView.onRefreshComplete();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MWTAsset asset = (MWTAsset) parent.getItemAtPosition(position);
        if (asset != null) {
            Intent intent = new Intent(MWTOtherUserActivity.this, MWTAssetActivity.class);
            intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
            startActivity(intent);
        }
    }

    private enum EContentMode {
        nContentModeNone,
        nContentModeUploadedAssets,
        nContentModeLikedAssets,
        nContentModeDownloadedAssets,
        nContentModeSharedAssets
    }
}
