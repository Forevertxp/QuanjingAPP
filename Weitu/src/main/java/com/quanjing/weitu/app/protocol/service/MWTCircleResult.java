package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

/**
 * Created by Administrator on 2014/12/17.
 */
public class MWTCircleResult extends MWTServiceResult{
    public List<MWTUserData> users;
    //public List<MWTAssetData> assets;
    public List<MWTAssetData> recommendedUserAssets;
}
