package com.quanjing.weitu.app.ui.found;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;

import org.apache.http.Header;
import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShowWebImageActivity extends Activity {
    private TextView imageTextView = null;
    private String imagePath = null;
    private ZoomableImageView imageView = null;

    private ImageView back, share, download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_showimage);
        getActionBar().hide();
        this.imagePath = getIntent().getStringExtra("image");

        this.imageTextView = (TextView) findViewById(R.id.show_webimage_imagepath_textview);
        imageTextView.setText(this.imagePath);
        imageView = (ZoomableImageView) findViewById(R.id.show_webimage_imageview);

        back = (ImageView) findViewById(R.id.pic_back);
        download = (ImageView) findViewById(R.id.pic_download);
        share = (ImageView) findViewById(R.id.pic_share);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDownload(imagePath);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performShare(imagePath);
            }
        });


        AsyncImageLoader imageLoader = new AsyncImageLoader();
        imageLoader.loadDrawable(this.imagePath, new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                imageView.setImageBitmap(drawableToBitmap(imageDrawable));
            }
        });

    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    private void performDownload(final String imageURL) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(ShowWebImageActivity.this)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用下载功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ShowWebImageActivity.this, MWTAuthSelectActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return;
        }


        SVProgressHUD.showInView(ShowWebImageActivity.this, "下载中，请稍候...", true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageURL, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                SVProgressHUD.showInView(ShowWebImageActivity.this, "下载完成，保存中...", true);

                final File outputDir = getCacheDir();
                final String fileExt = Files.getFileExtension(imageURL);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                    }

                    @Override
                    protected void onPostExecute(String outputFilePath) {
                        if (outputFilePath != null) {
                            MimeTypeMap mimemap = MimeTypeMap.getSingleton();
                            String ext = MimeTypeMap.getFileExtensionFromUrl(outputFilePath);
                            String type = mimemap.getMimeTypeFromExtension(ext);
                            if (type == null) {
                                type = "image/jpg";
                            }

                            ContentValues values = new ContentValues();

                            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                            values.put(MediaStore.Images.Media.MIME_TYPE, type);
                            values.put(MediaStore.MediaColumns.DATA, outputFilePath);

                            try {
                                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "成功保存到相册！", 1);
                            } catch (SecurityException se) {
                                SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "保存失败：没有相册访问权限", 1);
                            }
                        } else {
                            SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "保存失败：无法写入文件", 1);
                        }
                    }

                    @Override
                    protected String doInBackground(Void... param) {
                        try {
                            File outputDir = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing");
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }

                            File outputFile = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing/" + String.valueOf(Math.abs(imageURL.hashCode())) + "." + fileExt);
                            if (outputFile.exists()) {
                                outputFile.delete();
                            }
                            outputFile.createNewFile();
                            FileOutputStream stream = new FileOutputStream(outputFile);
                            stream.write(response);
                            stream.close();
                            return outputFile.getPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "下载失败：无法获取文件", 1);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private void performShare(final String imageURL) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(this)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用分享功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ShowWebImageActivity.this, MWTAuthSelectActivity.class);
                            ShowWebImageActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return;
        }


        SVProgressHUD.showInView(ShowWebImageActivity.this, "分享中，请稍候...", true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageURL, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {


                SVProgressHUD.dismiss(ShowWebImageActivity.this);

                final File outputDir = getCacheDir();
                final String fileExt = Files.getFileExtension(imageURL);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                    }

                    @Override
                    protected void onPostExecute(String outputFilePath) {
                        if (outputFilePath != null) {
                            final OnekeyShare oks = new OnekeyShare();
                            String appName = getApplicationInfo().name;
                            oks.setNotification(0, appName);

                            oks.setImagePath(outputFilePath);
                            oks.setUrl("http://www.quanjing.com");
                            oks.setSilent(false);

                            // 令编辑页面显示为Dialog模式
                            oks.setDialogMode();

                            // 在自动授权时可以禁用SSO方式
                            oks.disableSSOWhenAuthorize();

                            // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                            oks.show(ShowWebImageActivity.this);
                        } else {
                            SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "分享失败：无法写入文件", 1);
                        }
                    }

                    @Override
                    protected String doInBackground(Void... param) {
                        try {
                            File outputDir = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing");
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }

                            File outputFile = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing/" + String.valueOf(Math.abs(imageURL.hashCode())) + "." + fileExt);
                            if (outputFile.exists()) {
                                outputFile.delete();
                            }
                            outputFile.createNewFile();
                            FileOutputStream stream = new FileOutputStream(outputFile);
                            stream.write(response);
                            stream.close();
                            return outputFile.getPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                SVProgressHUD.showInViewWithoutIndicator(ShowWebImageActivity.this, "分享失败：无法获取文件", 1);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

}
