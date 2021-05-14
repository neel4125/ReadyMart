package com.codecoy.ecommerce.usermodule.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.usermodule.ProductActivity;
import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.model.SubCategoreyModel;

import com.codecoy.ecommerce.usermodule.viewholder.MainCategoryViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<MainCategoryViewHolder> implements Filterable {

    Context context;
    ArrayList<SubCategoreyModel> listDataArrayList;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    ArrayList<SubCategoreyModel> listDataArrayListfilterd;

    public SubCategoryAdapter(Context context, ArrayList<SubCategoreyModel> listDataArrayList) {
        this.context = context;
        this.listDataArrayList = listDataArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listDataArrayListfilterd=new ArrayList<>(listDataArrayList);

    }


    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_main_categorey, parent, false);
        return new MainCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainCategoryViewHolder holder, final int position) {
        holder.tv_name.setText(listDataArrayList.get(position).getName());

        Picasso.get().load(listDataArrayList.get(position).getImage()).into(holder.product_image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductActivity.class);
                intent.putExtra("main_cat_name",listDataArrayList.get(position).getMain_cat_name());
                intent.putExtra("sub_cat_name",listDataArrayList.get(position).getName());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listDataArrayList.size();
    }



    @Override
    public Filter getFilter() {
        return filterData;
    }
    private Filter filterData=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<SubCategoreyModel> fileredlist=new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                fileredlist.addAll(listDataArrayListfilterd);
            }else{
                String  filter=constraint.toString().toLowerCase().trim();
                for (SubCategoreyModel item:listDataArrayListfilterd){
                    if (item.getName().toLowerCase().contains(filter)){
                        fileredlist.add(item);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=fileredlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listDataArrayList.clear();
            listDataArrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };




}
