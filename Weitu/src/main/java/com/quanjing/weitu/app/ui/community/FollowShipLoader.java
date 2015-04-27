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
public class FollowShipLoader {

    public void fetchFollowShip(final String userID, final FollowCallBack callback) {
        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryUserPublicInfo(userID,
                new Callback<MWTUserResult>() {
                    @Override
                    public void success(MWTUserResult result, Response response) {
                        if (result!=null){
                            int num = result.user.fellowshipInfo.followerNum;
                            callback.success(num);
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
    }

    public interface FollowCallBack {
        public void success(int num);
    }
}
