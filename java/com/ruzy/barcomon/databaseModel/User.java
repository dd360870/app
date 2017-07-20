package com.ruzy.barcomon.databaseModel;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String photoUrl;
    //public String lasttimelogin;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String photoUrl) {
        java.util.Date now = new java.util.Date();
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        //this.lasttimelogin = new java.text.SimpleDateFormat().format(now);
    }

}
// [END blog_user_class]