package com.quanjing.weitu.app.model;

/**
 * Created by Administrator on 2014/12/17.
 */


import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTCircleComment;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTActivityData;
import com.quanjing.weitu.app.protocol.service.MWTCircleService;
import com.quanjing.weitu.app.protocol.service.MWTNewCircleResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

public class MWTNewCircleManager {
    private static MWTNewCircleManager s_instance;
    private ArrayList<MWTNewCircle> circles = new ArrayList<>();
    private HashMap<String, MWTUser> userHashMap = new HashMap<>();
    private MWTCircleService circleService;

    private MWTNewCircleManager() {
        circleService = MWTRestManager.getInstance().create(MWTCircleService.class);
    }

    public static MWTNewCircleManager getInstance() {
        if (s_instance == null) {
            s_instance = new MWTNewCircleManager();
        }

        return s_instance;
    }

    public ArrayList<MWTNewCircle> getCircles() {
        return circles;
    }


    public MWTCircleService getCircleService() {
        return circleService;
    }

    public void refreshCircles(int count, long timestap, final MWTCallback callback) {
        getCircleService().fetchCircleItems(count, timestap, new Callback<MWTNewCircleResult>() {
            @Override
            public void success(MWTNewCircleResult result, Response response) {
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

                List<MWTUserData> userDatas = result.users;
                if (userDatas == null) {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少user信息。"));
                    return;
                }
                MWTUserManager userManager = MWTUserManager.getInstance();
                for (MWTUserData userData : userDatas) {
                    userManager.registerUserData(userData);
                }
                List<MWTAssetData> assetDatas = result.assets;
                if (assetDatas == null) {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少asset信息。"));
                    return;
                }
                MWTAssetManager assetManager = MWTAssetManager.getInstance();
                assetManager.registerAssetDatas(result.assets);


                List<MWTNewCircle> newcircles = new ArrayList<MWTNewCircle>();
                List<MWTAsset> assetList = new ArrayList<MWTAsset>();
                List<MWTAsset> likeAssetList = new ArrayList<MWTAsset>();
                List<MWTUser> userList = new ArrayList<MWTUser>();
                // 目前方法为添加i= result.activities.size()-1的数据，后续完善
//                for (int i = 0; i < result.activities.size() - 1; i++) {
//                    MWTActivityData activityData = result.activities.get(i);
//                    if (activityData.activityType.equals("upload")) {
//                        assetList.add(assetManager.getAssetByID(activityData.subjectAssetID.toString()));
//                        if (result.activities.get(i + 1).userID.toString().equals(activityData.userID.toString()) && (Math.abs(result.activities.get(i + 1).timestamp - activityData.timestamp)) < 60) {
//                            //隶属同一userid且间隔小于60s
//                        } else {
//                            MWTNewCircle newCircle = new MWTNewCircle();
//                            newCircle.setAssets(assetList);
//                            newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
//                            newCircle.setTimestamp(activityData.timestamp);
//                            newCircle.setActivityType(activityData.activityType);
//                            newcircles.add(newCircle);
//                            assetList = new ArrayList<MWTAsset>();
//                        }
//                    } else if (activityData.activityType.equals("follow")) {
//                        userList.add(userManager.getUserByID(activityData.subjectUserID.toString()));
//                        if (result.activities.get(i + 1).userID.toString().equals(activityData.userID.toString()) && (Math.abs(result.activities.get(i + 1).timestamp - activityData.timestamp)) < 60) {
//                            //隶属同一userid且间隔小于60s
//                        } else {
//                            MWTNewCircle newCircle = new MWTNewCircle();
//                            newCircle.setSubjectUsers(userList);
//                            newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
//                            newCircle.setTimestamp(activityData.timestamp);
//                            newCircle.setActivityType(activityData.activityType);
//                            newcircles.add(newCircle);
//                            userList = new ArrayList<MWTUser>();
//                        }
//                    } else if (activityData.activityType.equals("like")) {
//                        likeAssetList.add(assetManager.getAssetByID(activityData.subjectAssetID.toString()));
//                        List<MWTUser> likeUserList = new ArrayList<MWTUser>();
//                        likeUserList.add(userManager.getUserByID(activityData.subjectUserID.toString()));
//                        MWTNewCircle newCircle = new MWTNewCircle();
//                        newCircle.setAssets(likeAssetList);
//                        newCircle.setSubjectUsers(likeUserList);
//                        newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
//                        newCircle.setTimestamp(activityData.timestamp);
//                        newCircle.setActivityType(activityData.activityType);
//                        newcircles.add(newCircle);
//                        likeAssetList = new ArrayList<MWTAsset>();
//                    }
//                }


                for (int i = 0; i < result.activities.size(); i++) {
                    MWTActivityData activityData = result.activities.get(i);
                    if (activityData.activityType.equals("upload")) {
                        MWTNewCircle newCircle = new MWTNewCircle();
                        String[] assetArray = activityData.subjectAssetID.toString().split(",");
                        for (int j = 0; j < assetArray.length; j++) {
                            assetList.add(assetManager.getAssetByID(assetArray[j]));
                        }
                        newCircle.setActivityID(activityData.id);
                        newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
                        newCircle.setAssets(assetList);
                        newCircle.setTimestamp(activityData.timestamp);
                        newCircle.setActivityType(activityData.activityType);
                        newcircles.add(newCircle);
                        assetList = new ArrayList<MWTAsset>();
                    } else if (activityData.activityType.equals("follow")) {
                        MWTNewCircle newCircle = new MWTNewCircle();
                        userList.add(userManager.getUserByID(activityData.subjectUserID.toString()));
                        newCircle.setActivityID(activityData.id);
                        newCircle.setSubjectUsers(userList);
                        newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
                        newCircle.setTimestamp(activityData.timestamp);
                        newCircle.setActivityType(activityData.activityType);
                        newcircles.add(newCircle);
                        userList = new ArrayList<MWTUser>();
                    } else if (activityData.activityType.equals("like")) {
                        likeAssetList.add(assetManager.getAssetByID(activityData.subjectAssetID.toString()));
                        List<MWTUser> likeUserList = new ArrayList<MWTUser>();
                        likeUserList.add(userManager.getUserByID(activityData.subjectUserID.toString()));
                        MWTNewCircle newCircle = new MWTNewCircle();
                        newCircle.setActivityID(activityData.id);
                        newCircle.setAssets(likeAssetList);
                        newCircle.setSubjectUsers(likeUserList);
                        newCircle.setUser(userManager.getUserByID(activityData.userID.toString()));
                        newCircle.setTimestamp(activityData.timestamp);
                        newCircle.setActivityType(activityData.activityType);
                        newcircles.add(newCircle);
                        likeAssetList = new ArrayList<MWTAsset>();
                    }
                }

                for (MWTNewCircle circle:newcircles){
                    List<MWTCircleComment> newCommentList = new ArrayList<MWTCircleComment>();
                    List<MWTCircleLike> newLikeList = new ArrayList<MWTCircleLike>();
                    for (MWTCircleComment comment:result.activComment){
                        if (comment.getActivityId().equals(circle.getActivityID())){
                            newCommentList.add(comment);
                        }
                    }
                    for (MWTCircleLike like:result.activLike){
                        if (like.getActivityid().equals(circle.getActivityID())){
                            newLikeList.add(like);
                        }
                    }
                    circle.setCircleComments(newCommentList);
                    circle.setCircleLikes(newLikeList);
                }


                updateRootCategories(newcircles);

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

    private void updateRootCategories(List<MWTNewCircle> newcircles) {
        circles = new ArrayList<>(newcircles);
    }

}

