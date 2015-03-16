package com.quanjing.weitu.app.protocol.service;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MWTSearchService {
    @GET("/searchtip/{keyword}")
    public void fetchKeywordTip(@Path("keyword") String keyword,
                                Callback<ArrayList<String>> cb);
}
