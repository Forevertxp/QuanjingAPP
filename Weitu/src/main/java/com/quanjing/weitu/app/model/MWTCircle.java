package com.quanjing.weitu.app.model;

/**
 * Created by Administrator on 2014/12/18.
 */


import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2014/12/12.
 */
public class MWTCircle implements Serializable {

    private String userID;
    private String nickname;
    private String signature;
    private MWTImageInfo avatarImageInfo;

    private MWTUserPrivateInfo privateInfo;
    private List<MWTAsset> assetList;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public MWTImageInfo getAvatarImageInfo() {
        return avatarImageInfo;
    }

    public void setAvatarImageInfo(MWTImageInfo avatarImageInfo) {
        this.avatarImageInfo = avatarImageInfo;
    }

    public MWTUserPrivateInfo getPrivateInfo() {
        return privateInfo;
    }

    public void setPrivateInfo(MWTUserPrivateInfo privateInfo) {
        this.privateInfo = privateInfo;
    }

    public List<MWTAsset> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<MWTAsset> assetList) {
        this.assetList = assetList;
    }
}

