package com.example.recordratings.records;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Records {
    private int mId;
    private String mTitle;
    private String mArtist;
    private double mRating;
    private Bitmap mPhoto;
    private String mGenre;
    private String mDescription;

    public Records(Integer id, String title, String artist, double rating, Bitmap photo, String genre, String description){
        mId = id;
        mTitle = title;
        mArtist = artist;
        mRating = rating;
        mPhoto = photo;
        mGenre = genre;
        mDescription = description;
    }

    public int getId() { return mId; }

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

    public String getGenre(){
        return mGenre;
    }

    public String getDesc(){
        return mDescription;
    }

    public static ArrayList<Records> createRecordsList(int x, Records r){
        ArrayList<Records> recordsList = new ArrayList<>();
        for(int i = 0; i < x; i++){
            recordsList.add(new Records(r.getId(), r.getTitle(), r.getArtist(), r.getRating(), r.getPhoto(), r.getGenre(), r.getDesc()));
        }
        return recordsList;
    }

    public static ArrayList<Records> addRecord(ArrayList<Records> list, Records r){
        list.add(r);
        return list;
    }
}