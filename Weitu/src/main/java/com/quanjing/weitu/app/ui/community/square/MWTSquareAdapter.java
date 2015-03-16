package com.quanjing.weitu.app.ui.community.square;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.model.MWTSquareFeed;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.squareup.picasso.Picasso;

public class MWTSquareAdapter extends BaseAdapter {
    private Context _context;
    private MWTSquareFeed _feed;

    public MWTSquareAdapter(Context context, MWTSquareFeed feed) {
        _context = context;
        _feed = feed;
    }

    public MWTSquareFeed getFeed() {
        return _feed;
    }

    public void setFeed(MWTSquareFeed feed) {
        if (feed != _feed) {
            _feed = feed;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return getAssetCount();
    }

    @Override
    public Object getItem(int position) {
        return getAsset(position);
    }

    @Override
    public long getItemId(int position) {
        return getAsset(position).getAssetID().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(_context, R.layout.item_grid_square, null);
        DynamicHeightImageView imageView = (DynamicHeightImageView) convertView.findViewById(R.id.item_pic);
        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView likeNum = (TextView) convertView.findViewById(R.id.likeUserNum);
        TextView captionText = (TextView) convertView.findViewById(R.id.captionText);
        TextView profileText = (TextView) convertView.findViewById(R.id.profileText);
        XCRoundImageView headImage = (XCRoundImageView) convertView.findViewById(R.id.img_head);

        final MWTAsset asset = getAsset(position);
        MWTUser user = MWTUserManager.getInstance().getUserByID(asset.getOwnerUserID());
        nameText.setText(user.getNickname());
        profileText.setText("个人资料");
        MWTImageInfo headInfo = user.getAvatarImageInfo();
        if (user.getAvatarImageInfo() != null && user.getAvatarImageInfo().url != null && !user.getAvatarImageInfo().url.equals("")) {
            Picasso.with(_context)
                    .load(user.getAvatarImageInfo().url)
                    .placeholder(new ColorDrawable(Color.WHITE))
                    .into(headImage);
        }
        if (asset != null) {
            if (asset.getCaption().equals("")) {
                captionText.setVisibility(View.GONE);
            } else {
                captionText.setVisibility(View.VISIBLE);
                captionText.setText(asset.getCaption());
            }
            if (asset.getLikedUserIDs().length > 0) {
                likeNum.setText(asset.getCommentNum().length() + "");
            } else {
                likeNum.setText("0");
            }
            MWTImageInfo imageInfo = asset.getImageInfo();
            if (imageInfo != null) {
                float heightRatio = (float) imageInfo.height / (float) imageInfo.width;
                int color = Color.WHITE;
                try {
                    color = Color.parseColor("#" + imageInfo.primaryColorHex);
                } catch (RuntimeException ex) {
                }
                imageView.setHeightRatio(heightRatio);
                Picasso.with(_context)
                        .load(imageInfo.url)
                        .resize(640, (int) (640 * heightRatio))
                        .placeholder(new ColorDrawable(color))
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(_context, MWTAssetActivity.class);
                        intent.putExtra(MWTAssetActivity.ARG_ASSETID, asset.getAssetID());
                        _context.startActivity(intent);
                    }
                });
            }
        }

        return convertView;
    }

    private int getAssetCount() {
        if (_feed != null) {
            return _feed.getItemNum();
        } else {
            return 0;
        }
    }

    private MWTAsset getAsset(int index) {
        if (_feed != null) {
            return _feed.getAsset(index);
        } else {
            return null;
        }
    }
}

