package com.example.recordratings.records;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.MainActivity;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.R;

public class RecordsPage extends AppCompatActivity implements RecordPageFragment.OnFragmentInteractionListener {

    //Fragment
    private Fragment myFragment;
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(returnDark()){
            setTheme(R.style.darkThemeNoBar);
            RecordPageFragment.isDark = true;
        }
        setTitle("");
        setContentView(R.layout.activity_records_page);

        //Initialize and launch record page fragment.
        myFragment = new RecordPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.records_page_background, myFragment).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
        finish();
    }

    @Override
    public void onStart(){
        super.onStart();


    }

    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
