package com.codecoy.ecommerce.adminmodule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.codecoy.ecommerce.adminmodule.adapter.SubCategoryAdapter;
import com.codecoy.ecommerce.adminmodule.model.SubCategoreyModel;
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

public class Sub_Cat_Activity extends AppCompatActivity implements SubCategoryAdapter.CallbackInterface {
    private static final String TAG = "Admin";

    String updateimage="false" ;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView _add_product_image;
    private StorageReference mStorageRef;

    private MaterialButton add_product_btn;
    private RecyclerView recyclerView;
    private SubCategoryAdapter mainCategoryAdapter;
    private ArrayList<SubCategoreyModel> datalist;
    private ProgressBar progbar;
    private String mainCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub__cat_admin);
        recyclerView = findViewById(R.id.recyclerview);
        progbar = findViewById(R.id.progbar);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        datalist = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Working");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        mStorageRef = FirebaseStorage.getInstance().getReference("SubCatImages");
        init();
        if (getIntent()!=null){
            mainCat=getIntent().getStringExtra("main_cat_name");
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
        View dialogView = inflater.inflate(R.layout.add_main_cat_dilouge_admin, null);
        final TextInputEditText _nameMainCat = (TextInputEditText) dialogView.findViewById(R.id.main_cat_name);
        _add_product_image = (ImageView) dialogView.findViewById(R.id.itemImageView);
        MaterialButton btn_addMainCat = (MaterialButton) dialogView.findViewById(R.id.btn_add);


        _add_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btn_addMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = _nameMainCat.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please enter  Categorey! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    dialogBuilder.dismiss();
                    saveProduct(name);
                }

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    // upload data to firebase //
    private void saveProduct(final String name) {

        if (imageUri != null) {
            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Map<String, Object> data = new HashMap<>();
                            data.put("sub_cat_name", name);
                            data.put("sub_cat_image", imageUrl);

                            db.collection("Db").document(mainCat).collection("SubCat").document(name).set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            progressDialog.dismiss();
                                            final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
                                                                    loadMainCategories();

                                                                }
                                                            })
                                                    .show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Sub_Cat_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
            Toast.makeText(Sub_Cat_Activity.this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMainCategories() {
        datalist.clear();
        progbar.setVisibility(View.VISIBLE);
        db.collection("Db").document(mainCat).collection("SubCat").orderBy("sub_cat_name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String _name = document.getString("sub_cat_name");
                                String _image = document.getString("sub_cat_image");

                                String docid=document.getId();
                                datalist.add(new SubCategoreyModel(mainCat,_name,_image,docid));

                            }

                            mainCategoryAdapter = new SubCategoryAdapter(Sub_Cat_Activity.this, datalist);
                            recyclerView.setAdapter(mainCategoryAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progbar.setVisibility(View.GONE);
                Toast.makeText(Sub_Cat_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(_add_product_image);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onHandleSelection(int position) {
        String name=datalist.get(position).getName();
        String image=datalist.get(position).getImage();
        String doc=datalist.get(position).getDocid();
        updateproduct(name,image,doc);
    }

    private void updateproduct(String name, final String image, final String doc) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_main_cat_dilouge, null);
        final TextInputEditText _nameMainCat = (TextInputEditText) dialogView.findViewById(R.id.main_cat_name);


        _add_product_image = (ImageView) dialogView.findViewById(R.id.itemImageView);
        MaterialButton btn_addMainCat = (MaterialButton) dialogView.findViewById(R.id.btn_add);

        _nameMainCat.setText(name);
        Picasso.get().load(image).into(_add_product_image);



        _add_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                updateimage="true";
            }
        });

        btn_addMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = _nameMainCat.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please enter name! ...", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    dialogBuilder.dismiss();
                    saveUpdateProduct(name,image,doc);
                }

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void saveUpdateProduct(final String name, final String image, final String doc) {

        if (imageUri != null) {
            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Map<String, Object> data = new HashMap<>();
                            data.put("sub_cat_name", name);
                            data.put("sub_cat_image", imageUrl);



                            db.collection("Db").document(mainCat).collection("SubCat").document(doc)
                                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
                                                            loadMainCategories();

                                                        }
                                                    })
                                            .show();

                                }
                            })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error adding document", e);
                                            //  Toast.makeText(Sub_Cat_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Sub_Cat_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            progressDialog.show();
            Map<String, Object> data = new HashMap<>();
            data.put("sub_cat_name", name);
            data.put("sub_cat_image", image);



            db.collection("Db").document(mainCat).collection("SubCat").document(doc)
                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
                                            loadMainCategories();

                                        }
                                    })
                            .show();

                }
            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                            //  Toast.makeText(Sub_Cat_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            final PrettyDialog pDialog = new PrettyDialog(Sub_Cat_Activity.this);
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
}
