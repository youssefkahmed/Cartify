package com.ecommerce.cartify.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.cartify.Adapters.HotDealsAdapter;
import com.ecommerce.cartify.Adapters.SearchProductsAdapter;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFrag extends Fragment {

    View view;
    String searchQuery;

    // View Items
    RecyclerView productsRV;
    TextView noResultsTxt;

    // Products List
    ArrayList<Product> products = new ArrayList<>();

    public SearchFrag(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchForProducts();
        return view;
    }

    public void searchForProducts(){

        // Adding loading bar
        ProgressDialog loadingBar = new ProgressDialog(getContext());
        loadingBar.setTitle("Searching...");
        loadingBar.setMessage("Please wait while your query is being processed.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Initialising query
        Query query = FirebaseDatabase.getInstance()
                .getReference("products")
                .orderByChild("prod_name");

        // Setting RV to be initially invisible and
        // displaying no results message
        noResultsTxt = view.findViewById(R.id.no_results_txt);
        productsRV = view.findViewById(R.id.rv_products);
        noResultsTxt.setVisibility(View.VISIBLE);
        productsRV.setVisibility(View.INVISIBLE);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Product product = postSnapshot.getValue(Product.class);

                    assert product != null;
                    if(!product.getProd_name().toLowerCase().contains(searchQuery.toLowerCase())){
                        continue;
                    }
                    products.add(product);
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                SearchProductsAdapter searchProductsAdapter = new SearchProductsAdapter(getContext(), products, fragmentManager);
                productsRV.setAdapter(searchProductsAdapter);
                productsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                if(products.size() != 0) {
                    noResultsTxt.setVisibility(View.INVISIBLE);
                    productsRV.setVisibility(View.VISIBLE);
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                noResultsTxt.setVisibility(View.VISIBLE);
                productsRV.setVisibility(View.INVISIBLE);
                loadingBar.dismiss();
            }
        });
    }
}
