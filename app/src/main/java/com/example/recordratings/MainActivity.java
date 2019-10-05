package com.example.recordratings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Records global
    public static ArrayList<Records> records = new ArrayList<>();
    Records r = new Records("Test", "Word", 5);

    //Intent module call
    MovePage m = new MovePage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvRecords = findViewById(R.id.rvRecords);

        final RecordsAdapter adapter = new RecordsAdapter(records);

        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));


        final Button button = findViewById(R.id.main_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
               m.moveActivity(MainActivity.this, AddRecord.class);
            }
        });
    }
}
