package com.codecoy.ecommerce.usermodule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.usermodule.MyCartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.codecoy.ecommerce.R;

import com.codecoy.ecommerce.usermodule.Utils;
import com.codecoy.ecommerce.usermodule.interfaces.CallBackInterface;
import com.codecoy.ecommerce.usermodule.model.MyCartModel;
import com.codecoy.ecommerce.usermodule.viewholder.MyCartViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class MyCartAdapter extends RecyclerView.Adapter<MyCartViewHolder>  implements Filterable {

    Context context;
    ArrayList<MyCartModel> listDataArrayList;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    private double totalPrice = 0;
    private CallBackInterface mcallBack;
    ArrayList<MyCartModel> listDataArrayListfilterd;

    //

    private ArrayList<Double> priceList;

    public MyCartAdapter(Context context, ArrayList<MyCartModel> listDataArrayList, CallBackInterface callBackInterface) {
        this.context = context;
        this.listDataArrayList = listDataArrayList;

        try {
            mcallBack = callBackInterface;
        } catch (ClassCastException ex) {
            Toast.makeText(context, "Must implement the interface in activity", Toast.LENGTH_SHORT).show();
        }
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listDataArrayListfilterd=new ArrayList<>(listDataArrayList);
        priceList = new ArrayList<>();
        loadOrders();
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_mycart, parent, false);

        return new MyCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, final int position) {
        Picasso.get().load(listDataArrayList.get(position).getProductimage()).into(holder.product_image);
        holder.tv_name.setText(listDataArrayList.get(position).getProductname());
        holder.tv_price.setText("Price: " + String.valueOf(Double.parseDouble(listDataArrayList.get(position).getProductprice()) * Double.parseDouble(listDataArrayList.get(position).getProductqty())) + " CAD");
        holder.tv_qty.setText("Quantity: " + listDataArrayList.get(position).getProductqty());

        holder.deletorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listDataArrayList.size() > 0) {
                    db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts")
                            .document(listDataArrayList.get(position).getDocid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Cart Items Deleted Successfully", Toast.LENGTH_SHORT).show();
                            removeAt(position);

                        }
                    });
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }


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
        Utils.savePreferences("cartqty", String.valueOf(listDataArrayList.size()), context);
        String cartitems = Utils.getPreferences("cartqty", context);
        MyCartActivity.tv.setText(cartitems);
        priceList.clear();
        totalPrice = 0.0;
        loadOrders();
    }

    private void loadOrders() {

        db.collection("CartProducts").document(firebaseUser.getUid()).collection("cartproducts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String _qty = document.getString("add_cart_product_quantity");
                                String _price = document.getString("add_cart_product_price");

                                priceList.add(Double.parseDouble(_qty) * Double.parseDouble(_price));
                            }
                            if (listDataArrayList.size() > 0) {
                                try {
                                    for (int i = 0; i < priceList.size(); i++) {
                                        totalPrice += priceList.get(i);

                                    }
                                    if (mcallBack != null) {
                                        mcallBack.amount(totalPrice);

                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    if ((mcallBack != null)) {
                                        mcallBack.amount((double) 0.00);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    @Override
    public Filter getFilter() {
        return filterData;
    }
    private Filter filterData=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MyCartModel> fileredlist=new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                fileredlist.addAll(listDataArrayListfilterd);
            }else{
                String  filter=constraint.toString().toLowerCase().trim();
                for (MyCartModel item:listDataArrayListfilterd){
                    if (item.getProductname().toLowerCase().contains(filter)){
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
