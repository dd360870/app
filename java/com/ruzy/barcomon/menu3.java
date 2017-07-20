package com.ruzy.barcomon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruzy.barcomon.databaseModel.Post;

import java.util.ArrayList;

/**
 * Created by Ruzy on 2017/4/27.
 */

public class menu3 extends Fragment {

    private TextView no_content;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;

    ArrayList<Post> currentItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_menu_3, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("我的收藏");
        this.no_content = (TextView) view.findViewById(R.id.menu3_no_content);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view3);
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

                    }
                }));
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            getData();
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("請先登入或註冊帳號")
                    .setPositiveButton("確定", null)
                    .show();
        }
    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/love")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentItems.clear();
                        if(dataSnapshot.getChildrenCount() == 0) {
                            mAdapter.notifyDataSetChanged();
                            no_content.setVisibility(View.VISIBLE);
                        } else {
                            no_content.setVisibility(View.GONE);
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                getPost(d.getKey());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void getPost(String barCode) {
        FirebaseDatabase.getInstance().getReference("posts/"+barCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post p = dataSnapshot.getValue(Post.class);
                        currentItems.add(p);
                        mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }
}

