package com.example.recordratings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.records.Records;
import com.example.recordratings.records.RecordsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public static String uid;

    private LinearLayout header, bottom;
    private CircleImageView pic;
    private ImageView editProfile;
    private TextView dn, bio, recordCount;
    private RecyclerView rvRecords;
    private RecordsAdapter adapter;
    private ArrayList<Records> records = new ArrayList<>();
    private ArrayList<Records> tempRecords = new ArrayList<>();
    private TextView emptyRv;

    private SharedPreferences shared;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            setTheme(R.style.darkThemeNoBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        header = findViewById(R.id.profile_header);
        bottom = findViewById(R.id.profile_bottom);
        pic = findViewById(R.id.profile_page_picture);
        dn = findViewById(R.id.profile_page_dn);
        bio = findViewById(R.id.profile_bio);
        recordCount = findViewById(R.id.profile_record_counts);

        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvProfileRecords);
        adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);
        emptyRv = findViewById(R.id.no_records);

        //Calls toolbar xml file
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if(returnDark()){
            bottom.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            rvRecords.setBackground(getResources().getDrawable(R.drawable.rv_dark_border));
        }
        //Gets all of the user's records from db
        readFromDatabase();

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    if(doc.getString("mId").equals(uid)){
                        Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                        Picasso.get().load(uri).into(pic);

                        dn.setText(doc.getString("mDisplayName"));

                        if(adapter.getItemCount() < 1){
                            recordCount.setText(doc.getString("mDisplayName") + " has not posted any records.");

                        }else{
                            recordCount.setText(doc.getString("mDisplayName") + "'s Total Records: " + Integer.toString(adapter.getItemCount()));
                        }

                        if(mAuth.getCurrentUser().getUid().equals(uid) && adapter.getItemCount() < 1){
                            recordCount.setText("You have not posted any records.");
                        }else if(mAuth.getCurrentUser().getUid().equals(uid) && adapter.getItemCount() >= 1){
                            recordCount.setText("Your Total Records: " + Integer.toString(adapter.getItemCount()));
                        }

                        if(doc.getString("bio").equals("Empty")){
                            bio.setText("\"This Mysterious User Has Nothing To Say...\"");
                        }

                        break;

                    }
                }
            }
        });

    }


    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    public void readFromDatabase(){
        db = FirebaseFirestore.getInstance();
        records = new ArrayList<>();
        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int i = 0;
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String id = document.get("id").toString();
                    String album = document.getString("title");
                    String artist = document.getString("artist");
                    Double rating = document.getDouble("rating");
                    String photo = document.getString("mPhotoString");
                    String genre = document.getString("genre");
                    String desc = document.getString("desc");
                    String recId = document.getString("recId");
                    if(id.equals(uid)){
                        records.add(new Records(id, album, artist, rating, photo, genre, desc, recId));
                        i++;
                    }
                }

                adapter = new RecordsAdapter(records);
                rvRecords.setAdapter(adapter);
                rvRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();


            }
        });
        records = new ArrayList<>();
        tempRecords = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        invalidateOptionsMenu();
        if(returnDark()){
            getMenuInflater().inflate(R.menu.menu_profile, menu);
            for(int i = 0; i < menu.size(); i++){
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }else{
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

}
