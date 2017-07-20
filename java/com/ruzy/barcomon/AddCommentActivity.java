package com.ruzy.barcomon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruzy.barcomon.databaseModel.Comment;

/**
 * Created by Ruzy on 2017/3/11.
 */

public class AddCommentActivity extends AppCompatActivity {

    private EditText title;
    private EditText body;
    private ImageView starImage[] = new ImageView[5];
    private Button submit;

    private String barCode;
    private String userUid;
    private int stars;
    private String categoryID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        barCode = getIntent().getStringExtra("barCode");
        title = (EditText) findViewById(R.id.add_comment_title);
        body = (EditText) findViewById(R.id.add_comment_body);
        starImage[0] = (ImageView) findViewById(R.id.add_comment_star1);
        starImage[1] = (ImageView) findViewById(R.id.add_comment_star2);
        starImage[2] = (ImageView) findViewById(R.id.add_comment_star3);
        starImage[3] = (ImageView) findViewById(R.id.add_comment_star4);
        starImage[4] = (ImageView) findViewById(R.id.add_comment_star5);
        submit = (Button) findViewById(R.id.add_comment_submit);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(getIntent().getBooleanExtra("edit", false)) {
            FirebaseDatabase
                    .getInstance()
                    .getReference("comments/"+barCode+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Comment previous = dataSnapshot.getValue(Comment.class);
                            body.setText(previous.body);
                            title.setText(previous.title);
                            setStar((int)previous.star);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }

        FirebaseDatabase.getInstance().getReference("posts/"+barCode+"/categoryID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryID = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().matches("")) {
                    Toast.makeText(AddCommentActivity.this, "標題不可留空", Toast.LENGTH_LONG).show();
                } else if (stars == 0) {
                    Toast.makeText(AddCommentActivity.this, "請評星數", Toast.LENGTH_LONG).show();
                } else {
                    Comment c = new Comment(title.getText().toString(), body.getText().toString(), stars);
                    FirebaseDatabase.getInstance().getReference("comments/"+barCode+"/")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(userUid)) {
                                        new manipulateUserInformation().plusEnergy(20);
                                        new manipulateUserInformation().staticsAdd(categoryID, 0);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    FirebaseDatabase.getInstance().getReference("comments/"+barCode+"/"+userUid).setValue(c);
                    finish();
                }
            }
        });
        starImage[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("star","star1");
                setStar(1);
            }
        });
        starImage[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("star","star2");
                setStar(2);
            }
        });
        starImage[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("star","star3");
                setStar(3);
            }
        });
        starImage[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("star","star4");
                setStar(4);
            }
        });
        starImage[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("star","star5");
                setStar(5);
            }
        });
    }

    private void setStar(int star) {
        if(star > this.stars) {
            for(int i=this.stars;i<star;i++) {
                //Log.i
                starImage[i].setImageResource(R.mipmap.ic_toggle_star_24);
            }
        } else {
            for(int i=star;i<this.stars;i++) {
                starImage[i].setImageResource(R.mipmap.ic_toggle_star_outline_24);
            }
        }
        this.stars = star;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
