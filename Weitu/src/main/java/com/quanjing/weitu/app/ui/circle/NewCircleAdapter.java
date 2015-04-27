package com.quanjing.weitu.app.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTCircleLike;
import com.quanjing.weitu.app.model.MWTLabel;
import com.quanjing.weitu.app.model.MWTNewCircle;
import com.quanjing.weitu.app.model.MWTNewCircleManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTCircleComment;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAddCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTCircleService;
import com.quanjing.weitu.app.protocol.service.MWTCommentService;
import com.quanjing.weitu.app.protocol.service.MWTCricleLikeResult;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.common.ActionItem;
import com.quanjing.weitu.app.ui.common.MWTPopup;
import com.quanjing.weitu.app.ui.common.Utils;
import com.quanjing.weitu.app.ui.community.recommend.NoScrollGridAdapter;
import com.quanjing.weitu.app.ui.community.recommend.NoScrollGridView;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.quanjing.weitu.app.ui.user.MWTOtherUserActivity;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Administrator on 2015/1/6.
 */


public class NewCircleAdapter extends BaseAdapter {

    private Context context;
    private List<MWTNewCircle> circleList;
    private MWTPopup titlePopup;
    private int tempPosition;
    private String praise = "赞";

    public NewCircleAdapter(Context context) {
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
        return getCircle(index);
    }

