package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

public class MWTRelatedAssetsResult extends MWTServiceResult
{
    public List<MWTAssetData> assets;
    public List<MWTUserData> relatedUsers;
}
