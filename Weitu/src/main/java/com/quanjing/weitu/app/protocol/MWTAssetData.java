package com.quanjing.weitu.app.protocol;

import com.quanjing.weitu.app.model.MWTImageInfo;

import java.util.List;

public class MWTAssetData
{
    public String assetID;
    public String oriPic;
    public String caption;
    public String createTime;
    public String ownerUserID;
    public MWTImageInfo imageInfo;
    public List<MWTCommentData> latestComments;
    public String commentNum;
    public String webURL;
    public String[] likedUserIDs;
}
