package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MWTFeedService
{
    @GET("/feeds/{feed_id}")
    void fetchItems(@Path("feed_id") String feedID,
                    @Query("max_item_timestamp") long maxItemTimestamp,
                    @Query("min_item_timestamp") long minItemTimestamp,
                    @Query("count") int count,
                    Callback<MWTFeedRefreshResult> cb);
}
