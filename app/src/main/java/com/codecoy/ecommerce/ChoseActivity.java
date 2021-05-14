package com.codecoy.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.LoginActivity;

public class ChoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);

        findViewById(R.id.btn_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoseActivity.this, LoginActivity.class));
            }
        });


        findViewById(R.id.btn_admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoseActivity.this, com.codecoy.ecommerce.adminmodule.LoginActivity.class));
            }
        });
    }
}