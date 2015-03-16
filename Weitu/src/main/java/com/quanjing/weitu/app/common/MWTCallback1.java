package com.quanjing.weitu.app.common;

import com.quanjing.weitu.app.protocol.MWTError;

public interface MWTCallback1<ResultType>
{
    public void success(ResultType result);

    public void failure(MWTError error);
}
