package com.quanjing.weitu.app.ui.category;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.quanjing.weitu.R;

public class MWTCategorySectionHeaderView extends FrameLayout
{
    private TextView _titleTextView;
    private String _sectionTitle;

    public MWTCategorySectionHeaderView(Context context)
    {
        super(context);
        construct(context);
    }

    public MWTCategorySectionHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        construct(context);
    }

    public MWTCategorySectionHeaderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        construct(context);
    }

    private void construct(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.view_category_section_header, this, true);
        setupViews();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews()
    {
        _titleTextView = (TextView) findViewById(R.id.TextView);
        updateTextView();
    }

    public void setSectionTitle(String sectionTitle)
    {
        _sectionTitle = sectionTitle;
        updateTextView();
    }

    private void updateTextView()
    {
        if (_titleTextView != null)
        {
            _titleTextView.setText(_sectionTitle);
        }
    }
}
