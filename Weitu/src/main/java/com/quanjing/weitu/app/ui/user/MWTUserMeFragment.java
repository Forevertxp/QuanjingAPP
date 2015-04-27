package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.etsy.android.grid.StaggeredGridView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserAssetsInfo;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.common.MWTPageFragment;
import com.quanjing.weitu.app.ui.found.ImagePagerActivity;
import com.quanjing.weitu.app.ui.photo.AlbumActivity;
import com.quanjing.weitu.app.ui.photo.AlbumHelper;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageBucket;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PictureUtil;

import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MWTUserMeFragment extends MWTPageFragment implements AdapterView.OnItemClickListener {
    private final static int MENU_PHOTO = 0x9999;
    private final static int EDIT_USER_INFO = 0x123123;

    private final static int EDIT_IMAGE = 0x8888;

    private MWTUserHeaderView _headerView;
    private EContentMode _contentMode = EContentMode.nContentModeNone;

    private MenuItem photoMenuItem;

    //显示手机里的所有图片的列表控件
    private HeaderGridView gridView;
    //gridView的adapter
    private LocalAlbumAdapter gridImageAdapter;
    private Intent intent;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;

    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    // private PullToRefreshScrollView scrollView;

    String localTempImgDir = "com.quanjing";
    String localTempImgFileName = "quanjing_temp.jpg";

    private static boolean isVisible = false;

    public MWTUserMeFragment() {
        // Required empty public constructor
    }

    public static MWTUserMeFragment newInstance() {
        MWTUserMeFragment fragment = new MWTUserMeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_me, container, false);

//        scrollView = (PullToRefreshScrollView) view.findViewById(R.id.mainScollView);
//        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
//
//            @Override
//            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                reloadFromUser();
//            }
//        });


        gridView = (HeaderGridView) view.findViewById(R.id.locaImageGrid);
        _headerView = new MWTUserHeaderView(this);
        gridView.addHeaderView(_headerView);
        initPop();
        init(); //本地相册
        IntentFilter filter = new IntentFilter("picture.broadcast.delete");
        getActivity().registerReceiver(broadcastReceiver, filter);
        return view;
    }


    // 初始化，给一些对象赋值
    private void init() {
        AlbumHelper albumHelper = new AlbumHelper();
        helper = albumHelper.getHelper();
        helper.init(getActivity().getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();

        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }

        DateHighToLowComparator comparator = new DateHighToLowComparator();
        Collections.sort(dataList, comparator);

        gridImageAdapter = new LocalAlbumAdapter(getActivity(), dataList, gridView);
        gridView.setAdapter(gridImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> imgList = new ArrayList<String>();
                ArrayList<String> webList = new ArrayList<String>();
                ArrayList<String> captionList = new ArrayList<String>();
                Intent intent = new Intent(getActivity(), LocalImageBrowerActivity.class);
                LocalImageBrowerActivity.imageItems = dataList;
                intent.putExtra(LocalImageBrowerActivity.EXTRA_IMAGE_INDEX, i - 4);
                intent.putExtra(LocalImageBrowerActivity.FROM_TYPE, 1);
                startActivityForResult(intent, EDIT_IMAGE);
            }
        });
    }

    public class DateHighToLowComparator implements Comparator<ImageItem> {

        @Override
        public int compare(ImageItem itemBean1, ImageItem itemBean2) {

            long date1 = itemBean1.getImageDate();
            long date2 = itemBean2.getImageDate();

            if (date1 > date2) {
                return -1;
            } else if (date1 < date2) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    public void initPop() {

        pop = new PopupWindow(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                Intent intent = new Intent(getActivity(),
                        LocalAlbumActivity.class);
                intent.putExtra("title","发布图片");
                startActivity(intent);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 默认是压缩后的图，创建原图，如果不加下面两行代码，则在onActivityResult可直接通过(Bitmap) data.getExtras().get("data"); 获得压缩后的bitmap
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                + "/" + localTempImgDir + "/" + localTempImgFileName));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
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
            View parentView = getActivity().getLayoutInflater().inflate(R.layout.activity_mwtupload_pic, null);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
            pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            _headerView.setFocusable(true);
            _headerView.setFocusableInTouchMode(true);
            isVisible = true;
            reloadFromUser();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFromUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public void reloadFromUser() {
        if (!MWTAuthManager.getInstance().isAuthenticated()) {
            _headerView.setUser(null);
            syncGridViewAdapterData();
            return;
        }

        MWTUser user = MWTUserManager.getInstance().getCurrentUser();

        if (user == null) {
            if (isVisible)
                SVProgressHUD.showInView(getActivity(), "获取用户信息中...", true);
            MWTUserManager.getInstance().refreshCurrentUserInfo(new MWTCallback() {
                @Override
                public void success() {
                    SVProgressHUD.dismiss(getActivity());
                    //scrollView.onRefreshComplete();
                    reloadFromUser();
                }

                @Override
                public void failure(MWTError error) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), error.getMessageWithPrompt("获取用户信息失败"), 3.f);
                    // scrollView.onRefreshComplete();
                }
            });
        } else if (user.checkAndCleanDataDirty()) {
            _headerView.setUser(user);
            syncGridViewAdapterData();

            if (isVisible)
                SVProgressHUD.showInView(getActivity(), "获取用户信息中...", true);
            MWTUserManager.getInstance().refreshCurrentUserInfo(new MWTCallback() {
                @Override
                public void success() {
                    SVProgressHUD.dismiss(getActivity());
                    //scrollView.onRefreshComplete();
                    reloadFromUser();
                }

                @Override
                public void failure(MWTError error) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), error.getMessageWithPrompt("获取用户信息失败"), 3.f);
                    // scrollView.onRefreshComplete();
                }
            });
        } else {
            _headerView.setUser(user);
            syncGridViewAdapterData();
            // scrollView.onRefreshComplete();
        }
    }

    protected void onEditUserInfo() {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), MWTUserInfoEditActivity.class);
            intent.putExtra(MWTUserInfoEditActivity.ARG_USER_ID, user.getUserID());
            startActivityForResult(intent, EDIT_USER_INFO);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Integer> positionList = intent.getIntegerArrayListExtra("positionList");
            updateLocaImage(positionList);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_USER_INFO && resultCode == Activity.RESULT_OK) {
            reloadFromUser();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            final String path = Environment.getExternalStorageDirectory()
                    + "/" + localTempImgDir + "/" + localTempImgFileName;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(path);
                    takePhoto.setBitmap(PictureUtil.getSmallBitmap(path));
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
            });
            thread.start();

            Intent intent = new Intent(getActivity(), MWTUploadPicActivity.class);
            startActivity(intent);
        }

        if (requestCode == EDIT_IMAGE && resultCode == Activity.RESULT_OK) {
            ArrayList<Integer> positionList = data.getIntegerArrayListExtra("positionList");
            updateLocaImage(positionList);
        }
    }

    private void updateLocaImage(ArrayList<Integer> positionList) {
        if (positionList.size() > 0) {
            for (int postion : positionList) {
                dataList.remove(postion);
            }
            gridImageAdapter.notifyDataSetChanged();
            _headerView.updateLocalImageCount(positionList.size());
        }
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

    protected void onCollectedButtonClicked() {
        switchContentMode(EContentMode.nContentModeCollectedAssets);
    }

    protected void onLocalButtonClicked() {
        switchContentMode(EContentMode.nContentModeLocalAssets);
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
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        List<MWTAsset> assets = null;
        MWTUserAssetsInfo assetsInfo = user.getAssetsInfo();
        if (assetsInfo == null) {
            if (isVisible)
                SVProgressHUD.showInView(getActivity(), "获取用户数据中...", true);
            user.refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    SVProgressHUD.dismiss(getActivity());
                    syncGridViewAdapterData();
                }

                @Override
                public void failure(MWTError error) {
                }
            });
        } else {
            switch (_contentMode) {
                case nContentModeNone:
                    return;
                case nContentModeLocalAssets:
                    break;
                case nContentModeUploadedAssets:
                    assets = assetsInfo.getUploadedAssets();
                    break;
                case nContentModeCollectedAssets:
                    assets = assetsInfo.getCollectedAssets();
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

            if (assets == null) {
                manualRefresh();
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
                Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                stopRefreshAnimation();
            }
        });
    }


    private void refreshCurrentContent(final MWTCallback callback) {
        final MWTUser user = MWTUserManager.getInstance().getCurrentUser();
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
            case nContentModeCollectedAssets:
                user.refreshUploadedAssets(new MWTCallback() {
                    @Override
                    public void success() {
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
    }

    private void stopRefreshAnimation() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MWTAsset asset = (MWTAsset) parent.getItemAtPosition(position);
        if (asset != null) {
            Intent intent = new Intent(getActivity(), MWTAssetActivity.class);
            intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
            startActivity(intent);
        }
    }

    private enum EContentMode {
        nContentModeNone,
        nContentModeLocalAssets,
        nContentModeCollectedAssets,
        nContentModeLikedAssets,
        nContentModeDownloadedAssets,
        nContentModeSharedAssets,
        nContentModeUploadedAssets
    }


}
