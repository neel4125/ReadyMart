package com.codecoy.ecommerce.usermodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.ChoseActivity;
import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.adapter.MainCategoryAdapter;
import com.codecoy.ecommerce.usermodule.adapter.ProductAdapter;
import com.codecoy.ecommerce.usermodule.model.MainCategoryMOdel;
import com.codecoy.ecommerce.usermodule.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Main_Cat_Activity extends AppCompatActivity {

    private static final String TAG = "Admin";


    FirebaseFirestore db;

    private RecyclerView recyclerView;
    private MainCategoryAdapter mainCategoryAdapter;

    private ArrayList<MainCategoryMOdel> datalist;
    private ArrayList<ProductModel> productdataList;
    private ProductAdapter productAdapter;
    private ProgressBar progbar;
    SearchView searchView;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("");
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerview);
        searchView = findViewById(R.id.searchView);
        progbar = findViewById(R.id.progbar);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        datalist = new ArrayList<>();
        productdataList = new ArrayList<>();

        loadProducts();
        loadMainCategories();
        loadLatlong();




    }

    private void loadLatlong() {
        progbar.setVisibility(View.VISIBLE);
        db.collection("ShopLoc").document("location").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    progbar.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();

                    String lat=document.getString("lat");
                    String lang=document.getString("long");

                    Utils.savePreferences("lat",lat,Main_Cat_Activity.this);
                    Utils.savePreferences("lang",lang,Main_Cat_Activity.this);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItem _item = menu.findItem(R.id.action_home);

        _item.setVisible(false);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        String qty = Utils.getPreferences("cartqty", this);
        tv.setText(qty);
        ImageView imageView = (ImageView) notifCount.findViewById(R.id.cart_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(Main_Cat_Activity.this, MyCartActivity.class);
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
                Intent intent = new Intent(Main_Cat_Activity.this, ChoseActivity.class);
                startActivity(intent);
                finish();
                break;
            // action with ID action_settings was selected
            case R.id.action_cart:
                Intent _intent = new Intent(Main_Cat_Activity.this, MyCartActivity.class);
                startActivity(_intent);

                break;
            case R.id.action_loc:
                Intent locintent = new Intent(Main_Cat_Activity.this, MapsActivity.class);
                startActivity(locintent);

                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Main_Cat_Activity.this, LoginActivity.class));
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


    private void loadMainCategories() {
        datalist.clear();
        progbar.setVisibility(View.VISIBLE);
        db.collection("Db").orderBy("main_cat_name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String _name = document.getString("main_cat_name");
                                String _image = document.getString("main_cat_image");

                                String docid = document.getId();
                                datalist.add(new MainCategoryMOdel(_name, _image, docid));

                            }

                            mainCategoryAdapter = new MainCategoryAdapter(Main_Cat_Activity.this, datalist);
                            recyclerView.setAdapter(mainCategoryAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(Main_Cat_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadProducts() {

        productdataList.clear();
        progbar.setVisibility(View.VISIBLE);
        db.collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String mainCat = document.getString("product_maincat");
                                String subCat = document.getString("product_subcat");
                                String _name = document.getString("product_name");
                                String desc = document.getString("product_desc");

                                String wholeprice = document.getString("product_wholeprice");

                                String unit = document.getString("product_unit");
                                String _image = document.getString("product_thumbnail");
                                ArrayList<String> arrayListimages = (ArrayList<String>) document.get("images");

                                String docid = document.getId();
                                productdataList.add(new ProductModel(mainCat, subCat, _name, desc,wholeprice, unit, _image, docid, arrayListimages));

                            }

                            productAdapter = new ProductAdapter(Main_Cat_Activity.this, productdataList);

                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {

                                        recyclerView.setAdapter(productAdapter);
                                        productAdapter.getFilter().filter(newText);


                                    return false;
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(Main_Cat_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onBackPressed() {
      startActivity(new Intent(Main_Cat_Activity.this, ChoseActivity.class));
      finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        String qty = Utils.getPreferences("cartqty", this);
        tv.setText(qty);
    }



}