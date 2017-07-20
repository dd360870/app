package com.ruzy.barcomon;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by Ruzy on 2017/5/27.
 */

public class manipulateUserInformation {

    private FirebaseUser mUser;
    private static HashMap<String, String> INFO = createMap();
    private static List<String> SkillPath = createList();

    private static List<String> createList() {
        List<String> temp = new ArrayList<>();
        temp.add(0, "StrongWater");
        temp.add(1, "H2SO4");
        temp.add(2, "PaperPlane");
        temp.add(3, "Encyclopedia");
        temp.add(4, "Compression");
        temp.add(5, "CansScroll");
        temp.add(6, "SweetSmell");
        temp.add(7, "RottenFood");
        temp.add(8, "HotWater");
        temp.add(9, "Coffee");
        temp.add(10, "RespectOlder");
        temp.add(11, "LoveTeaching");
        temp.add(12, "NotResigned");
        temp.add(13, "DeathAttack");
        return temp;
    }

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

    public void staticsAdd(final String categoryID,final int mode) {
        final String[] Mode = {"AddComment", "AddProduct"};
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/statics/"+INFO.get(categoryID)+"/"+Mode[mode])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int total;
                        if(dataSnapshot.exists()) {
                            total = dataSnapshot.getValue(int.class);
                            if(mode == 1 && total == 19) {
                                switch(INFO.get(categoryID)) {
                                    case "日常用品":
                                        unlockMonster(2);break;
                                    case "食品":
                                        unlockMonster(1);break;
                                    case "書籍文具":
                                        unlockMonster(4);break;
                                }
                            } else if(mode == 1 && total == 39) {
                                switch(INFO.get(categoryID)) {
                                    case "食品":
                                        unlockMonster(3);break;
                                }
                            }
                            if(mode == 0) {
                                if(INFO.get(categoryID).matches("食品")) {
                                    switch(total) {
                                        case 9:
                                            unlockSkill(0);break;
                                        case 19:
                                            unlockSkill(6);break;
                                        case 29:
                                            unlockSkill(7);break;
                                        case 39:
                                            unlockSkill(8);break;
                                        case 49:
                                            unlockSkill(9);break;
                                    }
                                } else if(INFO.get(categoryID).matches("日常用品")) {
                                    switch(total) {
                                        case 9:
                                            unlockSkill(1);break;
                                        case 19:
                                            unlockSkill(4);break;
                                        case 29:
                                            unlockSkill(13);break;
                                    }
                                } else if(INFO.get(categoryID).matches("書籍文具")) {
                                    switch(total) {
                                        case 9:
                                            unlockSkill(2);break;
                                        case 19:
                                            unlockSkill(3);break;
                                        case 29:
                                            unlockSkill(11);break;
                                    }
                                } else if(INFO.get(categoryID).matches("休閒娛樂")) {
                                    switch(total) {
                                        case 9:
                                            unlockSkill(5);break;
                                        case 19:
                                            unlockSkill(10);break;
                                        case 29:
                                            unlockSkill(12);break;
                                    }
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
                        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/userinformation/new/monster").setValue(number);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public void unlockSkill(final int number) {
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/Item/"+SkillPath.get(number)).setValue(1);
        FirebaseDatabase.getInstance().getReference("users/"+mUser.getUid()+"/userinformation/new/skill").setValue(number);
    }

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
