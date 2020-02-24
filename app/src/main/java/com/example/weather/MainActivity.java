package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weather.Common.Common;
import com.example.weather.Weather.Weather;
import com.example.weather.Weather.WeatherResult;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;



public class MainActivity extends AppCompatActivity {
    private LinearLayout Weather_panel;
    private TextView TVTemp;
    private TextView TVValueTemp;
    private TextView TVPressure;
    private TextView TVValuePressure;
    private TextView TVWind;
    private TextView TVValueWind;
    private TextView TVhumidity;
    private TextView TVValuehumidity;
    private Button BOk;
    private EditText ETString;
    private ProgressBar ProBar;
    private TextView TVCloud;
    private TextView TVCloudValue;
    private TextView TVRain;
    private TextView TVRainValue;
    private ImageView Pic;
    CompositeDisposable compositeDisposable;
    Weather weather = new Weather();
    private TextView TVCCity;


    public void postRequest(String urlStr, String jsonBodyStr) throws IOException {



        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(jsonBodyStr.getBytes());
            outputStream.flush();
        }
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // ... do something with line
                }
            }
        } else {
            // ... do something with unsuccessful response
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TVTemp = (TextView) findViewById(R.id.Temperature);
        TVValueTemp = (TextView) findViewById(R.id.ValueTemp);
        TVPressure = (TextView) findViewById(R.id.pressure);
        TVValuePressure = (TextView) findViewById(R.id.ValuePressure);
        TVWind = (TextView) findViewById(R.id.Wind);
        TVValueWind = (TextView) findViewById(R.id.ValueWind);
        TVhumidity = (TextView) findViewById(R.id.humidity);
        TVValuehumidity = (TextView) findViewById(R.id.Valuehumidity);
        BOk = (Button) findViewById(R.id.Ok);
        ETString = (EditText) findViewById(R.id.ChooseCity);
        TVCloud = (TextView) findViewById(R.id.Cloud);
        TVCloudValue = (TextView) findViewById(R.id.ValueCloud);
        TVRain = (TextView) findViewById(R.id.Rain);
        TVRainValue = (TextView) findViewById(R.id.ValueRain);
        Pic = (ImageView) findViewById(R.id.pic);
        TVCCity = (TextView)findViewById(R.id.citys);

        //Weather_panel = (LinearLayout)item


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        BOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NeedfulCity = ETString.getText().toString();
                if (NeedfulCity.length() == 0) {
                    AlertDialog.Builder errorWindow = new AlertDialog.Builder(MainActivity.this);
                    errorWindow.setMessage("Вы не ввели город")
                            .setCancelable(true);
                    AlertDialog a = errorWindow.create();
                    a.setTitle("Ошибка");
                    a.show();

                } else {
                    String url = "http://api.openweathermap.org/data/2.5/find?q=" + NeedfulCity + "&type=like&APPID=c3f93d23f4afc931f07743b1f8a9ffc6";

                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                        String fd = "";
                        StringBuilder builder = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()))) {
                            String jsonString;

                            while ((jsonString = reader.readLine()) != null) {
                                builder.append(jsonString);
                                fd = builder.toString();

                            }

                            //получили наш JSON
                            JSONObject json = new JSONObject(fd);

                            //Зашли в лист
                            JSONArray JSON_array = json.getJSONArray("list");
                            JSONObject JSON = JSON_array.getJSONObject(0);

                            JSONObject JSON_main = JSON.getJSONObject("main");

                            //JSONObject country = citiesFrom.getJSONObject(0);

                            String temp = JSON_main.getString("temp");
                            temp = Float.toString(Math.round(Float.parseFloat(temp) - 273.15f)) + " ºC";
                            //countryTitle= Integer.toString((Integer.parseInt(countryTitle)) - 273);

                            String pressure = JSON_main.getString("pressure");
                            pressure = pressure + "00";
                            pressure = Integer.toString(Integer.parseInt((pressure)) / 133) + " mmHg";

                            String humidity = JSON_main.getString("humidity") + " %";


                            JSONObject JSON_wind = JSON.getJSONObject("wind");
                            String wind = JSON_wind.getString("speed") + " m/s";

                            JSONObject JSON_clouds = JSON.getJSONObject("clouds");
                            String clouds = JSON_clouds.getString("all") + " %";


                            JSONArray JSON_w = JSON.getJSONArray("weather");
                            JSONObject JSON_ww = JSON_w.getJSONObject(0);
                            String weather = JSON_ww.getString("main");

                            switch (weather) {
                                case "Rain":
                                    TVRainValue.setText("Дождь");
                                    Pic.setImageResource(R.mipmap.rain_foreground);
                                    break;
                                case "Snow":
                                    TVRainValue.setText("Снег");
                                    Pic.setImageResource(R.mipmap.snow_cat_foreground);
                                    break;
                                case "Clouds":
                                    TVRainValue.setText("Облачно");
                                    Pic.setImageResource(R.mipmap.clouds_foreground);
                                    break;
                                case "Mist":
                                    TVRainValue.setText("Туман");
                                    Pic.setImageResource(R.mipmap.haze_foreground);
                                    break;
                                case "Haze":
                                    TVRainValue.setText("Дымка");
                                    Pic.setImageResource(R.mipmap.haze_foreground);
                                    break;
                                case "Clear":
                                    TVRainValue.setText("Ясно");
                                    Pic.setImageResource(R.mipmap.sunny_foreground);
                                    break;
                                default:
                                    TVRainValue.setText(weather);
                                    break;
                            }


                            TVCCity.setText(NeedfulCity);
                            TVValueTemp.setText(temp);
                            TVValuePressure.setText(pressure);
                            TVValuehumidity.setText(humidity);
                            TVValueWind.setText(wind);
                            TVCloudValue.setText(clouds);


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        });
    }


}
