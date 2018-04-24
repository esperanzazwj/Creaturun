package cn.zju.creaturun.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.core.ConfigManager;

/**
 * Created by 万方方 on 2016/12/6.
 */

//设置画笔和地图样式，样式怎么选待定
public class SettingsActivity extends BaseDrawerActivity implements  ColorPicker.OnColorChangedListener, SeekBar.OnSeekBarChangeListener{

    int color;
    int width;
    int map_type;

    ColorPicker picker=null;
    SeekBar seekBar=null;
    FloatingActionButton saveConfig=null;
    @BindView(R.id.putong)
    ImageButton putong;
    ;@BindView(R.id.heiye)
    ImageButton heiye;
    @BindView(R.id.threed)
    ImageButton threed;
    @BindView(R.id.weixing)
    ImageButton weixing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveConfig=(FloatingActionButton)findViewById(R.id.saveConfig);
        saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.SetConfig(color,width,map_type);
                ConfigManager.SaveConfig();
                finish();
            }
        });
        init();
    }
    void init(){

        color= ConfigManager.GetColor();
        width= ConfigManager.GetWidth();
        map_type=ConfigManager.GetMapType();
        switch (map_type)
        {
            case 0:
                threed.setImageResource(R.drawable.ic_done_black_48dp);
                putong.setImageResource(R.drawable.touming400);
                heiye.setImageResource(R.drawable.touming400);
                weixing.setImageResource(R.drawable.touming400);
                break;
            case 1:
                heiye.setImageResource(R.drawable.ic_done_white_48dp);
                putong.setImageResource(R.drawable.touming400);
                threed.setImageResource(R.drawable.touming400);
                weixing.setImageResource(R.drawable.touming400);
                break;
            case 2:
                putong.setImageResource(R.drawable.ic_done_black_48dp);
                heiye.setImageResource(R.drawable.touming400);
                threed.setImageResource(R.drawable.touming400);
                weixing.setImageResource(R.drawable.touming400);
                break;
            case 3:
                weixing.setImageResource(R.drawable.ic_done_white_48dp);
                putong.setImageResource(R.drawable.touming400);
                threed.setImageResource(R.drawable.touming400);
                heiye.setImageResource(R.drawable.touming400);
                break;
        }

        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

        picker.setColor(color);
        picker.setOldCenterColor(color);
        picker.setOnColorChangedListener(this);

        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onColorChanged(int color) {
        this.color=color;

        picker.setOldCenterColor(color);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        width=progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @OnClick(R.id.putong)
    public void onPutongClick() {
        putong.setImageResource(R.drawable.ic_done_black_48dp);
        heiye.setImageResource(R.drawable.touming400);
        threed.setImageResource(R.drawable.touming400);
        weixing.setImageResource(R.drawable.touming400);
        map_type=2;

    }
    @OnClick(R.id.heiye)
    public void onHeiyeClick() {
        heiye.setImageResource(R.drawable.ic_done_white_48dp);
        putong.setImageResource(R.drawable.touming400);
        threed.setImageResource(R.drawable.touming400);
        weixing.setImageResource(R.drawable.touming400);
        map_type=1;
    }
    @OnClick(R.id.threed)
    public void onThreedClick() {
        threed.setImageResource(R.drawable.ic_done_black_48dp);
        putong.setImageResource(R.drawable.touming400);
        heiye.setImageResource(R.drawable.touming400);
        weixing.setImageResource(R.drawable.touming400);
        map_type=0;
    }
    @OnClick(R.id.weixing)
    public void onWeixingClick() {
        weixing.setImageResource(R.drawable.ic_done_white_48dp);
        putong.setImageResource(R.drawable.touming400);
        threed.setImageResource(R.drawable.touming400);
        heiye.setImageResource(R.drawable.touming400);
        map_type=3;
    }

}

