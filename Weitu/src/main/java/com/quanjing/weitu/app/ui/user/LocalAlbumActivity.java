package com.quanjing.weitu.app.ui.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album);
        setTitleText("本机相册");
//        PublicWay.activityList.add(this);
        mContext = this;
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        init();
        initListener();
        //这个函数主要用来控制预览和完成按钮的状态
        //isShowOkBt();

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //添加菜单项
        MenuItem findItem = menu.add(0, 0, 0, "继续");
        findItem.setIcon(R.drawable.ic_goon);
        //绑定到ActionBar
        findItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
//            intent.setClass(LocalAlbumActivity.this, ImageFileActivity.class);
//            startActivity(intent);
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            intent.setClass(mContext, MWTUploadPicActivity.class);
            if (Bimp.tempSelectBitmap.size() > 0) {
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LocalAlbumActivity.this, "请选择图片", 100).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private void initListener() {

        gridImageAdapter
                .setOnItemClickListener(new LocalAlbumGridViewAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked, Button chooseBt) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
                            toggleButton.setChecked(false);
                            chooseBt.setVisibility(View.GONE);
                            if (!removeOneData(dataList.get(position))) {
                                Toast.makeText(LocalAlbumActivity.this, R.string.only_choose_num,
                                        200).show();
                            }
                            return;
                        }
                        if (isChecked) {
                            chooseBt.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                        } else {
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                        }
                        isShowOkBt();
                    }
                });


    }

    private boolean removeOneData(ImageItem imageItem) {
//        if (Bimp.tempSelectBitmap.contains(imageItem)) {
//            Bimp.tempSelectBitmap.remove(imageItem);
//            okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
//            return true;
//        }
        return false;
    }

    public void isShowOkBt() {
//        if (Bimp.tempSelectBitmap.size() > 0) {
//            okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
//            okButton.setPressed(true);
//            okButton.setClickable(true);
//            okButton.setTextColor(Color.WHITE);
//        } else {
//            okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
//            okButton.setPressed(false);
//            okButton.setClickable(false);
//            okButton.setTextColor(Color.parseColor("#E1E0DE"));
//        }
    }

    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
