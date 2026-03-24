package com.example.lab_5_task_6_shibitov_nikolay;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isTracking = false;

    private TextView tvStatus;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAltitude;
    private TextView tvAccuracy;
    private TextView tvSpeed;
    private TextView tvProvider;
    private TextView tvTime;
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus    = findViewById(R.id.tvStatus);
        tvLatitude  = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAltitude  = findViewById(R.id.tvAltitude);
        tvAccuracy  = findViewById(R.id.tvAccuracy);
        tvSpeed     = findViewById(R.id.tvSpeed);
        tvProvider  = findViewById(R.id.tvProvider);
        tvTime      = findViewById(R.id.tvTime);
        btnStart    = findViewById(R.id.btnStart);
        btnStop     = findViewById(R.id.btnStop);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateUI(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                tvStatus.setText("Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                tvStatus.setText("Provider disabled: " + provider);
            }
        };

        btnStart.setOnClickListener(v -> startTracking());
        btnStop.setOnClickListener(v -> stopTracking());
        btnStop.setEnabled(false);
    }

    private void startTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        isTracking = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        tvStatus.setText("GPS включен, ожидание сигнала...");

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            tvStatus.setText("GPS не включён. Включите GPS в настройках.");
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void stopTracking() {
        isTracking = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        tvStatus.setText("Остановлено");
        locationManager.removeUpdates(locationListener);
    }

    private void updateUI(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date(location.getTime()));

        tvLatitude.setText(String.format(Locale.getDefault(),  "%.6f°", location.getLatitude()));
        tvLongitude.setText(String.format(Locale.getDefault(), "%.6f°", location.getLongitude()));
        tvAltitude.setText(String.format(Locale.getDefault(),  "%.1f м", location.getAltitude()));
        tvAccuracy.setText(String.format(Locale.getDefault(),  "%.1f м", location.getAccuracy()));
        tvSpeed.setText(String.format(Locale.getDefault(),     "%.1f км/ч", location.getSpeed() * 3.6f));
        tvProvider.setText(location.getProvider());
        tvTime.setText(time);
        tvStatus.setText("Отслеживание активно");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Toast.makeText(this,
                        "Разрешение на геолокацию необходимо для работы приложения",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTracking) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTracking
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
