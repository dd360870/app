package com.ruzy.barcomon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruzy.barcomon.databaseModel.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ruzy on 2017/4/27.
 */

public class menu1 extends Fragment {

    boolean isLoading;
    private Handler handler = new Handler();

    private DatabaseReference mDatabaseRef;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Post> allItems = new ArrayList<>();
    private List<Post> currentItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_menu_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("最新動態");
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.CYAN);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentItems.clear();
                        setFirebaseValueListener();
                    }
                }, 2000);
            }
        });
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        this.mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(currentItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //animation
        mRecyclerView.addOnItemTouchListener(
                new RecyclerClickListener(getContext(), mRecyclerView, new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(getContext(), ProductViewActivity.class);
                        i.putExtra("barCode", currentItems.get(position).barcode);
                        startActivity(i);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        //Toast.makeText(MainActivity2.this, "LONG CLICKED.", Toast.LENGTH_LONG).show();
                    }
                }));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(lastVisibleItemPosition+1 == mAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                    if(isRefreshing) {
                        //setFirebaseValueListener();
                        return;
                    }
                    if(!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNew();
                                //mAdapter.notifyDataSetChanged();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        setFirebaseValueListener();
    }

    public void loadNew() {
        //Log.d("AA_LOADNEW", allItems.size()+","+currentItems.size()+"");
        int stop = currentItems.size()+6;
        for(int i=currentItems.size();i<stop;i++) {
            if(i < allItems.size()) {
                currentItems.add(allItems.get(i));
            } else
                return;
        }
        //Log.d("BB_LOADNEW", allItems.size()+","+currentItems.size()+"");
        mAdapter.notifyItemRangeInserted(stop-6,6);
    }

    public void setFirebaseValueListener() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("posts");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allItems.clear();
                currentItems.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Post item = itemSnapshot.getValue(Post.class);
                    allItems.add(item);
                    Log.i("item",item.name);
                }
                Collections.sort(allItems, new Comparator<Post>() { // 排序 新到舊
                    @Override
                    public int compare(Post o1, Post o2) {
                        //return (o2.time-o1.time)>(long)0?1:-1;//return>0,o2first;return<0,o1first
                        return (o2.time>o1.time)?1:-1;
                    }
                });
                loadNew();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
}
