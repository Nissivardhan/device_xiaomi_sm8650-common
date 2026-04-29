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
import android.provider.Settings;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.ITouchFeature;

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
                final String fqName = ITouchFeature.DESCRIPTOR + "/default";
                final IBinder binder = android.os.Binder.allowBlocking(
                        android.os.ServiceManager.waitForDeclaredService(fqName));
                mTouchFeature = ITouchFeature.Stub.asInterface(binder);
            }

            if (mTouchFeature == null) {
                Log.e(TAG, "TouchFeature service unavailable");
                return;
            }

            final boolean enabled = Settings.Secure.getInt(
                    getContentResolver(), Settings.Secure.DOUBLE_TAP_TO_WAKE, 0) == 1;
            mTouchFeature.setTouchMode(0, DOUBLE_TAP_TO_WAKE_MODE, enabled ? 1 : 0);
        } catch (Exception e) {
            Log.e(TAG, "Failed to update DT2W", e);
        }
    }
}
