package com.example.recordratings.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recordratings.R;

public class PopupRatingActivity extends AppCompatActivity {

    private Button playStoreBtn;
    private SharedPreferences shared;

    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            PopupRatingActivity.this.setTheme(R.style.darkThemeNoBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_rating);

        layout = findViewById(R.id.rating_layout);
        if(!returnDark()){
            layout.setBackground(getResources().getDrawable(R.drawable.border));
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int)(height * 0.7));

        playStoreBtn = findViewById(R.id.rating_btn);
        playStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://play.google.com/store/apps";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }
}
