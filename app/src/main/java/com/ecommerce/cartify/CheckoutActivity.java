package com.ecommerce.cartify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.cartify.Helpers.SendMailTask;
import com.ecommerce.cartify.Models.Customer;
import com.ecommerce.cartify.Models.Product;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
// TODO Display all info
// TODO Add location-picker via GPS
// TODO Add Confirm button with AlertDialog
// TODO Email customer the order details
// TODO Store order details in DB
// TODO Add Cancel button that goes back to HomepageActivity


public class CheckoutActivity extends AppCompatActivity{

    // Variables
    String username;
    int numOfUniqueItems;
    int numOfTotalItems;
    String checkoutDate;
    String address;
    int totalSum;

    // View Items
    TextView uniqueItemsTxt;
    TextView totalItemsTxt;
    TextView checkoutDateTxt;
    EditText addressTxt;
    TextView totalSumTxt;

    ImageButton locationPickerBtn;
    Button submitOrderBtn;
    Button cancelOrderBtn;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Instantiating variables
        username = getIntent().getStringExtra("username");
        numOfUniqueItems = getIntent().getIntExtra("numOfUniqueItems", 0);
        numOfTotalItems = getIntent().getIntExtra("numOfTotalItems", 0);
        checkoutDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        totalSum = getIntent().getIntExtra("totalSum", 0);

        // Instantiating View Items
        uniqueItemsTxt = (TextView)findViewById(R.id.unique_items_in_cart_txt);
        uniqueItemsTxt.setText(String.valueOf(numOfUniqueItems));

        totalItemsTxt = (TextView)findViewById(R.id.total_items_in_cart_txt);
        totalItemsTxt.setText(String.valueOf(numOfTotalItems));

        checkoutDateTxt = (TextView)findViewById(R.id.checkout_date_txt);
        checkoutDateTxt.setText(checkoutDate);

        addressTxt = (EditText) findViewById(R.id.address_txt);
        address = getIntent().getStringExtra("address");
        addressTxt.setText(address != null ? address : "");

        totalSumTxt = (TextView)findViewById(R.id.total_sum_txt);
        totalSumTxt.setText("$" + String.valueOf(totalSum));

        locationPickerBtn = (ImageButton) findViewById(R.id.location_picker_btn);
        submitOrderBtn = (Button) findViewById(R.id.submit_order_btn);
        cancelOrderBtn = (Button) findViewById(R.id.cancel_order_btn);

        // Setting onClick Listeners
            // Navigating to Map Activity
        locationPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("username", username);
                i.putExtra("numOfUniqueItems", numOfUniqueItems);
                i.putExtra("numOfTotalItems", numOfTotalItems);
                i.putExtra("totalSum", totalSum);
                startActivity(i);
            }
        });

            // Confirming Order
        submitOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = addressTxt.getText().toString();

                if(address == null || address.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "Please enter an address or select one using the location-picker",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(CheckoutActivity.this)
                        .setTitle("Submitting Order")
                        .setMessage("Are you sure you want to confirm this order?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SubmitOrder();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

            // Cancelling Order
        cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                i.putExtra("username", username);
                finish();
                startActivity(i);
            }
        });
    }

    private void SubmitOrder(){
        loadingBar = new ProgressDialog(CheckoutActivity.this);
        loadingBar.setTitle("Submitting...");
        loadingBar.setMessage("Please wait while your order is being submitted.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Setting Order ID
//                                DateFormat df = new SimpleDateFormat("yyMMddHHmmssZ", Locale.getDefault());
//                                String date = df.format(Calendar.getInstance().getTime());

        Map<String, Object> order = new HashMap<>();
        order.put("num_of_items", numOfUniqueItems);
        order.put("quantity_of_items", numOfTotalItems);
        order.put("total_sum", totalSum);
        order.put("address", address);

        Task<Void> submitTask = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(username)
                .push()
                .setValue(order);

                // On task completion, decrease ordered products' quantities
                // and empty cart, then send order details email
        submitTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UpdateProductQuantities();
            }
        })
                // Display connection error on failure
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Connection Error.",Toast.LENGTH_SHORT);
                    }
                });
    }

    private void UpdateProductQuantities(){
        // Getting all products in cart
        Query query = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(username)
                .orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Getting IDs and ordered quantities of all
                // products in Customer's cart
                ArrayList<Integer> productIds = new ArrayList<>();
                ArrayList<Integer> quantities = new ArrayList<>();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    // Getting all product IDs with their ordered quantities
                    productIds.add(Integer.parseInt(postSnapshot.getKey()));
                    quantities.add(postSnapshot.getValue(Integer.class));
                }

                // Looping over the IDs to decrease the quantity
                // of each product int the DB
                for(int i = 0; i < productIds.size(); i++){
                    int productId = productIds.get(i);
                    int quantity = quantities.get(i);
                    // Retrieving each product from the DB
                    Query query = FirebaseDatabase.getInstance()
                            .getReference("products")
                            .child(String.valueOf(productId));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // (1) Getting the product, changing its quantity
                            Product product = snapshot.getValue(Product.class);
                            product.setQuantity(
                                    product.getQuantity() - quantity
                            );
                            // (2) Then updating it in the DB
                            FirebaseDatabase.getInstance()
                                    .getReference("products")
                                    .child(String.valueOf(productId))
                                    .setValue(product);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Connection Error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Emptying cart
                EmptyCart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        getApplicationContext(),
                        "Connection Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void EmptyCart(){
        // Emptying User's Cart
        Task<Void> emptyCartTask = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(username)
                .removeValue();

        // Calling SendOrderDetails Function
        emptyCartTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SendOrderDetails();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Connection Error.",Toast.LENGTH_SHORT);
                    }
                });
    }

    private void SendOrderDetails(){
        // Sending order details to customer via email
        Query query = FirebaseDatabase.getInstance().getReference("customers")
                .child(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                assert customer != null;
                String custEmail =  customer.getEmail();

                // closing ProgressDialog
                loadingBar.dismiss();

                // Send Email
                String emailSender = "cartify.ecommerce.app@gmail.com";
                String emailPassword = "Cartifyapp";
                String emailSubject = "Order Details";
                String emailBody = "Your order details:\n" +
                        "Number of items: " + numOfUniqueItems + "\n" +
                        "Total Quantity: " + numOfTotalItems + "\n" +
                        "Address: " + address + "\n" +
                        "Total Sum: " + totalSum + "\n"
                        +"\nThank you for shopping at Cartify!";

                List<String> recipients = new ArrayList<>();
                recipients.add(custEmail);

                new SendMailTask(CheckoutActivity.this)
                        .execute(emailSender, emailPassword,
                                recipients, emailSubject, emailBody);

                Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                i.putExtra("username", username);
                finish();
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Connection Error.",Toast.LENGTH_SHORT);
            }
        });
    }
}