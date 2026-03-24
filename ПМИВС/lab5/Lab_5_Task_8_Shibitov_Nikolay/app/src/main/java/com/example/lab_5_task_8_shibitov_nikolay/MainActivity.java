package com.example.lab_5_task_8_shibitov_nikolay;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_LOCATION_UPDATE =
            "com.example.lab_5_task_8_shibitov_nikolay.LOCATION_UPDATE";
    private static final int REQUEST_LOCATION = 1;

    private LocationManager locationManager;
    private PendingIntent locationPendingIntent;
    private BroadcastReceiver locationReceiver;
    private boolean tracking = false;

    private TextView tvStatus, tvLatitude, tvLongitude, tvAltitude,
                     tvAccuracy, tvSpeed, tvProvider, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus   = findViewById(R.id.tvStatus);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude= findViewById(R.id.tvLongitude);
        tvAltitude = findViewById(R.id.tvAltitude);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvSpeed    = findViewById(R.id.tvSpeed);
        tvProvider = findViewById(R.id.tvProvider);
        tvTime     = findViewById(R.id.tvTime);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // PendingIntent for location updates via BroadcastReceiver
        Intent locationIntent = new Intent(ACTION_LOCATION_UPDATE);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        locationPendingIntent = PendingIntent.getBroadcast(this, 0, locationIntent, flags);

        // Register BroadcastReceiver to receive location updates
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_LOCATION_UPDATE.equals(intent.getAction())) {
                    Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
                    if (location != null) {
                        updateUI(location);
                    }
                }
            }
        };

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop  = findViewById(R.id.btnStop);
        btnStart.setOnClickListener(v -> startTracking());
        btnStop.setOnClickListener(v -> stopTracking());
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        tracking = true;
        tvStatus.setText("Отслеживание (PendingIntent)");
        IntentFilter filter = new IntentFilter(ACTION_LOCATION_UPDATE);
        registerReceiver(locationReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 1, locationPendingIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Ошибка доступа к GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopTracking() {
        if (!tracking) return;
        tracking = false;
        tvStatus.setText("Остановлено");
        locationManager.removeUpdates(locationPendingIntent);
        try {
            unregisterReceiver(locationReceiver);
        } catch (IllegalArgumentException ignored) {}
    }

    private void updateUI(Location location) {
        tvLatitude.setText(String.format(Locale.getDefault(), "%.6f°", location.getLatitude()));
        tvLongitude.setText(String.format(Locale.getDefault(), "%.6f°", location.getLongitude()));
        tvAltitude.setText(String.format(Locale.getDefault(), "%.1f м", location.getAltitude()));
        tvAccuracy.setText(String.format(Locale.getDefault(), "%.1f м", location.getAccuracy()));
        tvSpeed.setText(String.format(Locale.getDefault(), "%.1f км/ч", location.getSpeed() * 3.6f));
        tvProvider.setText(location.getProvider());
        tvTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date(location.getTime())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tracking) stopTracking();
    }
}
