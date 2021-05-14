package com.codecoy.ecommerce.usermodule.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.R;

public class MyCartViewHolder  extends RecyclerView.ViewHolder {
    public TextView tv_name,tv_price,tv_qty;
    public ImageView deletorder,product_image;
    public MyCartViewHolder(View view) {
        super(view);
        tv_name = itemView.findViewById(R.id.name_order_product);
        tv_price = itemView.findViewById(R.id.price_order_product);
        tv_qty = itemView.findViewById(R.id.qty_order_product);
        deletorder = itemView.findViewById(R.id.delete_order_product);
        product_image = itemView.findViewById(R.id.product_image);


    }
}
