package com.quanjing.weitu.app.ui.community;

import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Administrator on 2015/3/17.
 */
public class UserLoader {
    public void fetchUserByID(final String userID, final UserCallBack callback) {
        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryUserPublicInfo(userID,
                new Callback<MWTUserResult>() {
                    @Override
                    public void success(MWTUserResult result, Response response) {
                        MWTUserData userData = result.user;
                        callback.success(userData);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
    }

    public interface UserCallBack {
        public void success(MWTUserData userData);
    }
}
