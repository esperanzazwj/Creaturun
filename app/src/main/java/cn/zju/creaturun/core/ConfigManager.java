package cn.zju.creaturun.core;

import android.graphics.Color;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by lancha on 2016/12/5.
 */

public class ConfigManager {

    private static int color= Color.argb(0,0,0,255);
    private static int width=5;
    private static int map_type=0;

    public static int GetColor(){
        return color;
    }

    public static int GetWidth(){
        return width;
    }

    public static int GetMapType(){ return map_type;}

    public static void SetConfig(int color, int width, int map_type){
        ConfigManager.color=color;
        ConfigManager.width=width;
        ConfigManager.map_type=map_type;

    }
    public static void LoadConfig(){
        File file=new File(Environment.getExternalStorageDirectory() + "/Creaturun/" + "config.ini");
        if (!file.exists()){
            color=Color.argb(255,0,0,255);
            width=5;
        }
        else{
            Scanner input=null;
            try {
                input=new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            color=input.nextInt();
            width=input.nextInt();
            map_type=input.nextInt();
        }
    }

    public static void SaveConfig(){
        File file=new File(Environment.getExternalStorageDirectory() + "/Creaturun/" + "config.ini");

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

        output.println(color);
        output.println(width);
        output.println(map_type);

        output.close();
    }
}
