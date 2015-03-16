package com.quanjing.weitu.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTUtils;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import org.lcsky.SVProgressHUD;

public class MWTSMSAuthRequestActivity extends MWTBase2Activity
{
    public final static String ARG_IS_REGISTERING = "ARG_IS_REGISTERING";

    private boolean _isRegistering;

    private EditText _cellphoneEditText;
    private Button _actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsauth_request);
        setTitleText("登 录   ");

        _isRegistering = getIntent().getBooleanExtra(ARG_IS_REGISTERING, false);

        setupViews();

        _cellphoneEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _cellphoneEditText.requestFocus();
    }

    private void setupViews()
    {
        _cellphoneEditText = (EditText) findViewById(R.id.EditText);

        _actionButton = (Button) findViewById(R.id.ActionButton);
        _actionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestAuthCode();
            }
        });

        setupLicenseTextView();
    }

    private void setupLicenseTextView()
    {
        TextView licenceTextView = (TextView) findViewById(R.id.LicenseTextView);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String regularText = "点击获取验证码代表您同意";
        String clickableText = "《全景服务条款》";
        sb.append(regularText);
        sb.append(clickableText);

        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                Intent intent = new Intent(MWTSMSAuthRequestActivity.this, MWTLicenseActivity.class);
                startActivity(intent);
            }
        };
        sb.setSpan(clickableSpan, sb.length() - clickableText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        licenceTextView.setText(sb);
        licenceTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void requestAuthCode()
    {
        final String cellphone = _cellphoneEditText.getText().toString();
        if (!MWTUtils.isValidCellphoneNumber(cellphone))
        {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            _cellphoneEditText.requestFocus();
            return;
        }

        SVProgressHUD.showInView(this, "请求验证码，请稍候...", true);

        MWTAuthManager am = MWTAuthManager.getInstance();
        am.requestSMSAuthCode(cellphone, new MWTCallback()
        {
            @Override
            public void success()
            {
                SVProgressHUD.dismiss(MWTSMSAuthRequestActivity.this);

                Intent intent = new Intent(MWTSMSAuthRequestActivity.this, MWTSMSAuthVerifyActivity.class);
                intent.putExtra(MWTSMSAuthVerifyActivity.ARG_IS_REGISTERING, _isRegistering);
                intent.putExtra(MWTSMSAuthVerifyActivity.ARG_CELLPHONE, cellphone);
                startActivityForResult(intent, 0);
            }

            @Override
            public void failure(MWTError error)
            {
                SVProgressHUD.dismiss(MWTSMSAuthRequestActivity.this);
                Toast.makeText(MWTSMSAuthRequestActivity.this, error.getMessageWithPrompt("请求验证码失败"), Toast.LENGTH_SHORT).show();
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
