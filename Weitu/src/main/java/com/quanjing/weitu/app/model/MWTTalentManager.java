package com.quanjing.weitu.app.model;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTFeedItemData;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTCircleService;
import com.quanjing.weitu.app.protocol.service.MWTFeedRefreshResult;
import com.quanjing.weitu.app.protocol.service.MWTFollowerResult;
import com.quanjing.weitu.app.protocol.service.MWTSquareService;
import com.quanjing.weitu.app.protocol.service.MWTTalentResult;
import com.quanjing.weitu.app.protocol.service.MWTTalentService;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.user.MWTOtherUserActivity;

import org.lcsky.SVProgressHUD;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

public class MWTTalentManager {
    private static MWTTalentManager s_instance;
    private ArrayList<MWTTalent> talents = new ArrayList<>();
    private HashMap<String, MWTUser> userHashMap = new HashMap<>();
    private MWTTalentService talentService;

    MWTCircleService circleService;

    private MWTTalentManager() {
        talentService = MWTRestManager.getInstance().create(MWTTalentService.class);
    }

    public static MWTTalentManager getInstance() {
        if (s_instance == null) {
            s_instance = new MWTTalentManager();
        }

        return s_instance;
    }

    public ArrayList<MWTTalent> getTalents() {
        return talents;
    }

    public MWTUser getCategoryByID(String categoryID) {
        return userHashMap.get(categoryID);
    }

    public MWTTalentService getTalentService() {
        return talentService;
    }

    public MWTCircleService getCircleService() {
        return MWTRestManager.getInstance().create(MWTCircleService.class);
    }

    public void refreshTalents(int count, long max_item_timestamp, final MWTCallback callback) {
        getTalentService().queryTalents(count, max_item_timestamp, new Callback<MWTTalentResult>() {
            @Override
            public void success(MWTTalentResult result, Response response) {
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

                List<MWTUser> users = registerTalentDatas(userDatas);

                List<MWTAssetData> recommendedAssetDatas = result.recommendedUserAssets;
                if (recommendedAssetDatas == null) {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedAssets信息。"));
                    return;
                }
                List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(recommendedAssetDatas);


                // 建立用户与图片信息的关联关系，通过asset找到对应的user
                List<MWTTalent> talents = new ArrayList<MWTTalent>();
                for (MWTAsset asset : assets) {
                    MWTTalent talent = new MWTTalent();
                    talent.setAsset(asset);
                    MWTUser user = getUserByAssetId(users, asset.getOwnerUserID());
                    if (user != null) {
                        talent.setUserID(user.getUserID());
                        talent.setPrivateInfo(user.getPrivateInfo());
                        talent.setAvatarImageInfo(user.getAvatarImageInfo());
                        talent.setNickname(user.getNickname());
                        talent.setFellowshipInfo(user.getmwtFellowshipInfo());
                    }
                    talents.add(talent);
                }

                updateRootCategories(talents);

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

    public void refreshTalents2(int count, long max_item_timestamp, final MWTCallback callback) {
        MWTSquareService squareService = MWTSquareFeedManager.getInstance().getService();
        squareService.fetchItems(count, 0, max_item_timestamp, new Callback<MWTFeedRefreshResult>() {
            @Override
            public void success(MWTFeedRefreshResult result, Response response) {
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
                List<MWTUser> users = registerTalentDatas(result.relatedUsers);

                List<MWTAssetData> assetDatas = new ArrayList<MWTAssetData>();
                for (MWTFeedItemData feed : result.feedFragment.items) {
                    assetDatas.add(feed.asset);
                }
                List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(assetDatas);

                // 建立用户与图片信息的关联关系，通过asset找到对应的user
                List<MWTTalent> talents = new ArrayList<MWTTalent>();
                for (MWTAsset asset : assets) {
                    MWTTalent talent = new MWTTalent();
                    talent.setAsset(asset);
                    MWTUser user = getUserByAssetId(users, asset.getOwnerUserID());
                    if (user != null) {
                        talent.setUserID(user.getUserID());
                        talent.setPrivateInfo(user.getPrivateInfo());
                        talent.setAvatarImageInfo(user.getAvatarImageInfo());
                        talent.setNickname(user.getNickname());
                        talent.setFellowshipInfo(user.getmwtFellowshipInfo());
                    }
                    talents.add(talent);
                }
//                // 建立用户与图片信息的关联关系，通过user找到对应的asset
//                List<MWTTalent> talents = new ArrayList<MWTTalent>();
//                for (MWTUser user : users) {
//                    MWTTalent talent = new MWTTalent();
//                    talent.setUserID(user.getUserID());
//                    talent.setUserID(user.getUserID());
//                    talent.setPrivateInfo(user.getPrivateInfo());
//                    talent.setAvatarImageInfo(user.getAvatarImageInfo());
//                    talent.setNickname(user.getNickname());
//                    talent.setFellowshipInfo(user.getmwtFellowshipInfo());
//                    List<MWTAsset> assetList = getAssetByUserID(assets, user.getUserID().toString());
//                    if (assetList.size() > 0)
//                        talent.setAsset(assetList.get(0));
//                    talents.add(talent);
//                }
                updateRootCategories(talents);

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

    private void updateRootCategories(List<MWTTalent> categories) {
        talents = new ArrayList<>(categories);
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

    private MWTUser getUserByAssetId(List<MWTUser> users, String userID) {
        for (MWTUser user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }
}
