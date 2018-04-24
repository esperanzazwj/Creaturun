package cn.zju.creaturun.ui.activity;

import android.os.Bundle;

import cn.zju.creaturun.R;

/**
 * Created by 万方方 on 2016/12/6.
 */

//选取用户头像，可考虑提供几张供选择，好像比从sd卡中方便
public class ChoosePhotoActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
    }
}
