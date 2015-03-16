package com.quanjing.weitu.app.ui.user;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
/**
 * 图片扫描器
 *
 * @author tianxiaopeng
 *
 */
public class ImageScanner {
    private Context mContext;

    public ImageScanner(Context context){
        this.mContext = context;
    }

    /**
     * 利用ContentProvider扫描手机中的图片，将扫描的Cursor回调到ScanCompleteCallBack
     * 接口的scanComplete方法中，此方法在运行在子线程中
     */
    public void scanImages(final ScanCompleteCallBack callback) {
        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                callback.scanComplete((Cursor)msg.obj);
            }
        };

        new Thread(new Runnable() {

            @Override
            public void run() {
                //先发送广播扫描下整个sd卡
                mContext.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null, null, null, MediaStore.Images.Media.DATE_ADDED);

                //利用Handler通知调用线程
                Message msg = mHandler.obtainMessage();
                msg.obj = mCursor;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * 扫描完成之后的回调接口
     *
     */
    public static interface ScanCompleteCallBack{
        public void scanComplete(Cursor cursor);
    }


}
