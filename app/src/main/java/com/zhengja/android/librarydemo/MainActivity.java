package com.zhengja.android.librarydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhengja.android.comonlib.CommonUtil;
import com.zhengja.android.comonlib.Tasty;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text);
        String deviceBrand = CommonUtil.getDeviceBrand();
        textView.setText(deviceBrand);
        Tasty.s(deviceBrand);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException();
            }
        });
    }
}
