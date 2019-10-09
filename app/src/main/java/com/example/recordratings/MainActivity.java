package com.example.recordratings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    private SQLiteDatabase mDatabase;
    public static  RecyclerView rvRecords;
    public static ArrayList<Records> records = new ArrayList<>();

    //Intent module call
    MovePage m = new MovePage();

    //Database module call
    DatabaseHelper dbh;
    String album = "Album DB";
    String artist = "Artist DB";
    Double rating = 4.5;

    private List<Records> rl;
    public static RecordsAdapter adapter;

    //Button declaration
    Button button;
    Button dbButton;
    Button dbDelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);

        rvRecords = findViewById(R.id.rvRecords);

        //Displays DB data in RecyclerView
        final Cursor cursor = dbh.getAllData();

        if(cursor.moveToNext()){
            do {
                records.add(new Records(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3)
                ));
            } while (cursor.moveToNext());
            RecordsAdapter adapter = new RecordsAdapter(records);
            rvRecords.setAdapter(adapter);
            rvRecords.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            records = new ArrayList<>();
        }


        button = findViewById(R.id.main_add_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
               m.moveActivity(MainActivity.this, AddRecord.class);
            }
        });

        dbDelButton = findViewById(R.id.delete_DB);
        dbDelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
//                Integer deletedRows = dbh.deleteData(Integer.toString(cursor.getColumnCount()-1));
//
//                adapter.notifyDataSetChanged();
//                finish();
//                startActivity(getIntent());
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
