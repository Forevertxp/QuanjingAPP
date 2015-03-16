package com.quanjing.weitu.app.ui.user;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTTalent;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTUserData;
import com.quanjing.weitu.app.protocol.service.MWTFollowerResult;
import com.quanjing.weitu.app.protocol.service.MWTUserResult;
import com.quanjing.weitu.app.protocol.service.MWTUserService;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowerFragment extends Fragment {


    private GridView follwerGridView;
    private FollowGridAdapter adapter;
    private String userID;
    private List<MWTUserData> followerResults;

    final MWTUserManager userManager = MWTUserManager.getInstance();

    public FollowerFragment() {
    }

    public static FollowerFragment newInstance(String userID) {
        FollowerFragment followerFragment = new FollowerFragment();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        followerFragment.setArguments(args);
        return followerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        loadFollowers();
        follwerGridView = (GridView) view.findViewById(R.id.grid_followings);
        return view;
    }


    private void loadFollowers() {

        SVProgressHUD.showInView(getActivity(), "加载中，请稍候...", true);

        MWTUserService userService = MWTRestManager.getInstance().create(MWTUserService.class);
        userService.queryFollowers(userID, 0, 50, new Callback<MWTFollowerResult>() {

            @Override
            public void success(MWTFollowerResult mwtFollowerResult, Response response) {
                SVProgressHUD.dismiss(getActivity());

                followerResults = mwtFollowerResult.users;
                adapter = new FollowGridAdapter(getActivity(), followerResults);
                follwerGridView.setAdapter(adapter);
                follwerGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MWTUserData userData = (MWTUserData) adapter.getItem(i);
                        if (userData != null) {
                            Intent intent = new Intent(getActivity(), MWTOtherUserActivity.class);
                            intent.putExtra("userID", userData.userID);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(getActivity());
                Context ctx = getActivity();
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
            TextView followState = (TextView) v.findViewById(R.id.followState);
            TextView tx_name = (TextView) v.findViewById(R.id.ItemText);

            final MWTUserData user = datas.get(position);
            final int p = position;
            tx_name.setText((String) user.nickname);
            followState.setVisibility(View.VISIBLE);
            MWTUser cUser = userManager.getCurrentUser();
            if (cUser != null && cUser.getmwtFellowshipInfo().get_followingUserIDs().size() > 0) {
                if (cUser.getmwtFellowshipInfo().get_followingUserIDs().contains(user.userID))
                    followState.setText("取消关注");
                else
                    followState.setText("关注");
            }


            followState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userManager.getCurrentUser() != null && userManager.getCurrentUser().getmwtFellowshipInfo().get_followingUserIDs().size() > 0) {
                        if (userManager.getCurrentUser().getmwtFellowshipInfo().get_followingUserIDs().contains(user.userID))
                            cancelAttention(user.userID, p);
                        else
                            addAttention(user.userID, p);
                    }
                }
            });
            Picasso.with(context)
                    .load(user.avatarImageInfo.url)
                    .centerCrop().resize(200, 200)
                    .placeholder(new ColorDrawable(Color.WHITE))
                    .into(tx_img);
            return v;
        }

    }

    /**
     * 关注
     *
     * @param userid
     */
    private void addAttention(final String userid, final int position) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), MWTAuthSelectActivity.class);
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
        MWTUser cUser = userManager.getCurrentUser();
        if (cUser == null)
            return;
        SVProgressHUD.showInView(getActivity(), "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "follow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(getActivity());
                Toast.makeText(getActivity(), "关注失败", 500).show();
            }
        });

    }

    private void cancelAttention(String userid, final int positon) {
        MWTAuthManager am = MWTAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("请登录")
                    .setMessage("请在登录后使用关注功能")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), MWTAuthSelectActivity.class);
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
        MWTUser cUser = userManager.getCurrentUser();
        if (cUser == null)
            return;
        SVProgressHUD.showInView(getActivity(), "请稍后...", true);
        userService.addAttention(cUser.getUserID(), "unfollow", userid, new Callback<MWTUserResult>() {
            @Override
            public void success(MWTUserResult mwtUserResult, Response response) {
                refreshCurrentUser();
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(getActivity());
                Toast.makeText(getActivity(), "取消失败", 500).show();
            }
        });

    }

    private void refreshCurrentUser() {
        MWTUser user = MWTUserManager.getInstance().getCurrentUser();
        MWTUserManager.getInstance().refreshCurrentUserInfo(new MWTCallback() {
            @Override
            public void success() {
                SVProgressHUD.dismiss(getActivity());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(MWTError error) {
            }
        });
    }

}


