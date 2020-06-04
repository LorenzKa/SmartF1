package net.htlgrieskirchen.smartf1.Preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import net.htlgrieskirchen.smartf1.R;

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,
                rootKey);
    }

}