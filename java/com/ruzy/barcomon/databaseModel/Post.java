package com.ruzy.barcomon.databaseModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;


/**
 * Created by Ruzy on 2017/3/1.
 */
@IgnoreExtraProperties
public class Post {
    public String author; //上傳商品者Uid
    public long time; //上傳時間
    public String name; //商品名稱
    public String barcode; //條碼
    public double avgStar;
    public String categoryID;

    public Post() {}

    public Post(String name, String barcode) {
        this.author = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.time = new java.util.Date().getTime();
        this.name = name;
        this.barcode = barcode;
        this.avgStar = 0.0;
    }
}
