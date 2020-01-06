package com.example.recordratings.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

    //Creates SQLite database table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + RECORDS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ALBUM TEXT, ARTIST TEXT, RATING DOUBLE, " +
                "                                                PHOTO BLOB, GENRE TEXT, DESCRIPTION VARCHAR(1000))");
    }

    //Upgrades table after dropping current instance
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RECORDS_TABLE);
        onCreate(sqLiteDatabase);
    }

    //
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

    public boolean updateData(int id, String album, String artist, float rating, byte[] photo, String genre, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALBUM, album);
        contentValues.put(ARTIST, artist);
        contentValues.put(RATING, rating);
        contentValues.put(PHOTO, photo);
        contentValues.put(GENRE, genre);
        contentValues.put(DESCRIPTION, description);
        int res = db.update(RECORDS_TABLE, contentValues,"ID = ?",new String[]{Integer.toString(id)});
//        Cursor cursor = db.rawQuery("UPDATE " + RECORDS_TABLE + " SET ALBUM = '" + album + "', ARTIST = '" + artist + "', " +
//                "RATING = " + rating + ", PHOTO = '" + photo + "', GENRE = '" + genre + "', DESCRIPTION = '" + description +
//                "' WHERE ID = " + id, null);

        return true;
    }

}
