package com.codecoy.ecommerce.adminmodule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.adminmodule.adapter.ProductAdapter;

import com.codecoy.ecommerce.adminmodule.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.CallbackInterface {

    private static final String TAG = "Admin";

    String updateimage = "false";
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private String clicked = "";
    String oldname;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUriTumbnail, image1, image2, image3, image4;
    private ImageView _add_product_thumbnail, product_image_a, product_image_b, product_image_c, product_image_d;
    private StorageReference mStorageRef;

    private MaterialButton add_product_btn;
    private RecyclerView recyclerView;
    private ProductAdapter mainCategoryAdapter;
    private ArrayList<ProductModel> datalist;
    private ArrayList<String> datalist_images;
    private ProgressBar progbar;
    private String mainCat, subCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_admin);
        recyclerView = findViewById(R.id.recyclerview);
        progbar = findViewById(R.id.progbar);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        datalist = new ArrayList<>();
        datalist_images = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Working");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        mStorageRef = FirebaseStorage.getInstance().getReference("SubCatImages");
        init();
        if (getIntent() != null) {
            mainCat = getIntent().getStringExtra("main_cat_name");
            subCat = getIntent().getStringExtra("sub_cat_name");
        }

        loadMainCategories();
        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });


    }


    private void init() {

        add_product_btn = findViewById(R.id.add_product_btn);

    }


    private void addProduct() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_product_dilouge, null);
        final TextInputEditText _nameproduct = (TextInputEditText) dialogView.findViewById(R.id.product_name);
        final TextInputEditText _descproduct = (TextInputEditText) dialogView.findViewById(R.id.product_desc);

        final TextInputEditText _whole_priceproduct = (TextInputEditText) dialogView.findViewById(R.id.product_price_whole);
        final TextInputEditText _unitproduct = (TextInputEditText) dialogView.findViewById(R.id.product_unit);

        _add_product_thumbnail = (ImageView) dialogView.findViewById(R.id.tumbnail_product);
        product_image_a = (ImageView) dialogView.findViewById(R.id.product_image_a);
        product_image_b = (ImageView) dialogView.findViewById(R.id.product_image_b);
        product_image_c = (ImageView) dialogView.findViewById(R.id.product_image_c);
        product_image_d = (ImageView) dialogView.findViewById(R.id.product_image_d);
        MaterialButton btn_add_product = (MaterialButton) dialogView.findViewById(R.id.btn_add);


        _add_product_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "0";
                openFileChooser();
            }
        });
        product_image_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "1";
                openFileChooser();
            }
        });
        product_image_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "2";
                openFileChooser();
            }
        });
        product_image_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "3";
                openFileChooser();
            }
        });
        product_image_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "4";
                openFileChooser();
            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = _nameproduct.getText().toString().trim();
                String desc = _descproduct.getText().toString().trim();

                String wholeprice = _whole_priceproduct.getText().toString().trim();
                String unit = _unitproduct.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(desc)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Desc! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(wholeprice)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product  Price! ...", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(unit)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product Unit! ...", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    dialogBuilder.dismiss();
                    saveProduct(name, desc, wholeprice, unit);

                }

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    // upload data to firebase //
    private void saveProduct(final String name, final String desc, final String wholeprice, final String unit) {
        if (imageUriTumbnail != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUriTumbnail));
            storageReference.putFile(imageUriTumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String imageUrl = uri.toString();
                            Map<String, Object> data = new HashMap<>();
                            data.put("product_name", name);
                            data.put("product_desc", desc);
                            data.put("product_wholeprice", wholeprice);
                            data.put("product_unit", unit);
                            data.put("product_thumbnail", imageUrl);

                            db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            addImageA(name);
                                            addImageB(name);
                                            addImageC(name);
                                            addImageD(name);
                                            new Handler()
                                                    .postDelayed(new Runnable() {
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                                                            pDialog
                                                                    .setTitle("Add Product")
                                                                    .setMessage("Product Saved")
                                                                    .setIcon(R.drawable.tick)
                                                                    .addButton("OK", R.color.pdlg_color_white,        // button text color
                                                                            R.color.pdlg_color_green,        // button background color
                                                                            new PrettyDialogCallback() {        // button OnClick listener
                                                                                @Override
                                                                                public void onClick() {
                                                                                    // Do what you gotta do
                                                                                    pDialog.dismiss();
                                                                                    addImagesArraylist(name);
                                                                                    saveProductCollection(mainCat, subCat, name, desc, wholeprice, unit, imageUrl, datalist_images);
                                                                                    loadMainCategories();

                                                                                }
                                                                            })
                                                                    .show();
                                                        }
                                                    }, 10000);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                                    pDialog
                                            .setTitle("Add Product")
                                            .setMessage("Product Not Saved")
                                            .setIcon(R.drawable.wrong)
                                            .addButton("OK", R.color.pdlg_color_white,        // button text color
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
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                    pDialog
                            .setTitle("Add Product")
                            .setMessage("Product Not Saved")
                            .setIcon(R.drawable.wrong)
                            .addButton("OK", R.color.pdlg_color_white,        // button text color
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
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(ProductActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductCollection(String mainCat, String subCat, String name, String desc, String wholeprice, String unit, String imageUrl, ArrayList datalist_images) {

        progbar.setVisibility(View.VISIBLE);
        Map<String, Object> data = new HashMap<>();
        data.put("product_maincat", mainCat);
        data.put("product_subcat", subCat);
        data.put("product_name", name);
        data.put("product_desc", desc);
        data.put("product_wholeprice", wholeprice);

        data.put("product_unit", unit);
        data.put("product_thumbnail", imageUrl);
        data.put("images", datalist_images);
        db.collection("Products").document(name).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadMainCategories() {
        datalist.clear();
        progbar.setVisibility(View.VISIBLE);
        db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").orderBy("product_name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String _name = document.getString("product_name");
                                String desc = document.getString("product_desc");

                                String wholeprice = document.getString("product_wholeprice");
                                String unit = document.getString("product_unit");
                                String _image = document.getString("product_thumbnail");
                                ArrayList<String> arrayListimages = (ArrayList<String>) document.get("images");


                                String docid = document.getId();
                                datalist.add(new ProductModel(mainCat, subCat, _name, desc, wholeprice, unit, _image, docid, arrayListimages));

                            }

                            mainCategoryAdapter = new ProductAdapter(ProductActivity.this, datalist);
                            recyclerView.setAdapter(mainCategoryAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    // for choosing image from gallery //
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            if (clicked.equals("0")) {
                imageUriTumbnail = data.getData();
                Picasso.get().load(imageUriTumbnail).into(_add_product_thumbnail);
            } else if (clicked.equals("1")) {
                image1 = data.getData();
                Picasso.get().load(image1).into(product_image_a);
            } else if (clicked.equals("2")) {
                image2 = data.getData();
                Picasso.get().load(image2).into(product_image_b);
            } else if (clicked.equals("3")) {
                image3 = data.getData();
                Picasso.get().load(image3).into(product_image_c);
            } else if (clicked.equals("4")) {
                image4 = data.getData();
                Picasso.get().load(image4).into(product_image_d);
            }

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onHandleSelection(int position) {
        oldname = datalist.get(position).getProduct_name();
        String desc = datalist.get(position).getProduct_desc();

        String whole_price = datalist.get(position).getProduct_wholesale_price();

        String unit = datalist.get(position).getProduct_unit();
        String doc = datalist.get(position).getDocid();
        String image = datalist.get(position).getProducta_thumbnail();
        String maincat = datalist.get(position).getMaincat();
        String subcat = datalist.get(position).getSubcat();
        ArrayList<String> imageslist = datalist.get(position).getProductImages();
        updateproduct(maincat, subcat, oldname, desc, whole_price, unit, doc, image, imageslist);
    }

    private void updateproduct(final String maincat, final String subcat, String name, String desc, String whole_price, String unit, final String doc, final String image, final ArrayList<String> imageslist) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_product_dilouge, null);
        final TextInputEditText _nameproduct = (TextInputEditText) dialogView.findViewById(R.id.product_name);
        final TextInputEditText _descproduct = (TextInputEditText) dialogView.findViewById(R.id.product_desc);

        final TextInputEditText _whole_priceproduct = (TextInputEditText) dialogView.findViewById(R.id.product_price_whole);

        final TextInputEditText _unitproduct = (TextInputEditText) dialogView.findViewById(R.id.product_unit);

        _nameproduct.setText(name);
        _descproduct.setText(desc);

        _whole_priceproduct.setText(whole_price);
        _unitproduct.setText(unit);

        _add_product_thumbnail = (ImageView) dialogView.findViewById(R.id.tumbnail_product);
        product_image_a = (ImageView) dialogView.findViewById(R.id.product_image_a);
        product_image_b = (ImageView) dialogView.findViewById(R.id.product_image_b);
        product_image_c = (ImageView) dialogView.findViewById(R.id.product_image_c);
        product_image_d = (ImageView) dialogView.findViewById(R.id.product_image_d);

        Picasso.get().load(image).into(_add_product_thumbnail);
        if (imageslist != null) {
            if (imageslist.size() == 4) {
                Picasso.get().load(imageslist.get(0)).into(product_image_a);
                Picasso.get().load(imageslist.get(1)).into(product_image_b);
                Picasso.get().load(imageslist.get(2)).into(product_image_c);
                Picasso.get().load(imageslist.get(3)).into(product_image_d);
            } else if (imageslist.size() == 3) {
                Picasso.get().load(imageslist.get(0)).into(product_image_a);
                Picasso.get().load(imageslist.get(1)).into(product_image_b);
                Picasso.get().load(imageslist.get(2)).into(product_image_c);

            } else if (imageslist.size() == 2) {
                Picasso.get().load(imageslist.get(0)).into(product_image_a);
                Picasso.get().load(imageslist.get(1)).into(product_image_b);

            } else if (imageslist.size() == 1) {
                Picasso.get().load(imageslist.get(0)).into(product_image_a);
            }
        }

        MaterialButton btn_add_product = (MaterialButton) dialogView.findViewById(R.id.btn_add);


        _add_product_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateimage = "true";
                clicked = "0";
                openFileChooser();
            }
        });
        product_image_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "1";
                openFileChooser();
            }
        });
        product_image_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "2";
                openFileChooser();
            }
        });
        product_image_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "3";
                openFileChooser();
            }
        });
        product_image_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "4";
                openFileChooser();
            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_name = _nameproduct.getText().toString().trim();
                String desc = _descproduct.getText().toString().trim();

                String wholeprice = _whole_priceproduct.getText().toString().trim();

                String unit = _unitproduct.getText().toString().trim();


                if (TextUtils.isEmpty(new_name)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(desc)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Desc! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(wholeprice)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product  Price! ...", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(unit)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Product Unit! ...", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    dialogBuilder.dismiss();
                    saveUpdateProduct(maincat, subcat, new_name, desc, wholeprice, unit, doc, image);
                }

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void saveUpdateProduct(final String maincat, final String subcat, final String name, final String desc, final String wholeprice, final String unit, final String doc, final String image) {

        if (imageUriTumbnail != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUriTumbnail));
            storageReference.putFile(imageUriTumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Map<String, Object> data = new HashMap<>();
                            data.put("product_name", name);
                            data.put("product_desc", desc);

                            data.put("product_wholeprice", wholeprice);

                            data.put("product_unit", unit);
                            data.put("product_thumbnail", imageUrl);


                            db.collection("Db").document(maincat).collection("SubCat").document(subcat).collection("products").document(doc).set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("Products").document(oldname).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });

                                            addImageA(name);
                                            addImageB(name);
                                            addImageC(name);
                                            addImageD(name);

                                            new Handler()
                                                    .postDelayed(new Runnable() {
                                                        public void run() {
                                                            // yourMethod();
                                                            progressDialog.dismiss();
                                                            final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                                                            pDialog
                                                                    .setTitle("Add Sub Categorey")
                                                                    .setMessage("Sub Categorey  Saved")
                                                                    .setIcon(R.drawable.tick)
                                                                    .addButton("OK", R.color.pdlg_color_white,        // button text color
                                                                            R.color.pdlg_color_green,        // button background color
                                                                            new PrettyDialogCallback() {        // button OnClick listener
                                                                                @Override
                                                                                public void onClick() {
                                                                                    // Do what you gotta do
                                                                                    pDialog.dismiss();
                                                                                    addImagesArraylist(name);
                                                                                    saveProductCollection(maincat, subcat, name, desc, wholeprice, unit, image, datalist_images);
                                                                                    loadMainCategories();

                                                                                }
                                                                            })
                                                                    .show();
                                                        }
                                                    }, 10000);


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                                    pDialog
                                            .setTitle("Add Product")
                                            .setMessage("Product Not Saved")
                                            .setIcon(R.drawable.wrong)
                                            .addButton("OK", R.color.pdlg_color_white,        // button text color
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
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                    pDialog
                            .setTitle("Add Sub Categorey")
                            .setMessage("Sub Categorey Not Saved")
                            .setIcon(R.drawable.wrong)
                            .addButton("OK", R.color.pdlg_color_white,        // button text color
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
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });
        } else {
            progressDialog.show();
            Map<String, Object> data = new HashMap<>();
            data.put("product_name", name);
            data.put("product_desc", desc);

            data.put("product_wholeprice", wholeprice);

            data.put("product_unit", unit);
            data.put("product_thumbnail", image);


            db.collection("Db").document(maincat).collection("SubCat").document(subcat).collection("products").document(doc).set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("Products").document(oldname).delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                            addImageA(name);
                            addImageB(name);
                            addImageC(name);
                            addImageD(name);

                            new Handler()
                                    .postDelayed(new Runnable() {
                                        public void run() {
                                            // yourMethod();
                                            progressDialog.dismiss();
                                            final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                                            pDialog
                                                    .setTitle("Add Sub Categorey")
                                                    .setMessage("Sub Categorey  Saved")
                                                    .setIcon(R.drawable.tick)
                                                    .addButton("OK", R.color.pdlg_color_white,        // button text color
                                                            R.color.pdlg_color_green,        // button background color
                                                            new PrettyDialogCallback() {        // button OnClick listener
                                                                @Override
                                                                public void onClick() {
                                                                    // Do what you gotta do
                                                                    pDialog.dismiss();
                                                                    addImagesArraylist(name);
                                                                    saveProductCollection(maincat, subcat, name, desc, wholeprice, unit, image, datalist_images);
                                                                    loadMainCategories();

                                                                }
                                                            })
                                                    .show();
                                        }
                                    }, 10000);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    final PrettyDialog pDialog = new PrettyDialog(ProductActivity.this);
                    pDialog
                            .setTitle("Add Sub Categorey")
                            .setMessage("Sub Categorey Not Saved")
                            .setIcon(R.drawable.wrong)
                            .addButton("OK", R.color.pdlg_color_white,        // button text color
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
    }


    private void addImageA(final String name) {
        if (image1 != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(image1));
            storageReference.putFile(image1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            datalist_images.add(imageUrl);
                            progressDialog.dismiss();
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("image_a", imageUrl);
//
//
//                            db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            progressDialog.dismiss();
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });

        } else {
            //  Toast.makeText(this, "Image Path missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageB(final String name) {
        if (image2 != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(image2));
            storageReference.putFile(image2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            datalist_images.add(imageUrl);
                            progressDialog.dismiss();
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("image_b", imageUrl);
//
//
//                            db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            progressDialog.dismiss();
//
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });

        } else {
            //  Toast.makeText(this, "Image Path missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageC(final String name) {
        if (image3 != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(image3));
            storageReference.putFile(image3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            datalist_images.add(imageUrl);
                            progressDialog.dismiss();
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("image_c", imageUrl);
//
//
//                            db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            progressDialog.dismiss();
//
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });

        } else {
            //  Toast.makeText(this, "Image Path missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageD(final String name) {
        if (image4 != null) {

            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(image4));
            storageReference.putFile(image4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            datalist_images.add(imageUrl);
                            progressDialog.dismiss();
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("image_d", imageUrl);
//
//
//                            db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            progressDialog.dismiss();
//
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //  mProgressBar.setProgress((int) progress);
                }
            });

        } else {
            //  Toast.makeText(this, "Image Path missing", Toast.LENGTH_SHORT).show();
        }
    }


    private void addImagesArraylist(final String name) {
        progressDialog.show();
        Map<String, Object> data = new HashMap<>();
        Log.d("datalist", String.valueOf(datalist_images.size()));
        data.put("images", datalist_images);


        db.collection("Db").document(mainCat).collection("SubCat").document(subCat).collection("products").document(name).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressDialog.dismiss();
                        Toast.makeText(ProductActivity.this, "Images Saved", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

