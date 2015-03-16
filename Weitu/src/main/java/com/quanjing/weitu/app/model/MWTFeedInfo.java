package com.quanjing.weitu.app.model;

import java.util.Date;

public class MWTFeedInfo
{
    private String _feedID;
    private String _nameZH;
    private String _nameEN;
    private Date _lastUpdateTime;
    private long _generation;

    public MWTFeedInfo(String feedID, String nameZH, String nameEN)
    {
        _feedID = feedID;
        _nameZH = nameZH;
        _nameEN = nameEN;
        _lastUpdateTime = new Date();
        _generation = 1;
    }

    public String getFeedID()
    {
        return _feedID;
    }

    public String getNameZH()
    {
        return _nameZH;
    }

    public String getNameEN()
    {
        return _nameEN;
    }

    public Date getLastUpdateTime()
    {
        return _lastUpdateTime;
    }

    public long getGeneration()
    {
        return _generation;
    }
}
