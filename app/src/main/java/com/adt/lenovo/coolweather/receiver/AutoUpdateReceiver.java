package com.adt.lenovo.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adt.lenovo.coolweather.service.AutoUpdateService;

/**
 * Created by Lenovo on 2016/12/24 024.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
