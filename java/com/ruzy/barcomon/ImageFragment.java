package com.ruzy.barcomon;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * Created by Ruzy on 2017/3/15.
 */

public class ImageFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private ImageView gallery;

    // newInstance constructor for creating fragment with arguments
    public static ImageFragment newInstance(int page, String title) {
        ImageFragment fragmentFirst = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        gallery = (ImageView) view.findViewById(R.id.Gallery);
        Uri imageUri = Uri.parse(title);
        Glide
                .with(gallery.getContext())
                .load(imageUri)
                //.thumbnail(Glide.with(gallery.getContext()).load(R.drawable.ball))//ball.GIF
                //.placeholder(R.drawable.ball)
                //.crossFade()
                .into(gallery);
        //gallery.setImageResource(R.mipmap.ic_toggle_star_outline_24);
        return view;
    }
}

