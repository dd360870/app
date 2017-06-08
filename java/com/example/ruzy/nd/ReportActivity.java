package com.example.ruzy.nd;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ruzy.nd.databaseModel.Report;
import com.example.ruzy.nd.databinding.ActivityReportBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ruzy on 2017/5/28.
 */

public class ReportActivity extends AppCompatActivity {

    ActivityReportBinding binding;

    private String barCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        setTitle("問題回報");
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        barCode = getIntent().getStringExtra("barCode");
        binding.reportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new AlertDialog.Builder(ReportActivity.this)
                        .setTitle(binding.reportTitle.getText().toString())
                        .setMessage(barCode+"\n"+binding.reportContent.getText().toString())
                        .setPositiveButton("Yes", null)
                        .show();*/
                Report p = new Report(binding.reportTitle.getText().toString(),binding.reportContent.getText().toString());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("report").push();
                ref.setValue(p);
                finish();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        if((!binding.reportTitle.getText().toString().matches(""))||(!binding.reportContent.getText().toString().matches(""))) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("放棄已編輯的內容?")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            finish();
        }
    }
}
