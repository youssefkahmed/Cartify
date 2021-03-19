package com.ecommerce.cartify.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProductFrag extends Fragment {

    View view;
    int productId;

    // View Items
    ImageView productImage;
    TextView productName;
    TextView productPrice;
    TextView productSeller;
    TextView productCategory;
    TextView productQuantity;
    Button addToCartBtn;
    Button addToWishlistBtn;

    public ProductFrag(int productId) {
        this.productId = productId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.fragment_product, container, false);

        // Instantiating View Items
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);;
        productPrice = view.findViewById(R.id.product_price);;
        productSeller = view.findViewById(R.id.product_seller);;
        productCategory = view.findViewById(R.id.product_category);;
        productQuantity = view.findViewById(R.id.product_quantity_label);;
        addToCartBtn = view.findViewById(R.id.add_to_cart_btn);;
        addToWishlistBtn = view.findViewById(R.id.add_to_wishlist_btn);;

        // Adding To Cart functionality
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ADDED TO CART", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding To Wishlist functionality
        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ADDED TO WISHLIST", Toast.LENGTH_SHORT).show();
            }
        });

        // Filling Product Frag with Product Info
        getProduct();
        return view;
    }

    public void getProduct(){

        // Instantiating Query to get Product based on ID
        Query query = FirebaseDatabase.getInstance()
                .getReference("products")
                .orderByKey()
                .equalTo(String.valueOf(productId));

        // Listener for query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Product product = postSnapshot.getValue(Product.class);

                    // Loading Product Info Into View
                    Glide.with(getContext())
                            .asBitmap()
                            .load(product.getImage_url())
                            .error(R.mipmap.ic_launcher)
                            .into(productImage);

                    productName.setText(product.getProd_name());
                    productPrice.setText("$" + String.valueOf(product.getPrice()));
                    productSeller.setText(product.getSeller());
                    productCategory.setText(product.getCategory());

                    if(product.getQuantity() <= 10) {
                        productQuantity.setText("Only " + String.valueOf(product.getQuantity()) + " left in stock!");
                        productQuantity.setTextColor(Color.parseColor("#F12D2D"));
                    }else{
                        productQuantity.setText(String.valueOf(product.getQuantity()) + " left in stock.");
                        productQuantity.setTextColor(Color.parseColor("#1bce2d"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
