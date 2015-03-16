package com.quanjing.dutu.app.ui;

import android.os.Bundle;
import cn.sharesdk.framework.ShareSDK;
import com.quanjing.dutu.R;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.umeng.analytics.MobclickAgent;

public class MDTMainActivity extends MWTBaseSearchActivity
{
    MDTMainFragment _mainFragment;

    public MDTMainActivity()
    {
        super();
        //setDisplayHomeAsUpEnabled(false);
        //setTitle("读图时代");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initShareSDK();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            _mainFragment = new MDTMainFragment();
            getFragmentManager().beginTransaction()
                                .add(R.id.container, _mainFragment)
                                .commit();
        }
    }

    private void initShareSDK()
    {
        ShareSDK.initSDK(this);
        ShareSDK.setConnTimeout(5000);
        ShareSDK.setReadTimeout(10000);
    }

    public void switchToLifePage()
    {
        if (_mainFragment != null)
        {
            _mainFragment.switchToLifePage();
        }
    }

    public void switchToWikiPage()
    {
        if (_mainFragment != null)
        {
            _mainFragment.switchToWikiPage();
        }
    }

    public void switchToAppPage()
    {
        if (_mainFragment != null)
        {
            _mainFragment.switchToAppPage();
        }
    }
}
