package com.quanjing.weitu.app.ui.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTTalent;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.photo.AlbumHelper;
import com.quanjing.weitu.app.ui.photo.ImageBucket;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-23.
 */
public class MWTOtherUserHeader extends FrameLayout {


    private Context _context;

    private CircularImageView _avatarImageView;

    private TextView _nicknameTextView, _attentionTextView;

    private Button _uploadedButton;
    private Button _likedButton;
    private Button _downloadedButton;
    private Button _sharedButton;

    private MWTUser _user;

    private MWTOtherUserActivity _otherUserActivity;


    public MWTOtherUserHeader(MWTOtherUserActivity otherUserActivity) {
        super(otherUserActivity);
        construct(otherUserActivity);
    }

    private void construct(MWTOtherUserActivity otherUserActivity) {
        _otherUserActivity = otherUserActivity;
        _context = _otherUserActivity;
        LayoutInflater.from(_context).inflate(R.layout.view_otheruser_header, this, true);
        setupViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews() {
        _avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        _nicknameTextView = (TextView) findViewById(R.id.NicknameTextView);
        _attentionTextView = (TextView) findViewById(R.id.attendTextView);

        _uploadedButton = (Button) findViewById(R.id.uploadedButton);
        _uploadedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, MWTImageFlowActivity.class);
                intent.putExtra("type", 1);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
            }
        });

        _likedButton = (Button) findViewById(R.id.LikedButton);
        _likedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, MWTImageFlowActivity.class);
                intent.putExtra("type", 3);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
            }
        });

        _downloadedButton = (Button) findViewById(R.id.DownloadedButton);
        _downloadedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //_userFragment.onDownloadedButtonClicked(); // 原为下载，现为关注
                Intent intent = new Intent(_context, MWTFollowingsActivity.class);
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
                //_userFragment.onSharedButtonClicked(); // 原为分享，现为粉丝
                Intent intent = new Intent(_context, MWTFollowerActivity.class);
                if (_user != null) {
                    intent.putExtra("userID", _user.getUserID());
                    _context.startActivity(intent);
                }
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
                        Intent intent = new Intent();
                        intent.putExtra("image", imageUrl);
                        intent.putExtra("ratio", ratio + "");
                        intent.setClass(_context, MWTAvatarActivity.class);
                        _context.startActivity(intent);
                    }
                });
            }
        } else {
            _avatarImageView.setImageDrawable(null);
        }
    }

    private void updateTextViews() {
        if (_user != null) {
            _nicknameTextView.setVisibility(View.VISIBLE);
            _uploadedButton.setVisibility(View.VISIBLE);
            _likedButton.setVisibility(View.VISIBLE);
            _sharedButton.setVisibility(View.VISIBLE);
            _downloadedButton.setVisibility(View.VISIBLE);

            _nicknameTextView.setText(_user.getNickname());
            _uploadedButton.setText(_user.getAssetsInfo().getPublicAssetNum()  + "\n照片");
            _likedButton.setText(_user.getAssetsInfo().getLikedAssetNum() + "\n喜欢");
            _downloadedButton.setText(_user.getmwtFellowshipInfo().get_followingNum() + "\n关注"); // 原下载
            _sharedButton.setText(_user.getmwtFellowshipInfo().get_followerNum() + "\n粉丝");  //原分享
        } else {
            _nicknameTextView.setText("");
            _uploadedButton.setText("");
            _sharedButton.setText("");
            _downloadedButton.setText("");
            _likedButton.setText("");

            _nicknameTextView.setVisibility(View.INVISIBLE);
            _uploadedButton.setVisibility(View.INVISIBLE);
            _sharedButton.setVisibility(View.INVISIBLE);
            _downloadedButton.setVisibility(View.INVISIBLE);
            _likedButton.setVisibility(View.INVISIBLE);
        }
        final MWTUserManager userManager = MWTUserManager.getInstance();
        MWTUser cUser = userManager.getCurrentUser();
        if (_user != null && cUser != null) {
            if (cUser.getmwtFellowshipInfo().get_followingUserIDs().contains(_user.getUserID()))
                _attentionTextView.setText("已关注");
            else
                _attentionTextView.setText("＋关注");
        }
        _attentionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userManager.getCurrentUser() != null) {
                    if (userManager.getCurrentUser().getmwtFellowshipInfo().get_followingUserIDs().contains(_user.getUserID()))
                        cancelAttention(_user.getUserID());
                    else
                        addAttention(_user.getUserID());
                }
            }
        });
    }

    /**
     * 关注
     *
     * @param userid
     */
    private void addAttention(final String userid) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(_context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
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

        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTUserManager userManager = MWTUserManager.getInstance();
        MWTUserService userService = restManager.create(MWTUserService.class);
        MWTUser cUser = userManager.getCurrentUser();
        if (cUser == null)
            return;
        SVProgressHUD.showInView(_context, "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "follow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
                SVProgressHUD.dismiss(_context);
                _attentionTextView.setText("已关注");
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(_context);
                Toast.makeText(_context, "关注失败", 500).show();
            }
        });

    }

    private void cancelAttention(String userid) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(_context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
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

        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTUserManager userManager = MWTUserManager.getInstance();
        MWTUserService userService = restManager.create(MWTUserService.class);
        MWTUser cUser = userManager.getCurrentUser();
        if (cUser == null)
            return;
        SVProgressHUD.showInView(_context, "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "unfollow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
                SVProgressHUD.dismiss(_context);
                _attentionTextView.setText("＋关注");
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(_context);
                Toast.makeText(_context, "取消失败", 500).show();
            }
        });

    }

    private void refreshCurrentUser() {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        MWTUserManager.getInstance().refreshCurrentUserInfo(new MWTCallback() {
            @Override
            public void success() {
            }

            @Override
            public void failure(MWTError error) {
            }
        });
    }
}

