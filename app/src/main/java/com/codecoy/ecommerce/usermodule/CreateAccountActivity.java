package com.codecoy.ecommerce.usermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codecoy.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {


    private TextInputEditText create_email, create_name;
    private MaterialButton create_btn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progbar;
    private FirebaseUser firebaseUser;
    private String alreadyuser;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        init();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Getting Data");
        progressDialog.setMessage("Please wait......");
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });


    }



    private void init() {

        create_email = findViewById(R.id.create_email);
        create_name = findViewById(R.id.create_name);
        create_btn = findViewById(R.id.create_btn);
        progbar = findViewById(R.id.progbar);
    }

    //------------------------------New Registration_________________________________//

    private void registerNewUser() {
        final String name,email ;
        name = create_name.getText().toString().trim();
        email = create_email.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter Name ...", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Email ...", Toast.LENGTH_LONG).show();
            return;
        }

        else {
            progbar.setVisibility(View.VISIBLE);

                                String userid=firebaseUser.getUid();
                                saveUser(name,userid,email);



        }



    }

    private void saveUser( String name, String userid, String email) {
        progbar.setVisibility(View.VISIBLE);
        HashMap<String,Object> hashMap = new HashMap<>();

        //user
        hashMap.put("id",userid);
        hashMap.put("username",name);
        hashMap.put("userphoneno",firebaseUser.getPhoneNumber());
        hashMap.put("useremail",email);
        hashMap.put("alreadyuser","true");
        db.collection("Users").document(userid)
                .set(hashMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(CreateAccountActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateAccountActivity.this, Main_Cat_Activity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(CreateAccountActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
getUserData();
        //updateUI(currentUser);
    }
    private void getUserData() {
        progressDialog.show();
        db.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progressDialog.dismiss();
                alreadyuser=documentSnapshot.getString("alreadyuser");
                if (alreadyuser!=null){
                    if (alreadyuser.equals("true")){
                        Intent intent = new Intent(CreateAccountActivity.this, Main_Cat_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

}
