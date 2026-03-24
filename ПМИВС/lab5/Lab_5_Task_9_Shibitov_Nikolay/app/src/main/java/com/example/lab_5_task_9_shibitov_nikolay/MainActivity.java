package com.example.lab_5_task_9_shibitov_nikolay;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Built-in cities: regional city (Perm is a regional capital) + city starting with "П"
    private static final List<String> BUILT_IN = Arrays.asList("Perm", "Prague");
    private static final String PREF_KEY = "user_cities";

    private final List<String> cities = new ArrayList<>();
    private final List<String> temperatures = new ArrayList<>();
    private CityAdapter adapter;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load cities: built-in + saved user cities
        cities.addAll(BUILT_IN);
        loadUserCities();

        // Init temperatures list with placeholders
        for (int i = 0; i < cities.size(); i++) temperatures.add("...");

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CityAdapter(cities, temperatures, new CityAdapter.OnCityClickListener() {
            @Override
            public void onClick(String city) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(String city, int position) {
                // Only allow deleting user-added cities
                if (position < BUILT_IN.size()) {
                    Toast.makeText(MainActivity.this, "Встроенный город нельзя удалить",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Удалить город?")
                        .setMessage(city)
                        .setPositiveButton("Удалить", (d, w) -> {
                            cities.remove(position);
                            temperatures.remove(position);
                            adapter.notifyItemRemoved(position);
                            saveUserCities();
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
                return true;
            }
        });
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddCityDialog());

        // Load temperatures for all cities
        refreshAll();
    }

    private void refreshAll() {
        for (int i = 0; i < cities.size(); i++) {
            final int idx = i;
            final String city = cities.get(i);
            executor.execute(() -> {
                try {
                    WeatherData data = WeatherApiHelper.fetchCurrentWeather(city);
                    String tempStr = String.format("%+.0f°C", data.temp);
                    handler.post(() -> {
                        if (idx < temperatures.size()) {
                            temperatures.set(idx, tempStr);
                            adapter.notifyItemChanged(idx);
                        }
                    });
                } catch (Exception e) {
                    handler.post(() -> {
                        if (idx < temperatures.size()) {
                            temperatures.set(idx, "Err");
                            adapter.notifyItemChanged(idx);
                        }
                    });
                }
            });
        }
    }

    private void showAddCityDialog() {
        EditText input = new EditText(this);
        input.setHint("Название города (на англ.)");
        new AlertDialog.Builder(this)
                .setTitle("Добавить город")
                .setView(input)
                .setPositiveButton("Добавить", (d, w) -> {
                    String city = input.getText().toString().trim();
                    if (city.isEmpty()) return;
                    int idx = cities.size();
                    cities.add(city);
                    temperatures.add("...");
                    adapter.notifyItemInserted(idx);
                    saveUserCities();
                    // Load temperature for new city
                    executor.execute(() -> {
                        try {
                            WeatherData data = WeatherApiHelper.fetchCurrentWeather(city);
                            String tempStr = String.format("%+.0f°C", data.temp);
                            handler.post(() -> {
                                temperatures.set(idx, tempStr);
                                adapter.notifyItemChanged(idx);
                            });
                        } catch (Exception e) {
                            handler.post(() -> {
                                temperatures.set(idx, "Ошибка");
                                adapter.notifyItemChanged(idx);
                                Toast.makeText(MainActivity.this,
                                        "Город не найден: " + city, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void saveUserCities() {
        SharedPreferences prefs = getSharedPreferences("weather", MODE_PRIVATE);
        List<String> userCities = cities.subList(BUILT_IN.size(), cities.size());
        prefs.edit().putString(PREF_KEY, String.join(",", userCities)).apply();
    }

    private void loadUserCities() {
        SharedPreferences prefs = getSharedPreferences("weather", MODE_PRIVATE);
        String saved = prefs.getString(PREF_KEY, "");
        if (!saved.isEmpty()) {
            for (String city : saved.split(",")) {
                if (!city.isBlank()) cities.add(city.trim());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
