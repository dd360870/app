package com.ruzy.barcomon.databaseModel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ruzy on 2017/3/15.
 */
@IgnoreExtraProperties
public class Image {

    public String storageUri;
    public long time;
    public String by_name;
    public String by_Uid;

    public Image() {}

    public Image(String storageUri, String name, String Uid) {
        java.util.Date now = new java.util.Date();
        this.time = new java.util.Date().getTime();
        this.storageUri = storageUri;
        this.by_name = name;
        this.by_Uid = Uid;
    }
}
