package com.ecommerce.cartify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ecommerce.cartify.Fragments.CartFrag;
import com.ecommerce.cartify.Fragments.HomeFrag;
import com.ecommerce.cartify.Fragments.ProfileFrag;
import com.ecommerce.cartify.Fragments.WishlistFrag;

public class HomepageActivity extends AppCompatActivity {

    FrameLayout homepageFrame;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Grabbing View Items
        homepageFrame = (FrameLayout)findViewById(R.id.homepage_frame);
        Button homeBtn = (Button)findViewById(R.id.home_btn);
        Button cartBtn = (Button)findViewById(R.id.cart_btn);
        Button wishlistBtn = (Button)findViewById(R.id.wishlist_btn);
        Button profileBtn = (Button)findViewById(R.id.profile_btn);

        // Setting onClick Listeners to navigate between Fragments
            // Navigating to Home Frag
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.homepage_frame, new HomeFrag())
                        .commit();
                updateIconColors("Home");
            }
        });

            // Navigating to Cart Frag
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.homepage_frame, new CartFrag())
                .commit();
                updateIconColors("Cart");
            }
        });

            // Navigating to Wishlist Frag
        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.homepage_frame, new WishlistFrag())
                        .commit();
                updateIconColors("Wishlist");
            }
        });

            // Navigating to Profile Frag
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.homepage_frame, new ProfileFrag())
                        .commit();
                updateIconColors("Profile");
            }
        });


        // Navigate to Home Fragment initially
        getSupportFragmentManager().beginTransaction().replace(R.id.homepage_frame, new HomeFrag())
                .commit();
        updateIconColors("Home");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateIconColors(String type){
        Button homeBtn = (Button)findViewById(R.id.home_btn);
        Button cartBtn = (Button)findViewById(R.id.cart_btn);
        Button wishlistBtn = (Button)findViewById(R.id.wishlist_btn);
        Button profileBtn = (Button)findViewById(R.id.profile_btn);

        homeBtn.setBackground(getDrawable(R.drawable.icon_home));
        cartBtn.setBackground(getDrawable(R.drawable.icon_cart));
        wishlistBtn.setBackground(getDrawable(R.drawable.icon_heart));
        profileBtn.setBackground(getDrawable(R.drawable.icon_user));

        switch (type){
            case "Home":
                homeBtn.setBackground(getDrawable(R.drawable.icon_home_activated));
            break;
            case "Cart":
                cartBtn.setBackground(getDrawable(R.drawable.icon_cart_activated));
            break;
            case "Wishlist":
                wishlistBtn.setBackground(getDrawable(R.drawable.icon_heart_activated));
            break;
            case "Profile":
                profileBtn.setBackground(getDrawable(R.drawable.icon_user_activated));
                break;
            default:
                break;
        }

    }

}