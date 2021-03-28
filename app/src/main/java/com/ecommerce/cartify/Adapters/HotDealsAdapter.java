package com.ecommerce.cartify.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Fragments.ProductFrag;
import com.ecommerce.cartify.Helpers.MyGlideApp;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;

import java.util.ArrayList;

public class HotDealsAdapter extends RecyclerView.Adapter<HotDealsAdapter.HotDealsViewHolder>{

    private Context mContext;
    private ArrayList<Product> mHotDeals;
    private FragmentManager mFragmentManager;

    public HotDealsAdapter(Context mContext, ArrayList<Product> mHotDeals, FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mHotDeals = mHotDeals;
        this.mFragmentManager = mFragmentManager;
    }

    @NonNull
    @Override
    public HotDealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_deal, parent, false);
        HotDealsViewHolder hotDealsViewHolder = new HotDealsViewHolder(view);
        return hotDealsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HotDealsViewHolder holder, int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(mHotDeals.get(position).getImage_url())
                .error(R.mipmap.ic_launcher)
                .into(holder.hotDealImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.hotDealImage.setClipToOutline(true);

        holder.hotDealName.setText(mHotDeals.get(position).getProd_name());
        holder.hotDealSeller.setText(mHotDeals.get(position).getSeller());
        holder.hotDealPrice.setText("$" + String.valueOf(mHotDeals.get(position).getPrice()));

        holder.hotDealParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productId = mHotDeals.get(position).getProd_id();
                mFragmentManager
                        .beginTransaction().replace(R.id.homepage_frame, new ProductFrag(productId))
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHotDeals.size();
    }


    public class HotDealsViewHolder extends RecyclerView.ViewHolder {

        ImageView hotDealImage;
        TextView hotDealName;
        TextView hotDealSeller;
        TextView hotDealPrice;
        RelativeLayout hotDealParent;

        public HotDealsViewHolder(@NonNull View itemView) {
            super(itemView);
            hotDealImage = (ImageView)itemView.findViewById(R.id.hot_deal_image);
            hotDealName = (TextView)itemView.findViewById(R.id.hot_deal_name);
            hotDealSeller = (TextView)itemView.findViewById(R.id.hot_deal_seller);
            hotDealPrice = (TextView)itemView.findViewById(R.id.hot_deal_price);
            hotDealParent = (RelativeLayout)itemView.findViewById(R.id.hot_deal_parent);
        }
    }
}
