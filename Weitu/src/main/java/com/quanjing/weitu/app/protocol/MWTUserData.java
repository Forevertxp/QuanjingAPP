package com.quanjing.weitu.app.protocol;

import com.quanjing.weitu.app.model.MWTImageInfo;

public class MWTUserData
{
    public String userID;
    public String nickname;
    public String signature;
    public MWTImageInfo avatarImageInfo;

    public MWTUserPrivateInfoData privateInfo;
    public MWTUserAssetsInfoData assetsInfo;
    public MWTFellowshipInfoData fellowshipInfo;
}
