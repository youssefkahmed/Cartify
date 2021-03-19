package com.ecommerce.cartify.Helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.ecommerce.cartify.Models.Customer;
import com.ecommerce.cartify.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseHelper {

    private FirebaseDatabase mDatabase;

    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();;
    }

    public boolean checkLogin(String username, String password){

        final boolean[] result = {false};
        DatabaseReference dbRef = mDatabase.getReference("customers");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(username).exists() && snapshot.child(username).child(password).exists()){
                    result[0] = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

         });

        return result[0];
    }


    public boolean registerCustomer(Customer customer){

        DatabaseReference dbRef = mDatabase.getReference("customers");
        boolean[] result = {true};

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(customer.getUsername()).exists()){
                    HashMap<String, Object> custMap = new HashMap<>();

                    custMap.put("cust_name", customer.getName());
                    custMap.put("username", customer.getUsername());
                    custMap.put("email", customer.getEmail());
                    custMap.put("password", customer.getPassword());
                    custMap.put("gender", customer.getGender());
                    custMap.put("birth_date", customer.getBirthDate());
                    custMap.put("job", customer.getJob());

                    dbRef.child(customer.getUsername()).setValue(custMap)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    result[0] = false;
                                }
                            });
                }

                else{
                    result[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result[0] = false;
            }
        });

        return result[0];
    }

    public List<Product> getOffers(){

        List<Product> offers = new ArrayList<>();
        Query query = mDatabase.getReference("products").orderByKey().limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                offers.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    offers.add(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return offers;
    }
}
