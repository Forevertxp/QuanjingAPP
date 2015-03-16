package com.quanjing.quanjing.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import cn.sharesdk.framework.ShareSDK;

import com.quanjing.quanjing.app.R;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.sriramramani.droid.inspector.server.ViewServer;

public class MQJMainActivity extends MWTBaseSearchActivity {
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
}
