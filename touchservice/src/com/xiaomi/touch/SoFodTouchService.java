/*
 * Copyright (C) 2026
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.touch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.V1_0.ITouchFeature;

public class SoFodTouchService extends Service {
    private static final String TAG = "XiaomiTouchService";
    private static final int TOUCH_FOD_ENABLE = 10;
    private static final int TOUCH_AOD_ENABLE = 11;
    private static final int TOUCH_FODICON_ENABLE = 16;

    @Override
    public void onCreate() {
        super.onCreate();
        enableSoFodModes();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        enableSoFodModes();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableSoFodModes() {
        try {
            final ITouchFeature touchFeature = ITouchFeature.getService();
            if (touchFeature == null) {
                Log.e(TAG, "TouchFeature service unavailable");
                return;
            }

            touchFeature.setModeValue(0, TOUCH_FOD_ENABLE, 1);
            touchFeature.setModeValue(0, TOUCH_AOD_ENABLE, 1);
            touchFeature.setModeValue(0, TOUCH_FODICON_ENABLE, 1);
        } catch (RemoteException e) {
            Log.e(TAG, "TouchFeature HAL call failed", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to enable SoFOD modes", e);
        }
    }
}
