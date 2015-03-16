package com.quanjing.weitu.app.ui.user;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.squareup.picasso.Picasso;

public class MWTAvatarActivity extends MWTBase2Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtavatar);
        setTitleText("头像预览");
        String imagePath = getIntent().getStringExtra("image");
        String ratio = getIntent().getStringExtra("ratio");
        ImageView avatar = (ImageView) findViewById(R.id.browse_avatar);
        if (imagePath != null && !imagePath.equals("")) {
            Picasso.with(this)
                    .load(imagePath)
                            // .resize(getResources().getDisplayMetrics().widthPixels, (int) (getResources().getDisplayMetrics().widthPixels * Double.valueOf(ratio)))
                    .into(avatar);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mwtavatar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
