package com.example.recordratings;

import java.util.ArrayList;

public class Records {
    private String mTitle;
    private String mArtist;
    private double mRating;
    private String mPhoto;
    private String mGenre;
    private String mDescription;

    public Records(String title, String artist, double rating, String photo, String genre, String description){
        mTitle = title;
        mArtist = artist;
        mRating = rating;
        mPhoto = photo;
        mGenre = genre;
        mDescription = description;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getArtist(){
        return mArtist;
    }

    public double getRating(){
        return mRating;
    }

    public String getPhoto(){
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
            recordsList.add(new Records(r.getTitle(), r.getArtist(), r.getRating(), r.getPhoto(), r.getGenre(), r.getDesc()));
        }
        return recordsList;
    }

    public static ArrayList<Records> addRecord(ArrayList<Records> list, Records r){
        list.add(r);
        return list;
    }
}