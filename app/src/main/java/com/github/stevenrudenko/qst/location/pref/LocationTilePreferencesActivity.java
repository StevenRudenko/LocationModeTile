package com.github.stevenrudenko.qst.location.pref;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.github.stevenrudenko.qst.location.R;
import com.github.stevenrudenko.qst.location.utils.AppUtils;

/** Location tile preferences activity. */
public class LocationTilePreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private String keyActionType;
        private String keyLocationModes;
        private String keyVersion;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            keyActionType = getString(R.string.pref_action_type);
            keyLocationModes = getString(R.string.pref_location_modes);
            keyVersion = getString(R.string.pref_version);

            addPreferencesFromResource(R.xml.prefs);
            final String version = AppUtils.getVersion(getActivity());
            getPreferenceManager().findPreference(keyVersion).setSummary(version);
        }

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceManager()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, false);
        }

        @Override
        public void onStop() {
            getPreferenceManager()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            super.onStop();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            final Preference selectedPreference = getPreferenceManager().findPreference(key);
            if (selectedPreference == null) {
                return;
            }
            if (key.equals(keyActionType)) {
                final ListPreference preference = (ListPreference) selectedPreference;
                final int idx = preference.findIndexOfValue(preference.getValue());
                final String summary = getResources().getStringArray(R.array.action_type_summary)[idx];
                selectedPreference.setSummary(summary);
            } else if (key.equals(keyLocationModes)) {
                final MultiSelectListPreference preference = (MultiSelectListPreference) selectedPreference;;
                final CharSequence[] entries = preference.getSelectedEntries();
                final String summary = TextUtils.join(", ", entries);
                selectedPreference.setSummary(summary);
            }
        }
    }

}
