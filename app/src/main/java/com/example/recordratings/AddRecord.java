package com.example.recordratings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class AddRecord extends AppCompatActivity {
    private MovePage m = new MovePage();
    private EditText albumName;
    private EditText artistName;
    private RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        //Input edit text values into global variables
        albumName = (EditText) findViewById(R.id.editText);
        artistName = (EditText) findViewById(R.id.editText2);
        rating = findViewById(R.id.add_record_rating);
        //Button creation and listener creation
        Button mAddBtn = findViewById(R.id.addRecordBtn);

        mAddBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Records.addRecord(MainActivity.records, new Records(albumName.getEditableText().toString(),
                                                                    artistName.getEditableText().toString(),
                                                                    rating.getRating()));
                m.moveActivity(AddRecord.this, MainActivity.class);
            }
        });
    }
}
