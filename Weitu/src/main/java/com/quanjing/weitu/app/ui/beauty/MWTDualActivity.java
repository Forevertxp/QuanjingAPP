package com.quanjing.weitu.app.ui.beauty;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.common.MWTDualFragment;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowFragment;

/**
 * Created by Administrator on 2015/1/8.
 */
public class MWTDualActivity extends MWTBase2Activity {

    private MWTFeedFlowFragment _latestPicturesFragment;
    private MWTFeedFlowFragment _hottestPicturesFragment;
    private MWTDualFragment _selectedFragment;
    private FrameLayout _fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtdual);
        setTitleText("壁纸");

        _fragmentContainer = (FrameLayout) findViewById(R.id.FragmentContainer);
        _hottestPicturesFragment = MWTFeedFlowFragment.newInstance(MWTFeedManager.kWTFeedIDWallpaper);
        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, _hottestPicturesFragment).commit();

//        _latestPicturesFragment = MWTFeedFlowFragment.newInstance(MWTFeedManager.kWTFeedIDLatestUpload);
//        _hottestPicturesFragment = MWTFeedFlowFragment.newInstance(MWTFeedManager.kWTFeedIDWallpaper);
//        _selectedFragment = new MWTDualFragment("最新美图", _latestPicturesFragment, "最热美图", _hottestPicturesFragment);
//        _selectedFragment.setButtonBackgroundDrawable(R.drawable.btn_orange);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        //fragmentTransaction.replace(android.R.id.content, _selectedFragment);
//        fragmentTransaction.replace(android.R.id.content, _hottestPicturesFragment);
//        // ---add to the back stack---
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
