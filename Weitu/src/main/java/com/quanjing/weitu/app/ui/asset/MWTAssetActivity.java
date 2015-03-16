package com.quanjing.weitu.app.ui.asset;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.icon.EntypoIcon;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTTalent;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.user.MWTAuthSelectActivity;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTAssetActivity extends MWTBase2Activity {
    public final static String ARG_ASSETID = "ARG_ASSETID";

    private MWTAssetFragment _assetFragment;
    private boolean hasLiked;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_asset);
        setTitleText("图片");

        if (savedInstanceState == null) {
            if (_assetFragment == null) {
                String assetID = getIntent().getStringExtra(ARG_ASSETID);
                _assetFragment = MWTAssetFragment.newInstance(assetID);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, _assetFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, null);
        SpannableString s = new SpannableString("\u2665喜欢");
        int color = MWTThemer.getInstance().getActionBarForegroundColor();
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);

        Typeface font = EntypoIcon.HEART.getIconicTypeface().getTypeface(this);
        s.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(3.0f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new SuperscriptSpanAdjuster(-0.075), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new SuperscriptSpanAdjuster(0.25), 1, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        //item.setTitle(s);

        MWTAssetManager am = MWTAssetManager.getInstance();
        MWTAsset asset = am.getAssetByID(getIntent().getStringExtra(ARG_ASSETID));
        if (asset != null) {
            String[] likerIDs = asset.getLikedUserIDs();
            if (isContain(likerIDs)) {
                hasLiked = true;
                item.setIcon(R.drawable.ic_liked);
            } else {
                hasLiked = false;
                item.setIcon(R.drawable.ic_like);
            }
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }

        return true;
    }

    private boolean isContain(String[] ids) {
        MWTUserManager userManager = MWTUserManager.getInstance();
        if (userManager.getCurrentUser() == null || userManager.getCurrentUser().getUserID().equals(""))
            return false;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(userManager.getCurrentUser().getUserID()))
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String assetID = getIntent().getStringExtra(ARG_ASSETID);
        if (hasLiked)
            cancelFavotite(assetID);
        else
            addFavotite(assetID);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }

    public class SuperscriptSpanAdjuster extends MetricAffectingSpan {
        double ratio = 0.5;

        public SuperscriptSpanAdjuster() {
        }

        public SuperscriptSpanAdjuster(double ratio) {
            this.ratio = ratio;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.baselineShift += (int) (paint.ascent() * ratio);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            paint.baselineShift += (int) (paint.ascent() * ratio);
        }
    }

    /**
     * 添加喜欢
     *
     * @param assetid
     */

    private void addFavotite(String assetid) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(this)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用喜欢功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MWTAssetActivity.this, MWTAuthSelectActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return;
        }

        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTUserManager userManager = MWTUserManager.getInstance();
        MWTUserService userService = restManager.create(MWTUserService.class);
        final String uid = userManager.getCurrentUser().getUserID();
        SVProgressHUD.showInView(MWTAssetActivity.this, "请稍后...", true);
        userService.addFavorite(assetid, "like", new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                SVProgressHUD.dismiss(MWTAssetActivity.this);
                item.setIcon(R.drawable.ic_liked);
                hasLiked = true;
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(MWTAssetActivity.this);
                Toast.makeText(MWTAssetActivity.this, "点赞失败", 500).show();
            }
        });

    }

    /**
     * 取消喜欢
     *
     * @param assetid
     */

    private void cancelFavotite(String assetid) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(this)
                    .setTitle("请登录")
                    .setMessage("请在登录后使用喜欢功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MWTAssetActivity.this, MWTAuthSelectActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return;
        }

        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTUserManager userManager = MWTUserManager.getInstance();
        MWTUserService userService = restManager.create(MWTUserService.class);
        final String uid = userManager.getCurrentUser().getUserID();
        SVProgressHUD.showInView(MWTAssetActivity.this, "请稍后...", true);
        userService.cancelFavorite(assetid, "unlike", new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                SVProgressHUD.dismiss(MWTAssetActivity.this);
                item.setIcon(R.drawable.ic_like);
                hasLiked = false;
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(MWTAssetActivity.this);
                Toast.makeText(MWTAssetActivity.this, "点赞失败", 500).show();
            }
        });

    }
}
