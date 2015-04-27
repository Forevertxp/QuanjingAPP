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
import android.widget.LinearLayout;
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
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTCommentService;
import com.quanjing.weitu.app.protocol.service.MWTServiceResult;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.community.LikedUserImageLoader;
import com.quanjing.weitu.app.ui.community.UserLoader;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.quanjing.weitu.app.ui.found.ImagePagerActivity;
import com.quanjing.weitu.app.ui.found.ShowWebImageActivity;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;
import com.quanjing.weitu.app.ui.user.MWTOtherUserActivity;
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
    private LinearLayout _downloadButton;
    private LinearLayout _collectButton;
    private LinearLayout _shareButton;
    private LinearLayout _commentButton;

    private TextView _serialTextView;
    private TextView _captionTextView;
    private TextView _moreTextVIew;
    private ListView _commentListView;
    private TextView _likeTextView;
    private GridView _likeGridView;
    private MWTAsset _asset;
    private GridViewAdapter likeGridViewAdapter;

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

        _downloadButton = (LinearLayout) findViewById(R.id.DownloadButton);
        _collectButton = (LinearLayout) findViewById(R.id.CollectButton);
        _shareButton = (LinearLayout) findViewById(R.id.ShareButton);
        _commentButton = (LinearLayout) findViewById(R.id.CommentButton);

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

        _commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, MWTCommentActivity.class);
                intent.putExtra(MWTAssetActivity.ARG_ASSETID, _asset.getAssetID());
                _context.startActivity(intent);
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
//                    Intent intent = new Intent();
//                    intent.putExtra("caption", _asset.getCaption());
//                    intent.putExtra("webUrl", _asset.get_webURL());
//                    intent.putExtra("image", imageUrl);
//                    intent.setClass(_context, ShowWebImageActivity.class);
//                    _context.startActivity(intent);

                    ArrayList<String> imgList = new ArrayList<String>();
                    ArrayList<String> webList = new ArrayList<String>();
                    ArrayList<String> captionList = new ArrayList<String>();
                    imgList.add(imageUrl);
                    webList.add(_asset.get_webURL());
                    captionList.add(_asset.getCaption());
                    if (_asset.getRelatedAssets() != null)
                        for (MWTAsset mwtAsset : _asset.getRelatedAssets()) {
                            imgList.add(mwtAsset.getImageInfo().url);
                            webList.add(mwtAsset.get_webURL());
                            captionList.add(mwtAsset.getCaption());
                        }
                    Intent intent = new Intent(_context, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imgList);
                    intent.putExtra("captions", captionList);
                    intent.putExtra("webs", webList);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
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
            if (userIDS != null && userIDS.length > 0) {
                _likeTextView.setVisibility(View.GONE);
                likeGridViewAdapter = new GridViewAdapter(userIDS);
                _likeGridView.setNumColumns(6);
                _likeGridView.setAdapter(likeGridViewAdapter);
            } else {
                _likeTextView.setVisibility(View.VISIBLE);
            }

            MWTRestManager restManager = MWTRestManager.getInstance();
            MWTCommentService commentService = restManager.create(MWTCommentService.class);
            commentService.getComments(asset.getAssetID(), new Callback<MWTCommentResult>() {
                @Override
                public void success(MWTCommentResult result, Response response) {
                    if (result != null && result.error != null) {
                        Toast.makeText(_context, "获取数据错误", 1).show();
                        return;
                    }
                    if (result != null) {
                        List<MWTCommentData> commentDataList = result.comments;
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
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

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
                    MWTUserManager.getInstance().getCurrentUser().markDataDirty();
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
                                if (caption != null && !caption.equals("")) {
                                    oks.setTitle(_asset.getCaption());
                                } else {
                                    oks.setTitle("全景网");
                                }

                                oks.setImagePath(outputFilePath);
                                if (_asset.get_webURL() != null && !_asset.get_webURL().equals("")) {
                                    if (_asset.get_webURL().indexOf("zone.quanjing.com") == -1) {
                                        oks.setUrl(_asset.get_webURL());
                                    }
                                }

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
            final ViewHolder holder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_comment_min, null);
            holder.imageView = (XCRoundImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);

            final MWTCommentData commentData = commentDataList.get(position);
            MWTUser u = (MWTUserManager.getInstance().getUserByID(commentData.userID));
            if (u != null) {
                holder.textView.setText(u.getNickname());
                holder.contentTextView.setText(commentData.content);
                Picasso.with(context)
                        .load(u.getAvatarImageInfo().url).resize(160, 120)
                        .into(holder.imageView);
            } else {
                UserLoader userLoader = new UserLoader();
                userLoader.fetchUserByID(commentData.userID, new UserLoader.UserCallBack() {
                    @Override
                    public void success(MWTUserData userData) {
                        holder.textView.setText(userData.nickname);
                        holder.contentTextView.setText(commentData.content);
                        Picasso.with(context)
                                .load(userData.avatarImageInfo.url).resize(160, 120)
                                .into(holder.imageView);
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            XCRoundImageView imageView;
            TextView textView;
            TextView contentTextView;
        }
    }

    /**
     * 点赞人的头像
     */
    private class GridViewAdapter extends BaseAdapter {
        private String[] userIds;

        public GridViewAdapter(String[] userIds) {
            this.userIds = userIds;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(_context, R.layout.item_likeuser_image, null);
            final ImageView imageview = (ImageView) convertView.findViewById(R.id.avatar);
            final String userID = userIds[position];
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(_context, MWTOtherUserActivity.class);
                    intent.putExtra("userID", userID);
                    _context.startActivity(intent);
                }
            });
            LikedUserImageLoader imageLoader = new LikedUserImageLoader();
            imageLoader.fetchLikedUserImageUrl(userID, new LikedUserImageLoader.LikerCallBack() {
                @Override
                public void success(String imageUrl) {
                    Picasso.with(_context)
                            .load(imageUrl)
                            .into(imageview);
                }
            });

            return convertView;
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
            return userIds.length;
        }
    }
}
