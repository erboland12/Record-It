package com.example.recordratings.records;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Blob;

import java.util.ArrayList;

public class Records {
    private String mId;
    private String mTitle;
    private String mArtist;
    private double mRating;
    private Bitmap mPhoto;
    private String mPhotoString;
    private String mGenre;
    private String mDescription;
    private String mRecId;

    public Records() {}

    public Records(String id, String title, String artist){
        mId = id;
        mTitle = title;
        mArtist = artist;
    }

    public Records(String id, String title, String artist, double rating, String photo, String genre, String description, String recId){
        mId = id;
        mTitle = title;
        mArtist = artist;
        mRating = rating;
        mPhotoString = photo;
        mGenre = genre;
        mDescription = description;
        mRecId = recId;
    }

    public String getId() { return mId; }

    public String getTitle(){
        return mTitle;
    }

    public String getArtist(){
        return mArtist;
    }

    public double getRating(){
        return mRating;
    }

    public Bitmap getPhoto(){
        return mPhoto;
    }

    public String getmPhotoString() { return mPhotoString; }

    public String getGenre(){
        return mGenre;
    }

    public String getDesc(){
        return mDescription;
    }

    public String getRecId(){ return mRecId; }

}