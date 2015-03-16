package com.quanjing.weitu.app.ui.user;


import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> Views;//存放View的ArrayList

    /*
     * ViewAdapter构造函数
     * @author：Robin
     */
    public ViewPagerAdapter(ArrayList<View> Views) {
        this.Views = Views;
    }


    /*
     * 返回View的个数
     */
    @Override
    public int getCount() {
        if (Views != null) {
            return Views.size();
        }
        return 0;
    }

    /*
     * 销毁View
     */
    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(Views.get(position));
    }

    /*
     * 初始化
     */
    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(Views.get(position), 0);
        return Views.get(position);

    }

    /*
     * 判断View是否来自Object
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
