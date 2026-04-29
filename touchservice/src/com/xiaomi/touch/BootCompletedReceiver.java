/*
 * Copyright (C) 2026
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.touch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        context.startServiceAsUser(new Intent(context, DoubleTapService.class), UserHandle.CURRENT);
        context.startServiceAsUser(new Intent(context, SoFodTouchService.class), UserHandle.CURRENT);
    }
}
