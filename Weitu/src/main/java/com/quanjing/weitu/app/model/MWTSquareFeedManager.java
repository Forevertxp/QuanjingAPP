package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.service.MWTSquareService;

public class MWTSquareFeedManager {
    private static MWTSquareFeedManager s_instance;

    private MWTSquareFeedManager() {
    }

    public static MWTSquareFeedManager getInstance() {
        if (s_instance == null) {
            s_instance = new MWTSquareFeedManager();
        }

        return s_instance;
    }

    public MWTSquareFeed getSquareFeed() {
        return new MWTSquareFeed();
    }

    public MWTSquareService getService() {
        return MWTRestManager.getInstance().create(MWTSquareService.class);
    }
}
