package com.ruzy.barcomon;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruzy.barcomon.databaseModel.Post;
import com.ruzy.barcomon.databaseModel.Price;

import java.util.List;

/**
 * Created by Ruzy on 2017/3/10.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    private List<Post> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card_view;

        public ImageView c_image;
        public TextView c_name;
        public TextView c_price;

        public ViewHolder(View v) {
            super(v);
            card_view = (CardView) v.findViewById(R.id.card_view);
            c_image = (ImageView) v.findViewById(R.id.c_image);
            c_name = (TextView) v.findViewById(R.id.c_name);
            c_price = (TextView) v.findViewById(R.id.c_price);
        }
    }

    public RecyclerViewAdapter(List<Post> items) { mDataset = items; }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.c_name.setText(mDataset.get(position).name);
        holder.c_price.setText(" -- ");
        FirebaseDatabase.getInstance()
                .getReference("images/"+mDataset.get(position).barcode)
                .orderByChild("time")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Uri image= Uri.EMPTY;
                        if(!dataSnapshot.exists()) {
                            //holder.c_image.setImageResource(R.mipmap.ic_image);
                            //Log.d("imageNotExist",mDataset.get(position).name);
                            return;
                        }
                        for(DataSnapshot d:dataSnapshot.getChildren()) {
                            image = Uri.parse(d.child("storageUri").getValue(String.class));
                        }
                        Glide
                                .with(holder.c_image.getContext())
                                .load(image)
                                //.thumbnail(Glide.with(holder.c_image.getContext()).load(R.drawable.ball))
                                .crossFade()
                                .into(holder.c_image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        /*if(mDataset.get(position).imageUri != null) {
            Uri image = Uri.parse(mDataset.get(position).imageUri);
            Glide
                    .with(holder.c_image.getContext())//c_image.context
                    .load(image)
                    //.centerCrop()
                    .placeholder(R.mipmap.ic_toggle_star_24)
                    .crossFade()
                    .into(holder.c_image);
        }*/
        FirebaseDatabase.getInstance().getReference("prices/"+mDataset.get(position).barcode)
                .orderByChild("time")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren()) {
                            Price p = d.getValue(Price.class);
                            holder.c_price.setText(p.price);
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
