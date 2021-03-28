package com.ecommerce.cartify.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecommerce.cartify.MainActivity;
import com.ecommerce.cartify.R;

import static android.content.Context.MODE_PRIVATE;

// TODO Implement Profile Page
public class ProfileFrag extends Fragment {
    Context mContext;

    // Shared Preferences vars
    public static final String PREFS_NAME = "CartifyPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Instantiating Logout Button
        Button logoutBtn = (Button)view.findViewById(R.id.logout_btn);
        // Adding Logout Functionality
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MainActivity.class);

                new AlertDialog.Builder(getContext())
                        .setTitle("Logging out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                        .edit()
                                        .remove(PREF_USERNAME)
                                        .remove(PREF_PASSWORD)
                                        .apply();

                                getActivity().finish();
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
