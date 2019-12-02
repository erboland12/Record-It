package com.example.recordratings.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;

public class SettingsActivity extends AppCompatActivity {

    public static Context contextOfApplication;
    private static ContentResolver contentResolver;
    private static Window window;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment fragment;

    private MyPreferencesFragment frag;

    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contextOfApplication = this;
        if(returnDark()){
            contextOfApplication.setTheme(R.style.darkTheme_Preferences);
        }
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        contentResolver = getContentResolver();
        window = getWindow();

        frag = new MyPreferencesFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_layout, frag)
                .commit();
    }


    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

}
