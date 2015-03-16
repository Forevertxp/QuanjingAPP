package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.service.MWTFeedService;

import java.util.HashMap;

public class MWTFeedManager
{
    public final static String kWTFeedIDLatestUpload = "latest";
    public final static String kWTFeedIDHome = "home";
    public final static String kWTFeedIDWallpaper = "wallpaper";
    public final static String kWTFeedIDSubscription = "subscription";
    private static MWTFeedManager s_instance;
    private HashMap<String, MWTFeed> _feedsByID = new HashMap<>();

    private MWTFeedManager()
    {
        setupDefaultFeeds();
    }

    public static MWTFeedManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTFeedManager();
        }

        return s_instance;
    }

    private void setupDefaultFeeds()
    {
        MWTFeedInfo feedInfo = new MWTFeedInfo(kWTFeedIDLatestUpload, "最新上传", "Latest Upload");
        MWTFeed feed = new MWTFeed(feedInfo);
        registerFeed(feed);

        feedInfo = new MWTFeedInfo(kWTFeedIDHome, "首页", "Home");
        feed = new MWTFeed(feedInfo);
        registerFeed(feed);

        feedInfo = new MWTFeedInfo(kWTFeedIDWallpaper, "热门壁纸", "Wallpaper");
        feed = new MWTFeed(feedInfo);
        registerFeed(feed);

        feedInfo = new MWTFeedInfo(kWTFeedIDSubscription, "我的订阅", "Subscription");
        feed = new MWTFeed(feedInfo);
        registerFeed(feed);
    }

    private void registerFeed(MWTFeed feed)
    {
        _feedsByID.put(feed.getFeedID(), feed);
    }

    public MWTFeed getHomeFeed()
    {
        return getFeedWithID(kWTFeedIDHome);
    }

    public MWTFeed getLatestUploadFeed()
    {
        return getFeedWithID(kWTFeedIDLatestUpload);
    }

    public MWTFeed getWallpaperFeed()
    {
        return getFeedWithID(kWTFeedIDWallpaper);
    }

    public MWTFeed getSubscriptionFeed()
    {
        return getFeedWithID(kWTFeedIDSubscription);
    }

    public MWTFeed getFeedWithID(String feedID)
    {
        return _feedsByID.get(feedID);
    }

    public MWTFeed getFeedForCategory(MWTCategory category)
    {
        String feedID = category.getFeedID();
        MWTFeed feed = getFeedWithID(feedID);
        if (feed == null)
        {
            MWTFeedInfo feedInfo = new MWTFeedInfo(feedID, category.getCategoryName(), category.getCategoryName());
            feed = new MWTFeed(feedInfo);
            registerFeed(feed);
        }

        return feed;
    }

    public MWTFeedService getService()
    {
        return MWTRestManager.getInstance().create(MWTFeedService.class);
    }
}
