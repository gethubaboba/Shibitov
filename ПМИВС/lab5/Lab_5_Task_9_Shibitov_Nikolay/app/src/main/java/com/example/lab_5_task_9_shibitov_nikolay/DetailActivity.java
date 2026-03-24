package com.example.lab_5_task_9_shibitov_nikolay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private TextView tvCity, tvTemp, tvDescription, tvFeelsLike,
                     tvHumidity, tvPressure, tvWind, tvVisibility;
    private LinearLayout forecastContainer;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String city = getIntent().getStringExtra("city");
        if (city == null || city.isEmpty()) { finish(); return; }

        tvCity        = findViewById(R.id.tvCity);
        tvTemp        = findViewById(R.id.tvTemp);
        tvDescription = findViewById(R.id.tvDescription);
        tvFeelsLike   = findViewById(R.id.tvFeelsLike);
        tvHumidity    = findViewById(R.id.tvHumidity);
        tvPressure    = findViewById(R.id.tvPressure);
        tvWind        = findViewById(R.id.tvWind);
        tvVisibility  = findViewById(R.id.tvVisibility);
        forecastContainer = findViewById(R.id.forecastContainer);
        progressBar   = findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(city);
        }

        loadWeather(city);
        loadForecast(city);
    }

    private void loadWeather(String city) {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                WeatherData data = WeatherApiHelper.fetchCurrentWeather(city);
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvCity.setText(data.cityName);
                    tvTemp.setText(String.format(Locale.getDefault(), "%+.1f°C", data.temp));
                    tvDescription.setText(capitalize(data.description));
                    tvFeelsLike.setText(String.format(Locale.getDefault(),
                            "Ощущается как: %+.1f°C", data.feelsLike));
                    tvHumidity.setText("Влажность: " + data.humidity + "%");
                    tvPressure.setText("Давление: " + data.pressure + " гПа");
                    tvWind.setText(String.format(Locale.getDefault(),
                            "Ветер: %.1f м/с", data.windSpeed));
                    tvVisibility.setText("Видимость: " + data.visibility / 1000.0 + " км");
                });
            } catch (Exception e) {
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadForecast(String city) {
        executor.execute(() -> {
            try {
                List<WeatherData.ForecastEntry> forecast = WeatherApiHelper.fetchForecast(city);
                handler.post(() -> showForecast(forecast));
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this,
                        "Прогноз недоступен: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showForecast(List<WeatherData.ForecastEntry> forecast) {
        forecastContainer.removeAllViews();
        for (WeatherData.ForecastEntry entry : forecast) {
            TextView tv = new TextView(this);
            tv.setPadding(0, 8, 0, 8);
            tv.setTextSize(15f);
            String line = String.format(Locale.getDefault(),
                    "%-10s %5s  %+5.1f°C  %s",
                    entry.dateLabel, entry.timeLabel, entry.temp,
                    capitalize(entry.description));
            tv.setText(line);
            tv.setTypeface(android.graphics.Typeface.MONOSPACE);
            forecastContainer.addView(tv);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
