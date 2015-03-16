package com.quanjing.dutu.app;

import android.app.Application;
import android.widget.Button;
import com.quanjing.dutu.R;
import com.quanjing.weitu.app.MWTConfig;
import com.quanjing.weitu.app.common.MWTThemer;

public class MDTApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        MWTThemer.getInstance().init(this);
        MWTThemer.getInstance().setActionBarBackgroundColor(getResources().getColor(R.color.ActionBarBackgroundColor));
        MWTThemer.getInstance().setActionBarForegroundColor(getResources().getColor(R.color.ActionBarForegroundColor));

        final String API_BASE_URL = "http://app.quanjing.com/dtapi";
        MWTConfig.getInstance().init(API_BASE_URL, "读图时代 v1.0", this);
    }
}
