package com.quanjing.weitu.app.model;

import android.content.Context;

public class MWTSearchManager
{
    private static MWTSearchManager s_instance;

    private MWTSearchManager()
    {
    }

    public static MWTSearchManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTSearchManager();
        }

        return s_instance;
    }

    public void performSearch(Context context, String keyword)
    {

    }
}
