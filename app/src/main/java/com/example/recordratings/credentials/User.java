package com.example.recordratings.credentials;

public class User {
    private String mId;
    private String mEmail;
    private String mDisplayName;
    private String mPhotoUrl;
    private String mBio;
    private boolean mAdmin;

    public User(String id, String email, String displayName, String url, String bio, boolean admin){
        mId = id;
        mEmail = email;
        mDisplayName = displayName;
        mPhotoUrl = url;
        mBio = bio;
        mAdmin = admin;
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

    public boolean getAdmin() { return mAdmin; }
}
