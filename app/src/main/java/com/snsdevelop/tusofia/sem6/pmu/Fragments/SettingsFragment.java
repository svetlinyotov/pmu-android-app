package com.snsdevelop.tusofia.sem6.pmu.Fragments;


import android.os.Bundle;

import com.snsdevelop.tusofia.sem6.pmu.Helpers.BackgroundMusic;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_settings, rootKey);

        SwitchPreference settingIsMuted = (SwitchPreference) findPreference(StoredData.SETTINGS_IS_BG_MUSIC_PLAYING);
        if (getContext() != null) {
            BackgroundMusic.doBindService(getContext());
            BackgroundMusic.startService(getContext());
            if (StoredData.getBoolean(getContext(), StoredData.SETTINGS_IS_BG_MUSIC_PLAYING)) {
                settingIsMuted.setIcon(getContext().getDrawable(R.drawable.ic_volume_off_999_24dp));
            } else {
                settingIsMuted.setIcon(getContext().getDrawable(R.drawable.ic_volume_up_999_24dp));
            }
            settingIsMuted.setOnPreferenceChangeListener((preference, o) -> {
                boolean isPlaying = (boolean) o;


                if (isPlaying) {
                    BackgroundMusic.resume();
                    settingIsMuted.setIcon(getContext().getDrawable(R.drawable.ic_volume_up_999_24dp));
                } else {
                    BackgroundMusic.pause();
                    settingIsMuted.setIcon(getContext().getDrawable(R.drawable.ic_volume_off_999_24dp));
                }

                return true;
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
