package com.example.recordratings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.sql.Blob;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Records Database variables
    public static final String DATABASE_NAME = "Records.db";
    public static final String TABLE_NAME = "Records_Table";
    public static final String RECORDS_TABLE = "Records_Table_7";

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
        sqLiteDatabase.execSQL("CREATE TABLE " + RECORDS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ALBUM TEXT, ARTIST TEXT, RATING DOUBLE, " +
                "                                                PHOTO BLOB, GENRE TEXT, DESCRIPTION VARCHAR(1000))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RECORDS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String album, String artist, double rating, byte[] photo, String genre, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALBUM, album);
        contentValues.put(ARTIST, artist);
        contentValues.put(RATING, rating);
        contentValues.put(PHOTO, photo);
        contentValues.put(GENRE, genre);
        contentValues.put(DESCRIPTION, description);
        long result = db.insert(RECORDS_TABLE, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Integer deleteData(String row) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RECORDS_TABLE, "id=?", new String[]{row});
    }

    public Integer deleteRecord(String row, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RECORDS_TABLE, null);
        do{
            if(id == cursor.getInt(0)){
                return db.delete(RECORDS_TABLE, "id=?", new String[]{row});
            }
        } while(cursor.moveToNext());
        return -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + RECORDS_TABLE, null);
        return res;
    }

}
