package com.ruzy.barcomon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Ruzy on 2017/4/27.
 */

public class menu2 extends Fragment {

    private ImageButton a01,a02,a03,a04,a05,a06,a07,a08,a09;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_menu_2, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("商品分類");
        a01 = (ImageButton) view.findViewById(R.id.A01);
        a01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A01");
                startActivity(i);
            }
        });
        a02 = (ImageButton) view.findViewById(R.id.A02);
        a03 = (ImageButton) view.findViewById(R.id.A03);
        a04 = (ImageButton) view.findViewById(R.id.A04);
        a05 = (ImageButton) view.findViewById(R.id.A05);
        a06 = (ImageButton) view.findViewById(R.id.A06);
        a07 = (ImageButton) view.findViewById(R.id.A07);
        a08 = (ImageButton) view.findViewById(R.id.A08);
        a09 = (ImageButton) view.findViewById(R.id.A09);
        a02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A02");
                startActivity(i);
            }
        });
        a03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A03");
                startActivity(i);
            }
        });
        a04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A04");
                startActivity(i);
            }
        });
        a05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A05");
                startActivity(i);
            }
        });
        a06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A06");
                startActivity(i);
            }
        });
        a07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A07");
                startActivity(i);
            }
        });
        a08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A08");
                startActivity(i);
            }
        });
        a09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), menu2_category.class);
                i.putExtra("categoryID","A09");
                startActivity(i);
            }
        });
    }
}