    @Override
    public long getItemId(int index) {
        return getCircle(index).getUser().getUserID().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_circle, null);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            holder.tv_time = (TextView) convertView.findViewById(R.id.timeText);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.commentText);
            holder.tv_like = (TextView) convertView.findViewById(R.id.likeText);
            holder.lv_comment = (ListView) convertView.findViewById(R.id.commentList);
            holder.likeRL = (RelativeLayout) convertView.findViewById(R.id.likeRL);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_comment.setTag(position);
        final MWTNewCircle circleItem = getCircle(position);
        holder.tv_title.setText(circleItem.getUser().getNickname());
        holder.tv_time.setText(convert2String(circleItem.getTimestamp()));
        //头像
        Picasso.with(context)
                .load(circleItem.getUser().getAvatarImageInfo().url)
                .into(holder.iv_avatar);
        final String userID = circleItem.getUser().getUserID();
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MWTOtherUserActivity.class);
                intent.putExtra("userID", userID);
                context.startActivity(intent);
            }
        });


        final List<MWTAsset> assets = circleItem.getAssets();
        if (assets == null || assets.size() == 0) { // 没有图片资源就隐藏GridView
            String usernames = "";
            for (MWTUser user : circleItem.getSubjectUsers()) {
                usernames += (user.getNickname() + " ");
            }
            holder.tv_content.setText("关注了 " + usernames);
            holder.gridview.setVisibility(View.GONE);
        } else {
            if (circleItem.getActivityType().equals("upload")) {
                holder.tv_content.setText("上传了" + circleItem.getAssets().size() + "张新照片");
                if (circleItem.getAssets().size() == 1) {
                    holder.gridview.setNumColumns(circleItem.getAssets().size());
                } else {
                    holder.gridview.setNumColumns(3);
                }
            } else {
                if (circleItem.getSubjectUsers().get(0) != null)
                    holder.tv_content.setText("喜欢了" + circleItem.getSubjectUsers().get(0).getNickname() + "的照片");
                holder.gridview.setNumColumns(1);
            }
            holder.gridview.setVisibility(View.VISIBLE);
            holder.gridview.setAdapter(new NoScrollGridAdapter(context, assets));
        }

        if (circleItem.getCircleLikes().size() > 0) {
            holder.likeRL.setVisibility(View.VISIBLE);
            StringBuilder actionText = new StringBuilder();
            for (int i = 0; i < circleItem.getCircleLikes().size(); i++) {
                MWTCircleLike like = circleItem.getCircleLikes().get(i);
                MWTUser likeUser = MWTUserManager.getInstance().getUserByID(like.getLikeUserid());
                if (likeUser != null) {
                    String nickName = "";
                    if (i == circleItem.getCircleLikes().size() - 1)
                        nickName = " " + likeUser.getNickname();
                    else
                        nickName = " " + likeUser.getNickname() + " , ";
                    actionText
                            .append("<a style=\"text-decoration:none;\" href='" + likeUser.getUserID() + "'>"
                                    + nickName + " </a>");
                }
            }
            holder.tv_like.setText(Html.fromHtml(actionText.toString()));
            holder.tv_like.setMovementMethod(LinkMovementMethod
                    .getInstance());
            CharSequence text = holder.tv_like.getText();
            int ends = text.length();
            Spannable spannable = (Spannable) holder.tv_like.getText();
            URLSpan[] urlspan = spannable.getSpans(0, ends, URLSpan.class);
            SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(text);
            stylesBuilder.clearSpans(); // should clear old spans
            for (URLSpan url : urlspan) {
                TextViewURLSpan myURLSpan = new TextViewURLSpan(url.getURL());
                stylesBuilder.setSpan(myURLSpan, spannable.getSpanStart(url),
                        spannable.getSpanEnd(url), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.tv_like.setText(stylesBuilder);
        } else {
            holder.likeRL.setVisibility(View.GONE);
        }

        final CommentAdapter adapter = new CommentAdapter(context, circleItem.getCircleComments());
        holder.lv_comment.setDivider(null);
        holder.lv_comment.setAdapter(adapter);
        setListViewHeightBasedOnChildren(holder.lv_comment);
        holder.lv_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MWTCircleComment circleComment = (MWTCircleComment) adapter.getItem(i);
                Intent intent = new Intent("com.quanjing.sendComment");
                intent.putExtra("activityId", circleComment.getActivityId());
                intent.putExtra("replyuserid", circleComment.getUserid());
                intent.putExtra("position", position);
                context.sendBroadcast(intent);
            }
        });

        // 点击回帖九宫格，查看大图
        holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //imageBrower(position, imageUrls);
                Intent intent = new Intent(context, MWTAssetActivity.class);
                if (assets.get(position) != null) {
                    intent.putExtra(MWTAssetActivity.ARG_ASSETID, assets.get(position).getAssetID());
                    context.startActivity(intent);
                }

            }
        });

        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup = new MWTPopup(context, Utils.dip2px(context, 165), Utils.dip2px(context, 40));
                praise = "赞";
                for (MWTCircleLike liker : circleItem.getCircleLikes()) {
                    if (liker.getLikeUserid().equals(MWTUserManager.getInstance().getCurrentUser().getUserID())) {
                        praise = "取消";
                    }
                }
                titlePopup.addAction(new ActionItem(context, praise, R.drawable.circle_praise));
                titlePopup.addAction(new ActionItem(context, "评论", R.drawable.circle_comment));
                titlePopup.setItemOnClickListener(new MWTPopup.OnItemOnClickListener() {
                    @Override
                    public void onItemClick(ActionItem item, int position) {
                        if (!isNetworkConnected(context)) {
                            SVProgressHUD.showInViewWithoutIndicator(context, "网络连接失败", 2.0f);
                            return;
                        }
                        switch (position) {
                            case 0://赞
                                addLikeToActivity(circleList.get(tempPosition).getActivityID());
                                if (praise.equals("赞")) { //赞
                                    MWTCircleLike like = new MWTCircleLike();
                                    like.setActivityid(circleList.get(tempPosition).getActivityID());
                                    like.setLikeUserid(MWTUserManager.getInstance().getCurrentUser().getUserID());
                                    circleList.get(tempPosition).getCircleLikes().add(like);
                                    notifyDataSetChanged();
                                } else if (praise.equals("取消")) { //取消赞
                                    for (MWTCircleLike like : circleList.get(tempPosition).getCircleLikes()) {
                                        if (like.getActivityid().equals(circleList.get(tempPosition).getActivityID()) && like.getLikeUserid().equals((MWTUserManager.getInstance().getCurrentUser().getUserID()))) {
                                            circleList.get(tempPosition).getCircleLikes().remove(like);
                                            break;
                                        }
                                    }
                                    notifyDataSetChanged();
                                }
                                break;
                            case 1://评论
                                Intent intent = new Intent("com.quanjing.sendComment");
                                intent.putExtra("position", tempPosition);
                                intent.putExtra("activityId", circleList.get(tempPosition).getActivityID());
                                context.sendBroadcast(intent);
                                break;
                            default:
                                break;
                        }
                    }
                });

                Integer positon = (Integer) view.getTag();
                tempPosition = position;
                titlePopup.setAnimationStyle(R.style.cricleBottomAnimation);
                titlePopup.show(view);
            }
        });

        return convertView;
    }

    private class TextViewURLSpan extends ClickableSpan {
        private String clickString;

        public TextViewURLSpan(String clickString) {
            this.clickString = clickString;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.common_blue));
            ds.setUnderlineText(false); //去掉下划线
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(context, MWTOtherUserActivity.class);
            intent.putExtra("userID", clickString);
            context.startActivity(intent);
        }
    }

    private void addLikeToActivity(String activityId) {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTCircleService circleService = restManager.create(MWTCircleService.class);
        circleService.addLikeToActivity(activityId, new Callback<MWTCricleLikeResult>() {
            @Override
            public void success(MWTCricleLikeResult result, Response response) {
                if (result.error != null) {
                    return;
                }
//                if (result.success.equals("1")) { //赞
//                    MWTCircleLike like = new MWTCircleLike();
//                    like.setActivityid(circleList.get(tempPosition).getActivityID());
//                    like.setLikeUserid(MWTUserManager.getInstance().getCurrentUser().getUserID());
//                    circleList.get(tempPosition).getCircleLikes().add(like);
//                    notifyDataSetChanged();
//                } else if (result.success.equals("2")) { //取消赞
//                    for (MWTCircleLike like : circleList.get(tempPosition).getCircleLikes()) {
//                        if (like.getActivityid().equals(circleList.get(tempPosition).getActivityID()) && like.getLikeUserid().equals((MWTUserManager.getInstance().getCurrentUser().getUserID()))) {
//                            circleList.get(tempPosition).getCircleLikes().remove(like);
//                            break;
//                        }
//                    }
//                    notifyDataSetChanged();
//                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "点赞失败！", 1).show();
            }
        });
    }

    protected void updateView(String commentText, String replyuserid, String activityId, int position) {
        MWTCircleComment comment = new MWTCircleComment();
        comment.setContent(commentText);
        comment.setReplyuserid(replyuserid);
        comment.setActivityId(activityId);
        comment.setUserid(MWTUserManager.getInstance().getCurrentUser().getUserID());
        int temp = position == 0 ? tempPosition : position;
        circleList.get(temp).getCircleComments().add(comment);
        notifyDataSetChanged();
    }

    private String convert2String(long time) {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - time < 60)
            return "刚刚";
        else if ((currentTime - time) >= 60 && (currentTime - time) < 3600)
            return (currentTime - time) / 60 + "分钟前";
        else if ((currentTime - time) >= 3600 && (currentTime - time) < 86400)
            return (currentTime - time) / 3600 + "小时前";
        else
            return (currentTime - time) / 86400 + "天前";

    }

    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
