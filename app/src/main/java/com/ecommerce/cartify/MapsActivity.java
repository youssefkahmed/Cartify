package com.ecommerce.cartify;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ecommerce.cartify.Helpers.MyLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    MyLocationListener myLocationListener;

    // View Items
    EditText addressText;
    Button getAddressBtn;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        addressText = (EditText)findViewById(R.id.current_address_txt);
        getAddressBtn = (Button)findViewById(R.id.get_address_btn);
        doneBtn = (Button)findViewById(R.id.maps_done_btn);
        myLocationListener = new MyLocationListener(getApplicationContext());
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, myLocationListener);
        }catch (SecurityException ex){
            Toast.makeText(getApplicationContext(), "LOCATION INACCESSIBLE", Toast.LENGTH_SHORT).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getIntent().getStringExtra("username");
                int numOfUniqueItems = getIntent().getIntExtra("numOfUniqueItems", 0);
                int numOfTotalItems = getIntent().getIntExtra("numOfTotalItems", 0);
                int totalSum = getIntent().getIntExtra("totalSum", 0);


                Intent i = new Intent(getApplicationContext(), CheckoutActivity.class);
                i.putExtra("username", username);
                i.putExtra("numOfUniqueItems", numOfUniqueItems);
                i.putExtra("numOfTotalItems", numOfTotalItems);
                i.putExtra("totalSum", totalSum);
                i.putExtra("address", addressText.getText().toString());
                finish();
                startActivity(i);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(30.04441960, 31.235711600), 8));

        getAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                Location location = null;

                try {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }catch (SecurityException ex){
                    Toast.makeText(getApplicationContext(), "LOCATION INACCESSIBLE", Toast.LENGTH_SHORT).show();
                }


                if(location != null){
                    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        addressList = geocoder.getFromLocation(currentPosition.latitude, currentPosition.longitude, 1);

                        if(!addressList.isEmpty()){
                            String address = "";
                            for(int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++){
                                address += addressList.get(0).getAddressLine(i) + ", ";
                            }

                            mMap.addMarker(new MarkerOptions().position(currentPosition)
                                    .title("My Location").snippet(address))
                                    .setDraggable(true);
                            addressText.setText(address);
                        }

                    } catch (IOException e) {
                        mMap.addMarker(new MarkerOptions().position(currentPosition)
                                .title("My Location"));
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
                } else {
                    Toast.makeText(getApplicationContext(), "PLEASE WAIT UNTIL POSITION IS DETERMINED", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);

                    if(!addressList.isEmpty()){
                        String address = "";
                        for(int i = 0; i < addressList.get(0).getMaxAddressLineIndex(); i++){
                            address += addressList.get(0).getAddressLine(i) + ", ";
                        }
                        addressText.setText(address);
                    }else{
                        Toast.makeText(getApplicationContext(), "NO ADDRESS FOR THIS LOCATION.", Toast.LENGTH_SHORT).show();
                        addressText.getText().clear();
                    }
                }catch (IOException ex){
                    Toast.makeText(getApplicationContext(), "CONNECTION ERROR. CANNOT GET ADDRESS.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}