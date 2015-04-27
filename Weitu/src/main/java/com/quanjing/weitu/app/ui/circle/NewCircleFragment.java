package com.quanjing.weitu.app.ui.circle;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.common.MWTThemer;
import com.quanjing.weitu.app.model.MWTAuthManager;
import com.quanjing.weitu.app.model.MWTNewCircleManager;
import com.quanjing.weitu.app.model.MWTRecommendManager;
import com.quanjing.weitu.app.model.MWTRestManager;
import com.quanjing.weitu.app.model.MWTUser;
import com.quanjing.weitu.app.model.MWTUserManager;
import com.quanjing.weitu.app.protocol.MWTCircleComment;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.service.MWTAddCommentResult;
import com.quanjing.weitu.app.protocol.service.MWTCircleService;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.common.MWTPageFragment;
import com.quanjing.weitu.app.ui.common.PreImeEditText;
import com.quanjing.weitu.app.ui.photo.AlbumActivity;
import com.quanjing.weitu.app.ui.photo.Bimp;
import com.quanjing.weitu.app.ui.photo.ImageItem;
import com.quanjing.weitu.app.ui.user.LocalAlbumActivity;
import com.quanjing.weitu.app.ui.user.MWTUploadPicActivity;

import org.lcsky.SVProgressHUD;

import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 圈子页面 替换原来的美图
 */

public class NewCircleFragment extends MWTPageFragment {

    private PullToRefreshListView circlListView;
    private NewCircleAdapter adapter;
    private MWTDataRetriever dataRetriver;
    private MenuItem addMenuItem;
    private static int MENU_PHOTO = 1;
    public static int COUNT = 30;

    private static PopupWindow pop = null;
    private RelativeLayout ll_popup;
    private PreImeEditText commentText;
    private boolean isRegister = false;
    private String activityId;
    private String replyuserid;
    private int position = 0;

    public NewCircleFragment() {
        super();
        this.setDataRetriver(new MWTDataRetriever() {
            @Override
            public void refresh(MWTCallback callback) {
                if (adapter != null) {
                    adapter.refresh(callback);
                    startRefreshAnimation();
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(MWTCallback callback) {
                if (adapter != null) {
                    adapter.loadMore(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_circle, container, false);

        circlListView = (PullToRefreshListView) view.findViewById(R.id.talentListView);
        circlListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new NewCircleAdapter(getActivity());
        circlListView.setAdapter(adapter);
//        MWTNewCircleManager cm = MWTNewCircleManager.getInstance();
//        cm.refreshCircles(COUNT, Long.MAX_VALUE, new MWTCallback() {
//            @Override
//            public void success() {
//            }
//
//            @Override
//            public void failure(MWTError error) {
//            }
//        });

        initPop();
        register();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        IconicFontDrawable iconDrawable = new IconicFontDrawable(getActivity());
        iconDrawable.setIcon(FontAwesomeIcon.COG);
        iconDrawable.setIconColor(MWTThemer.getInstance().getActionBarForegroundColor());

        addMenuItem = menu.add(Menu.NONE, MENU_PHOTO, 2, "相册");
        addMenuItem.setIcon(R.drawable.ic_add_photo).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(MWTBaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_PHOTO) {
            Bimp.tempSelectBitmap.clear();
            Intent intent = new Intent(getActivity(),
                    LocalAlbumActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
    }

    private void performRefresh() {
        if (dataRetriver != null) {
            dataRetriver.refresh(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregister();
    }

    private void performLoadMore() {
        if (dataRetriver != null) {
            dataRetriver.loadMore(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(MWTDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (circlListView != null) {
            circlListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (circlListView != null) {
            circlListView.onRefreshComplete();
        }
    }

    public void initPop() {

        pop = new PopupWindow(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_popup_comment, null);
        ll_popup = (RelativeLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        commentText = (PreImeEditText) view
                .findViewById(R.id.commentText);
        Button send = (Button) view
                .findViewById(R.id.send);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
                commentText.clearFocus();
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(commentText.getText().toString())) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), "请输入评论内容", 2.0f);
                    return;
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
                commentText.clearFocus();

                addComment(commentText.getText().toString());
                pop.dismiss();
                ll_popup.clearAnimation();
                adapter.updateView(commentText.getText().toString(), replyuserid, activityId, position);
            }
        });

    }

    private void register() {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter("com.quanjing.sendComment");
            getActivity().registerReceiver(commentBroad, filter);
            IntentFilter filter2 = new IntentFilter("com.quanjing.hideKeyboard");
            getActivity().registerReceiver(hideKeyBoardcast, filter2);
            isRegister = true;
        }
    }

    private void unregister() {
        if (isRegister) {
            getActivity().unregisterReceiver(commentBroad);
            getActivity().unregisterReceiver(hideKeyBoardcast);
            isRegister = false;
        }
    }

    private BroadcastReceiver commentBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            commentText.setFocusable(true);
            commentText.setFocusableInTouchMode(true);
            commentText.requestFocus();
            //对于刚跳到一个新的界面就要弹出软键盘的情况上述代码可能由于界面为加载完全而无法弹出软键盘。此时应该适当的延迟弹出软键盘如500毫秒
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                               public void run() {
                                   InputMethodManager inputManager =
                                           (InputMethodManager) commentText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                   inputManager.showSoftInput(commentText, 0);
                               }

                           },
                    500);


            activityId = intent.getStringExtra("activityId");
            replyuserid = intent.getStringExtra("replyuserid");
            position = intent.getIntExtra("position", 0);
            if (replyuserid != null && !replyuserid.equals("")) {
                commentText.setHint("回复 " + MWTUserManager.getInstance().getUserByID(replyuserid).getNickname() + ":");
            } else {
                commentText.setHint("");
            }
            View parentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_new_circle, null);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
            pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            commentText.setText("");

            // 定位到评论所在行
            circlListView.getRefreshableView().setSelection(position + 1);
        }
    };

    private BroadcastReceiver hideKeyBoardcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pop != null)
                pop.dismiss();
        }
    };

    private void addComment(final String content) {
        MWTRestManager restManager = MWTRestManager.getInstance();
        MWTCircleService circleService = restManager.create(MWTCircleService.class);
        circleService.addCommentToActivity(activityId, replyuserid, content, new Callback<MWTAddCommentResult>() {
            @Override
            public void success(MWTAddCommentResult result, Response response) {
//                if (result.error != null) {
//                    return;
//                }
//                if (result != null) {
//                    adapter.updateView(content, replyuserid);
//                }
            }

            @Override
            public void failure(RetrofitError error) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("发布失败，再试一次？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                addComment(content);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }
}

