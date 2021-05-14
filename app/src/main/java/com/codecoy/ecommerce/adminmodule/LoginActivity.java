package com.codecoy.ecommerce.adminmodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codecoy.ecommerce.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText signin_username, signin_password;
    private MaterialButton signin_btn;
    final String _username = "Admin";
    final String _pass = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        init();
        String loginstate = Utils.getPreferences("Login", this);
        if (loginstate != null) {
            if (loginstate.equals("true")) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        }
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
    }

    private void loginUserAccount() {

        String username, password;
        username = signin_username.getText().toString().trim();
        password = signin_password.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter Username ...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();
            return;
        } else if (username.equals(_username) && password.equals(_pass)) {
            Utils.savePreferences("Login", "true", this);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

        }

    }

    private void init() {

        signin_password = findViewById(R.id.signin_password);
        signin_username = findViewById(R.id.signin_username);
        signin_btn = findViewById(R.id.signin_btn);
    }

}
