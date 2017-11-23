package com.aidina.training.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private Button btnRefresh;
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLocation = (TextView) findViewById(R.id.location);
        tvTemperature = (TextView) findViewById(R.id.temperature);
        tvHumidity = (TextView) findViewById(R.id.humidity);
        tvWindSpeed = (TextView) findViewById(R.id.wind_speed);
        tvCloudiness = (TextView) findViewById(R.id.cloudiness);

        btnRefresh = (Button) findViewById(R.id.button_refresh);
        ivIcon = (ImageView) findViewById(R.id.icon);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new WeatherDataRetrival().execute();
            }
        });
    }


    private class WeatherDataRetrival extends AsyncTask<Void, Void, String> {
        // 1735161
        private static final String WEATHER_SOURCE = "http://api.openweathermap.org/data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&id=1528675";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NetworkInfo networkInfo = ((ConnectivityManager) MainActivity.this
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // network connected
                URL url = null;
                try {
                    url = new URL(WEATHER_SOURCE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        if (bufferedReader != null) {
                            String readline;
                            StringBuffer strBuffer = new StringBuffer();
                            while ((readline=bufferedReader.readLine()) != null) {
                                strBuffer.append(readline);
                            }
                            return strBuffer.toString();
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                // no connection...
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result!=null) {
                final JSONObject weatherJSON;
                try {
                    weatherJSON = new JSONObject(result);
                    tvLocation.setText(weatherJSON.getString("name") + "," + weatherJSON.getJSONObject("sys").getString("country"));

                    tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + "mps");

                    tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + "%");

                    final JSONObject mainJSON = weatherJSON.getJSONObject("main");

                    tvTemperature.setText(String.format("%.0f", mainJSON.getDouble("temp") - 273.15));

                    tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity")) + "%");

                    final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");
                    if(weatherJSONArray.length()>0) {
                        int code = weatherJSONArray.getJSONObject(0).getInt("id");
                        ivIcon.setImageResource(getIcon(code));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private int getIcon(int code) {
            if (code >= 200 && code < 250)
                return R.drawable.ic_thunderstorm_large;
            else if (code >= 300 && code < 350)
                return R.drawable.ic_drizzle_large;
            else if (code >= 500 && code < 550)
                return R.drawable.ic_rain_large;
            else if (code >= 600 && code < 650)
                return R.drawable.ic_snow_large;
            else if (code == 800)
                return R.drawable.ic_day_clear_large;
            else if (code == 801)
                return R.drawable.ic_day_few_clouds_large;
            else if (code == 802)
                return R.drawable.ic_scattered_clouds_large;
            else if (code == 803 || code == 804)
                return R.drawable.ic_broken_clouds_large;
            else if (code >= 700 && code < 770)
                return R.drawable.ic_fog_large;
            else if (code == 781 || code == 900)
                return R.drawable.ic_tornado_large;
            else if (code == 905)
                return R.drawable.ic_windy_large;
            else if (code == 906)
                return R.drawable.ic_hail_large;
            else
                return R.drawable.ic_day_clear_large;


        }
    }

}
