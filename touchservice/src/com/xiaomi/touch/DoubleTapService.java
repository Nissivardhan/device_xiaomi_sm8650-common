/*
 * Copyright (C) 2026
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.touch;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.V1_0.ITouchFeature;

public class DoubleTapService extends Service {
    private static final String TAG = "XiaomiTouchService";
    private static final int DOUBLE_TAP_TO_WAKE_MODE = 14;

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updateTapToWakeStatus();
        }
    };

    private ITouchFeature mTouchFeature;

    @Override
    public void onCreate() {
        super.onCreate();
        getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.DOUBLE_TAP_TO_WAKE), true, mObserver);
        updateTapToWakeStatus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateTapToWakeStatus();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateTapToWakeStatus() {
        try {
            if (mTouchFeature == null) {
                mTouchFeature = ITouchFeature.getService();
            }

            if (mTouchFeature == null) {
                Log.e(TAG, "TouchFeature service unavailable");
                return;
            }

            final boolean enabled = Settings.Secure.getInt(
                    getContentResolver(), Settings.Secure.DOUBLE_TAP_TO_WAKE, 0) == 1;
            mTouchFeature.setModeValue(0, DOUBLE_TAP_TO_WAKE_MODE, enabled ? 1 : 0);
        } catch (RemoteException e) {
            Log.e(TAG, "TouchFeature HAL call failed", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to update DT2W", e);
        }
    }
}
