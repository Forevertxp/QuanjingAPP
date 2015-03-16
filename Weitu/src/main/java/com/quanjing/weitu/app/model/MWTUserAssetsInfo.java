package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTUserAssetsInfoData;

import java.io.Serializable;
import java.util.ArrayList;

public class MWTUserAssetsInfo implements Serializable {
    private int _publicAssetNum = 0;
    private int _privateAssetNum = 0;
    private int _uploadedAssetNum = 0;
    private int _likedAssetNum = 0;
    private int _downloadedAssetNum = 0;
    private int _sharedAssetNum = 0;
    private int _collectedAssetNum = 0;
    private int _lightbox = 0;

    private transient ArrayList<MWTAsset> _assets;
    private transient ArrayList<MWTAsset> _uploadedAssets;
    private transient ArrayList<MWTAsset> _likedAssets;
    private transient ArrayList<MWTAsset> _downloadedAssets;
    private transient ArrayList<MWTAsset> _sharedAssets;
    private transient ArrayList<MWTAsset> _collectedAssets;

    public int getPublicAssetNum() {
        return _publicAssetNum;
    }

    public int getUploadedAssetNum() {
        return _uploadedAssetNum;
    }

    public int getPrivateAssetNum() {
        return _privateAssetNum;
    }

    public int getLikedAssetNum() {
        return _likedAssetNum;
    }

    public int getDownloadedAssetNum() {
        return _downloadedAssetNum;
    }

    public int getLightboxNum() {
        return _lightbox;
    }

    public void increateDownloadedAssetNum() {
        _downloadedAssetNum++;
    }

    public int getSharedAssetNum() {
        return _sharedAssetNum;
    }

    public void increateSharedAssetNum() {
        _sharedAssetNum++;
    }

    public int getCollectedAssetNum() {
        return _collectedAssetNum;
    }

    public ArrayList<MWTAsset> getAssets() {
        return _assets;
    }

    public ArrayList<MWTAsset> getUploadedAssets() {
        return _uploadedAssets;
    }

    public ArrayList<MWTAsset> getLikedAssets() {
        return _likedAssets;
    }

    public ArrayList<MWTAsset> getDownloadedAssets() {
        return _downloadedAssets;
    }

    public ArrayList<MWTAsset> getSharedAssets() {
        return _sharedAssets;
    }

    public ArrayList<MWTAsset> getCollectedAssets() {
        return _collectedAssets;
    }

    public void setAssets(ArrayList<MWTAsset> assets) {
        _assets = assets;
    }

    public void setUploadedAssets(ArrayList<MWTAsset> uploadedAssets) {
        _uploadedAssets = uploadedAssets;
    }

    public void setLikedAssets(ArrayList<MWTAsset> likedAssets) {
        _likedAssets = likedAssets;
    }

    public void setDownloadedAssets(ArrayList<MWTAsset> downloadedAssets) {
        _downloadedAssets = downloadedAssets;
    }

    public void setSharedAssets(ArrayList<MWTAsset> sharedAssets) {
        _sharedAssets = sharedAssets;
    }

    public void setCollectedAssets(ArrayList<MWTAsset> collectedAssets) {
        _collectedAssets = collectedAssets;
    }

    public void mergeWithData(MWTUserAssetsInfoData assetsInfoData) {
        assert (assetsInfoData != null);

        if (assetsInfoData.publicAssetNum != null) {
            _publicAssetNum = assetsInfoData.publicAssetNum.intValue();
        }

        if (assetsInfoData.privateAssetNum != null) {
            _privateAssetNum = assetsInfoData.privateAssetNum.intValue();
        }

        if (assetsInfoData.uploadedAssetNum != null) {
            _uploadedAssetNum = assetsInfoData.uploadedAssetNum.intValue();
        }

        if (assetsInfoData.lightbox != null) {
            _lightbox = assetsInfoData.lightbox.intValue();
        }

        if (assetsInfoData.likedAssetNum != null) {
            _likedAssetNum = assetsInfoData.likedAssetNum.intValue();
        }

        if (assetsInfoData.down != null) {
            _downloadedAssetNum = assetsInfoData.down.intValue();
        }

        if (assetsInfoData.share != null) {
            _sharedAssetNum = assetsInfoData.share.intValue();
        }

        if (assetsInfoData.collectedAssetNum != null) {
            _collectedAssetNum = assetsInfoData.collectedAssetNum.intValue();
        }
    }
}
