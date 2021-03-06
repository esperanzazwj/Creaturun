package cn.zju.creaturun.core;

/**
 * Created by 万方方 on 2016/12/6.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyw on 2016/10/27.
 */

public class PathTracer implements LocationSource, AMapLocationListener, AMap.OnMapScreenShotListener {

    private Context context=null;
    private MapView mMapView=null;
    private AMap aMap=null;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private AMapLocationClientOption mLocationOption = null;

    //目前连续定位得到的路线
    private List<LatLng> cur_latLngs=null;
    // 多次定位得到的路线集合
    // latlngs中的第i个元素即为一个路线，记录了第i次定位得到的路线
    private List<List<LatLng>> latLngs=null;

    private boolean isTracing = false;
    private boolean locateOnce = false;
    private boolean locationButtonPressed = false;
    Bitmap screenShot=null;

    Handler handler=null;

    public AMap GetAMAP(){
        return aMap;
    }

    public List<List<LatLng>> GetLatlngs(){
        return latLngs;
    }

    public List<LatLng> GetCurLatlngs(){
        return cur_latLngs;
    }
    public PathTracer(MapView mMapView_, Context context_, Handler handler_){

        mMapView = mMapView_;
        context = context_;
        handler = handler_;

        initLocater();

        //设置定位监听后会立即出发一次定位
        if (aMap == null) {
            aMap = mMapView.getMap();
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是false
        }

        latLngs = new ArrayList<>();
    }

    public void initLocater(){
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(1000);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
       // locationButtonPressed = true;
        StartTracingOnce();
    }
    @Override
    public void deactivate() {

    }

    //定位回调事件
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                LatLng point = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                cur_latLngs.add(point);
                if (locateOnce) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(point));
                }

                aMap.clear();
                //在地图上设置路线与marker
                SetPath();
                SetMarker(point);

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                int errorCode = aMapLocation.getErrorCode();
                Toast.makeText(context, "AmapError"
                        + "location Error, ErrCode:" + errorCode + ", errInfo:"
                        + aMapLocation.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            //如果按了定位按钮的话，只定位一次
            if (locateOnce)
            {
                Message msg = new Message();
                Bundle bundle = new Bundle();// 存放数据
                bundle.putString("locate", "success");
                msg.setData(bundle);
                handler.sendMessage(msg);
                StopTracing();
            }
        }
    }

    private void SetPath(){
        if (latLngs != null) {
            for (int k = 0;k < latLngs.size();k++){
                aMap.addPolyline(new PolylineOptions().addAll(latLngs.get(k)).width(ConfigManager.GetWidth()).color(ConfigManager.GetColor()));
            }
        }
    }

    private void SetMarker(LatLng point){
        aMap.addMarker(new MarkerOptions().
                position(point).
                title("Current position").
                snippet("DefaultMarker"));
    }

    public Bitmap GetScreenShot(){
        return screenShot;
    }

    public void GenScreenShot()
    {
        screenShot=null;
        aMap.getMapScreenShot(this);
    }

    //只定位一次（点击定位按钮时）
    public boolean StartTracingOnce(){
        if (isTracing == false) {
            cur_latLngs = new ArrayList<>();
            locateOnce = true;
            isTracing = true;
            mLocationClient.startLocation();
            return true;
        }
        else return false;
    }

    //重置定位，清空路线集合
    public void ResetTracing(){
        StopTracing();
        latLngs.clear();
    }

    //连续定位（跑步时）
    public boolean StartTracing() {
        if (isTracing == false) {
            cur_latLngs = new ArrayList<>();
            latLngs.add(cur_latLngs);
            locateOnce=false;
            isTracing=true;
            mLocationClient.startLocation();
            return true;
        }
        else return false;
    }

    //停止定位
    public boolean StopTracing() {
        if (isTracing == true) {
            mLocationClient.stopLocation();
            locateOnce = false;
            isTracing = false;
            return true;
        }
        else return false;
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int status) {
        screenShot=bitmap;
        Message msg = new Message();
        Bundle bundle = new Bundle();// 存放数据
        bundle.putString("screenShot", "success");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    //计算路线距离
    public double GetTracedDistance(){
        double distance = 0.0;
        for (int k = 0;k < latLngs.size();k++)
        {
            List<LatLng> tlatLngs=latLngs.get(k);
            int sizeOfList=tlatLngs.size();
            for (int i = 0;i < sizeOfList - 1;i++) {
                distance += AMapUtils.calculateLineDistance(tlatLngs.get(i), tlatLngs.get(i + 1));
            }
        }
        return distance;
    }

    public AMapLocationClient getLocationClient()
    {
        return mLocationClient;
    }
}

