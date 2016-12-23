package com.adt.lenovo.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.adt.lenovo.coolweather.activity.WeatherActivity;
import com.adt.lenovo.coolweather.receiver.AutoUpdateReceiver;
import com.adt.lenovo.coolweather.util.HttpUtil;
import com.adt.lenovo.coolweather.util.Utility;

/**
 * Created by Lenovo on 2016/12/24 024.
 */

public class AutoUpdateService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                String code = sharedPreferences.getString("cityid", "");
                if (!TextUtils.isEmpty(code)) {
                    String url = "http://www.weather.com.cn/data/cityinfo/" + code + ".html";
                    HttpUtil.SendHttpRequest(url, new HttpUtil.HttpCallbackListener() {
                        @Override
                        public void OnFinish(String response) {
                            Utility.HandleWeatherResponse(response);

                            WeatherActivity.RefreshUI();
                        }

                        @Override
                        public void OnError(Exception ee) {

                        }
                    });
                }

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(AutoUpdateService.this, AutoUpdateReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AutoUpdateService.this, 0, intent1, 0);//8 * 60 * 60 * 1000
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, pendingIntent);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
