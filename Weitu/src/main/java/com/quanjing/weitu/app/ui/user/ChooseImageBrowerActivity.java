package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.PhotoView;
import com.quanjing.weitu.app.ui.photo.PictureUtil;
import com.quanjing.weitu.app.ui.photo.Res;
import com.quanjing.weitu.app.ui.photo.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @version 2014年10月18日  下午11:47:53
 * @QQ:595163260
 */
public class ChooseImageBrowerActivity extends Activity {
    private Intent intent;
    //顶部显示预览图片位置的textview
    private TextView positionTextView;
    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;

    public List<Bitmap> bmp = new ArrayList<Bitmap>();
    public List<String> drr = new ArrayList<String>();
    public List<String> del = new ArrayList<String>();

    private Context mContext;

    RelativeLayout photo_relativeLayout;
    private ImageView remove, back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(Res.getLayoutID("activity_choose_image_brower"));// 切屏到主界面
//		PublicWay.activityList.add(this);
        mContext = this;
        positionTextView = (TextView) findViewById(Res.getWidgetID("indicator"));
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = Integer.parseInt(intent.getStringExtra("position"));
        if (Bimp.tempSelectBitmap.size() > 0) {
            positionTextView.setText(position + "/" + Bimp.tempSelectBitmap.size());
            positionTextView.setTextColor(Color.WHITE);
        }
        // 为发送按钮设置文字
        pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            int degree = PictureUtil.readPictureDegree(Bimp.tempSelectBitmap.get(i).getImagePath());
            Bitmap bitmap = PictureUtil.getSmallBitmap(Bimp.tempSelectBitmap.get(i).getImagePath());
            if (degree != 0) {
                bitmap = PictureUtil.rotaingImageView(degree, bitmap);
            }
            initListViews(bitmap);
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin((int) getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);

        remove = (ImageView) findViewById(Res.getWidgetID("remove"));
        back = (ImageView) findViewById(Res.getWidgetID("back"));

        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ChooseImageBrowerActivity.this)
                        .setTitle("请确认")
                        .setMessage("确认取消上传该张图片？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (listViews.size() == 1) {
                                    Bimp.tempSelectBitmap.clear();
                                    Bimp.max = 0;
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Bimp.tempSelectBitmap.remove(location);
                                    Bimp.max--;
                                    pager.removeAllViews();
                                    listViews.remove(location);
                                    adapter.setListViews(listViews);
                                    positionTextView.setText((location + 1) + "/" + Bimp.tempSelectBitmap.size());
                                    adapter.notifyDataSetChanged();
                                }
                                Intent broadIntent = new Intent("data.broadcast.action");
                                sendBroadcast(broadIntent);
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
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setResult(RESULT_OK);
        finish();
        return super.onKeyDown(keyCode, event);
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
            positionTextView.setText((location + 1) + "/" + Bimp.tempSelectBitmap.size());
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void initListViews(Bitmap bm) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
