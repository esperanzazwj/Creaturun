package cn.zju.creaturun.core;

import android.os.Environment;

import com.amap.api.maps.model.LatLng;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by huyw on 2016/11/28.
 */

public class InfoManager {

    String UserName=null;

    public class UserInfo{
        public String date;
        public List<LatLng> path;
        public double time;
        public double distance;
        public int steps;
    }

    List<UserInfo> UserInfos=null;

    public void LoadInfo(){
        File file=new File(Environment.getExternalStorageDirectory() + "/Creaturun/" + UserName);
        FileFilter fileFilter=new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile() && pathname.getName().endsWith(".info")){
                    return true;
                } else {
                    return false;
                }
            }
        };
        File fileList[]=file.listFiles(fileFilter);
        UserInfos=new ArrayList<>();
        for (int i=0;i<fileList.length;i++){
            File fileInfo=fileList[i];
            Scanner input=null;
            try {
                input=new Scanner(fileInfo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            UserInfo ui=new UserInfo();

            String fileName=fileInfo.getName();
            ui.date=fileName.substring(0,fileName.length()-4);
            ui.time=input.nextDouble();
            ui.distance=input.nextDouble();
            ui.steps=input.nextInt();
            ui.path=new ArrayList<>();
            int pathSize=input.nextInt();
            for (int j=0;j<pathSize;j++){
                double lat=input.nextDouble();
                double lng=input.nextDouble();
                LatLng point=new LatLng(lat,lng);
                ui.path.add(point);
            }

            UserInfos.add(ui);
        }
    }
    public void SaveInfo(String SaveFileName, List<LatLng> path, double time, double distance, int steps){
        String fullPath = Environment.getExternalStorageDirectory() + "/Creaturun/" + UserName
                + "/" +SaveFileName;
        File file=new File(fullPath);

        if (file.exists()){

        }
        else try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter output= null;
        try {
            output = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        output.println(time);
        output.println(distance);
        output.println(steps);
        output.println(path.size());
        for (int i=0;i<path.size();i++){
            output.print(path.get(i).latitude);
            output.print(" ");
            output.print(path.get(i).longitude);
            output.print(" ");
        }
        output.flush();
        output.close();

    }
    public InfoManager( String UserName_){
        UserName=UserName_;
        File dir=new File(Environment.getExternalStorageDirectory() + "/Creaturun/" + UserName);
        if (!dir.exists())
        {
            dir.mkdir();
        }
    }

    public double calcTotDistance(){
        double tot=0.0;
        if (UserInfos!=null){
            int k=UserInfos.size();
            for (int i=0;i<k;i++){
                tot+=UserInfos.get(i).distance;
            }
        }
        return tot;
    }

    public int calcTotSteps(){
        int tot=0;
        if (UserInfos!=null){
            int k=UserInfos.size();
            for (int i=0;i<k;i++){
                tot+=UserInfos.get(i).steps;
            }
        }
        return tot;
    }

    public int calcTotWorks(){
        if (UserInfos!=null)
            return UserInfos.size();
        else return 0;
    }

    public List<UserInfo> GetUserInfo(){
        return UserInfos;
    }
}

