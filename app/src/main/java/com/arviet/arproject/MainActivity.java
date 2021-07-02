package com.arviet.arproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnDangNhap = (Button) findViewById(R.id.btn_dangnhap);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("ACCESS_TOKEN",null);
        if (accessToken!=null){
            startActivity(new Intent(MainActivity.this,HomePageActivity.class));
        }else{
            btnDangNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,LoginAcitivity.class));
                }
            });
            Button btnDangKy = (Button) findViewById(R.id.btn_dangky);
            btnDangKy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                }
            });
        }
    }
}