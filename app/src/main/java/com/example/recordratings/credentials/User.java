package com.example.recordratings.credentials;

public class User {
    private String mId;
    private String mEmail;
    private String mDisplayName;
    private String mPhotoUrl;
    private String mBio;

    public User(String id, String email, String displayName, String url, String bio){
        mId = id;
        mEmail = email;
        mDisplayName = displayName;
        mPhotoUrl = url;
        mBio = bio;
    }


    public String getmId() {
        return mId;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public String getBio(){
        return mBio;
    }
}
