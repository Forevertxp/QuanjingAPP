package com.quanjing.weitu.app.model;

import java.io.Serializable;
import java.util.Date;

public class MWTAccessToken implements Serializable
{
    public String tokenValue;
    public Boolean isNewUser;

    public String getTokenValue()
    {
        return tokenValue;
    }

    public boolean isNewUser()
    {
        if (isNewUser != null)
        {
            return isNewUser.booleanValue();
        }
        else
        {
            return true;
        }
    }
}
