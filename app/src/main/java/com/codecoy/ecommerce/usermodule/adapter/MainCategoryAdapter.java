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

import com.codecoy.ecommerce.R;
import com.codecoy.ecommerce.usermodule.Sub_Cat_Activity;
import com.codecoy.ecommerce.usermodule.model.MainCategoryMOdel;

import com.codecoy.ecommerce.usermodule.viewholder.MainCategoryViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryViewHolder> implements Filterable {

    Context context;
    ArrayList<MainCategoryMOdel> listDataArrayList;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    ArrayList<MainCategoryMOdel> listDataArrayListfilterd;


    public MainCategoryAdapter(Context context, ArrayList<MainCategoryMOdel> listDataArrayList) {
        this.context = context;
        this.listDataArrayList = listDataArrayList;
        listDataArrayListfilterd=new ArrayList<>(listDataArrayList);
        db = FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
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


    @Override
    public Filter getFilter() {
        return filterData;
    }
    private Filter filterData=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MainCategoryMOdel> fileredlist=new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                fileredlist.addAll(listDataArrayListfilterd);
            }else{
                String  filter=constraint.toString().toLowerCase().trim();
                for (MainCategoryMOdel item:listDataArrayListfilterd){
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
