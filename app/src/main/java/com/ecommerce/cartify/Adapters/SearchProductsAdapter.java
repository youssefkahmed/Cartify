package com.ecommerce.cartify.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Fragments.ProductFrag;
import com.ecommerce.cartify.Fragments.ProfileFrag;
import com.ecommerce.cartify.Fragments.SearchFrag;
import com.ecommerce.cartify.Helpers.MyGlideApp;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;

import java.util.ArrayList;

public class SearchProductsAdapter extends RecyclerView.Adapter<SearchProductsAdapter.SearchProductsViewHolder>{

    private Context mContext;
    private ArrayList<Product> mProducts;
    private FragmentManager mFragmentManager;

    public SearchProductsAdapter(Context mContext, ArrayList<Product> mProducts, FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mProducts = mProducts;
        this.mFragmentManager = mFragmentManager;
    }

    @NonNull
    @Override
    public SearchProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_product, parent, false);
        SearchProductsViewHolder searchProductsViewHolder = new SearchProductsViewHolder(view);
        return searchProductsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductsViewHolder holder, int position) {

        // Getting Product Images
        Glide.with(mContext)
                .asBitmap()
                .load(mProducts.get(position).getImage_url())
                .error(R.mipmap.ic_launcher)
                .into(holder.productImage);

        // Setting Product Info Text Fields
        holder.productName.setText(mProducts.get(position).getProd_name());
        holder.productSeller.setText(mProducts.get(position).getSeller());
        holder.productPrice.setText("$" + String.valueOf(mProducts.get(position).getPrice()));

        // onClickListener for each Product Item
        holder.productParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productId = mProducts.get(position).getProd_id();
                mFragmentManager
                        .beginTransaction().replace(R.id.homepage_frame, new ProductFrag(productId))
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    public class SearchProductsViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productSeller;
        TextView productPrice;
        RelativeLayout productParent;

        public SearchProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView)itemView.findViewById(R.id.search_product_image);
            productName = (TextView)itemView.findViewById(R.id.search_product_name);
            productSeller = (TextView)itemView.findViewById(R.id.search_product_seller);
            productPrice = (TextView)itemView.findViewById(R.id.search_product_price);
            productParent = (RelativeLayout)itemView.findViewById(R.id.search_product_parent);
        }
    }
}
