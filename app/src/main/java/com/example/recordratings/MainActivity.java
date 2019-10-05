package com.example.recordratings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
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

    private List<Records> rl;
    private RecordsAdapter adapter;

    //Button declaration
    Button button;
    Button dbButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);

        RecyclerView rvRecords = findViewById(R.id.rvRecords);
//
//        final RecordsAdapter adapter = new RecordsAdapter(records);
//
//        rvRecords.setAdapter(adapter);
//        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        //addToDB();

        //Displays DB data in RecyclerView
        Cursor cursor = dbh.getAllData();

        if(cursor.moveToFirst()){
            for(int i = 0; i <= cursor.getCount(); i++) {
                Records r = new Records(cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
                records.add(r);
            }

        }

        final RecordsAdapter adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

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
                    Cursor res = dbh.getAllData();
                    if(res.getCount() == 0){
                        showMessage("Error", "No Data Found");
                        return;
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        while(res.moveToNext()){
                            buffer.append("ID :" + res.getString(0) + "\n");
                            buffer.append("ALBUM :" + res.getString(1) + "\n");
                            buffer.append("ARTIST :" + res.getString(2) + "\n");
                            buffer.append("RATING :" + res.getDouble(3) + "\n\n");
                        }

                        showMessage("Data", buffer.toString());
                    }
                } else
                    Toast.makeText(MainActivity.this, "Insertion Failed", Toast.LENGTH_LONG).show();
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
