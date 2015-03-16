package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTCategoryData;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

/**
 * Created by Administrator on 2014/12/11.
 */
public class MWTTalentResult extends MWTServiceResult {
    public List<MWTUserData> users;
    public List<MWTAssetData> recommendedUserAssets;
    public List<MWTUserData> relatedUsers;
}
