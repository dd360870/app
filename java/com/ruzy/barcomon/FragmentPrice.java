package com.ruzy.barcomon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruzy.barcomon.databaseModel.Price;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ruzy on 2017/3/15.
 */

public class FragmentPrice extends Fragment {
    // Store instance variables
    private String barCode;
    private int page;

    private List<Price> currentItems = new ArrayList<>();

    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView noContent;

    // newInstance constructor for creating fragment with arguments
    public static FragmentPrice newInstance(int page, String barCode) {
        FragmentPrice fragmentPrice = new FragmentPrice();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("barcode", barCode);
        fragmentPrice.setArguments(args);

        return fragmentPrice;
    }

    public void setFirebaseValueListener() {
        mDatabase = FirebaseDatabase.getInstance().getReference("prices/"+barCode);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentItems.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Price item = itemSnapshot.getValue(Price.class);
                    currentItems.add(item);
                }
                Collections.sort(currentItems, new Comparator<Price>() { // 排序 新到舊
                    @Override
                    public int compare(Price o1, Price o2) {
                        return (int) (o2.time-o1.time);//return>0,o2first;return<0,o1first
                    }
                });
                if(currentItems.size()==0) {
                    noContent.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        barCode = getArguments().getString("barcode");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comment);
        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new mRecyclerViewAdapter(currentItems);
        mRecyclerView.setAdapter(mAdapter);
        setFirebaseValueListener();
        this.noContent = (TextView) view.findViewById(R.id.price_empty);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerClickListener(FragmentPrice.this.getContext(), mRecyclerView,  new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(FragmentPrice.this.getContext(), MapActivity.class);
                        i.putExtra("barCode", barCode);
                        i.putExtra("id", currentItems.get(position).mapId);
                        startActivity(i);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                }));
        return view;
    }

    public class mRecyclerViewAdapter extends RecyclerView.Adapter<mRecyclerViewAdapter.ViewHolder>  {
        private List<Price> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView rowPrice_place;
            public TextView rowPrice_price;//"$ 130"
            public TextView rowPrice_user;//"by $USERNAME"

            public ViewHolder(View v) {
                super(v);
                rowPrice_place = (TextView) v.findViewById(R.id.rowPrice_place);
                rowPrice_price = (TextView) v.findViewById(R.id.rowPrice_price);
                rowPrice_user = (TextView) v.findViewById(R.id.rowPrice_user);
            }
        }

        public mRecyclerViewAdapter(List<Price> items) { mDataset = items; }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_price, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.rowPrice_place.setText(mDataset.get(position).mapName);
            holder.rowPrice_price.setText("$ "+mDataset.get(position).price);
            FirebaseDatabase.getInstance().getReference("users/"+mDataset.get(position).by_uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String time = new SimpleDateFormat("yyyy/MM/dd").format(new Date(mDataset.get(position).time));
                            if(dataSnapshot.child("name").exists()) {
                                holder.rowPrice_user.setText(time+" by "+dataSnapshot.child("name").getValue(String.class));
                            } else {
                                String id = dataSnapshot.child("email").getValue(String.class);
                                id = id.substring(0,id.indexOf('@'));
                                holder.rowPrice_user.setText(time+" by "+id);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
