package com.quanjing.weitu.app.model;

import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.quanjing.weitu.app.MWTConfig;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

import java.util.Date;

public class MWTRestManager
{
    private static MWTRestManager s_instance;
    private RestAdapter _restAdapter;

    private MWTRestManager()
    {
        Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                        .registerTypeAdapter(Date.class, new DateTypeAdapter())
                        .create();

        ErrorHandler errorHandler = new ErrorHandler()
        {
            @Override
            public Throwable handleError(RetrofitError retrofitError)
            {
                if (retrofitError != null && retrofitError.getMessage() != null)
                {
                    Log.e("retrofit", retrofitError.getMessage());
                }
                return retrofitError;
            }
        };

        RequestInterceptor requestInterceptor = new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request)
            {
                request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2141.0 Safari/537.36");
                request.addHeader("Accept", "application/json");

                MWTAccessToken accessToken = MWTAuthManager.getInstance().getAccessToken();
                if (accessToken != null)
                {
                    String tokenValue = accessToken.getTokenValue();
                    if (tokenValue != null)
                    {
                        request.addHeader("Authorization", "Bearer " + tokenValue);
                    }
                }
            }
        };

        //        System.setProperty("http.proxyHost", "192.168.5.194");
        //        System.setProperty("http.proxyPort", "7777");

        _restAdapter = new RestAdapter.Builder()
                           .setEndpoint(MWTConfig.getInstance().getAPIBaseURL())
                                //                           .setClient(client)
                           .setErrorHandler(errorHandler)
                           .setRequestInterceptor(requestInterceptor)
                           .setConverter(new GsonConverter(gson))
                           .setLogLevel(RestAdapter.LogLevel.FULL)
                           .build();
    }

    public static MWTRestManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTRestManager();
        }

        return s_instance;
    }

    public <T> T create(Class<T> service)
    {
        return _restAdapter.create(service);
    }
}
