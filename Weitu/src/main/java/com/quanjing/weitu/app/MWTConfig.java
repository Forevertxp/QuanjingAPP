package com.quanjing.weitu.app;

import android.content.Context;

public class MWTConfig
{
    private static MWTConfig s_instance;

    private String _apiBaseURL;
    private Context _context;
    private String _versionText;

    private MWTConfig()
    {
    }

    public static MWTConfig getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTConfig();
        }
        return s_instance;
    }

    public void init(String apiBaseURL, String versionText, Context context)
    {
        _apiBaseURL = apiBaseURL;
        _versionText = versionText;
        _context = context;
    }

    public String getVersionText()
    {
        return _versionText;
    }

    public String getAPIBaseURL()
    {
        return _apiBaseURL;
    }

    public Context getContext()
    {
        return _context;
    }
}
