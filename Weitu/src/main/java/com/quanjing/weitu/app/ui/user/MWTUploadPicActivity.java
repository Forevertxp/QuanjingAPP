package com.quanjing.weitu.app.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTUtils;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.protocol.service.MWTAddCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTAssetsResult;
import com.quanjing.weitu.app.protocol.service.MWTCommentService;
import com.quanjing.weitu.app.ui.asset.MWTAssetsAdapter;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.photo.AlbumActivity;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.FileUtils;
import com.quanjing.weitu.app.ui.photo.GalleryActivity;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PictureUtil;
import com.quanjing.weitu.app.ui.photo.PublicWay;
import com.quanjing.weitu.app.ui.photo.Res;

import org.lcsky.SVProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * 首页面activity
 *
 * @author king
 * @version 2014年10月18日  下午11:48:34
 * @QQ:595163260
 */
public class MWTUploadPicActivity extends MWTBase2Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private TextView photo_text;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    private RelativeLayout rl_switch_private;
    private ImageView iv_switch_open_private;
    private ImageView iv_switch_close_private;

    private static int PHOTONUM = 0;
    private Lock lock = new ReentrantLock();

    private RelativeLayout rl_switch_notification;
    private ImageView iv_switch_open_notification;
    private ImageView iv_switch_close_notification;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        //PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_mwtupload_pic, null);
        setContentView(parentView);
        Init();
        setTitleText("上传");

        rl_switch_private = (RelativeLayout) findViewById(R.id.rl_private);

        iv_switch_open_private = (ImageView) findViewById(R.id.iv_switch_open_private);
        iv_switch_close_private = (ImageView) findViewById(R.id.iv_switch_close_private);

        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);

        iv_switch_open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);

        rl_switch_private.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open_private.getVisibility() == View.VISIBLE) {
                    iv_switch_open_private.setVisibility(View.INVISIBLE);
                    iv_switch_close_private.setVisibility(View.VISIBLE);
                } else {
                    iv_switch_open_private.setVisibility(View.VISIBLE);
                    iv_switch_close_private.setVisibility(View.INVISIBLE);
                }
            }
        });

        rl_switch_notification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //添加菜单项
        MenuItem findItem = menu.add(0, 0, 0, "上传");
        findItem.setIcon(R.drawable.ic_upload);
        //绑定到ActionBar
        findItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            if (MWTUtils.isFastDoubleClick())
                return true;
            ImageItem imageItem = null;
            if (Bimp.tempSelectBitmap.size() > 0) {
                imageItem = Bimp.tempSelectBitmap.get(0);
                uploadPhotos(imageItem.imagePath, 1);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Init() {

        photo_text = (TextView) findViewById(R.id.photo_text);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        int height = ((getResources().getDisplayMetrics().widthPixels - 20) / 4);
        if (Bimp.tempSelectBitmap.size() > 4 && Bimp.tempSelectBitmap.size() < 9) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, height * 2);
            noScrollgridview.setLayoutParams(layoutParams);
        }

        if (Bimp.tempSelectBitmap.size() > 8) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, height * 3);
            noScrollgridview.setLayoutParams(layoutParams);
        }
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
    }

    /**
     * @param isNew 是否是本次上传的第一张
     *              上传照片及说明
     */

    private void uploadPhotos(String filePath, int isNew) {
        lock.lock();
        PHOTONUM++;
        SVProgressHUD.showInView(MWTUploadPicActivity.this, "正在上传第" + PHOTONUM + "照片...", true);
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTAssetService assetService = restManager.create(MWTAssetService.class);
        final boolean is_private = false;
        TypedFile imageTypedFile = null;
        int degree = 0; //图片拍摄角度
        if (filePath != null && !filePath.equals("")) {

            //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
            degree = PictureUtil.readPictureDegree(filePath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            //压缩图片
            String tempPath;
            if (options.outWidth > 640) {
                try {
                    //Bitmap bitmap = PictureUtil.getSmallBitmap(filePath);
                    Bitmap bitmap = PictureUtil.compressImage(filePath);
                    tempPath = PictureUtil.createTempFile(bitmap);
                } catch (IOException e) {
                    tempPath = filePath;
                    e.printStackTrace();
                }
            } else {
                tempPath = filePath;
            }

            File imageFile = new File(tempPath);
            imageTypedFile = new TypedFile("application/octet-stream", imageFile);
        }
        assetService.uploadAssets("upload", "", photo_text.getText().toString(), is_private, isNew, imageTypedFile, degree, new Callback<MWTAssetsResult>() {
            @Override
            public void success(MWTAssetsResult result, Response response) {
                lock.unlock();
                SVProgressHUD.dismiss(MWTUploadPicActivity.this);
                if (PHOTONUM == Bimp.tempSelectBitmap.size()) {
                    Bimp.tempSelectBitmap.clear();
                    PHOTONUM = 0;
                    finish();
                    PictureUtil.deleteTempFile(FileUtils.SDPATH + "quanjing_temp.jpg");
                } else {
                    ImageItem imageItem = Bimp.tempSelectBitmap.get(PHOTONUM);
                    uploadPhotos(imageItem.imagePath, 0);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                lock.unlock();
                SVProgressHUD.dismiss(MWTUploadPicActivity.this);
                if (PHOTONUM == Bimp.tempSelectBitmap.size()) {
                    Bimp.tempSelectBitmap.clear();
                    PHOTONUM = 0;
                    finish();
                    PictureUtil.deleteTempFile(FileUtils.SDPATH + "quanjing_temp.jpg");
                } else {
                    ImageItem imageItem = Bimp.tempSelectBitmap.get(PHOTONUM);
                    uploadPhotos(imageItem.imagePath, 0);
                }
            }
        });

    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return Bimp.tempSelectBitmap.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int degree = PictureUtil.readPictureDegree(Bimp.tempSelectBitmap.get(position).imagePath);
            holder.image.setImageBitmap(PictureUtil.rotaingImageView(degree, Bimp.tempSelectBitmap.get(position).getBitmap()));
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

}

