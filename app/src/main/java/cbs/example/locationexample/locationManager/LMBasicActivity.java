package cbs.example.locationexample.locationManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssAntennaInfo;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cbs.example.locationexample.MenuActivity;
import cbs.example.locationexample.R;

public class LMBasicActivity extends AppCompatActivity implements View.OnClickListener {
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonPassive, buttonNetwork, buttonFused, buttonGPS;
    private TextView textViewDetail, textViewUpdate;
    private LocationManager locationManager;
    private final List<String> providers = new ArrayList<>();
    private String lastAccuracy = "null";
    private String lastAltitude = "null";
    private String lastBearing = "null";
    private String lastBearingAccuracyDegrees = "null";
    private String lastLatitude = "null";
    private String lastLongitude = "null";
    private String lastSpeed = "null";
    private String lastTime = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmbasic);

        buttonPassive = findViewById(R.id.button14);
        buttonNetwork = findViewById(R.id.button15);
        buttonFused = findViewById(R.id.button16);
        buttonGPS = findViewById(R.id.button17);
        textViewDetail = findViewById(R.id.textView9);
        textViewUpdate = findViewById(R.id.textView10);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        providers.addAll(locationManager.getAllProviders());

        for (String provider : providers) {
            switch (provider) {
                case LocationManager.PASSIVE_PROVIDER: {
                    buttonPassive.setEnabled(true);
                    break;
                }
                case LocationManager.NETWORK_PROVIDER: {
                    buttonNetwork.setEnabled(true);
                    break;
                }
                case LocationManager.FUSED_PROVIDER: {
                    buttonFused.setEnabled(true);
                    break;
                }
                case LocationManager.GPS_PROVIDER: {
                    buttonGPS.setEnabled(true);
                    break;
                }
                default: {
                    print("Unknown provider");
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "Passive": {
                getLocation(LocationManager.PASSIVE_PROVIDER);
                break;
            }
            case "Network": {
                getLocation(LocationManager.NETWORK_PROVIDER);
                break;
            }
            case "Fused": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    getLocation(LocationManager.FUSED_PROVIDER);
                } else {
                    print("Need Android 12");
                }
                break;
            }
            case "GPS": {
                getLocation(LocationManager.GPS_PROVIDER);
                break;
            }
            default: {
                print("Unknown action");
            }
        }
    }

    private void getLocation(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Define", Toast.LENGTH_SHORT).show();
        } else {
            String GNSSAntennaInformation;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                List<GnssAntennaInfo> information = locationManager.getGnssAntennaInfos();

                if (information != null) {
                    GNSSAntennaInformation = information.toString();
                } else {
                    GNSSAntennaInformation = " null";
                }
            } else {
                GNSSAntennaInformation = "Need Android 12(API 31)";
            }

            String GNSSCapabilities;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                GNSSCapabilities = locationManager.getGnssCapabilities().toString();
            } else {
                GNSSCapabilities = "Need Android 11(API 30)";
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(provider, null, getMainExecutor(), location -> {
                    if (location != null) {
                        lastAccuracy = String.valueOf(location.getAccuracy());
                        lastAltitude = String.valueOf(location.getAltitude());
                        lastBearing = String.valueOf(location.getBearing());
                        lastBearingAccuracyDegrees = String.valueOf(location.getBearingAccuracyDegrees());
                        lastLatitude = String.valueOf(location.getLatitude());
                        lastLongitude = String.valueOf(location.getLongitude());
                        lastSpeed = String.valueOf(location.getSpeed());
                        lastTime = String.valueOf(location.getTime());
                    }
                });
            } else {
                Location lastLocation = locationManager.getLastKnownLocation(provider);
                if (lastLocation != null) {
                    lastAccuracy = String.valueOf(lastLocation.getAccuracy());
                    lastAltitude = String.valueOf(lastLocation.getAltitude());
                    lastBearing = String.valueOf(lastLocation.getBearing());
                    lastBearingAccuracyDegrees = String.valueOf(lastLocation.getBearingAccuracyDegrees());
                    lastLatitude = String.valueOf(lastLocation.getLatitude());
                    lastLongitude = String.valueOf(lastLocation.getLongitude());
                    lastSpeed = String.valueOf(lastLocation.getSpeed());
                    lastTime = String.valueOf(lastLocation.getTime());
                }
            }
            String GNSSHardwareModelName = locationManager.getGnssHardwareModelName();
            String GNSSYearOfHardware = String.valueOf(locationManager.getGnssYearOfHardware());

            String accuracy;
            String powerUsage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ProviderProperties providerProperties = locationManager.getProviderProperties(provider);
                accuracy = String.valueOf(providerProperties.getAccuracy());
                powerUsage = String.valueOf(providerProperties.getPowerUsage());
            } else {
                accuracy = "Need Android 12(API 31)";
                powerUsage = "Need Android 12(API 31)";
            }
            String enableProvider = locationManager.getProviders(true).toString();
            String hasProvider;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasProvider = String.valueOf(locationManager.hasProvider(provider));
            } else {
                hasProvider = "Need Android 12(API 31)";
            }
            String locationEnabled = String.valueOf(locationManager.isLocationEnabled());
            String providerEnabled = String.valueOf(locationManager.isProviderEnabled(provider));

            textViewDetail.setText(MessageFormat.format("Providers: {0}\nEnable Provider: {1}" +
                            "\nHas Provider: {2}\nLocation Enabled: {3}" +
                            "\nProvider Enabled: {4}\nCurrent Provider: {5}" +
                            "\nAccuracy: {6}\nPower Usage: {7}" +
                            "\n" +
                            "\nGNSS Antenna Information: {8}\nGNSS Capabilities: {9}" +
                            "\nGNSS Hardware Model Name: {10}\nGNSS Year Of Hardware: {11}",
                    providers.toString(), enableProvider,
                    hasProvider, locationEnabled,
                    providerEnabled, provider,
                    accuracy, powerUsage,
                    GNSSAntennaInformation, GNSSCapabilities,
                    GNSSHardwareModelName, GNSSYearOfHardware));

            textViewUpdate.setText(MessageFormat.format("\nLast Location: {0},{1}\nLast Altitude: {2}\nLast Accuracy: {3}\nLast Time: {4}\nLast Speed: {5}\nLast Bearing: {6}\nLast Bearing Accuracy Degrees: {7}", lastLatitude, lastLongitude, lastAltitude, lastAccuracy, lastTime, lastSpeed, lastBearing, lastBearingAccuracyDegrees));
        }
    }

    private void print(String message) {
        Log.w(MenuActivity.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}