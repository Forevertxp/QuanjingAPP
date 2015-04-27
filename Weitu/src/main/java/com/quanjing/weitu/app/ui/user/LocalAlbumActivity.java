package com.quanjing.weitu.app.ui.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.photo.AlbumHelper;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageBucket;
import com.quanjing.weitu.app.ui.photo.ImageFileActivity;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PublicWay;
import com.quanjing.weitu.app.ui.user.MWTUploadPicActivity;


/**
 * 本地相册
 */
public class LocalAlbumActivity extends MWTBase2Activity {
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private LocalAlbumGridViewAdapter gridImageAdapter;
    private Intent intent;
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    public static Bitmap bitmap;

    private static LinearLayout bottomLL;
    private ImageView nativeUpload, nativeDelete, nativeShare;

    private ImageView leftImage;
    private TextView titleText;
    private static TextView rightText;

    private PopupWindow pop = null;
    private LinearLayout ll_popup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_local_album, null);
        leftImage = (ImageView) actionbarLayout.findViewById(R.id.left_imbt);
        leftImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String title = getIntent().getStringExtra("title");
        titleText = (TextView) actionbarLayout.findViewById(R.id.title);
        if (title != null && !title.equals("")) {
            titleText.setText(title);
        }
        rightText = (TextView) actionbarLayout.findViewById(R.id.right_tv);
        getActionBar().setCustomView(actionbarLayout);
//        PublicWay.activityList.add(this);
        mContext = this;
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        init();
        initPop();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
            //显示或关闭底部工具条
            isShowOkEditView();
        }
    };

//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        //添加菜单项
//        MenuItem findItem = menu.add(0, 0, 0, "继续");
//        findItem.setIcon(R.drawable.ic_goon);
//        //绑定到ActionBar
//        findItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == 0) {
////            intent.setClass(LocalAlbumActivity.this, ImageFileActivity.class);
////            startActivity(intent);
//            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//            intent.setClass(mContext, MWTUploadPicActivity.class);
//            if (Bimp.tempSelectBitmap.size() > 0) {
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(LocalAlbumActivity.this, "请选择图片", 100).show();
//            }
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // 完成按钮的监听
    private class AlbumSendListener implements View.OnClickListener {
        public void onClick(View v) {
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            intent.setClass(mContext, MWTUploadPicActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    // 初始化，给一些对象赋值
    private void init() {
        bottomLL = (LinearLayout) findViewById(R.id.bottom_layout);
        nativeUpload = (ImageView) findViewById(R.id.nativeUpload);
        nativeUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                intent.setClass(mContext, MWTUploadPicActivity.class);
                if (Bimp.tempSelectBitmap.size() > 0) {
                    startActivity(intent);
                } else {
                    Toast.makeText(LocalAlbumActivity.this, "请选择图片", 100).show();
                }
            }
        });

        nativeDelete = (ImageView) findViewById(R.id.nativeDel);
        nativeDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LocalAlbumActivity.this)
                        .setTitle("请确认")
                        .setMessage("照片将从本地彻底删除，是否继续？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePic();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

        nativeShare = (ImageView) findViewById(R.id.nativeShare);
        nativeShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                View parentView = getLayoutInflater().inflate(R.layout.activity_mwtupload_pic, null);
                ll_popup.startAnimation(AnimationUtils.loadAnimation(LocalAlbumActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            }
        });

        AlbumHelper albumHelper = new AlbumHelper();
        helper = albumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }

        DateHighToLowComparator comparator = new DateHighToLowComparator();
        Collections.sort(dataList, comparator);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new LocalAlbumGridViewAdapter(this, dataList,
                Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
    }

    public void initPop() {
        pop = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popup_share, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        ImageView bt1 = (ImageView) view
                .findViewById(R.id.friend);
        ImageView bt2 = (ImageView) view
                .findViewById(R.id.friend_circle);
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
                shareToFriend();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareToFriendCircle();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    // 多张图片分享给微信好友
    private void shareToFriend() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (ImageItem item : Bimp.tempSelectBitmap) {
            imageUris.add(Uri.fromFile(new File(item.imagePath)));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        startActivity(intent);
    }

    // 多张图片分享到微信朋友圈
    private void shareToFriendCircle() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra("Kdescription", "");
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (ImageItem item : Bimp.tempSelectBitmap) {
            imageUris.add(Uri.fromFile(new File(item.imagePath)));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        startActivity(intent);
    }

    private void deletePic() {
        ArrayList<Integer> positionList = new ArrayList<Integer>();
        for (int i = 0; i < dataList.size(); i++) {
            if (Bimp.tempSelectBitmap.contains(dataList.get(i))) {
                File file = new File(dataList.get(i).imagePath);
                if (file.exists()) {
                    file.delete();
                    dataList.remove(dataList.get(i));
                    positionList.add(i);
                }

            }
        }
        Bimp.tempSelectBitmap.clear();
        isShowOkEditView();
        gridImageAdapter.notifyDataSetChanged();
        // 向我页面发送广播
        Intent intent = new Intent("picture.broadcast.delete");
        intent.putExtra("positionList", positionList);
        sendBroadcast(intent);
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

    public static void isShowOkEditView() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            bottomLL.setVisibility(View.VISIBLE);
            rightText.setText(Bimp.tempSelectBitmap.size() + "/9");
        } else {
            bottomLL.setVisibility(View.GONE);
            rightText.setText("");
        }
    }

    @Override
    protected void onRestart() {
        isShowOkEditView();
        super.onRestart();
    }
}
