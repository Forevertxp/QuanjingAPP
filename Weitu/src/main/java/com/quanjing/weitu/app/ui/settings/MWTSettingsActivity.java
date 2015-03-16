package com.quanjing.weitu.app.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.MWTConfig;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.user.MWTLicenseActivity;

public class MWTSettingsActivity extends MWTBase2Activity {
    private Button _licenseButton;
    private TextView _versionTextView;
    private Button _logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitleText("设置");
        setupViews();
    }

    private void setupViews() {
        _licenseButton = (Button) findViewById(R.id.LicenseButton);
        _licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLicense();
            }
        });

        _versionTextView = (TextView) findViewById(R.id.VersionTextView);
        _versionTextView.setText(MWTConfig.getInstance().getVersionText());

        _logoutButton = (Button) findViewById(R.id.LogoutButton);
        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void showLicense() {
        Intent intent = new Intent(this, MWTLicenseActivity.class);
        startActivity(intent);
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage("确认退出当前用户吗？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MWTUserManager.getInstance().logout();
                        MWTAuthManager.getInstance().clearAccessToken();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}
