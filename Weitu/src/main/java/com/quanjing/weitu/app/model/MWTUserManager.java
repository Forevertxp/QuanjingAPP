package com.quanjing.weitu.app.model;

import android.content.Context;

import com.quanjing.weitu.app.MWTConfig;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTAssetData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MWTUserManager
{
    private final static String USERMANAGER_CONFIG_FILENAME = "usermanager.dat";

    private static MWTUserManager s_instance;
    private MWTUser _currentUser;
    private MWTUserService _userService;
    private HashMap<String, MWTUser> _usersByID = new HashMap<>();

    private MWTUserManager()
    {
        _userService = MWTRestManager.getInstance().create(MWTUserService.class);
        load();
    }

    public static MWTUserManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTUserManager();
        }

        return s_instance;
    }

    public MWTUserService getUserService()
    {
        return _userService;
    }

    public MWTUser getCurrentUser()
    {
        return _currentUser;
    }

    public MWTUser registerUserData(MWTUserData userData)
    {
        if (userData == null)
        {
            return null;
        }

        MWTUser user = _usersByID.get(userData.userID);
        if (user == null)
        {
            user = new MWTUser();
            _usersByID.put(userData.userID, user);
        }

        user.mergeWithData(userData);

        return user;
    }

    public void logout()
    {
        if (_currentUser != null)
        {
            _currentUser = null;
            save();
        }
    }

    public List<MWTUser> registerUserDatas(Collection<MWTUserData> userDatas)
    {
        ArrayList<MWTUser> users = new ArrayList<>();

        for (MWTUserData userData : userDatas)
        {
            MWTUser user = registerUserData(userData);
            users.add(user);
        }

        return users;
    }

    public void refreshCurrentUserInfo(final MWTCallback callback)
    {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated())
        {
            callback.failure(new MWTError(-1, "尚未验证身份"));
            return;
        }

        _userService.queryUserMe(new Callback<MWTUserResult>()
        {
            @Override
            public void success(MWTUserResult result, Response response)
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

                MWTUserData userData = result.user;
                if (userData == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少user信息。"));
                    return;
                }

                MWTUser user = registerUserData(userData);
                if (_currentUser == null)
                {
                    _currentUser = user;
                }
                else
                {
                    assert _currentUser == user;
                }

                List<MWTAssetData> relatedAssetDatas = result.relatedAssets;
                if (relatedAssetDatas == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedAssets信息。"));
                    return;
                }
                MWTAssetManager.getInstance().registerAssetDatas(relatedAssetDatas);

                List<MWTUserData> relatedUserDatas = result.relatedUsers;
                if (relatedUserDatas == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedUsers信息。"));
                    return;
                }
                registerUserDatas(relatedUserDatas);

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

    private void save()
    {
        try
        {
            FileOutputStream fos = MWTConfig.getInstance().getContext().openFileOutput(
                USERMANAGER_CONFIG_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            if (_currentUser != null)
            {
                oos.writeObject(_currentUser);
                oos.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void load()
    {
        try
        {
            FileInputStream fis = MWTConfig.getInstance().getContext().openFileInput(
                USERMANAGER_CONFIG_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();

            if (readObject != null && readObject instanceof MWTUser)
            {
                _currentUser = (MWTUser) readObject;
            }
        }
        catch (IOException e)
        {
        }
        catch (ClassNotFoundException e)
        {
        }
    }

    public MWTUser getUserByID(String userID)
    {
        return _usersByID.get(userID);
    }

    public void modifyUserMe(final String updatedNickname, final String updatedSignature, final String updatedAvatarImageFilename, final MWTCallback callback)
    {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated())
        {
            callback.failure(new MWTError(-1, "尚未验证身份"));
            return;
        }

        if (_currentUser == null)
        {
            refreshCurrentUserInfo(new MWTCallback()
            {
                @Override
                public void success()
                {
                    modifyUserMe(updatedNickname, updatedSignature, updatedAvatarImageFilename, callback);
                }

                @Override
                public void failure(MWTError error)
                {
                    if (callback != null)
                    {
                        callback.failure(error);
                    }
                }
            });

            return;
        }

        String nickname = updatedNickname;
        if (nickname == null)
        {
            nickname = _currentUser.getNickname();
        }

        String signature = updatedSignature;
        if (signature == null)
        {
            signature = _currentUser.getSignature();
        }

        TypedFile avatarImageTypedFile = null;
        if (updatedAvatarImageFilename != null)
        {
            File avatarImageFile = new File(updatedAvatarImageFilename);
            avatarImageTypedFile = new TypedFile("application/octet-stream", avatarImageFile);
        }

        Callback<MWTUserResult> modifyUserMeCallback = new Callback<MWTUserResult>()
        {
            @Override
            public void success(MWTUserResult result, Response response)
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

                MWTUserData userData = result.user;
                if (userData == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少user信息。"));
                    return;
                }

                MWTUser user = registerUserData(userData);
                if (_currentUser == null)
                {
                    _currentUser = user;
                }
                else
                {
                    assert _currentUser == user;
                }

                List<MWTAssetData> relatedAssetDatas = result.relatedAssets;
                if (relatedAssetDatas == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedAssets信息。"));
                    return;
                }
                MWTAssetManager.getInstance().registerAssetDatas(relatedAssetDatas);

                List<MWTUserData> relatedUserDatas = result.relatedUsers;
                if (relatedUserDatas == null)
                {
                    callback.failure(new MWTError(-1, "服务器返回数据错误，缺少relatedUsers信息。"));
                    return;
                }
                registerUserDatas(relatedUserDatas);

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
        };

        if (avatarImageTypedFile != null)
        {
            _userService.modifyUserMe(updatedNickname, updatedSignature, avatarImageTypedFile, modifyUserMeCallback);
        }
        else
        {
            _userService.modifyUserMe(updatedNickname, updatedSignature, modifyUserMeCallback);
        }
    }
}
