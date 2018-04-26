package cn.zju.creaturun.core;

/**
 * Created by lancha on 2016/11/29.
 */

public class UserManager {
    static String curUser=null;
    static public void SetCurrentUser(String user){
        curUser=user;
    }
    static public String GetCurrentUser(){
        return "User0";
    }
    static public void SaveCurUserInfo(){

    }
}
