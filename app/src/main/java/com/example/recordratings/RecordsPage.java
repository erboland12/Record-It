package com.example.recordratings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecordsPage extends AppCompatActivity {

    //Text View for album and artist names
    TextView album;
    TextView artist;
    ImageView photo;
    RatingBar rating;
    TextView genre;
    TextView description;

    //Placeholders for recycler view variables
    public static String albumTemp;
    public static String artistTemp;
    public static double ratingTemp;
    public static String genreTemp;
    public static String descTemp;
    public static Bitmap photoTemp;

    //Buttons


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

        photo = findViewById(R.id.records_page_image);
        photo.setImageBitmap(photoTemp);
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
