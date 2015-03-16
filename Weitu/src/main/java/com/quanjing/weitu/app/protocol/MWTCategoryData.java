package com.quanjing.weitu.app.protocol;

import com.quanjing.weitu.app.model.MWTImageInfo;

public class MWTCategoryData
{
    public String categoryID;
    public String categoryName;
    public Integer priority;
    public MWTImageInfo coverImageInfo;
    public String feedID;
    public String GroupName;
    public Boolean IsMultiLevel;

    public String getRealCategoryID()
    {
        if (IsMultiLevel != null && IsMultiLevel.booleanValue())
        {
            return feedID;
        }
        else
        {
            return categoryID;
        }
    }
}
