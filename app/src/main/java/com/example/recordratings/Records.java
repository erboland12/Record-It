package com.example.recordratings;

import java.util.ArrayList;

public class Records {
    private String mTitle;
    private String mArtist;
    private double mRating;

    public Records(String title, String artist, double rating){
        mTitle = title;
        mArtist = artist;
        mRating = rating;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAritst(){
        return mArtist;
    }

    public double getRating(){
        return mRating;
    }

    public static ArrayList<Records> createRecordsList(int x, Records r){
        ArrayList<Records> recordsList = new ArrayList<>();
        for(int i = 0; i < x; i++){
            recordsList.add(new Records(r.getTitle(), r.getAritst(), r.getRating()));
        }
        return recordsList;
    }

    public static ArrayList<Records> addRecord(ArrayList<Records> list, Records r){
        list.add(r);
        return list;
    }
}
