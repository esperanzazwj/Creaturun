package cn.zju.creaturun.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.Utils;
import cn.zju.creaturun.core.ConfigManager;
import cn.zju.creaturun.core.InfoManager;
import cn.zju.creaturun.core.UserManager;
import cn.zju.creaturun.ui.adapter.FeedAdapter;
import cn.zju.creaturun.ui.adapter.FeedItemAnimator;
import cn.zju.creaturun.ui.view.FeedContextMenu;
import cn.zju.creaturun.ui.view.FeedContextMenuManager;

public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {

    List<String> pictures=new ArrayList<>();

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.content)
    CoordinatorLayout clContent;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFeed();

        File dir=new File(Environment.getExternalStorageDirectory() + "/Creaturun");
        if (!dir.exists()){
            dir.mkdir();
        }

        ConfigManager.LoadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InfoManager im=new InfoManager(UserManager.GetCurrentUser());
        im.LoadInfo();
        String path = Environment.getExternalStorageDirectory() + "/Creaturun/" + UserManager.GetCurrentUser() + "/";
        int tot=im.GetUserInfo().size();
        pictures.clear();
        for (int i=tot-1;i>=0;i--){
            pictures.add(path + im.GetUserInfo().get(i).date + "png");
        }

        feedAdapter.updateItems(false,pictures);
    }

    //主界面路线recyclerlist展示，通过feedAdapter配置内容
    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    //动画效果
    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }
    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        feedAdapter.updateItems(true, pictures);
    }


    //小菜单(run\share等，先放着不管
    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    //打开用户主页
    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    //开始导航跑此路线,进入NaviRunActivity
    @Override
    public void onRunClick(int feedItem) {
        //FeedContextMenuManager.getInstance().hideContextMenu();
        final Intent intent = new Intent(this, NaviRunActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //分享此路线
    @Override
    public void onSharePhotoClick(int feedItem) {
        final Intent intent = new Intent(this, PublishActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    //保存到路线集
    @Override
    public void onCopyShareUrlClick(int feedItem) {
        Snackbar.make(clContent, "Saved!", Snackbar.LENGTH_SHORT).show();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    //准备开始跑步，进入PrePlaceActivity
    //不是用intent直接打开是因为需要动画效果
    @OnClick(R.id.btnCreate)
    public void onPrePlaceClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        PrePlaceActivity.prePlaceFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }


}
