package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 2014/12/11.
 */
public interface MWTTalentService {
    @GET("/recommendation/users1")
    public void queryTalents(@Query("count") int count,
                             @Query("max_item_timestamp") long max_item_timestamp,
                             Callback<MWTTalentResult> cb);

}
