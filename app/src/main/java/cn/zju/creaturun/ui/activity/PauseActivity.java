package cn.zju.creaturun.ui.activity;

import android.os.Bundle;

import cn.zju.creaturun.R;

/**
 * Created by 万方方 on 2016/12/6.
 */

//暂停，现在是测试用的页面
public class PauseActivity extends BaseDrawerActivity {
    //ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
    //SVBar svBar = (SVBar) findViewById(R.id.svbar);
    //OpacityBar opacityBar = (OpacityBar) findViewById(opacitybar);
    //SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
    //ValueBar valueBar = (ValueBar) findViewById(valuebar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
    }
    /*
    public void test(){
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

//To get the color
        picker.getColor();
//To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
// adds listener to the colorpicker which is implemented
//in the activity
        //picker.setOnColorChangedListener(this);

//to turn of showing the old color
        picker.setShowOldCenterColor(false);

//adding onChangeListeners to bars
        //opacitybar.setOnOpacityChangeListener(new NumberPicker.OnOpacityChangeListener(){});
        //valuebar.setOnValueChangeListener(new NumberPicker.OnValueChangeListener(){});
        //saturationBar.setOnSaturationChangeListener(new NumberPicker.OnSaturationChangeListener(){});
    }
    */
}

