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
import retrofit.mime.TypedFile;

public interface MWTAssetService {
    @Multipart
    @POST("/assets")
    public void uploadAssets(@Part("action") String action,
                             @Part("caption") String caption,
                             @Part("album_ids") String album_ids,
                             @Part("is_private") boolean is_private,
                             @Part("isNew") int isNew,
                             @Part("file") TypedFile file,
                             @Part("degree") int degree,
                             Callback<MWTAssetsResult> callback);

    @GET("/assets/{asset_id}/related_assets")
    public void queryRelatedAssets(@Path("asset_id") String assetID,
                                   Callback<MWTRelatedAssetsResult> cb);

    @GET("/assets/search1/{keyword}")
    public void search(@Path("keyword") String keyword,
                       @Query("startIndex") int startIndex,
                       @Query("count") int count,
                       Callback<MWTAssetsResult> cb);

    @FormUrlEncoded
    @POST("/assets/{asset_id}/down")
    public void markDownloaded(@Path("asset_id") String assetID,
                               @Field("action") String action,
                               Callback<MWTServiceResult> callback);

    @FormUrlEncoded
    @POST("/assets/{asset_id}/share")
    public void markShared(@Path("asset_id") String assetID,
                           @Field("action") String action,
                           Callback<MWTServiceResult> callback);

    @FormUrlEncoded
    @POST("/assets/{asset_id}/lightbox")
    public void markCollected(@Path("asset_id") String assetID,
                              @Field("action") String action,
                              Callback<MWTServiceResult> callback);
}
