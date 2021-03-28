package com.ecommerce.cartify.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.cartify.Adapters.CategoriesAdapter;
import com.ecommerce.cartify.Adapters.HotDealsAdapter;
import com.ecommerce.cartify.Adapters.OffersAdapter;
import com.ecommerce.cartify.Helpers.Capture;
import com.ecommerce.cartify.HomepageActivity;
import com.ecommerce.cartify.MainActivity;
import com.ecommerce.cartify.Models.Category;
import com.ecommerce.cartify.Models.Product;
import com.ecommerce.cartify.R;
import com.ecommerce.cartify.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFrag extends Fragment {

    View view;
    int VOICE_CODE = 1;
    Context mContext;

    // Offers
    private ArrayList<Product> offers = new ArrayList<>();

    // Category Names
    private ArrayList<String> categoryNames = new ArrayList<>();

    // Hot Deals
    private ArrayList<Product> hotDeals = new ArrayList<>();

    // View Items
    ImageButton searchBtn;
    ImageButton overflowBtn;


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

        overflowBtn = (ImageButton)view.findViewById(R.id.overflow_btn);
        overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating PopUp Menu
                PopupMenu popupMenu = new PopupMenu(getContext(), overflowBtn);
                // Inflating PopUp menu
                popupMenu.getMenuInflater()
                        .inflate(R.menu.menu_overflow, popupMenu.getMenu());
                // Registering popup with OnMenuItemClickListener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.voice_btn:
                                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                startActivityForResult(i, VOICE_CODE);
                                return true;
                            case R.id.scan_barcode_btn:
                                IntentIntegrator.forSupportFragment(HomeFrag.this)
                                        // Prompt to be displayed on camera activation
                                        .setPrompt("Press volume key to use flash.")
                                        // Enabling camera beep
                                        .setBeepEnabled(true)
                                        // Locking orientation
                                        .setOrientationLocked(true)
                                        // Assigning Capture class which should extend CaptureActivity
                                        .setCaptureActivity(Capture.class)
                                        // Starting barcode scan
                                        .initiateScan();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                // Displaying the PopUp Menu
                popupMenu.show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getOffers();
            getCategories();
            getHotDeals();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == VOICE_CODE && resultCode == getActivity().RESULT_OK){
            ArrayList<String> speechText = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            EditText searchText = (EditText)view.findViewById(R.id.search_txt);
            if(speechText.get(0) != null)
                searchText.setText(speechText.get(0));
            return;
        }

        else if(requestCode == VOICE_CODE && resultCode != getActivity().RESULT_OK){
            Toast.makeText(getContext(), "Sorry, could not hear that.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data
        );

        if(intentResult.getContents() != null){
            EditText searchText = (EditText)view.findViewById(R.id.search_txt);
            searchText.setText(intentResult.getContents());
        }else{
            Toast.makeText(getContext(), "Could not scan anything.", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void getOffers(){

        Query query = FirebaseDatabase.getInstance().getReference("products")
                                        .orderByKey().limitToLast(5);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    offers.add(product);
                }

//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                RecyclerView offersRV = (RecyclerView)view.findViewById(R.id.rv_offers);
                OffersAdapter offersAdapter = new OffersAdapter(getContext(), offers, getFragmentManager());
                offersRV.setAdapter(offersAdapter);
                offersRV.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                        mContext,
                        LinearLayoutManager.HORIZONTAL);
                offersRV.addItemDecoration(dividerItemDecoration);
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

                    assert category != null;
                    categoryNames.add(category.getCat_name());
                }

                RecyclerView categoriesRV = (RecyclerView)view.findViewById(R.id.rv_categories);

                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(
                        getContext(),
                        categoryNames,
                        getFragmentManager());

                categoriesRV.setAdapter(categoriesAdapter);
                categoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                        mContext,
                        LinearLayoutManager.HORIZONTAL);
                categoriesRV.addItemDecoration(dividerItemDecoration);
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

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    hotDeals.add(product);
                }

//                FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
//                        .getSupportFragmentManager();
                RecyclerView hotDealsRV = (RecyclerView)view.findViewById(R.id.rv_hot_deals);
                HotDealsAdapter hotDealsAdapter = new HotDealsAdapter(getContext(), hotDeals, getFragmentManager());
                hotDealsRV.setAdapter(hotDealsAdapter);
                hotDealsRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,
                        false));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                        mContext,
                        LinearLayoutManager.HORIZONTAL);
                hotDealsRV.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
