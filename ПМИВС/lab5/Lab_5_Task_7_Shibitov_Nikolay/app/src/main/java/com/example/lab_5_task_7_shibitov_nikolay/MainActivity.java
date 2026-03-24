package com.example.lab_5_task_7_shibitov_nikolay;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean tracking = false;

    private TextView tvStatus, tvLatitude, tvLongitude, tvAltitude,
                     tvAccuracy, tvSpeed, tvProvider, tvTime, tvGpxFile;

    private final List<Location> trackPoints = new ArrayList<>();

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
        tvGpxFile  = findViewById(R.id.tvGpxFile);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (tracking) {
                    trackPoints.add(location);
                    updateUI(location);
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
        trackPoints.clear();
        tvStatus.setText("Отслеживание...");
        tvGpxFile.setText("GPX: не сохранён");
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(this, "Ошибка доступа к GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopTracking() {
        if (!tracking) return;
        tracking = false;
        locationManager.removeUpdates(locationListener);
        tvStatus.setText("Остановлено");
        if (!trackPoints.isEmpty()) {
            saveGpx();
        }
    }

    private void updateUI(Location location) {
        String fmt = "%.6f°";
        tvLatitude.setText(String.format(Locale.getDefault(), fmt, location.getLatitude()));
        tvLongitude.setText(String.format(Locale.getDefault(), fmt, location.getLongitude()));
        tvAltitude.setText(String.format(Locale.getDefault(), "%.1f м", location.getAltitude()));
        tvAccuracy.setText(String.format(Locale.getDefault(), "%.1f м", location.getAccuracy()));
        tvSpeed.setText(String.format(Locale.getDefault(), "%.1f км/ч", location.getSpeed() * 3.6f));
        tvProvider.setText(location.getProvider());
        tvTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date(location.getTime())));
    }

    private void saveGpx() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File gpxFile = new File(dir, "track_" + timestamp + ".gpx");

        try (FileWriter writer = new FileWriter(gpxFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"RunTracker\"\n");
            writer.write("  xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
            writer.write("  <trk>\n");
            writer.write("    <name>RunTracker " + timestamp + "</name>\n");
            writer.write("    <trkseg>\n");
            for (Location loc : trackPoints) {
                writer.write(String.format(Locale.US,
                        "      <trkpt lat=\"%.6f\" lon=\"%.6f\">\n",
                        loc.getLatitude(), loc.getLongitude()));
                writer.write(String.format(Locale.US,
                        "        <ele>%.1f</ele>\n", loc.getAltitude()));
                writer.write("        <time>" +
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                                .format(new Date(loc.getTime())) +
                        "</time>\n");
                writer.write("      </trkpt>\n");
            }
            writer.write("    </trkseg>\n");
            writer.write("  </trk>\n");
            writer.write("</gpx>\n");

            tvGpxFile.setText("GPX: " + gpxFile.getName() + " (" + trackPoints.size() + " точек)");
            Toast.makeText(this, "GPX сохранён: " + gpxFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка записи GPX: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
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
    protected void onPause() {
        super.onPause();
        if (tracking) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tracking && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } catch (SecurityException ignored) {}
        }
    }
}
