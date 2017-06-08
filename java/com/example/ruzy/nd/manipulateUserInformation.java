package com.example.ruzy.nd;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Ruzy on 2017/5/27.
 */

public class manipulateUserInformation {

    private FirebaseUser mUser;
    private static HashMap<String, String> INFO = createMap();

    private static HashMap<String, String> createMap() {
        HashMap<String, String> temp = new HashMap<>();
        for(int i=1;i<20;i++) {
            temp.put(String.format("%04d", i), "日常用品");
        }
        for(int i=20;i<=37;i++) {
            temp.put(String.format("%04d", i), "運動用品");
        }
        for(int i=38;i<=45;i++) {
            temp.put(String.format("%04d", i), "休閒娛樂");
        }
        for(int i=46;i<=66;i++) {
            temp.put(String.format("%04d", i), "男服飾");
        }
        for(int i=67;i<=89;i++) {
            temp.put(String.format("%04d", i), "女服飾");
        }
        for(int i=90;i<=100;i++) {
            temp.put(String.format("%04d", i), "書籍文具");
        }
        for(int i=101;i<=121;i++) {
            temp.put(String.format("%04d", i), "食品");
        }
        return temp;
    }

    public manipulateUserInformation() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void staticsAdd(final String categoryID,final int mode,final Context context) {
        final String[] Mode = {"AddComment", "AddProduct"};
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/statics/"+INFO.get(categoryID)+"/"+Mode[mode])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int total;
                        if(dataSnapshot.exists()) {
                            total = dataSnapshot.getValue(int.class);
                            ImageView imageView = new ImageView(context);
                            imageView.setMaxHeight(500);
                            imageView.setMaxWidth(500);
                            imageView.setAdjustViewBounds(true);
                            android.app.AlertDialog.Builder builder =
                                    new android.app.AlertDialog.Builder(context)
                                            .setTitle("獲得新怪獸")
                                            .setPositiveButton("OK", null);
                            if(mode == 1 && total == 19) {
                                switch(INFO.get(categoryID)) {
                                    case "日常用品":
                                        unlockMonster(2);
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_mug);
                                        builder.setView(imageView).create().show();
                                        break;
                                    case "食品":
                                        unlockMonster(1);
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_can);
                                        builder.setView(imageView).create().show();
                                        break;
                                    case "書籍文具":
                                        unlockMonster(4);
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_book);
                                        builder.setView(imageView).create().show();
                                        break;
                                }
                            } else if(mode == 1 && total == 39) {
                                switch(INFO.get(categoryID)) {
                                    case "食品":
                                        unlockMonster(3);
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_bento);
                                        builder.setView(imageView).create().show();
                                        /*NotificationManager nm = (NotificationManager)
                                                context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        NotificationCompat.Builder bui = new NotificationCompat.Builder(context);
                                        bui.setContentTitle("YeahYeah");
                                        bui.setContentText("OhNoOhNo");
                                        bui.setContentInfo("Click to get it!");
                                        nm.notify(0,bui.build());*/
                                        break;

                                }
                            }
                        } else {
                            total = 0;
                        }
                        FirebaseDatabase.getInstance()
                                .getReference("users/"+mUser.getUid()+"/statics/"+INFO.get(categoryID)+"/"+Mode[mode]).setValue(total+1);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

    }

    public void unlockMonster(final int number) {
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/userinformation/ownMonster")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String monster = dataSnapshot.getValue(String.class);
                        char[] monsterArray = monster.toCharArray();
                        monsterArray[number] = '1';
                        dataSnapshot.getRef().setValue(new String(monsterArray));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    /*private void getCategoryData() {
        FirebaseDatabase.getInstance().getReference("CategoryAsset/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    String categoryName = d.getKey();
                    for(DataSnapshot d2:d.getChildren()) {
                        for(DataSnapshot d3:d2.getChildren()) {
                            String ID = d3.child("ID").getValue(String.class);
                            INFO.put(ID,categoryName);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }*/

    public void plusEnergy(final int Add) {
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/userinformation/BarCoMonEnergy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long energy = dataSnapshot.getValue(Long.class);
                        FirebaseDatabase.getInstance()
                                .getReference("users/"+mUser.getUid()+"/userinformation/BarCoMonEnergy")
                                .setValue(energy+Add);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }
}
