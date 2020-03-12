package com.example.mdb_weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PastData extends AppCompatActivity {

    String[] allData = {"precipIntensity", "precipProbability", "dewPoint", "humidity",
            "pressure", "windSpeed", "windGust", "windBearing", "cloudCover", "uvIndex", "visibility", "ozone"};
    HashMap<String, String> cleanData = new HashMap<>();
    long dateMil;
    double longitude;
    double latitude;
    String key;
    Geocoder geocoder;
    private RequestQueue mQueue;
    ImageView weatherImg;
    boolean isFahren = false;
    TextView currentTemp, currentLoc, currentDate;
    TextView currDesc;
    FlexboxLayout dataList;
    Switch tempSwitch;
    String URL;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_data);

        key = Keys.getWeatherKey();

        mQueue = Volley.newRequestQueue(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        weatherImg = findViewById(R.id.weatherImg);
        currentTemp = findViewById(R.id.currentTemp);
        currentDate = findViewById(R.id.locationText);
        currentLoc = findViewById(R.id.dateText);
        currDesc = findViewById(R.id.currDesc);
        tempSwitch = findViewById(R.id.tempSwitch);
        dataList = findViewById(R.id.dataList);
        backBtn = findViewById(R.id.backBtn);

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

        dateMil = getIntent().getLongExtra("DateMil", 0);
        latitude = getIntent().getDoubleExtra("Lat", 0);
        longitude = getIntent().getDoubleExtra("Lon", 0);

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isFahren = true;
                } else {
                    isFahren = false;
                }
                tempSwitch.setText(correctTempUnit());
                dataList.removeAllViews();
               parseJSON(URL);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PastData.this, MainActivity.class);
                startActivity(i);
            }
        });

        if (dateMil == 0) {
            URL = "https://api.darksky.net/forecast/" + key + "/" + latitude + "," + longitude;
        } else {
            URL = "https://api.darksky.net/forecast/" + key + "/" + latitude + "," + longitude + "," + dateMil;
        }
        System.out.println(URL);

        parseJSON(URL);

        currentDate.setPaintFlags(currentDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        currentLoc.setPaintFlags(currentDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
                            JSONObject daily = response.getJSONObject("daily").getJSONArray("data").getJSONObject(0);
                            Date dt = new Date(dateMil * 1000L);
                            currentDate.setText(DateFormat.getDateInstance().format(dt));
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            currentLoc.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                            String tempCurrMin = daily.getString("temperatureLow");
                            String tempCurrMax = daily.getString("temperatureHigh");
                            currentTemp.setText("Min: " + correctTempVal(tempCurrMin.substring(0, ((tempCurrMin.indexOf('.') == -1) ? tempCurrMin.length() : tempCurrMin.indexOf('.')))) + correctTempUnit() + "\n"
                                            + "Max: " + correctTempVal(tempCurrMax.substring(0, ((tempCurrMax.indexOf('.') == -1) ? tempCurrMax.length() : tempCurrMax.indexOf('.')))) + correctTempUnit());
                            String icon = daily.getString("icon").replaceAll("-", "_");
                            int id = c.getResources().getIdentifier("drawable/" + icon, null, c.getPackageName());
                            weatherImg.setImageResource(id);
                            currDesc.setText(daily.getString("summary"));
                            HashMap<String, String> dataMap = new HashMap<>();
                            System.out.println(dataMap);
                            for (String i : allData) {
                                try {
                                    dataMap.put(i, daily.getString(i));
                                } catch (Exception ignored) {
                                }
                            }

                            for (Map.Entry<String, String> data : dataMap.entrySet()) {
                                Chip chip = new Chip(PastData.this);
                                chip.setText(cleanData.get(data.getKey()) + ": " + data.getValue());
                                chip.setGravity(Gravity.RIGHT);
                                chip.setCheckable(false);
                                chip.setClickable(false);
                                chip.setElevation(5);
                                chip.setTextSize(11);
                                chip.setChipStrokeWidth(1);
                                dataList.addView(chip);
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
