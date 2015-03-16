package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.protocol.MWTCommentData;

import java.util.List;

/**
 * Created by Administrator on 2014/12/24.
 */
public class MWTCommentResult extends MWTServiceResult {

    public String assetID;
    public List<MWTCommentData> comments;
}
