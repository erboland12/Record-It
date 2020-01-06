package com.example.recordratings.records;

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
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.MainActivity;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Random;

import io.grpc.Context;

public class AddRecord extends AppCompatActivity {
    //Image request constant and movePage call
    private int PICK_IMAGE_REQUEST = 1;
    private MovePage m = new MovePage();

    //Front end variables
    private EditText albumName, artistName, description;
    private TextView descCharCount;
    private RatingBar rating;
    private Spinner genre;
    private String photoToString;
    private ImageView albumCover;
    private Button browseGalleryBtn;

    //Shared preferences, storage, auth, and db declarations
    private SharedPreferences shared;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Random nums for creating unique record id
    Random rand1;
    Random rand2;
    Random rand3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Determines styling if night mode preference is enabled
        if(returnDark()){
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_add_record);

        //Input edit text values into global variables
        albumName = findViewById(R.id.editText);
        artistName = findViewById(R.id.editText2);
        rating = findViewById(R.id.add_record_rating);
        genre = findViewById(R.id.genre_spinner);
        description = findViewById(R.id.editText3);
        descCharCount = findViewById(R.id.desc_char_count);
        albumCover = findViewById(R.id.add_record_image_view);

        //Additional styling if night mode is enabled
        if(returnDark()){
            albumName.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            artistName.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }

        //Initializes random numbers
        rand1 = new Random();
        rand2 = new Random();
        rand3 = new Random();

        //Sets up Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Adds options to Spinner
        String[] items = new String[]{"Pop", "Rock", "Jazz", "Blues", "Rap", "Country", "Folk",
                                      "Metal", "Progressive", "Psychedelic", "Punk", "Alternative",
                                      "Indie", "Classical", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        genre.setAdapter(adapter);

        //Allows user to search gallery for photos
        browseGalleryBtn = findViewById(R.id.browse_btn);
        browseGalleryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                chooseImage();
            }

        });


        //Button and listener creation
        final Button mAddBtn = findViewById(R.id.addRecordBtn);

        //Text watcher for album name edit text
        albumName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 50){
                    Toast.makeText(getApplicationContext(), "Max Character Count Reached for Album Name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Text watcher for artist name edit text
        artistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 50){
                    Toast.makeText(getApplicationContext(), "Max Character Count Reached for Artist Name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Text watcher for description edit text
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    descCharCount.setText(Integer.toString(s.length()) + "/1000");
                    descCharCount.setVisibility(View.VISIBLE);
                }

                if(s.length() >= 1000 ){
                    Toast.makeText(getApplicationContext(), "Max Character Count Reached for Description", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Button listener that handles adding new button
        mAddBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Validation check to make sure no empty inputs are sent through
                if(albumName.getEditableText().toString().isEmpty() ||
                   artistName.getEditableText().toString().isEmpty() ||
                   description.getEditableText().toString().isEmpty()){
                    Toast.makeText(v.getContext(), "One or more of your fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    //Creates random numbers between 1 and 10000000
                    int randomNum1 = rand1.nextInt(10000000);
                    int randomNum2 = rand2.nextInt(10000000);

                    //Defaults to empty record string if no photo is chosen
                    if(photoToString == null){
                        photoToString = "https://firebasestorage.googleapis.com/v0/b/record-ratings.appspot.com/o/content%3A%2Fcom.android.providers.downloads.documents%2Fdocument%2F2695?alt=media&token=d8740fa6-6385-4eb7-bc6c-8b6d9b78dc40";
                    }

                    //Stores values from front end inputs to be put into record item
                    String id = mAuth.getUid();
                    String album = albumName.getText().toString();
                    String artist = artistName.getText().toString();
                    double rating2 = rating.getRating();
                    String photo = photoToString;
                    String genre2 = genre.getSelectedItem().toString();
                    String desc = description.getText().toString();
                    String recId = id + Integer.toString(randomNum1) + album + Integer.toString(randomNum2);
                    long datePosted = Instant.now().getEpochSecond();

                    //Creates record item from stored inputs and creates new document in record collection
                    Records newRecord = new Records(id, album, artist, rating2, photo, genre2, desc, recId, datePosted);
                    db.collection("records").add(newRecord)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Moves back to main activity on successful creation
                                    m.moveActivity(AddRecord.this, MainActivity.class);
                                }
                            });
                }
            }
        });
    }

    //Creates intent to open gallery
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Handles action of chosen image
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
                                Picasso.get().load(uri).into(albumCover);
                                photoToString = downloadUri.toString();
                            }
                        });
                    }
                });
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something Went Wrong.  Please Try Again.", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "No Image Has Been Selected.",Toast.LENGTH_LONG).show();
        }
    }

    //Determines if night mode preference is enabled.
    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }
}