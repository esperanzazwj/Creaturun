package cn.zju.creaturun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;

/**
 * Created by 万方方 on 2016/12/6.
 */

//设置个人信息
//edittext:name 名字
//edittext:signature 签名
//头像
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.button1)
    Button choosebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    @OnClick(R.id.button1)
    public void onPrePlaceClick() {
        final Intent intent = new Intent(this, ChoosePhotoActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
