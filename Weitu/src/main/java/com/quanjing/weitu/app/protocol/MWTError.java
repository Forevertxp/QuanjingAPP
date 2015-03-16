package com.quanjing.weitu.app.protocol;

import retrofit.RetrofitError;

public class MWTError
{
    public Integer code;
    public String message;

    public MWTError(RetrofitError retrofitError)
    {
        code = -999;
        message = retrofitError.getMessage();
    }

    public MWTError(Integer code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public String getMessageWithPrompt(String prompt)
    {
        if (message == null || message.isEmpty())
        {
            return prompt;
        }
        else if (code == 1)
        {
            return prompt+ "：" + message;
        }
        else
        {
            return prompt + "：请检查您的网络。";
        }
    }
}
