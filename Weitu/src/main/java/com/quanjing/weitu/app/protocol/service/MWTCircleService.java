package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 2014/12/17.
 */
public interface MWTCircleService {

    @GET("/activity/friends2")
    void fetchCircleItems(@Query("count") int count,
                          @Query("max_item_timestamp") long max_item_timestamp,
                          Callback<MWTNewCircleResult> cb);

    // 对动作的评论
    @FormUrlEncoded
    @POST("/activity/comment")
    void addCommentToActivity(@Field("activityid") String activityid,
                     @Field("replyuserid") String replyuserid,
                     @Field("content") String content,
                     Callback<MWTAddCommentResult> cb);
}
