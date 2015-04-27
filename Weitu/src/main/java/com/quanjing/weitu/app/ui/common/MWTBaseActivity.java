package com.quanjing.weitu.app.ui.common;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.Window;

import com.quanjing.weitu.R;
import com.umeng.analytics.MobclickAgent;

public class MWTBaseActivity extends FragmentActivity
{
    private boolean _isDisplayHomeAsUpEnabled = true;
    private boolean _isDisplayShowTitleEnabled = true;

    public void setDisplayHomeAsUpEnabled(boolean isDisplayHomeAsUpEnabled)
    {
        _isDisplayHomeAsUpEnabled = isDisplayHomeAsUpEnabled;
    }

    public void setDisplayShowTitleEnabled(boolean isDisplayShowTitleEnabled)
    {
        _isDisplayShowTitleEnabled = isDisplayShowTitleEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setTheme(R.style.AppTheme);

        applyUpButtonState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void applyUpButtonState()
    {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(_isDisplayHomeAsUpEnabled);
        ab.setDisplayShowTitleEnabled(_isDisplayShowTitleEnabled);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
