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
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import java.util.Random;

import io.grpc.Context;

public class AddRecord extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private MovePage m = new MovePage();
    private EditText albumName;
    private EditText artistName;
    private EditText description;
    private Bitmap photo;
    private RatingBar rating;
    private Spinner genre;
    private String photoToString;
    private ImageView albumCover;

    //Database declarations
    private DatabaseHelper dbh;

    //Button declaration
    private Button browseGalleryBtn;

    private SharedPreferences shared;
    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private com.google.firebase.database.DatabaseReference dbRef;

    Random rand1;
    Random rand2;
    Random rand3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(returnDark()){
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_add_record);


        //Input edit text values into global variables
        albumName = (EditText) findViewById(R.id.editText);
        artistName = (EditText) findViewById(R.id.editText2);
        rating = findViewById(R.id.add_record_rating);
        genre = findViewById(R.id.genre_spinner);
        description = findViewById(R.id.editText3);
        albumCover = findViewById(R.id.add_record_image_view);

        if(returnDark()){
            albumName.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            artistName.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }

        rand1 = new Random();
        rand2 = new Random();
        rand3 = new Random();

        //Sets up Firebase
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Adds options to Spinner
        String[] items = new String[]{"Pop", "Rock", "Jazz", "Blues", "Rap", "Country", "Folk",
                                      "Metal", "Progressive", "Psychedelic", "Punk", "Alternative",
                                      "Indie", "Classical", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        genre.setAdapter(adapter);

        //Initializes db helper
        dbh = new DatabaseHelper(this);

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

        mAddBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(albumName.getEditableText().toString().isEmpty() ||
                   artistName.getEditableText().toString().isEmpty() ||
                   description.getEditableText().toString().isEmpty()){
                    Toast.makeText(v.getContext(), "One or more of your fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    int randomNum1 = rand1.nextInt(10000000);
                    int randomNum2 = rand2.nextInt(10000000);

                    if(photoToString == null){
                        photoToString = "https://firebasestorage.googleapis.com/v0/b/record-ratings.appspot.com/o/content%3A%2Fcom.android.providers.downloads.documents%2Fdocument%2F2695?alt=media&token=d8740fa6-6385-4eb7-bc6c-8b6d9b78dc40";
                    }

                    String id = mAuth.getUid();
                    String album = albumName.getText().toString();
                    String artist = artistName.getText().toString();
                    double rating2 = rating.getRating();
                    String photo = photoToString;
                    String genre2 = genre.getSelectedItem().toString();
                    String desc = description.getText().toString();
                    String recId = id + Integer.toString(randomNum1) + album + Integer.toString(randomNum2);

                    Records newRecord = new Records(id, album, artist, rating2, photo, genre2, desc, recId);
                    db.collection("records").add(newRecord)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    m.moveActivity(AddRecord.this, MainActivity.class);
                                }
                            });
                }
            }
        });
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
                                Picasso.get().load(uri).into(albumCover);
                                photoToString = downloadUri.toString();
                            }
                        });
                    }
                });
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                photo = selectedImage;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "No Image Selected",Toast.LENGTH_LONG).show();
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }



}