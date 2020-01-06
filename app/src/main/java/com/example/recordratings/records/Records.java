package com.example.recordratings.records;

public class Records {
    private String mId;
    private String mTitle;
    private String mArtist;
    private double mRating;
    private String mPhotoString;
    private String mGenre;
    private String mDescription;
    private String mRecId;
    private long mDatePostedUnix;


    public Records(String id, String title, String artist, double rating, String photo, String genre,
                   String description, String recId, long datePostedUnix) {
        mId = id;
        mTitle = title;
        mArtist = artist;
        mRating = rating;
        mPhotoString = photo;
        mGenre = genre;
        mDescription = description;
        mRecId = recId;
        mDatePostedUnix = datePostedUnix;
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

    public String getmPhotoString() { return mPhotoString; }

    public String getGenre(){
        return mGenre;
    }

    public String getDesc(){
        return mDescription;
    }

    public String getRecId(){ return mRecId; }

    public long getDatePostedUnix(){ return mDatePostedUnix; }

}