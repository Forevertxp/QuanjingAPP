package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 2014/12/24.
 */
public interface MWTCommentService {

    @GET("/assets/{assetID}/comments")
    void getComments(@Path("assetID") String assetID,
                     Callback<MWTCommentResult> cb);

    @FormUrlEncoded
    @POST("/assets/{assetID}/comments")
    void addComments(@Path("assetID") String assetID,
            @Field("action") String action,
            @Field("content") String content,
            Callback<MWTAddCommentResult> cb);
}
