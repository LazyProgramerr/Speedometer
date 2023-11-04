package com.example.lo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1; // Define the request code
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        TextView tv = findViewById(R.id.text);
        tv.setTextSize(40);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check for GPS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Get the speed from the location
                float speedInMetersPerSecond = location.getSpeed();
                // Convert speed to km/h
                float speedInKmph = (speedInMetersPerSecond * 3600) / 1000;
                float roundedSpeed = Math.round(speedInKmph * 100.0f) / 100.0f;
                
                tv.setText("Speed: " + roundedSpeed + " KMPH");
                // Check for internet connectivity
                if (isInternetAvailable(MainActivity.this)) {
                    String cityName = getCityName(location);
                    tv.append("\nCity: " + cityName);
                }
            }

            // Implement other LocationListener methods as needed
        };

        // Request GPS location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // Initialize the Geocoder
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    // Handle permission request results and other necessary code

    // Method to check for internet connectivity (You need to implement this)
    public boolean isInternetAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    return false;
}

    // Method to get the city name
    private String getCityName(Location location) {
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "City Not Found";
    }
}
