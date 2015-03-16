package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.*;

public interface MWTAuthService
{
    @FormUrlEncoded
    @POST("/auth/sms")
    public void requestSMSAuthCode(@Field("cellphone") String cellphone,
                                   @Field("client_id") String clientID,
                                   Callback<MWTServiceResult> cb);

    @FormUrlEncoded
    @POST("/auth/sms")
    public void verifySMSAuthCode(@Field("cellphone") String cellphone,
                                  @Field("client_id") String clientID,
                                  @Field("verification_code") String verificationCode,
                                  Callback<MWTAccessTokenResult> cb);

    @FormUrlEncoded
    @POST("/auth/login")
    public void authWithUsernamePassword(@Field("username") String username,
                                         @Field("password") String password,
                                         @Field("client_id") String clientID,
                                         Callback<MWTAccessTokenResult> cb);
}
