package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 2015/1/15.
 */
public interface MWTRecommendService {
    @GET("/recommendation/users3")
    void fetchItems(@Query("page") int page,
                    @Query("count") int count,
                    Callback<MWTRecommendResult> cb);

}
