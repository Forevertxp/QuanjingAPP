package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserAssetsInfo;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.common.MWTListAssetsAdapter;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class MWTImageFlowActivity extends MWTBase2Activity implements AdapterView.OnItemClickListener {

    private PullToRefreshStaggeredGridView _pullToRefreshStaggeredGridView;
    private StaggeredGridView _gridView;
    private MWTListAssetsAdapter _gridViewAdapter;

    private int _contentMode;
    private static int UPLOADASSET = 1;
    private static int DOWNLOADASSET = 2;
    private static int LIKEDASSET = 3;
    private static int COMMENTASSET = 4;

    private String userID;

    private static int DELETE = 0x1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtimage_flow);
        userID = getIntent().getStringExtra("userID");
        _contentMode = getIntent().getExtras().getInt("type");
        switch (_contentMode) {
            case 1:
                //getActionBar().setTitle("云相册");
                setTitleText("云相册");
                break;
            case 2:
                //getActionBar().setTitle("收藏");
                setTitleText("收藏");
                break;
            case 3:
                //getActionBar().setTitle("喜欢");
                setTitleText("喜欢");
                break;
            case 4:
                //getActionBar().setTitle("喜欢");
                setTitleText("喜欢的图片");
                break;
            case 5:
                //getActionBar().setTitle("喜欢");
                setTitleText("评论的图片");
                break;
        }
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

        _gridViewAdapter = new MWTListAssetsAdapter(this);
        _gridView.setAdapter(_gridViewAdapter);

        performRefresh();
        manualRefresh();
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    private void refreshCurrentContent(final MWTCallback callback) {
        final MWTUser user = MWTUserManager.getInstance().getUserByID(userID);
        if (user == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        switch (_contentMode) {
            case 0:
                if (callback != null) {
                    callback.success();
                }
                break;
            case 1:
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
            case 2:
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
            case 3:
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
            case 4:
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
            case 5:
                user.refreshCommentAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getCommentAssets());
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
        final MWTUser user = MWTUserManager.getInstance().getUserByID(userID);
        if (user == null) {
            if (callback != null) {
                callback.success();
            }
            return;
        }

        switch (_contentMode) {
            case 0:
                if (callback != null) {
                    callback.success();
                }
                break;
            case 1:
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
            case 2:
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
            case 3:
                user.loadMoreLikedAssets(new MWTCallback() {
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
            case 4:
                user.loadMoreLikedAssets(new MWTCallback() {
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
            case 5:
                user.loadMoreCommentAssets(new MWTCallback() {
                    @Override
                    public void success() {
                        _gridViewAdapter.setAssets(user.getAssetsInfo().getCommentAssets());
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
                Toast.makeText(MWTImageFlowActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MWTImageFlowActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                stopRefreshAnimation();
            }
        });
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
            Intent intent = new Intent(this, MWTAssetActivity.class);
            intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
            startActivityForResult(intent, DELETE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DELETE){
            performRefresh();
        }
    }
}
