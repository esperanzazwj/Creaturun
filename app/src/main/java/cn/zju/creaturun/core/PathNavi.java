package cn.zju.creaturun.core;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class PathNavi implements AMapNaviListener {

    Context context = null;
    AMapNavi mAMapNavi = null;
    AMap aMap=null;
    public List<List<LatLng>> naviLatLngs=null;

    public void ShowRoutes(){
        for (int i=0;i < naviLatLngs.size();i++){
            aMap.addPolyline(new PolylineOptions(). addAll(naviLatLngs.get(i)).width(ConfigManager.GetWidth()).color(ConfigManager.GetColor()));
        }
    }

    public void ShowRoutesLast(){
        aMap.addPolyline(new PolylineOptions(). addAll(naviLatLngs.get(naviLatLngs.size() - 1)).width(ConfigManager.GetWidth()).color(ConfigManager.GetColor()));
    }

    @Override
    public void hideCross() {

    }

    @Override
    public void onInitNaviFailure() {

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

    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

    }

    @Override
    public void onCalculateRouteSuccess() {

        AMapNaviPath path = mAMapNavi.getNaviPath();
        List<NaviLatLng> naviLatLngs_t=path.getCoordList();
        //Toast.makeText(context, Integer.toString(naviLatLngs_t.size()), Toast.LENGTH_SHORT).show();

        List<LatLng> tNaviLatLngs=new ArrayList<>();
        for (int i=0;i<naviLatLngs_t.size();i++){
            NaviLatLng naviLatLng=naviLatLngs_t.get(i);
            tNaviLatLngs.add(new LatLng(naviLatLng.getLatitude(),naviLatLng.getLongitude()));
        }

        naviLatLngs.add(tNaviLatLngs);

       // ShowRoutes();
        ShowRoutesLast();
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
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

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
    public void hideLaneInfo() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    public void clear(){
        naviLatLngs.clear();
    }

    public PathNavi(Context context_, AMap aMap_) {
        context = context_;aMap=aMap_;
        mAMapNavi = AMapNavi.getInstance(context);
        mAMapNavi.addAMapNaviListener(this);
        naviLatLngs = new ArrayList<>();
    }

    public void Release(){
        if (mAMapNavi!=null)
            mAMapNavi.destroy();
    }

    public void CalcWalkRoute(LatLng x, LatLng y){
        mAMapNavi.calculateWalkRoute(new NaviLatLng(x.latitude, x.longitude), new NaviLatLng(y.latitude, y.longitude));
    }

}


