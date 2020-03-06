package com.example.mdb_weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    ImageView weatherImg;
    TextView dayOne, dayTwo, dayThree, dayFour, dayFive, daySix, daySeven;
    TextView tempOne, tempTwo, tempThree, tempFour, tempFive, tempSix, tempSeven;
    ImageView imgDayOne, imgDayTwo, imgDayThree, imgDayFour, imgDayFive, imgDaySix, imgDaySeven;
    Location userLoc;
    double latitude, longitude;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        key = Keys.getWeatherKey();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startGettingLocation();
        System.out.println(userLoc);
        //TODO: keep key secret
//        String key = "KEEP THIS IN GIT IGNORE";
//        double latitude = userLoc.getLatitude();
//        double longitude = userLoc.getLongitude();
//        String URL = "https://api.darksky.net/forecast/" + key + latitude + longitude;

        weatherImg = findViewById(R.id.weatherImg);
        tempOne = findViewById(R.id.tempOne);
        dayOne = findViewById(R.id.dayOne);
        imgDayOne = findViewById(R.id.imgDayOne);

        weatherImg.setImageResource(R.drawable.partly_cloudy_night);
        tempOne.setText("45 ºC");
        dayOne.setText("Mar 2");
        imgDayOne.setImageResource(R.drawable.cloudy);
        dayOne.setPaintFlags(dayOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    private void startGettingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    userLoc = location;
                    latitude = userLoc.getLatitude();
                    longitude = userLoc.getLongitude();
                    String URL = "https://api.darksky.net/forecast/" + key + latitude + longitude;
                    System.out.println(userLoc);
                }
            });
            fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Error", "Exception while getting the location: "+e.getMessage());
                }
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(MainActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
