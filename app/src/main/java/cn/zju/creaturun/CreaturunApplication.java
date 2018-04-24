package cn.zju.creaturun;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class CreaturunApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
