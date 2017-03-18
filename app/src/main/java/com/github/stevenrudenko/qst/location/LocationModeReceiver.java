package com.github.stevenrudenko.qst.location;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.service.quicksettings.TileService;

/** Location mode change broadcast listener. */
public class LocationModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (LocationManager.MODE_CHANGED_ACTION.equals(action)) {
            TileService.requestListeningState(context,
                    new ComponentName(context, LocationTileService.class));
        }
    }
}
