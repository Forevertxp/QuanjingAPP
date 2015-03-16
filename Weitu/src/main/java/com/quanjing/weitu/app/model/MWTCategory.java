package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTCategoryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MWTCategory implements Comparable<MWTCategory>
{
    private String _categoryID;
    private String _categoryName;
    private Integer _priority;
    private MWTImageInfo _coverImageInfo;
    private String _feedID;
    private String _groupName;
    private boolean _isMultiLevel;

    private HashMap<String, List<MWTCategory>> _subCategoriesByGroup;
    private ArrayList<String> _subCategoryGroups;
    private List<MWTCategory> _subCategories;

    public String getCategoryID()
    {
        return _categoryID;
    }

    public void setCategoryID(String categoryID)
    {
        _categoryID = categoryID;
    }

    public String getCategoryName()
    {
        return _categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        _categoryName = categoryName;
    }

    public Integer getPriority()
    {
        return _priority;
    }

    public MWTImageInfo getCoverImageInfo()
    {
        return _coverImageInfo;
    }

    public String getFeedID()
    {
        return _feedID;
    }

    public String getGroupName()
    {
        return _groupName;
    }

    public boolean isMultiLevel()
    {
        return _isMultiLevel;
    }

    public void setMultiLevel(boolean isMultiLevel)
    {
        _isMultiLevel = isMultiLevel;
    }

    public HashMap<String, List<MWTCategory>> getSubCategoriesByGroup()
    {
        return _subCategoriesByGroup;
    }

    public ArrayList<String> getSubCategoryGroups()
    {
        return _subCategoryGroups;
    }

    public List<MWTCategory> getSubCategories()
    {
        return _subCategories;
    }

    public List<MWTCategory> getSubCategoriesListeSortedByGroup()
    {
        ArrayList<MWTCategory> subCategories = new ArrayList<>();

        if (_subCategoryGroups != null && _subCategoriesByGroup != null)
        {
            for (String group : _subCategoryGroups)
            {
                List<MWTCategory> categories = _subCategoriesByGroup.get(group);
                subCategories.addAll(categories);
            }
        }

        return subCategories;
    }

    public void setSubCategories(List<MWTCategory> subCategories)
    {
        if (subCategories == null)
        {
            _subCategoriesByGroup = null;
            _subCategoryGroups = null;
            _subCategories = null;
            return;
        }

        _subCategoriesByGroup = new HashMap<>();
        _subCategoryGroups = new ArrayList<>();
        _subCategories = subCategories;

        for (MWTCategory category : subCategories)
        {
            String groupName = category.getGroupName();
            List<MWTCategory> categories;
            categories = _subCategoriesByGroup.get(groupName);
            if (categories == null)
            {
                categories = new ArrayList<MWTCategory>();
                _subCategoriesByGroup.put(groupName, categories);
                _subCategoryGroups.add(groupName);
            }

            categories.add(category);
        }
    }

    public void mergeWithData(MWTCategoryData data)
    {
        if (data.getRealCategoryID() != null)
        {
            _categoryID = data.getRealCategoryID();
        }

        if (data.categoryName != null)
        {
            _categoryName = data.categoryName;
        }

        if (data.priority != null)
        {
            _priority = data.priority;
        }

        if (data.coverImageInfo != null)
        {
            _coverImageInfo = data.coverImageInfo;
        }

        if (data.feedID != null)
        {
            _feedID = data.feedID;
        }

        if (data.GroupName != null)
        {
            _groupName = data.GroupName;
            if (_groupName.equals(""))
            {
                _groupName = "其它";
            }
        }
        else
        {
            _groupName = null;
        }

        if (data.IsMultiLevel != null)
        {
            _isMultiLevel = data.IsMultiLevel.booleanValue();
        }
        else
        {
            _isMultiLevel = false;
        }
    }

    @Override
    public int compareTo(MWTCategory rhs)
    {
        if (_priority < rhs._priority)
        {
            return -1;
        }
        else if (_priority == rhs._priority)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}
