package com.quanjing.weitu.app.ui.found;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;

import org.apache.http.Header;
import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {


    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> captions = new ArrayList<String>();
    private ArrayList<String> webs = new ArrayList<String>();

    private ImageView back, share, download;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_pager);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        captions = getIntent().getStringArrayListExtra("captions");
        webs = getIntent().getStringArrayListExtra("webs");

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
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);

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
                performDownload(urls.get(pagerPosition));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performShare(urls.get(pagerPosition));
            }
        });
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
            return ImageDetailFragment.newInstance(url);
        }

    }

    private void performDownload(final String imageURL) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(ImagePagerActivity.this)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用下载功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ImagePagerActivity.this, MWTAuthSelectActivity.class);
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


        SVProgressHUD.showInView(ImagePagerActivity.this, "下载中，请稍候...", true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageURL, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                SVProgressHUD.showInView(ImagePagerActivity.this, "下载完成，保存中...", true);

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

                                SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "成功保存到相册！", 1);
                            } catch (SecurityException se) {
                                SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "保存失败：没有相册访问权限", 1);
                            }
                        } else {
                            SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "保存失败：无法写入文件", 1);
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
                SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "下载失败：无法获取文件", 1);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private void performShare(final String imageURL) {
        MWTAuthManager am = MWTAuthManager.getInstance();

        SVProgressHUD.showInView(ImagePagerActivity.this, "分享中，请稍候...", true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageURL, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {


                SVProgressHUD.dismiss(ImagePagerActivity.this);

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

                            if (captions != null && captions.get(pagerPosition) != null && !captions.get(pagerPosition).equals("")) {
                                oks.setTitle(captions.get(pagerPosition));
                            } else {
                                oks.setTitle("全景网");
                            }

                            oks.setImagePath(outputFilePath);
                            if (webs != null && webs.get(pagerPosition) != null && !webs.get(pagerPosition).equals("")) {
                                if (webs.get(pagerPosition).indexOf("zone.quanjing.com") == -1) {
                                    oks.setUrl(webs.get(pagerPosition));
                                }
                            }

                            oks.setSilent(false);

                            // 令编辑页面显示为Dialog模式
                            oks.setDialogMode();

                            // 在自动授权时可以禁用SSO方式
                            oks.disableSSOWhenAuthorize();

                            // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                            oks.show(ImagePagerActivity.this);
                        } else {
                            SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "分享失败：无法写入文件", 1);
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
                SVProgressHUD.showInViewWithoutIndicator(ImagePagerActivity.this, "分享失败：无法获取文件", 1);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}

