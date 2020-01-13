package com.example.recordratings.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import static android.util.Patterns.PHONE;


public class MyPreferencesFragment extends PreferenceFragmentCompat{
    private Preference rating, feedback, darkMode, disableCensorship, reportUser;

    //Shared Preference Declaration for dark mode.
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    //Front-end variables
    private EditText feedbackEdit, reportEdit;
    private Button ratingBtn, feedbackBtn, reportBtn;
    private Spinner reportOptions;

    //Shared Preferences for Censorship
    private SharedPreferences censorSP;
    private SharedPreferences.Editor censorEditor;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    //For sending feedback message
    private String PHONE = "8604223810";
    private SmsManager smgr;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        //Initializes database variables
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Links rating preference and creates on preference listener for opening dialog
        rating = findPreference("SayThanks");
        rating.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.rating_dialog_box, null);
                dialogBuilder.setView(dialogView);

                ratingBtn = dialogView.findViewById(R.id.rating_btn);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                //Button listener for rating button
                ratingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Directs user to google play home screen
                        String url = "https://play.google.com/store/apps";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        alertDialog.cancel();
                        startActivity(intent);
                    }
                });
                return false;
            }
        });

        //Feedback preference click listener
        feedback = findPreference("feedback");
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.feedback_dialog_box, null);
                dialogBuilder.setView(dialogView);

                feedbackEdit = dialogView.findViewById(R.id.feedback_message);
                feedbackBtn = dialogView.findViewById(R.id.feedback_btn);

                if(returnDark()){
                    feedbackEdit.setBackground(getResources().getDrawable(R.drawable.dark_border));
                }


                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                smgr = SmsManager.getDefault();
                feedbackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String finalMessage = feedbackEdit.getText().toString();
                        String[] array = {Manifest.permission.SEND_SMS};
                        if (ContextCompat.checkSelfPermission(SettingsActivity.getContextOfApplication(), Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(array, 1);
                            Toast.makeText(getContext(), "Permission Denied.  Turn on permissions and try again.", Toast.LENGTH_LONG).show();
                        } else{
                            if(feedbackEdit.getText().toString().isEmpty()){
                                Toast.makeText(getActivity(), "Empty Responses Cannot be Sent.", Toast.LENGTH_SHORT).show();
                            }else{
                                smgr.sendTextMessage(PHONE, null, finalMessage, null, null);
                                feedbackEdit.setText("");
                                alertDialog.cancel();
                                Toast.makeText(getActivity(), "Feedback Sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                return false;
            }
        });

        shared = getActivity().getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        censorSP = getActivity().getSharedPreferences("censorPrefs", Context.MODE_PRIVATE);

        darkMode = findPreference("darkMode");
        disableCensorship = findPreference("disableCensor");

        editor = shared.edit();
        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isOn = (boolean) newValue;
                if(isOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("darkMode",true);
                    editor.apply();
                    editor.commit();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("darkMode",false);
                    editor.apply();
                    editor.commit();
                }
                getActivity().finish();
                startActivity(new Intent(SettingsActivity.getContextOfApplication(), SettingsActivity.class));
                return true;
            }
        });

        censorEditor = censorSP.edit();
        disableCensorship.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isOn = (boolean) newValue;
                if(isOn){
                    censorEditor.putBoolean("censorOff", true);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Censorship Disabled", Toast.LENGTH_SHORT).show();
                }else{
                    censorEditor.putBoolean("censorOff", false);
                    Toast.makeText(SettingsActivity.contextOfApplication, "Censorship Enabled", Toast.LENGTH_SHORT).show();
                }
                censorEditor.apply();
                censorEditor.commit();

                return true;
            }
        });

        smgr = SmsManager.getDefault();
        reportUser = findPreference("reportUser");
        reportUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.report_user_dialog_box, null);
                dialogBuilder.setView(dialogView);

                reportEdit = dialogView.findViewById(R.id.report_user_name);
                reportOptions = dialogView.findViewById(R.id.report_reason);
                reportBtn = dialogView.findViewById(R.id.report_btn);

                if(returnDark()){
                    reportEdit.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
                }
                String[] items = new String[]{"Inappropriate Profile", "Inappropriate Comments", "Offensive Language",
                                              "Harassing Another User", "Advertising Unauthorized Content"};

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
                reportOptions.setAdapter(adapter);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                reportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                boolean userFound = false;
                                String dn = "";
                                String uid = "";
                                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                    if(doc.getString("mDisplayName").equals(reportEdit.getText().toString())){
                                        userFound = true;
                                        dn = doc.getString("mDisplayName");
                                        uid = doc.getString("mId");
                                    }
                                }
                                if(userFound){
                                    String finalMessage = "REPORTED USER: " +
                                                "           Username - " + dn  +
                                                "           UID - " + uid +
                                                "           REPORTED FOR: " + reportOptions.getSelectedItem().toString();
                                    String[] array = {Manifest.permission.SEND_SMS};
                                    if (ContextCompat.checkSelfPermission(SettingsActivity.getContextOfApplication(), Manifest.permission.SEND_SMS)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(array, 1);
                                        Toast.makeText(getContext(), "Permission Denied.  Turn on permissions and try again.", Toast.LENGTH_LONG).show();
                                    } else{
                                        smgr.sendTextMessage(PHONE, null, finalMessage, null, null);
                                        reportEdit.setText("");
                                        alertDialog.cancel();
                                        Toast.makeText(getActivity(), "Report Sent.  Our Team Will Look into this User as Soon as Possible.", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getActivity(), "User Not Found.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


                return false;
            }
        });


    }


    private boolean returnDark(){
        shared = SettingsActivity.getContextOfApplication().getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }
}
