package com.ecommerce.cartify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Helpers.MyGlideApp;
import com.ecommerce.cartify.R;

import java.util.ArrayList;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder>{

    private Context mContext;
    private ArrayList<String> mOfferNames = new ArrayList<>();
    private ArrayList<String> mOfferImages = new ArrayList<>();

    public OffersAdapter(Context mContext, ArrayList<String> mOfferNames, ArrayList<String> mOfferImages) {
        this.mContext = mContext;
        this.mOfferNames = mOfferNames;
        this.mOfferImages = mOfferImages;
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
                .load(mOfferImages.get(position))
                .error(R.mipmap.ic_launcher)
                .into(holder.offerImage);

        holder.offerName.setText(mOfferNames.get(position));

        holder.offerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mOfferNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOfferNames.size();
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
