package com.quanjing.weitu.app.ui.found;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempActivity extends MWTBase2Activity {

    private ListView tempListView;

    private static int width;

    final public static int TRAVEL = 1;
    final public static int CAR = 2;
    final public static int LIFE = 3;
    final public static int FOOD = 4;
    final public static int FASHION = 5;
    final public static int ENCYCLOPEDIA = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        setTitleText("全景故事");
        tempListView = (ListView) findViewById(R.id.list_temp);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        int type = getIntent().getExtras().getInt("type");
        switch (type) {
            case TRAVEL:
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("content", "来一次说走就走的星际旅行——探访《星际穿越》取景地冰岛");
                map1.put("contentUrl", "http://m.quanjing.com/topic/l01.html");
                map1.put("imageUrl", "http://m.quanjing.com/topic/cover/l01.jpg");
                list.add(map1);
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("content", "全球十大顶级滑雪场");
                map2.put("contentUrl", "http://m.quanjing.com/topic/l02.html");
                map2.put("imageUrl", "http://m.quanjing.com/topic/cover/l02.jpg");
                list.add(map2);
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("content", "全球最美的教堂TOP10排行榜");
                map3.put("contentUrl", "http://m.quanjing.com/topic/l03.html");
                map3.put("imageUrl", "http://m.quanjing.com/topic/cover/l03.jpg");
                list.add(map3);
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("content", "“番茄大战”——西班牙传统节日");
                map4.put("contentUrl", "http://m.quanjing.com/topic/l04.html");
                map4.put("imageUrl", "http://m.quanjing.com/topic/cover/l04.jpg");
                list.add(map4);
                Map<String, String> map5 = new HashMap<String, String>();
                map5.put("content", "印度传统洒红节 迎接春天万木复苏");
                map5.put("contentUrl", "http://m.quanjing.com/topic/l05.html");
                map5.put("imageUrl", "http://m.quanjing.com/topic/cover/l05.jpg");
                list.add(map5);
                break;
            case CAR:
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("content", "真男人60秒认识各系车（上）");
                map6.put("contentUrl", "http://m.quanjing.com/topic/q01.html");
                map6.put("imageUrl", "http://m.quanjing.com/topic/cover/q01.jpg");
                list.add(map6);
                Map<String, String> map7 = new HashMap<String, String>();
                map7.put("content", "真男人60秒认识各系车（中）");
                map7.put("contentUrl", "http://m.quanjing.com/topic/q02.html");
                map7.put("imageUrl", "http://m.quanjing.com/topic/cover/q02.jpg");
                list.add(map7);
                Map<String, String> map8 = new HashMap<String, String>();
                map8.put("content", "真男人60秒认识各系车（下）");
                map8.put("contentUrl", "http://m.quanjing.com/topic/q03.html");
                map8.put("imageUrl", "http://m.quanjing.com/topic/cover/q03.jpg");
                list.add(map8);
                Map<String, String> map9 = new HashMap<String, String>();
                map9.put("content", "2015国际汽车博览会，豪车美女要啥都有！");
                map9.put("imageUrl", "http://m.quanjing.com/topic/cover/q04.jpg");
                list.add(map9);
                Map<String, String> map10 = new HashMap<String, String>();
                map10.put("content", "急速狂飙，玩的就是心跳---世界顶级跑车赏析");
                map10.put("imageUrl", "http://m.quanjing.com/topic/cover/q05.jpg");
                list.add(map10);
                break;
            case LIFE:
                Map<String, String> map11 = new HashMap<String, String>();
                map11.put("content", "当东方遇见西方");
                map11.put("contentUrl", "http://m.quanjing.com/topic/j01.html");
                map11.put("imageUrl", "http://m.quanjing.com/topic/cover/j01.jpg");
                list.add(map11);
                Map<String, String> map12 = new HashMap<String, String>();
                map12.put("content", "时光易逝，风格永存");
                map12.put("contentUrl", "http://m.quanjing.com/topic/j02.html");
                map12.put("imageUrl", "http://m.quanjing.com/topic/cover/j02.jpg");
                list.add(map11);
                Map<String, String> map13 = new HashMap<String, String>();
                map13.put("content", "法国风尚的触动");
                map13.put("contentUrl", "http://m.quanjing.com/topic/j03.html");
                map13.put("imageUrl", "http://m.quanjing.com/topic/cover/j03.jpg");
                list.add(map13);
                Map<String, String> map14 = new HashMap<String, String>();
                map14.put("content", "家的品味");
                map14.put("imageUrl", "http://m.quanjing.com/topic/cover/j04.jpg");
                list.add(map14);
                Map<String, String> map15 = new HashMap<String, String>();
                map15.put("content", "好莱坞”之家");
                map15.put("imageUrl", "http://m.quanjing.com/topic/cover/j05.jpg");
                list.add(map15);
                break;
            case FOOD:
                Map<String, String> map16 = new HashMap<String, String>();
                map16.put("content", "喷香肉丸");
                map16.put("contentUrl", "http://m.quanjing.com/topic/m01.html");
                map16.put("imageUrl", "http://m.quanjing.com/topic/cover/m01.jpg");
                list.add(map16);
                Map<String, String> map17 = new HashMap<String, String>();
                map17.put("content", "甜菜的红色魅影");
                map17.put("contentUrl", "http://m.quanjing.com/topic/m02.html");
                map17.put("imageUrl", "http://m.quanjing.com/topic/cover/m02.jpg");
                list.add(map17);
                Map<String, String> map18 = new HashMap<String, String>();
                map18.put("content", "下午茶也可以甜蜜浪漫");
                map18.put("contentUrl", "http://m.quanjing.com/topic/m03.html");
                map18.put("imageUrl", "http://m.quanjing.com/topic/cover/m03.jpg");
                list.add(map18);
                Map<String, String> map19 = new HashMap<String, String>();
                map19.put("content", "热辣辣的小龙虾");
                map19.put("contentUrl", "http://m.quanjing.com/topic/m04.html");
                map19.put("imageUrl", "http://m.quanjing.com/topic/cover/m04.jpg");
                list.add(map19);
                Map<String, String> map20 = new HashMap<String, String>();
                map20.put("content", "瑞典夏天的轻盈美味");
                map20.put("contentUrl", "http://m.quanjing.com/topic/m05.html");
                map20.put("imageUrl", "http://m.quanjing.com/topic/cover/m05.jpg");
                list.add(map20);
                break;
            case FASHION:
                Map<String, String> map21 = new HashMap<String, String>();
                map21.put("content", "维多利亚秘密内衣秀后台探秘");
                map21.put("contentUrl", "http://m.quanjing.com/topic/s01.html");
                map21.put("imageUrl", "http://m.quanjing.com/topic/cover/s01.jpg");
                list.add(map21);
                Map<String, String> map22 = new HashMap<String, String>();
                map22.put("content", "2015春季女装艳花系列Lookbook");
                map22.put("contentUrl", "http://m.quanjing.com/topic/s02.html");
                map22.put("imageUrl", "http://m.quanjing.com/topic/cover/s02.jpg");
                list.add(map22);
                Map<String, String> map23 = new HashMap<String, String>();
                map23.put("content", "田园精灵——Zahia Dehar的田园风");
                map23.put("contentUrl", "http://m.quanjing.com/topic/s03.html");
                map23.put("imageUrl", "http://m.quanjing.com/topic/cover/s03.jpg");
                list.add(map23);
                Map<String, String> map24 = new HashMap<String, String>();
                map24.put("content", "红毯上的战服——Angel Sanchez与她的唯美婚纱");
                map24.put("contentUrl", "http://m.quanjing.com/topic/s04.html");
                map24.put("imageUrl", "http://m.quanjing.com/topic/cover/s04.jpg");
                list.add(map24);
                Map<String, String> map25 = new HashMap<String, String>();
                map25.put("content", "一幅美人绘——Dior浮世绘系列成衣秀");
                map25.put("contentUrl", "http://m.quanjing.com/topic/s05.html");
                map25.put("imageUrl", "http://m.quanjing.com/topic/cover/s05.jpg");
                list.add(map25);
                break;
            case ENCYCLOPEDIA:
                Map<String, String> map26 = new HashMap<String, String>();
                map26.put("content", "有关水獭的一切");
                map26.put("contentUrl", "http://m.quanjing.com/topic/b01.html");
                map26.put("imageUrl", "http://m.quanjing.com/topic/cover/b01.jpg");
                list.add(map26);
                Map<String, String> map27 = new HashMap<String, String>();
                map27.put("content", "正在消失的埃塞俄比亚文明");
                map27.put("contentUrl", "http://m.quanjing.com/topic/b02.html");
                map27.put("imageUrl", "http://m.quanjing.com/topic/cover/b02.jpg");
                list.add(map27);
                Map<String, String> map28 = new HashMap<String, String>();
                map28.put("content", "西西里岛的活火山——埃特纳火山");
                map28.put("contentUrl", "http://m.quanjing.com/topic/b03.html");
                map28.put("imageUrl", "http://m.quanjing.com/topic/cover/b03.jpg");
                list.add(map28);
                Map<String, String> map29 = new HashMap<String, String>();
                map29.put("content", "巴厘岛的五彩水牛");
                map29.put("imageUrl", "http://m.quanjing.com/topic/cover/b04.jpg");
                list.add(map29);
                Map<String, String> map30 = new HashMap<String, String>();
                map30.put("content", "亿年积淀出的黄石公园");
                map30.put("imageUrl", "http://m.quanjing.com/topic/cover/b05.jpg");
                list.add(map30);
                break;
            default:
                break;
        }


        NewsAdapter newsAdapter = new NewsAdapter(this, list);

        tempListView.setAdapter(newsAdapter);
        tempListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(TempActivity.this, MWTContentActivity.class);
                String contentUrl = map.get("contentUrl");
                if (contentUrl != null && !contentUrl.equals("")) {
                    intent.putExtra("contentUrl", map.get("contentUrl"));
                    startActivity(intent);
                }
            }
        });
    }


    private class NewsAdapter extends BaseAdapter {

        private List<Map<String, String>> newsList;
        private Context context;

        public NewsAdapter(Context context, List<Map<String, String>> newsList) {
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            holder = new ViewHolder();

            if (position == 0) {
                Map<String, String> map = newsList.get(0);
                DynamicHeightImageView imageView = new DynamicHeightImageView(context);
                Picasso.with(context)
                        .load(map.get("imageUrl")).resize(width, (int) (width * 0.457))
                        .into(imageView);
                convertView = imageView;
            } else {
                convertView = View.inflate(context, R.layout.item_found, null);
                holder.imageView = (DynamicHeightImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_title);

                Map<String, String> map = newsList.get(position);
                holder.textView.setText(map.get("content"));
                Picasso.with(context)
                        .load(map.get("imageUrl")).resize(160, 120)
                        .into(holder.imageView);
            }
            return convertView;
        }

        private class ViewHolder {
            DynamicHeightImageView imageView;
            TextView textView;
        }
    }
}
