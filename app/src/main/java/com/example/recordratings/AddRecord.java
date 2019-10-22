package com.example.recordratings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import static com.example.recordratings.MainActivity.records;

public class AddRecord extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    private MovePage m = new MovePage();
    private EditText albumName;
    private EditText artistName;
    private EditText description;
    private String photo = "test";
    private RatingBar rating;
    private Spinner genre;

    //Database declarations
    DatabaseHelper dbh;

    Button browseGalleryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        //Input edit text values into global variables
        albumName = (EditText) findViewById(R.id.editText);
        artistName = (EditText) findViewById(R.id.editText2);
        rating = findViewById(R.id.add_record_rating);
        genre = findViewById(R.id.genre_spinner);
        description = findViewById(R.id.editText3);

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
                startActivityForResult(
                        new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        ),
                        GET_FROM_GALLERY
                );
            }

        });

        //Button and listener creation
        Button mAddBtn = findViewById(R.id.addRecordBtn);

        mAddBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                boolean isInserted = dbh.insertData(albumName.getEditableText().toString(),
                        artistName.getEditableText().toString(),
                        rating.getRating(),
                        photo,
                        genre.getSelectedItem().toString(),
                        description.getEditableText().toString());
                if(isInserted){
                    m.moveActivity(AddRecord.this, MainActivity.class);
                }
            }
        });
    }

}