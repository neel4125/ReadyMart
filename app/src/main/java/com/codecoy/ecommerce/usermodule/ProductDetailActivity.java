package com.codecoy.ecommerce.usermodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.MenuItemCompat;

import com.codecoy.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView product_thumbnail, image_a, image_b, image_c, image_d;
    private TextView tv_name, tv_desc, tv_variant1, tv_variant2, tv_variant3, tv_color, tv_wholeprice, tv_retailprice, tv_unit;
    private MaterialButton btn_add_cart;
    private String name, desc, variant1, variant2, variant3, color, wholeprice, retailprice, unit, thumbnail;
    int qty = 1;
    private String _pQuantity;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    private ProgressBar progbar;
    private String checkedvariant = "";
    private ProgressDialog progressDialog;
    TextView tv;
    LinearLayout linearLayout;
    ArrayList<String> imageslist;
    private ArrayList<String> alreadyincart;
    private ArrayList<String> alreadyincartQty;
    private ArrayList<String> alreadyincartDocid;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("");
        setContentView(R.layout.activity_product_detail);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Working");
        progressDialog.setTitle("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        linearLayout = findViewById(R.id.linearLayout);
        init();
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        alreadyincart = new ArrayList<>();
        alreadyincartQty = new ArrayList<>();
        alreadyincartDocid = new ArrayList<>();

        loadOrders();

        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            desc = getIntent().getStringExtra("desc");

            wholeprice = getIntent().getStringExtra("wholeprice");

            unit = getIntent().getStringExtra("unit");
            thumbnail = getIntent().getStringExtra("thumbnail");
            imageslist = getIntent().getStringArrayListExtra("imageslist");

            tv_name.setText("Name: " + name);
            tv_desc.setText("Desc: " + desc);

            tv_wholeprice.setText("Price: " + wholeprice + " CAD");
            tv_unit.setText("Unit: " + unit);
            Picasso.get().load(thumbnail).into(product_thumbnail);
            product_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageWindow(thumbnail);
                }
            });


            if (imageslist!=null){
                if (imageslist.size() == 4) {
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);
                    Picasso.get().load(imageslist.get(2)).into(image_c);
                    Picasso.get().load(imageslist.get(3)).into(image_d);
                    image_a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(0));
                        }
                    });
                    image_b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(1));
                        }
                    });
                    image_c.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(2));
                        }
                    });
                    image_d.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(3));
                        }
                    });
                } else if (imageslist.size() == 3) {
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);
                    Picasso.get().load(imageslist.get(2)).into(image_c);
                    image_a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(0));
                        }
                    });
                    image_b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(1));
                        }
                    });
                    image_c.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(2));
                        }
                    });

                } else if (imageslist.size() == 2) {
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);
                    image_a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(0));
                        }
                    });
                    image_b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(1));
                        }
                    });

                } else if (imageslist.size() == 1) {
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    image_a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageWindow(imageslist.get(0));
                        }
                    });
                }
            }


        }

        btn_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtocartDilouge();
            }
        });
    }

    private void init() {
        product_thumbnail = findViewById(R.id.product_thumbnail);
        image_a = findViewById(R.id.product_image_a);
        image_b = findViewById(R.id.product_image_b);
        image_c = findViewById(R.id.product_image_c);
        image_d = findViewById(R.id.product_image_d);
        tv_name = findViewById(R.id.tv_name);
        tv_desc = findViewById(R.id.tv_desc);

        tv_wholeprice = findViewById(R.id.tv_wholeprice);

        tv_unit = findViewById(R.id.tv_unit);
        btn_add_cart = findViewById(R.id.btn_add_to_cart);
        progbar = findViewById(R.id.progbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        String qty = Utils.getPreferences("cartqty", this);
        tv.setText(qty);
        ImageView imageView = (ImageView) notifCount.findViewById(R.id.cart_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                startActivity(_intent);
                finish();

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_home:
                Intent aaintent = new Intent(ProductDetailActivity.this, Main_Cat_Activity.class);
                startActivity(aaintent);
                break;
            // action with ID action_settings was selected
            case R.id.action_cart:
                Intent _intent = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                startActivity(_intent);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
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


    private void addtocartDilouge() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_to_cart_alert_dilouge, null);
        MaterialButton btn_add_to_cart = (MaterialButton) dialogView.findViewById(R.id.btn_add_to_cart);
        MaterialButton btn_cancel = (MaterialButton) dialogView.findViewById(R.id.btn_cancel);
        TextView tv_product_name = dialogView.findViewById(R.id.tv_product_name);
        TextView tv_product_desc = dialogView.findViewById(R.id.tv_product_desc);
        TextView tv_product_price = dialogView.findViewById(R.id.tv_product_price);

        TextView tv_product_unit = dialogView.findViewById(R.id.tv_product_unit);
        ImageView itemImageView = dialogView.findViewById(R.id.itemImageView);
        ImageView add_qty = dialogView.findViewById(R.id.CHART_Add_item_TV);
        ImageView del_qty = dialogView.findViewById(R.id.CHART_Remove_item_TV);
        final TextView etquantity = dialogView.findViewById(R.id.CHART_Total_Item_TV);

        Picasso.get().load(thumbnail).into(itemImageView);
        tv_product_name.setText(name);
        tv_product_desc.setText(desc);

            tv_product_price.setText("Price: " + wholeprice + " CAD");

//        } else {
//            tv_product_price.setText("Price: " + retailprice + " INR");
//        }





        tv_product_unit.setText("Unit: " + unit);
        add_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty++;
                etquantity.setText(String.valueOf(qty));
            }
        });

        del_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 1) {
                    qty--;
                    etquantity.setText(String.valueOf(qty));
                }

            }
        });
        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _pQuantity = etquantity.getText().toString();
                if (_pQuantity.equals("0")) {
                    Toast.makeText(ProductDetailActivity.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                } else {

                    dialogBuilder.dismiss();





                            saveaddtocartProduct(name, wholeprice, _pQuantity, thumbnail, unit);




                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();


    }

    private void savealreadyCartProduct(String finalqty, String docid) {

        progressDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("add_cart_product_quantity", finalqty);

        db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts").document(docid).set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        final PrettyDialog pDialog = new PrettyDialog(ProductDetailActivity.this);
                        pDialog
                                .setTitle("Add to Cart")
                                .setMessage("Product Added to Cart")
                                .setIcon(R.drawable.tick)
                                .addButton("OK", R.color.pdlg_color_white,        // button text color
                                        R.color.pdlg_color_green,        // button background color
                                        new PrettyDialogCallback() {        // button OnClick listener
                                            @Override
                                            public void onClick() {
                                                // Do what you gotta do
                                                finish();
                                                pDialog.dismiss();


                                            }
                                        })
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                final PrettyDialog pDialog = new PrettyDialog(ProductDetailActivity.this);
                pDialog
                        .setTitle("Failure")
                        .setMessage("Error To Save Product In Cart")
                        .setIcon(R.drawable.wrong)
                        .addButton("Okay", R.color.pdlg_color_white,        // button text color
                                R.color.pdlg_color_red,        // button background color
                                new PrettyDialogCallback() {        // button OnClick listener
                                    @Override
                                    public void onClick() {
                                        // Do what you gotta do
                                        pDialog.dismiss();
                                    }
                                })
                        .show();
            }
        });
    }

    private void saveaddtocartProduct(String name, String price, String _pQuantity, String _imageuri, String unit) {
        progressDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("add_cart_product_name", name);
        data.put("add_cart_product_price", price);
        data.put("add_cart_product_quantity", _pQuantity);
        data.put("add_cart_product_image", _imageuri);
        data.put("add_cart_product_unit", unit);
        db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts").document().set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        final PrettyDialog pDialog = new PrettyDialog(ProductDetailActivity.this);
                        pDialog
                                .setTitle("Add to Cart")
                                .setMessage("Product Added to Cart")
                                .setIcon(R.drawable.tick)
                                .addButton("OK", R.color.pdlg_color_white,        // button text color
                                        R.color.pdlg_color_green,        // button background color
                                        new PrettyDialogCallback() {        // button OnClick listener
                                            @Override
                                            public void onClick() {
                                                // Do what you gotta do

                                                String cartItem = Utils.getPreferences("cartqty", ProductDetailActivity.this);
                                                int newcartQty = Integer.parseInt(cartItem) + 1;
                                                Utils.savePreferences("cartqty", String.valueOf(newcartQty), ProductDetailActivity.this);
                                                tv.setText(String.valueOf(newcartQty));
                                                pDialog.dismiss();
                                                finish();



                                            }
                                        })
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                final PrettyDialog pDialog = new PrettyDialog(ProductDetailActivity.this);
                pDialog
                        .setTitle("Fail")
                        .setMessage("Add To Cart Error")
                        .setIcon(R.drawable.wrong)
                        .addButton("Okay", R.color.pdlg_color_white,        // button text color
                                R.color.pdlg_color_red,        // button background color
                                new PrettyDialogCallback() {        // button OnClick listener
                                    @Override
                                    public void onClick() {
                                        // Do what you gotta do
                                        pDialog.dismiss();
                                    }
                                })
                        .show();
            }
        });
    }

    private void showImageWindow(String imagepath) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.image_alert_dilouge, null);
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        Picasso.get().load(imagepath).into(imageView);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void loadOrders() {
        alreadyincart.clear();
        alreadyincartQty.clear();
        alreadyincartDocid.clear();


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

                                alreadyincart.add(_name);
                                alreadyincartQty.add(_qty);
                                alreadyincartDocid.add(_doc_id);



                            }


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

    @Override
    protected void onRestart() {
        super.onRestart();
        String qty = Utils.getPreferences("cartqty", this);
        tv.setText(qty);
    }

    private int getProductPos(String name) {
        return alreadyincart.indexOf(name);
    }
}
