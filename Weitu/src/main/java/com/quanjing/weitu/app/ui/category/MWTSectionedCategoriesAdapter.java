package com.quanjing.weitu.app.ui.category;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;

public abstract class MWTSectionedCategoriesAdapter extends MWTCategoriesAdapter implements StickyGridHeadersBaseAdapter
{
    private Context _context;

    protected MWTSectionedCategoriesAdapter(Context context)
    {
        super(context);
        _context = context;
    }

    @Override
    public int getCountForHeader(int i)
    {
        return getCategoryGroupCategoryNum(i);
    }

    @Override
    public int getNumHeaders()
    {
        return getCategoryGroupNum();
    }

    public View getHeaderView(int i, View convertView, ViewGroup viewGroup)
    {
        MWTCategorySectionHeaderView headerView = null;

        if (convertView instanceof MWTCategorySectionHeaderView)
        {
            headerView = (MWTCategorySectionHeaderView) convertView;
        }

        if (headerView == null)
        {
            headerView = new MWTCategorySectionHeaderView(_context);
        }

        String groupName = getCategoryGroupName(i);
        headerView.setSectionTitle(groupName);

        return headerView;
    }
}
