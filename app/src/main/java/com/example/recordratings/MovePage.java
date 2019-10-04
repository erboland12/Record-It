package com.example.recordratings;

import android.content.Context;
import android.content.Intent;

public class MovePage {
    public void moveActivity(Context current, Class<?> nav){
        Intent intent = new Intent(current, nav);
        current.startActivity(intent);
    }

    public void moveData(Context current, Class<?> nav, String id, int data){
        Intent intent = new Intent(current, nav);
        intent.putExtra(id, data);
        current.startActivity(intent);
    }
}