package com.quanjing.weitu.app.model;

/**
 * Created by Administrator on 2014/12/17.
 */


import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTRecommendResult;
import com.quanjing.weitu.app.protocol.service.MWTCircleService;
import com.quanjing.weitu.app.protocol.service.MWTRecommendService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

public class MWTRecommendManager {
    private static MWTRecommendManager s_instance;
    private ArrayList<MWTUserData> users = new ArrayList<>();
    private HashMap<String, MWTUser> userHashMap = new HashMap<>();
    private MWTRecommendService recommendService;

    private MWTRecommendManager() {
        recommendService = MWTRestManager.getInstance().create(MWTRecommendService.class);
    }

    public static MWTRecommendManager getInstance() {
        if (s_instance == null) {
            s_instance = new MWTRecommendManager();
        }

        return s_instance;
    }

    public ArrayList<MWTUserData> getTalents() {
        return users;
    }


    public MWTRecommendService getRecommendService() {
        return recommendService;
    }

    public void refreshCircles(int page, int count,final MWTCallback callback) {
        getRecommendService().fetchItems(page, count, new Callback<MWTRecommendResult>() {
            @Override
            public void success(MWTRecommendResult result, Response response) {
                if (result == null) {
                    if (callback != null) {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null) {
                    if (callback != null) {
                        callback.failure(result.error);
                    }
                    return;
                }

                // 服务端返回的数据是多个list的集合，包括人员列表以及图片列表，需要在客户端根据userID来配对，效率极低
                List<MWTUserData> userDatas = result.users;
                if (userDatas == null) {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少user信息。"));
                    return;
                }

                updateRootUser(userDatas);

                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (callback != null) {
                    callback.failure(new MWTError(retrofitError));
                }
            }
        });
    }

    private List<MWTUser> registerTalentDatas(List<MWTUserData> userDatas) {
        ArrayList<MWTUser> users = new ArrayList<>();

        for (MWTUserData userData : userDatas) {
            String userID = userData.userID;
            MWTUser user = userHashMap.get(userID);
            if (user == null) {
                user = new MWTUser();
                userHashMap.put(userID, user); // Dirty hack, since server side will reuse feedID to store the actural categoryID
            }
            user.mergeWithData(userData);
            users.add(user);
        }

        return users;
    }

    private void updateRootUser(List<MWTUserData> users) {
        this.users = new ArrayList<>(users);
    }

    private List<MWTAsset> getAssetByUserID(List<MWTAsset> assets, String userID) {
        List<MWTAsset> list = new ArrayList<MWTAsset>();
        for (MWTAsset asset : assets) {
            if (asset.getOwnerUserID().equals(userID)) {
                list.add(asset);
            }
        }
        return list;
    }
}

