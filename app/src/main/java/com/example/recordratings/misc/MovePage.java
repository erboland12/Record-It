package com.example.recordratings.misc;

import android.content.Context;
import android.content.Intent;
//Basically just a modularized version of intent.  Consider replacing...
public class MovePage {
    public void moveActivity(Context current, Class<?> nav){
        Intent intent = new Intent(current, nav);
        current.startActivity(intent);
    }
}