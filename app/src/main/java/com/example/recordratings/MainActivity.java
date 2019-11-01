package com.example.recordratings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.graphics.Matrix;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Records global
    public RecyclerView rvRecords;
    public static ArrayList<Records> records = new ArrayList<>();

    //Intent module call
    MovePage m = new MovePage();

    //Database module call
    DatabaseHelper dbh;

    private List<Records> rl;
    public RecordsAdapter adapter;

   //Button declaration
    Button button;
    Button dbDelButton;

    Spinner filter;

    //Search
    public static SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Prevents soft keyboard from pushing view up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Calls toolbar xml file
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dbh = new DatabaseHelper(this);
        //dbh.onCreate(dbh.getWritableDatabase());

        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvRecords);
        adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);

        //Sets up search view and filter spinner
        search = findViewById(R.id.action_search);
        filter = findViewById(R.id.filter_spinner);


        //Makes initial call to load db contents into RV and creates listener for only showing
        //filter when search icon is clicked.
        searchRV();

        search.setOnSearchClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filter.setVisibility(View.VISIBLE);
            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filter.setVisibility(View.INVISIBLE);
                return false;
            }
        });


        showDB();

        populateFilter();



        rvRecords.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MainActivity.this, "Word", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        button = findViewById(R.id.main_add_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                m.moveActivity(MainActivity.this, AddRecord.class);
            }
        });

        dbDelButton = findViewById(R.id.delete_DB);
        dbDelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                for(int i = 1; i < 250; i++){
                    Integer deletedRows = dbh.deleteData(Integer.toString(i));
                }
                adapter.notifyDataSetChanged();
                finish();
                startActivity(getIntent());
            }
        });
    }


    public void showDB(){
        Button b = findViewById(R.id.add_DB);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = dbh.getAllData();
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("ID :" + res.getString(0) + "\n");
                    buffer.append("ALBUM :" + res.getString(1) + "\n");
                    buffer.append("ARTIST :" + res.getString(2) + "\n");
                    buffer.append("RATING :" + res.getDouble(3) + "\n");
                    buffer.append("GENRE :" + res.getString(5) + "\n");
                    buffer.append("DESCRIPTION :" + res.getString(6) + "\n");
                }

                showMessage("Data", buffer.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(filter.getVisibility() == View.INVISIBLE){
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else{
            menu.findItem(R.id.action_favorite).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {

            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (filter.getVisibility() == View.INVISIBLE) {
            menu.findItem(R.id.action_favorite).setVisible(true);
        } else if (filter.getVisibility() == View.VISIBLE){
            menu.findItem(R.id.action_favorite).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    //Performs filter on database
    private void filter(CharSequence text){
        Cursor cursor = dbh.getAllData();
        if (cursor.moveToFirst()) {
            do {
                if (filter.getSelectedItem().toString() == "Album"){
                    if (cursor.getString(1).toLowerCase().contains(text)) {
                        Log.d(text.toString(), "Match");
                        records.add(new Records(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                getImage(cursor.getBlob(4)),
                                cursor.getString(5),
                                cursor.getString(6)
                        ));
                    }
                }
                if (filter.getSelectedItem().toString() == "Artist"){
                    if (cursor.getString(2).toLowerCase().contains(text)) {
                        Log.d(text.toString(), "Match");
                        records.add(new Records(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                getImage(cursor.getBlob(4)),
                                cursor.getString(5),
                                cursor.getString(6)
                        ));
                    }
                }
                if (filter.getSelectedItem().toString() == "Genre"){
                    if (cursor.getString(5).toLowerCase().contains(text)) {
                        Log.d(text.toString(), "Match");
                        records.add(new Records(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                getImage(cursor.getBlob(4)),
                                cursor.getString(5),
                                cursor.getString(6)
                        ));
                    }
                }
                if (filter.getSelectedItem().toString() == "No Filter") {
                    if (cursor.getString(5).toLowerCase().contains(text) ||
                            cursor.getString(1).toLowerCase().contains(text) ||
                            cursor.getString(2).toLowerCase().contains(text)) {
                        Log.d(text.toString(), "Match");
                        records.add(new Records(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                getImage(cursor.getBlob(4)),
                                cursor.getString(5),
                                cursor.getString(6)
                        ));
                    }
                }
            } while (cursor.moveToNext());
            adapter = new RecordsAdapter(records);
            rvRecords.setAdapter(adapter);
            rvRecords.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            records = new ArrayList<>();

        }
    }

    //Loads content of RV from database entries
    public void loadRV(){
        Cursor cursor = dbh.getAllData();

        if (cursor.moveToNext()) {
            do {
                records.add(new Records(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        getImage(cursor.getBlob(4)),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
            rvRecords.setAdapter(adapter);
            rvRecords.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            records = new ArrayList<>();
        }

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
        loadRV();
    }

    private void populateFilter(){
        filter = findViewById(R.id.filter_spinner);

        String[] items = new String[]{"No Filter", "Artist", "Album", "Genre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        filter.setAdapter(adapter);
        filter.setPrompt("Title");
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


}