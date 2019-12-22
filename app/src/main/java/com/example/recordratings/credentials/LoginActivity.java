package com.example.recordratings.credentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {
    private TextView mSignUp, mForgotPw, mCheckEmailHeader;
    private EditText mLogin, mPass, mCheckEmail;
    private Button btnApply, btnCancel;

    private android.widget.Button btn;
    private LinearLayout mLoginLayout;

    private SharedPreferences shared;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Log In");
        if (returnDark()) {
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_login);

        //link to resources
        mSignUp = findViewById(R.id.sign_up_link);
        mForgotPw = findViewById(R.id.forgot_password);
        mLogin = findViewById(R.id.loginText);
        mPass = findViewById(R.id.passText);
        mLoginLayout = findViewById(R.id.loginLayout);
        btn = findViewById(R.id.logged);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (verifyCredentials(mLogin.getText().toString(), mPass.getText().toString())) {
                    String email = mLogin.getText().toString();
                    String password = mPass.getText().toString();

                    if(mAuth.getCurrentUser() != null){
                        mAuth.signOut();
                    }

                    mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(v.getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(v.getContext(), MainActivity.class));
                        }
                    });

                    mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignInActivity.class));
            }
        });

        mForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForValidUser();
            }
        });

        if (returnDark()) {
            mLoginLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            mSignUp.setTextColor(getResources().getColor(R.color.signUpLinkDarkMode));
            mLogin.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            mPass.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }

    }

    private boolean returnDark() {
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    private boolean verifyCredentials(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "One of the Fields is Empty.  Try Again.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.contains("@") || (!email.contains("."))) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void checkForValidUser(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.forgot_pw_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        mCheckEmailHeader = dialogView.findViewById(R.id.check_email_header);
        mCheckEmail = dialogView.findViewById(R.id.check_for_valid_email);

        btnApply = dialogView.findViewById(R.id.edit_apply);
        btnCancel = dialogView.findViewById(R.id.edit_cancel);

        if(returnDark()){
            mCheckEmail.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(mCheckEmail.getText().toString().isEmpty()){
                    Toast.makeText(v.getContext(), "Email Address Not Found.", Toast.LENGTH_SHORT).show();
                }else{
                    db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            boolean notFound = true;
                            for(DocumentSnapshot doc: queryDocumentSnapshots){
                                if(mCheckEmail.getText().toString().equals(doc.getString("mEmail"))){
                                    notFound = false;
                                    mAuth.sendPasswordResetEmail(mCheckEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(v.getContext(), "Email Sent.", Toast.LENGTH_SHORT).show();
                                                alertDialog.cancel();
                                            }else{
                                                Toast.makeText(v.getContext(), "Something Went Wrong.  Please Try Again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            if(notFound){
                                Toast.makeText(v.getContext(), "Email Address Not Found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
