package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTFeedData;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

public class MWTFeedRefreshResult extends MWTServiceResult
{
    public MWTFeedData feedFragment;
    public List<MWTUserData> relatedUsers;
}
