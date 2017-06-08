package com.example.ruzy.nd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruzy.nd.databaseModel.Image;
import com.example.ruzy.nd.databaseModel.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * reference collapsingtoolbarlayout animation:
 * http://stackoverflow.com/questions/35244455/collapsingtoolbarlayout-custom-contentscrim-similar-to-facebook
 * Created by Ruzy on 2017/3/11.
 */

public class ProductViewActivity extends AppCompatActivity {
    private static String TAG = "ProductViewActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    FragmentPagerAdapter adapterViewPager;
    FragmentPagerAdapter adapterImage;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private String barCode;
    private long image_count;
    private List<String> images = new ArrayList<>();

    private CollapsingToolbarLayout collapsingToolbar;
    private ViewPager gallery;
    private TextView p_name;
    private TextView p_avgStar;
    private Toolbar p_toolbar;
    private ImageView love_btn;
    private FloatingActionButton fab_comment;
    private FloatingActionButton fab_price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Date d = new Date();
        d.getTime();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        if(getIntent().hasExtra("barCode")) {
            barCode = getIntent().getStringExtra("barCode");
        } else {
            finish();
        }
        p_name = (TextView) findViewById(R.id.p_name);
        p_avgStar = (TextView) findViewById(R.id.p_avgStar);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comment);
        fab_price = (FloatingActionButton) findViewById(R.id.fab_price);
        p_toolbar = (Toolbar) findViewById(R.id.p_toolbar);
        gallery = (ViewPager) findViewById(R.id.vp_gallery);
        love_btn = (ImageView) findViewById(R.id.love_btn);
        love_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null) {
                    final DatabaseReference ref = database.getReference("users/" + mAuth.getCurrentUser().getUid() + "/love/" + barCode);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                love_btn.setImageResource(R.drawable.ic_star_border_light);
                                ref.removeValue();
                            } else {
                                Date d = new Date();
                                love_btn.setImageResource(R.drawable.ic_star_light);
                                ref.setValue(d.getTime());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    new AlertDialog.Builder(ProductViewActivity.this)
                            .setTitle("提示")
                            .setMessage("如要將商品加入最愛\n請先登入會員")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .show();
                }

            }
        });
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        setSupportActionBar(p_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        //collapsingToolbar.setTitle("MyTitle");
        //collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbar.setTitleEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        database.getReference("posts/"+barCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);
                p_toolbar.setTitle(p.name);
                p_name.setText(p.name);
                DecimalFormat df = new DecimalFormat("#.0");
                String s = df.format(p.avgStar);
                p_avgStar.setText(s);
                setStar((int)Math.round(p.avgStar));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        if(mAuth.getCurrentUser() != null) {
            final DatabaseReference ref = database.getReference("users/" + mAuth.getCurrentUser().getUid() + "/love/" + barCode);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        love_btn.setImageResource(R.drawable.ic_star_light);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(vpPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        fab_price.hide();
                        fab_comment.show();
                        break;
                    default:
                        fab_price.show();
                        fab_comment.hide();
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        getImages();

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()==null) {
                    Snackbar.make(findViewById(R.id.product_view), "請先登入", Snackbar.LENGTH_LONG).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference("comments/"+barCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                            new AlertDialog.Builder(ProductViewActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您已經留過評論囉~")
                                    .setPositiveButton("編輯", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(ProductViewActivity.this, AddCommentActivity.class);
                                            i.putExtra("barCode", barCode);
                                            i.putExtra("edit", true);
                                            startActivity(i);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        } else {
                            Intent i = new Intent(ProductViewActivity.this, AddCommentActivity.class);
                            i.putExtra("barCode", barCode);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }
        });
        fab_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()==null) {
                    Snackbar.make(findViewById(R.id.product_view), "請先登入", Snackbar.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(ProductViewActivity.this, AddPriceActivity.class);
                i.putExtra("barCode", barCode);
                startActivity(i);
            }
        });
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appBar);
        AppBarLayout.OnOffsetChangedListener mListener = new AppBarLayout.OnOffsetChangedListener() {
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int toolBarHeight = p_toolbar.getMeasuredHeight();
                int appBarHeight = collapsingToolbar.getMeasuredHeight();
                Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ( (float) appBarHeight - toolBarHeight)) * 255;
                int colorPrimary = ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null);
                p_toolbar.setBackgroundColor(colorPrimary);
                p_toolbar.getBackground().setAlpha(255 - Math.round(f));
                p_toolbar.setTitleTextColor(Color.argb(255 - Math.round(f),255,255,255));//Color.WHITE
            }
        };
        appBar.addOnOffsetChangedListener(mListener);
        collapsingToolbar.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
    }

    private void getImages() {
        database.getReference("images/"+barCode+"")
                .limitToFirst(5)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        image_count = dataSnapshot.getChildrenCount();
                        for(DataSnapshot data:dataSnapshot.getChildren()) {
                            Image i = data.getValue(Image.class);
                            images.add(i.storageUri);
                        }
                        if(image_count == 0) {
                            images.add("android.resource://com.example.ruzy.nd/"+R.mipmap.chih_product_default);
                            image_count = 1;
                        }
                        adapterImage = new ImagePagerAdapter(getSupportFragmentManager());
                        gallery.setAdapter(adapterImage);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void setStar(int n) {
        int i;
        String s = new String();
        for(i=0;i<n;i++) {
            s += "★";
        }
        for(;i<5;i++) {
            s += "☆";
        }
        p_avgStar.setText("  "+s+"  "+p_avgStar.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        //debug
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return;
        FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/userinformation/new")
               .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            if(dataSnapshot.child("monster").exists()) {
                                int number = dataSnapshot.child("monster").getValue(int.class);
                                ImageView imageView = new ImageView(ProductViewActivity.this);
                                imageView.setMaxHeight(500);
                                imageView.setMaxWidth(500);
                                imageView.setAdjustViewBounds(true);
                                switch(number) {
                                    case 0:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_bottle);break;
                                    case 1:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_can);break;
                                    case 2:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_mug);break;
                                    case 3:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_bento);break;
                                    case 4:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.monsterbox_book);break;
                                }
                                new AlertDialog.Builder(ProductViewActivity.this)
                                        .setTitle("獲得新怪獸")
                                        .setView(imageView)
                                        .setPositiveButton("確定", null)
                                        .show();
                            } else if(dataSnapshot.child("skill").exists()) {
                                int number = dataSnapshot.child("skill").getValue(int.class);
                                ImageView imageView = new ImageView(ProductViewActivity.this);
                                imageView.setMaxHeight(500);
                                imageView.setMaxWidth(500);
                                imageView.setAdjustViewBounds(true);
                                switch(number) {
                                    case 0:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.strongwater);break;
                                    case 1:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.sulfuric);break;
                                    case 2:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.paperplane);break;
                                    case 3:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.encyclopedia);break;
                                    case 4:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.compression);break;
                                    case 5:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.cansscroll);break;
                                    case 6:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.sweetsmell);break;
                                    case 7:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.rottenfood);break;
                                    case 8:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.hotwater);break;
                                    case 9:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.coffee);break;
                                    case 10:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.respectolder);break;
                                    case 11:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.loveteaching);break;
                                    case 12:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.notresigned);break;
                                    case 13:
                                        imageView.setImageResource(com.example.barcomon.R.drawable.deathattack);break;
                                }
                                new AlertDialog.Builder(ProductViewActivity.this)
                                        .setTitle("獲得新技能")
                                        .setView(imageView)
                                        .setPositiveButton("確定", null)
                                        .show();
                            }
                            dataSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_product_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            if(mAuth.getCurrentUser()==null) {
                Snackbar.make(findViewById(R.id.product_view), "請先登入", Snackbar.LENGTH_LONG).show();
                return false;
            }
            final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductViewActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        //PROFILE_PIC_COUNT = 1;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else if (items[item].equals("Choose from Library")) {
                        //PROFILE_PIC_COUNT = 1;
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,SELECT_FILE);
                    } else if (items[item].equals("Cancel")) {
                        //PROFILE_PIC_COUNT = 0;
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
            return true;
        } else if (id == R.id.action_report) {
            Intent i = new Intent(ProductViewActivity.this, ReportActivity.class);
            i.putExtra("barCode", barCode);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "reqCode"+requestCode+":resultCode"+resultCode);
        if(resultCode == RESULT_OK && (requestCode == REQUEST_CAMERA || requestCode == SELECT_FILE)) {
            Uri selectedImage = data.getData();
            String filePath = SiliCompressor.with(this).compress(selectedImage.toString());
            final Uri compressedImage = Uri.fromFile(new File(filePath));
            ImageView imageView = new ImageView(ProductViewActivity.this);
            imageView.setMaxHeight(500);
            imageView.setMaxWidth(500);
            imageView.setAdjustViewBounds(true);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), compressedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(ProductViewActivity.this)
                            .setTitle("上傳此張圖片嗎?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    upload(compressedImage);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setView(imageView);
            builder.create().show();
        }
    }

    private void upload(Uri image) {
        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("images/"+barCode+"/").push();
        final ProgressDialog dialog = ProgressDialog.show(ProductViewActivity.this, null, "上傳中...",true);
        String id = mDatabaseRef.getKey();
        final Image imageData = new Image("","ADMIN", FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Compress image
        String filePath = SiliCompressor.with(this).compress(image.toString());
        Uri compressedImage = Uri.fromFile(new File(filePath));

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("images/"+id);
                    UploadTask uploadTask = mStorageRef.putFile(compressedImage);
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Snackbar.make(findViewById(R.id.product_view), "Upload Failed.", Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            imageData.storageUri = downloadUrl.toString();
                            mDatabaseRef.setValue(imageData);
                            Log.d("uploadSuccess", downloadUrl.getPath());
                            dialog.dismiss();
                        }
                    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FragmentComment
                    return FragmentPrice.newInstance(0, barCode);
                case 1: // Fragment # 0 - This will show FragmentComment different title
                    return FragmentComment.newInstance(1, barCode);
                /*case 2: // Fragment # 1 - This will show SecondFragment
                    return SecondFragment.newInstance(2, "Page # 3");*/
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "價格";
            return "評論";
        }

    }


    public class ImagePagerAdapter extends FragmentPagerAdapter {
        private Long NUM_ITEMS = image_count;

        public ImagePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS.intValue();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FragmentComment
                    return ImageFragment.newInstance(0, images.get(0));
                case 1: // Fragment # 0 - This will show FragmentComment different title
                    return ImageFragment.newInstance(1, images.get(1));
                case 2: // Fragment # 0 - This will show FragmentComment different title
                    return ImageFragment.newInstance(2, images.get(2));
                case 3: // Fragment # 0 - This will show FragmentComment different title
                    return ImageFragment.newInstance(3, images.get(3));
                case 4: // Fragment # 0 - This will show FragmentComment different title
                    return ImageFragment.newInstance(4, images.get(4));
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "001";
            return "002";
        }

    }
}
