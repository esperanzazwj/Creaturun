package cn.zju.creaturun.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.core.PathTracer;

/**
 * Created by 万方方 on 2016/12/6.
 */

//直接跑步，无导航，记录轨迹，带轨迹的地图显示区域为usualrunmap，现在为普通ImageView
// 显示距离(Textview:distance)时长(timelong)步数(steps)等信息(具体待定）

public class UsualRunActivity extends BaseDrawerActivity implements SensorEventListener {

    SensorManager mSensorManager=null;
    Sensor mStepSensor=null;

    Handler handler=null;

    private MapView mMapView = null;

    private PathTracer pathTracer=null;

    private TextView timelong=null;
    private TextView distance=null;
    private TextView steps=null;

    Timer timer=null;

    double timeCounter=0;
    double distanceCounter=0;
    int stepCounter=0;

    public static boolean usual_run_data_enabled=false;

    public static List<List<LatLng>> pathDrawPoint_usual_run=null;//next stage data
    public static double time_usual_run=0;
    public static double distance_usual_run=0;
    public static int steps_usual_run=0;
    public static String startTime_usual_run=null;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                stepCounter++;
            }
            steps.setText(Integer.toString(stepCounter));
            distanceCounter = stepCounter*0.45;
            distance.setText(Double.toString(distanceCounter));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usual_run);

        timelong=(TextView)findViewById(R.id.timelong);
        distance=(TextView)findViewById(R.id.distance);
        steps=(TextView)findViewById(R.id.steps);

        usual_run_data_enabled=false;
        startTime_usual_run=PrePlaceActivity.startTime;

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                timelong.setText(bundle.getString("time"));
            }
        };

        mMapView = (MapView) findViewById(R.id.MapView_in_usual_run);
        mMapView.onCreate(savedInstanceState);
        pathTracer=new PathTracer(mMapView,getApplicationContext(),handler);

        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);

        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
        //Toast.makeText(getApplicationContext(),"Version: "+Integer.toString(VERSION_CODES),Toast.LENGTH_SHORT).show();
        if (VERSION_CODES<19)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Creaturun" )
                    .setMessage("need Android 4.4 for step counter" )
                    .setPositiveButton("confirm" ,  null )
                    .show();
            finish();
        }

        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

    }

    //停止
    @OnClick(R.id.btnCreate1)
    public void onStopClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UsualRunActivity.this);
        dialog.setTitle("已暂停");
        dialog.setMessage("已停止记录轨迹");
        dialog.setCancelable(false);

        pathTracer.StopTracing();
        timer.cancel();
        mSensorManager.unregisterListener(this);

        //完成跑步，处理图像
        dialog.setPositiveButton("完成",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

                pathDrawPoint_usual_run=pathTracer.GetLatlngs();
                time_usual_run=timeCounter;
                distance_usual_run=stepCounter*0.5;
                steps_usual_run=stepCounter;
                usual_run_data_enabled=true;

                Intent intent = new Intent(UsualRunActivity.this, PostProcActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        //继续记录gps轨迹
        dialog.setNegativeButton("继续",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                timer=new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("time",Double.toString(timeCounter)+"s");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        timeCounter++;
                    }
                },0,1000);
                registerSensor();
                pathTracer.StartTracing();

            }
        });
        dialog.show();

    }

    public void registerSensor()
    {
        mSensorManager.registerListener(this, mStepSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mSensorManager.unregisterListener(this);
        pathTracer.ResetTracing();
        mMapView.onDestroy();
    }

    @Override

    protected void onResume() {
        super.onResume();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("time",Double.toString(timeCounter)+"s");
                msg.setData(bundle);
                handler.sendMessage(msg);
                timeCounter++;
            }
        },0,1000);
        mSensorManager.registerListener(this, mStepSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        //pathTracer.ResetTracing();
        pathTracer.StartTracing();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        mSensorManager.unregisterListener(this);
        pathTracer.StopTracing();
        mMapView.onPause();
    }
    @Override

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}

