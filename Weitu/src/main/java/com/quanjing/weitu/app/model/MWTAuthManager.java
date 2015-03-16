package com.quanjing.weitu.app.model;

import android.content.Context;
import com.quanjing.weitu.app.MWTConfig;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAccessTokenResult;
import com.quanjing.weitu.app.protocol.service.MWTAuthService;
import com.quanjing.weitu.app.protocol.service.MWTServiceResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.*;

public class MWTAuthManager
{
    private final static String AUTH_CONFIG_FILENAME = "auth.dat";
    private final static String CLIENT_ID = "3ae125d6e9a009a6fcce3f081f4ce5ff";

    private static MWTAuthManager s_instance;
    private MWTAuthService _authService;
    private MWTAccessToken _accessToken;

    private MWTAuthManager()
    {
        _authService = MWTRestManager.getInstance().create(MWTAuthService.class);
        load();
    }

    public static MWTAuthManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTAuthManager();
        }

        return s_instance;
    }

    public MWTAccessToken getAccessToken()
    {
        return _accessToken;
    }

    public boolean isAuthenticated()
    {
        return _accessToken != null;
    }

    public void requestSMSAuthCode(String cellphone, final MWTCallback callback)
    {
        _authService.requestSMSAuthCode(cellphone, CLIENT_ID, new Callback<MWTServiceResult>()
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
            public void failure(RetrofitError retrofitError)
            {
                if (callback != null)
                {
                    callback.failure(new MWTError(retrofitError));
                }
            }
        });
    }

    public void verifySMSAuthCode(String cellphone, String authCode, final MWTCallback callback)
    {
        _authService.verifySMSAuthCode(cellphone, CLIENT_ID, authCode, new Callback<MWTAccessTokenResult>()
        {
            @Override
            public void success(MWTAccessTokenResult result, Response response)
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

                MWTAccessToken accessToken = result.accessToken;
                if (accessToken == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                updateAccessToken(accessToken);

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

    public void authWithUsernamePassword(String username, String password, final MWTCallback callback)
    {
        _authService.authWithUsernamePassword(username, password, CLIENT_ID, new Callback<MWTAccessTokenResult>()
        {
            @Override
            public void success(MWTAccessTokenResult result, Response response)
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

                MWTAccessToken accessToken = result.accessToken;
                if (accessToken == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错，缺少accessToken"));
                    }
                    return;
                }

                updateAccessToken(accessToken);

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

    public void clearAccessToken()
    {
        _accessToken = null;
    }

    private void updateAccessToken(MWTAccessToken accessToken)
    {
        if (accessToken == null)
        {
            return;
        }

        _accessToken = accessToken;
        save();
    }

    private void save()
    {
        try
        {
            FileOutputStream fos = MWTConfig.getInstance().getContext().openFileOutput(AUTH_CONFIG_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_accessToken);
            oos.close();
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
            FileInputStream fis = MWTConfig.getInstance().getContext().openFileInput(AUTH_CONFIG_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();

            if (readObject != null && readObject instanceof MWTAccessToken)
            {
                _accessToken = (MWTAccessToken) readObject;
            }
        }
        catch (IOException e)
        {
        }
        catch (ClassNotFoundException e)
        {
        }
    }
}