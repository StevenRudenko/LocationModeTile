package com.github.stevenrudenko.qst.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

/** Location tile service. */
public class LocationTileService extends TileService {

    /** Log tag. */
    @SuppressWarnings("unused")
    private static final String TAG = LocationTileService.class.getSimpleName();

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        final int mode = getLocationMode();
        updateTile(mode);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        final int mode = getLocationMode();
        updateTile(mode);
    }

    @Override
    public void onClick() {
        super.onClick();
        // we can't show dialog while locked
        if (isLocked()) {
            Toast.makeText(this, R.string.locked, Toast.LENGTH_SHORT).show();
            return;
        }
        final int mode = getLocationMode();
        final Dialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.location_title)
                .setSingleChoiceItems(R.array.location_modes, mode,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setLocationMode(which);
                                updateTile(which);
                                dialog.dismiss();
                            }
                        })
                .create();
        showDialog(dialog);
    }

    private void updateTile(int mode) {
        final Tile tile = getQsTile();
        final int icon;
        final int label;
        if (mode == Settings.Secure.LOCATION_MODE_OFF) {
            icon = R.drawable.ic_location_off;
            label = R.string.location_disable;
        } else {
            icon = R.drawable.ic_location_on;
            switch (mode) {
                case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
                    label = R.string.location_device_only;
                    break;
                case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
                    label = R.string.location_battery_saving;
                    break;
                case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
                    label = R.string.location_high_accuracy;
                    break;
                default:
                    label = R.string.location_unknown;
            }
        }

        tile.setIcon(Icon.createWithResource(this, icon));
        tile.setLabel(getText(label));
        tile.setContentDescription(getText(label));
        tile.setState(mode == Settings.Secure.LOCATION_MODE_OFF
                ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    private void setLocationMode(int mode) {
        try {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCATION_MODE, mode);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.workaround, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Fail to set location mode", e);
        }
    }

    private int getLocationMode() {
        try {
            return Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Fail to read location mode", e);
        }
        return Settings.Secure.LOCATION_MODE_OFF;
    }

}
