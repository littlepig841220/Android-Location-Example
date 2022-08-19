package cbs.example.locationexample.method1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.MessageFormat;
import java.util.Date;

import cbs.example.locationexample.R;

public class BasicActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        textView = findViewById(R.id.textView);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    textView.setText(MessageFormat.format("Provider(提供者):{0}\nAccuracy(精準度):{1}\n" +
                                    "Altitude(高度):{2}\nAccuracy altitude(精準的高度):{3}\n" +
                                    "Longitude(經度):{4}\nLatitude:(緯度){5}\n" +
                                    "Bearing(方位):{6}\nAccuracy bearing(精確的方位):{7}\n" +
                                    "Speed(速度):{8}\nAccuracy speed(精確的速度){9}\n" +
                                    "Time(時間):{10}",
                            location.getProvider(), location.getAccuracy(),
                            location.getAltitude(), location.getVerticalAccuracyMeters(),
                            location.getLongitude(), location.getLatitude(),
                            location.getBearing(), location.getBearingAccuracyDegrees(),
                            location.getSpeed(), location.getSpeedAccuracyMetersPerSecond(),
                            new Date(location.getTime())));
                    //Toast.makeText(getApplicationContext(),String.valueOf(location.getAltitude()),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

//https://www.geeksforgeeks.org/how-to-get-current-location-in-android/