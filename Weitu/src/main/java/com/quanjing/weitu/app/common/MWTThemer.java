package com.quanjing.weitu.app.common;

import android.content.Context;
import android.graphics.Typeface;

public class MWTThemer
{
    private static MWTThemer s_instance;

    private Typeface _wtFont;
    private Typeface _fakFont;

    private int _actionBarForegroundColor;
    private int _actionBarBackgroundColor;

    private MWTThemer()
    {
    }

    public static MWTThemer getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTThemer();
        }
        return s_instance;
    }

    public void init(Context context)
    {
        _wtFont = Typeface.createFromAsset(context.getAssets(), "fonts/WTFont.ttf");
        _fakFont = Typeface.createFromAsset(context.getAssets(), "fonts/FontAwesome.ttf");
    }

    public Typeface getFAKFont()
    {
        return _fakFont;
    }

    public Typeface getWTFont()
    {
        return _wtFont;
    }

    public int getActionBarForegroundColor()
    {
        return _actionBarForegroundColor;
    }

    public void setActionBarForegroundColor(int actionBarForegroundColor)
    {
        _actionBarForegroundColor = actionBarForegroundColor;
    }

    public int getActionBarBackgroundColor()
    {
        return _actionBarBackgroundColor;
    }

    public void setActionBarBackgroundColor(int actionBarBackgroundColor)
    {
        _actionBarBackgroundColor = actionBarBackgroundColor;
    }
}
