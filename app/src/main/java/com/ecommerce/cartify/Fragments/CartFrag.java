package com.ecommerce.cartify.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Adapters.CartAdapter;
import com.ecommerce.cartify.Adapters.SearchProductsAdapter;
import com.ecommerce.cartify.CheckoutActivity;
import com.ecommerce.cartify.HomepageActivity;
import com.ecommerce.cartify.MainActivity;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CartFrag extends Fragment {

    View view;
    String username;
    CartAdapter cartAdapter;
    Context mContext;

    // View Items
    RecyclerView cartItemsRV;
    TextView noCartItemsTxt;
    ProgressDialog loadingBar;
    Button checkoutBtn;
    Button clearCartBtn;

    // Cart Items and Their Quantities
    ArrayList<Product> cartItems = new ArrayList<>();
    ArrayList<Integer> cartItemQuantities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Getting username
        username = getActivity().getIntent().getStringExtra("username");

        // Instantiating View Items
        loadingBar = new ProgressDialog(getContext());
        checkoutBtn = (Button)view.findViewById(R.id.checkout_btn);
        clearCartBtn = (Button)view.findViewById(R.id.clear_cart_btn);

        // Setting RV and buttins to be initially invisible and
        // displaying no cart items message
        noCartItemsTxt = view.findViewById(R.id.no_cart_items_txt);
        cartItemsRV = view.findViewById(R.id.rv_cart_items);
        noCartItemsTxt.setVisibility(View.VISIBLE);
        cartItemsRV.setVisibility(View.INVISIBLE);
        checkoutBtn.setVisibility(View.INVISIBLE);
        clearCartBtn.setVisibility(View.INVISIBLE);

        // Adding Functionality
            // Checking out
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartItems.size() == 0){
                    checkoutBtn.setVisibility(View.INVISIBLE);
                    clearCartBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                int numOfTotalItems = 0;
                int totalSum = 0;
                for(int i = 0; i < cartItemQuantities.size(); i++){
                    numOfTotalItems += cartItemQuantities.get(i);
                    totalSum += (cartItems.get(i).getPrice() * cartItemQuantities.get(i));
                }

                Intent i = new Intent(getContext(), CheckoutActivity.class);
                i.putExtra("username", username);
                i.putExtra("numOfUniqueItems", cartItems.size());
                i.putExtra("numOfTotalItems", numOfTotalItems);
                i.putExtra("totalSum", totalSum);
                getActivity().finish();
                startActivity(i);

            }
        });

            // Clearing cart
        clearCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Customer Confirmation For Removing All Items From Cart
                new AlertDialog.Builder(getContext())
                        .setTitle("Removing All Items From Cart")
                        .setMessage("Do you really want to remove all items from your cart?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Adding loading bar
                                loadingBar.setTitle("Removing...");
                                loadingBar.setMessage("Please wait while all items are being removed.");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();

                                // Removing Item From DB
                                Task<Void> removeTask = FirebaseDatabase.getInstance()
                                        .getReference("carts")
                                        .child(username)
                                        .removeValue();

                                removeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Emptying Arrays and Refreshing Adapter
                                        cartItems.removeAll(cartItems);
                                        cartItemQuantities.removeAll(cartItemQuantities);
                                        cartAdapter.notifyDataSetChanged();

                                        // Changing View Items' Visibility
                                        noCartItemsTxt.setVisibility(View.VISIBLE);
                                        cartItemsRV.setVisibility(View.INVISIBLE);
                                        checkoutBtn.setVisibility(View.INVISIBLE);
                                        clearCartBtn.setVisibility(View.INVISIBLE);

                                        loadingBar.dismiss();
                                        Toast.makeText(getContext(), "Items Removed Successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingBar.dismiss();
                                                Toast.makeText(getContext(), "Error. Item Not Removed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Getting Items inside Cart
        try {
            getCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void getCartItems(){
        // Adding loading bar
        loadingBar.setTitle("Getting Cart Items...");
        loadingBar.setMessage("Please wait while your query is being processed.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Initialising query
        Query query = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(username);


        // Temporary Array To Hold Product IDs
        ArrayList<Integer> productIds = new ArrayList<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    productIds.add(Integer.valueOf(postSnapshot.getKey()));
                    cartItemQuantities.add(postSnapshot.getValue(Integer.TYPE));
                }

                getProducts(productIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                noCartItemsTxt.setVisibility(View.VISIBLE);
                cartItemsRV.setVisibility(View.INVISIBLE);
                checkoutBtn.setVisibility(View.INVISIBLE);
                clearCartBtn.setVisibility(View.INVISIBLE);
                loadingBar.dismiss();
            }
        });
    }

    private void getProducts(ArrayList<Integer> productIds){
        for(int i = 0; i < productIds.size(); i++){
            // Initialising query
            Query query = FirebaseDatabase.getInstance()
                    .getReference("products")
                    .orderByKey()
                    .equalTo(String.valueOf(productIds.get(i)));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot postSnapshot: snapshot.getChildren()){
                        Product product = postSnapshot.getValue(Product.class);
                        cartItems.add(product);
                    }

//                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
//                            .getSupportFragmentManager();

                    cartAdapter = new CartAdapter(getContext(), username, cartItems,
                            cartItemQuantities, getFragmentManager());

                    cartItemsRV.setAdapter(cartAdapter);
                    cartItemsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                            mContext, LinearLayoutManager.VERTICAL);
                    cartItemsRV.addItemDecoration(dividerItemDecoration);

                    if(cartItems.size() != 0) {
                        noCartItemsTxt.setVisibility(View.INVISIBLE);
                        cartItemsRV.setVisibility(View.VISIBLE);
                        checkoutBtn.setVisibility(View.VISIBLE);
                        clearCartBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        loadingBar.dismiss();
    }
}
