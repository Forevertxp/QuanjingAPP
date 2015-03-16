package com.quanjing.quanjing.app.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.EntypoIcon;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.quanjing.app.R;
import com.quanjing.weitu.app.common.MWTCallback1;
import com.quanjing.weitu.app.common.MWTOverScrollView;
import com.quanjing.weitu.app.model.MWTAsset;
import com.quanjing.weitu.app.model.MWTAssetManager;
import com.quanjing.weitu.app.model.MWTLabel;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.protocol.MWTArticleData;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTArticleResult;
import com.quanjing.weitu.app.protocol.service.MWTArticleService;
import com.quanjing.weitu.app.protocol.service.MWTDailyResult;
import com.quanjing.weitu.app.protocol.service.MWTLabelResult;
import com.quanjing.weitu.app.protocol.service.MWTSearchService;
import com.quanjing.weitu.app.ui.asset.MWTAssetActivity;
import com.quanjing.weitu.app.ui.beauty.MWTDualActivity;
import com.quanjing.weitu.app.ui.found.MWTContentActivity;
import com.quanjing.weitu.app.ui.found.TempActivity;
import com.quanjing.weitu.app.ui.search.MWTSearchActivity;
import com.squareup.picasso.Picasso;

import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MQJHomeFragment extends Fragment {
    private Context _context;
    private RelativeLayout _searchAreaLayout;
    private AutoCompleteTextView _editText;
    private ImageButton _searchButton;
    private DynamicHeightImageView dynamicHeightImageView;
    private ListView newsListView;
    private DynamicHeightImageView dailyImage;
    private TextView btn_travel, btn_car, btn_jiaju, btn_food, btn_fashion, btn_baike;
    private TextView dailyCaption, moreLabel;
    private TableLayout tableLayout;
    private MWTOverScrollView scrollView;
    private KeyboardLayout mainView;
    private LinearLayout newsLL, picLL, labelLL;

    private int width;
    private List<MWTArticleData> articleList = new ArrayList<MWTArticleData>();
    private static int PAGE = 1;
    private String dailyImageUrl = "";

    private ArrayList<String> keywordList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    public static boolean isIndex = true; // 是否是首页

    public MQJHomeFragment() {
    }

    public static MQJHomeFragment newInstance() {
        return new MQJHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _context = activity;
    }

    private float convertDP2PX(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = view.getMeasuredWidth();
                System.out.println("View width is " + width);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) _searchAreaLayout.getLayoutParams();
                int topMargin = width * 10 / 640; // 275 & 640 is measured from the background image
                layoutParams.topMargin = topMargin;
                _searchAreaLayout.setLayoutParams(layoutParams);
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        newsLL = (LinearLayout) view.findViewById(R.id.index_news);
        picLL = (LinearLayout) view.findViewById(R.id.index_pic);
        labelLL = (LinearLayout) view.findViewById(R.id.labelLL);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        mainView = (KeyboardLayout) view.findViewById(R.id.keyboardLayout);
        scrollView = (MWTOverScrollView) view.findViewById(R.id.scrollview);

        _searchAreaLayout = (RelativeLayout) view.findViewById(R.id.SearchAreaLayout);

        _editText = (AutoCompleteTextView) view.findViewById(R.id.EditText);
//        initAutoComplete("history");

        _editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
//                String key = _editText.getText().toString();
//                if (!key.equals("")) {
//                    getKeywordTip(key, _editText);
//                } else {
//                    initAutoComplete("history");
//                }
            }
        });
        _editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(_editText.getWindowToken(), 0);
                _editText.clearFocus();
                return false;
            }
        });

        _searchButton = (ImageButton) view.findViewById(R.id.SearchButton);

        IconicFontDrawable searchIconDrawable = new IconicFontDrawable(getActivity());
        searchIconDrawable.setIcon(EntypoIcon.SEARCH);
        searchIconDrawable.setIconColor(Color.WHITE);
        searchIconDrawable.setIntrinsicHeight((int) convertDP2PX(24));
        searchIconDrawable.setIntrinsicWidth((int) convertDP2PX(24));

        _searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        // 图文列表
        newsListView = (ListView) view.findViewById(R.id.index_list);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MWTArticleData articleData = (MWTArticleData) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), MWTContentActivity.class);
                intent.putExtra("imageUrl", articleData.CoverUrl);
                intent.putExtra("caption", articleData.Caption);
                intent.putExtra("contentUrl", articleData.Url);
                startActivity(intent);
            }
        });

        // 每日一图
        dailyImage = (DynamicHeightImageView) view.findViewById(R.id.index_everypic);
        dailyCaption = (TextView) view.findViewById(R.id.daily_caption);

        btn_travel = (TextView) view.findViewById(R.id.btn_travel);
        btn_car = (TextView) view.findViewById(R.id.btn_car);
        btn_jiaju = (TextView) view.findViewById(R.id.btn_jiaju);
        btn_food = (TextView) view.findViewById(R.id.btn_food);
        btn_fashion = (TextView) view.findViewById(R.id.btn_fashion);
        btn_baike = (TextView) view.findViewById(R.id.btn_baike);

        moreLabel = (TextView) view.findViewById(R.id.more_label);
        tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        loadLabelData(PAGE);

        moreLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PAGE < 4) {
                    PAGE = PAGE + 1;
                    loadLabelData(PAGE);
                } else {
                    PAGE = 1;
                    loadLabelData(PAGE);
                }

            }
        });


        btn_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.TRAVEL);
                performSearch("旅游");
            }
        });
        btn_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.CAR);
                performSearch("汽车");
            }
        });
        btn_jiaju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.LIFE);
                performSearch("家居");
            }
        });
        btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.FOOD);
                performSearch("美食");
            }
        });
        btn_fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.FASHION);
                performSearch("时尚");
            }
        });
        btn_baike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toTemp(TempActivity.ENCYCLOPEDIA);
                Intent intent = new Intent(getActivity(), MWTDualActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使
     * AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void initAutoComplete(String field) {
        SharedPreferences sp = getActivity().getSharedPreferences("network_url", 0);
        String longhistory = sp.getString("history", "");
        String[] hisArrays = longhistory.split(",");
        //只保留最近的10条的记录
        int n = 0;
        if (hisArrays.length > 10) {
            n = 10;
        } else {
            n = hisArrays.length;
        }
        for (int i = 0; i < n; i++) {
            keywordList.add(hisArrays[i]);
        }
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, keywordList);
        _editText.setAdapter(adapter);
        //auto.setCompletionHint("最近的5条记录");
        // 通过监听键盘是否弹出来控制搜索提示框显隐
        mainView.setOnkbdStateListener(new KeyboardLayout.onKybdsChangeListener() {

            public void onKeyBoardStateChange(int state) {
                switch (state) {
                    case KeyboardLayout.KEYBOARD_STATE_HIDE:
                        _editText.dismissDropDown();
                        break;
                    case KeyboardLayout.KEYBOARD_STATE_SHOW:
                        if (isIndex)
                            _editText.showDropDown();
                        break;
                }
            }
        });

    }


    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getActivity().getSharedPreferences("network_url", 0);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            if (!sb.toString().equals(""))
                sp.edit().putString("history", sb.toString()).commit();
        }
    }

    /**
     * 搜索提示
     */
    private void getKeywordTip(String key, final AutoCompleteTextView auto) {
        if (key.equals(""))
            return;
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTSearchService searchService = restManager.create(MWTSearchService.class);
        searchService.fetchKeywordTip(key, new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> result, Response response) {
                adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, result);
                _editText.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void loadDailyImage() {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTArticleService articleService = restManager.create(MWTArticleService.class);
        articleService.fetchDailyImage(new Callback<MWTDailyResult>() {
            @Override
            public void success(final MWTDailyResult result, Response response) {
                picLL.setVisibility(View.VISIBLE);
                dailyImageUrl = result.daily.imageurl;
                SharedPreferences.Editor sharedata = getActivity().getSharedPreferences("data", 0).edit();
                sharedata.putString("dailyImageUrl", dailyImageUrl);
                sharedata.commit();
                Picasso.with(getActivity())
                        .load(dailyImageUrl)
                        .resize(width, (int) (width * 0.6))
                        .into(dailyImage);
                dailyCaption.setText(result.daily.caption);
                dailyImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), MWTAssetActivity.class);
                        intent.putExtra(MWTAssetActivity.ARG_ASSETID, result.daily.originalid);
                        getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadArticleData() {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTArticleService articleService = restManager.create(MWTArticleService.class);
        articleService.fetchHomeActicles(new Callback<MWTArticleResult>() {
            @Override
            public void success(MWTArticleResult result, Response response) {
                articleList = result.article;
                NewsAdapter newsAdapter = new NewsAdapter(getActivity(), articleList);
                newsListView.setAdapter(newsAdapter);
                setListViewHeightBasedOnChildren(newsListView);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLabelData(final int page) {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTArticleService articleService = restManager.create(MWTArticleService.class);
        articleService.fetchLabels(page, 8, new Callback<MWTLabelResult>() {
                    @Override
                    public void success(MWTLabelResult result, Response response) {
                        labelLL.setVisibility(View.VISIBLE);
                        tableLayout.removeAllViews();
                        addRow(result.hottag);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    private void addRow(final List<MWTLabel> labelList) {
        for (int i = 0; i < labelList.size(); i = i + 2) {
            TableRow tableRow = new TableRow(getActivity());
            TextView textView1 = new TextView(getActivity());
            textView1.setTag(i);
            textView1.setTextColor(R.color.LabelText);
            textView1.setTextSize(15);
            TextView textView2 = new TextView(getActivity());
            textView2.setTextColor(R.color.LabelText);
            textView2.setTag(i + 1);
            textView2.setTextSize(15);

            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (Integer) view.getTag();
                    performSearch(labelList.get(tag).searchkeywords);
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (Integer) view.getTag();
                    performSearch(labelList.get(tag).searchkeywords);
                }
            });

            textView1.setText(labelList.get(i).tag);
            if (i + 1 < labelList.size())
                textView2.setText(labelList.get(i + 1).tag);
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 10, 20, 10);
            tableLayout.addView(tableRow, layoutParams);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            isIndex = true;
            if (scrollView != null) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
            if (articleList.size() == 0) {
                loadArticleData();
            }
            if (dailyImageUrl.equals("")) {
                loadDailyImage();
            }
        } else {
            isIndex = false;
        }
    }

    private void toTemp(int n) {
        Intent intent = new Intent(getActivity(), TempActivity.class);
        intent.putExtra("type", n);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void performSearch() {
        //保存搜索历史
        saveHistory("history", _editText);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        final String keyword = _editText.getText().toString();
        if (keyword.isEmpty()) {
            Toast.makeText(getActivity(), "请输入搜索关键字", Toast.LENGTH_SHORT);
            return;
        }

        SVProgressHUD.showInView(getActivity(), "搜索中，请稍候...", true);

        MWTAssetManager am = MWTAssetManager.getInstance();
        am.searchAssets(keyword, 0, 20, new MWTCallback1<List<MWTAsset>>() {
            @Override
            public void success(List<MWTAsset> assets) {
                SVProgressHUD.dismiss(getActivity());

                int assetNum = assets.size();
                String[] assetIDs = new String[assetNum];
                for (int i = 0; i < assetNum; ++i) {
                    assetIDs[i] = assets.get(i).getAssetID();
                }

                Intent intent = new Intent(getActivity(), MWTSearchActivity.class);
                intent.putExtra(MWTSearchActivity.ARG_KEYWORD, keyword);
                intent.putExtra(MWTSearchActivity.ARG_ASSETIDS, assetIDs);
                startActivity(intent);
            }

            @Override
            public void failure(MWTError error) {
                SVProgressHUD.dismiss(getActivity());
                Context ctx = getActivity();
                if (ctx != null) {
                    Toast.makeText(ctx, error.getMessageWithPrompt("搜索失败"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performSearch(final String keyword) {

        SVProgressHUD.showInView(getActivity(), "加载中，请稍候...", true);

        MWTAssetManager am = MWTAssetManager.getInstance();
        am.searchAssets(keyword, 0, 50, new MWTCallback1<List<MWTAsset>>() {
            @Override
            public void success(List<MWTAsset> assets) {
                SVProgressHUD.dismiss(getActivity());

                int assetNum = assets.size();
                String[] assetIDs = new String[assetNum];
                for (int i = 0; i < assetNum; ++i) {
                    assetIDs[i] = assets.get(i).getAssetID();
                }

                Intent intent = new Intent(getActivity(), MWTSearchActivity.class);
                intent.putExtra(MWTSearchActivity.ARG_KEYWORD, keyword);
                intent.putExtra(MWTSearchActivity.ARG_ASSETIDS, assetIDs);
                startActivity(intent);
            }

            @Override
            public void failure(MWTError error) {
                SVProgressHUD.dismiss(getActivity());
                Context ctx = getActivity();
                if (ctx != null) {
                    Toast.makeText(ctx, error.getMessageWithPrompt("加载失败"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class NewsAdapter extends BaseAdapter {

        private List<MWTArticleData> newsList;
        private Context context;

        //定义两个int常量标记不同的Item视图
        public static final int PIC_ITEM = 0;
        public static final int PIC_WORD_ITEM = 1;


        public NewsAdapter(Context context, List<MWTArticleData> newsList) {
            this.context = context;
            this.newsList = newsList;
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Object getItem(int i) {
            return newsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return PIC_ITEM;
            } else {
                return PIC_WORD_ITEM;
            }
        }

        @Override
        public int getViewTypeCount() {
            //因为有两种视图，所以返回2
            return 2;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            TopViewHolder topViewHolder = null;
            MWTArticleData articleData = newsList.get(position);
            if (getItemViewType(position) == PIC_ITEM) {
                if (convertView == null) {
                    convertView = View.inflate(context, com.quanjing.weitu.R.layout.view_article_top, null);
                    topViewHolder = new TopViewHolder();
                    topViewHolder.dynamicHeightImageView = (DynamicHeightImageView) convertView.findViewById(com.quanjing.weitu.R.id.articleView);
                    convertView.setTag(topViewHolder);
                } else {
                    topViewHolder = (TopViewHolder) convertView.getTag();
                }
                String imageUrl = articleData.HCoverUrl;
                Picasso.with(context)
                        .load(imageUrl)
                        .into(topViewHolder.dynamicHeightImageView);

            } else {
                if (convertView == null) {
                    convertView = View.inflate(context, com.quanjing.weitu.R.layout.item_found, null);
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) convertView.findViewById(com.quanjing.weitu.R.id.iv_avatar);
                    holder.textView = (TextView) convertView.findViewById(com.quanjing.weitu.R.id.tv_title);
                    holder.contentView = (TextView) convertView.findViewById(com.quanjing.weitu.R.id.tv_content);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.textView.setText(articleData.Caption);
                holder.contentView.setText(articleData.Summary);
                String imageUrl = articleData.CoverUrl;
                Picasso.with(context)
                        .load(imageUrl).resize(80, 60)
                        .into(holder.imageView);
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView textView;
            TextView contentView;
        }

        private class TopViewHolder {
            DynamicHeightImageView dynamicHeightImageView;
        }
    }

    /**
     * 根据listview内容设置其高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
