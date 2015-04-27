package com.quanjing.weitu.app.ui.community;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTFellowshipInfo;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTSquareFeed;
import com.quanjing.weitu.app.model.MWTSquareFeedManager;
import com.quanjing.weitu.app.model.MWTTalent;
import com.quanjing.weitu.app.model.MWTTalentManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.MWTError;


import com.quanjing.weitu.app.protocol.MWTFeedItemData;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTFeedRefreshResult;
import com.quanjing.weitu.app.protocol.service.MWTSquareService;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.asset.MWTCommentActivity;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;
import com.quanjing.weitu.app.ui.user.MWTOtherUserActivity;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTNewCommunityAdapter extends BaseAdapter {
    private Context context;
    private List<MWTTalent> talentList;
    private Button btn_attention;
    private RelativeLayout btn_favorate;
    private HashMap<String, MWTUser> userHashMap = new HashMap<>();

    private static int COUNT = 20;

    public MWTNewCommunityAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingTalents(1);
    }

    @Override
    public int getCount() {
        return getTalentsCount();
    }

    @Override
    public Object getItem(int index) {
        return getTalent(index);
    }

    @Override
    public long getItemId(int index) {
        return getTalent(index).getAsset().getAssetID().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();

        convertView = View.inflate(context, R.layout.item_talent, null);
        holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        holder.tv_follow = (TextView) convertView.findViewById(R.id.tv_time);
        holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
        holder.imageview = (DynamicHeightImageView) convertView.findViewById(R.id.imageview);
        holder.likeGridView = (GridView) convertView.findViewById(R.id.likeGridView);
        holder.likenum = (TextView) convertView.findViewById(R.id.likenum);
        holder.commentListView = (ListView) convertView.findViewById(R.id.commentListView);
        holder.moreTextVIew = (TextView) convertView.findViewById(R.id.moreText);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int num = (context.getResources().getDisplayMetrics().widthPixels - 500) / 40;
        holder.likeGridView.setNumColumns(5);

        btn_attention = (Button) convertView.findViewById(R.id.btn_attention);
        LinearLayout btn_like = (LinearLayout) convertView.findViewById(R.id.btn_like);
        btn_favorate = (RelativeLayout) convertView.findViewById(R.id.btn_favorate);
        LinearLayout btn_comment = (LinearLayout) convertView.findViewById(R.id.btn_comment);
        LinearLayout btn_share = (LinearLayout) convertView.findViewById(R.id.btn_share);

        final MWTTalent talent = getTalent(position);
        holder.tv_title.setText(talent.getNickname());
        if (talent.getAsset() != null) {
            String caption = talent.getAsset().getCaption();
            if (caption.equals("")) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(talent.getAsset().getCaption());
            }
        }
        // 使用ImageLoader加载网络图片
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .showImageOnFail(R.drawable.ic_launcher) // 设置加载失败的默认图片
                .cacheInMemory(true) // 内存缓存
                .cacheOnDisk(true) // sdcard缓存
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置最低配置
                .build();//
        ImageLoader.getInstance().displayImage(talent.getAvatarImageInfo().url, holder.iv_avatar, options);
        final String userID = talent.getUserID();
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MWTOtherUserActivity.class);
                intent.putExtra("userID", userID);
                context.startActivity(intent);
            }
        });
        // MWTAsset asset = talent.getAssetsInfo();
        String imageUrl = "";
        if (talent.getAsset() != null) {
            imageUrl = talent.getAsset().getImageInfo().url;
        }
        if (imageUrl == null || imageUrl.equals("")) { // 没有图片资源就隐藏ImageView
            holder.imageview.setVisibility(View.GONE);
        } else {
            float heightRatio = (float) talent.getAsset().getImageInfo().height / (float) talent.getAsset().getImageInfo().width;
//            if (heightRatio < 1)
            holder.imageview.setHeightRatio(heightRatio);
            Picasso.with(context)
                    .load(imageUrl)
                    .into(holder.imageview);
        }
        // 点击，查看大图
        final MWTAsset asset = (MWTAsset) talent.getAsset();
        final String follwing_user_id = talent.getUserID();
        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MWTAssetActivity.class);
                intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                context.startActivity(intent);
            }
        });

        // 粉丝
        FollowShipLoader loader = new FollowShipLoader();
        loader.fetchFollowShip(userID, new FollowShipLoader.FollowCallBack() {
            @Override
            public void success(int num) {
                holder.tv_follow.setText("粉丝 " + num);
            }
        });
        // 喜欢该照片人的头像
        final MWTUserManager userManager = MWTUserManager.getInstance();
        if (asset != null) {
            String[] userIDS = asset.getLikedUserIDs();
            if (userIDS != null && userIDS.length > 0) {
                holder.likenum.setText(userIDS.length + "");
                GridViewAdapter gridViewAdapter = new GridViewAdapter(userIDS);
                holder.likeGridView.setAdapter(gridViewAdapter);
            } else {
                holder.likenum.setVisibility(View.GONE);
            }
        }

        // 针对该图片的评论
        if (asset != null) {
            String serial = asset.getAssetID();
            if (serial != null && !serial.isEmpty()) {
                holder.commentListView.setVisibility(View.VISIBLE);
            } else {
                holder.commentListView.setVisibility(View.GONE);
            }
            List<MWTCommentData> commentDataList = asset.get_latestComments();
            if (commentDataList.size() > 0) {
                holder.moreTextVIew.setText("查看所有评论");
                holder.moreTextVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MWTCommentActivity.class);
                        intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                        context.startActivity(intent);
                    }
                });
                holder.commentListView.setAdapter(new CommentAdapter(context, commentDataList));

            } else {
                holder.moreTextVIew.setVisibility(View.GONE);
            }
        } else {
            holder.commentListView.setVisibility(View.GONE);
        }


        MWTUser cUser = userManager.getCurrentUser();
        if (asset != null && cUser != null) {
            String[] likedUsers = asset.getLikedUserIDs();
            if (isContain(likedUsers, cUser.getUserID())) {
                btn_favorate.setBackgroundResource(R.drawable.ic_talent_faverated);
            } else {
                btn_favorate.setBackgroundResource(R.drawable.ic_talent_faverate);
            }
        }

        if (cUser != null && cUser.getmwtFellowshipInfo().get_followingUserIDs().size() > 0) {
            if (cUser.getmwtFellowshipInfo().get_followingUserIDs().contains(talent.getUserID()))
                btn_attention.setText("取消");
            else
                btn_attention.setText("关注");
        }

        btn_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userManager.getCurrentUser() != null) {
                    if (userManager.getCurrentUser().getmwtFellowshipInfo().get_followingUserIDs().contains(talent.getUserID()))
                        cancelAttention(follwing_user_id);
                    else
                        addAttention(follwing_user_id, talent, position);
                }
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MWTUserManager userManager = MWTUserManager.getInstance();
                MWTUser cUser = userManager.getCurrentUser();
                if (asset != null && cUser != null) {
                    String[] likedUsers = asset.getLikedUserIDs();
                    if (isContain(likedUsers, cUser.getUserID())) {
                        cancelFavotite(asset.getAssetID(), talent, position);
                    } else {
                        addFavotite(asset.getAssetID(), talent, position);
                    }
                }

            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MWTCommentActivity.class);
                intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                context.startActivity(intent);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performShare(asset);
            }
        });

        return convertView;
    }

    private boolean isContain(String[] strs, String id) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] != null && strs[i].equals(id)) {//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
                return true;//查找到了就返回真，不在继续查询
            }
        }
        return false;
    }

    /**
     * 获取粉丝数
     *
     * @param userID
     * @param callback
     */
    public void fetchFollowShip(final String userID, final MWTCallback callback) {
        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryUserPublicInfo(userID,
                new Callback<MWTUserResult>() {
                    @Override
                    public void success(MWTUserResult result, Response response) {
                        callback.success();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
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
            convertView = View.inflate(context, R.layout.item_likeuser_image, null);
            final ImageView imageview = (ImageView) convertView.findViewById(R.id.avatar);
            final String userID = userIds[position];
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MWTOtherUserActivity.class);
                    intent.putExtra("userID", userID);
                    context.startActivity(intent);
                }
            });
            LikedUserImageLoader imageLoader = new LikedUserImageLoader();
            imageLoader.fetchLikedUserImageUrl(userID, new LikedUserImageLoader.LikerCallBack() {
                @Override
                public void success(String imageUrl) {
                    Picasso.with(context)
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

    /**
     * 评论adapter
     */
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

    /**
     * 添加喜欢
     *
     * @param assetid
     */

    private void addFavotite(String assetid, final MWTTalent talent, final int position) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用喜欢功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MWTAuthSelectActivity.class);
                            context.startActivity(intent);
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
        final String uid = userManager.getCurrentUser().getUserID();
        SVProgressHUD.showInView(context, "请稍后...", true);
        userService.addFavorite(assetid, "like", new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                SVProgressHUD.dismiss(context);
                // 更新item
                MWTTalent newTalent = new MWTTalent();
                MWTAsset newAsset = new MWTAsset();
                newAsset.setImageInfo(talent.getAsset().getImageInfo());
                newAsset.setCaption(talent.getAsset().getCaption());
                newAsset.setAssetID(talent.getAsset().getAssetID());
                newAsset.set_createTime(talent.getAsset().get_createTime());
                newAsset.set_latestComments(talent.getAsset().get_latestComments());
                newAsset.setOwnerUserID(talent.getAsset().getOwnerUserID());
                newAsset.setCommentNum(talent.getAsset().getCommentNum());
                String[] newLikedUserIDs = new String[talent.getAsset().getLikedUserIDs().length + 1];
                if (talent.getAsset().getLikedUserIDs().length > 0) {
                    for (int i = 0; i < talent.getAsset().getLikedUserIDs().length; i++) {
                        newLikedUserIDs[i] = talent.getAsset().getLikedUserIDs()[i];
                    }
                }
                newLikedUserIDs[talent.getAsset().getLikedUserIDs().length] = uid;
                newAsset.setLikedUserIDs(newLikedUserIDs);
                newTalent.setAsset(newAsset);
                newTalent.setUserID(talent.getUserID());
                newTalent.setAvatarImageInfo(talent.getAvatarImageInfo());
                newTalent.setFellowshipInfo(talent.getFellowshipInfo());
                newTalent.setNickname(talent.getNickname());
                newTalent.setPrivateInfo(talent.getPrivateInfo());
                newTalent.setSignature(talent.getSignature());
                talentList.set(position, newTalent);
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(context);
                Toast.makeText(context, "点赞失败", 500).show();
            }
        });

    }

    /**
     * 取消喜欢
     *
     * @param assetid
     */

    private void cancelFavotite(String assetid, final MWTTalent talent, final int position) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用喜欢功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MWTAuthSelectActivity.class);
                            context.startActivity(intent);
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
        final String uid = userManager.getCurrentUser().getUserID();
        SVProgressHUD.showInView(context, "请稍后...", true);
        userService.cancelFavorite(assetid, "unlike", new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                SVProgressHUD.dismiss(context);
                // 更新item
                MWTTalent newTalent = new MWTTalent();
                MWTAsset newAsset = new MWTAsset();
                newAsset.setImageInfo(talent.getAsset().getImageInfo());
                newAsset.setCaption(talent.getAsset().getCaption());
                newAsset.setAssetID(talent.getAsset().getAssetID());
                newAsset.set_createTime(talent.getAsset().get_createTime());
                newAsset.set_latestComments(talent.getAsset().get_latestComments());
                newAsset.setOwnerUserID(talent.getAsset().getOwnerUserID());
                newAsset.setCommentNum(talent.getAsset().getCommentNum());

                ArrayList<String> temp = new ArrayList<String>();
                if (talent.getAsset() != null && talent.getAsset().getLikedUserIDs().length > 0) {
                    for (int i = 0; i < talent.getAsset().getLikedUserIDs().length; i++) {
                        MWTAsset asset = talent.getAsset();
                        if (talent.getAsset().getLikedUserIDs()[i] != null && !talent.getAsset().getLikedUserIDs()[i].equals(uid))
                            temp.add(talent.getAsset().getLikedUserIDs()[i]);
                    }
                }
                String[] newLikedUserIDs = new String[temp.size()];
                for (int j = 0; j < temp.size(); j++) {
                    newLikedUserIDs[j] = temp.get(j);
                }
                newAsset.setLikedUserIDs(newLikedUserIDs);
                newTalent.setAsset(newAsset);
                newTalent.setUserID(talent.getUserID());
                newTalent.setAvatarImageInfo(talent.getAvatarImageInfo());
                newTalent.setFellowshipInfo(talent.getFellowshipInfo());
                newTalent.setNickname(talent.getNickname());
                newTalent.setPrivateInfo(talent.getPrivateInfo());
                newTalent.setSignature(talent.getSignature());
                talentList.set(position, newTalent);
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(context);
                Toast.makeText(context, "点赞失败", 500).show();
            }
        });

    }

    private void refreshCurrentUser() {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        MWTUserManager.getInstance().refreshCurrentUserInfo(new MWTCallback() {
            @Override
            public void success() {
                SVProgressHUD.dismiss(context);
                notifyDataSetChanged();
            }

            @Override
            public void failure(MWTError error) {
            }
        });
    }


    /**
     * 关注
     *
     * @param userid
     */
    private void addAttention(final String userid, final MWTTalent talent, final int position) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MWTAuthSelectActivity.class);
                            context.startActivity(intent);
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
        SVProgressHUD.showInView(context, "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "follow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(context);
                Toast.makeText(context, "关注失败", 500).show();
            }
        });

    }

    private void cancelAttention(String userid) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MWTAuthSelectActivity.class);
                            context.startActivity(intent);
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
        SVProgressHUD.showInView(context, "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "unfollow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(context);
                Toast.makeText(context, "取消失败", 500).show();
            }
        });

    }

    /**
     * 分享
     *
     * @param _asset
     */

    private void performShare(final MWTAsset _asset) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(context)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用分享功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MWTAuthSelectActivity.class);
                            context.startActivity(intent);
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

            SVProgressHUD.showInView(context, "分享中，请稍候...", true);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(imageURL, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {

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
                                String appName = context.getApplicationInfo().name;
                                oks.setNotification(0, appName);

                                String caption = _asset.getCaption();
                                if (caption != null&&!caption.equals("")) {
                                    oks.setTitle(_asset.getCaption());
                                }else {
                                    oks.setTitle("全景网——全景社区");
                                }
                                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                                //oks.setTitleUrl(contentUrl);
                                // text是分享文本，所有平台都需要这个字段
                                //oks.setText("123");

                                oks.setImagePath(outputFilePath);
//                                if (_asset.get_webURL() != null && !_asset.get_webURL().equals(""))
//                                    oks.setUrl(_asset.get_webURL());
                                oks.setSilent(false);

                                // 令编辑页面显示为Dialog模式
                                oks.setDialogMode();

                                // 在自动授权时可以禁用SSO方式
                                oks.disableSSOWhenAuthorize();

                                // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                                oks.show(context);
                            } else {
                                SVProgressHUD.showInViewWithoutIndicator(context, "分享失败：无法写入文件", 1);
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
                    SVProgressHUD.showInViewWithoutIndicator(context, "分享失败：无法获取文件", 1);
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }

    /**
     * 屏蔽list 的item点击事件
     *
     * @return
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public int getTalentsCount() {
        if (talentList != null) {
            return talentList.size();
        } else {
            return 0;
        }
    }

    public MWTTalent getTalent(int index) {
        if (talentList != null) {
            return talentList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final MWTCallback callback) {

        MWTTalentManager cm = MWTTalentManager.getInstance();
        cm.refreshTalents2(COUNT, Long.MAX_VALUE, new MWTCallback() {
            @Override
            public void success() {
                updatePresentingTalents(1);
                notifyDataSetChanged();
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(MWTError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public void refreshIfNeeded() {
        if (talentList == null || talentList.isEmpty()) {
            refresh(null);
        }
    }

    public void loadmore(final MWTCallback callback) {
        MWTTalentManager cm = MWTTalentManager.getInstance();
        if(talentList.size()>0){
            long max_item_timestamp = Long.parseLong(talentList.get(talentList.size() - 1).getAsset().get_createTime());

            cm.refreshTalents2(COUNT, max_item_timestamp, new MWTCallback() {
                @Override
                public void success() {
                    updatePresentingTalents(2);
                    notifyDataSetChanged();
                    if (callback != null) {
                        callback.success();
                    }
                }

                @Override
                public void failure(MWTError error) {
                    if (callback != null) {
                        callback.failure(error);
                    }
                }
            });
        }
    }


    private void updatePresentingTalents(int type) {
        if (type == 1) {
            talentList = new ArrayList<MWTTalent>();
            talentList.addAll(MWTTalentManager.getInstance().getTalents());
        } else {
            talentList.addAll(MWTTalentManager.getInstance().getTalents());
        }
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_follow;
        private TextView tv_content;
        private DynamicHeightImageView imageview;
        private GridView likeGridView;
        private TextView likenum;
        private ListView commentListView;
        private TextView moreTextVIew;
    }
}
