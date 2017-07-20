package com.ruzy.barcomon;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;

import static android.view.View.*;

/**
 * Created by Ruzy on 2017/4/27.
 */

public class AccountActivity extends AppCompatActivity{
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;

    private ImageView image;
    private TextView name;
    private ImageView editname;
    private TextView email;
    private Button login;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image = (ImageView) findViewById(R.id.account_image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null)
                    return;
                final CharSequence[] items = {"拍攝照片", "從相簿中選擇", "取消"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("更換大頭貼");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("拍攝照片")) {
                            //PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("從相簿中選擇")) {
                            //PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,SELECT_FILE);
                        } else if (items[item].equals("取消")) {
                            //PROFILE_PIC_COUNT = 0;
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        login = (Button) findViewById(R.id.account_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        logout = (Button) findViewById(R.id.account_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        name = (TextView) findViewById(R.id.account_name);
        name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(AccountActivity.this, "click name", Toast.LENGTH_LONG).show();
                if(mAuth.getCurrentUser() == null)
                    return;
                nameDialog();
            }
        });
        editname = (ImageView) findViewById(R.id.account_editname);
        editname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDialog();
            }
        });
        email = (TextView) findViewById(R.id.account_email);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if(user.getPhotoUrl() != null) {
                        Glide
                                .with(AccountActivity.this)
                                .load(user.getPhotoUrl())
                                .centerCrop()
                                .crossFade()
                                .into(image);
                    }
                    if(user.getDisplayName()==null||user.getDisplayName().matches("")) {
                        name.setText("點此設定暱稱");
                    } else {
                        name.setText(user.getDisplayName());
                    }
                    email.setText(user.getEmail());
                    login.setVisibility(GONE);
                    logout.setVisibility(VISIBLE);
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                    image.setImageResource(R.drawable.chih_account_default);
                    name.setText("尚未登入");
                    email.setText("");
                    login.setVisibility(VISIBLE);
                    logout.setVisibility(GONE);
                }
            }
        };

        /*if(mAuth.getCurrentUser().getPhotoUrl() == null) {
            Log.d("itt", "bad");
        } else {
            Log.d("itt", mAuth.getCurrentUser().getPhotoUrl().toString());
        }*/
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();

    }
    private void nameDialog(){
        final View item = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_name, null);
        new AlertDialog.Builder(AccountActivity.this)
                .setTitle("更改暱稱")
                .setView(item)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) item.findViewById(R.id.edit_text);
                        final String nameString = editText.getText().toString();
                        UserProfileChangeRequest profileUpdates
                                = new UserProfileChangeRequest.Builder().setDisplayName(nameString).build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(findViewById(R.id.activity_account), "更新成功", Snackbar.LENGTH_LONG).show();
                                            name.setText(nameString);
                                        } else {
                                            Snackbar.make(findViewById(R.id.activity_account), "failed", Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });

                        mDatabase.getReference("users/"+mAuth.getCurrentUser().getUid()+"/name").setValue(nameString);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK && (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA)) {
            final Uri selectedImage = imageReturnedIntent.getData();
            String filePath = SiliCompressor.with(this).compress(selectedImage.toString());//壓縮圖片
            Uri compressedImage = Uri.fromFile(new File(filePath));
            StorageReference riversRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+".jpg");
            riversRef.putFile(compressedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        @SuppressWarnings("VisibleForTests")
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            UserProfileChangeRequest profileUpdates
                                    = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build();
                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Snackbar.make(findViewById(R.id.activity_account), "更新成功", Snackbar.LENGTH_LONG).show();
                                                //Bitmap myBitmap = BitmapFactory.decodeFile(selectedImage.toString());
                                                image.setImageURI(selectedImage);
                                            } else {
                                                Snackbar.make(findViewById(R.id.activity_account), "failed", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            mDatabase.getReference("users/"+mAuth.getCurrentUser().getUid()+"/photoUrl").setValue(downloadUrl.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Snackbar.make(findViewById(R.id.activity_account), "failed", Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return true;
    }
}
