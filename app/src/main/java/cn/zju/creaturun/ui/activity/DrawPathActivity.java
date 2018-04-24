package cn.zju.creaturun.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import butterknife.OnClick;
import cn.zju.creaturun.R;

/**
 * Created by 万方方 on 2016/12/6.
 */

//绘制路径，功能区域为layout中的drawmap，现在是普通ImageView
public class DrawPathActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_path);
    }
    //绘制完成，开始导航跑步
    @OnClick(R.id.roundBtn)
    public void onFinishClick() {
        final Intent intent = new Intent(this, NaviRunActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
