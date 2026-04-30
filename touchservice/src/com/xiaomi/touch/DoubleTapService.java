/*
 * Copyright (C) 2026
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.touch;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.ITouchFeature;

public class DoubleTapService extends Service {
    private static final String TAG = "XiaomiTouchService";
    private static final int DOUBLE_TAP_TO_WAKE_MODE = 14;

    private ITouchFeature mTouchFeature;

    @Override
    public void onCreate() {
        super.onCreate();
        initTouchFeature();
        registerObserver();
    }

    private void initTouchFeature() {
        try {
            IBinder binder = Binder.allowBlocking(
                ServiceManager.waitForDeclaredService(
                    ITouchFeature.DESCRIPTOR + "/default"));
            mTouchFeature = ITouchFeature.Stub.asInterface(binder);
        } catch (Exception e) {
            Log.e(TAG, "DT2W bind failed", e);
        }
    }

    private void registerObserver() {
        ContentResolver cr = getContentResolver();
        cr.registerContentObserver(
            Settings.Secure.getUriFor(Settings.Secure.DOUBLE_TAP_TO_WAKE),
            true,
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    updateMode();
                }
            });
        updateMode();
    }

    private void updateMode() {
        if (mTouchFeature == null) {
            return;
        }
        try {
            boolean enabled = Settings.Secure.getInt(
                getContentResolver(), Settings.Secure.DOUBLE_TAP_TO_WAKE, 0) == 1;
            mTouchFeature.setTouchMode(0, DOUBLE_TAP_TO_WAKE_MODE, enabled ? 1 : 0);
        } catch (Exception e) {
            Log.e(TAG, "DT2W setTouchMode failed", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateMode();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
