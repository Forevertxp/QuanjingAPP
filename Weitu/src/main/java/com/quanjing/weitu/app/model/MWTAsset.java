package com.quanjing.weitu.app.model;

import android.util.Log;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAssetService;
import com.quanjing.weitu.app.protocol.service.MWTRelatedAssetsResult;
import com.quanjing.weitu.app.protocol.service.MWTServiceResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

public class MWTAsset
{
    private String _assetID;
    private String _oriPic;
    private String _webURL;
    private String _caption;
    private String _createTime;
    private String _ownerUserID;
    private MWTImageInfo _imageInfo;
    private List<MWTAsset> _relatedAssets;
    private List<MWTCommentData> _latestComments;
    private String[] _likedUserIDs;
    private String _commentNum;

    public String getAssetID()
    {
        return _assetID;
    }

    public void setAssetID(String assetID)
    {
        _assetID = assetID;
    }

    public String get_oriPic() {
        return _oriPic;
    }

    public void set_oriPic(String _oriPic) {
        this._oriPic = _oriPic;
    }


    public String get_webURL() {
        return _webURL;
    }

    public void set_webURL(String _webURL) {
        this._webURL = _webURL;
    }

    public String getCaption()
    {
        return _caption;
    }

    public void setCaption(String caption)
    {
        _caption = caption;
    }

    public String getOwnerUserID()
    {
        return _ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID)
    {
        _ownerUserID = ownerUserID;
    }

    public MWTImageInfo getImageInfo()
    {
        return _imageInfo;
    }

    public void setImageInfo(MWTImageInfo imageInfo)
    {
        _imageInfo = imageInfo;
    }

    public List<MWTAsset> getRelatedAssets()
    {
        return _relatedAssets;
    }

    public String[] getLikedUserIDs() {
        return _likedUserIDs;
    }

    public void setLikedUserIDs(String[] likedUserIDs) {
        this._likedUserIDs = likedUserIDs;
    }

    public String getCommentNum() {
        return _commentNum;
    }

    public void setCommentNum(String commentNum) {
        this._commentNum = commentNum;
    }

    public List<MWTCommentData> get_latestComments() {
        return _latestComments;
    }

    public void set_latestComments(List<MWTCommentData> _latestComments) {
        this._latestComments = _latestComments;
    }

    public String get_createTime() {
        return _createTime;
    }

    public void set_createTime(String _createTime) {
        this._createTime = _createTime;
    }

    public void mergeWithData(MWTAssetData assetData)
    {
        if (assetData.assetID != null)
        {
            if (_assetID == null)
            {
                _assetID = assetData.assetID;
            }
            else
            {
                if (!_assetID.equals(assetData.assetID))
                {
                    Log.e("Weitu", "OWTAsset [" + _assetID + "] merging with wrong object, ID: " + assetData.assetID + ".");
                    return;
                }
            }
        }

        if (assetData.oriPic != null)
        {
            if (_oriPic == null)
            {
                _oriPic = assetData.oriPic;
            }
            else
            {
                if (!_oriPic.equals(assetData.oriPic))
                {
                    Log.e("Weitu", "OWTAsset [" + _oriPic + "] merging with wrong object, ID: " + assetData.oriPic + ".");
                    return;
                }
            }
        }

        if (assetData.webURL != null)
        {
            if (_webURL == null)
            {
                _webURL = assetData.webURL;
            }
            else
            {
                if (!_webURL.equals(assetData.webURL))
                {
                    Log.e("Weitu", "OWTAsset [" + _webURL + "] merging with wrong object, ID: " + assetData.webURL + ".");
                    return;
                }
            }
        }

        if (assetData.caption != null)
        {
            _caption = assetData.caption;
        }

        if (assetData.createTime != null)
        {
            _createTime = assetData.createTime;
        }

        if (assetData.ownerUserID != null)
        {
            _ownerUserID = assetData.ownerUserID;
        }

        if (assetData.commentNum!=null)
        {
            _commentNum = assetData.commentNum;
        }

        if (assetData.latestComments!=null)
        {
            _latestComments = assetData.latestComments;
        }

        if (assetData.likedUserIDs!=null)
        {
            _likedUserIDs = assetData.likedUserIDs;
        }

        if (assetData.imageInfo != null)
        {
            _imageInfo = assetData.imageInfo;
            _imageInfo.width = Math.max(_imageInfo.width, 1);
            _imageInfo.height = Math.max(_imageInfo.height, 1);
        }
    }

    public void queryRelatedAssets(final MWTCallback callback)
    {
        MWTAssetService service = MWTAssetManager.getInstance().getAssetService();
        service.queryRelatedAssets(getAssetID(),
                                   new Callback<MWTRelatedAssetsResult>()
                                   {
                                       @Override
                                       public void success(MWTRelatedAssetsResult result, Response response)
                                       {
                                           if (result == null)
                                           {
                                               if (callback != null)
                                               {
                                                   callback.failure(new MWTError(-1, "服务器返回数据出错"));
                                               }
                                               return;
                                           }

                                           if (result.error != null)
                                           {
                                               if (callback != null)
                                               {
                                                   callback.failure(result.error);
                                               }
                                               return;
                                           }

                                           MWTUserManager.getInstance().registerUserDatas(result.relatedUsers);
                                           _relatedAssets = MWTAssetManager.getInstance().registerAssetDatas(result.assets);
                                           if (callback != null)
                                           {
                                               callback.success();
                                           }
                                       }

                                       @Override
                                       public void failure(RetrofitError retrofitError)
                                       {
                                           if (callback != null)
                                           {
                                               callback.failure(new MWTError(retrofitError));
                                           }
                                       }
                                   });
    }

    public void markDownloadedByCurrentUser(final MWTCallback callback)
    {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        if (user != null)
        {
            user.markDataDirty();
            user.clearDownloadedAssetsInfo();
        }

        MWTAssetService service = MWTAssetManager.getInstance().getAssetService();
        service.markDownloaded(_assetID, "like", new Callback<MWTServiceResult>()
        {
            @Override
            public void success(MWTServiceResult result, Response response)
            {
                if (result == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null)
                {
                    if (callback != null)
                    {
                        callback.failure(result.error);
                    }
                    return;
                }

                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                if (callback != null)
                {
                    callback.failure(new MWTError(-1, "服务器访问出错，请检查您的网络。"));
                }
            }
        });
    }

    public void markSharedByCurrentUser(final MWTCallback callback)
    {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        if (user != null)
        {
            user.markDataDirty();
            user.clearSharedAssetsInfo();
        }

        MWTAssetService service = MWTAssetManager.getInstance().getAssetService();
        service.markShared(_assetID, "like", new Callback<MWTServiceResult>()
        {
            @Override
            public void success(MWTServiceResult result, Response response)
            {
                if (result == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null)
                {
                    if (callback != null)
                    {
                        callback.failure(result.error);
                    }
                    return;
                }

                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                if (callback != null)
                {
                    callback.failure(new MWTError(-1, "服务器访问出错，请检查您的网络。"));
                }
            }
        });
    }
}
