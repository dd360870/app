package com.example.ruzy.nd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruzy.nd.databaseModel.Image;

import java.util.Date;

/**
 * Created by Ruzy on 2017/5/16.
 */

public class InfoActivity extends AppCompatActivity{

    private static final String data = "DATA";
    private SharedPreferences settings;

    private ImageView i1;
    private TextView i2;
    private TextView i3;
    private boolean show = false;

    private int count = 0;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        i1 = (ImageView) findViewById(R.id.i1);
        i2 = (TextView) findViewById(R.id.i2);
        i3 = (TextView) findViewById(R.id.i3);

        findViewById(R.id.info_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show) {
                    i1.setVisibility(View.GONE);
                    i2.setVisibility(View.GONE);
                    i3.setVisibility(View.GONE);
                    show = false;
                    count=0;
                } else {
                    i3.setVisibility(View.VISIBLE);
                    if (count < 10) {
                        i3.setText((10 - count) + "");
                        count++;
                    } else {
                        i1.setVisibility(View.VISIBLE);
                        i2.setVisibility(View.VISIBLE);
                        i3.setVisibility(View.GONE);
                        show = true;
                    }
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return true;
    }
}
