package com.quanjing.weitu.app.ui.community.recommend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTAsset;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoScrollGridAdapter extends BaseAdapter {

    /**
     * 上下文
     */
    private Context ctx;
    /**
     * 图片集合
     */
    private List<MWTAsset> imageUrls;

    public NoScrollGridAdapter(Context ctx, List<MWTAsset> urls) {
        this.ctx = ctx;
        this.imageUrls = urls;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageUrls == null ? 0 : imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(ctx).inflate(R.layout.item_gridview, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gridImage);
        int column;
        if (imageUrls.size() == 1) {
            column = imageUrls.size();
        } else {
            column = 3;
        }
        int leftWidth = (int)ctx.getResources().getDimension(R.dimen.circle_width_left);
        int onePicLeftWidth = (int)ctx.getResources().getDimension(R.dimen.circle_onepic_width_left);
        // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
        if (column == 3) {
            int itemWidth = (int) (ctx.getResources().getDisplayMetrics().widthPixels - leftWidth) / column;
            // 下面根据比例计算item的高度，此处只是h使用itemWidth
            int itemHeight = itemWidth;
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    itemWidth,
                    itemHeight);
            imageView.setLayoutParams(param);
            if (imageUrls.get(position) != null && imageUrls.get(position).getImageInfo() != null && !imageUrls.get(position).getImageInfo().url.equals(""))
                Picasso.with(ctx)
                        .load(imageUrls.get(position).getImageInfo().smallURL)
                        .into(imageView);
        } else {
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            imageView.setLayoutParams(param);
            if (imageUrls.get(position) != null && imageUrls.get(position).getImageInfo() != null && !imageUrls.get(position).getImageInfo().url.equals("")) {
                float ratio = (float) (imageUrls.get(position).getImageInfo().height) / (float) (imageUrls.get(position).getImageInfo().width);
                Picasso.with(ctx)
                        .load(imageUrls.get(position).getImageInfo().smallURL)
                        .resize(ctx.getResources().getDisplayMetrics().widthPixels-onePicLeftWidth, (int) ((ctx.getResources().getDisplayMetrics().widthPixels-onePicLeftWidth) * ratio))
                        .into(imageView);
//                Picasso.with(ctx)
//                        .load(imageUrls.get(position).getImageInfo().smallURL)
//                        .resize(250, (int) (250 * ratio))
//                        .into(imageView);
            }

        }
//        if (imageUrls.get(position) != null && imageUrls.get(position).getImageInfo() != null && !imageUrls.get(position).getImageInfo().url.equals(""))
//            Picasso.with(ctx)
//                    .load(imageUrls.get(position).getImageInfo().smallURL)
//                    .into(imageView);
        return convertView;
    }

}
