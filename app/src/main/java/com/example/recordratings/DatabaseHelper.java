package com.example.recordratings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Blob;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Records Database variables
    public static final String DATABASE_NAME = "Records.db";
    public static final String TABLE_NAME = "Records_Table";

    //Fields for database
    public static final String ID = "ID";
    public static final String ALBUM = "ALBUM";
    public static final String ARTIST = "ARTIST";
    public static final String RATING = "RATING";
    public static final String PHOTO = "PHOTO";
    public static final String GENRE = "GENRE";
    public static final String DESCRIPTION = "DESCRIPTION";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ALBUM TEXT, ARTIST TEXT, RATING DOUBLE, " +
                "                                                PHOTO TEXT, GENRE TEXT, DESCRIPTION VARCHAR(1000))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String album, String artist, double rating, String photo, String genre, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALBUM, album);
        contentValues.put(ARTIST, artist);
        contentValues.put(RATING, rating);
        contentValues.put(PHOTO, photo);
        contentValues.put(GENRE, genre);
        contentValues.put(DESCRIPTION, description);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Integer deleteData(String row) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{row});
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public void alterData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD GENRE TEXT ");
        db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD DESCRIPTION VARCHAR(1000) ");
    }
}
