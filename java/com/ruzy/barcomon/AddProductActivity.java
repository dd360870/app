package com.ruzy.barcomon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ruzy.barcomon.databaseModel.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

/**
 * Created by Ruzy on 2017/5/9.
 */

public class AddProductActivity extends AppCompatActivity {

    private String categoryID;

    private EditText editText_barCode;
    private EditText editText_name;
    private Button scan;
    private Button submit;
    private Spinner spinner1, spinner2, spinner3;
    private ArrayList<String> list1,list2,list3;
    private ArrayAdapter<String> s1,s2,s3;
    private FirebaseDatabase mDatabase;
    private int p1,p2,p3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance();
        editText_barCode = (EditText) findViewById(R.id.barcode);
        editText_name = (EditText) findViewById(R.id.product_name);
        scan = (Button) findViewById(R.id.scan_barcode);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(AddProductActivity.this).initiateScan();
            }
        });
        String barCode = getIntent().getStringExtra("barCode");
        if(barCode != null) {
            editText_barCode.setText(barCode);
        }
        submit = (Button) findViewById(R.id.product_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editText_name.getText().toString();
                final String barCode = editText_barCode.getText().toString();
                if(!validEAN(barCode)) {
                    new AlertDialog.Builder(AddProductActivity.this)
                            .setTitle("Warning")
                            .setMessage("條碼有誤")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .show();
                    return;
                }
                if(name.matches("")) {
                    editText_name.setError("不能為空");
                    return;
                }
                if(categoryID.matches("")) {
                    new AlertDialog.Builder(AddProductActivity.this)
                            .setTitle("Warning")
                            .setMessage("請選擇正確分類")
                            .setPositiveButton("確定", null)
                            .show();
                    return;
                }
                mDatabase.getReference("posts/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(barCode)) {
                            new AlertDialog.Builder(AddProductActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("此商品已存在")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }})
                                    .show();
                        } else {
                            AddProduct(barCode, name);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
        spinner1 = (AppCompatSpinner) findViewById(R.id.spinner1);
        spinner2 = (AppCompatSpinner) findViewById(R.id.spinner2);
        spinner3 = (AppCompatSpinner) findViewById(R.id.spinner3);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        s1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list1);
        s2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list2);
        s3 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list3);
        spinner1.setAdapter(s1);
        spinner2.setAdapter(s2);
        spinner3.setAdapter(s3);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    list2.clear();
                    list2.add("  --  ");
                    list3.clear();
                    list3.add("  --  ");
                    s3.notifyDataSetChanged();
                    return;
                }
                p1 = position;
                spinner2.setSelection(0);
                spinner3.setSelection(0);
                mDatabase.getReference("CategoryAsset/"+list1.get(p1)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list2.clear();
                        list2.add("  --  ");
                        for(DataSnapshot d:dataSnapshot.getChildren()) {
                            list2.add(d.getKey());
                        }
                        s2.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    return;
                }
                p2 = position;
                spinner3.setSelection(0);
                mDatabase.getReference("CategoryAsset/"+list1.get(p1)+"/"+list2.get(p2)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list3.clear();
                        list3.add("  --  ");
                        for(DataSnapshot d:dataSnapshot.getChildren()) {
                            list3.add(d.getKey());
                        }
                        s3.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    categoryID = "";
                    return;
                }
                p3 = position;
                mDatabase.getReference("CategoryAsset/"+list1.get(p1)+"/"+list2.get(p2)+"/"+list3.get(p3)+"/ID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryID = dataSnapshot.getValue(String.class);
                        new AlertDialog.Builder(AddProductActivity.this)
                                .setMessage(dataSnapshot.getValue(String.class))
                                .show();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mDatabase.getReference("CategoryAsset/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list1.clear();
                list1.add("    --    ");
                list2.clear();
                list2.add("    --    ");
                list3.clear();
                list3.add("    --    ");
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    list1.add(d.getKey());
                }
                s1.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void AddProduct(final String barCode, String name) {
        final ProgressDialog dialog = ProgressDialog.show(AddProductActivity.this, null, "讀取中...",true);
        Post p = new Post(name, barCode);
        p.categoryID = this.categoryID;
        mDatabase.getReference("CategoryByID/"+categoryID+"/"+barCode+"/name").setValue(name);

        mDatabase.getReference("posts/"+barCode).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()) {

                    Intent i = new Intent(AddProductActivity.this, ProductViewActivity.class);
                    i.putExtra("barCode", barCode);
                    new manipulateUserInformation().plusEnergy(40);
                    new manipulateUserInformation().staticsAdd(categoryID ,1);
                    startActivity(i);
                    finish();
                } else {
                    new AlertDialog.Builder(AddProductActivity.this)
                            .setMessage("不明的錯誤")
                            .setPositiveButton("取消",null)
                            .show();
                }
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
        //Log.d("onActivityResult", "reqCode"+requestCode+":resultCode"+resultCode);
        switch(requestCode) {
            case 49374:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Snackbar.make(findViewById(R.id.activity_add_product), "Scan cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        String barCode = result.getContents().toString();
                        String format = result.getFormatName();
                        if (format.matches("EAN_13") || format.matches("EAN_8")) {
                            editText_barCode.setText(barCode);
                            return;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
