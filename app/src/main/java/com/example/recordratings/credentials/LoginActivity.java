package com.example.recordratings.credentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView mSignUp;
    private EditText mLogin;
    private EditText mPass;

    private android.widget.Button btn;
    private LinearLayout mLoginLayout;

    private SharedPreferences shared;
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
        mLogin = findViewById(R.id.loginText);
        mPass = findViewById(R.id.passText);
        mLoginLayout = findViewById(R.id.loginLayout);
        btn = findViewById(R.id.logged);
        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (verifyCredentials(mLogin.getText().toString(), mPass.getText().toString())) {
                    String email = mLogin.getText().toString();
                    String password = mPass.getText().toString();

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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
