package com.example.recordratings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.records.Records;
import com.example.recordratings.records.RecordsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MyRecordsActivity extends AppCompatActivity {
    //Records global
    public RecyclerView rvRecords;
    public static ArrayList<Records> records = new ArrayList<>();
    private ArrayList<Records> tempRecords = new ArrayList<>();
    private TextView emptyRv;

    //Database module call
    DatabaseHelper dbh;
    public RecordsAdapter adapter;

    //Firebase declarations
    private SharedPreferences shared;
    private FirebaseAuth mAuth;
    private com.google.firebase.firestore.FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            this.setTheme(R.style.darkTheme);
        }
        super.onCreate(savedInstanceState);
        setTitle("My Records");
        setContentView(R.layout.activity_my_records);

        mAuth = FirebaseAuth.getInstance();
        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvRecords);
        rvRecords.setAdapter(adapter);
        emptyRv = findViewById(R.id.no_records);

    }

    public boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    public void readFromDatabase(){
        db = FirebaseFirestore.getInstance();
        records = new ArrayList<>();
        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String id = document.get("id").toString();
                    String album = document.getString("title");
                    String artist = document.getString("artist");
                    Double rating = document.getDouble("rating");
                    String photo = document.getString("mPhotoString");
                    String genre = document.getString("genre");
                    String desc = document.getString("desc");
                    String recId = "tes";
                    if(id.equals(mAuth.getUid())){
                        records.add(new Records(id, album, artist, rating, photo, genre, desc, recId));
                    }
                }
                adapter = new RecordsAdapter(records);
                rvRecords.setAdapter(adapter);
                rvRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();

                records.add(new Records("1", "2", "3", 0.5, "4", "5", "6", "7"));

                if(adapter.getItemCount() < 1){
                    emptyRv.setVisibility(View.VISIBLE);
                }

            }
        });
        records = new ArrayList<>();
        tempRecords = new ArrayList<>();
    }

    @Override
    public void onStart(){
        super.onStart();
        readFromDatabase();
    }
}
