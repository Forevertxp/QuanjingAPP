package com.quanjing.weitu.app.ui.found;

import android.content.Context;
import android.util.AttributeSet;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import com.quanjing.weitu.app.common.MWTViewAspectRatioMeasurer;

public class MWTAutoSlidingPagerView extends AutoScrollViewPager
{
    private static final double VIEW_ASPECT_RATIO = 640.0 / 320.0;

    private MWTViewAspectRatioMeasurer _viewMeasurer = new MWTViewAspectRatioMeasurer(VIEW_ASPECT_RATIO);

    public MWTAutoSlidingPagerView(Context paramContext)
    {
        super(paramContext);
    }

    public MWTAutoSlidingPagerView(Context paramContext, AttributeSet paramAttributeSet)
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
