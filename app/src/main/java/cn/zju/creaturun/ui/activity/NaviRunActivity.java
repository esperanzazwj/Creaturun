package cn.zju.creaturun.ui.activity;

import android.app.Activity;
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
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.zju.creaturun.R;
import cn.zju.creaturun.core.PathTracer;

/**
 * Created by 万方方 on 2016/12/6.
 */

//按照轨迹（用户确定的点序列）导航跑步，显示区域为usualrunmap，现在为普通ImageView
public class NaviRunActivity extends Activity implements AMapNaviListener, AMapNaviViewListener, SensorEventListener {

    AMapNaviView mAMapNaviView= null;
    AMapNavi mAMapNavi = null;
    SensorManager mSensorManager=null;
    Sensor mStepSensor=null;
    Handler handler=null;

    public static boolean navi_run_data_enabled=false;
    public static List<List<LatLng>>  pathKeyPoint_navi_run=null; //next stage data
    public static List<List<LatLng>>  pathDrawPoint_navi_run=null;
    public static double time_navi_run=0;
    public static double distance_navi_run=0;
    public static int steps_navi_run=0;
    public static String startTime_navi_run=null;

    int current_path=0;
    int current=0;

    boolean finished=false;

    private TextView timelong=null;
    private TextView distance=null;
    private TextView steps=null;

    double timeCounter=0;
    double distanceCounter=0;
    int stepCounter=0;

    FloatingActionButton btnStop=null;

    Timer timer=null;

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
            distanceCounter=stepCounter*0.45;
            distance.setText(Double.toString(distanceCounter));
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        if (pathKeyPoint_navi_run.size()>0 && pathKeyPoint_navi_run.get(0).size()>=2)
        {
            LatLng L1=pathKeyPoint_navi_run.get(0).get(0),L2=pathKeyPoint_navi_run.get(0).get(1);current++;
            mAMapNavi.calculateWalkRoute(new NaviLatLng(L1.latitude,L1.longitude),new NaviLatLng(L2.latitude,L2.longitude));
        }
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {
        if (current < pathKeyPoint_navi_run.get(current_path).size() - 2){
            LatLng L1=pathKeyPoint_navi_run.get(current_path).get(current);
            LatLng L2=pathKeyPoint_navi_run.get(current_path).get(current+1);
            current++;
            mAMapNavi.stopNavi();
            mAMapNavi.calculateWalkRoute(new NaviLatLng(L1.latitude,L1.longitude),new NaviLatLng(L2.latitude,L2.longitude));
        }
        else {
            current_path++;
            current = 0;
            if (current_path >= pathKeyPoint_navi_run.size()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Creaturun")
                        .setMessage("you have finished this running")
                        .setPositiveButton("confirm", null)
                        .show();

                finished=true;

                mAMapNavi.stopNavi();

                time_navi_run = timeCounter;
                distance_navi_run = distanceCounter;
                steps_navi_run = stepCounter;
                navi_run_data_enabled = true;

                final Intent intent = new Intent(NaviRunActivity.this, PostProcActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

    }

    @Override
    public void onCalculateRouteSuccess() {
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_run);

        timelong=(TextView)findViewById(R.id.timelong_navi);
        distance=(TextView)findViewById(R.id.distance_navi);
        steps=(TextView)findViewById(R.id.steps_navi);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                timelong.setText(bundle.getString("time"));
            }
        };

        navi_run_data_enabled=false;
        pathKeyPoint_navi_run=PrePlaceActivity.pathKeyPoint;
        pathDrawPoint_navi_run=PrePlaceActivity.pathDrawPoint;
        startTime_navi_run=PrePlaceActivity.startTime;

        btnStop=(FloatingActionButton)findViewById(R.id.btnStop_navi);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(NaviRunActivity.this);
                dialog.setTitle("已暂停");
                dialog.setMessage("已停止记录轨迹");
                dialog.setCancelable(false);

                timer.cancel();
                unregisterSensor();

                //完成跑步，处理图像
                dialog.setPositiveButton("完成",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                      //  if (finished) {
                            mAMapNavi.stopNavi();
                            time_navi_run = timeCounter;
                            distance_navi_run = distanceCounter;
                            steps_navi_run = stepCounter;
                            navi_run_data_enabled = true;

                            final Intent intent = new Intent(NaviRunActivity.this, PostProcActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                      //  }
                     //   else{
                      //      Toast.makeText(getApplicationContext(),"you have not finished your running plan", Toast.LENGTH_SHORT).show();
                      //  }
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
                    }
                });
                dialog.show();

            }
        });

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);

        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions mAMapNaviViewOptions=new AMapNaviViewOptions();
        mAMapNaviViewOptions.setLayoutVisible(false);

        mAMapNaviView.setViewOptions(mAMapNaviViewOptions);

        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);

        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
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

    public void unregisterSensor(){
        mSensorManager.unregisterListener(this);
    }

    public void registerSensor(){
        mSensorManager.registerListener(this, mStepSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
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
                // bundle.putString("distance", String.format("%.2f",pathTracer.GetTracedDistance())+"m");
                bundle.putString("time",Double.toString(timeCounter)+"s");
                msg.setData(bundle);
                handler.sendMessage(msg);
                timeCounter++;
            }
        },0,1000);

        mSensorManager.registerListener(this, mStepSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        finished=false;
        current_path=0;
        current=0;

        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        mSensorManager.unregisterListener(this);
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mSensorManager.unregisterListener(this);
        mAMapNaviView.onDestroy();
    }

    //naviView listener
    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {

        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }
}

