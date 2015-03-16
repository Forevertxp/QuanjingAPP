package com.quanjing.weitu.app.ui.category;

import android.content.Context;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTCategory;
import com.quanjing.weitu.app.model.MWTCategoryManager;
import com.quanjing.weitu.app.protocol.MWTError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MWTDynamicCategoriesAdapter extends MWTSectionedCategoriesAdapter
{
    private MWTCategory _category;
    private List<MWTCategory> _presentingCategories;
    private HashMap<String, List<MWTCategory>> _subCategoriesByGroup;
    private List<String> _subCategoryGroups;

    public MWTDynamicCategoriesAdapter(Context context, MWTCategory category)
    {
        super(context);
        _category = category;
        updatePresentingCategories();
    }

    @Override
    public int getCategoriesCount()
    {
        if (_presentingCategories != null)
        {
            return _presentingCategories.size();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public MWTCategory getCategory(int index)
    {
        if (_presentingCategories != null)
        {
            return _presentingCategories.get(index);
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getCategoryGroupNum()
    {
        if (_subCategoryGroups != null)
        {
            return _subCategoryGroups.size();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int getCategoryGroupCategoryNum(int index)
    {
        if (_subCategoriesByGroup != null)
        {
            String groupName = getCategoryGroupName(index);
            List<MWTCategory> categories = _subCategoriesByGroup.get(groupName);
            if (categories != null)
            {
                return categories.size();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public String getCategoryGroupName(int index)
    {
        if (_subCategoryGroups != null)
        {
            return _subCategoryGroups.get(index);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void refresh(final MWTCallback callback)
    {
        MWTCategoryManager cm = MWTCategoryManager.getInstance();
        if (_category != null)
        {
            cm.refreshSubCategories(_category, new MWTCallback()
            {
                @Override
                public void success()
                {
                    updatePresentingCategories();
                    notifyDataSetChanged();
                    if (callback != null)
                    {
                        callback.success();
                    }
                }

                @Override
                public void failure(MWTError error)
                {
                    if (callback != null)
                    {
                        callback.failure(error);
                    }
                }
            });
        }
        else
        {
            cm.refreshRootCategories(new MWTCallback()
            {
                @Override
                public void success()
                {
                    updatePresentingCategories();
                    notifyDataSetChanged();
                    if (callback != null)
                    {
                        callback.success();
                    }
                }

                @Override
                public void failure(MWTError error)
                {
                    if (callback != null)
                    {
                        callback.failure(error);
                    }
                }
            });
        }
    }

    @Override
    public void refreshIfNeeded()
    {
        if (_presentingCategories == null || _presentingCategories.isEmpty())
        {
            refresh(null);
        }
    }

    private void updatePresentingCategories()
    {
        if (_category != null)
        {
            _presentingCategories = _category.getSubCategoriesListeSortedByGroup();
            _subCategoriesByGroup = _category.getSubCategoriesByGroup();
            _subCategoryGroups = _category.getSubCategoryGroups();
        }
        else
        {
            _presentingCategories = new ArrayList<MWTCategory>();
            _presentingCategories.addAll(MWTCategoryManager.getInstance().getRootCategories());
            _subCategoriesByGroup = null;
            _subCategoryGroups = null;
        }
    }
}
