package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTUtils;
import com.quanjing.weitu.app.model.MWTAccessToken;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;

import org.lcsky.SVProgressHUD;

public class MWTPasswordLoginActivity extends MWTBase2Activity
{
    private EditText _usernameEditText;
    private EditText _passwordEditText;
    private Button _actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_login);
        setTitleText("登 录   ");

        setupViews();

        _usernameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setupViews()
    {
        _usernameEditText = (EditText) findViewById(R.id.UsernameEditText);
        _passwordEditText = (EditText) findViewById(R.id.PasswordEditText);

        _actionButton = (Button) findViewById(R.id.ActionButton);
        _actionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _usernameEditText.requestFocus();
    }

    private void login()
    {
        final String username = _usernameEditText.getText().toString();
        if (username.length() <= 3)
        {
            Toast.makeText(this, "请输入正确用户名，长度至少为3个字符。", Toast.LENGTH_SHORT).show();
            _usernameEditText.requestFocus();
            return;
        }

        final String password = _passwordEditText.getText().toString();

        SVProgressHUD.showInView(this, "登录中，请稍候...", true);

        MWTAuthManager am = MWTAuthManager.getInstance();
        am.authWithUsernamePassword(username, password, new MWTCallback()
        {
            @Override
            public void success()
            {
                SVProgressHUD.dismiss(MWTPasswordLoginActivity.this);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void failure(MWTError error)
            {
                SVProgressHUD.dismiss(MWTPasswordLoginActivity.this);
                Toast.makeText(MWTPasswordLoginActivity.this, error.getMessageWithPrompt("登录失败"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
