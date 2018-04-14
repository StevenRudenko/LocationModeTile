package com.github.stevenrudenko.qst.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/** Location mode change broadcast listener. */
public class LocationModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case LocationManager.MODE_CHANGED_ACTION:
                LocationTileService.start(context);
                break;
            default:
        }
    }
}
