package com.example.recordratings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.recordratings.credentials.LoginActivity;
import com.example.recordratings.credentials.ProfileActivity;
import com.example.recordratings.misc.Censor;
import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.records.AddRecord;
import com.example.recordratings.records.Records;
import com.example.recordratings.records.RecordsAdapter;
import com.example.recordratings.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Recycler View variables
    public RecyclerView rvRecords;
    public static ArrayList<Records> records = new ArrayList<>();
    private ArrayList<Records> tempRecords = new ArrayList<>();
    public RecordsAdapter adapter;

    //Front-end variables
    private View hView;
    private CircleImageView menuPic;
    private TextView menuSub;

    //Intent module call
    MovePage m = new MovePage();

    //Spinner declaration
    private Spinner filter;

    //Search
    public static SearchView search;

    //Drawer Layout
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //Nav view layout
    NavigationView navView;

    //Shared preference, fireauth, and firestore
    private SharedPreferences shared;
    private FirebaseAuth mAuth;
    private com.google.firebase.firestore.FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(returnDark()){
            setTheme(R.style.darkThemeNoBar);
        }
        setContentView(R.layout.activity_main);
        setNavigationViewListener();

        //Prevents soft keyboard from pushing view up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Sets up DrawerLayout and ActionBar
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        navView = findViewById(R.id.nav_view);

        //Inflates header for navigation view
        hView = navView.inflateHeaderView(R.layout.nav_header);

        //Handles creation of drawer layout
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //Calls toolbar xml file
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvRecords);
        adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);

        //Sets up search view and filter spinner
        search = findViewById(R.id.action_search);
        filter = findViewById(R.id.filter_spinner);

        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter("Test");
            }
        });

        //Makes initial call to load db contents into RV and creates listener for only showing
        //filter when search icon is clicked.

        //Icon click listener that sets filter visibility
        search.setOnSearchClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filter.setVisibility(View.VISIBLE);
            }
        });

        //Icon close listener that hides filter
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filter.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        //Populates filter spinner with sort options
        populateFilter();

        //Handles filtering recycler view when search is sued
        searchRV();

    }


    //Handles custom menu icons and actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        invalidateOptionsMenu();
        if(returnDark()){
            getMenuInflater().inflate(R.menu.menu_main, menu);
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

    //Handles menu icon selection actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            //Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.menu_add_record){
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Handles navigation view item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.login:{
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            }
            case R.id.menu_add_record: {
                MovePage m = new MovePage();
                if(mAuth.getCurrentUser() != null){
                    m.moveActivity(MainActivity.this, AddRecord.class);
                }else{
                    Toast.makeText(this, "You Must be Logged In to Add a Record.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.my_profile: {
                if(mAuth.getCurrentUser() != null){
                    ProfileActivity.uid = mAuth.getCurrentUser().getUid();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }else{
                    Toast.makeText(this, "You Must be Logged In to View the Profile Page.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.log_out:{
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                Toast.makeText(this, "You Have Been Logged Out.", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.settings:{
                MovePage m = new MovePage();
                finish();
                m.moveActivity(MainActivity.this, SettingsActivity.class);
            }
            case R.id.quit_app:{
                finish();
                System.exit(0);
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Performs filter on database
    private void filter(CharSequence text){

        tempRecords.addAll(records);
        records = new ArrayList<>();
        for(Records record: tempRecords){
            if(filter.getSelectedItem().toString() == "No Filter"){
                if(record.getTitle().toLowerCase().contains(text) || record.getArtist().toLowerCase().contains(text) ||
                   record.getGenre().toLowerCase().contains(text)){
                    records.add(record);
                }
            }
            if(filter.getSelectedItem().toString() == "Album"){
                if(record.getTitle().toLowerCase().contains(text)){
                    records.add(record);
                }
            }
            if(filter.getSelectedItem().toString() == "Artist"){
                if(record.getArtist().toLowerCase().contains(text)){
                    records.add(record);
                }
            }
            if(filter.getSelectedItem().toString() == "Genre"){
                if(record.getGenre().toLowerCase().contains(text)){
                    records.add(record);
                }
            }
        }
        adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter.notifyDataSetChanged();
        records = new ArrayList<>();

    }


    //Filters Recycler View based on search input
    public void searchRV(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query.toLowerCase());
                Log.d(query, "Query");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase());
                Log.d(newText, "New Text");
                return false;
            }

        });
    }

    //Populates filter spinner with options to filter by
    private void populateFilter(){
        filter = findViewById(R.id.filter_spinner);

        String[] items = new String[]{"No Filter", "Artist", "Album", "Genre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        filter.setAdapter(adapter);
        filter.setPrompt("Title");
    }

    //Closes activity on back pressed
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    //Determines if night mode preference is enabled
    public boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    //Reads in record collections to add to unfiltered recycler view
    public void readFromDatabase(final FirebaseUser user){
        db = FirebaseFirestore.getInstance();
        records = new ArrayList<>();
        //Database query to store all record documents as objects
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
                    String recId = "test";
                    records.add(new Records(id, album, artist, rating, photo, genre, desc, recId));
                }
                adapter = new RecordsAdapter(records);
                rvRecords.setAdapter(adapter);
                rvRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();

            }
        });

        //Database query for loading profile picture into navigation image view
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot doc: queryDocumentSnapshots){
                    if(user != null){
                        if(doc.getString("mId").equals(user.getUid())){
                            Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                            Picasso.get().load(uri).into(menuPic);
                            String dn = user.getDisplayName();
                            menuSub.append(" " + dn);
                            //Displays additional text if user is granted admin status
                            if(doc.getBoolean("admin") != null){
                                if(doc.getBoolean("admin")){
                                    menuSub.append(" " + "(Admin Logged In)");
                                }
                            }
                            return;
                        }
                    }else{
                        menuSub.setText("Not Logged In");
                    }
                }
            }
        });
        records = new ArrayList<>();
        tempRecords = new ArrayList<>();
    }

    //Handles start-up actions
    @Override
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        menuPic = hView.findViewById(R.id.menu_picture);
        menuSub = hView.findViewById(R.id.menu_sub);
        menuSub.setText("Welcome Back,");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        readFromDatabase(currentUser);
    }
}