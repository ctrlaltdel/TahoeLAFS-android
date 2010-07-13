package org.allmydata.tahoelafs;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Prefs extends PreferenceActivity {

    public static final String KEY_NODE = "node";
    public static final String KEY_ROOTCAP = "rootcap";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        updateSummaries();
    }

    public void updateSummaries() {
        //TODO: Get default summary from string
    	getPreferenceScreen().findPreference(KEY_NODE).setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_NODE, ""));
    	getPreferenceScreen().findPreference(KEY_ROOTCAP).setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_ROOTCAP, ""));
    }

}
