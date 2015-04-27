package com.quanjing.weitu.app.ui.user;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTOverScrollView;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.service.MWTAddCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTAssetResult;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTAssetsResult;
import com.quanjing.weitu.app.protocol.service.MWTCommentService;
import com.quanjing.weitu.app.ui.asset.MWTAssetsAdapter;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.photo.AlbumActivity;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.FileUtils;
import com.quanjing.weitu.app.ui.photo.GalleryActivity;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.photo.PictureUtil;
import com.quanjing.weitu.app.ui.photo.PublicWay;
import com.quanjing.weitu.app.ui.photo.Res;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * 首页面activity
 *
 * @author king
 * @version 2014年10月18日  下午11:48:34
 * @QQ:595163260
 */
public class ImageInfoEditorActivity extends MWTBase2Activity {

    private MWTOverScrollView scrollView;
    private View parentView;
    private EditText photo_text, keywords, position;
    private TextView delete;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;

    private RelativeLayout rl_switch_private;
    private ImageView iv_switch_open_private;
    private ImageView iv_switch_close_private;

    private boolean is_private = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_image_info_editor, null);
        setContentView(parentView);
        setTitleText("编辑图片");
        scrollView = (MWTOverScrollView) findViewById(R.id.main);
        photo_text = (EditText) findViewById(R.id.caption);
        keywords = (EditText) findViewById(R.id.keywords);
        position = (EditText) findViewById(R.id.position);
        delete = (TextView) findViewById(R.id.delete);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(photo_text.getWindowToken(), 0);
                photo_text.clearFocus();
                return false;
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ImageInfoEditorActivity.this)
                        .setTitle("请确认")
                        .setMessage("确认删除该图片吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAsset();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });

        rl_switch_private = (RelativeLayout) findViewById(R.id.rl_private);
        iv_switch_open_private = (ImageView) findViewById(R.id.iv_switch_open_private);
        iv_switch_close_private = (ImageView) findViewById(R.id.iv_switch_close_private);


        rl_switch_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open_private.getVisibility() == View.VISIBLE) {
                    iv_switch_open_private.setVisibility(View.INVISIBLE);
                    iv_switch_close_private.setVisibility(View.VISIBLE);
                    is_private = false;
                } else {
                    iv_switch_open_private.setVisibility(View.VISIBLE);
                    iv_switch_close_private.setVisibility(View.INVISIBLE);
                    is_private = true;
                }
            }
        });

        initPhotoInfo();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //添加菜单项
        MenuItem findItem = menu.add(0, 0, 0, "保存");
        findItem.setIcon(R.drawable.ic_save);
        //绑定到ActionBar
        findItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            editPhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPhotoInfo() {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTAssetService assetService = restManager.create(MWTAssetService.class);
        String asset_id = getIntent().getStringExtra("asset_id");
        assetService.fetchAssetInfo(asset_id, new Callback<MWTAssetResult>() {
            @Override
            public void success(MWTAssetResult result, Response response) {
                photo_text.setText(result.asset.caption);
                keywords.setText(result.asset.keywords);
                position.setText(result.asset.position);
                if (result.asset.privateAsset){
                    iv_switch_open_private.setVisibility(View.INVISIBLE);
                    iv_switch_close_private.setVisibility(View.VISIBLE);
                    is_private = false;
                }else {
                    iv_switch_open_private.setVisibility(View.VISIBLE);
                    iv_switch_close_private.setVisibility(View.INVISIBLE);
                    is_private = true;
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }


    private void editPhoto() {
        SVProgressHUD.showInView(ImageInfoEditorActivity.this, "正在保存...", true);
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTAssetService assetService = restManager.create(MWTAssetService.class);
        String asset_id = getIntent().getStringExtra("asset_id");
        String caption = photo_text.getText().toString();
        String keyword = keywords.getText().toString();
        String posit = position.getText().toString();
        assetService.modifyAsset("modify", "", caption, keyword, posit, is_private, asset_id, new Callback<MWTAssetsResult>() {
            @Override
            public void success(MWTAssetsResult result, Response response) {
                SVProgressHUD.dismiss(ImageInfoEditorActivity.this);
                Toast.makeText(ImageInfoEditorActivity.this, "保存成功", 500).show();
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(ImageInfoEditorActivity.this);
                Toast.makeText(ImageInfoEditorActivity.this, "保存失败", 500).show();
            }
        });

    }

    private void deleteAsset() {
        SVProgressHUD.showInView(ImageInfoEditorActivity.this, "正在删除...", true);
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTAssetService assetService = restManager.create(MWTAssetService.class);
        String asset_id = getIntent().getStringExtra("asset_id");
        assetService.deleteAsset(asset_id, "delete", new Callback<MWTAssetsResult>() {
            @Override
            public void success(MWTAssetsResult result, Response response) {
                SVProgressHUD.dismiss(ImageInfoEditorActivity.this);
                MWTUser user = MWTUserManager.getInstance().getCurrentUser();
                user.markDataDirty();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(ImageInfoEditorActivity.this);
                Toast.makeText(ImageInfoEditorActivity.this, "刪除失败", 500).show();
            }
        });

    }


    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            for (int i = 0; i < PublicWay.activityList.size(); i++) {
//                if (null != PublicWay.activityList.get(i)) {
//                    PublicWay.activityList.get(i).finish();
//                }
//            }
//            System.exit(0);
//        }
//        return true;
//    }

}


