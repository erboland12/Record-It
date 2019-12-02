package com.example.recordratings.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recordratings.R;


public class MyPreferencesFragment extends PreferenceFragmentCompat{
    private Preference rating;
    private Preference feedback;
    private Preference darkMode;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private boolean isDark = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if(returnDark()){
//            SettingsActivity.getContextOfApplication().setTheme(R.style.darkTheme_Preferences);
        }
        setPreferencesFromResource(R.xml.preferences, rootKey);
        rating = findPreference("SayThanks");
        rating.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                startActivity(new Intent(SettingsActivity.getContextOfApplication(), PopupRatingActivity.class));
                return false;
            }
        });

        feedback = findPreference("feedback");
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                startActivity(new Intent(SettingsActivity.getContextOfApplication(), PopupFeedbackActivity.class));
                return false;
            }
        });

        darkMode = (SwitchPreference) findPreference("darkMode");

        editor = shared.edit();
        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isOn = (boolean) newValue;
                if(isOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                    isDark = true;
                    editor.putBoolean("darkMode",true);
                    editor.apply();
                    editor.commit();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
                    isDark = false;
                    editor.putBoolean("darkMode",false);
                    editor.apply();
                    editor.commit();
                }
                getActivity().finish();
                startActivity(new Intent(SettingsActivity.getContextOfApplication(), SettingsActivity.class));
                return true;
            }
        });


    }


    private boolean returnDark(){
        shared = SettingsActivity.getContextOfApplication().getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }
}
