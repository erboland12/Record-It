package com.example.recordratings.credentials;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private String photoToString = "";

    private LinearLayout mSignLayout;
    private android.widget.EditText mEmail, mDisplay, mPassword, mConfirmPassword;
    private android.widget.Button mSubmitBtn;
    private android.widget.Button mSelectPhotoBtn;
    private CircleImageView mProfilePreview;

    private SharedPreferences shared;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private boolean dnInUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            setTheme(R.style.darkTheme);
        }
        super.onCreate(savedInstanceState);
        setTitle("Register Account");
        setContentView(R.layout.activity_sign_in);

        mSignLayout = findViewById(R.id.signUpLayout);
        mEmail = findViewById(R.id.emailText);
        mDisplay = findViewById(R.id.displayNameText);
        mPassword = findViewById(R.id.passText);
        mConfirmPassword = findViewById(R.id.confirmPwTest);
        mSubmitBtn = findViewById(R.id.sign_up_btn);
        mSelectPhotoBtn = findViewById(R.id.sign_up_select_img);
        mProfilePreview = findViewById(R.id.sign_up_img_view);

        //Sets up Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mSelectPhotoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String email = mEmail.getText().toString();
                final String displayName = mDisplay.getText().toString();
                String password = mPassword.getText().toString();
                String confirm = mConfirmPassword.getText().toString();

                db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                            if(displayName.equals(doc.getString("mDisplayName"))){
                                dnInUse = true;
                                Toast.makeText(v.getContext(), "Display Name has Already been Taken.  Choose Another One.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                });


                if(verifyCredentials(email, displayName, password, confirm)){
                    if(mAuth != null){
                        mAuth.signOut();
                    }
                    if(dnInUse){
                        dnInUse = false;
                    }else{
                        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(final AuthResult authResult) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName).build();

                                user.updateProfile(profileUpdate);

                                if(photoToString.isEmpty()) {
                                    photoToString = "https://firebasestorage.googleapis.com/v0/b/record-ratings.appspot.com/o/content%3A%2Fcom.android.providers.media.documents%2Fdocument%2Fimage%253A906?alt=media&token=db7295d0-c0c1-4c33-b512-d0a43f7156e4";
                                }
                                final User newUser = new User(authResult.getUser().getUid(), email, displayName, photoToString, "Empty");
                                Toast.makeText(getApplicationContext(), "Registering...", Toast.LENGTH_SHORT).show();
                                db.collection("users").add(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Registration Successful.", Toast.LENGTH_SHORT).show();
                                                startActivity(new android.content.Intent(v.getContext(), MainActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Something Went Wrong.  Please Try Again.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                try{
                                    throw e;
                                }
                                catch(Exception inUse){
                                    Toast.makeText(getApplicationContext(), inUse.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        if(returnDark()){
            mSignLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            mEmail.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            mDisplay.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            mPassword.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            mConfirmPassword.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));

        }
    }

    private boolean returnDark() {
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    private boolean verifyCredentials(String email, String displayName, String password, String confirmPassword) {
        if (email.isEmpty() || displayName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "One of the Fields is Empty.  Try Again.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.contains("@") || (!email.contains("."))) {
            Toast.makeText(this, "Invalid Email Format.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!confirmPassword.equals(password)){
            Toast.makeText(this, "Passwords do not Match.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(password.length() < 8){
            Toast.makeText(this, "Passwords Must be 8 Digits Long.",
                           Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final StorageReference ref = mStorageRef.child(data.getDataString());
                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                                Picasso.get().load(uri).into(mProfilePreview);
                                photoToString = downloadUri.toString();
                            }
                        });
                    }
                });
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
