package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTCategoryData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTCategoriesResult;
import com.quanjing.weitu.app.protocol.service.MWTCategoryService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

public class MWTCategoryManager
{
    private static MWTCategoryManager s_instance;
    private ArrayList<MWTCategory> _rootCategories = new ArrayList<>();
    private HashMap<String, MWTCategory> _categoriesByID = new HashMap<>();
    private MWTCategoryService _categoryService;

    private MWTCategoryManager()
    {
        _categoryService = MWTRestManager.getInstance().create(MWTCategoryService.class);

        createMultiLevelCategory("travel-international", "国外旅游");
        createMultiLevelCategory("travel-domestic", "国内旅游");
    }

    public static MWTCategoryManager getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new MWTCategoryManager();
        }

        return s_instance;
    }

    public ArrayList<MWTCategory> getRootCategories()
    {
        return _rootCategories;
    }

    public MWTCategory getCategoryByID(String categoryID)
    {
        return _categoriesByID.get(categoryID);
    }

    public MWTCategoryService getCategoryService()
    {
        return _categoryService;
    }

    public MWTCategory createMultiLevelCategory(String categoryID, String categoryName)
    {
        MWTCategory category = new MWTCategory();

        category.setCategoryID(categoryID);
        category.setCategoryName(categoryName);
        category.setMultiLevel(true);
        _categoriesByID.put(categoryID, category);

        return category;
    }

    public void refreshRootCategories(final MWTCallback callback)
    {
        getCategoryService().queryRootCategories(new Callback<MWTCategoriesResult>()
        {
            @Override
            public void success(MWTCategoriesResult result, Response response)
            {
                if (result == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null)
                {
                    if (callback != null)
                    {
                        callback.failure(result.error);
                    }
                    return;
                }

                List<MWTCategoryData> categoryDatas = result.categories;
                if (categoryDatas == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据错误"));
                    }
                    return;
                }

                List<MWTCategory> categories = registerCategoryDatas(categoryDatas);
                updateRootCategories(categories);

                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                if (callback != null)
                {
                    callback.failure(new MWTError(retrofitError));
                }
            }
        });
    }

    public void refreshSubCategories(final MWTCategory category, final MWTCallback callback)
    {
        getCategoryService().querySubCategories(category.getCategoryID(), new Callback<MWTCategoriesResult>()
        {
            @Override
            public void success(MWTCategoriesResult result, Response response)
            {
                if (result == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null)
                {
                    if (callback != null)
                    {
                        callback.failure(result.error);
                    }
                    return;
                }

                List<MWTCategoryData> categoryDatas = result.categories;
                if (categoryDatas == null)
                {
                    if (callback != null)
                    {
                        callback.failure(new MWTError(-1, "服务器返回数据错误"));
                    }
                    return;
                }

                List<MWTCategory> categories = registerCategoryDatas(categoryDatas);
                category.setSubCategories(categories);

                if (callback != null)
                {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                if (callback != null)
                {
                    callback.failure(new MWTError(retrofitError));
                }
            }
        });
    }

    private List<MWTCategory> registerCategoryDatas(List<MWTCategoryData> categoryDatas)
    {
        ArrayList<MWTCategory> categories = new ArrayList<>();

        for (MWTCategoryData categoryData : categoryDatas)
        {
            String categoryID = categoryData.getRealCategoryID();
            MWTCategory category = _categoriesByID.get(categoryID);
            if (category == null)
            {
                category = new MWTCategory();
                _categoriesByID.put(categoryID, category); // Dirty hack, since server side will reuse feedID to store the actural categoryID
            }
            category.mergeWithData(categoryData);
            categories.add(category);
        }

        return categories;
    }

    private void updateRootCategories(List<MWTCategory> categories)
    {
        _rootCategories = new ArrayList<>(categories);
        Collections.sort(_rootCategories, new Comparator<MWTCategory>()
        {
            @Override
            public int compare(MWTCategory lhs, MWTCategory rhs)
            {
                if (lhs.getPriority() < rhs.getPriority())
                {
                    return -1;
                }
                else if (lhs.getPriority() == rhs.getPriority())
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
        });
    }
}
