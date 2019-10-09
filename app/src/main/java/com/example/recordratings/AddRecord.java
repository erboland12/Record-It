package com.example.recordratings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import static com.example.recordratings.MainActivity.records;

public class AddRecord extends AppCompatActivity {
    private MovePage m = new MovePage();
    private EditText albumName;
    private EditText artistName;
    private RatingBar rating;

    //Database declarations
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        //Input edit text values into global variables
        albumName = (EditText) findViewById(R.id.editText);
        artistName = (EditText) findViewById(R.id.editText2);
        rating = findViewById(R.id.add_record_rating);

        dbh = new DatabaseHelper(this);

        //Button creation and listener creation
        Button mAddBtn = findViewById(R.id.addRecordBtn);

        mAddBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                boolean isInserted = dbh.insertData(albumName.getEditableText().toString(),
                                    artistName.getEditableText().toString(),
                                    rating.getRating());
                if(isInserted){
                    Cursor res = dbh.getAllData();
                    StringBuffer buffer = new StringBuffer();
                    while(res.moveToNext()){
                        buffer.append("ID :" + res.getString(0) + "\n");
                        buffer.append("ALBUM :" + res.getString(1) + "\n");
                        buffer.append("ARTIST :" + res.getString(2) + "\n");
                        buffer.append("RATING :" + res.getDouble(3) + "\n\n");
                    }

                    showMessage("Data", buffer.toString());
                    m.moveActivity(AddRecord.this, MainActivity.class);
                }
            }
        });
    }



    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
