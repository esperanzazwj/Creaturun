package cn.zju.creaturun.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.core.ConfigManager;
import cn.zju.creaturun.core.InfoManager;
import cn.zju.creaturun.core.UserManager;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class PostProcActivity extends BaseDrawerActivity implements AMap.OnMapScreenShotListener, ColorPicker.OnColorChangedListener, SeekBar.OnSeekBarChangeListener {

    MapView mMapView=null;
    AMap mAMap=null;

    ColorPicker picker=null;
    SeekBar seekBar=null;

    ArrayList<Integer> color;
    ArrayList<Integer> width;
    int cur_line;
    int map_type;

    public static List<List<LatLng>> pathDrawPoint_post_proc=null;//next stage data
    public static double time_post_proc=0;
    public static double distance_post_proc=0;
    public static int steps_post_proc=0;
    public static String startTime_post_proc=null;

    public static Bitmap post_proc_bitmap=null;

    Handler handler;

    public Button btn_change=null;
    public Button btn_switchLine=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_proc);
        //点击放置图片的那个card来改变地图的样式
        btn_change=(Button)findViewById(R.id.changemap) ;
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostProcActivity.this, "样式改变", Toast.LENGTH_LONG).show();
                map_type=(map_type+1)%4;
                if (mAMap!=null) {
                    switch (map_type) {
                        case 0:
                            mAMap.setMapType(AMap.MAP_TYPE_NAVI);
                            break;
                        case 1:
                            mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
                            break;
                        case 2:
                            mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                            break;
                        case 3:
                            mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                            break;
                    }
                }
            }
        });
        btn_switchLine=(Button)findViewById(R.id.changeline) ;
        btn_switchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_line=(cur_line+1)%pathDrawPoint_post_proc.size();
            }
        });
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                if (bundle.getString("MapViewChange")!=null){
                    ShowMap();
                }
                if (bundle.getString("screenShot")!=null){

                    Intent intent = new Intent(PostProcActivity.this, PublishActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

            }
        };

        mMapView=(MapView)findViewById(R.id.MapView_in_post_proc);
        mMapView.onCreate(savedInstanceState);
        mAMap=mMapView.getMap();

        map_type=ConfigManager.GetMapType();

        switch (map_type) {
            case 0:
                mAMap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
            case 1:
                mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
            case 2:
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case 3:
                mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
        }

        initData();
        initColorPicker();

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(pathDrawPoint_post_proc.get(0).get(0)));
        ShowMap();
    }

    //完成并分享
    @OnClick(R.id.btnCreate)
    public void onFinishClick() {

        InfoManager infoManager=new InfoManager(UserManager.GetCurrentUser());
        infoManager.SaveInfo(startTime_post_proc + ".info",
                pathDrawPoint_post_proc.get(0),time_post_proc,distance_post_proc,steps_post_proc);

        mAMap.getMapScreenShot(this);

    }

    public void initColorPicker(){
        cur_line=0;
        color=new ArrayList<>();
        width=new ArrayList<>();
        for (int i=0;i<pathDrawPoint_post_proc.size();i++)
        {
            color.add(ConfigManager.GetColor());
            width.add(ConfigManager.GetWidth());
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

        picker.setColor(color.get(cur_line));
        picker.setOldCenterColor(color.get(cur_line));
        picker.setOnColorChangedListener(this);

        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }
    public void initData(){
        if (NaviRunActivity.navi_run_data_enabled==true){
            pathDrawPoint_post_proc=NaviRunActivity.pathDrawPoint_navi_run;
            time_post_proc=NaviRunActivity.time_navi_run;
            distance_post_proc=NaviRunActivity.distance_navi_run;
            steps_post_proc=NaviRunActivity.steps_navi_run;
            startTime_post_proc=NaviRunActivity.startTime_navi_run;
        }
        else if (UsualRunActivity.usual_run_data_enabled==true){
            pathDrawPoint_post_proc=UsualRunActivity.pathDrawPoint_usual_run;
            time_post_proc=UsualRunActivity.time_usual_run;
            distance_post_proc=UsualRunActivity.distance_usual_run;
            steps_post_proc=UsualRunActivity.steps_usual_run;
            startTime_post_proc=UsualRunActivity.startTime_usual_run;
        }
    }

    public void ShowMap(){
        if (pathDrawPoint_post_proc != null) {
            mAMap.clear();
            for (int i=0;i<pathDrawPoint_post_proc.size();i++){
                mAMap.addPolyline(new PolylineOptions().
                        addAll(pathDrawPoint_post_proc.get(i)).width(width.get(i)).color(color.get(i)));
            }
        }
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int status) {

        post_proc_bitmap=bitmap;

        if(null == bitmap){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(
                    Environment.getExternalStorageDirectory() + "/Creaturun/" + UserManager.GetCurrentUser()
                            + "/" + startTime_post_proc + ".png");

            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (b)
                buffer.append("截屏成功 ");
            else {
                buffer.append("截屏失败 ");
            }
            if (status != 0)
                buffer.append("地图渲染完成，截屏无网格");
            else {
                buffer.append( "地图未渲染完成，截屏有网格");
            }
            Toast.makeText(getApplicationContext(), buffer.toString(),Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();// 存放数据
        bundle.putString("screenShot","success");
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    @Override
    public void onColorChanged(int color) {
        this.color.set(cur_line,color);
        //Toast.makeText(this,Integer.toString(color),Toast.LENGTH_SHORT).show();
        picker.setOldCenterColor(color);

        Message msg = new Message();
        Bundle bundle = new Bundle();// 存放数据
        bundle.putString("MapViewChange","changed");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        width.set(cur_line,progress);
        //  Toast.makeText(this,Integer.toString(width),Toast.LENGTH_SHORT).show();
        Message msg = new Message();
        Bundle bundle = new Bundle();// 存放数据
        bundle.putString("MapViewChange","changed");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}

