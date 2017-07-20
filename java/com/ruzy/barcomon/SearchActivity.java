package com.ruzy.barcomon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ruzy.barcomon.databaseModel.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ruzy on 2017/5/16.
 */

public class SearchActivity extends AppCompatActivity {

    private ArrayList<Post> currentItems = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private FloatingSearchView mSearchView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private String categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        this.mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        this.mLayoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        categoryID = getIntent().getStringExtra("categoryID");
        if(categoryID != null) {
            mDatabaseRef.child("CategoryByID/"+categoryID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        Post p = new Post();
                        p.name = d.child("name").getValue(String.class);
                        p.barcode = d.getKey();
                        currentItems.add(p);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

        mAdapter = new RecyclerViewAdapter(currentItems);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerClickListener(SearchActivity.this, mRecyclerView, new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(SearchActivity.this, ProductViewActivity.class);
                        i.putExtra("barCode", currentItems.get(position).barcode);
                        startActivity(i);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        //Toast.makeText(MainActivity2.this, "LONG CLICKED.", Toast.LENGTH_LONG).show();
                    }
                }));
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //animation
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                finish();
            }
        });
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                currentItems.clear();
                mAdapter.notifyDataSetChanged();
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("search/request");
                Map<String, Object> d = new HashMap<String, Object>();
                Map<String, Object> b = new HashMap<String, Object>();
                Map<String, Object> q = new HashMap<String, Object>();
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("name","*"+currentQuery+"*");
                q.put("match",m);
                b.put("query", q);
                //d.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                d.put("index", "firebase");
                d.put("type", "product");
                d.put("body", b);
                mDatabaseRef =  mDatabaseRef.push();
                String key = mDatabaseRef.getKey();

                mDatabaseRef.setValue(d);
                mDatabaseRef = FirebaseDatabase.getInstance()
                        .getReference("search/response/"+key+"/hits/hits");
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChildren()) {
                            //findViewById(R.id.noresult).setVisibility(View.VISIBLE);
                            return;
                        }
                        //findViewById(R.id.noresult).setVisibility(View.GONE);
                        for(DataSnapshot d:dataSnapshot.getChildren()) {
                            for(DataSnapshot d1:d.getChildren()) {
                                if(d1.getKey().matches("_source")) {
                                    Post p = d1.getValue(Post.class);
                                    if(categoryID != null) {
                                        if(!p.categoryID.matches(categoryID)) {
                                            continue;
                                        }
                                    }
                                    currentItems.add(d1.getValue(Post.class));
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if(item.getItemId()==R.id.activity_search_scan) {
                    new IntentIntegrator(SearchActivity.this).initiateScan();
                }
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

            }
        });
    }

    private boolean validEAN(String barCode) {
        if(barCode.length() != 13 || !android.text.TextUtils.isDigitsOnly(barCode)) {
            return false;
        }
        int evens = 0;
        int odds = 0;
        int checkSum;
        for(int i=0;i<12;i++) {
            if(i % 2 == 0) {
                evens+=Character.getNumericValue(barCode.charAt(i));
            } else {
                odds+=Character.getNumericValue(barCode.charAt(i));
            }
        }
        odds = odds * 3;
        int total = odds + evens;
        if(total % 10 == 0) {
            checkSum = 0;
        } else {
            checkSum = 10 - (total % 10);
        }
        return checkSum == Character.getNumericValue(barCode.charAt(12));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 49374:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Snackbar.make(findViewById(R.id.activity_search), "Scan cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        final String barCode = result.getContents();
                        if (validEAN(barCode)) {
                            FirebaseDatabase.getInstance().getReference("posts/"+barCode).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        Intent i = new Intent(SearchActivity.this, ProductViewActivity.class);
                                        i.putExtra("barCode",barCode);
                                        startActivity(i);
                                    } else {
                                        new AlertDialog.Builder(SearchActivity.this)
                                                .setTitle("提示")
                                                .setMessage("資料庫內無此商品\n是否要新增商品")
                                                .setPositiveButton("好啊", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(SearchActivity.this, AddProductActivity.class);
                                                        i.putExtra("barCode", barCode);
                                                        startActivity(i);
                                                        finish();
                                                    }})
                                                .setNegativeButton("不要", null)
                                                .show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                } else {
                    // This is important, otherwise the result will not be passed to the fragment
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                break;
        }
    }
}
