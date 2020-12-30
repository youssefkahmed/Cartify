package com.ecommerce.cartify.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.cartify.Adapters.OffersAdapter;
import com.ecommerce.cartify.R;

import java.util.ArrayList;

public class HomeFrag extends Fragment {

    private ArrayList<String> offerImages = new ArrayList<>();
    private ArrayList<String> offerNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        offerImages.add("https://amayei.nyc3.digitaloceanspaces.com/2019/10/58e336b26ee69cbfb21d906c57b8ac8f9cb53bdf.jpg");
        offerNames.add("Penguin 1");

        offerImages.add("https://animalia-life.com/data_images/penguin/penguin1.jpg");
        offerNames.add("Penguin 2");

        offerImages.add("https://www.pbs.org/wnet/nature/files/2014/10/Penguin-Main-1280x600.jpg");
        offerNames.add("Penguin 3");

        offerImages.add("https://upload.wikimedia.org/wikepedia/commons/0/08/South_Shetland-2016-Deception_Island%E2%80%93Chinstrap_penguin_%28Pygoscelis_antarctica%29_04.jpg");
        offerNames.add("Penguin 4");

        RecyclerView offersRV = (RecyclerView)view.findViewById(R.id.rv_offers);
        OffersAdapter offersAdapter = new OffersAdapter(this.getContext(), offerNames, offerImages);
        offersRV.setAdapter(offersAdapter);
        offersRV.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }
}
