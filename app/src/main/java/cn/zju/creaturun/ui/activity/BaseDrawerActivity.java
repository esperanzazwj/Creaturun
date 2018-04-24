package cn.zju.creaturun.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import cn.zju.creaturun.R;
import cn.zju.creaturun.ui.utils.CircleTransformation;

import static cn.zju.creaturun.R.id.menu_about;
import static cn.zju.creaturun.R.id.menu_direct;
import static cn.zju.creaturun.R.id.menu_feed;
import static cn.zju.creaturun.R.id.menu_news;
import static cn.zju.creaturun.R.id.menu_photo_you_liked;
import static cn.zju.creaturun.R.id.menu_photos_nearby;
import static cn.zju.creaturun.R.id.menu_popular;
import static cn.zju.creaturun.R.id.menu_settings;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class BaseDrawerActivity extends BaseActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
    @BindString(R.string.user_profile_photo)
    String profilePhoto;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
        setupHeader();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });
        //设置item点击
        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                int id = menuItem.getItemId();
                int[] startingLocation = new int[2];
                onMenuItemClick(id);
                return true;
            }
        });
        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }
    //处理菜单item
    public void onMenuItemClick(int id){
        //关闭抽屉侧滑菜单
        drawerLayout.closeDrawers();
        //测试
        if(id == menu_feed){
            final Intent intent = new Intent(this, PrePlaceActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        //开始跑步
        if(id == menu_direct){
            int[] startingLocation = new int[2];
            //fabCreate.getLocationOnScreen(startingLocation);
            //startingLocation[0] += fabCreate.getWidth() / 2;
            startingLocation[0]=150;startingLocation[1]=150;
            PrePlaceActivity.prePlaceFromLocation(startingLocation, this);
            overridePendingTransition(0, 0);
        }
        //个人主页
        if(id == menu_news){
            drawerLayout.closeDrawer(Gravity.LEFT);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int[] startingLocation = new int[2];
                    //v.getLocationOnScreen(startingLocation);
                    //startingLocation[0] += v.getWidth() / 2;
                    startingLocation[0]=150;startingLocation[1]=150;
                    UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                    overridePendingTransition(0, 0);
                }
            }, 200);
        }
        //首页
        if(id == menu_popular){
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        if(id == menu_settings){
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        if(id == menu_about){
            final Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        //个人信息
        if(id == menu_photos_nearby){
            final Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        //路线
        if(id == menu_photo_you_liked){
            final Intent intent = new Intent(this, RoutersActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
}

