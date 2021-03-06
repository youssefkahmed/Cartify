package com.ecommerce.cartify.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.cartify.Fragments.SearchFrag;
import com.ecommerce.cartify.R;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private Context mContext;
    private ArrayList<String> mCategoryNames;
    private FragmentManager mFragmentManager;


    public CategoriesAdapter(Context mContext, ArrayList<String> mCategoryNames,
                             FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mCategoryNames = mCategoryNames;
        this.mFragmentManager = mFragmentManager;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        CategoriesAdapter.CategoriesViewHolder categoriesViewHolder = new CategoriesAdapter.CategoriesViewHolder(view);
        return categoriesViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        // Setting Category Image depending on Category Name
        switch(mCategoryNames.get(position)){
            case "Electronics":
                holder.categoryImage.setImageResource(R.drawable.electronics);
                break;
            case "Fashion":
                holder.categoryImage.setImageResource(R.drawable.fashion);
                break;
            case "Fitness":
                holder.categoryImage.setImageResource(R.drawable.fitness);
                break;
            case "Games":
                holder.categoryImage.setImageResource(R.drawable.games);
                break;
            case "Toys":
                holder.categoryImage.setImageResource(R.drawable.toys);
                break;
            default:
                holder.categoryImage.setImageResource(R.drawable.appliances);
                break;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.categoryImage.setClipToOutline(true);
        holder.categoryName.setText(mCategoryNames.get(position));

        holder.categoryParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mCategoryNames.get(position);
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.homepage_frame, new SearchFrag(searchQuery, true))
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryNames.size();
    }


    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryName;
        RelativeLayout categoryParent;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = (ImageView)itemView.findViewById(R.id.category_image);
            categoryName = (TextView)itemView.findViewById(R.id.category_name);
            categoryParent = (RelativeLayout)itemView.findViewById(R.id.category_parent);
        }
    }

}
