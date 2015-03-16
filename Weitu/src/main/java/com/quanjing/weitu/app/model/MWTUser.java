package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTFellowshipInfoData;
import com.quanjing.weitu.app.protocol.MWTUserAssetsInfoData;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.MWTUserPrivateInfoData;
import com.quanjing.weitu.app.protocol.service.MWTAssetsResult;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTUser implements Serializable {
    private String _userID;
    private String _nickname;
    private String _signature;
    private MWTImageInfo _avatarImageInfo;

    private MWTUserPrivateInfo _privateInfo;
    private MWTUserAssetsInfo _assetsInfo;
    private MWTFellowshipInfo _mwtFellowshipInfo;

    private boolean _isDataDirty = false;

    public String getUserID() {
        return _userID;
    }

    public String getNickname() {
        return _nickname;
    }

    public String getSignature() {
        return _signature;
    }

    public MWTImageInfo getAvatarImageInfo() {
        return _avatarImageInfo;
    }

    public MWTUserPrivateInfo getPrivateInfo() {
        return _privateInfo;
    }

    public MWTFellowshipInfo getmwtFellowshipInfo() {
        return _mwtFellowshipInfo;
    }

    public MWTUserAssetsInfo getAssetsInfo() {
        return _assetsInfo;
    }

    public void markDataDirty() {
        _isDataDirty = true;
    }

    public boolean checkAndCleanDataDirty() {
        boolean isDataDirty = _isDataDirty;
        _isDataDirty = false;
        return isDataDirty;
    }

    public void clearDownloadedAssetsInfo() {
        if (_assetsInfo != null) {
            _assetsInfo.setDownloadedAssets(null);
        }
    }

    public void clearSharedAssetsInfo() {
        if (_assetsInfo != null) {
            _assetsInfo.setSharedAssets(null);
        }
    }

    public void mergeWithData(MWTUserData userData) {
        if (_userID == null) {
            _userID = userData.userID;
        } else {
            if (!_userID.equalsIgnoreCase(userData.userID)) {
                return;
            }
        }

        if (userData.nickname != null) {
            _nickname = userData.nickname;
        }

        if (userData.signature != null) {
            _signature = userData.signature;
        }

        if (userData.avatarImageInfo != null) {
            _avatarImageInfo = userData.avatarImageInfo;
        }

        mergeWithPrivateInfoData(userData.privateInfo);
        mergeWithAssetsInfoData(userData.assetsInfo);
        mergeWithFellowshifInfoData(userData.fellowshipInfo);
    }

    private void mergeWithPrivateInfoData(MWTUserPrivateInfoData privateInfoData) {
        if (privateInfoData == null) {
            return;
        }

        if (_privateInfo == null) {
            _privateInfo = new MWTUserPrivateInfo();
        }

        _privateInfo.mergeWithData(privateInfoData);
    }

    private void mergeWithFellowshifInfoData(MWTFellowshipInfoData fellowshipInfoData) {
        if (fellowshipInfoData == null) {
            return;
        }

        if (_mwtFellowshipInfo == null) {
            _mwtFellowshipInfo = new MWTFellowshipInfo();
        }

        _mwtFellowshipInfo.mergeWithData(fellowshipInfoData);
    }

    private void mergeWithAssetsInfoData(MWTUserAssetsInfoData assetsInfoData) {
        if (assetsInfoData == null) {
            return;
        }

        if (_assetsInfo == null) {
            _assetsInfo = new MWTUserAssetsInfo();
        }

        _assetsInfo.mergeWithData(assetsInfoData);
    }

    // --- Public Info

    public boolean isPublicInfoAvailable() {
        return _assetsInfo != null && _assetsInfo != null;
    }

    public void refreshPublicInfo(final MWTCallback callback) {
        MWTUserService userService = MWTUserManager.getInstance().getUserService();
        userService.queryUserPublicInfo(_userID,
                new Callback<MWTUserResult>() {
                    @Override
                    public void success(MWTUserResult result, Response response) {
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

                        MWTUserData userData = result.user;
                        if (userData == null) {
                            callback.failure(new MWTError(-1, "服务器返回数据错误，缺少user信息。"));
                            return;
                        }

                        mergeWithData(userData);

                        List<MWTAssetData> relatedAssetDatas = result.relatedAssets;
                        if (relatedAssetDatas == null) {
                            callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedAssets信息。"));
                            return;
                        }
                        MWTAssetManager.getInstance().registerAssetDatas(relatedAssetDatas);

                        List<MWTUserData> relatedUserDatas = result.relatedUsers;
                        if (relatedUserDatas == null) {
                            callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedUsers信息。"));
                            return;
                        }
                        MWTUserManager.getInstance().registerUserDatas(relatedUserDatas);

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

    // --- Liked Assets Info

    public void refreshUploadedAssets(final MWTCallback callback) {
        fetchUploadedAssets(0, 50, true, callback);
    }

    public void loadMoreUploadedAssets(final MWTCallback callback) {
        int startIndex;
        if (_assetsInfo != null && _assetsInfo.getUploadedAssets() != null) {
            startIndex = _assetsInfo.getUploadedAssets().size();
        } else {
            startIndex = 0;
        }

        fetchUploadedAssets(startIndex, 50, false, callback);
    }

    private void fetchUploadedAssets(final int startIndex, final int count, final boolean dropOld, final MWTCallback callback) {
        if (!isPublicInfoAvailable()) {
            refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    fetchUploadedAssets(startIndex, count, dropOld, callback);
                }

                @Override
                public void failure(MWTError error) {
                    if (callback != null) {
                        callback.failure(error);
                    }
                }
            });
        } else {
            MWTUserService userService = MWTUserManager.getInstance().getUserService();
            userService.queryUserUploadAssets(_userID, startIndex, count,
                    new Callback<MWTAssetsResult>() {
                        @Override
                        public void success(MWTAssetsResult result, Response response) {
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

                            List<MWTAssetData> assetDatas = result.assets;
                            if (assetDatas == null) {
                                callback.failure(new MWTError(-1, "服务器返回数据错误，缺少assets信息。"));
                                return;
                            }

                            List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(assetDatas);
                            if (dropOld) {
                                _assetsInfo.setUploadedAssets(new ArrayList<MWTAsset>(assets));
                            } else {
                                if (_assetsInfo.getUploadedAssets() == null) {
                                    _assetsInfo.setUploadedAssets(new ArrayList<MWTAsset>(assets));
                                } else {
                                    _assetsInfo.getUploadedAssets().addAll(assets);
                                }
                            }

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
    }

    // --- Liked Assets Info

    public void refreshLikedAssets(final MWTCallback callback) {
        fetchLikedAssets(0, 50, true, callback);
    }

    public void loadMoreLikedAssets(final MWTCallback callback) {
        int startIndex;
        if (_assetsInfo != null && _assetsInfo.getLikedAssets() != null) {
            startIndex = _assetsInfo.getLikedAssets().size();
        } else {
            startIndex = 0;
        }

        fetchLikedAssets(startIndex, 50, false, callback);
    }

    private void fetchLikedAssets(final int startIndex, final int count, final boolean dropOld, final MWTCallback callback) {
        if (!isPublicInfoAvailable()) {
            refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    fetchLikedAssets(startIndex, count, dropOld, callback);
                }

                @Override
                public void failure(MWTError error) {
                    if (callback != null) {
                        callback.failure(error);
                    }
                }
            });
        } else {
            MWTUserService userService = MWTUserManager.getInstance().getUserService();
            userService.queryUserLikedAssets(_userID, startIndex, count,
                    new Callback<MWTAssetsResult>() {
                        @Override
                        public void success(MWTAssetsResult result, Response response) {
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

                            List<MWTAssetData> assetDatas = result.assets;
                            if (assetDatas == null) {
                                callback.failure(new MWTError(-1, "服务器返回数据错误，缺少assets信息。"));
                                return;
                            }

                            List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(assetDatas);
                            if (dropOld) {
                                _assetsInfo.setLikedAssets(new ArrayList<MWTAsset>(assets));
                            } else {
                                if (_assetsInfo.getLikedAssets() == null) {
                                    _assetsInfo.setLikedAssets(new ArrayList<MWTAsset>(assets));
                                } else {
                                    _assetsInfo.getLikedAssets().addAll(assets);
                                }
                            }

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
    }

    // --- Shared Assets Info

    public void refreshSharedAssets(final MWTCallback callback) {
        fetchSharedAssets(0, 50, true, callback);
    }

    public void loadMoreSharedAssets(final MWTCallback callback) {
        int startIndex;
        if (_assetsInfo != null && _assetsInfo.getSharedAssets() != null) {
            startIndex = _assetsInfo.getSharedAssets().size();
        } else {
            startIndex = 0;
        }

        fetchSharedAssets(startIndex, 50, false, callback);
    }

    private void fetchSharedAssets(final int startIndex, final int count, final boolean dropOld, final MWTCallback callback) {
        if (!isPublicInfoAvailable()) {
            refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    fetchSharedAssets(startIndex, count, dropOld, callback);
                }

                @Override
                public void failure(MWTError error) {
                    if (callback != null) {
                        callback.failure(error);
                    }
                }
            });
        } else {
            MWTUserService userService = MWTUserManager.getInstance().getUserService();
            userService.queryUserSharedAssets(_userID, startIndex, count,
                    new Callback<MWTAssetsResult>() {
                        @Override
                        public void success(MWTAssetsResult result, Response response) {
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

                            List<MWTAssetData> assetDatas = result.assets;
                            if (assetDatas == null) {
                                callback.failure(new MWTError(-1, "服务器返回数据错误，缺少assets信息。"));
                                return;
                            }

                            List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(assetDatas);
                            if (dropOld) {
                                _assetsInfo.setSharedAssets(new ArrayList<MWTAsset>(assets));
                            } else {
                                if (_assetsInfo.getSharedAssets() == null) {
                                    _assetsInfo.setSharedAssets(new ArrayList<MWTAsset>(assets));
                                } else {
                                    _assetsInfo.getSharedAssets().addAll(assets);
                                }
                            }

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
    }

    // download assets info
    public void refreshDownloadedAssets(final MWTCallback callback) {
        fetchDownloadedAssets(0, 50, true, callback);
    }

    public void loadMoreDownloadedAssets(final MWTCallback callback) {
        int startIndex;
        if (_assetsInfo != null && _assetsInfo.getDownloadedAssets() != null) {
            startIndex = _assetsInfo.getDownloadedAssets().size();
        } else {
            startIndex = 0;
        }

        fetchDownloadedAssets(startIndex, 50, false, callback);
    }

    private void fetchDownloadedAssets(final int startIndex, final int count, final boolean dropOld, final MWTCallback callback) {
        if (!isPublicInfoAvailable()) {
            refreshPublicInfo(new MWTCallback() {
                @Override
                public void success() {
                    fetchDownloadedAssets(startIndex, count, dropOld, callback);
                }

                @Override
                public void failure(MWTError error) {
                    if (callback != null) {
                        callback.failure(error);
                    }
                }
            });
        } else {
            MWTUserService userService = MWTUserManager.getInstance().getUserService();
            userService.queryUserDownloadedAssets(_userID, startIndex, count,
                    new Callback<MWTAssetsResult>() {
                        @Override
                        public void success(MWTAssetsResult result, Response response) {
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

                            List<MWTAssetData> assetDatas = result.assets;
                            if (assetDatas == null) {
                                callback.failure(new MWTError(-1, "服务器返回数据错误，缺少assets信息。"));
                                return;
                            }

                            List<MWTAsset> assets = MWTAssetManager.getInstance().registerAssetDatas(assetDatas);
                            if (dropOld) {
                                _assetsInfo.setDownloadedAssets(new ArrayList<MWTAsset>(assets));
                            } else {
                                if (_assetsInfo.getDownloadedAssets() == null) {
                                    _assetsInfo.setDownloadedAssets(new ArrayList<MWTAsset>(assets));
                                } else {
                                    _assetsInfo.getDownloadedAssets().addAll(assets);
                                }
                            }

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
    }
}
