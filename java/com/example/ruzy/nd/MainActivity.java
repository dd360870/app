package com.example.ruzy.nd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment currentFragment = null;

    private View headerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton game = (FloatingActionButton) findViewById(R.id.game);
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new manipulateUserInformation().staticsAdd("0017",1, MainActivity.this);//daily
                new manipulateUserInformation().staticsAdd("0121",1, MainActivity.this);//food
                new manipulateUserInformation().staticsAdd("0092",1, MainActivity.this);//book*/
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("請先登入以進入遊戲")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .show();
                    return;
                }
                startActivity(new Intent(getApplicationContext(), com.example.barcomon.BarCoMonGameConsole.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            currentFragment = new menu1();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, currentFragment).commit();
        }

        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        refreshNavHeader();
    }

    public void sendNotification(String message) {

        try {

            // -- 新的寫法
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE );
            Notification.Builder builder = new Notification.Builder(MainActivity.this );
            PendingIntent contentIndent = PendingIntent.getActivity(
                    MainActivity. this, 0, new Intent(MainActivity.this,
                            MainActivity. class),
                    PendingIntent. FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIndent)
                    .setSmallIcon(R.drawable.ic_action_comment)
                    .setTicker(message) // 設置狀態列的顯示的資訊
                    .setWhen(System. currentTimeMillis())// 設置時間發生時間
                    .setAutoCancel( false) // 設置可以清除
                    .setContentTitle( "Notification ") // 設置下拉清單裡的標題
                    .setContentText(message); // 設置上下文內容

            Notification notification = builder.getNotification();
            // 後面的設定會蓋掉前面的


            // 加i是為了顯示多條Notification
            notificationManager.notify(1, notification);
            // --
        } catch (Exception e) {

        }
    }

    public void refreshNavHeader() {
        TextView userName = (TextView) headerLayout.findViewById(R.id.user_name);
        TextView userEmail = (TextView) headerLayout.findViewById(R.id.user_email);
        ImageView image = (ImageView) headerLayout.findViewById(R.id.user_image);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            if(user.getPhotoUrl() != null) {
                Glide
                        .with(this)
                        .load(user.getPhotoUrl())
                        .centerCrop()
                        .into(image);
            } else {
                image.setImageResource(R.mipmap.ic_account_circle);
            }
        } else {
            userName.setText("尚未登入");
            userEmail.setText("");
            image.setImageResource(R.mipmap.ic_account_circle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNavHeader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //sendNotification("AABBAAB");
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_new) {
            currentFragment = new menu1();
        } else if (id == R.id.nav_category) {
            currentFragment = new menu2();
        } else if (id == R.id.nav_star) {
            currentFragment = new menu3();
        } else if (id == R.id.nav_add) {
            Intent i = new Intent(this, AddProductActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_account) {
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_config) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if (currentFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, currentFragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
