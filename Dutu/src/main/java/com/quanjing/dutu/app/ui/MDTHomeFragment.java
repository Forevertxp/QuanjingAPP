package com.quanjing.dutu.app.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.jakewharton.salvage.RecyclingPagerAdapter;
import com.quanjing.dutu.R;
import com.quanjing.weitu.app.ui.feed.MWTFeedFlowFragment;

import java.util.ArrayList;
import java.util.List;

public class MDTHomeFragment extends MWTFeedFlowFragment
{
    private MDTAutoSlidingPagerView _autoSlidingPagerView;
    private MDTMainActivity _mainActivity;

    public static MDTHomeFragment newInstance(String feedID)
    {
        MDTHomeFragment fragment = new MDTHomeFragment();
        Bundle args = new Bundle();
        args.putString(MWTFeedFlowFragment.ARG_PARAM_FEEDID, feedID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        _autoSlidingPagerView = (MDTAutoSlidingPagerView) inflater.inflate(R.layout.view_auto_slide_pager, container, false);
        ArrayList<Integer> imageIdList = new ArrayList<Integer>();
        imageIdList.add(R.drawable.banner_life);
        imageIdList.add(R.drawable.banner_wiki);
        imageIdList.add(R.drawable.banner_app);
        _autoSlidingPagerView.setAdapter(new ImagePagerAdapter(getActivity(), imageIdList));
        _autoSlidingPagerView.setOnPageChangeListener(new MyOnPageChangeListener());
        _autoSlidingPagerView.setInterval(4000);
        _autoSlidingPagerView.setScrollDurationFactor(2.0);

        getGridView().addHeaderView(_autoSlidingPagerView);

        _autoSlidingPagerView.startAutoScroll();

        return view;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageSelected(int position)
        {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        _mainActivity = (MDTMainActivity) activity;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        _autoSlidingPagerView.stopAutoScroll();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        _autoSlidingPagerView.startAutoScroll();
    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter
    {
        private Context context;
        private List<Integer> imageIdList;

        private int size;

        public ImagePagerAdapter(Context context, List<Integer> imageIdList)
        {
            this.context = context;
            this.imageIdList = imageIdList;
            this.size = imageIdList.size();
        }

        @Override
        public int getCount()
        {
            return imageIdList.size();
        }

        @Override
        public View getView(int position, View view, ViewGroup container)
        {
            ViewHolder holder;
            if (view == null)
            {
                holder = new ViewHolder();
                view = holder.imageView = new ImageView(context);
                view.setTag(holder);
                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        int item = _autoSlidingPagerView.getCurrentItem();
                        handlePagerItemClicked(item);
                    }
                });
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }
            holder.imageView.setImageResource(imageIdList.get(position));
            return view;
        }

        private class ViewHolder
        {
            ImageView imageView;
        }
    }

    private void handlePagerItemClicked(int index)
    {
        switch (index)
        {
            case 0:
                _mainActivity.switchToLifePage();
                break;
            case 1:
                _mainActivity.switchToWikiPage();
                break;
            case 2:
                _mainActivity.switchToAppPage();
                break;
        }
    }
}
