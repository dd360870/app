package com.ruzy.barcomon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

/**
 * Created by Ruzy on 2017/5/16.
 */

public class SettingsActivity extends AppCompatActivity {

    private ToggleButton color;
    private SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("DATA", 0);

        setContentView(R.layout.activity_settings);

        color = (ToggleButton) findViewById(R.id.color_toggle);
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(color.isChecked()) {
                    settings.edit()
                            .putInt("mode", 1)
                            .apply();
                } else {
                    settings.edit()
                            .putInt("mode", 0)
                            .apply();
                }
            }
        });
    }
}
