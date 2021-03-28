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

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder>{

    private Context mContext;
    private ArrayList<Product> mOffers;
    private FragmentManager mFragmentManager;

    public OffersAdapter(Context mContext, ArrayList<Product> mOffers, FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mOffers = mOffers;
        this.mFragmentManager = mFragmentManager;
    }

    @NonNull
    @Override
    public OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        OffersViewHolder offersViewHolder = new OffersViewHolder(view);
        return offersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OffersViewHolder holder, int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(mOffers.get(position).getImage_url())
                .error(R.mipmap.ic_launcher)
                .into(holder.offerImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.offerImage.setClipToOutline(true);

        holder.offerName.setText(mOffers.get(position).getProd_name());

        holder.offerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productId = mOffers.get(position).getProd_id();
                mFragmentManager
                        .beginTransaction().replace(R.id.homepage_frame, new ProductFrag(productId))
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }


    public class OffersViewHolder extends RecyclerView.ViewHolder {

        ImageView offerImage;
        TextView offerName;
        RelativeLayout offerParent;

        public OffersViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage = (ImageView)itemView.findViewById(R.id.offer_image);
            offerName = (TextView)itemView.findViewById(R.id.offer_name);
            offerParent = (RelativeLayout)itemView.findViewById(R.id.offer_parent);
        }
    }
}
