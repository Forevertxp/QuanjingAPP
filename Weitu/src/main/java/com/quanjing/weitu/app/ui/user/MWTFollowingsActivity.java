package com.quanjing.weitu.app.ui.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTFollowerResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTFollowingsActivity extends MWTBase2Activity {

    private GridView follwerGridView;
    private FollowGridAdapter adapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtfollowings);
        setTitleText("关注");

        userID = getIntent().getStringExtra("userID");
        loadFollowers();

        follwerGridView = (GridView) findViewById(R.id.grid_followings);
    }

    private void loadFollowers() {

        SVProgressHUD.showInView(MWTFollowingsActivity.this, "加载中，请稍候...", true);

        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryFollowings(userID, 0, 300, new Callback<MWTFollowerResult>() {

            @Override
            public void success(MWTFollowerResult mwtFollowerResult, Response response) {
                SVProgressHUD.dismiss(MWTFollowingsActivity.this);

                List<MWTUserData> followerResults = mwtFollowerResult.users;
                adapter = new FollowGridAdapter(MWTFollowingsActivity.this, followerResults);
                follwerGridView.setAdapter(adapter);
                follwerGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MWTUserData userData = (MWTUserData) adapter.getItem(i);
                        if (userData != null) {
                            Intent intent = new Intent(MWTFollowingsActivity.this, MWTOtherUserActivity.class);
                            intent.putExtra("userID", userData.userID);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(MWTFollowingsActivity.this);
                Context ctx = MWTFollowingsActivity.this;
                if (ctx != null) {
                    Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class FollowGridAdapter extends BaseAdapter {

        private Context context;
        private List<MWTUserData> datas = new ArrayList<>();

        public FollowGridAdapter(Context context, List<MWTUserData> datas) {
            this.context = context;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView.inflate(context, R.layout.item_grid_follow, null);
            XCRoundImageView tx_img = (XCRoundImageView) v.findViewById(R.id.ItemImage);
            TextView tx_name = (TextView) v.findViewById(R.id.ItemText);
            MWTUserData user = datas.get(position);
            tx_name.setText((String) user.nickname);
            Picasso.with(context)
                    .load(user.avatarImageInfo.url)
                    .centerCrop().resize(200, 200)
                    .placeholder(new ColorDrawable(Color.WHITE))
                    .into(tx_img);
            return v;
        }

    }

}
