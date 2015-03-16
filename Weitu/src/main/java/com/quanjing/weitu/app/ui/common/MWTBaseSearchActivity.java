package com.quanjing.weitu.app.ui.common;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.EntypoIcon;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback1;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.search.MWTSearchActivity;
import com.umeng.analytics.MobclickAgent;

import org.lcsky.SVProgressHUD;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MWTBaseSearchActivity extends MWTBase2Activity {
    public final static int MENU_SEARCH = 0x1234;

    private EditText _searchViewEditText;

    private float convertDP2PX(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final SearchView searchView = new SearchView(getActionBar().getThemedContext());
        customizeSearchView(searchView);

        IconicFontDrawable searchIconDrawable = new IconicFontDrawable(this);
        searchIconDrawable.setIcon(EntypoIcon.SEARCH);
        searchIconDrawable.setIconColor(MWTThemer.getInstance().getActionBarForegroundColor());
        searchIconDrawable.setIntrinsicHeight((int) convertDP2PX(24));
        searchIconDrawable.setIntrinsicWidth((int) convertDP2PX(24));

        final MenuItem searchMenuItem = menu.add(Menu.NONE, MENU_SEARCH, 1, "搜索");
        searchMenuItem.setIcon(searchIconDrawable)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String keyword) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                _searchViewEditText.clearFocus();

                if (MWTBaseSearchActivity.this instanceof MWTSearchActivity) {
                    ((MWTSearchActivity) MWTBaseSearchActivity.this).performSearch(keyword);
                } else {
                    performSearch(keyword);
                }

                searchMenuItem.collapseActionView();

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void customizeSearchView(SearchView searchView) {
        int themeColor = MWTThemer.getInstance().getActionBarForegroundColor();

        IconicFontDrawable searchIconDrawable = new IconicFontDrawable(this);
        searchIconDrawable.setIcon(EntypoIcon.SEARCH);
        searchIconDrawable.setIconColor(themeColor);
        searchIconDrawable.setIntrinsicHeight((int) convertDP2PX(24));
        searchIconDrawable.setIntrinsicWidth((int) convertDP2PX(24));

        IconicFontDrawable closeDrawable = new IconicFontDrawable(this);
        closeDrawable.setIcon(EntypoIcon.CIRCLED_CROSS);
        closeDrawable.setIconColor(themeColor);
        closeDrawable.setIntrinsicHeight((int) convertDP2PX(24));
        closeDrawable.setIntrinsicWidth((int) convertDP2PX(24));

        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        _searchViewEditText = (EditText) searchView.findViewById(searchSrcTextId);
        _searchViewEditText.setTextColor(themeColor);
        _searchViewEditText.setHintTextColor(themeColor);

        int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonId);

        closeButtonImage.setImageDrawable(closeDrawable);

        // Accessing the SearchAutoComplete
        int queryTextViewId = getResources().getIdentifier("android:id/search_src_text", null, null);
        View autoComplete = searchView.findViewById(queryTextViewId);

        try {
            Class<?> clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");

            SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");

            // Add the icon as an spannable
            IconicFontDrawable searchIcon = new IconicFontDrawable(this);
            searchIcon.setIcon(EntypoIcon.SEARCH);
            searchIcon.setIconColor(MWTThemer.getInstance().getActionBarBackgroundColor());
            searchIcon.setIntrinsicHeight((int) convertDP2PX(24));
            searchIcon.setIntrinsicWidth((int) convertDP2PX(24));

            Method textSizeMethod = clazz.getMethod("getTextSize");
            Float rawTextSize = (Float) textSizeMethod.invoke(autoComplete);
            int textSize = (int) (rawTextSize * 1.25);
            searchIcon.setBounds(0, 0, textSize, textSize);
            ImageSpan imageSpan = new ImageSpan(searchIcon);
            stopHint.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the new hint text
            Method setHintMethod = clazz.getMethod("setHint", CharSequence.class);
            setHintMethod.invoke(autoComplete, stopHint);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_view_background));
        }
    }

    private void performSearch(final String keyword) {
        if (keyword.isEmpty()) {
            return;
        }

        SVProgressHUD.showInView(this, "搜索中，请稍候...", true);

        MWTAssetManager am = MWTAssetManager.getInstance();
        am.searchAssets(keyword, 0, 50, new MWTCallback1<List<MWTAsset>>() {
            @Override
            public void success(List<MWTAsset> assets) {
                SVProgressHUD.dismiss(MWTBaseSearchActivity.this);

                int assetNum = assets.size();
                String[] assetIDs = new String[assetNum];
                for (int i = 0; i < assetNum; ++i) {
                    assetIDs[i] = assets.get(i).getAssetID();
                }

                Intent intent = new Intent(MWTBaseSearchActivity.this, MWTSearchActivity.class);
                intent.putExtra(MWTSearchActivity.ARG_KEYWORD, keyword);
                intent.putExtra(MWTSearchActivity.ARG_ASSETIDS, assetIDs);
                startActivity(intent);
            }

            @Override
            public void failure(MWTError error) {
                SVProgressHUD.dismiss(MWTBaseSearchActivity.this);
                Toast.makeText(MWTBaseSearchActivity.this, error.getMessageWithPrompt("搜索失败"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected EditText getSearchViewEditText() {
        return _searchViewEditText;
    }
}
