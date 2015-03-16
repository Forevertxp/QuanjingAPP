package com.quanjing.weitu.app.ui.community;

import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-2-17.
 */
public class LikedUserImageLoader {

    public void fetchLikedUserImageUrl(final String userID, final LikerCallBack callback) {
        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryUserPublicInfo(userID,
                new Callback<MWTUserResult>() {
                    @Override
                    public void success(MWTUserResult result, Response response) {
                        String imageUrl = result.user.avatarImageInfo.url;
                        callback.success(imageUrl);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
    }

    public interface LikerCallBack {
        public void success(String imageUrl);
    }
}
