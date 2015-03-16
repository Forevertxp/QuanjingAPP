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

public interface MWTUserService
{
    @GET("/users/me")
    public void queryUserMe(Callback<MWTUserResult> cb);

    @GET("/users/{user_id}/assets")
    public void queryUserUploadAssets(@Path("user_id") String userID,
                                      @Query("startIndex") int startIndex,
                                      @Query("count") int count,
                                      Callback<MWTAssetsResult> callback);

    @GET("/users/{user_id}/share")
    public void queryUserSharedAssets(@Path("user_id") String userID,
                                      @Query("startIndex") int startIndex,
                                      @Query("count") int count,
                                      Callback<MWTAssetsResult> callback);

    //@GET("/users/{user_id}/down")
    @GET("/users/{user_id}/lightbox")
    public void queryUserDownloadedAssets(@Path("user_id") String userID,
                                          @Query("startIndex") int startIndex,
                                          @Query("count") int count,
                                          Callback<MWTAssetsResult> callback);

    @GET("/users/{user_id}/likes")
    public void queryUserLikedAssets(@Path("user_id") String userID,
                                          @Query("startIndex") int startIndex,
                                          @Query("count") int count,
                                          Callback<MWTAssetsResult> callback);

    // 粉丝
    @GET("/users/{user_id}/followings")
    public void queryFollowings(@Path("user_id") String userID,
                                @Query("startIndex") int startIndex,
                                @Query("count") int count,
                                Callback<MWTFollowerResult> callback);

    // 关注
    @GET("/users/{user_id}/followers")
    public void queryFollowers(@Path("user_id") String userID,
                                     @Query("startIndex") int startIndex,
                                     @Query("count") int count,
                                     Callback<MWTFollowerResult> callback);

    @GET("/users/{user_id}")
    public void queryUserPublicInfo(@Path("user_id") String userID,
                                    Callback<MWTUserResult> callback);


    @Multipart
    @POST("/users/me")
    public void modifyUserMe(@Part("nickname") String nickname,
                             @Part("signature") String signature,
                             @Part("avatar") TypedFile avatarFile,
                             Callback<MWTUserResult> callback);

    @Multipart
    @POST("/users/me")
    public void modifyUserMe(@Part("nickname") String nickname,
                             @Part("signature") String signature,
                             Callback<MWTUserResult> callback);

    @FormUrlEncoded
    @POST("/users/{user_id}/followings")
    public void addAttention(@Path("user_id") String user_id,
                             @Field("action") String action,
                             @Field("following_user_id") String following_user_id,
                             Callback<MWTUserResult> callback);

    @FormUrlEncoded
    @POST("/assets/{asset_id}/likes")
    public void addFavorite(@Path("asset_id") String asset_id,
                            @Field("action") String action,
                             Callback<MWTUserResult> callback);

    @FormUrlEncoded
    @POST("/assets/{asset_id}/likes")
    public void cancelFavorite(@Path("asset_id") String asset_id,
                            @Field("action") String action,
                            Callback<MWTUserResult> callback);
}
