package com.codecoy.ecommerce.usermodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.model.Country;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText numberEdit, codeEdit;
    Button continueBtn;

    private String mVerificationId, Phone_number;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    FirebaseAuth mAuth;
    FirebaseUser  firebaseUser;


    ProgressDialog progressDialog;

    private ArrayList<Country> countries, temp;
    private ArrayAdapter<Country> myAdapter;
    private AppCompatSpinner spinner;

    //Google
    private int RC_SIGN_IN = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "jeuxdevelopers.com.edo4all",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", "Keyyyy : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging you in");
        progressDialog.setMessage("Please Wait while we log you in");
        progressDialog.setCancelable(false);

        numberEdit = findViewById(R.id.number_edit_login);
        codeEdit = findViewById(R.id.code_edit_login);
        codeEdit.setVisibility(View.GONE);
        continueBtn = findViewById(R.id.continue_btn_login);

        spinner = findViewById(R.id.code_spinner);




        //SetUp Country Code
        SetUpCoutryCodes();


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner.getSelectedItem() == null) {
                    Toast.makeText(LoginActivity.this, "Select Country Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (numberEdit.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Input Your Number first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Phone_number = ((Country) spinner.getSelectedItem()).getDailCode() + numberEdit.getText().toString().replaceAll("\\s+", "");
                if (!Patterns.PHONE.matcher(Phone_number).matches()) {
                    Toast.makeText(LoginActivity.this, "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(numberEdit.getText().toString());
                builder.setMessage("One time password will be sent on this number! Continue with this number?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        initiateAuth(Phone_number);
                        progressDialog.show();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        numberEdit.requestFocus();
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        codeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    if (s.length() == 6) {
                        progressDialog.show();
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, s.toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }




    private void SetUpCoutryCodes() {

        countries = getCountries();
        temp = getCountries();
        if (countries != null) {
            spinner.setVisibility(View.VISIBLE);
            myAdapter = new ArrayAdapter<Country>(this, R.layout.item_country_spinner, countries);
            spinner.setAdapter(myAdapter);
        }

    }

    private ArrayList<Country> getCountries() {
        ArrayList<Country> toReturn = new ArrayList<>();

        try {
            JSONArray countrArray = new JSONArray(readEncodedJsonString());
            toReturn = new ArrayList<>();
            for (int i = 0; i < countrArray.length(); i++) {
                JSONObject jsonObject = countrArray.getJSONObject(i);
                String countryName = jsonObject.getString("name");
                String countryDialCode = jsonObject.getString("dial_code");
                String countryCode = jsonObject.getString("code");

                Log.d("country",countryName);
                Log.d("country",countryDialCode);
                Log.d("country",countryCode);
                Country country = new Country(countryCode, countryName, countryDialCode);
                toReturn.add(country);
            }
            //Country country = new Country("IN", "India", "+91");
         //  Country country = new Country("PAK", "Pakistan", "+92");


            Collections.sort(toReturn, new Comparator<Country>() {
                @Override
                public int compare(Country lhs, Country rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    private String readEncodedJsonString() throws IOException {
        String base64 = getString(R.string.code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    //End Country Code


    private void Initiate_Google_Signin() {

       /* try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("384944924740-7ugpvurf6430kubvd50sunu1epcj1139.apps.googleusercontent.com")
                    .requestEmail()
                    .build();

            mGoogleSignInClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        } catch (Exception e){
            e.printStackTrace();
        }*/
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        try {
            mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, String.format("Something went wrong. ", e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();

            Toast.makeText(LoginActivity.this, String.format("Something went wrong. ", e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
        }
    }



    private void initiateAuth(String phone) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeAutoRetrievalTimeOut(String s) {
                            super.onCodeAutoRetrievalTimeOut(s);
                        }

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            //progressDialog.dismiss();
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                            progressDialog.show();
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            //progressDialog.dismiss();
                            //verificationMessage.setText(String.format(getString(R.string.error_detail), (e.getMessage() != null) ? ("\n" + e.getMessage()) : ""));
                            //retryTimer.setVisibility(View.VISIBLE);
                            //retryTimer.setText(getString(R.string.resend));
                            //retryTimer.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                initiateAuth(phoneNumber);
//                            }
//                        });
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, String.format("Something Went Wrong", e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(verificationId, forceResendingToken);
                            startCountdown();
                            progressDialog.dismiss();
                            //authInProgress = true;
                            //Verification_Layout.setVisibility(View.VISIBLE);
                            //Activated = "Verification_Layout";
                            //progressDialog.dismiss();
                            mVerificationId = verificationId;
                            mResendToken = forceResendingToken;

                            codeEdit.setVisibility(View.VISIBLE);
                            //verificationMessage.setText(String.format(getString(R.string.otp_sent), phoneNumber));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CountDownTimer countDownTimer;

    private void startCountdown() {
        //retryTimer.setOnClickListener(null);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
//                if (retryTimer != null) {
//                    retryTimer.setText(String.valueOf(l / 1000));
//                }
            }

            @Override
            public void onFinish() {
//                if (retryTimer != null) {
//                    retryTimer.setText(getText(R.string.resend));
//                    retryTimer.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            initiateAuth(phoneNumber);
//                        }
//                    });
//                }
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            finish();
        }
    }
}