package com.joakimsvedin.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void updateLocationinfo(Location location) {
        Log.i("Locationinfo", location.toString());

        TextView latTextView = (TextView) findViewById(R.id.LatTextView);
        TextView lonTextView = (TextView) findViewById(R.id.LonTextView);
        TextView accuracyView = (TextView) findViewById(R.id.AccuracyTextView);
        TextView altitudeTextView = (TextView) findViewById(R.id.AltitudeTextView);

        latTextView.setText("Latitude:" + location.getLatitude());
        lonTextView.setText("Longitude: " + location.getLongitude());
        accuracyView.setText("Accuracy:" + location.getAccuracy());
        altitudeTextView.setText("Altitude: " + location.getAltitude());

        // Get User's Address using the Geocoder class
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find address"; // default message if no address is found

            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(listAddresses != null && listAddresses.size() > 0){
                Log.i("Place info",listAddresses.get(0).toString());

                address = "Address: \n"; // set address to empty string, now that we know an address was successfully found

                if(listAddresses.get(0).getSubThoroughfare() != null){
                    address += listAddresses.get(0).getSubThoroughfare() + " ";
                }
                if(listAddresses.get(0).getThoroughfare() != null){
                    address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if(listAddresses.get(0).getLocality() != null){
                    address += listAddresses.get(0).getLocality() + "\n";
                }
                if(listAddresses.get(0).getPostalCode() != null){
                    address += listAddresses.get(0).getPostalCode() + "\n";
                }
                if(listAddresses.get(0).getCountryName() != null){
                    address += listAddresses.get(0).getCountryName() + "\n";
                }
            }

            TextView addressTextView = (TextView) findViewById(R.id.AddressTextView);
            addressTextView.setText(address);

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i("Location info",location.toString());
                updateLocationinfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

//        if(Build.VERSION.SDK_INT < 23){
//            startListening();
//        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                updateLocationinfo(location);
            }
        }
    }
}