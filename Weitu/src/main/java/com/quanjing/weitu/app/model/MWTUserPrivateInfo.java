package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTUserPrivateInfoData;

import java.io.Serializable;

public class MWTUserPrivateInfo implements Serializable
{
    private String _cellphone;
    private String _email;
    private String _password;

    public String getCellphone()
    {
        return _cellphone;
    }

    public String getEmail()
    {
        return _email;
    }

    public String getPassword()
    {
        return _password;
    }

    public void mergeWithData(MWTUserPrivateInfoData privateInfoData)
    {
        if (privateInfoData.cellphone != null)
        {
            _cellphone = privateInfoData.cellphone;
        }

        if (privateInfoData.email != null)
        {
            _email = privateInfoData.email;
        }

        if (privateInfoData.password != null)
        {
            _password = privateInfoData.password;
        }
    }
}
