package com.example.recordratings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Records global
    public static ArrayList<Records> records = new ArrayList<>();

    //Intent module call
    MovePage m = new MovePage();

    //Database module call
    DatabaseHelper dbh;
    String album = "Album DB";
    String artist = "Artist DB";
    Double rating = 4.5;

    //Button declaration
    Button button;
    Button dbButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);

        RecyclerView rvRecords = findViewById(R.id.rvRecords);

        final RecordsAdapter adapter = new RecordsAdapter(records);

        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        addToDB();

        button = findViewById(R.id.main_add_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
               m.moveActivity(MainActivity.this, AddRecord.class);
            }
        });
    }

    public void addToDB(){
        dbButton = findViewById(R.id.add_DB);
        dbButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                boolean isInserted = dbh.insertData(album, artist, rating);
                if(isInserted == true){
                    Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Insertion Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
