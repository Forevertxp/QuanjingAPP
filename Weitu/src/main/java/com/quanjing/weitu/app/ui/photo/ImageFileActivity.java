package com.quanjing.weitu.app.ui.photo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.user.MWTUploadPicActivity;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 */
public class ImageFileActivity extends MWTBase2Activity {

    //private FolderAdapter folderAdapter;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Res.getLayoutID("plugin_camera_image_file"));
        setTitleText("相册");
//        PublicWay.activityList.add(this);
        mContext = this;
        GridView gridView = (GridView) findViewById(Res.getWidgetID("fileGridView"));
//        folderAdapter = new FolderAdapter(this);
//        gridView.setAdapter(folderAdapter);
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }
}
