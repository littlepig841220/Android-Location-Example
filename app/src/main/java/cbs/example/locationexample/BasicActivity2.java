package cbs.example.locationexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.MessageFormat;
import java.util.Date;

public class BasicActivity2 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private TextView textView;
    private GoogleApiClient googleApiClient;
    private Location location;
    private static final String TAG = "basic-location-sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        textView = findViewById(R.id.textView);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
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
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
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
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}