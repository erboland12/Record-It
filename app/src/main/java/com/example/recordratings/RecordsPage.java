package com.example.recordratings;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecordsPage extends AppCompatActivity {

    //Text View for album and artist names
    TextView album;
    TextView artist;
    RatingBar rating;

    //Placeholders for recycler view variables
    public static String albumTemp;
    public static String artistTemp;
    public static double ratingTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_page);

        setDetailViewVariables();



    }

    private void setDetailViewVariables(){
        album = findViewById(R.id.records_page_Album);
        album.setText(albumTemp);

        rating = findViewById(R.id.records_page_rating);
        rating.setRating((float) ratingTemp);

        artist = findViewById(R.id.records_page_artist);
        artist.setText(artistTemp);
    }
}
