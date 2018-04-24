package cn.zju.creaturun.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.core.InfoManager;
import cn.zju.creaturun.core.UserManager;
import cn.zju.creaturun.ui.adapter.UserProfileAdapter;
import cn.zju.creaturun.ui.utils.CircleTransformation;
import cn.zju.creaturun.ui.view.RevealBackgroundView;

/**
 * Created by 万方方 on 2016/12/6.
 */

//用户个人主页
//textview:vUsername 用户名
//textview:vCreates 作品数
//textview:vDistance 总距离
//textview:vSteps 总步数
//显示的作品图片在UserProfileAdapter中部署，现在是在线加载了strings.xml中设置图片url
//用户头像在此activity中加载了strings.xml中的图片的url
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {

    TextView vUsername=null;
    TextView vCreates=null;
    TextView vDistance=null;
    TextView vSteps=null;

    List<String> pictures=null;

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;

    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = getString(R.string.user_profile_photo);

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);

        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);

        initProfile();
    }

    private void initProfile(){
        vUsername=(TextView)findViewById(R.id.vUsername);
        vCreates=(TextView)findViewById(R.id.vCreates);
        vDistance=(TextView)findViewById(R.id.vDistance);
        vSteps=(TextView)findViewById(R.id.vSteps);

        String curUser= UserManager.GetCurrentUser();
        vUsername.setText(curUser);
        InfoManager im=new InfoManager(curUser);
        im.LoadInfo();
        vCreates.setText(Integer.toString(im.calcTotWorks()));
        vDistance.setText(String.format("%.2f",im.calcTotDistance()));
        vSteps.setText(Integer.toString(im.calcTotSteps()));

        String path= "file:" + Environment.getExternalStorageDirectory() + "/Creaturun/" + curUser + "/";
        int tot=im.GetUserInfo().size();
        pictures=new ArrayList<>();
        for (int i=0;i<tot;i++){
            pictures.add(path + im.GetUserInfo().get(i).date + "png");
        }
    }


    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            // userPhotosAdapter = new UserProfileAdapter(this);
            userPhotosAdapter = new UserProfileAdapter(this,pictures);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0);

        vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }
    @OnClick(R.id.btnCreate)
    public void onPrePlaceClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        PrePlaceActivity.prePlaceFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }
    @OnClick(R.id.btnFollow)
    public void onRegisterClick() {
        /*
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);*/
        btnFollow.setText("已关注");
    }
}

