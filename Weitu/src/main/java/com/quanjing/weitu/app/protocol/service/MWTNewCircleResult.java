package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTCircleComment;
import com.quanjing.weitu.app.protocol.MWTUserData;

import java.util.List;

/**
 * Created by Administrator on 2015/1/6.
 */
public class MWTNewCircleResult extends MWTServiceResult {
    public List<MWTUserData> users;
    public List<MWTAssetData> assets;
    public List<MWTActivityData> activities;
    public List<MWTCircleComment> activComment;
}
