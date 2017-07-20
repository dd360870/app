package com.ruzy.barcomon.databaseModel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ruzy on 2017/3/15.
 */

@IgnoreExtraProperties
public class Price {

    public String price;
    public long time;
    public String by_name;
    public String by_uid;
    public String mapName;
    public double mapLat;
    public double mapLng;
    public String mapId;
    public String mapAddr;


    public Price() {}

    public Price(String price, String name, String Uid) {
        this.price = price;
        this.by_name = name;
        this.by_uid = Uid;
        this.time = new java.util.Date().getTime();;
    }
    public void setMap(String name, double lat, double lng, String id, String addr) {
        this.mapName = name;
        this.mapLat = lat;
        this.mapLng = lng;
        this.mapId = id;
        this.mapAddr = addr;
    }
}
