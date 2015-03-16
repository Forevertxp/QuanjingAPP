package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

/**
 * Created by su on 9/26/14.
 */
public class MWTUserResult extends MWTServiceResult
{
    public MWTUserData user;
    public List<MWTAssetData> relatedAssets;
    public List<MWTUserData> relatedUsers;
}
