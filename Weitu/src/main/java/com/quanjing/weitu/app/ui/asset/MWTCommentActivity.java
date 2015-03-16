package com.quanjing.weitu.app.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTCommentData;
import com.quanjing.weitu.app.protocol.service.MWTAddCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTCommentService;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;
import com.quanjing.weitu.app.ui.community.square.XCRoundImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTCommentActivity extends MWTBaseActivity {

    private MWTCommentService commentService;
    private ListView commentList;
    private TextView contentText;
    private Button btn_send;
    private CommentAdapter adapter;
    private List<MWTCommentData> commentDataList = new ArrayList<>();

    private static String assetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwtcomment);

        initView();

        MWTRestManager restManager = MWTRestManager.getInstance();
        commentService = restManager.create(MWTCommentService.class);
        commentService.getComments(assetID, new Callback<MWTCommentResult>() {
            @Override
            public void success(MWTCommentResult result, Response response) {
                if (result.error != null) {
                    Toast.makeText(MWTCommentActivity.this, "获取数据错误", 1).show();
                    return;
                }
                if (result != null) {
                    commentDataList = result.comments;
                    adapter = new CommentAdapter(MWTCommentActivity.this, commentDataList);
                    commentList.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }

    private void initView() {
        assetID = getIntent().getExtras().getString(MWTAssetActivity.ARG_ASSETID);
        contentText = (TextView) findViewById(R.id.content);
        btn_send = (Button) findViewById(R.id.send);
        commentList = (ListView) findViewById(R.id.list_comment);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentText.getText().toString();
                if (!content.equals("")) {
                    addComment(content);
                    // 关闭输入法
                    contentText.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(MWTCommentActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });


    }

    private void addComment(String content) {
        commentService.addComments(assetID, "addComment", content, new Callback<MWTAddCommentResult>() {
            @Override
            public void success(MWTAddCommentResult result, Response response) {
                if (result.error != null) {
                    Toast.makeText(MWTCommentActivity.this, "获取数据错误", 1).show();
                    return;
                }
                if (result != null) {
                    commentDataList.add(0, result.comment);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MWTCommentActivity.this, "获取数据错误", 1).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mwtcomment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CommentAdapter extends BaseAdapter {

        private List<MWTCommentData> commentDataList;
        private Context context;

        public CommentAdapter(Context context, List<MWTCommentData> commentDataList) {
            this.context = context;
            this.commentDataList = commentDataList;
        }

        @Override
        public int getCount() {
            return commentDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return commentDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            holder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_comment, null);
            holder.imageView = (XCRoundImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);

            MWTCommentData commentData = commentDataList.get(position);
            MWTUserManager userManager = MWTUserManager.getInstance();
            MWTUser user = userManager.getUserByID(commentData.userID);
            holder.textView.setText(user.getNickname());
            holder.contentTextView.setText(commentData.content);
            Picasso.with(context)
                    .load(user.getAvatarImageInfo().url).resize(160, 120)
                    .into(holder.imageView);
            return convertView;
        }

        private class ViewHolder {
            XCRoundImageView imageView;
            TextView textView;
            TextView contentTextView;
        }
    }
}
