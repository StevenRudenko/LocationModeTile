package com.github.stevenrudenko.qst.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Location tile service.
 */
public class LocationTileService extends TileService {
    /**
     * Log tag.
     */
    private static final String TAG = LocationTileService.class.getSimpleName();

    private String keyActionType;
    private String actionTypeDialog;

    private String keyLocationModes;
    private SharedPreferences prefs;

    public static void start(Context context) {
        TileService.requestListeningState(context,
                new ComponentName(context, LocationTileService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        keyActionType = getString(R.string.pref_action_type);
        actionTypeDialog = getString(R.string.pref_action_type_dialog);
        keyLocationModes = getString(R.string.pref_location_modes);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

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
        final String type = prefs.getString(keyActionType, getString(R.string.default_action_type));
        if (type.equals(actionTypeDialog)) {
            showDialog();
        } else {
            toggle();
        }
    }

    private void toggle() {
        final int mode = getLocationMode();
        final List<Integer> modes = getLocationModes();
        final int size = modes.size();
        final String[] values = getResources().getStringArray(R.array.location_modes_values);
        final List<String> selection = new ArrayList<>(size);
        modes.forEach(idx -> selection.add(values[idx]));
        final String[] items = new String[size];
        selection.toArray(items);

        final int idx = Arrays.binarySearch(items, Integer.toString(mode));
        if (idx < 0) {
            final int next = Integer.parseInt(items[0]);
            setLocationMode(next);
            updateTile(next);
        } else {
            final int next = Integer.parseInt(items[(idx + 1) % items.length]);
            setLocationMode(next);
            updateTile(next);
        }
    }

    private void showDialog() {
        // we can't show dialog while locked
        if (isLocked()) {
            Toast.makeText(this, R.string.locked, Toast.LENGTH_SHORT).show();
            return;
        }
        final int mode = getLocationMode();
        final List<Integer> modes = getLocationModes();
        final int size = modes.size();
        final CharSequence[] entries = getResources().getTextArray(R.array.location_modes);
        final List<CharSequence> selection = new ArrayList<>(size);
        modes.forEach(idx -> selection.add(entries[idx]));
        final CharSequence[] items = new CharSequence[size];
        selection.toArray(items);

        final Dialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.location_title)
                .setSingleChoiceItems(items, mode, (d, which) -> {
                    setLocationMode(which);
                    updateTile(which);
                    d.dismiss();
                })
                .create();
        showDialog(dialog);
    }

    private List<Integer> getLocationModes() {
        final String[] defaultValue = getResources().getStringArray(R.array.location_modes_values);
        final Set<String> defaultEntries = new HashSet<>(defaultValue.length);
        Collections.addAll(defaultEntries, defaultValue);
        final Set<String> values = prefs.getStringSet(keyLocationModes, defaultEntries);
        final int size = values.size();
        final List<Integer> indexes = new ArrayList<>(size);
        values.forEach(value -> indexes.add(Arrays.binarySearch(defaultValue, value)));
        Collections.sort(indexes);
        return indexes;
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

        final CharSequence title = getText(label);
        tile.setIcon(Icon.createWithResource(this, icon));
        tile.setLabel(title);
        tile.setContentDescription(title);
        tile.setState(mode == Settings.Secure.LOCATION_MODE_OFF
                ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        tile.updateTile();

        Toast.makeText(
                getApplicationContext(),
                getString(R.string.location_mode_changed_message, title),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void setLocationMode(int mode) {
        try {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
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
