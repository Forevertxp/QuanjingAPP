package com.quanjing.weitu.app.ui.category;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.squareup.picasso.Picasso;

public abstract class MWTCategoriesAdapter extends BaseAdapter
{
    private Context _context;

    protected MWTCategoriesAdapter(Context context)
    {
        _context = context;
    }

    @Override
    public int getCount()
    {
        return getCategoriesCount();
    }

    @Override
    public Object getItem(int index)
    {
        return getCategory(index);
    }

    @Override
    public long getItemId(int index)
    {
        return getCategory(index).getCategoryID().hashCode();
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent)
    {
        DynamicHeightImageView imageView = null;
        if (convertView instanceof DynamicHeightImageView)
        {
            imageView = (DynamicHeightImageView) convertView;
        }

        if (imageView == null)
        {
            imageView = new DynamicHeightImageView(_context);
        }

        MWTCategory category = getCategory(index);
        if (category != null)
        {
            MWTImageInfo imageInfo = category.getCoverImageInfo();
            if (imageInfo != null)
            {
                float heightRatio = (float) imageInfo.height / (float) imageInfo.width;
                int color = Color.WHITE;
                try
                {
                    color = Color.parseColor("#" + imageInfo.primaryColorHex);
                }
                catch(RuntimeException ignored)
                {

                }
                imageView.setHeightRatio(heightRatio);
                String url = imageInfo.url;
                Picasso.with(_context)
                       .load(url)
                       .resize(640, (int) (640 * heightRatio))
                       .placeholder(new ColorDrawable(color))
                       .into(imageView);
            }
        }

        return imageView;
    }

    public abstract int getCategoriesCount();

    public abstract MWTCategory getCategory(int index);

    public abstract int getCategoryGroupNum();

    public abstract int getCategoryGroupCategoryNum(int index);

    public abstract String getCategoryGroupName(int index);

    public abstract void refresh(MWTCallback callback);

    public abstract void refreshIfNeeded();
}
