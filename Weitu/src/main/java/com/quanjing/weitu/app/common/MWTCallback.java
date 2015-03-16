package com.quanjing.weitu.app.common;

import com.quanjing.weitu.app.protocol.MWTError;

public interface MWTCallback
{
    public void success();

    public void failure(MWTError error);
}
