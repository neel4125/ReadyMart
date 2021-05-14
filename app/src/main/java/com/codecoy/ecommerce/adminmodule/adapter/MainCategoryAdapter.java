package com.codecoy.ecommerce.adminmodule.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.adminmodule.Sub_Cat_Activity;
import com.codecoy.ecommerce.adminmodule.model.MainCategoryMOdel;
import com.codecoy.ecommerce.adminmodule.views.MainCategoryViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryViewHolder> {

    Context context;
    ArrayList<MainCategoryMOdel> listDataArrayList;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    private CallbackInterface mCallback;
    public interface CallbackInterface{


        void onHandleSelection(int position);
    }
    public MainCategoryAdapter(Context context, ArrayList<MainCategoryMOdel> listDataArrayList) {
        this.context = context;
        this.listDataArrayList = listDataArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Saving Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }
    }


    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_main_categorey_admin, parent, false);
        return new MainCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainCategoryViewHolder holder, final int position) {
        holder.tv_name.setText(listDataArrayList.get(position).getName());

        Picasso.get().load(listDataArrayList.get(position).getImage()).into(holder.product_image);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listDataArrayList.size() > 0) {
                    db.collection("Db").document(listDataArrayList.get(position).getDocid())
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Categorey Deleted Successfully", Toast.LENGTH_SHORT).show();
                            removeAt(position);

                        }
                    });
                } else {
                    Toast.makeText(context, "No Item found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  addProduct(listDataArrayList.get(position).getCat_type(),listDataArrayList.get(position).getDocid());
                if(mCallback != null){
                    mCallback.onHandleSelection(position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Sub_Cat_Activity.class);
                intent.putExtra("main_cat_name",listDataArrayList.get(position).getName());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listDataArrayList.size();
    }
    private void removeAt(int position) {
        listDataArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listDataArrayList.size());

    }




}
