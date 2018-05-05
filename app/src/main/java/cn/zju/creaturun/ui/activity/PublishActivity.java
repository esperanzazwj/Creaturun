package cn.zju.creaturun.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.zju.creaturun.R;
import cn.zju.creaturun.Utils;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    public  static Bitmap publish_bitmap=null;

    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.savephoto)
    Switch savephoto;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        //设置了一个返回键，按下销毁分享事件，如果不好可直接去掉
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.this.finish();
            }
        });
        //保存到相册开关，现在向右滑动会提示true,向左滑动会提示false(只是判断展示，需要去掉）
        //需要的功能：如果用户选择true，在点击完成时将图片保存到本地相册
        //如果麻烦可去掉，直接默认保存到相册
        savephoto.setChecked(true);
        savephoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              savephoto.setChecked(true);
            }
        });

        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

      /*  ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });*/
        ShareSDK.initSDK(this);

        publish_bitmap=PostProcActivity.post_proc_bitmap;
        ivPhoto.setImageBitmap(publish_bitmap);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }
    //完成键的点击效果，现在是回到主界面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }
    //分享战果！
    @OnClick(R.id.button3)
    public void onShareClick(){
        showShare();
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        cn.sharesdk.onekeyshare.OnekeyShare oks = new cn.sharesdk.onekeyshare.OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段——获取用户输入的文字
        oks.setText("hh");
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(Environment.getExternalStorageDirectory() + "/Creaturun/User0");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}

