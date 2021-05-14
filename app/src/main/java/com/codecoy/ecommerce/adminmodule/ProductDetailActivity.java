package com.codecoy.ecommerce.adminmodule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codecoy.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView product_thumbnail,image_a,image_b,image_c,image_d;
    private TextView tv_name,tv_desc,tv_variant1,tv_variant2,tv_variant3,tv_color,tv_wholeprice,tv_retailprice,tv_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_admin);

        init();

        if (getIntent()!=null){
            String name=getIntent().getStringExtra("name");
            String desc=getIntent().getStringExtra("desc");
            String wholeprice=getIntent().getStringExtra("wholeprice");
            String unit=getIntent().getStringExtra("unit");
            String thumbnail=getIntent().getStringExtra("thumbnail");
            ArrayList<String> imageslist=getIntent().getStringArrayListExtra("imageslist");

            tv_name.setText(name);
            tv_desc.setText(desc);


            tv_wholeprice.setText("Price: "+wholeprice+" CAD");
            tv_unit.setText("Unit: "+unit);
            Picasso.get().load(thumbnail).into(product_thumbnail);


            if (imageslist!=null){
                if (imageslist.size()==4){
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);
                    Picasso.get().load(imageslist.get(2)).into(image_c);
                    Picasso.get().load(imageslist.get(3)).into(image_d);
                }
                else  if (imageslist.size()==3){
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);
                    Picasso.get().load(imageslist.get(2)).into(image_c);

                }
                else  if (imageslist.size()==2){
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                    Picasso.get().load(imageslist.get(1)).into(image_b);

                }
                else  if (imageslist.size()==1){
                    Picasso.get().load(imageslist.get(0)).into(image_a);
                }

            }


        }
    }

    private void init() {
        product_thumbnail=findViewById(R.id.product_thumbnail);
        image_a=findViewById(R.id.product_image_a);
        image_b=findViewById(R.id.product_image_b);
        image_c=findViewById(R.id.product_image_c);
        image_d=findViewById(R.id.product_image_d);
        tv_name=findViewById(R.id.tv_name);
        tv_desc=findViewById(R.id.tv_desc);


        tv_wholeprice=findViewById(R.id.tv_wholeprice);

        tv_unit=findViewById(R.id.tv_unit);
    }
}
