package com.github.stevenrudenko.qst.location.pref;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs);
            final String version = AppUtils.getVersion(getActivity());
            final String keyVersion = getString(R.string.pref_version);
            getPreferenceManager().findPreference(keyVersion).setSummary(version);
        }
    }

}
