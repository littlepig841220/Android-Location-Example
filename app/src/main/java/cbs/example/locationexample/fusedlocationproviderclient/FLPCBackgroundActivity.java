package cbs.example.locationexample.fusedlocationproviderclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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

import cbs.example.locationexample.R;
import cbs.example.locationexample.services.LocationService;

public class FLPCBackgroundActivity extends AppCompatActivity implements OnSuccessListener<Location> , View.OnClickListener{
    private TextView textView, textView2;

    private LocationService locationService;
    private ServiceConnector serviceConnector = new ServiceConnector();
    private Receiver receiver = new Receiver();

    public LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    public FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        textView = findViewById(R.id.textView12);
        textView2 = findViewById(R.id.textView13);

        createLocationRequest();
        buildLocationSettingsRequest();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String Action = "LocationBackgroundService";
        IntentFilter intentFilter = new IntentFilter(Action);
        registerReceiver(receiver,intentFilter);

        bindService(new Intent(FLPCBackgroundActivity.this,LocationService.class), serviceConnector, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //locationService.startLocationUpdate(fusedLocationClient,locationRequest);
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

    @Override
    public void onClick(View view) {
        //finish();
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
                        resolvable.startResolutionForResult(FLPCBackgroundActivity.this,
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

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle message = intent.getExtras();
            String value = message.getString("returnString");
            textView2.setText(value);
            System.out.println("returnString:" + value);
        }
    }

    class ServiceConnector implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = ((LocationService.LocationBinder) iBinder).getService();
            locationService.startLocationUpdate(fusedLocationClient,locationRequest);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    }
}