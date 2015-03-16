package com.quanjing.weitu.app.protocol.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MWTCategoryService
{
    @GET("/categories")
    public void queryRootCategories(Callback<MWTCategoriesResult> cb);

    @GET("/categories/{categoryID}")
    public void querySubCategories(@Path("categoryID") String categoryID, Callback<MWTCategoriesResult> cb);
}
