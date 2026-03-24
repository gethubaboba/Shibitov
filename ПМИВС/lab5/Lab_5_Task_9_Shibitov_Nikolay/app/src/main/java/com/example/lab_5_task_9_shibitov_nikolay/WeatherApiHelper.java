package com.example.lab_5_task_9_shibitov_nikolay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherApiHelper {

    private static final String API_KEY = "1bafea42aff0cc6f6528b480f29ed028";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    public interface WeatherCallback {
        void onSuccess(WeatherData data);
        void onError(String error);
    }

    public interface ForecastCallback {
        void onSuccess(List<WeatherData.ForecastEntry> forecast);
        void onError(String error);
    }

    /** Fetches current weather for a city (call from background thread) */
    public static WeatherData fetchCurrentWeather(String city) throws Exception {
        String urlStr = BASE_URL + "weather?q=" + city
                + "&appid=" + API_KEY + "&units=metric&lang=ru";
        String json = get(urlStr);
        JSONObject obj = new JSONObject(json);

        if (obj.has("cod") && !obj.getString("cod").equals("200")
                && obj.getInt("cod") != 200) {
            throw new Exception(obj.optString("message", "Unknown error"));
        }

        WeatherData data = new WeatherData();
        data.cityName   = obj.getString("name");
        JSONObject main = obj.getJSONObject("main");
        data.temp       = main.getDouble("temp");
        data.feelsLike  = main.getDouble("feels_like");
        data.humidity   = main.getInt("humidity");
        data.pressure   = main.getInt("pressure");
        JSONArray weather = obj.getJSONArray("weather");
        data.description = weather.getJSONObject(0).getString("description");
        data.icon        = weather.getJSONObject(0).getString("icon");
        data.windSpeed   = obj.getJSONObject("wind").getDouble("speed");
        data.visibility  = obj.optInt("visibility", 0);
        return data;
    }

    /** Fetches 5-day / 3-hour forecast for a city (call from background thread) */
    public static List<WeatherData.ForecastEntry> fetchForecast(String city) throws Exception {
        String urlStr = BASE_URL + "forecast?q=" + city
                + "&appid=" + API_KEY + "&units=metric&lang=ru&cnt=24";
        String json = get(urlStr);
        JSONObject obj = new JSONObject(json);

        if (obj.has("cod") && !obj.getString("cod").equals("200")) {
            throw new Exception(obj.optString("message", "Unknown error"));
        }

        List<WeatherData.ForecastEntry> result = new ArrayList<>();
        JSONArray list = obj.getJSONArray("list");

        // Take one entry per day (midday: 12:00) — max 7 days
        String lastDate = "";
        SimpleDateFormat dayFmt  = new SimpleDateFormat("dd MMM", new Locale("ru"));
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < list.length(); i++) {
            JSONObject item  = list.getJSONObject(i);
            long dt          = item.getLong("dt") * 1000L;
            Date date        = new Date(dt);
            String dayLabel  = dayFmt.format(date);
            String timeLabel = timeFmt.format(date);

            if (!dayLabel.equals(lastDate)) {
                lastDate = dayLabel;
                WeatherData.ForecastEntry entry = new WeatherData.ForecastEntry();
                entry.dateLabel  = dayLabel;
                entry.timeLabel  = timeLabel;
                entry.temp       = item.getJSONObject("main").getDouble("temp");
                entry.description = item.getJSONArray("weather")
                        .getJSONObject(0).getString("description");
                entry.icon       = item.getJSONArray("weather")
                        .getJSONObject(0).getString("icon");
                result.add(entry);
                if (result.size() >= 7) break;
            }
        }
        return result;
    }

    private static String get(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        int code = conn.getResponseCode();
        BufferedReader reader;
        if (code == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        conn.disconnect();
        return sb.toString();
    }
}
