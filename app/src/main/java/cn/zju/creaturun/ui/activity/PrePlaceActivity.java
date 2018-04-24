package cn.zju.creaturun.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zju.creaturun.R;
import cn.zju.creaturun.core.PathNavi;
import cn.zju.creaturun.core.PathTracer;
import cn.zju.creaturun.ui.view.RevealBackgroundView;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class PrePlaceActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener, AMap.OnMapClickListener {

    public static List<List<LatLng>> pathKeyPoint=null; //next stage data
    public static List<List<LatLng>>  pathDrawPoint=null;
    public static String startTime=null;

    private Handler handler=null;

    private List<List<LatLng>> latLngs=null;
    private List<LatLng> cur_latLngs=null;

    private MapView mMapView = null;
    private PathNavi pathNavi=null;
    private PathTracer pathTracer=null;

    private boolean canStart=false;

    private ImageButton btn_refresh=null;
    private ImageButton btn_redo=null;
    private ImageButton btn_undo=null;
    private ImageButton btn_newplace=null;

    private LatLng OldPoint=null;
    private List<LatLng> OldPath=null;

    @Override
    public void onMapClick(LatLng latLng) {
        if (pathNavi!=null && canStart==true){
            if (cur_latLngs.size()>0)
                pathNavi.CalcWalkRoute(cur_latLngs.get(cur_latLngs.size()-1),latLng);
            else
                mMapView.getMap().addMarker(new MarkerOptions().
                        position(latLng).
                        title("Current position").
                        snippet("DefaultMarker"));

            cur_latLngs.add(latLng);
        }
    }

    //------------------------------------------//
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.vView1)
    View vView1;
    @BindView(R.id.vView2)
    View vView2;

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public static void prePlaceFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, PrePlaceActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_place);
        setupRevealBackground(savedInstanceState);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                if (bundle.getString("located")!=null){
                    canStart=true;
                    cur_latLngs.add(pathTracer.GetCurLatlngs().get(0));
                }
            }
        };

        mMapView=(MapView)findViewById(R.id.MapView_in_pre_place);
        mMapView.onCreate(savedInstanceState);

        pathTracer=new PathTracer(mMapView,getApplicationContext(),handler);
        pathTracer.GetAMAP().setOnMapClickListener(this);
        pathNavi=new PathNavi(getApplicationContext(),pathTracer.GetAMAP());
        latLngs=new ArrayList<>();

        initDrawButton();
    }

    private void initDrawButton(){
        btn_refresh=(ImageButton)findViewById(R.id.drawrefresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng=latLngs.get(0).get(0);
                latLngs.clear();
                cur_latLngs=new ArrayList<>();
                cur_latLngs.add(latLng);
                latLngs.add(cur_latLngs);

                pathNavi.clear();
                OldPoint=null;
                OldPath=null;
                pathNavi.ShowRoutes();
            }
        });
        btn_redo=(ImageButton)findViewById(R.id.drawredo);
        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OldPoint!=null) {
                    cur_latLngs.add(OldPoint);
                    OldPoint=null;
                    if (cur_latLngs.size() > 1) {
                        pathNavi.naviLatLngs.add(OldPath);
                        OldPath=null;
                    }
                }
                pathNavi.ShowRoutes();
            }
        });
        btn_undo=(ImageButton)findViewById(R.id.drawundo);
        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_latLngs.size()>0){
                    if (cur_latLngs.size()>1) {
                        OldPath = pathNavi.naviLatLngs.get(pathNavi.naviLatLngs.size() - 1);
                        pathNavi.naviLatLngs.remove(pathNavi.naviLatLngs.size() - 1);
                    }
                    OldPoint = cur_latLngs.get(cur_latLngs.size()-1);
                    cur_latLngs.remove(cur_latLngs.size()-1);
                }
                pathNavi.ShowRoutes();
            }
        });
        btn_newplace=(ImageButton)findViewById(R.id.drawnewplace);
        btn_newplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_latLngs=new ArrayList<>();
                latLngs.add(cur_latLngs);
                OldPoint=null;
                OldPath=null;
                pathNavi.ShowRoutes();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        latLngs.clear();
        cur_latLngs=new ArrayList<>();
        latLngs.add(cur_latLngs);

        canStart=false;
        if (pathNavi!=null)
            pathNavi.naviLatLngs.clear();
        pathTracer.StartTracingOnce();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        latLngs.clear();canStart=false;
        if (pathNavi!=null)
            pathNavi.naviLatLngs.clear();
        mMapView.onDestroy();
    }

    //入场动画相关，入场动画为实验状态，待调，未推行至所有ACTIVITY
    private void setupRevealBackground(Bundle savedInstanceState) {
        //vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }
    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vView1.setVisibility(View.VISIBLE);
            vView2.setVisibility(View.VISIBLE);
        } else {
            vView1.setVisibility(View.INVISIBLE);
            vView2.setVisibility(View.INVISIBLE);
        }
    }

    //随性跑
    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startTime=sdf.format(new Date());

        final Intent intent = new Intent(this, UsualRunActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    //绘制路径——理想状态是绘制路径也在这个ACTIVITY中，但是我不确定能否直接在定位地图上绘制路径，所以先开辟另一个ACTIVITY
    @OnClick(R.id.btnCreate1)
    public void onTakePhotoClick1() {

        if (latLngs.size()==0 || latLngs.get(0).size()<2){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Creaturun" )
                    .setMessage("need two or more key points" )
                    .setPositiveButton("confirm" ,  null )
                    .show();
        }
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PrePlaceActivity.this);
            dialog.setTitle("绘制完成");
            dialog.setMessage("现在去跑吗？");
            dialog.setCancelable(false);
            //跑步
            dialog.setPositiveButton("Go！",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    pathKeyPoint = latLngs;
                    pathDrawPoint = pathNavi.naviLatLngs;

                    // pathNavi.Release();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    startTime = sdf.format(new Date());

                    Intent intent = new Intent(PrePlaceActivity.this, NaviRunActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
            //保存路线，重新绘制路线
            dialog.setNegativeButton("保存路线",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //need refine
                }
            });
            dialog.show();

        }

    }
}

