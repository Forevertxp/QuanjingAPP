package com.quanjing.quanjing.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

import com.quanjing.quanjing.app.R;
import com.quanjing.weitu.app.ui.circle.NewCircleFragment;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.sriramramani.droid.inspector.server.ViewServer;

public class MQJMainActivity extends MWTBaseSearchActivity {
    private long exitTime;

    public MQJMainActivity() {
        super();
        // setDisplayHomeAsUpEnabled(false);
        //setTitle("全景");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getActionBar().hide();
        initShareSDK();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MQJMainFragment())
                    .commit();
        }
        ViewServer.get(this).addWindow(this);
    }

    private void initShareSDK() {
        ShareSDK.initSDK(this);
        ShareSDK.setConnTimeout(5000);
        ShareSDK.setReadTimeout(10000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    // 实现返回键的点击事件

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            exit(); // 在这里进行点击判断

            return false;

        }

        return super.onKeyDown(keyCode, event);

    }


    public void exit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {

            // 点击间隔大于两秒，做出提示

            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();

            exitTime = System.currentTimeMillis();

        } else {

            // 连续点击量两次，进行应用退出的处理

            System.exit(0);

        }

    }
}
