package com.example.ruzy.nd;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ruzy.nd.databaseModel.Comment;
import com.example.ruzy.nd.databaseModel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ruzy on 2017/3/11.
 */

public class FragmentComment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private List<Comment> currentItems = new ArrayList<>();

    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView noContent;

    // newInstance constructor for creating fragment with arguments
    public static FragmentComment newInstance(int page, String title) {
        FragmentComment fragmentFirst = new FragmentComment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    public void setFirebaseValueListener() {
        mDatabase = FirebaseDatabase.getInstance().getReference("comments/"+title);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentItems.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Comment item = itemSnapshot.getValue(Comment.class);
                    item.uid = itemSnapshot.getKey();
                    currentItems.add(item);
                    Log.i("item",item.title);
                }
                Collections.sort(currentItems, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment o1, Comment o2) {
                        return ((o2.time-o1.time)>(long)0)?1:-1;
                    }
                });
                if(currentItems.size()==0) {
                    noContent.setVisibility(View.VISIBLE);
                    //mRecyclerView.setVisibility(View.INVISIBLE);
                }
                mAdapter = new mRecyclerViewAdapter(currentItems, title);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError Error) {

            }
        });
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comment);
        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        setFirebaseValueListener();
        this.noContent = (TextView) view.findViewById(R.id.comment_empty);
        /*TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        tvLabel.setText(page + " -- " + title);*/
        return view;
    }

    public class mRecyclerViewAdapter extends RecyclerView.Adapter<mRecyclerViewAdapter.ViewHolder>  {
        private String barCode;
        private List<Comment> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView post_author_photo;
            public TextView post_author;
            public TextView post_title;
            public TextView post_body;
            public TextView post_num_star;
            public TextView post_star;
            public Button post_agree;

            public ViewHolder(View v) {
                super(v);
                post_author = (TextView) v.findViewById(R.id.post_author);
                post_title = (TextView) v.findViewById(R.id.post_title);
                post_body = (TextView) v.findViewById(R.id.post_body);
                post_num_star = (TextView) v.findViewById(R.id.post_num_stars);
                post_star = (TextView) v.findViewById(R.id.post_star);
                post_author_photo = (ImageView) v.findViewById(R.id.post_author_photo);
                post_agree = (Button) v.findViewById(R.id.row_comment_agree);
            }

            public void setStar(int n) {
                int i;
                String s = new String();
                for(i=0;i<n;i++) {
                    s += "★";
                }
                for(;i<5;i++) {
                    s += "☆";
                }
                post_star.setText(s);
            }
        }

        public mRecyclerViewAdapter(List<Comment> items, String barCode) { mDataset = items; this.barCode = barCode;}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_comment, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            /*
            if(mDataset.get(position).photoUrl != null) {
                Glide
                        .with(FragmentComment.this)
                        .load(Uri.parse(mDataset.get(position).photoUrl))
                        .centerCrop()
                        .into(holder.post_author_photo);
            }
            holder.post_author.setText(mDataset.get(position).author);
            */
            FirebaseDatabase.getInstance().getReference("users/"+mDataset.get(position).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.photoUrl != null) {
                        Glide
                                .with(FragmentComment.this)
                                .load(Uri.parse(user.photoUrl))
                                .centerCrop()
                                .into(holder.post_author_photo);
                    }
                    holder.post_author.setText(user.name);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            holder.post_title.setText(mDataset.get(position).title);
            holder.post_body.setText(mDataset.get(position).body);
            //holder.post_num_star.setText(String.format(Locale.ENGLISH,"%d", mDataset.get(position).star));
            holder.setStar((int)mDataset.get(position).star);
            FirebaseDatabase.getInstance().getReference("comments/"
                    +barCode+"/"
                    +mDataset.get(position).uid
                    +"/agree").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long n = dataSnapshot.getChildrenCount();
                    holder.post_num_star.setText(n==0?"":String.valueOf(n));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                FirebaseDatabase.getInstance().getReference("comments/"
                        + barCode + "/"
                        + mDataset.get(position).uid
                        + "/agree/"
                        + FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    holder.post_agree.setBackgroundColor(Color.BLACK);
                                    holder.post_agree.setTextColor(Color.WHITE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
            holder.post_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("請先登入")
                                .setPositiveButton("確定", null)
                                .show();
                        return;
                    }
                    final DatabaseReference refAgree = FirebaseDatabase.getInstance().getReference("comments/"
                            +barCode+"/"
                            +mDataset.get(position).uid
                            +"/agree/"
                            +FirebaseAuth.getInstance().getCurrentUser().getUid());

                            refAgree.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        refAgree.removeValue();
                                    } else {
                                        refAgree.setValue(new java.util.Date().getTime());
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
