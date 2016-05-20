package com.example.achypur.notepadapp.JsonObjects;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.achypur.notepadapp.Entities.Coordinate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForecastFetcher extends AsyncTask<Coordinate, Void, Forecast> {

    private final static String API_KEY = "4a9a966b470dff0a17f71d7526d05746";

    @Override
    protected Forecast doInBackground(Coordinate... params) {

        Forecast forecast = null;
        int i = 0;
        for (Coordinate coordinate : params) {
            try {
                forecast = readForecast(coordinate);
                forecast.setIcon(readIcon(forecast, i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return forecast;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private Forecast readForecast(Coordinate coordinate) {
        Forecast forecast = null;
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + coordinate.getLatitude() + "&lon=" + coordinate.getLongtitude() + "&appid=" + API_KEY);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            if (response == 200) {
                Reader reader = new InputStreamReader((InputStream) conn.getContent());

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                forecast = gson.fromJson(reader, Forecast.class);
                double celsius = covertKelvinToCelsius(forecast.getmMain().getmTemperature());
                forecast.getmMain().setmTemperature(celsius);


                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecast;
    }

    private byte[] readIcon(Forecast forecast, int position) {
        byte[] icon = null;
        try {
            URL imageUrl = new URL("http://openweathermap.org/img/w/" + forecast.getmWeather().get(position).getmIconWeatherId() + ".png");
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = (InputStream) conn.getContent();

            icon = getBytes(inputStream);
            inputStream.close();
            conn.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    private double covertKelvinToCelsius(double kelvin) {
        return Math.round(kelvin) - 273;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
