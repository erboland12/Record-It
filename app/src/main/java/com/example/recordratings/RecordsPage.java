package com.example.recordratings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class RecordsPage extends AppCompatActivity {

    //Text View for album and artist names
    TextView album;
    TextView artist;
    ImageView photo;
    RatingBar rating;
    TextView description;

    //Placeholders for recycler view variables
    public static int idTemp;
    public static String albumTemp;
    public static String artistTemp;
    public static double ratingTemp;
    public static String genreTemp;
    public static String descTemp;
    public static Bitmap photoTemp;

    //Buttons
    private Button delBtn;
    MovePage m;

    //Database helper
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_page);

        setDetailViewVariables();

        dbh = new DatabaseHelper(this);

        delBtn = findViewById(R.id.records_page_delete_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbh.deleteData(Integer.toString(idTemp));
                m = new MovePage();
                m.moveActivity(RecordsPage.this, MainActivity.class);
            }
        });
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

        description = findViewById(R.id.records_page_desc);
        description.setText(descTemp);

        setBackground(genreTemp);

    }

    private void setBackground(String genre){
        ConstraintLayout bg = findViewById(R.id.records_page_background);
//        if(genre != "Rock"){
//            bg.setBackgroundColor(getResources().getColor(R.color.red));
//        }

    }

}
