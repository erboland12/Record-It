package com.example.recordratings.settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recordratings.R;

public class PopupFeedbackActivity extends AppCompatActivity {
    private EditText getMessage;
    private Button submitBtn;

    private SharedPreferences shared;
    private SmsManager smgr;

    private String PHONE = "8604223810";
    private EditText message;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            PopupFeedbackActivity.this.setTheme(R.style.darkThemeNoBar);
        }else{
            PopupFeedbackActivity.this.setTheme(R.style.AppTheme_CustomWindowTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_feedback);

        message = findViewById(R.id.feedback_message);
        layout = findViewById(R.id.feedback_layout);
        if(returnDark()){
            message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else{
            layout.setBackground(getResources().getDrawable(R.drawable.border));
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getMessage = findViewById(R.id.feedback_message);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int)(height * 0.6));

        shared = getSharedPreferences("Feedback", MODE_PRIVATE);
        if(shared != null){
            getMessage.setText(shared.getString("Response", ""));
        }

        smgr = SmsManager.getDefault();
        submitBtn = findViewById(R.id.rating_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String finalMessage = getMessage.getText().toString();
                String[] array = {Manifest.permission.SEND_SMS};
                if (ContextCompat.checkSelfPermission(SettingsActivity.getContextOfApplication(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(array, 1);
                    Toast.makeText(PopupFeedbackActivity.this, "Permission Denied.  Turn on permissions and try again.", Toast.LENGTH_SHORT).show();
                } else{
                    smgr.sendTextMessage(PHONE, null, finalMessage, null, null);
                    getMessage.setText("");
                    finish();
                    Toast.makeText(PopupFeedbackActivity.this, "Feedback Sent", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed(){
        SharedPreferences.Editor editor = shared.edit();
        if(!getMessage.getText().toString().isEmpty()){
            editor.putString("Response", getMessage.getText().toString());
        }else{
            editor.putString("Response", "");
        }
        editor.apply();
        editor.commit();
        finish();
    }

    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

}
