package org.allmydata.tahoelafs;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
        updateSummaries();
    }

    public void updateSummaries() {
    	getPreferenceScreen().findPreference("node").setSummary(getPreferenceScreen().getSharedPreferences().getString("node", ""));
    	getPreferenceScreen().findPreference("rootcap").setSummary(getPreferenceScreen().getSharedPreferences().getString("rootcap", ""));
    }
}
