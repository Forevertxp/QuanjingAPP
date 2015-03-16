package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTFellowshipInfoData;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/12.
 */
public class MWTTalent implements Serializable {

    private String userID;
    private String nickname;
    private String signature;
    private MWTImageInfo avatarImageInfo;

    private MWTUserPrivateInfo privateInfo;
    private MWTAsset asset;
    private MWTFellowshipInfo fellowshipInfo;

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

    public MWTAsset getAsset() {
        return asset;
    }

    public void setAsset(MWTAsset asset) {
        this.asset = asset;
    }

    public MWTFellowshipInfo getFellowshipInfo() {
        return fellowshipInfo;
    }

    public void setFellowshipInfo(MWTFellowshipInfo fellowshipInfo) {
        this.fellowshipInfo = fellowshipInfo;
    }
}
