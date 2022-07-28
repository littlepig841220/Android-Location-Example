package cbs.example.locationexample;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

public class LocationSettingActivity extends AppCompatActivity implements OnSuccessListener<Location>, CompoundButton.OnCheckedChangeListener{
    private TextView textView, textView2;
    private Switch aSwitch;
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private boolean requestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_seting);

        textView = findViewById(R.id.textView5);
        textView2 = findViewById(R.id.textView6);
        aSwitch = findViewById(R.id.switch1);

        aSwitch.setOnCheckedChangeListener(this);

        createLocationRequest();
        buildLocationSettingsRequest();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestingLocationUpdates){
            stopLocationUpdates();
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            currentLocation = location;
            updateLocationUI();
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            currentLocation = locationResult.getLastLocation();
            if (currentLocation != null){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
                updateLocationUI();
            }
            /*for (Location location : locationResult.getLocations()) {
                currentLocation = location;
                if (currentLocation != null){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
                    updateLocationUI();
                }
            }*/
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()){
            startLocationUpdates();
            textView.setText("更新中的位置");
            requestingLocationUpdates = true;
        }else {
            stopLocationUpdates();
            textView.setText("最後的位置");
            requestingLocationUpdates = false;
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Something is wrong",Toast.LENGTH_SHORT).show();
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationSettingActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void updateLocationUI() {
        textView2.setText(MessageFormat.format("Provider(提供者):{0}\nAccuracy(精準度):{1}\n" +
                        "Altitude(高度):{2}\nAccuracy altitude(精準的高度):{3}\n" +
                        "Longitude(經度):{4}\nLatitude:(緯度){5}\n" +
                        "Bearing(方位):{6}\nAccuracy bearing(精確的方位):{7}\n" +
                        "Speed(速度):{8}\nAccuracy speed(精確的速度){9}\n" +
                        "Time(時間):{10}",
                currentLocation.getProvider(), currentLocation.getAccuracy(),
                currentLocation.getAltitude(), currentLocation.getVerticalAccuracyMeters(),
                currentLocation.getLongitude(), currentLocation.getLatitude(),
                currentLocation.getBearing(), currentLocation.getBearingAccuracyDegrees(),
                currentLocation.getSpeed(), currentLocation.getSpeedAccuracyMetersPerSecond(),
                DateFormat.getTimeInstance().format(new Date(currentLocation.getTime()))));
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
