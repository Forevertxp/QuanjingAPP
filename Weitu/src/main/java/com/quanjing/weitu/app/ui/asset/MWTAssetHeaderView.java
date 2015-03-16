package com.quanjing.weitu.app.ui.asset;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTServiceResult;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.quanjing.weitu.app.ui.found.ShowWebImageActivity;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTAssetHeaderView extends FrameLayout {
    private Context _context;

    private DynamicHeightImageView _imageView;
    private Button _downloadButton;
    private Button _collectButton;
    private Button _shareButton;

    private TextView _serialTextView;
    private TextView _captionTextView;
    private TextView _moreTextVIew;
    private ListView _commentListView;
    private TextView _likeTextView;
    private GridView _likeGridView;
    private MWTAsset _asset;

    private List<String> images = new ArrayList<>(); // 关注者头像url

    public MWTAssetHeaderView(Context context) {
        super(context);
        construct(context);
    }

    public MWTAssetHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct(context);
    }

    public MWTAssetHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        construct(context);
    }

    private void construct(Context context) {
        _context = context;
        LayoutInflater.from(context).inflate(R.layout.view_asset_header, this, true);
        setupViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews() {
        _imageView = (DynamicHeightImageView) findViewById(R.id.ImageView);

        _downloadButton = (Button) findViewById(R.id.DownloadButton);
        _collectButton = (Button) findViewById(R.id.CollectButton);
        _shareButton = (Button) findViewById(R.id.ShareButton);

        _serialTextView = (TextView) findViewById(R.id.SerialTextView);
        _captionTextView = (TextView) findViewById(R.id.CaptionTextView);
        _moreTextVIew = (TextView) findViewById(R.id.more_comment);
        _commentListView = (ListView) findViewById(R.id.commentList);

        _likeGridView = (GridView) findViewById(R.id.like);
        _likeTextView = (TextView) findViewById(R.id.likenum);

        _downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performDownload();
            }
        });

        _collectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performCollect();
            }
        });

        _shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performShare();
            }
        });
    }

    public void setAsset(MWTAsset asset) {
        _asset = asset;
        updateImageView(asset);
        updateTextViews(asset);
        updateCommentListView(asset);
    }

    /**
     * 更新图片
     *
     * @param asset
     */
    private void updateImageView(final MWTAsset asset) {
        if (asset != null && asset.getImageInfo() != null) {
            MWTImageInfo imageInfo = asset.getImageInfo();
            float heightRatio = (float) imageInfo.height / (float) imageInfo.width;
            int color = Color.WHITE;
            try {
                color = Color.parseColor("#" + imageInfo.primaryColorHex);
            } catch (RuntimeException ignored) {

            }
            _imageView.setHeightRatio(heightRatio);
            if (heightRatio > 0)
                Picasso.with(_context)
                        .load(imageInfo.url)
                        .resize(640, (int) (640 * heightRatio))
                        .placeholder(new ColorDrawable(color))
                        .into(_imageView);
            final String imageUrl = imageInfo.url;
            _imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("image", imageUrl);
                    intent.setClass(_context, ShowWebImageActivity.class);
                    _context.startActivity(intent);
                }
            });
        } else {
            _imageView.setImageResource(R.color.ClearImageViewColor);
            _imageView.setHeightRatio(2.0 / 3.0);
        }
    }

    /**
     * 更新图片编号
     *
     * @param asset
     */
    private void updateTextViews(MWTAsset asset) {
        if (asset != null) {
            String oriPic = asset.get_oriPic();
            String serial = asset.getAssetID();
            if (oriPic != null && !oriPic.isEmpty()) {
                _serialTextView.setVisibility(View.VISIBLE);
                _serialTextView.setText("编号：" + oriPic);
            } else if (serial != null && !serial.isEmpty()) {
                _serialTextView.setVisibility(View.VISIBLE);
                _serialTextView.setText("编号：" + serial);
            } else {
                _serialTextView.setVisibility(View.GONE);
            }

            String caption = asset.getCaption();
            if (caption != null && !caption.isEmpty()) {
                _captionTextView.setVisibility(View.VISIBLE);
                _captionTextView.setText("标签：" + caption);
            } else {
                _captionTextView.setVisibility(View.GONE);
            }
        } else {
            _serialTextView.setVisibility(View.GONE);
            _captionTextView.setVisibility(View.GONE);
        }
    }


    /**
     * 更新评论
     *
     * @param asset
     */
    public void updateCommentListView(final MWTAsset asset) {
        if (asset != null) {
            String serial = asset.getAssetID();
            if (serial != null && !serial.isEmpty()) {
                _commentListView.setVisibility(View.VISIBLE);
            } else {
                _commentListView.setVisibility(View.GONE);
            }
            String[] userIDS = asset.getLikedUserIDs();
            MWTUserManager userManager = MWTUserManager.getInstance();
            if (userIDS != null && userIDS.length > 0) {
                for (int i = 0; i < userIDS.length; i++) {
                    if (userManager.getUserByID(userIDS[i]) != null && userManager.getUserByID(userIDS[i]).getAssetsInfo() != null)
                        images.add(userManager.getUserByID(userIDS[i]).getAvatarImageInfo().url);
                }
                _likeTextView.setVisibility(View.GONE);
            } else {
                _likeTextView.setVisibility(View.VISIBLE);
            }
            GridViewAdapter gridViewAdapter = new GridViewAdapter();
            _likeGridView.setAdapter(gridViewAdapter);
            List<MWTCommentData> commentDataList = asset.get_latestComments();
            if (commentDataList != null && commentDataList.size() > 0) {
                _moreTextVIew.setText("查看所有评论");
                _moreTextVIew.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(_context, MWTCommentActivity.class);
                        intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                        _context.startActivity(intent);
                    }
                });
                _commentListView.setAdapter(new CommentAdapter(_context, commentDataList));

            }
        } else {
            _commentListView.setVisibility(View.GONE);
        }
    }

    private void performDownload() {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(_context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用下载功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(_context, MWTAuthSelectActivity.class);
                            _context.startActivity(intent);
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

        if (_asset != null && _asset.getImageInfo() != null && _asset.getImageInfo().url != null) {
            final String imageURL = _asset.getImageInfo().url;

            SVProgressHUD.showInView(getContext(), "下载中，请稍候...", true);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(imageURL, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    SVProgressHUD.showInView(getContext(), "下载完成，保存中...", true);

                    _asset.markDownloadedByCurrentUser(null);

                    final Context context = getContext();
                    final File outputDir = context.getCacheDir();
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
                                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                    SVProgressHUD.showInViewWithoutIndicator(getContext(), "成功保存到相册！", 1);
                                } catch (SecurityException se) {
                                    SVProgressHUD.showInViewWithoutIndicator(getContext(), "保存失败：没有相册访问权限", 1);
                                }
                            } else {
                                SVProgressHUD.showInViewWithoutIndicator(getContext(), "保存失败：无法写入文件", 1);
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
                    SVProgressHUD.showInViewWithoutIndicator(getContext(), "下载失败：无法获取文件", 1);
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        } else {

        }
    }

    /**
     * 添加收藏
     *
     * @param assetid
     */
    private void performCollect() {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(_context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用收藏功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(_context, MWTAuthSelectActivity.class);
                            _context.startActivity(intent);
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

        if (_asset != null && _asset.getImageInfo() != null && _asset.getImageInfo().url != null) {

            MWTRestManager restManager = MWTRestManager.getInstance();
            MWTAssetService assetService = restManager.create(MWTAssetService.class);
            SVProgressHUD.showInView(_context, "请稍后...", true);
            assetService.markCollected(_asset.getAssetID(), "lightbox", new Callback<MWTServiceResult>() {
                @Override
                public void success(MWTServiceResult mwtServiceResult, Response response) {
                    SVProgressHUD.dismiss(_context);
                    Toast.makeText(_context, "成功", 500).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    SVProgressHUD.dismiss(_context);
                    Toast.makeText(_context, error.getMessage(), 500).show();
                }
            });
        }
    }


    private void performShare() {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(_context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用分享功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(_context, MWTAuthSelectActivity.class);
                            _context.startActivity(intent);
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

        if (_asset != null && _asset.getImageInfo() != null && _asset.getImageInfo().url != null) {
            final String imageURL = _asset.getImageInfo().url;

            SVProgressHUD.showInView(getContext(), "分享中，请稍候...", true);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(imageURL, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    final Context context = getContext();

                    _asset.markSharedByCurrentUser(null);

                    SVProgressHUD.dismiss(context);

                    final File outputDir = context.getCacheDir();
                    final String fileExt = Files.getFileExtension(imageURL);

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                        }

                        @Override
                        protected void onPostExecute(String outputFilePath) {
                            if (outputFilePath != null) {
                                final OnekeyShare oks = new OnekeyShare();
                                String appName = _context.getApplicationInfo().name;
                                oks.setNotification(0, appName);

                                String caption = _asset.getCaption();
                                if (caption != null&&!caption.equals("")) {
                                    oks.setTitle(_asset.getCaption());
                                }else {
                                    oks.setTitle("全景网");
                                }

                                oks.setImagePath(outputFilePath);
                                if (_asset.get_webURL() != null && !_asset.get_webURL().equals(""))
                                    oks.setUrl(_asset.get_webURL());
                                oks.setSilent(false);

                                // 令编辑页面显示为Dialog模式
                                oks.setDialogMode();

                                // 在自动授权时可以禁用SSO方式
                                oks.disableSSOWhenAuthorize();

                                // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                                oks.show(getContext());
                            } else {
                                SVProgressHUD.showInViewWithoutIndicator(getContext(), "分享失败：无法写入文件", 1);
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
                    SVProgressHUD.showInViewWithoutIndicator(getContext(), "分享失败：无法获取文件", 1);
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }

    private class CommentAdapter extends BaseAdapter {

        private List<MWTCommentData> commentDataList;
        private Context context;

        public CommentAdapter(Context context, List<MWTCommentData> commentDataList) {
            this.context = context;
            this.commentDataList = commentDataList;
        }

        @Override
        public int getCount() {
            return commentDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return commentDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            holder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_comment_min, null);
            holder.imageView = (XCRoundImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);

            MWTCommentData commentData = commentDataList.get(position);
            MWTUserManager userManager = MWTUserManager.getInstance();
            MWTUser user = userManager.getUserByID(commentData.userID);
            holder.textView.setText(user.getNickname());
            holder.contentTextView.setText(commentData.content);
            Picasso.with(context)
                    .load(user.getAvatarImageInfo().url)
                    .into(holder.imageView);
            return convertView;
        }

        private class ViewHolder {
            XCRoundImageView imageView;
            TextView textView;
            TextView contentTextView;
        }
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview; // 声明ImageView的对象
            if (convertView == null) {
                imageview = new ImageView(_context); // 实例化ImageView的对象
                imageview.setScaleType(ImageView.ScaleType.FIT_START); // 设置缩放方式
                imageview.setPadding(5, 0, 5, 0); // 设置ImageView的内边距
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(_context)
                    .load(images.get(position)).resize(80, 80)
                    .into(imageview);
            return imageview; // 返回ImageView
        }

        /*
         * 功能：获得当前选项的ID
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            //System.out.println("getItemId = " + position);
            return position;
        }

        /*
         * 功能：获得当前选项
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return position;
        }

        /*
         * 获得数量
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return images.size();
        }
    }
}
