package com.ecommerce.cartify.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.math.MathContext;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private Context mContext;
    private String mUsername;
    private ArrayList<Product> mCartItems;
    private ArrayList<Integer> mCartItemQuantities;
    private FragmentManager mFragmentManager;

    public CartAdapter(Context mContext, String mUsername, ArrayList<Product> mCartItems,
                       ArrayList<Integer> mCartItemQuantities,
                       FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mUsername = mUsername;
        this.mCartItems = mCartItems;
        this.mCartItemQuantities = mCartItemQuantities;
        this.mFragmentManager = mFragmentManager;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        CartViewHolder cartViewHolder = new CartViewHolder(view);
        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        // Getting Cart Items Images
        Glide.with(mContext)
                .asBitmap()
                .load(mCartItems.get(position).getImage_url())
                .error(R.mipmap.ic_launcher)
                .into(holder.cartItemImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.cartItemImage.setClipToOutline(true);

        // Setting Cart Item Info Text Fields
        holder.cartItemName.setText(mCartItems.get(position).getProd_name());
        holder.cartItemSeller.setText(mCartItems.get(position).getSeller());
        holder.cartItemPrice.setText("$" + String.valueOf(mCartItems.get(position).getPrice()));
        holder.cartItemQantity.setText(String.valueOf(mCartItemQuantities.get(position)));


        // Set Default States For Plus & Minus Buttons
        int currentQuantity = mCartItemQuantities.get(position);
        int itemStock = mCartItems.get(position).getQuantity();

        if(currentQuantity == itemStock)
            holder.plusBtn.setEnabled(false);
        else if(currentQuantity == 1)
            holder.minusBtn.setEnabled(false);


        // onClickListener for each Cart Item
        holder.cartItemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productId = mCartItems.get(position).getProd_id();
                mFragmentManager
                        .beginTransaction().replace(R.id.homepage_frame, new ProductFrag(productId))
                        .commit();
            }
        });

        //onClickListener for Cross Button
        holder.crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Customer Confirmation For Removing Item From Cart
                new AlertDialog.Builder(mContext)
                        .setTitle("Removing Item From Cart")
                        .setMessage("Do you really want to remove this item from your cart?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Adding loading bar
                                ProgressDialog loadingBar = new ProgressDialog(mContext);
                                loadingBar.setTitle("Removing...");
                                loadingBar.setMessage("Please wait while the item is being removed.");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();

                                // Removing Item From DB
                                Task<Void> removeTask = FirebaseDatabase.getInstance()
                                        .getReference("carts")
                                        .child(mUsername)
                                        .child(String.valueOf(mCartItems.get(position).getProd_id()))
                                        .removeValue();

                                removeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mCartItems.remove(position);
                                        mCartItemQuantities.remove(position);
                                        notifyDataSetChanged();
                                        loadingBar.dismiss();
                                        Toast.makeText(mContext, "Item Removed Successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingBar.dismiss();
                                                Toast.makeText(mContext, "Error. Item Not Removed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        // onClickListener for Plus Button
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.minusBtn.setEnabled(true);
                int currentQuantity = mCartItemQuantities.get(position);
                int itemStock = mCartItems.get(position).getQuantity();

                if(currentQuantity == itemStock-1)
                    holder.plusBtn.setEnabled(false);

                holder.cartItemQantity.setText(String.valueOf(currentQuantity + 1));
                mCartItemQuantities.set(position, currentQuantity + 1);

                FirebaseDatabase.getInstance()
                        .getReference("carts")
                        .child(mUsername)
                        .child(String.valueOf(mCartItems.get(position).getProd_id()))
                        .setValue(mCartItemQuantities.get(position));
            }
        });

        // onClickListener for Minus Button
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.plusBtn.setEnabled(true);
                int currentQuantity = mCartItemQuantities.get(position);
                int itemStock = mCartItems.get(position).getQuantity();

                if(currentQuantity == 2)
                    holder.minusBtn.setEnabled(false);

                holder.cartItemQantity.setText(String.valueOf(currentQuantity - 1));
                mCartItemQuantities.set(position, currentQuantity - 1);

                FirebaseDatabase.getInstance()
                        .getReference("carts")
                        .child(mUsername)
                        .child(String.valueOf(mCartItems.get(position).getProd_id()))
                        .setValue(mCartItemQuantities.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCartItems.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {

        // Cart Item Info
        ImageView cartItemImage;
        TextView cartItemName;
        TextView cartItemSeller;
        TextView cartItemPrice;
        TextView cartItemQantity;
        RelativeLayout cartItemParent;

        // Cart Item Buttons
        Button crossBtn;
        Button plusBtn;
        Button minusBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Instantiating Cart Item Info
            cartItemImage = (ImageView)itemView.findViewById(R.id.cart_item_image);
            cartItemName = (TextView)itemView.findViewById(R.id.cart_item_name);
            cartItemSeller = (TextView)itemView.findViewById(R.id.cart_item_seller);
            cartItemPrice = (TextView)itemView.findViewById(R.id.cart_item_price);
            cartItemQantity = (TextView)itemView.findViewById(R.id.cart_item_quantity);
            cartItemParent = (RelativeLayout)itemView.findViewById(R.id.cart_item_parent);

            // Instantiating Cart Item Buttons
            crossBtn = (Button)itemView.findViewById(R.id.cart_item_cross_btn);
            plusBtn = (Button)itemView.findViewById(R.id.cart_item_plus_btn);
            minusBtn = (Button)itemView.findViewById(R.id.cart_item_minus_btn);
        }
    }
}
