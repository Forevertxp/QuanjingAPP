package com.quanjing.weitu.app.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAccessToken;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import org.lcsky.SVProgressHUD;

public class MWTSMSAuthVerifyActivity extends MWTBase2Activity
{
    public static final String ARG_IS_REGISTERING = "ARG_IS_REGISTERING";
    public static final String ARG_CELLPHONE = "ARG_CELLPHONE";

    private final static int EDIT_USER_INFO = 0x123123;

    private boolean _isRegistering;
    private String _cellphone;

    private TextView _cellphoneTextView;
    private EditText _authCodeEditText;
    private Button _resendButton;
    private Button _actionButton;

    private CountDownTimer _timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsauth_verify);
        setTitleText("注 册   ");

        _isRegistering = getIntent().getBooleanExtra(ARG_IS_REGISTERING, false);
        _cellphone = getIntent().getStringExtra(ARG_CELLPHONE);

        setupViews();

        _authCodeEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setupViews()
    {
        _cellphoneTextView = (TextView) findViewById(R.id.CellphoneTextView);
        _cellphoneTextView.setText("您的手机号：" + _cellphone);

        _authCodeEditText = (EditText) findViewById(R.id.EditText);

        _resendButton = (Button) findViewById(R.id.ResendButton);
        _resendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resend();
            }
        });

        _actionButton = (Button) findViewById(R.id.ActionButton);
        _actionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                verify();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        _authCodeEditText.requestFocus();

        startCountDownTimer();
    }

    private void resend()
    {
        SVProgressHUD.showInView(this, "请求验证码，请稍候...", true);

        MWTAuthManager am = MWTAuthManager.getInstance();
        am.requestSMSAuthCode(_cellphone, new MWTCallback()
        {
            @Override
            public void success()
            {
                SVProgressHUD.dismiss(MWTSMSAuthVerifyActivity.this);

                startCountDownTimer();
            }

            @Override
            public void failure(MWTError error)
            {
                SVProgressHUD.dismiss(MWTSMSAuthVerifyActivity.this);
                Toast.makeText(MWTSMSAuthVerifyActivity.this, error.getMessageWithPrompt("请求验证码失败"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCountDownTimer()
    {
        if (_timer != null)
        {
            _timer.cancel();
        }

        _resendButton.setEnabled(false);

        _resendButton.setText("60秒后重发");

        _timer = new CountDownTimer(60000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                _resendButton.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            public void onFinish()
            {
                _resendButton.setText("重发");
                _resendButton.setEnabled(true);
            }
        }.start();
    }

    private void verify()
    {
        final String authCode = _authCodeEditText.getText().toString();
        if (!authCode.matches("[0-9]{6}"))
        {
            Toast.makeText(this, "请输入6位数字验证码", Toast.LENGTH_SHORT).show();
            _authCodeEditText.requestFocus();
            return;
        }

        SVProgressHUD.showInView(this, "验证中，请稍候...", true);

        MWTAuthManager am = MWTAuthManager.getInstance();
        am.verifySMSAuthCode(_cellphone, authCode, new MWTCallback()
        {
            @Override
            public void success()
            {
                SVProgressHUD.showInView(MWTSMSAuthVerifyActivity.this, "登录中，请稍候...", true);
                MWTUserManager um = MWTUserManager.getInstance();
                um.refreshCurrentUserInfo(new MWTCallback()
                {
                    @Override
                    public void success()
                    {
                        SVProgressHUD.dismiss(MWTSMSAuthVerifyActivity.this);

                        MWTAccessToken accessToken = MWTAuthManager.getInstance().getAccessToken();

                        boolean shouldShowUserInfoEditView = false;
                        if (_isRegistering)
                        {
                            shouldShowUserInfoEditView = true;
                            if (!accessToken.isNewUser())
                            {
                                Toast.makeText(MWTSMSAuthVerifyActivity.this, "您的手机号已经注册过，请编辑您的个人信息。", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            if (accessToken.isNewUser())
                            {
                                shouldShowUserInfoEditView = true;
                                Toast.makeText(MWTSMSAuthVerifyActivity.this, "您第一次使用该手机号登录，请完成注册流程。", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                // Normal
                            }
                        }

                        if (shouldShowUserInfoEditView)
                        {
                            showUserInfoEditActivity();
                        }
                        else
                        {
                            finishLogin();
                        }
                    }

                    @Override
                    public void failure(MWTError error)
                    {
                        SVProgressHUD.dismiss(MWTSMSAuthVerifyActivity.this);
                        Toast.makeText(MWTSMSAuthVerifyActivity.this, error.getMessageWithPrompt("登录失败"), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(MWTError error)
            {
                SVProgressHUD.dismiss(MWTSMSAuthVerifyActivity.this);
                Toast.makeText(MWTSMSAuthVerifyActivity.this, error.getMessageWithPrompt("验证失败"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserInfoEditActivity()
    {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        Intent intent = new Intent(this, MWTUserInfoEditActivity.class);
        intent.putExtra(MWTUserInfoEditActivity.ARG_USER_ID, user.getUserID());
        startActivityForResult(intent, EDIT_USER_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == EDIT_USER_INFO && resultCode == RESULT_OK)
        {
            finishLogin();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void finishLogin()
    {
        setResult(RESULT_OK);
        finish();
    }
}
