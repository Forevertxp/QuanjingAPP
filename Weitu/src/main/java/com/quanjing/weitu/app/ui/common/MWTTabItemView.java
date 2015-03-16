package com.quanjing.weitu.app.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quanjing.weitu.R;

public class MWTTabItemView extends LinearLayout
{
    public final static int DISPLAY_STYLE_NORMAL = 0;
    public final static int DISPLAY_STYLE_SELECTED = 1;
    private TextView _iconTextView;
    private TextView _titleTextView;

    public MWTTabItemView(Context context)
    {
        super(context);
    }

    public MWTTabItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MWTTabItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        _iconTextView = (TextView) findViewById(R.id.IconTextView);
        _iconTextView.setGravity(Gravity.CENTER);
        //        _iconTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        _titleTextView = (TextView) findViewById(R.id.TitleTextView);
    }

    public void setIconTextWithTypeface(CharSequence iconText, Typeface typeface)
    {
        _iconTextView.setTypeface(typeface);
        _iconTextView.setText(iconText);
    }

    public void setIconTextSize(float iconTextSize)
    {
        _iconTextView.setTextSize(iconTextSize);
    }

    public CharSequence getIconText()
    {
        return _iconTextView.getText();
    }

    public void setIconText(CharSequence iconText)
    {
        _iconTextView.setText(iconText);
    }

    public void setTitleTextSize(float titleTextSize)
    {
        _titleTextView.setTextSize(titleTextSize);
    }

    public CharSequence getTitleText()
    {
        return _titleTextView.getText();
    }

    public void setTitleText(CharSequence titleText)
    {
        _titleTextView.setText(titleText);
    }

    public void setDisplayStyle(DisplayStyle displayStyle)
    {
        switch (displayStyle)
        {
            case NORMAL:
            {
                Context cx = getContext();
                int color = getResources().getColor(R.color.TabBarForegroundColor);
                _iconTextView.setTextColor(color);
                _titleTextView.setTextColor(color);
                break;
            }
            case SELECTED:
            {
                Context cx = getContext();
                int color = getResources().getColor(R.color.WTBlueColor);
                _iconTextView.setTextColor(color);
                _titleTextView.setTextColor(color);
                break;
            }
            default:
                break;
        }
    }

    public enum DisplayStyle
    {
        NORMAL, SELECTED
    }
}
