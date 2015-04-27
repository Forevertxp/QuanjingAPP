package com.quanjing.weitu.app.ui.user;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.found.HackyViewPager;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PublicWay;

import java.io.File;
import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 图片查看器
 */
public class LocalImageBrowerActivity extends FragmentActivity {


    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String FROM_TYPE = "from_type";


    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private int fromType;
    public static ArrayList<ImageItem> imageItems;
    private ArrayList<String> urls = new ArrayList<String>();

    private ImageView delete, share, upload, choose, back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_image_brower);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        if (imageItems != null) {
            for (ImageItem item : imageItems) {
                urls.add(item.getImagePath());
            }
        }

        back = (ImageView) findViewById(R.id.nativeBack);
        upload = (ImageView) findViewById(R.id.nativeUpload);
        delete = (ImageView) findViewById(R.id.nativeDel);
        share = (ImageView) findViewById(R.id.nativeShare);
        choose = (ImageView) findViewById(R.id.nativeChoose);

        fromType = getIntent().getIntExtra(FROM_TYPE, 0);
        if (fromType == 1) {
            upload.setVisibility(View.VISIBLE);
            choose.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
        } else if (fromType == 2) {
            upload.setVisibility(View.GONE);
            choose.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            if (Bimp.tempSelectBitmap.contains(imageItems.get(pagerPosition))) {
                choose.setBackgroundResource(R.drawable.plugin_camera_choosed);
            } else {
                choose.setBackgroundResource(R.drawable.plugin_camera_no_choosed);
            }
        } else if (fromType == 3) {
            LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_layout1);
            bottom.setVisibility(View.GONE);
        }

        mPager = (HackyViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                pagerPosition = arg0;
                if (fromType == 2) {
                    if (Bimp.tempSelectBitmap.contains(imageItems.get(pagerPosition))) {
                        choose.setBackgroundResource(R.drawable.plugin_camera_choosed);
                    } else {
                        choose.setBackgroundResource(R.drawable.plugin_camera_no_choosed);
                    }
                }
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LocalImageBrowerActivity.this)
                        .setTitle("请确认")
                        .setMessage("照片将从本地彻底删除，是否继续？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePic(urls.get(pagerPosition));
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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageItem imageItem = imageItems.get(pagerPosition);
                Intent editorIntent = new Intent(LocalImageBrowerActivity.this, ImageEditorActivity.class);
                editorIntent.putExtra("imgUrl", imageItem.getImagePath());
                editorIntent.putExtra("imgLongtitude", imageItem.longtitude);
                editorIntent.putExtra("imgLatitude", imageItem.latitude);
                startActivity(editorIntent);
                finish();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performShare(urls.get(pagerPosition));
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bimp.tempSelectBitmap.contains(imageItems.get(pagerPosition))) {
                    choose.setBackgroundResource(R.drawable.plugin_camera_no_choosed);
                    Bimp.tempSelectBitmap.remove(imageItems.get(pagerPosition));
                } else {
                    if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
                        choose.setBackgroundResource(R.drawable.plugin_camera_no_choosed);
                        if (!Bimp.tempSelectBitmap.remove(imageItems.get(pagerPosition))) {
                            Toast.makeText(LocalImageBrowerActivity.this, R.string.only_choose_num, 200).show();
                        }
                        return;
                    }
                    choose.setBackgroundResource(R.drawable.plugin_camera_choosed);
                    Bimp.tempSelectBitmap.add(imageItems.get(pagerPosition));
                }
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deletePic(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
            Intent intent = getIntent();
            ArrayList<Integer> positionList = new ArrayList<Integer>();
            positionList.add(pagerPosition);
            intent.putExtra("positionList", positionList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return LocalImageFragment.newInstance(url);
        }

    }

    private void performShare(String imagePath) {
        final OnekeyShare oks = new OnekeyShare();
        String appName = getApplicationInfo().name;
        oks.setNotification(0, appName);

        oks.setTitle("全景网");
        oks.setImagePath(imagePath);

        oks.setSilent(false);

        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();

        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();

        // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
        oks.show(LocalImageBrowerActivity.this);
    }
}

