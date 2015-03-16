package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 2014/12/17.
 */
public interface MWTSquareService{

    @GET("/feeds/square")
    void fetchItems(@Query("count") int count,
                    @Query("min_item_timestamp") long minItemTimestamp,
                    @Query("max_item_timestamp") long max_item_timestamp,
                    Callback<MWTFeedRefreshResult> cb);
}