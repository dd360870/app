package com.example.ruzy.nd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

/**
 * Created by Ruzy on 2017/5/16.
 */

public class InfoActivity extends AppCompatActivity{

    private static final String data = "DATA";
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(data, 0);
        int i = settings.getInt("mode", 0);
        if(i == 66) {
            setTheme(R.style.AppTheme_Red);
        }
        //getTheme().applyStyle(R.style.AppTheme_Red, true);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return true;
    }
}
