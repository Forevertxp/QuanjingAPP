package com.quanjing.weitu.app.ui.index;

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
public class MWTPicActivity extends MWTBase2Activity {

    private MWTFeedFlowFragment _hottestPicturesFragment;
    private FrameLayout _fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtpic);

        String feed = getIntent().getExtras().getString("feed");
        if (feed.equals(MWTFeedManager.kWTFeedIDIngenious)){
            setTitleText("创意");
        }
        if (feed.equals(MWTFeedManager.kWTFeedIDMaster)){
            setTitleText("大师");
        }
        if (feed.equals(MWTFeedManager.kWTFeedIDArt)){
            setTitleText("艺术");
        }
        if (feed.equals(MWTFeedManager.kWTFeedIDT)){
            setTitleText("T台");
        }


        _fragmentContainer = (FrameLayout) findViewById(R.id.FragmentContainer);
        _hottestPicturesFragment = MWTFeedFlowFragment.newInstance(feed);
        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, _hottestPicturesFragment).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
