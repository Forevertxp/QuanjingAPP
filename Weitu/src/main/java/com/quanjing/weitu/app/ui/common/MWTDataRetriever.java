package com.quanjing.weitu.app.ui.common;

import com.quanjing.weitu.app.common.MWTCallback;

public class MWTDataRetriever
{
    public void refresh(final MWTCallback callback)
    {
        if (callback != null)
        {
            callback.success();
        }
    }

    public void loadMore(final MWTCallback callback)
    {
        if (callback != null)
        {
            callback.success();
        }
    }
}
