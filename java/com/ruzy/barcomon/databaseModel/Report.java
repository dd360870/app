package com.ruzy.barcomon.databaseModel;

import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ruzy on 2017/5/30.
 */

@IgnoreExtraProperties
public class Report {
    public String title;
    public String content;
    public long time;
    public String userUID;
    public boolean isTrue;

    public Report() {}

    public Report(String title, String content) {
        this.title = title;
        this.content = content;
        this.time = new java.util.Date().getTime();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            this.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            this.userUID = null;
        isTrue = false;
    }
}
