package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;

public class MWTAuthSelectActivity extends MWTBase2Activity
{
    private Button _smsRegisterButton;
    private Button _smsLoginButton;
    private Button _passwordLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth_select);
        setTitleText("登 录   ");
        setupViews();
    }

    private void setupViews()
    {
        _smsRegisterButton = (Button) findViewById(R.id.SMSRegisterButton);
        _smsRegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MWTAuthSelectActivity.this, MWTSMSAuthRequestActivity.class);
                intent.putExtra(MWTSMSAuthRequestActivity.ARG_IS_REGISTERING, true);
                startActivityForResult(intent, 0);
            }
        });

        _smsLoginButton = (Button) findViewById(R.id.SMSLoginButton);
        _smsLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MWTAuthSelectActivity.this, MWTSMSAuthRequestActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        _passwordLoginButton = (Button) findViewById(R.id.PasswordLoginButton);
        _passwordLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MWTAuthSelectActivity.this, MWTPasswordLoginActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.CancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();
        }
    }
}
