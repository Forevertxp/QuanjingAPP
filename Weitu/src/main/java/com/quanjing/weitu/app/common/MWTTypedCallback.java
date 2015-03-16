package com.quanjing.weitu.app.common;

import com.quanjing.weitu.app.protocol.MWTError;

public interface MWTTypedCallback<T>
{
    public void success(T result);

    public void failure(MWTError error);
}
