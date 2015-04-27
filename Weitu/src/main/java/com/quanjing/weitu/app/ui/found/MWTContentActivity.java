package com.quanjing.weitu.app.ui.found;

/**
 * Created by Administrator on 2014/12/17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.found.ShowWebImageActivity;
import com.quanjing.weitu.app.ui.sharesdk.ShareModel;
import com.quanjing.weitu.app.ui.sharesdk.SharePopupWindow;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;

import org.apache.http.Header;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lcsky.SVProgressHUD;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.onekeyshare.OnekeyShare;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;

@SuppressLint("SetJavaScriptEnabled")
public class MWTContentActivity extends MWTBase2Activity implements PlatformActionListener, Callback {

    private final static int MENU_SHARE = 0x888;
    private WebView contentWebView = null;
    private String contentUrl = "";
    private String imageUrl = "";
    private String caption = "";

    private String text = "这是我的分享测试数据！~我只是来酱油的！~请不要在意 好不好？？？？？";
    private String imageurl = "http://h.hiphotos.baidu.com/image/pic/item/ac4bd11373f082028dc9b2a749fbfbedaa641bca.jpg";
    private String title = "拍拍搜";
    private String url = "www.baidu.com";

    //public static final String SHARE_APP_KEY = "21b0f35691b8";
    private Button shareButton;
    private SharePopupWindow share;

    private ArrayList<String> imgList = new ArrayList<String>();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        setTitleText("          "+getIntent().getExtras().getString("caption"));
        contentWebView = (WebView) findViewById(R.id.webview);
        // 启用javascript
        contentWebView.getSettings().setJavaScriptEnabled(true);
        // 随便找了个带图片的网站
        contentUrl = getIntent().getExtras().getString("contentUrl");
        imageUrl = getIntent().getExtras().getString("imageUrl");
        caption = getIntent().getExtras().getString("caption");
        contentWebView.loadUrl(contentUrl);
        // 添加js交互接口类，并起别名 imagelistner
        contentWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        contentWebView.setWebViewClient(new MyWebViewClient());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(contentUrl).get();
                    Elements links = doc.select("img");
                    for (int i = 0; i < links.size(); i++) {
                        Element element = links.get(i);
                        String imgUrl = element.attr("src");
                        if (imgUrl.indexOf("http:") != -1) {
                            imgList.add(imgUrl);
                        }
                    }
                    if (!doc.title().equals(""))
                        caption = doc.title();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        ShareSDK.initSDK(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(0, MENU_SHARE, 0, "分享").setIcon(R.drawable.ic_article_share);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_SHARE) {
            performShare();
            //share();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share() {
        share = new SharePopupWindow(MWTContentActivity.this);
        share.setPlatformActionListener(MWTContentActivity.this);
        ShareModel model = new ShareModel();
        model.setImageUrl(imageurl);
        model.setText(text);
        model.setTitle(caption);
        model.setUrl(contentUrl + "&d=1");

        share.initShareParams(model);
        share.showShareWindow();
        // 显示窗口 (设置layout在PopupWindow中显示的位置)
        share.showAtLocation(MWTContentActivity.this.findViewById(R.id.main),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        contentWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }
        // 4.2及以上版本需要增加注释语句@JavascriptInterface
        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (imgList.size() > 0) {
                int position = 0;
                for (int i = 0; i < imgList.size(); i++) {
                    if (imgList.get(i).equals(img)) {
                        position = i;
                        break;
                    }
                }
                Intent intent = new Intent(context, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imgList);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                context.startActivity(intent);
            }
//            Intent intent = new Intent();
//            intent.putExtra("image", img);
//            intent.setClass(context, ShowWebImageActivity.class);
//            context.startActivity(intent);
        }

    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    private void performShare() {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (imageUrl != null && !imageUrl.equals("")) {
            SVProgressHUD.showInView(MWTContentActivity.this, "分享中，请稍候...", true);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(imageUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {

                    SVProgressHUD.dismiss(MWTContentActivity.this);


                    final File outputDir = getCacheDir();
                    final String fileExt = Files.getFileExtension(imageUrl);

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                        }

                        @Override
                        protected void onPostExecute(String outputFilePath) {
                            if (outputFilePath != null) {
                                final OnekeyShare oks = new OnekeyShare();
                                String appName = getApplicationInfo().name;
                                // 分享时Notification的图标和文字
                                oks.setNotification(0, appName);
                                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                                oks.setTitle(caption);

                                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                                //oks.setTitleUrl(contentUrl);
                                // text是分享文本，所有平台都需要这个字段
                                //oks.setText(caption + "\n" + contentUrl);

                                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                                oks.setImagePath(outputFilePath);
                                // url仅在微信（包括好友和朋友圈）中使用
                                //oks.setImageUrl(contentUrl);
                                // url仅在微信（包括好友和朋友圈）中使用
                                oks.setUrl(contentUrl + "&d=1");

                                oks.setSilent(false);

                                // 令编辑页面显示为Dialog模式
                                oks.setDialogMode();

                                // 在自动授权时可以禁用SSO方式
                                //oks.disableSSOWhenAuthorize();

                                // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                                oks.show(MWTContentActivity.this);
                            } else {
                                SVProgressHUD.showInViewWithoutIndicator(MWTContentActivity.this, "分享失败：无法写入文件", 1);
                            }
                        }

                        @Override
                        protected String doInBackground(Void... param) {
                            try {
                                File outputDir = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing");
                                if (!outputDir.exists()) {
                                    outputDir.mkdirs();
                                }

                                File outputFile = new File(Environment.getExternalStorageDirectory() + "/com.quanjing/QuanJing/" + String.valueOf(Math.abs(imageUrl.hashCode())) + "." + fileExt);
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
                    SVProgressHUD.showInViewWithoutIndicator(MWTContentActivity.this, "分享失败：无法获取文件", 1);
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (share != null) {
            share.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        Message msg = new Message();
        msg.what = 0;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Message msg = new Message();
        msg.what = 1;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == 1) {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
        if (share != null) {
            share.dismiss();
        }
        return false;
    }


}