package com.codecoy.ecommerce.usermodule.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.ecommerce.R;


public class MainCategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_name;

    public ImageView product_image;
    public MainCategoryViewHolder(View view) {
        super(view);
        tv_name = itemView.findViewById(R.id.main_category_name);

        product_image=itemView.findViewById(R.id.product_image);
    }
}