//		Intent intent = new Intent(mContext, ImagePagerActivity.class);
//		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
//		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
//		mContext.startActivity(intent);
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
        if (circleList != null) {
            return circleList.size();
        } else {
            return 0;
        }
    }

    public MWTNewCircle getCircle(int index) {
        if (circleList != null) {
            return circleList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final MWTCallback callback) {
        MWTNewCircleManager cm = MWTNewCircleManager.getInstance();
        cm.refreshCircles(NewCircleFragment.COUNT, System.currentTimeMillis(), new MWTCallback() {
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
        MWTNewCircleManager cm = MWTNewCircleManager.getInstance();
        cm.refreshCircles(NewCircleFragment.COUNT, circleList.get(circleList.size() - 1).getTimestamp(), new MWTCallback() {
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
        if (circleList == null || circleList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingTalents(int type) {
        if (type == 1) {
            circleList = new ArrayList<MWTNewCircle>();
            circleList.addAll(MWTNewCircleManager.getInstance().getCircles());
        } else {
            circleList.addAll(MWTNewCircleManager.getInstance().getCircles());
        }
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private NoScrollGridView gridview;
        private TextView tv_time;
        private ImageView iv_comment;
        private ListView lv_comment;
        private TextView tv_like;
        private RelativeLayout likeRL;
    }

    private class CommentAdapter extends BaseAdapter {

        private List<MWTCircleComment> commentDataList;
        private Context context;

        public CommentAdapter(Context context, List<MWTCircleComment> commentDataList) {
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

            convertView = View.inflate(context, R.layout.item_circle_comment, null);
            holder.senderTV = (TextView) convertView.findViewById(R.id.tv_sender);
            holder.getterTV = (TextView) convertView.findViewById(R.id.tv_getter);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);

            final MWTCircleComment commentData = commentDataList.get(position);
            MWTUserManager userManager = MWTUserManager.getInstance();
            MWTUser user = userManager.getUserByID(commentData.getUserid());
            holder.senderTV.setText(user.getNickname());
            holder.senderTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MWTOtherUserActivity.class);
                    intent.putExtra("userID", commentData.getUserid());
                    context.startActivity(intent);
                }
            });
            if (commentData.getReplyuserid() != null && !commentData.getReplyuserid().equals("0") && !user.getUserID().equals(commentData.getReplyuserid())) {
                MWTUser likeUser = MWTUserManager.getInstance().getUserByID(commentData.getReplyuserid());
                if (likeUser != null) {
                    SpannableString ss = new SpannableString("回复 " + likeUser.getNickname() + ":");
                    ss.setSpan(new ForegroundColorSpan(Color.rgb(0, 144, 255)), 3, 3 + likeUser.getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.getterTV.setText(ss);
                    holder.getterTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MWTOtherUserActivity.class);
                            intent.putExtra("userID", commentData.getReplyuserid());
                            context.startActivity(intent);
                        }
                    });
                }
            } else {
                holder.getterTV.setText(":");
            }
            holder.contentTextView.setText(commentData.getContent());
            return convertView;
        }

        private class ViewHolder {
            TextView senderTV;
            TextView getterTV;
            TextView contentTextView;
        }
    }

    /**
     * 根据listview内容设置其高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}


