package com.quanjing.weitu.app.ui.user;


//public class ImageEditorActivity extends MWTBaseActivity {
//
//    private ImageView editImage;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_editor);
//        Bitmap bitmap = getLocalBitmap(getIntent().getStringExtra("imgUrl"));
//        editImage = (ImageView) findViewById(R.id.editImage);
//        if (bitmap != null) {
//            DisplayMetrics dm = getResources().getDisplayMetrics();
//            int w_screen = dm.widthPixels;
//            int h_screen = dm.heightPixels;
//            float ratio = (float) (bitmap.getHeight()) / (float) (bitmap.getWidth());
//            Bitmap middle = zoom(bitmap, w_screen, (int) (w_screen * ratio));
//
//            editImage.setImageBitmap(Bitmap.createBitmap(middle, 0, 0, w_screen, (int) (w_screen * ratio) < 400 ? (int) (w_screen * ratio) : 400));
//        }
//    }
//
//    private Bitmap getLocalBitmap(String url) {
//        try {
//            FileInputStream fis = new FileInputStream(url);
//            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private Bitmap zoom(Bitmap bitmap, int w, int h) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        Matrix matrix = new Matrix();
//        float scaleWidth = ((float) w / width);
//        float scaleHeight = ((float) h / height);
//        matrix.postScale(scaleWidth, scaleHeight);
//        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
//                matrix, true);
//        return newbmp;
//    }
//
//}


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUserManager;
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
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
public class ImageEditorActivity extends MWTBase2Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private TextView photo_text;
    private EditText captionET, keywordsET, positionET;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private ArrayList<String> imageList = new ArrayList<String>();

    private RelativeLayout rl_switch_private;
    private ImageView iv_switch_open_private;
    private ImageView iv_switch_close_private;

    private RelativeLayout rl_switch_notification;
    private ImageView iv_switch_open_notification;
    private ImageView iv_switch_close_notification;

    private boolean compress = true;
    private String is_private = "false";

    final MWTUserManager userManager = MWTUserManager.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_mwtupload_pic, null);
        setContentView(parentView);
        Init();
        setTitleText("      上传");

        rl_switch_private = (RelativeLayout) findViewById(R.id.rl_private);
        iv_switch_open_private = (ImageView) findViewById(R.id.iv_switch_open_private);
        iv_switch_close_private = (ImageView) findViewById(R.id.iv_switch_close_private);

        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
        iv_switch_open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);

        rl_switch_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open_private.getVisibility() == View.VISIBLE) {
                    iv_switch_open_private.setVisibility(View.INVISIBLE);
                    iv_switch_close_private.setVisibility(View.VISIBLE);
                    is_private = "false";
                } else {
                    iv_switch_open_private.setVisibility(View.VISIBLE);
                    iv_switch_close_private.setVisibility(View.INVISIBLE);
                    is_private = "true";
                }
            }
        });

        rl_switch_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                    compress = true;
                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                    compress = false;
                }
            }
        });
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
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            if (userManager.getCurrentUser() != null && userManager.getCurrentUser().getNickname().equals("")) {
                Intent intent = new Intent(ImageEditorActivity.this, MWTUserInfoEditActivity.class);
                Toast.makeText(ImageEditorActivity.this, "请先完善个人信息", 100).show();
                intent.putExtra(MWTUserInfoEditActivity.ARG_USER_ID, userManager.getCurrentUser().getUserID());
                startActivity(intent);
                return false;
            }
            ImageItem imageItem = null;
            if (imageList.size() > 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    uploadPhotos(imageList.get(i));
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Init() {
        photo_text = (TextView) findViewById(R.id.photo_text);
        captionET = (EditText) findViewById(R.id.caption);
        keywordsET = (EditText) findViewById(R.id.keywords);
        positionET = (EditText) findViewById(R.id.position);
        String imgLongtitude = getIntent().getStringExtra("imgLongtitude");
        String imgLatitude = getIntent().getStringExtra("imgLatitude");
        if (!TextUtils.isEmpty(imgLongtitude) && !TextUtils.isEmpty(imgLatitude))
            setTapCoordinates(Float.parseFloat(imgLatitude), Float.parseFloat(imgLongtitude));
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

        final String imageUrl = getIntent().getStringExtra("imgUrl");
        imageList.add(imageUrl);
        adapter = new GridAdapter(this, imageList);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> imgList = new ArrayList<String>();
                ArrayList<String> webList = new ArrayList<String>();
                ArrayList<String> captionList = new ArrayList<String>();
                Intent intent = new Intent(ImageEditorActivity.this, LocalImageBrowerActivity.class);
                ImageItem item = new ImageItem();
                item.setImagePath(imageUrl);
                ArrayList<ImageItem> items = new ArrayList<ImageItem>();
                items.add(item);
                LocalImageBrowerActivity.imageItems =items;
                intent.putExtra(LocalImageBrowerActivity.FROM_TYPE, 3);
                intent.putExtra(LocalImageBrowerActivity.EXTRA_IMAGE_INDEX, i);
                startActivity(intent);
            }
        });
    }

    /**
     * set Tap Coordinates
     */
    public void setTapCoordinates(float latitude, float longtitude) {

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longtitude, 1);
            StringBuilder add = new StringBuilder();
            if (addresses.size() > 0) {
                int max = addresses.get(0).getMaxAddressLineIndex();
                // add.append(addresses.get(0).getAddressLine(max - 1));
                // 取得全部名称时如下
//                for (int i = 0; i < max; i++) {
//                    add.append(addresses.get(0).getAddressLine(i) + " ");
//                }
                for (int i = 0; i < max; i++) {
                    if (i == 2)
                        break;
                    add.append(addresses.get(0).getAddressLine(i) + " ");
                }
            }
            positionET.setText(add.toString());
        } catch (IOException e) {
        }
    }

    /**
     * 上传照片及说明
     */

    private synchronized void uploadPhotos(String filePath) {
        SVProgressHUD.showInView(ImageEditorActivity.this, "正在上传照片...", true);
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTAssetService assetService = restManager.create(MWTAssetService.class);
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

            if (compress) {
                if (((degree == 0 || degree == 180) && options.outWidth > 640) || ((degree == 90 || degree == 270) && options.outHeight > 640)) {
                    try {
                        // Bitmap bitmap = PictureUtil.getSmallBitmap(filePath);
                        Bitmap bitmap = PictureUtil.compressImage(filePath, degree);
                        tempPath = PictureUtil.createTempFile(bitmap);
                    } catch (IOException e) {
                        tempPath = filePath;
                        e.printStackTrace();
                    }
                } else {
                    tempPath = filePath;
                }
            } else {
                tempPath = filePath;
            }

            File imageFile = new File(tempPath);
            imageTypedFile = new TypedFile("application/octet-stream", imageFile);
        }
        assetService.uploadAssets("upload", captionET.getText().toString(), keywordsET.getText().toString(), positionET.getText().toString(), "", is_private, 1, imageTypedFile, degree, "android", new Callback<MWTAssetsResult>() {
            @Override
            public void success(MWTAssetsResult result, Response response) {
                SVProgressHUD.dismiss(ImageEditorActivity.this);
                Toast.makeText(ImageEditorActivity.this, "上传成功", 500).show();
                if (userManager.getCurrentUser() != null)
                    userManager.getCurrentUser().markDataDirty();
                finish();
                PictureUtil.deleteTempFile(FileUtils.SDPATH + "quanjing_temp.jpg");
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(ImageEditorActivity.this);
                Toast.makeText(ImageEditorActivity.this, "上传失败", 500).show();
                PictureUtil.deleteTempFile(FileUtils.SDPATH + "quanjing_temp.jpg");
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;
        private ArrayList<String> imageList;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context, ArrayList<String> imageList) {
            inflater = LayoutInflater.from(context);
            this.imageList = imageList;
        }

        public int getCount() {
            return imageList.size();
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

            try {
                //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                int degree = PictureUtil.readPictureDegree(imageList.get(position));
                holder.image.setImageBitmap(PictureUtil.rotaingImageView(degree, Bimp.revitionImageSize(imageList.get(position))));
            } catch (IOException e) {
                e.printStackTrace();
            }

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

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            for (int i = 0; i < PublicWay.activityList.size(); i++) {
//                if (null != PublicWay.activityList.get(i)) {
//                    PublicWay.activityList.get(i).finish();
//                }
//            }
//            System.exit(0);
//        }
//        return true;
//    }

}


