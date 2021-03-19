package com.ecommerce.cartify.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.cartify.Adapters.CategoriesAdapter;
import com.ecommerce.cartify.Adapters.HotDealsAdapter;
import com.ecommerce.cartify.Adapters.OffersAdapter;
import com.ecommerce.cartify.Models.Category;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFrag extends Fragment {

    View view;

    // Offers
    private ArrayList<Product> offers = new ArrayList<>();

    // Category Names
    private ArrayList<String> categoryNames = new ArrayList<>();

    // Hot Deals
    private ArrayList<Product> hotDeals = new ArrayList<>();

    // View Items
    ImageButton searchBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

//        List<Product> offers = fbHelper.getOffers();
//        for(int i = 0; i < offers.size(); i++){
//            offerNames.add(offers.get(i).getProd_name());
//            offerImages.add(offers.get(i).getImage_url());
//        }
//
//        offerImages.add("https://amayei.nyc3.digitaloceanspaces.com/2019/10/58e336b26ee69cbfb21d906c57b8ac8f9cb53bdf.jpg");
//        offerNames.add("Penguin 1");
//
//        offerImages.add("https://animalia-life.com/data_images/penguin/penguin1.jpg");
//        offerNames.add("Penguin 2");
//
//        offerImages.add("https://www.pbs.org/wnet/nature/files/2014/10/Penguin-Main-1280x600.jpg");
//        offerNames.add("Penguin 3");
//
//        offerImages.add("https://upload.wikimedia.org/wikepedia/commons/0/08/South_Shetland-2016-Deception_Island%E2%80%93Chinstrap_penguin_%28Pygoscelis_antarctica%29_04.jpg");
//        offerNames.add("Penguin 4");

//        RecyclerView offersRV = (RecyclerView)view.findViewById(R.id.rv_offers);
//        OffersAdapter offersAdapter = new OffersAdapter(this.getContext(), offerNames, offerImages);
//        offersRV.setAdapter(offersAdapter);
//        offersRV.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        searchBtn = (ImageButton)view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = ((EditText)view.findViewById(R.id.search_txt))
                        .getText().toString();

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction().replace(R.id.homepage_frame, new SearchFrag(searchQuery))
                        .commit();
            }
        });

        getOffers();
        getCategories();
        getHotDeals();
        return view;
    }

    public void getOffers(){

        Query query = FirebaseDatabase.getInstance().getReference("products")
                                        .orderByKey().limitToLast(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    offers.add(product);
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                RecyclerView offersRV = (RecyclerView)view.findViewById(R.id.rv_offers);
                OffersAdapter offersAdapter = new OffersAdapter(getContext(), offers, fragmentManager);
                offersRV.setAdapter(offersAdapter);
                offersRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCategories(){

        Query query = FirebaseDatabase.getInstance().getReference("categories")
                .orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);

                    categoryNames.add(category.getCat_name());

                    RecyclerView categoriesRV = (RecyclerView)view.findViewById(R.id.rv_categories);
                    CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categoryNames);
                    categoriesRV.setAdapter(categoriesAdapter);
                    categoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getHotDeals(){

        Query query = FirebaseDatabase.getInstance().getReference("products")
                                .orderByChild("price").limitToFirst(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    hotDeals.add(product);
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                RecyclerView hotDealsRV = (RecyclerView)view.findViewById(R.id.rv_hot_deals);
                HotDealsAdapter hotDealsAdapter = new HotDealsAdapter(getContext(), hotDeals, fragmentManager);
                hotDealsRV.setAdapter(hotDealsAdapter);
                hotDealsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
