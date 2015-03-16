package com.quanjing.weitu.app.ui.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;

public class UserSquareActivity extends MWTBase2Activity {

    private MyPagerAdapter mAdapter;
    private NoScrollViewPager mPager;
    private String userID;
    private Button followerBtn, followingBtn;

    private int _buttonBackgroundDrawable = R.drawable.btn_orange;
    private Button _currentActiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_square);
        setTitleText("圈子");
        mAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (NoScrollViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setNoScroll(true);
        userID = getIntent().getStringExtra("userID");
        followerBtn = (Button) findViewById(R.id.followerBtn);
        followingBtn = (Button) findViewById(R.id.followingBtn);
        followerBtn.setBackgroundResource(_buttonBackgroundDrawable);
        followingBtn.setBackgroundResource(_buttonBackgroundDrawable);
        followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToFragmentA();
            }
        });
        followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToFragmentB();
            }
        });
        switchToFragmentA();
    }

    @Override
    public void setTitleText(String title) {
        super.setTitleText(title);
    }

    private void switchToFragmentA() {
        if (_currentActiveButton == followerBtn) {
            return;
        }

        _currentActiveButton = followerBtn;

        followerBtn.setSelected(true);
        followingBtn.setSelected(false);
        mPager.setCurrentItem(0);
    }

    private void switchToFragmentB() {
        if (_currentActiveButton == followingBtn) {
            return;
        }

        _currentActiveButton = followingBtn;

        followerBtn.setSelected(false);
        followingBtn.setSelected(true);
        mPager.setCurrentItem(1);

    }

    public void setButtonBackgroundDrawable(int buttonBackgroundDrawable) {
        _buttonBackgroundDrawable = buttonBackgroundDrawable;
    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private int mCount = 2;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (0 == position) {
                return FollowingFragment.newInstance(userID);
            } else if (1 == position) {
                return FollowerFragment.newInstance(userID);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

    }

}
