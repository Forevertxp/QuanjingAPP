package com.quanjing.weitu.app.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.quanjing.weitu.R;

import java.util.ArrayList;

import static android.util.DisplayMetrics.DENSITY_DEFAULT;

public class MWTTabBar extends FrameLayout
{
    private ArrayList<MWTTabItemView> _itemViews = new ArrayList<>();
    private int _selectedTabIndex = -1;
    private OnTabSelectionEventListener _onTabSelectionEventListener;
    private LinearLayout _containerLayout;

    public MWTTabBar(Context context)
    {
        super(context);
        construct();
    }

    public MWTTabBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        construct();
    }

    public MWTTabBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        construct();
    }

    public void limitTabNum(int tabNum)
    {
        for (int i = tabNum; i < _itemViews.size(); ++i)
        {
            _itemViews.get(i).setVisibility(View.GONE);
        }
    }

    public void setOnTabSelectionEventListener(OnTabSelectionEventListener onTabSelectionEventListener)
    {
        _onTabSelectionEventListener = onTabSelectionEventListener;
    }

    private void onItemViewClicked(int itemIndex)
    {
        setSelectedTabIndex(itemIndex);
    }

    public int getItemViewCount()
    {
        return _itemViews.size();
    }

    public MWTTabItemView getItemViewAt(int index)
    {
        if (index >= 0 && index < _itemViews.size())
        {
            return _itemViews.get(index);
        }
        else
        {
            return null;
        }
    }

    public ArrayList<MWTTabItemView> getItemViews()
    {
        return _itemViews;
    }

    public int getSelectedTabIndex()
    {
        return _selectedTabIndex;
    }

    public void setSelectedTabIndex(int selectedTabIndex)
    {
        if (selectedTabIndex == _selectedTabIndex)
        {
            return;
        }

        if (!isValidIndex(selectedTabIndex))
        {
            throw new RuntimeException("Out of bounds.");
        }

        if (_onTabSelectionEventListener != null)
        {
            boolean shouldSelect = _onTabSelectionEventListener.onWillSelectTab(selectedTabIndex);
            if (!shouldSelect)
            {
                return;
            }
        }

        if (isValidIndex(_selectedTabIndex))
        {
            getItemViewAt(_selectedTabIndex).setDisplayStyle(MWTTabItemView.DisplayStyle.NORMAL);
        }

        _selectedTabIndex = selectedTabIndex;

        getItemViewAt(_selectedTabIndex).setDisplayStyle(MWTTabItemView.DisplayStyle.SELECTED);

        if (_onTabSelectionEventListener != null)
        {
            _onTabSelectionEventListener.onDidSelectTab(_selectedTabIndex);
        }
    }

    private void construct()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _containerLayout = (LinearLayout) inflater.inflate(R.layout.view_tabbar, this, false);
        addView(_containerLayout);

        for (int i = 0; i < _containerLayout.getChildCount(); ++i)
        {
            View childView = _containerLayout.getChildAt(i);
            if (childView instanceof MWTTabItemView)
            {
                MWTTabItemView itemView = (MWTTabItemView) childView;
                itemView.setTag(R.id.TabBarItemIndexTag, new Integer(i));
                itemView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Integer index = (Integer) view.getTag(R.id.TabBarItemIndexTag);
                        onItemViewClicked(index.intValue());
                    }
                });
                _itemViews.add(itemView);
            }
        }

        if (!_itemViews.isEmpty())
        {
            setSelectedTabIndex(0);
        }
    }

    private boolean isValidIndex(int index)
    {
        return (index >= 0 && index < _itemViews.size());
    }

    public interface OnTabSelectionEventListener
    {
        public boolean onWillSelectTab(int tabIndex);
        public void onDidSelectTab(int tabIndex);
    }

    public void setIconTextSize(float iconTextSize)
    {
        for (MWTTabItemView itemView : _itemViews)
        {
            itemView.setIconTextSize(iconTextSize);
        }
    }

    public void setTitleTextSize(float titleTextSize)
    {
        for (MWTTabItemView itemView : _itemViews)
        {
            itemView.setTitleTextSize(titleTextSize);
        }
    }

    public int dpToPx(int dp)
    {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DENSITY_DEFAULT));
        return px;
    }

    public void setTopPadding(int topPadding)
    {
        _containerLayout.setPadding(_containerLayout.getPaddingLeft(),
                                    dpToPx(topPadding),
                                    _containerLayout.getPaddingRight(),
                                    _containerLayout.getPaddingBottom());
    }
}
