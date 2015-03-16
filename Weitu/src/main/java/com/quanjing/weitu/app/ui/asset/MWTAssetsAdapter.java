package com.quanjing.weitu.app.ui.asset;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.squareup.picasso.Picasso;

public abstract class MWTAssetsAdapter extends BaseAdapter {
    private Context _context;

    protected MWTAssetsAdapter(Context context) {
        _context = context;
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
        DynamicHeightImageView imageView = (DynamicHeightImageView) convertView;
        if (imageView == null) {
            imageView = new DynamicHeightImageView(_context);
        }

        MWTAsset asset = getAsset(position);
        if (asset != null) {
            MWTImageInfo imageInfo = asset.getImageInfo();
            if (imageInfo != null) {
                float heightRatio = (float) imageInfo.height / (float) imageInfo.width;
                int color = Color.WHITE;
                try {
                    color = Color.parseColor("#" + imageInfo.primaryColorHex);
                } catch (RuntimeException ex) {
                }
                imageView.setHeightRatio(heightRatio);
                if (imageInfo.smallURL != null)
                    Picasso.with(_context)
                            .load(imageInfo.smallURL)
                            .resize(640, (int) (640 * heightRatio))
                            .placeholder(new ColorDrawable(color))
                            .into(imageView);
            }
        }

        return imageView;
    }

    public abstract int getAssetCount();

    public abstract MWTAsset getAsset(int index);
}
