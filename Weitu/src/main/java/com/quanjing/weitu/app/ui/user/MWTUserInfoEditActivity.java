package com.quanjing.weitu.app.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTImageInfo;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.io.File;

public class MWTUserInfoEditActivity extends MWTBase2Activity implements ImageChooserListener {
    public final static String ARG_USER_ID = "ARG_USER_ID";
    private final static int MENU_SAVE = 0x3453;

    private final static int MENU_AVATAR_FROM_CAPTURE = 0x123;
    private final static int MENU_AVATAR_FROM_GALLERY = 0x456;

    private Button _avatarButton;
    private Button _nicknameButton;
    private Button _signatureButton;
    private CircularImageView _avatarImageView;
    private TextView _nicknameTextView;
    private TextView _signatureTextView;
    private TextView _cellphoneTextView;
    private Button _saveBtn;

    private MWTUser _user;

    private String _updatedNickname;
    private String _updatedSignature;
    private String _updatedAvatarFilename;

    private ImageChooserManager _imageChooserManager;
    private int _chooserType;
    private String _filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info_edit);
        setTitleText("编辑");
        setupViews();

        String userID = getIntent().getStringExtra(ARG_USER_ID);
        _user = MWTUserManager.getInstance().getUserByID(userID);
        updateUI();
    }

    @Override
    public void back() {
        //super.back();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem menuItem = menu.add(Menu.NONE, MENU_SAVE, 1, "保存");
        //部分系统报错
//        SpannableString s = new SpannableString("保存");
//        int color = MWTThemer.getInstance().getActionBarForegroundColor();
//        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
//
//        try {
//            menuItem.setTitle(s);
//        } catch (IllegalArgumentException e) {
//            menuItem.setTitle("保存");
//        }
//        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SAVE:
                onSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSave() {
        if (_nicknameTextView.getText().toString().equals("")) {
            SVProgressHUD.showInViewWithoutIndicator(MWTUserInfoEditActivity.this, "抱歉，昵称不能为空。", 2.f);
            return;
        }

        if (_updatedNickname == null && _updatedSignature == null && _updatedAvatarFilename == null) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        SVProgressHUD.showInView(this, "保存中，请稍候...", true);
        MWTUserManager.getInstance().modifyUserMe(_updatedNickname,
                _updatedSignature,
                _updatedAvatarFilename,
                new MWTCallback() {
                    @Override
                    public void success() {
                        SVProgressHUD.dismiss(MWTUserInfoEditActivity.this);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void failure(MWTError error) {
                        SVProgressHUD.showInViewWithoutIndicator(MWTUserInfoEditActivity.this, "保存出错，请检查您的网络。", 2);
                    }
                });
    }

    private void setupViews() {
        _avatarButton = (Button) findViewById(R.id.AvatarButton);
        _avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAvatar();
            }
        });

        _nicknameButton = (Button) findViewById(R.id.NicknameButton);
        _nicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickname();
            }
        });

        _signatureButton = (Button) findViewById(R.id.SignatureButton);
        _signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSignature();
            }
        });

        _avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        _nicknameTextView = (TextView) findViewById(R.id.NicknameTextView);
        _signatureTextView = (TextView) findViewById(R.id.SignatureTextView);
        _cellphoneTextView = (TextView) findViewById(R.id.CellphoneTextView);

        _saveBtn = (Button) findViewById(R.id.user_save);
        _saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });
    }

    private void updateUI() {
        updateTextViews();
        updateAvatarImageView();
    }

    private void updateTextViews() {
        if (_user != null) {
            _nicknameTextView.setText(getPresentingNickname());
            _signatureTextView.setText(getPresentingSignature());

            String cellphone = _user.getPrivateInfo().getCellphone();
            if (cellphone != null) {
                _cellphoneTextView.setText(cellphone);
            } else {
                _cellphoneTextView.setText("无");
            }
        } else {
            _nicknameTextView.setText("");
            _signatureTextView.setText("");
            _cellphoneTextView.setText("");
        }
    }

    private void updateAvatarImageView() {
        MWTImageInfo imageInfo = null;
        if (_user != null) {
            imageInfo = _user.getAvatarImageInfo();
        }

        if (_updatedAvatarFilename != null) {
            File imageFile = new File(_updatedAvatarFilename);
            int width = _avatarImageView.getWidth();
            if (width <= 0) {
                width = 150;
            }
            Picasso.with(this)
                    .load(imageFile)
                    .resize(width, width)
                    .centerCrop()
                    .into(_avatarImageView);
        } else if (imageInfo != null) {
            int color = Color.WHITE;
            try {
                color = Color.parseColor("#" + imageInfo.primaryColorHex);
            } catch (RuntimeException ignored) {

            }
            int width = _avatarImageView.getWidth();
            width = Math.min(imageInfo.width, width);
            if (width <= 0) {
                width = 150;
            }
            Picasso.with(this)
                    .load(imageInfo.url)
                    .resize(width, width)
                    .centerCrop()
                    .placeholder(new ColorDrawable(color))
                    .into(_avatarImageView);
        } else {
            _avatarImageView.setImageDrawable(null);
        }
    }

    private void editAvatar() {
        PopupMenu popupMenu = new PopupMenu(this, _avatarButton);
        popupMenu.getMenu().add(Menu.NONE, MENU_AVATAR_FROM_CAPTURE, 1, "相机拍摄");
        popupMenu.getMenu().add(Menu.NONE, MENU_AVATAR_FROM_GALLERY, 2, "图库选取");

        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case MENU_AVATAR_FROM_CAPTURE:
                                takePicture();
                                break;
                            case MENU_AVATAR_FROM_GALLERY:
                                chooseImage();
                                break;
                        }
                        return true;
                    }
                });

        popupMenu.show();
    }

    private void chooseImage() {
        _chooserType = ChooserType.REQUEST_PICK_PICTURE;
        _imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        try {
            _filePath = _imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        _chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        _imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        try {
            _filePath = _imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPresentingNickname() {
        if (_updatedNickname != null) {
            return _updatedNickname;
        } else if (_user.getNickname() != null) {
            return _user.getNickname();
        } else {
            return "";
        }
    }

    private String getPresentingSignature() {
        if (_updatedSignature != null) {
            return _updatedSignature;
        } else if (_user.getSignature() != null) {
            return _user.getSignature();
        } else {
            return "";
        }
    }

    private void editNickname() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("编辑昵称");

        final EditText editText = new EditText(this);
        editText.setSingleLine();
        editText.setText(getPresentingNickname());
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setSelection(editText.getText().length());
            }
        });
        alert.setView(editText);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String updatedNickname = editText.getText().toString();
                        if (updatedNickname.length() > 0) {
                            _updatedNickname = updatedNickname;
                            updateTextViews();
                        } else {
                            SVProgressHUD.showInViewWithoutIndicator(MWTUserInfoEditActivity.this, "抱歉，昵称不能为空。", 2.f);
                        }
                    }
                });

        alert.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    private void editSignature() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("编辑个性签名");

        final EditText editText = new EditText(this);
        editText.setText(getPresentingSignature());
        editText.setLines(5);
        editText.setGravity(Gravity.TOP | Gravity.LEFT);
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setSelection(editText.getText().length());
                editText.requestFocus();
            }
        });
        alert.setView(editText);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String updatedSignature = editText.getText().toString();
                        _updatedSignature = updatedSignature;
                        updateTextViews();
                    }
                });

        alert.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (_imageChooserManager == null) {
                reinitializeImageChooser();
            }
            _imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    _updatedAvatarFilename = image.getFileThumbnail();
                    updateAvatarImageView();
                }
            }
        });
    }

    private void reinitializeImageChooser() {
        _imageChooserManager = new ImageChooserManager(this, _chooserType, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        _imageChooserManager.reinitialize(_filePath);
    }

    @Override
    public void onError(String reason) {
        SVProgressHUD.showInViewWithoutIndicator(this, "无法选择照片，请重试。", 3.f);
    }
}
