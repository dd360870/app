package com.example.ruzy.nd.databaseModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ruzy on 2017/3/11.
 */
@IgnoreExtraProperties
public class Comment {
    public String uid;
    public String photoUrl;
    public String author;
    public String title;
    public String body;
    public long time;
    public long star;

    public Comment() {}

    public Comment(String title, String body, int star) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uid = user.getUid();
        this.author = user.getDisplayName();
        this.photoUrl = user.getPhotoUrl()==null?null:user.getPhotoUrl().toString();
        this.title = title;
        this.body = body;
        this.star = star;
        this.time = new java.util.Date().getTime();
    }
}
