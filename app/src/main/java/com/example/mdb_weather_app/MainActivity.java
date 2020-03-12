package com.example.mdb_weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
    String[] allData = {"precipIntensity", "precipProbability", "dewPoint", "humidity",
            "pressure", "windSpeed", "windGust", "windBearing", "cloudCover", "uvIndex", "visibility", "ozone"};
    HashMap<String, String> cleanData = new HashMap<>();
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
    boolean isFahren = true;
    Switch tempSwitch;
    FlexboxLayout dataList;
    TextView currDesc;
    Button submitBtn, cityBtn;
    Calendar dateVal;
    CalendarView calendarView;
    Calendar currDateVal = Calendar.getInstance();

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
        tempSwitch = findViewById(R.id.tempSwitch);
        dataList = findViewById(R.id.dataList);
        currDesc = findViewById(R.id.currDesc);
        submitBtn = findViewById(R.id.submitBtn);
        calendarView = findViewById(R.id.calendarDate);

        cleanData.put("precipIntensity", "Precipitation Intensity");
        cleanData.put("precipProbability", "Precipitation Probability");
        cleanData.put("dewPoint", "Dew Point(ºF)");
        cleanData.put("humidity", "Humidity");
        cleanData.put("pressure", "Pressure");
        cleanData.put("windSpeed", "Wind Speed");
        cleanData.put("windGust",  "Wind Gust");
        cleanData.put("windBearing", "Wind Direction(º)");
        cleanData.put("cloudCover", "Cloud Covering(%)");
        cleanData.put("uvIndex", "UV Index");
        cleanData.put("visibility", "Visibility");
        cleanData.put("ozone", "Ozone");
        dateVal = Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateVal.set(year, month, dayOfMonth);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateVal == null) {
                    Toast toast = Toast.makeText(MainActivity.this, "Please select a date", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(dateVal.getTimeInMillis() > currDateVal.getTimeInMillis()) {
                    Toast toast = Toast.makeText(MainActivity.this, "Please select a date in the past", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                else {
                    Intent i = new Intent(MainActivity.this, PastData.class);
                    i.putExtra("DateMil", dateVal.getTimeInMillis() / 1000L);
                    i.putExtra("Lat", latitude);
                    i.putExtra("Lon", longitude);
                    startActivity(i);
                    finish();
                }
            }
        });

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isFahren = false;
                } else {
                    isFahren = true;
                }
                tempSwitch.setText(correctTempUnit());
                dataList.removeAllViews();
                startGettingLocation();
            }
        });

        startGettingLocation();

        currentDate.setPaintFlags(currentDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        currentLoc.setPaintFlags(currentDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayOne.setPaintFlags(dayOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayTwo.setPaintFlags(dayTwo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayThree.setPaintFlags(dayThree.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayFour.setPaintFlags(dayFour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dayFive.setPaintFlags(dayFive.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        daySix.setPaintFlags(daySix.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        daySeven.setPaintFlags(daySeven.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        try {
            String location = "New Delhi";
            Geocoder gc = new Geocoder(this);
            List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects

            double lat = 0, lon = 0;
            for(Address a : addresses){
                if(a.hasLatitude() && a.hasLongitude()){
                    lat = a.getLatitude();
                    lon = a.getLongitude();
                }
            }
            System.out.println("BOOOMO: " + lat + " " + lon);

        } catch (IOException e) {
            // handle the exception
        }

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
                    @SuppressLint("SetTextI18n")
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
                            String tempCurr = current.getString("temperature");
                            currentTemp.setText(correctTempVal(tempCurr.substring(0, ((tempCurr.indexOf('.') == -1) ? tempCurr.length() : tempCurr.indexOf('.'))))  + correctTempUnit());
                            String icon = current.getString("icon").replaceAll("-", "_");
                            int id = c.getResources().getIdentifier("drawable/" + icon, null, c.getPackageName());
                            weatherImg.setImageResource(id);
                            JSONObject minutely = response.getJSONObject("minutely");
                            currDesc.setText(minutely.getString("summary"));
                            HashMap<String, String> dataMap = new HashMap<>();
                            System.out.println(dataMap);
                            for (String i : allData) {
                                try {
                                    dataMap.put(i, current.getString(i));
                                } catch (Exception ignored) {
                                }
                            }

                            for (Map.Entry<String, String> data : dataMap.entrySet()) {
                                Chip chip = new Chip(MainActivity.this);
                                chip.setText(cleanData.get(data.getKey()) + ": " + data.getValue());
                                chip.setGravity(Gravity.RIGHT);
                                chip.setCheckable(false);
                                chip.setClickable(false);
                                chip.setElevation(5);
                                chip.setTextSize(11);
                                chip.setChipStrokeWidth(1);
                                dataList.addView(chip);
                            }

                            //Setting weekly timing related weather features
                            JSONArray weeklyData = response.getJSONObject("daily").getJSONArray("data");
                            TextView[] currentDayTracker = {dayOne, dayTwo, dayThree, dayFour, dayFive, daySix, daySeven};
                            ImageView[] currentImgTracker = {imgDayOne, imgDayTwo, imgDayThree, imgDayFour, imgDayFive, imgDayFive, imgDaySix, imgDaySeven};
                            TextView[] currentTempTracker = {tempOne, tempTwo, tempThree, tempFour, tempFive, tempSix, tempSeven};
                            for(int i = 0; i < weeklyData.length() - 1; i++) {
                                JSONObject tempObj = (JSONObject) weeklyData.get(i);
                                Date dtTemp = new Date(tempObj.getLong("time") * 1000L);
                                currentDayTracker[i].setText(dateFormat.format(dtTemp));

                                String tempLow = tempObj.getString("temperatureLow");
                                String tempHigh = tempObj.getString("temperatureHigh");
                                currentTempTracker[i].setText("Min: " + correctTempVal(tempLow.substring(0, ((tempLow.indexOf('.') == -1) ? tempLow.length() : tempLow.indexOf('.')))) + correctTempUnit() + "\n"
                                        + "Max: " + correctTempVal(tempHigh.substring(0, ((tempHigh.indexOf('.') == -1) ? tempHigh.length() : tempHigh.indexOf('.')))) + correctTempUnit());

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

    private String correctTempUnit() {
        if (isFahren) {
            return getString(R.string.fahrenheit);
        }
        return getString(R.string.celsius);
    }

    private String correctTempVal(String val) {
        if (isFahren) {
            return val;
        }
        return Integer.toString(((Integer.parseInt(val) - 32) * 5 / 9));
    }

}
