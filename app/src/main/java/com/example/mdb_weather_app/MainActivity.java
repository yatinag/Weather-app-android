package com.example.mdb_weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM, dd");
    private FusedLocationProviderClient fusedLocationClient;
    Geocoder geocoder;
    private RequestQueue mQueue;
    ImageView weatherImg;
    TextView currentTemp, currentLoc, currentDate;
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

        mQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        weatherImg = findViewById(R.id.weatherImg);
        currentTemp = findViewById(R.id.currentTemp);
        currentDate = findViewById(R.id.locationText);
        currentLoc = findViewById(R.id.dateText);
        tempOne = findViewById(R.id.tempOne);
        dayOne = findViewById(R.id.dayOne);
        imgDayOne = findViewById(R.id.imgDayOne);
        tempTwo = findViewById(R.id.tempTwo);
        dayTwo = findViewById(R.id.dayTwo);
        imgDayTwo = findViewById(R.id.imgDayTwo);
        tempThree = findViewById(R.id.tempThree);
        dayThree = findViewById(R.id.dayThree);
        imgDayThree = findViewById(R.id.imgDayThree);
        tempFour = findViewById(R.id.tempFour);
        dayFour = findViewById(R.id.dayFour);
        imgDayFour = findViewById(R.id.imgDayFour);
        tempFive = findViewById(R.id.tempFive);
        dayFive = findViewById(R.id.dayFive);
        imgDayFive = findViewById(R.id.imgDayFive);
        tempSix = findViewById(R.id.tempSix);
        daySix = findViewById(R.id.daySix);
        imgDaySix = findViewById(R.id.imgDaySix);
        tempSeven = findViewById(R.id.tempSeven);
        daySeven = findViewById(R.id.daySeven);
        imgDaySeven = findViewById(R.id.imgDaySeven);

        startGettingLocation();

        currentDate.setPaintFlags(currentDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayOne.setPaintFlags(dayOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayTwo.setPaintFlags(dayTwo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayThree.setPaintFlags(dayThree.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayFour.setPaintFlags(dayFour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayFive.setPaintFlags(dayFive.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        daySix.setPaintFlags(daySix.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        daySeven.setPaintFlags(daySeven.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
                    String URL = "https://api.darksky.net/forecast/" + key + "/" + latitude + "," + longitude;
                    parseJSON(URL);
                    System.out.println(URL);
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

    private void parseJSON(String URL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Setting current timing related weather features
                            Context c = getApplicationContext();
                            JSONObject current = response.getJSONObject("currently");
                            Date dt = new Date(current.getLong("time") * 1000L);
                            currentDate.setText(DateFormat.getDateInstance().format(dt));
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            currentLoc.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                            currentTemp.setText(current.getString("temperature") + "ºF");
                            String icon = current.getString("icon").replaceAll("-", "_");
                            int id = c.getResources().getIdentifier("drawable/" + icon, null, c.getPackageName());
                            weatherImg.setImageResource(id);

                            //Setting weekly timing related weather features
                            JSONArray weeklyData = response.getJSONObject("daily").getJSONArray("data");
                            TextView[] currentDayTracker = {dayOne, dayTwo, dayThree, dayFour, dayFive, daySix, daySeven};
                            ImageView[] currentImgTracker = {imgDayOne, imgDayTwo, imgDayThree, imgDayFour, imgDayFive, imgDayFive, imgDaySix, imgDaySeven};
                            TextView[] currentTempTracker = {tempOne, tempTwo, tempThree, tempFour, tempFive, tempSix, tempSeven};
                            for(int i = 0; i < weeklyData.length() - 1; i++) {
                                JSONObject tempObj = (JSONObject) weeklyData.get(i);
                                Date dtTemp = new Date(tempObj.getLong("time") * 1000L);
                                currentDayTracker[i].setText(dateFormat.format(dtTemp));

                                currentTempTracker[i].setText(tempObj.getString("temperatureLow").substring(0, 2) + " - " + tempObj.getString("temperatureHigh").substring(0, 2)  + "ºF");

                                String iconTemp = tempObj.getString("icon").replaceAll("-", "_");
                                int tempId = c.getResources().getIdentifier("drawable/" + iconTemp, null, c.getPackageName());
                                currentImgTracker[i].setImageResource(tempId);
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}
