package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.photo.BitmapCache;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PictureUtil;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-29.
 */
public class LocalAlbumAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private DisplayMetrics dm;
    BitmapCache cache;

    public LocalAlbumAdapter(Context c, ArrayList<ImageItem> dataList) {
        mContext = c;
        cache = new BitmapCache();
        this.dataList = dataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals((String) imageView.getTag())) {
                    //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                    int degree = PictureUtil.readPictureDegree(url);
                    if (degree != 0)
                        bitmap = PictureUtil.rotaingImageView(degree, bitmap);
                    ((ImageView) imageView).setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "callback, bmp not match");
                }
            } else {
                Log.e(TAG, "callback, bmp null");
            }
        }
    };

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_native_image, parent, false);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.nativeImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (dataList != null && dataList.size() > position)
            path = dataList.get(position).imagePath;
        else
            path = "camera_default";
        if (path.contains("camera_default")) {
            viewHolder.imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
        } else {
            final ImageItem item = dataList.get(position);
            viewHolder.imageView.setTag(item.imagePath);
            cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath,
                    callback);
        }
        return convertView;
    }

}
