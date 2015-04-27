package com.quanjing.weitu.app.ui.found;

import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTRotation;
import com.quanjing.weitu.app.protocol.service.MWTArticleService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Administrator on 2015/3/31.
 */
public class RotationLoader {
    public void loadRotation(final RotationCallBack callback) {
        MWTArticleService articleService = MWTRestManager.getInstance().create(MWTArticleService.class);
        articleService.fetchFoundRotations(new Callback<ArrayList<MWTRotation>>() {
            @Override
            public void success(ArrayList<MWTRotation> result, Response response) {
                callback.success(result);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });


    }

    public interface RotationCallBack {
        public void success(ArrayList<MWTRotation> rotationArrayList);
    }

}
