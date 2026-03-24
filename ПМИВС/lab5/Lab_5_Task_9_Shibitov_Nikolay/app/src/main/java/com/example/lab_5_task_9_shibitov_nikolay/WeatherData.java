package com.example.lab_5_task_9_shibitov_nikolay;

public class WeatherData {
    public String cityName;
    public double temp;
    public double feelsLike;
    public int humidity;
    public int pressure;
    public String description;
    public double windSpeed;
    public int visibility;
    public String icon;

    // Forecast day entry
    public static class ForecastEntry {
        public String dateLabel; // e.g. "24 мар"
        public String timeLabel; // e.g. "12:00"
        public double temp;
        public String description;
        public String icon;
    }
}
