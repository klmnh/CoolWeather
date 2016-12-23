package com.adt.lenovo.coolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Lenovo on 2016/12/23 023.
 */

public class MyApplication extends Application {

    private static Context globeContent = null;

    public static Context GetGlobeContent()
    {
        return globeContent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        globeContent = getApplicationContext();
    }
}
