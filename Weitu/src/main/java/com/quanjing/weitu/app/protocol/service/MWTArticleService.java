package com.quanjing.weitu.app.protocol.service;

import com.quanjing.weitu.app.model.MWTRotation;
import com.quanjing.weitu.app.protocol.MWTDailyData;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/1/6.
 */
public interface MWTArticleService {
    @GET("/articleHome")
    public void fetchHomeActicles(Callback<MWTArticleResult> callback);

    @GET("/articleFound")
    public void fetchFoundActicles(@Query("page") int page,
                                   @Query("count") int count,
                                   Callback<MWTArticleResult> callback);


    @GET("/Rotation")
    public void fetchFoundRotations(Callback<ArrayList<MWTRotation>> callback);

    @GET("/article")
    public void fetchSubActicles(@Query("type") int type,
                                 @Query("page") int page,
                                 @Query("count") int count,
                                 Callback<MWTArticleResult> callback);

    @GET("/daily")
    public void fetchDailyImage(Callback<MWTDailyResult> callback);

    @GET("/hottag")
    public void fetchLabels(@Query("page") int page,
                            @Query("count") int count,
                            Callback<MWTLabelResult> callback);
}
