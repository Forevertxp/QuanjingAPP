package com.quanjing.weitu.app.ui.photo;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.user.MWTUploadPicActivity;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:49:10
 */
public class ShowAllPhotoActivity extends MWTBase2Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private Button okButton;
	private Intent intent;
	private Context mContext;
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_show_all_photo"));
//		PublicWay.activityList.add(this);
		mContext = this;
		okButton = (Button) findViewById(Res.getWidgetID("showallphoto_ok_button"));
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
        setTitleText(folderName);

		init();
		initListener();
		isShowOkBt();
	}

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  


	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter);  
		progressBar = (ProgressBar) findViewById(Res.getWidgetID("showallphoto_progressbar"));
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(Res.getWidgetID("showallphoto_myGrid"));
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		okButton = (Button) findViewById(Res.getWidgetID("showallphoto_ok_button"));
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,
							Button button) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num&&isChecked) {
							button.setVisibility(View.GONE);
							toggleButton.setChecked(false);
							Toast.makeText(ShowAllPhotoActivity.this, Res.getString("only_choose_num"), 200)
									.show();
							return;
						}

						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							button.setVisibility(View.GONE);
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
//				if (PublicWay.photoService != null) {
//					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
//					Bimp.tempSelectBitmap.clear();
//					PublicWay.photoService.onActivityResult(0, -2,
//							intent);
//				}
				intent.setClass(mContext, MWTUploadPicActivity.class);
				startActivity(intent);
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("selectedDataList",
				// selectedDataList);
				// intent.putExtras(bundle);
				// intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
				// startActivity(intent);
				finish();

			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			okButton.setPressed(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
		} else {
			okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			this.finish();
//			intent.setClass(ShowAllPhotoActivity.this, ImageFileActivity.class);
//			startActivity(intent);
//		}
//
//		return false;
//
//	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		isShowOkBt();
		super.onRestart();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
