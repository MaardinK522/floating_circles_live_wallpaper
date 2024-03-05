package com.mkproductions.floatingcircles.fragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.mkproductions.floatingcircles.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        Preference circlesCount = getPreferenceScreen().findPreference("ball_count_pref");
        if (circlesCount != null)
            circlesCount.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*"))
                    return true;
                Toast.makeText(this.getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                return false;
            });
    }
}