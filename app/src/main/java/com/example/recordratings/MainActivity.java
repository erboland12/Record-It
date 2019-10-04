package com.example.recordratings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Records> records;
    Records r = new Records("Test", "Word", 5);
    MovePage m = new MovePage();
    Hashtable<String, Integer> titles = new Hashtable<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvRecords = findViewById(R.id.rvRecords);
        records = Records.createRecordsList(10, r);

        final RecordsAdapter adapter = new RecordsAdapter(records);

        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));



    }
}
