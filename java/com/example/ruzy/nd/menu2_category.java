package com.example.ruzy.nd;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruzy.nd.databaseModel.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ID3 {
    public String name;
    public String ID;
    public ID3(String name, String categoryID) {
        this.name = name;
        this.ID = categoryID;
    }
}

public class menu2_category extends AppCompatActivity {
    List<String> groups = new ArrayList<>();
    List<List<ID3>> childs = new ArrayList<>();
    ExpandableAdapter viewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ExpandableListView elv = (ExpandableListView) findViewById(R.id.expandableListView);
        viewAdapter = new ExpandableAdapter(this, groups, childs);
        String Layer1 = getIntent().getStringExtra("categoryID");
            switch(Layer1) {
                case "A01": Layer1 = "食品"; viewAdapter.imageResId = R.drawable.category_food_small; break;
                case "A02": Layer1 = "日常用品"; viewAdapter.imageResId = R.drawable.category_daily_supplies_small; break;
                case "A03": Layer1 = "休閒娛樂"; viewAdapter.imageResId = R.drawable.category_entertainment_small; break;
                case "A04": Layer1 = "男服飾"; viewAdapter.imageResId = R.drawable.category_clothes_man_small; break;
                case "A05": Layer1 = "女服飾"; viewAdapter.imageResId = R.drawable.category_clothes_women_small; break;
                case "A06": Layer1 = "運動用品"; viewAdapter.imageResId = R.drawable.category_sport_goods_small; break;
                case "A07": Layer1 = "家具"; viewAdapter.imageResId = R.drawable.category_furniture_small; break;
                case "A08": Layer1 = "書籍文具"; viewAdapter.imageResId = R.drawable.category_book_small; break;
                case "A09": Layer1 = "電器3C"; viewAdapter.imageResId = R.drawable.category_electrical_appliances_small; break;
            }
        FirebaseDatabase.getInstance()
                .getReference("CategoryAsset/"+Layer1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot layer2 : dataSnapshot.getChildren()) {
                    groups.add(layer2.getKey());
                    List<ID3> temp = new ArrayList<>();
                    temp.clear();
                    for(DataSnapshot layer3 : layer2.getChildren()) {
                        temp.add(new ID3(layer3.getKey(), layer3.child("ID").getValue(String.class)));
                    }
                    childs.add(temp);
                }
                viewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        elv.setAdapter(viewAdapter);
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            int selectChildPos;
            int selectParentPos;
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view,int parentPos, int childPos, long l) {
                selectChildPos=childPos;
                selectParentPos=parentPos;
                //Toast.makeText(menu2_category.this, childs.get(parentPos).get(childPos).ID, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(menu2_category.this, SearchActivity.class);
                i.putExtra("categoryID", childs.get(parentPos).get(childPos).ID);
                startActivity(i);
                return true;
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
 class ExpandableAdapter extends BaseExpandableListAdapter {
     private Context context;
     private List<String> groups;
     private List<List<ID3>> childs;
     public int imageResId;

     public ExpandableAdapter(Context context, List<String> groups, List<List<ID3>> childs) {
         this.groups = groups;
         this.childs = childs;
         this.context = context;
     }

     public Object getChild(int groupPosition, int childPosition) {
         return childs.get(groupPosition).get(childPosition);
     }

     public long getChildId(int groupPosition, int childPosition) {
         return childPosition;
     }

     // 獲取二級清單的View物件
     public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
         LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

         LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.category_child_item, null);
         TextView textView = (TextView) linearLayout.findViewById(R.id.child_text);
         textView.setText(childs.get(groupPosition).get(childPosition).name);
         return linearLayout;
     }

     public int getChildrenCount(int groupPosition) {
         return childs.get(groupPosition).size();
     }

     public Object getGroup(int groupPosition) {
         return groups.get(groupPosition);
     }

     public int getGroupCount() {
         return groups.size();
     }

     public long getGroupId(int groupPosition) {
         return groupPosition;
     }

     //獲取一級清單View物件
     public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
         String text = groups.get(groupPosition);

         /*LinearLayout ll = new LinearLayout(context);
         ll.setOrientation(LinearLayout.VERTICAL);
         ImageView logo = new ImageView(context);
         logo.setImageResource(imageResId);
         ll.addView(logo);
         TextView textView = getTextView();//调用定义的getTextView()方法
         textView.setText(getGroup(groupPosition).toString());//添加数据
         ll.addView(textView);
         return ll;*/
         LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

         //獲取一級清單佈局檔,設置相應元素屬性
         LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.category_group_item, null);
         TextView textView = (TextView) linearLayout.findViewById(R.id.group_text);
         ImageView imageView = (ImageView) linearLayout.findViewById(R.id.group_image);
         imageView.setImageResource(imageResId);
         imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
         textView.setText(getGroup(groupPosition).toString());

         return linearLayout;
     }

     public boolean hasStableIds() {
         return false;
     }

     public boolean isChildSelectable(int groupPosition, int childPosition) {
         return true;
     }
 }