package com.codecoy.ecommerce.usermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.adapter.MyCartAdapter;
import com.codecoy.ecommerce.usermodule.interfaces.CallBackInterface;
import com.codecoy.ecommerce.usermodule.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyCartAdapter myCartAdapter;
    ArrayList<MyCartModel> datalist;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;

    private TextView tv_price;
    private double GrandTotal;
    private ProgressBar progbar;
    public static TextView tv;
    private MaterialButton proceed;

    DecimalFormat numberFormat = new DecimalFormat("#.00");


    //--------------------------Order Strings---------------------------//

    String _name = "";
    String _phoneno = "";
    String _email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("");
        setContentView(R.layout.activity_my_cart);
        recyclerView = findViewById(R.id.recyclerview);
        tv_price = findViewById(R.id.tv_price);
        progbar = findViewById(R.id.progbar);


        proceed = findViewById(R.id.proceed_btn);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCartActivity.this));
        datalist = new ArrayList<>();
        pd = new ProgressDialog(MyCartActivity.this);
        pd.setTitle("Working");
        pd.setMessage("Sending Order Email");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        loadOrders();
        getUserInfo();

        proceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    final StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < datalist.size(); i++) {
                        stringBuilder.append("Item Name: " + datalist.get(i).getProductname() + "\n" +
                                "Quantity: " + datalist.get(i).getProductqty() + "\n" +
                                "Price: " + (Double.parseDouble(datalist.get(i).getProductprice()) * Double.parseDouble(datalist.get(i).getProductqty())) + "\n"

                               + "Unit: " + datalist.get(i).getProductunit() + "\n"


                        );
                    }

                    sendEmail(stringBuilder);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    CallBackInterface callBackInterface = new CallBackInterface() {

        @Override
        public void amount(double t_amount) {
            try {
                tv_price.setText("Grand Total:  " + t_amount + " CAD");
                GrandTotal = t_amount;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    private void loadOrders() {
        datalist.clear();
        progbar.setVisibility(View.VISIBLE);
        db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String _name = document.getString("add_cart_product_name");
                                String _qty = document.getString("add_cart_product_quantity");
                                String _price = document.getString("add_cart_product_price");
                                String _image = document.getString("add_cart_product_image");
                                String _unit = document.getString("add_cart_product_unit");
                                String _doc_id = document.getId();
                                datalist.add(new MyCartModel(_image, _name, _price, _qty, _unit, _doc_id));

                            }

                            myCartAdapter = new MyCartAdapter(MyCartActivity.this, datalist, callBackInterface);
                            recyclerView.setAdapter(myCartAdapter);

                            // check that you placed any order yet or not
                            if (myCartAdapter.getItemCount() == 0) {
                                proceed.setEnabled(false);
                                Toast.makeText(MyCartActivity.this, "No Cart item found", Toast.LENGTH_SHORT).show();
//                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setMessage("No data found")
//                                        .setCancelable(false)
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                //do things
//                                                dialog.dismiss();
//
//                                            }
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
                            }
                            Utils.savePreferences("cartqty", String.valueOf(myCartAdapter.getItemCount()), MyCartActivity.this);
                            String itemcart = Utils.getPreferences("cartqty", MyCartActivity.this);
                            tv.setText(itemcart);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);

            }
        });
        progbar.setVisibility(View.GONE);

    }


    private void deletCartProducts() {
        progbar.setVisibility(View.VISIBLE);


        db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progbar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("CartProducts").document(firebaseUser.getUid()).
                                collection("cartproducts").document(document.getId()).delete();
                        loadOrders();
                    }
                } else {
                    progbar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void getUserInfo() {
        db.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            _name = document.getString("username");
                            _email = document.getString("useremail");
                            _phoneno = document.getString("userphoneno");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem _item = menu.findItem(R.id.action_cart);
        MenuItem item = menu.findItem(R.id.action_home);
        _item.setVisible(false);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        ImageView imageView = (ImageView) notifCount.findViewById(R.id.cart_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(MyCartActivity.this, MyCartActivity.class);
                startActivity(_intent);

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_home:
                Intent intent = new Intent(MyCartActivity.this, Main_Cat_Activity.class);
                startActivity(intent);
                break;
            // action with ID action_settings was selected
            case R.id.action_cart:
                Intent _intent = new Intent(MyCartActivity.this, MyCartActivity.class);
                startActivity(_intent);
                break;

            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MyCartActivity.this, LoginActivity.class));
                finishAffinity();
                break;
            case R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    private void sendEmail(final StringBuilder stringBuilder) {

        try {
            final StringBuilder _stringBuilder = new StringBuilder();
            for (int i = 0; i < datalist.size(); i++) {
                _stringBuilder.append("ITEM NAME : " + datalist.get(i).getProductname() + " || " +
                        "Quantity : " + datalist.get(i).getProductqty() + " || " +
                        "Price : " + (Double.parseDouble(datalist.get(i).getProductprice()) * Double.parseDouble(datalist.get(i).getProductqty())) + " || "+

                        "Unit : " + datalist.get(i).getProductunit() + " >>> "


                );

            }
            String message = "Name : " + _name + " || " + "Phone No : " + _phoneno + " || " + "Email : " + _email + " || " + _stringBuilder + "Grand Total : " + GrandTotal + "CAD";
            String mobile = "+1 (437) 974-6221";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", mobile, message))));
            deletCartProducts();
        }catch (Exception e){
            e.printStackTrace();
        }



    }


}

