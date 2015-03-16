package com.quanjing.weitu.app.ui.community.recommend;

/**
 * Created by Administrator on 2014/12/18.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTRecommendManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.community.RecommendFragment;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MWTRecommendAdapter extends BaseAdapter {
    private Context context;
    private List<MWTUserData> userList;

    public MWTRecommendAdapter(Context context) {
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
        return getTalent(index).userID.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_circle, null);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            //holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            holder.attention = ((Button) convertView.findViewById(R.id.attention));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MWTUserData userData = getTalent(position);
        holder.tv_title.setText(userData.nickname);
        //holder.tv_content.setText("上传了" + circleItem.getAssetList().size() + "张新照片");
        holder.tv_content.setText(userData.signature);
        //头像
        Picasso.with(context)
                .load(userData.avatarImageInfo.url)
                .into(holder.iv_avatar);


//        final List<MWTAsset> assets = circleItem.getAssetList();
//        if (assets == null || assets.size() == 0) { // 没有图片资源就隐藏GridView
//            holder.gridview.setVisibility(View.GONE);
//        } else {
//            holder.gridview.setAdapter(new NoScrollGridAdapter(context, assets));
//        }
        holder.attention.setText("关注");
        holder.attention.setVisibility(View.VISIBLE);
        holder.attention.setTag(position + 1);
        holder.attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = (int) view.getTag();
                addAttention(userData.userID, p);
            }
        });
        return convertView;
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
        if (userList != null) {
            return userList.size();
        } else {
            return 0;
        }
    }

    public MWTUserData getTalent(int index) {
        if (userList != null) {
            return userList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final MWTCallback callback) {
        MWTRecommendManager cm = MWTRecommendManager.getInstance();
        cm.refreshCircles(1, RecommendFragment.COUNT, new MWTCallback() {
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

    public void loadMore(final MWTCallback callback) {
        MWTRecommendManager cm = MWTRecommendManager.getInstance();
        int page = (int) Math.ceil(userList.size() * 1.0 / RecommendFragment.COUNT);
        cm.refreshCircles(page, RecommendFragment.COUNT, new MWTCallback() {
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

    public void refreshIfNeeded() {
        if (userList == null || userList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingTalents(int type) {
        if (type == 1) {
            userList = new ArrayList<MWTUserData>();
            userList.addAll(MWTRecommendManager.getInstance().getTalents());
        } else {
            userList.addAll(MWTRecommendManager.getInstance().getTalents());
        }

    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private NoScrollGridView gridview;
        private Button attention;
    }

    /**
     * 关注
     *
     * @param userid
     */
    private void addAttention(String userid, final int position) {
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
        if (cUser == null){
            Toast.makeText(context, "当前用户信息为空", 500).show();
            return;
        }
        SVProgressHUD.showInView(context, "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "follow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                SVProgressHUD.dismiss(context);
                userList.remove(position - 1);
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(context);
                Toast.makeText(context, "关注失败", 500).show();
            }
        });

    }
}

