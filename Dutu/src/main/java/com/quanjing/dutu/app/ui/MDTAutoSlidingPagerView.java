package com.quanjing.dutu.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import com.quanjing.weitu.app.common.MWTViewAspectRatioMeasurer;

public class MDTAutoSlidingPagerView extends AutoScrollViewPager
{
    private static final double VIEW_ASPECT_RATIO = 640.0 / 320.0;

    private MWTViewAspectRatioMeasurer _viewMeasurer = new MWTViewAspectRatioMeasurer(VIEW_ASPECT_RATIO);

    public MDTAutoSlidingPagerView(Context paramContext)
    {
        super(paramContext);
    }

    public MDTAutoSlidingPagerView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        _viewMeasurer.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(_viewMeasurer.getMeasuredWidth(), _viewMeasurer.getMeasuredHeight());
    }
}
