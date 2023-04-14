package cbs.example.locationexample.googleapiclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import cbs.example.locationexample.R;

public class GACAutoUpdateActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult>,
        CompoundButton.OnCheckedChangeListener {
    private TextView textView,textView2;
    private Switch aSwitch;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private Location currentLocation;
    private Boolean requestingLocationUpdates = false;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_seting);

        textView = findViewById(R.id.textView5);
        textView2 = findViewById(R.id.textView6);
        aSwitch = findViewById(R.id.switch1);

        aSwitch.setOnCheckedChangeListener(this);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates && googleApiClient.isConnected()){
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestingLocationUpdates && googleApiClient.isConnected()){
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            updateLocationUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        updateLocationUI();
        Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS: {
                System.out.println("All location settings are satisfied.");
                break;
            }
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                System.out.println("Location settings are not satisfied. Show the user a dialog to upgrade location settings");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    System.out.println("PendingIntent unable to execute request.");
                }
                break;
            }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                System.out.println("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                break;
            }

        }
    }

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

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);//最佳化電池用量
        locationRequest.setFastestInterval(5000);//用程式處理位置資訊更新的最快速度
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private void updateLocationUI() {
        if (currentLocation != null) {
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
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

            }
        });
    }



    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

            }
        });
    }
}