package com.quanjing.weitu.app.ui.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.ui.found.ShowWebImageActivity;
import com.quanjing.weitu.app.ui.photo.AlbumHelper;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageBucket;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MWTUserHeaderView extends FrameLayout {
    private Context _context;

    private CircularImageView _avatarImageView;

    private TextView _nicknameTextView;

    private Button _localButton;
    //    private Button _collectedButton;
    private Button _uploadedButton;
    //    private Button _likedButton;
    private Button _downloadedButton;
    private Button _sharedButton;

    private Button _actionButton;

    private MWTUser _user;

    private MWTUserMeFragment _userFragment;

    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    private ArrayList<ImageItem> dataList;

    public MWTUserHeaderView(MWTUserMeFragment userFragment) {
        super(userFragment.getActivity());
        construct(userFragment);
    }

    private void construct(MWTUserMeFragment userFragment) {
        _userFragment = userFragment;
        _context = userFragment.getActivity();
        LayoutInflater.from(_context).inflate(R.layout.view_user_header, this, true);
        init();
        setupViews();
    }

    // 初始化，给一些对象赋值
    private void init() {
        AlbumHelper albumHelper = new AlbumHelper();
        helper = albumHelper.getHelper();
        helper.init(_context.getApplicationContext());
        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews() {
        _avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        _nicknameTextView = (TextView) findViewById(R.id.NicknameTextView);

        _localButton = (Button) findViewById(R.id.localButton);
        _localButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                Intent intent = new Intent(_context,
                        LocalAlbumActivity.class);
                _context.startActivity(intent);
            }
        });

//        _collectedButton = (Button) findViewById(R.id.CollectedButton);
//        _collectedButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _userFragment.onCollectedButtonClicked();
//            }
//        });

        _uploadedButton = (Button) findViewById(R.id.uploadedButton);
        _uploadedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                _userFragment.onUploadedButtonClicked();
                Intent intent = new Intent(_context, MWTImageFlowActivity.class);
                intent.putExtra("type", 1);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
            }
        });

//        _likedButton = (Button) findViewById(R.id.LikedButton);
//        _likedButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _userFragment.onLikedButtonClicked();
//            }
//        });

        _downloadedButton = (Button) findViewById(R.id.DownloadedButton);
        _downloadedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //_userFragment.onDownloadedButtonClicked();
                Intent intent = new Intent(_context, MWTImageFlowActivity.class);
                intent.putExtra("type", 2);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
            }
        });

        _sharedButton = (Button) findViewById(R.id.SharedButton);
        _sharedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //_userFragment.onSharedButtonClicked();
                Intent intent = new Intent(_context, UserSquareActivity.class);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
            }
        });

        _actionButton = (Button) findViewById(R.id.ActionButton);
        _actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _userFragment.onEditUserInfo();
            }
        });
    }

    public void setUser(MWTUser user) {
        _user = user;
        updateAvatarImageView();
        updateTextViews();
    }

    private void updateAvatarImageView() {
        MWTImageInfo imageInfo = null;
        if (_user != null) {
            imageInfo = _user.getAvatarImageInfo();
        }

        if (imageInfo != null) {
            int color = Color.WHITE;
            try {
                color = Color.parseColor("#" + imageInfo.primaryColorHex);
            } catch (RuntimeException ignored) {

            }
            int width = _avatarImageView.getWidth();
            width = Math.min(imageInfo.width, width);
            if (width <= 0) {
                width = 150;
            }
            Picasso.with(_context)
                    .load(imageInfo.url)
                    .resize(width, width)
                    .centerCrop()
                    .placeholder(new ColorDrawable(color))
                    .into(_avatarImageView);
            final String imageUrl = imageInfo.url;
            final double ratio = (imageInfo.height * 1.0) / (imageInfo.width * 1.0);
            if (imageUrl != null && !imageUrl.equals("")) {
                _avatarImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent();
//                        intent.putExtra("image", imageUrl);
//                        intent.putExtra("ratio", ratio + "");
//                        intent.setClass(_context, MWTAvatarActivity.class);
//                        _context.startActivity(intent);
                        _userFragment.onEditUserInfo();
                    }
                });
            }
        } else {
            _avatarImageView.setImageDrawable(null);
        }
    }

    public void updateLocalImageCount(int n) {

        _localButton.setText(String.valueOf(dataList.size() - n) + "\n本机");
    }

    private void updateTextViews() {
        if (_user != null) {
            _nicknameTextView.setVisibility(View.VISIBLE);
            _localButton.setVisibility(View.VISIBLE);
            _actionButton.setVisibility(View.VISIBLE);
            _uploadedButton.setVisibility(View.VISIBLE);
//            _collectedButton.setVisibility(View.VISIBLE);
            _sharedButton.setVisibility(View.VISIBLE);
            _downloadedButton.setVisibility(View.VISIBLE);
//            _likedButton.setVisibility(View.VISIBLE);

            _nicknameTextView.setText(_user.getNickname());
            _localButton.setText(dataList.size() + "\n本机");
            _uploadedButton.setText(_user.getAssetsInfo().getPublicAssetNum() + _user.getAssetsInfo().getPrivateAssetNum() + "\n云相册");
//            _collectedButton.setText(_user.getAssetsInfo().getCollectedAssetNum() + "\n收藏");
            _downloadedButton.setText(_user.getAssetsInfo().getLightboxNum() + "\n收藏"); // 原下载
            _sharedButton.setText(_user.getmwtFellowshipInfo().get_followerNum() + _user.getmwtFellowshipInfo().get_followingNum() + "\n圈子");  //原分享
//            _likedButton.setText(_user.getAssetsInfo().getLikedAssetNum() + "\n喜欢");
//            _sharedButton.setText(_user.getAssetsInfo().getSharedAssetNum() + "\n分享");
        } else {
            _nicknameTextView.setText("");
            _localButton.setText("");
            _uploadedButton.setText("");
//            _collectedButton.setText("");
            _sharedButton.setText("");
            _downloadedButton.setText("");
//            _likedButton.setText("");

            _nicknameTextView.setVisibility(View.INVISIBLE);
            _actionButton.setVisibility(View.INVISIBLE);
            _localButton.setVisibility(View.INVISIBLE);
            _uploadedButton.setVisibility(View.INVISIBLE);
//            _collectedButton.setVisibility(View.INVISIBLE);
            _sharedButton.setVisibility(View.INVISIBLE);
            _downloadedButton.setVisibility(View.INVISIBLE);
//            _likedButton.setVisibility(View.INVISIBLE);
        }
    }
}
