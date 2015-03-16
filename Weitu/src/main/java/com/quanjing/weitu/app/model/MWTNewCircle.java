package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTCircleComment;

import java.util.List;

/**
 * Created by Administrator on 2015/1/6.
 */
public class MWTNewCircle {
    private String activityID;
    private long timestamp;
    private MWTUser user;
    private List<MWTAsset> assets;
    private List<MWTUser> subjectUsers;
    private List<MWTCircleComment> circleComments;
    private String activityType;
    private String friendsOrFans;

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public List<MWTCircleComment> getCircleComments() {
        return circleComments;
    }

    public void setCircleComments(List<MWTCircleComment> circleComments) {
        this.circleComments = circleComments;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MWTUser getUser() {
        return user;
    }

    public List<MWTAsset> getAssets() {
        return assets;
    }


    public String getActivityType() {
        return activityType;
    }

    public String getFriendsOrFans() {
        return friendsOrFans;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<MWTUser> getSubjectUsers() {
        return subjectUsers;
    }

    public void setSubjectUsers(List<MWTUser> subjectUsers) {
        this.subjectUsers = subjectUsers;
    }

    public void setAssets(List<MWTAsset> assets) {
        this.assets = assets;
    }

    public void setUser(MWTUser user) {
        this.user = user;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setFriendsOrFans(String friendsOrFans) {
        this.friendsOrFans = friendsOrFans;
    }
}
