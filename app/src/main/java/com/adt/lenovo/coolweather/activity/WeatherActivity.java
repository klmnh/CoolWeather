package com.adt.lenovo.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adt.lenovo.coolweather.R;
import com.adt.lenovo.coolweather.service.AutoUpdateService;
import com.adt.lenovo.coolweather.util.HttpUtil;
import com.adt.lenovo.coolweather.util.Utility;

/**
 * Created by Lenovo on 2016/12/23 023.
 */

public class WeatherActivity extends AppCompatActivity {

    static Button btnRefresh = null;
    static Handler handler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        final TextView txtWeatherCity = (TextView) findViewById(R.id.txtWeatherCity);
        final TextView txtWeatherPTime = (TextView) findViewById(R.id.txtWeatherTime);
        final TextView txtWeatherInfo = (TextView) findViewById(R.id.txtDesp);
        final TextView txtDate = (TextView) findViewById(R.id.txtDate);
        final TextView txtTemp1 = (TextView) findViewById(R.id.txtTemp1);
        final TextView txtTemp2 = (TextView) findViewById(R.id.txtTemp2);

        String code = getIntent().getStringExtra("county_url");
        if (!TextUtils.isEmpty(code)) {
            txtWeatherPTime.setText("同步中...");
            String url = "http://www.weather.com.cn/data/cityinfo/" + code + ".html";
            HttpUtil.SendHttpRequest(url, new HttpUtil.HttpCallbackListener() {
                @Override
                public void OnFinish(String response) {
                    Utility.HandleWeatherResponse(response);
                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtWeatherCity.setText(preferences.getString("city_name", ""));
                            txtWeatherPTime.setText(preferences.getString("ptime", "") + "发布");
                            txtWeatherInfo.setText(preferences.getString("weather", ""));
                            txtTemp1.setText(preferences.getString("temp1", ""));
                            txtTemp2.setText(preferences.getString("temp2", ""));
                            txtDate.setText(preferences.getString("currtime", ""));
                        }
                    });
                }

                @Override
                public void OnError(Exception ee) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtWeatherPTime.setText("同步失败");
                        }
                    });

                }
            });
        } else {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            txtWeatherCity.setText(preferences.getString("city_name", ""));
            txtWeatherPTime.setText(preferences.getString("ptime", "") + "发布");
            txtWeatherInfo.setText(preferences.getString("weather", ""));
            txtTemp1.setText(preferences.getString("temp1", ""));
            txtTemp2.setText(preferences.getString("temp2", ""));
            txtDate.setText(preferences.getString("currtime", ""));
        }

        Button btnSwitch = (Button) findViewById(R.id.btnSwitch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String code = preferences.getString("cityid", "");
                if (!TextUtils.isEmpty(code)) {
                    txtWeatherPTime.setText("同步中...");
                    String url = "http://www.weather.com.cn/data/cityinfo/" + code + ".html";
                    HttpUtil.SendHttpRequest(url, new HttpUtil.HttpCallbackListener() {
                        @Override
                        public void OnFinish(String response) {
                            Utility.HandleWeatherResponse(response);
                            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtWeatherCity.setText(preferences.getString("city_name", ""));
                                    txtWeatherPTime.setText(preferences.getString("ptime", "") + "发布");
                                    txtWeatherInfo.setText(preferences.getString("weather", ""));
                                    txtTemp1.setText(preferences.getString("temp1", ""));
                                    txtTemp2.setText(preferences.getString("temp2", ""));
                                    txtDate.setText(preferences.getString("currtime", ""));
                                }
                            });
                        }

                        @Override
                        public void OnError(Exception ee) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtWeatherPTime.setText("刷新失败");
                                }
                            });

                        }
                    });
                }
            }
        });

        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 2:
                        if (txtWeatherCity != null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            txtWeatherCity.setText(preferences.getString("city_name", ""));
                            txtWeatherPTime.setText(preferences.getString("ptime", "") + "发布");
                            txtWeatherInfo.setText(preferences.getString("weather", ""));
                            txtTemp1.setText(preferences.getString("temp1", ""));
                            txtTemp2.setText(preferences.getString("temp2", ""));
                            txtDate.setText(preferences.getString("currtime", ""));
                        }
                        break;
                }
            }
        };

    }

    public static void RefreshUI() {
        if (handler != null) {
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        }
    }
}
