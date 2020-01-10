package com.example.recordratings.records;

import java.util.Date;

public class Comment {
    private String mDisplayName;
    private String mContents;
    private String mTimestamp;
    private String mUserId;
    private String mRecordId;
    private String mCommentId;
    private String mPhotoString;
    private int mVotes;
    private long mUnixTime;

    public Comment(String name, String contents, String time, String userId, String recId, String commentId, String photo, int votes, long unixTime){
        mDisplayName = name;
        mContents = contents;
        mTimestamp = time;
        mUserId = userId;
        mRecordId = recId;
        mCommentId = commentId;
        mPhotoString = photo;
        mVotes = votes;
        mUnixTime = unixTime;
    }


    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getmContents() {
        return mContents;
    }

    public String getmTimestamp() {
        return mTimestamp;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmRecordId() {
        return mRecordId;
    }

    public String getmCommentId() { return mCommentId; }

    public String getPhotoString(){
        return mPhotoString;
    }

    public int getmVotes(){
        return mVotes;
    }

    public long getUnixTime(){ return mUnixTime; }
}
